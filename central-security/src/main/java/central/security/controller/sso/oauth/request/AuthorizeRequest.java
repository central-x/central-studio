/*
 * MIT License
 *
 * Copyright (c) 2022-present Alan Yeh <alan@yeh.cn>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package central.security.controller.sso.oauth.request;

import central.api.client.security.SessionVerifier;
import central.api.scheduled.ScheduledDataContext;
import central.api.scheduled.fetcher.DataFetcherType;
import central.lang.Arrayx;
import central.lang.Stringx;
import central.security.Digestx;
import central.security.controller.sso.oauth.OAuthController;
import central.security.controller.sso.oauth.option.GrantScope;
import central.security.controller.sso.oauth.support.AuthorizationCode;
import central.security.controller.sso.oauth.support.OAuthSession;
import central.security.controller.sso.oauth.support.AuthorizationTransaction;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.core.attribute.OAuthAttributes;
import central.security.core.attribute.SessionAttributes;
import central.security.core.body.RedirectBody;
import central.security.core.request.Request;
import central.util.Guidx;
import central.util.Setx;
import central.validation.Fix;
import central.validation.Label;
import central.validation.Validatex;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import lombok.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 认证入口
 *
 * @author Alan Yeh
 * @see OAuthController#authorize
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.1">Authorization Request</a>
 * @since 2022/11/07
 */
public class AuthorizeRequest extends Request {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Params {
        /**
         * 授权类型
         * <p>
         * 此字段因定要求为 code
         */
        @Label("授权类型")
        @Fix("code")
        private String responseType;

        /**
         * 应用标识
         * <p>
         * 此值使用应用的标识[code]字段
         */
        @Label("应用标识")
        @NotBlank
        @Size(min = 1, max = 36)
        private String clientId;

        /**
         * 成功授权后的回调地址，必须是创建应用时的服务地址域名和端口下的地址。
         * <p>
         * 注意前端传递这个参数的时候，需要将 url 进行 URLEncode
         */
        @Label("回调地址")
        @NotBlank
        @Size(min = 1, max = 4096)
        private String redirectUri;

        /**
         * client 的状态值。用于第三方应用防止 CSRF 攻击，成功授权后回调时会原样带回
         * <p>
         * client 端需要检查用户与 state 参数状态的绑定
         */
        @Label("状态值")
        @NotBlank
        @Size(min = 1, max = 128)
        private String state;

        /**
         * 请求用户授权时，向用户显示的可进行授权的列表
         * <p>
         * 不传则默认只能获取用户主键信息
         */
        @Label("授权范围")
        private Set<GrantScope> scope;
    }

    @Getter
    private final Params params;

    @SneakyThrows
    public AuthorizeRequest(HttpServletRequest request) {
        super(request);
        this.params = Params.builder()
                .responseType(this.getParameter("response_type"))
                .clientId(this.getParameter("client_id"))
                .redirectUri(this.getParameter("redirect_uri"))
                .state(this.getParameter("state"))
                .scope(Arrayx.asStream(this.getParameterOrDefault("scope", GrantScope.BASIC.getValue()).split(",")).map(String::trim).map(GrantScope::resolve).filter(Objects::nonNull).collect(Collectors.toSet()))
                .build();
        Validatex.Default().validate(this.params, new Class[]{Default.class}, ServletRequestBindingException::new);
    }

    public static AuthorizeRequest of(HttpServletRequest request) {
        return new AuthorizeRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new Action();
    }

    private static class Action extends SecurityAction implements InitializingBean {
        private SessionVerifier verifier;
        private ScheduledDataContext context;
        private OAuthSession session;
        private static final AtomicInteger serial = new AtomicInteger(1000);

        private static synchronized int getSerial() {
            return serial.updateAndGet(value -> {
                if (value > 9999) {
                    return 1000;
                } else {
                    return value + 1;
                }
            });
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            this.verifier = this.getBean(SessionVerifier.class);
            this.context = this.getBean(ScheduledDataContext.class);
            this.session = this.getBean(OAuthSession.class);
        }

        @Override
        public void execute(SecurityExchange exchange) {
            if (!exchange.getRequiredAttribute(OAuthAttributes.ENABLED)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "OAuth 2.0 认证服务已禁用");
            }

            var request = (AuthorizeRequest) exchange.getRequest();


