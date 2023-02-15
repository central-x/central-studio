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

import central.api.client.security.SessionClaims;
import central.api.scheduled.ScheduledDataContext;
import central.api.scheduled.fetcher.DataFetcherType;
import central.data.saas.Application;
import central.security.controller.sso.oauth.OAuthController;
import central.security.controller.sso.oauth.option.GrantScope;
import central.security.controller.sso.oauth.support.OAuthSession;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.core.attribute.OAuthAttributes;
import central.security.core.attribute.SessionAttributes;
import central.security.core.body.JsonBody;
import central.security.core.body.StringBody;
import central.security.core.body.XmlBody;
import central.security.core.request.Request;
import central.security.signer.KeyPair;
import central.util.Guidx;
import central.validation.Fix;
import central.validation.Label;
import central.validation.Validatex;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import lombok.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.server.ResponseStatusException;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 获取访问凭证
 *
 * @author Alan Yeh
 * @see OAuthController#getAccessToken
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.3">Access Token Request</a>
 * @since 2022/11/08
 */
public class AccessTokenRequest extends Request {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Params {
        /**
         * 授权类型
         * <p>
         * 此值固定为 authorization_code
         */
        @Label("授权类型")
        @NotBlank
        @Fix("authorization_code")
        private String grantType;
        /**
         * @see Application#getCode
         */
        @Label("应用标识")
        @NotBlank
        @Size(min = 1, max = 50)
        private String clientId;
        /**
         * @see Application#getSecret
         */
        @Label("应用密钥")
        @NotBlank
        @Size(min = 1, max = 50)
        private String clientSecret;

        @Label("授权码")
        @NotBlank
        @Size(min = 1, max = 128)
        private String code;

        @Label("重定向地址")
        @NotBlank
        @Size(min = 1, max = 4096)
        private String redirectUri;
    }

    @Getter
    private final Params params;

    @SneakyThrows
    public AccessTokenRequest(HttpServletRequest request) {
        super(request);
        if (MediaType.APPLICATION_JSON.isCompatibleWith(this.getContentType())) {
            var body = this.bindBody(Map.class);
            this.params = Params.builder()
                    .grantType(body.get("grant_type").toString())
                    .clientId(body.get("client_id").toString())
                    .clientSecret(body.get("client_secret").toString())
                    .code(body.get("code").toString())
                    .redirectUri(body.get("redirect_uri").toString())
                    .build();
        } else {
            this.params = Params.builder()
                    .grantType(this.getParameter("grant_type"))
                    .clientId(this.getParameter("client_id"))
                    .clientSecret(this.getParameter("client_secret"))
                    .code(this.getParameter("code"))
                    .redirectUri(this.getParameter("redirect_uri"))
                    .build();
        }
        Validatex.Default().validate(this.params, new Class[]{Default.class}, ServletRequestBindingException::new);
    }

    public static AccessTokenRequest of(HttpServletRequest request) {
        return new AccessTokenRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new Action();
    }

    private static class Action extends SecurityAction implements InitializingBean {
        private ScheduledDataContext context;

        private OAuthSession session;

        private KeyPair keyPair;

        @Override
        public void afterPropertiesSet() throws Exception {
            this.context = this.getBean(ScheduledDataContext.class);
            this.session = this.getBean(OAuthSession.class);
            this.keyPair = this.getBean(KeyPair.class);
        }

        @Override
        public void execute(SecurityExchange exchange) {
            if (!exchange.getRequiredAttribute(OAuthAttributes.ENABLED)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "OAuth 2.0 认证服务已禁用");
            }

            var request = (AccessTokenRequest) exchange.getRequest();

            var code = session.getCode(request.getTenantCode(), request.getParams().getCode());
            if (code == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "授权码[code]已过期");
            }

            if (!Objects.equals(code.getRedirectUri(), request.getParams().getRedirectUri())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "授权码[code]与重定向地址[redirect_uri]不匹配，请确保重定向地址[redirect_uri]与申请授权码[code]时使用的是相同值");
            }

            if (!Objects.equals(code.getClientId(), request.getParams().getClientId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "授权码[code]与应用标识[client_id]不符");
            }

            var application = this.context.getData(DataFetcherType.SAAS).getApplicationByCode(code.getClientId());
            if (application == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "应用标识[client_id]无效");
            }
            if (!application.getEnabled()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "应用标识[client_id]已禁用");
            }
            if (!Objects.equals(application.getSecret(), request.getParams().getClientSecret())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "应用密钥[client_secret]错误");
            }

            var scopes = code.getScope();
            scopes.add(GrantScope.BASIC);

            // 颁发 access_token
            // 使用私钥签名，这样客户端那边就没办法伪造，我们也不需要保存这个凭证的信息，日期过了就失效了
            var token = JWT.create()
                    // 指定本 JWT 为 access_token 类型
                    .withHeader(Map.of("typ", "access_token"))
                    .withJWTId(Guidx.nextID())
                    .withSubject(code.getSessionJwt().getSubject())
                    .withIssuer(exchange.getRequiredAttribute(SessionAttributes.ISSUER))
                    // 被授权的应用
                    .withAudience(application.getCode())
                    // 限定范围
                    .withArrayClaim("scope", scopes.stream().map(GrantScope::getValue).toArray(String[]::new))
                    // 指定过期时间
                    .withExpiresAt(new Date(System.currentTimeMillis() + exchange.getRequiredAttribute(OAuthAttributes.ACCESS_TOKEN_TIMEOUT).toMillis()))
                    .sign(Algorithm.RSA256((RSAPublicKey) keyPair.getVerifyKey(), (RSAPrivateKey) keyPair.getSignKey()));

            // 返回响应
            var body = Map.of(
                    "access_token", token,
                    "token_type", "bearer",
                    "expires_in", exchange.getRequiredAttribute(OAuthAttributes.ACCESS_TOKEN_TIMEOUT).toSeconds(),
                    "account_id", code.getSessionJwt().getSubject(),
                    "username", code.getSessionJwt().getClaim(SessionClaims.USERNAME).asString(),
                    "scope", String.join(",", scopes.stream().map(GrantScope::getValue).toList())
            );

            if (request.isAcceptContentType(MediaType.APPLICATION_JSON)) {
                exchange.getResponse().setBody(new JsonBody(body));
            } else if (request.isAcceptContentType(MediaType.APPLICATION_XML)) {
                var content = new StringBuilder("<OAuth>");
                for (var item : body.entrySet()) {
                    content.append("<").append(item.getKey()).append("?").append(item.getValue()).append("</").append(item.getKey()).append(">");
                }
                content.append("</OAuth>");

                exchange.getResponse().setBody(new XmlBody(content.toString()));
            } else if (request.isAcceptContentType(MediaType.APPLICATION_FORM_URLENCODED)) {
                var content = body.entrySet().stream().map(it -> it.getKey() + "=" + it.getValue()).collect(Collectors.joining("&"));
                exchange.getResponse().setBody(new StringBody(content));
            } else {
                exchange.getResponse().setBody(new JsonBody(body));
            }
        }
    }
}
