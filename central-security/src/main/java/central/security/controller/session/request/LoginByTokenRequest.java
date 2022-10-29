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

package central.security.controller.session.request;

import central.api.client.security.SessionClaims;
import central.api.provider.org.AccountProvider;
import central.data.org.Account;
import central.lang.Stringx;
import central.security.controller.session.SessionController;
import central.security.controller.session.support.Endpoint;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.core.SecuritySession;
import central.security.core.attribute.ExchangeAttributes;
import central.security.core.body.StringBody;
import central.security.core.request.Request;
import central.security.signer.KeyPair;
import central.util.Guidx;
import central.util.Mapx;
import central.validation.Label;
import central.validation.Validatex;
import com.auth0.jwt.JWT;
import com.auth0.jwt.RegisteredClaims;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Map;

/**
 * @author Alan Yeh
 * @see SessionController#loginByToken
 * @since 2022/10/19
 */
public class LoginByTokenRequest extends Request {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Params {
        @Label("会话凭证")
        @NotBlank
        @Size(min = 1, max = 2000)
        private String token;

        @Label("终端密钥")
        @NotBlank
        private String secret;

        @Label("JWT Claim")
        private Map<String, Object> claims;
    }

    @Getter
    private final Params params;

    public LoginByTokenRequest(HttpServletRequest request) {
        super(request);
        if (MediaType.APPLICATION_JSON.isCompatibleWith(this.getContentType())) {
            this.params = this.getBody(Params.class);
        } else {
            this.params = this.getParameter(Params.class);
        }

        Validatex.Default().validate(params, new Class[0], (message) -> new ResponseStatusException(HttpStatus.BAD_REQUEST, message));
    }

    public static LoginByTokenRequest of(HttpServletRequest request) {
        return new LoginByTokenRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new LoginAction();
    }

    private static class LoginAction extends SecurityAction implements InitializingBean {

        private AccountProvider provider;

        private KeyPair keyPair;

        private SecuritySession session;

        @Override
        public void afterPropertiesSet() throws Exception {
            this.provider = this.getBean(AccountProvider.class);
            this.keyPair = this.getBean(KeyPair.class);
            this.session = this.getBean(SecuritySession.class);
        }

        @Override
        public void execute(SecurityExchange exchange) {
            var request = (LoginByTokenRequest) exchange.getRequest();
            // 检查终端密钥
            var endpoint = Endpoint.resolve(exchange, request.getParams().getSecret());
            if (endpoint == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "终端密钥[secret]错误");
            }

            DecodedJWT token;
            try {
                token = JWT.require(Algorithm.RSA256((RSAPublicKey) keyPair.getVerifyKey()))
                        .withClaim(SessionClaims.TENANT_CODE, request.getTenantCode())
                        .withIssuer(exchange.getRequiredAttribute(ExchangeAttributes.SESSION_ISSUER))
                        .build().verify(request.getParams().getToken());
            } catch (JWTVerificationException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不是有效会话凭证");
            }

            if (!token.getClaim(SessionClaims.SOURCE).isMissing()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不允许二次颁发会话");
            }

            if (!this.session.verify(token)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "会话已过期");
            }

            var account = this.provider.findById(token.getSubject());
            if (account == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("帐户[account={}]不存在", token.getClaim(SessionClaims.USERNAME).asString()));
            }
            if (!account.getEnabled()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("用户[account={}]已禁用", token.getClaim(SessionClaims.USERNAME).asString()));
            }
            if (account.getDeleted()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("用户[account={}]已删除", token.getClaim(SessionClaims.USERNAME).asString()));
            }

            var issued = this.issue(exchange, token, account, endpoint, request.getParams().getClaims());
            var limit = exchange.getRequiredAttribute(endpoint.getAttribute()).getLimit();

            session.save(JWT.decode(issued), limit);

            exchange.getResponse().setBody(new StringBody(issued));
        }

        /**
         * 颁发会话
         */
        private String issue(SecurityExchange exchange, DecodedJWT source, Account account, Endpoint endpoint, Map<String, Object> claims) {
            var jwt = JWT.create()
                    // JWT 唯一标识
                    .withJWTId(Guidx.nextID())
                    // 记录颁发当前会话的会话标识
                    .withClaim(SessionClaims.SOURCE, source.getId())
                    // 用户主键
                    .withSubject(account.getId())
                    // 用户帐号
                    .withClaim(SessionClaims.USERNAME, account.getUsername())
                    // 是否管理员
                    .withClaim(SessionClaims.ADMIN, account.getAdmin())
                    // 是否超级管理员
                    .withClaim(SessionClaims.SUPERVISOR, account.getSupervisor())
                    // 颁发时间
                    .withClaim(SessionClaims.ISSUE_TIME, new Date())
                    // 终端类型
                    .withClaim(SessionClaims.ENDPOINT, endpoint.getValue())
                    // 颁发者
                    .withIssuer(exchange.getRequiredAttribute(ExchangeAttributes.SESSION_ISSUER))
                    // 会话有效时间
                    .withClaim(SessionClaims.TIMEOUT, exchange.getRequiredAttribute(ExchangeAttributes.SESSION_TIMEOUT))
                    // 租户标识
                    .withClaim(SessionClaims.TENANT_CODE, exchange.getRequest().getTenantCode());

            // 附加用户指定的 Claims
            if (Mapx.isNotEmpty(claims)) {
                for (var entry : claims.entrySet()) {
                    if (entry.getValue() instanceof Boolean b) {
                        jwt.withClaim(entry.getKey(), b);
                    } else if (entry.getValue() instanceof Integer i) {
                        jwt.withClaim(entry.getKey(), i);
                    } else if (entry.getValue() instanceof Long l) {
                        jwt.withClaim(entry.getKey(), l);
                    } else if (entry.getValue() instanceof Double d) {
                        jwt.withClaim(entry.getKey(), d);
                    } else if (entry.getValue() instanceof String s) {
                        jwt.withClaim(entry.getKey(), s);
                    } else if (entry.getValue() instanceof Date d) {
                        jwt.withClaim(entry.getKey(), d);
                    }
                }
            }
            return jwt.sign(Algorithm.RSA256((RSAPublicKey) keyPair.getVerifyKey(), (RSAPrivateKey) keyPair.getSignKey()));
        }
    }
}
