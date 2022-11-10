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
import central.data.organization.Account;
import central.security.controller.session.SessionController;
import central.security.controller.session.support.Endpoint;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.core.SecuritySession;
import central.security.core.attribute.ExchangeAttributes;
import central.security.core.request.Request;
import central.security.signer.KeyPair;
import central.util.Guidx;
import central.util.Mapx;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Map;

/**
 * 使用凭证登录
 *
 * @author Alan Yeh
 * @see SessionController#loginByCredential
 * @since 2022/10/19
 */
public class LoginByCredentialRequest extends Request {

    public LoginByCredentialRequest(HttpServletRequest request) {
        super(request);
    }

    public static LoginByCredentialRequest of(HttpServletRequest request) {
        return new LoginByCredentialRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new LoginAction();
    }

    private static class LoginAction extends SecurityAction implements InitializingBean {

        private KeyPair keyPair;

        private SecuritySession session;

        @Override
        public void afterPropertiesSet() throws Exception {
            this.keyPair = this.getBean(KeyPair.class);
            this.session = this.getBean(SecuritySession.class);
        }

        @Override
        public void execute(SecurityExchange exchange) {
            var request = (LoginByCredentialRequest) exchange.getRequest();

            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, HttpStatus.NOT_IMPLEMENTED.getReasonPhrase());
//            DecodedJWT token;
//            try {
//                token = JWT.require(Algorithm.RSA256((RSAPublicKey) keyPair.getVerifyKey()))
//                        .withClaim(SessionClaims.TENANT_CODE, request.getTenantCode())
//                        .withIssuer(exchange.getRequiredAttribute(ExchangeAttributes.SESSION_ISSUER))
//                        .build().verify(request.getParams().getToken());
//            } catch (JWTVerificationException ex) {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不是有效会话凭证");
//            }
//
//            if (!this.session.verify(token)){
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "会话已过期");
//            }


        }

        /**
         * 颁发会话
         */
        private String issue(SecurityExchange exchange, Account account, Endpoint endpoint, Map<String, Object> claims) {
            var jwt = JWT.create()
                    // JWT 唯一标识
                    .withJWTId(Guidx.nextID())
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
                    .withIssuer(exchange.getRequiredAttribute(ExchangeAttributes.Session.ISSUER))
                    // 会话有效时间
                    .withClaim(SessionClaims.TIMEOUT, exchange.getRequiredAttribute(ExchangeAttributes.Session.TIMEOUT))
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
