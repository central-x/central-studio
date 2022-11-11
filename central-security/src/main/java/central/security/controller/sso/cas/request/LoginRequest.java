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

package central.security.controller.sso.cas.request;

import central.api.client.security.SessionVerifier;
import central.api.scheduled.DataContext;
import central.api.scheduled.fetcher.DataFetcherType;
import central.lang.Stringx;
import central.security.controller.sso.cas.CasController;
import central.security.controller.sso.cas.support.ServiceTicket;
import central.security.controller.sso.cas.support.CasSession;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.core.attribute.ExchangeAttributes;
import central.security.core.body.RedirectBody;
import central.security.core.request.Request;
import central.util.Guidx;
import central.validation.Label;
import central.validation.Validatex;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Cas Login Request
 *
 * @author Alan Yeh
 * @see CasController#login
 * @since 2022/11/07
 */
public class LoginRequest extends Request {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Params {
        /**
         * 在大多数情况下，此参数是待接入的应用的 URL 地址。根据 HTTP 协议标准 RFC3986，这个 URL 须要使用 URLEncoded 进行编码。
         * 如果此参数为空，则直接跳转到登录界面
         */
        @Label("服务")
        @NotBlank
        @Size(min = 1, max = 4096)
        private String service;

        /**
         * 如果此参数被设置为 true，CAS 的 SSO 将会失效，每次进入登录界面都需重新登录。
         * 此参数与 gateway 参数冲突，因此两个参数不能共存。如果两个参数都存在值，gateway 参数将被忽略。
         */
        private Boolean renew;

        /**
         * 如果此参数被设置为 true，CAS 会直接检测当前是否已经存在有会话，或者是否能通过非交互式（non-interactive）的方式建立会话，如果满足
         * 以上条件，CAS 会携带有效的 ticket 并重定向到 service 指定的 URL 地址。
         * <p>
         * 如果此参数被设置为 true，CAS 发现当前没有存在会话，也不能通过非交互式的方式建立会话，那么 CAS 会直接重定向到 service 指定的 URL（未携带 ticket）。
         * <p>
         * 如果 gateway 参数被设置为 true，但是 service 参数为空的话，CAS 将直接转到登录界面，并忽略此参数。
         * 此参数与 renew 参数冲突
         */
        private Boolean gateway;
    }

    @Getter
    private final Params params;

    public LoginRequest(HttpServletRequest request) {
        super(request);

        this.params = this.bindParameter(Params.class);
        Validatex.Default().validate(this.params, new Class[0], (message) -> new ResponseStatusException(HttpStatus.BAD_REQUEST, message));
    }

    public static LoginRequest of(HttpServletRequest request) {
        return new LoginRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new LoginAction();
    }

    private static class LoginAction extends SecurityAction implements InitializingBean {

        private DataContext context;

        private CasSession tickets;

        private SessionVerifier verifier;

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
            this.context = this.getBean(DataContext.class);
            this.verifier = this.getBean(SessionVerifier.class);
            this.tickets = this.getBean(CasSession.class);
        }

        @Override
        public void execute(SecurityExchange exchange) {
            if (!exchange.getRequiredAttribute(ExchangeAttributes.Cas.ENABLED)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "中央认证服务（CAS）已禁用");
            }

            var request = (LoginRequest) exchange.getRequest();

            var loginUrl = UriComponentsBuilder.fromUri(request.getUri())
                    .replacePath(request.getTenantPath())
                    .path("/security")
                    .path("/")
                    .replaceQuery(null);

            var cookie = exchange.getRequiredAttribute(ExchangeAttributes.Session.COOKIE);

            // 如果没有传递 service，则无法重定向到应用系统，直接跳转登录界面
            if (Stringx.isNullOrBlank(request.getParams().getService())) {
                if (Objects.equals(Boolean.TRUE, request.getParams().getRenew())) {
                    // 如果要求重新登录
                    cookie.remove(exchange);
                }

                exchange.getResponse().setStatus(HttpStatus.TEMPORARY_REDIRECT);
                exchange.getResponse().setBody(new RedirectBody(URI.create(loginUrl.build().toString())));
                return;
            }

            // 验证 service 是否可信
            var application = this.context.getData(DataFetcherType.SAAS).getApplications().stream()
                    .filter(it -> Stringx.addSuffix(request.getParams().getService(), "/").startsWith(Stringx.addSuffix(it.getUrl() + it.getContextPath(), "/")))
                    .findFirst().orElse(null);
            if (application == null) {
                // 此应用不是已登记的应用，属于非法接入
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "服务[service]未登记: " + request.getParams().getService());
            }
            if (!application.getEnabled()) {
                // 此应用已禁用
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "服务[service]已禁用: " + request.getParams().getService());
            }

            // 验证会话
            var session = cookie.get(exchange);
            if (!verifier.verify(session)) {
                // 如果会话无效，则跳转到登录界面
                exchange.getResponse().setStatus(HttpStatus.TEMPORARY_REDIRECT);
                if (Objects.equals(Boolean.TRUE, request.getParams().getGateway())) {
                    // 如果 gateway 不为 true，则要求直接重定向回 service 指定的地址
                    exchange.getResponse().setBody(new RedirectBody(URI.create(request.getParams().getService())));
                } else {
                    // 跳转到登录界面
                    loginUrl.queryParam("redirect_uri", Stringx.encodeUrl(request.getUri().toString()));
                    exchange.getResponse().setBody(new RedirectBody(URI.create(loginUrl.build().toString())));
                }
            } else {
                // 会话有效，则生成一次性的 ST（Service Ticket）返回给应用系统
                // 应用系统在接收到 ST 之后，需要通过 /security/sso/cas/p3/serviceValidate 验证并拿到用户信息

                var ticket = ServiceTicket.builder()
                        .expires(exchange.getRequiredAttribute(ExchangeAttributes.Cas.TIMEOUT))
                        .code(application.getCode())
                        .ticket("ST-" + getSerial() + "-" + Guidx.nextID())
                        .session(session)
                        .build();

                this.tickets.save(request.getTenantCode(), ticket);

                // 协带 ST 重定向回应用系统
                var location = URI.create(UriComponentsBuilder.fromUriString(request.getParams().getService())
                        .queryParam("ticket", ticket.getTicket())
                        .build().toString());

                exchange.getResponse().setStatus(HttpStatus.TEMPORARY_REDIRECT);
                exchange.getResponse().setBody(new RedirectBody(location));
            }
        }
    }
}