            var application = this.context.getData(DataFetcherType.SAAS).getApplicationByCode(request.getParams().getClientId());
            if (application == null) {
                // 此应用不是已登记的应用
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "应用标识[client_id]无效");
            }
            if (!application.getEnabled()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "应用标识[client_id]无效: 已禁用");
            }

            if (!request.getParams().getRedirectUri().toLowerCase().startsWith(application.getUrl() + application.getContextPath())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "回调地址[redirect_uri]与应用不符");
            }

            if (exchange.getRequiredAttribute(OAuthAttributes.AUTO_GRANTING)) {
                this.autoGranting(exchange);
            } else {
                this.granting(exchange);
            }
        }

        /**
         * 如果发现当前用户已登录，则直接重定向到业务系统，不需要用户手动确认
         */
        private void autoGranting(SecurityExchange exchange) {
            var request = (AuthorizeRequest) exchange.getRequest();

            // 获取会话信息
            var cookie = exchange.getRequiredAttribute(SessionAttributes.COOKIE);
            var session = cookie.get(exchange);

            if (!this.verifier.verify(session)) {
                // 如果找不到会话，则重定向到登录界面
                var loginUrl = UriComponentsBuilder.fromUri(exchange.getRequest().getUri())
                        .replacePath(exchange.getRequest().getTenantPath())
                        .path("/security/")
                        .replaceQuery(null)
                        .queryParam("redirect_uri", Stringx.encodeUrl(exchange.getRequest().getUri().toString()))
                        .build().toString();
                exchange.getResponse().setStatus(HttpStatus.TEMPORARY_REDIRECT);
                exchange.getResponse().setBody(new RedirectBody(URI.create(loginUrl)));
            } else {
                // 会话有效，则生成一次性授权码（Authorization Code）
                var code = AuthorizationCode.builder()
                        .expires(exchange.getRequiredAttribute(OAuthAttributes.AUTHORIZATION_CODE_TIMEOUT))
                        .code("OC-" + getSerial() + "-" + Guidx.nextID())
                        .clientId(request.getParams().getClientId())
                        .redirectUri(request.getParams().getRedirectUri())
                        .session(session)
                        .scope(request.getParams().getScope())
                        .build();

                // 保存一次性授权
                this.session.saveCode(request.getTenantCode(), code);

                var redirectUri = UriComponentsBuilder.fromUriString(request.getParams().getRedirectUri())
                        .queryParam("state", request.getParams().getState())
                        .queryParam("code", code.getCode())
                        .build().toString();

                exchange.getResponse().setStatus(HttpStatus.TEMPORARY_REDIRECT);
                exchange.getResponse().setBody(new RedirectBody(URI.create(redirectUri)));
            }
        }

        /**
         * 用户手动授权
         * 无论用户是否已经登录了，都需要跳转到登录界面上，引导用户完成授权
         */
        private void granting(SecurityExchange exchange) {
            var request = (AuthorizeRequest) exchange.getRequest();

            var transCookie = exchange.getRequiredAttribute(OAuthAttributes.GRANTING_TRANS_COOKIE);
            var transId = transCookie.get(exchange);

            if (Stringx.isNullOrBlank(transId)) {
                // 如果没有授权事务，则开始新的授权事务
                var transaction = AuthorizationTransaction.builder()
                        .expires(exchange.getRequiredAttribute(OAuthAttributes.GRANTING_TRANS_TIMEOUT))
                        .id(Guidx.nextID())
                        .clientId(request.getParams().getClientId())
                        .scopes(request.getParams().getScope())
                        // 保存摘要，等完成授权后进行对比，保证没有人篡改过
                        .digest(Digestx.SHA256.digest(request.getUri().toString(), StandardCharsets.UTF_8))
                        // 标记为未授权
                        .granted(false)
                        .build();

                this.session.saveTransaction(request.getTenantCode(), transaction);
                // 通过 Cookie 跟踪事务
                transCookie.set(exchange, transaction.getId());

                // 用户完成授权之后，会重新重定向回本接口，就会走到 else 的逻辑了
                var loginUrl = UriComponentsBuilder.fromUri(request.getUri())
                        .replacePath(request.getTenantPath()).path("/security/")
                        .replaceQuery(null)
                        .queryParam("redirect_uri", Stringx.encodeUrl(request.getUri().toString()))
                        .build();
                exchange.getResponse().setStatus(HttpStatus.TEMPORARY_REDIRECT);
                exchange.getResponse().setBody(new RedirectBody(URI.create(loginUrl.toString())));
            } else {
                // 取出事务，进行摘要对比，如果发现摘要不匹配，则说明有人篡改了参数
                var transaction = this.session.getAndRemoveTransaction(request.getTenantCode(), transId);
                // 事务是一次性的，因此删除 Cookie
                transCookie.remove(exchange);

                if (!Objects.equals(transaction.getDigest(), Digestx.SHA256.digest(request.getUri().toString(), StandardCharsets.UTF_8))) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "参数被篡改，请重新发起登录请求");
                }

                if (!transaction.isGranted()) {
                    // 用户未授权，则直接跳回业务系统，不携带 code，代表用户拒绝授梳
                    exchange.getResponse().setStatus(HttpStatus.TEMPORARY_REDIRECT);
                    exchange.getResponse().setBody(new RedirectBody(URI.create(request.getParams().getRedirectUri())));
                } else {
                    // 用户已授权，则生成一次性授权码
                    var code = AuthorizationCode.builder()
                            .expires(exchange.getRequiredAttribute(OAuthAttributes.AUTHORIZATION_CODE_TIMEOUT))
                            .code("OC-" + getSerial() + "-" + Guidx.nextID())
                            .clientId(request.getParams().getClientId())
                            .redirectUri(request.getParams().getRedirectUri())
                            .session(transaction.getSession())
                            .scope(Setx.asStream(transaction.getGrantedScope()).map(GrantScope::resolve).collect(Collectors.toSet()))
                            .build();

                    this.session.saveCode(request.getTenantCode(), code);

                    var redirectUri = UriComponentsBuilder.fromUriString(request.getParams().getRedirectUri())
                            .queryParam("state", request.getParams().getState())
                            .queryParam("code", code.getCode())
                            .build().toString();

                    exchange.getResponse().setStatus(HttpStatus.TEMPORARY_REDIRECT);
                    exchange.getResponse().setBody(new RedirectBody(URI.create(redirectUri)));
                }
            }
        }
    }
}
