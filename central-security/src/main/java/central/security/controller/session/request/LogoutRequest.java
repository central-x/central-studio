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
import central.security.controller.session.SessionController;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.support.session.SessionContainer;
import central.security.core.attribute.SessionAttributes;
import central.security.core.body.JsonBody;
import central.security.core.request.Request;
import central.security.signer.KeyPair;
import central.util.Mapx;
import central.validation.Label;
import central.validation.Validatex;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
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

import java.security.interfaces.RSAPublicKey;

/**
 * 退出登录
 *
 * @author Alan Yeh
 * @see SessionController#logout
 * @since 2022/10/19
 */
public class LogoutRequest extends Request {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Params {
        @Label("会话凭证")
        @NotBlank
        @Size(min = 1, max = 2000)
        private String token;
    }

    @Getter
    private final Params params;

    public LogoutRequest(HttpServletRequest request) {
        super(request);
        this.params = this.bindParameter(Params.class);

        Validatex.Default().validate(params, new Class[0], (message) -> new ResponseStatusException(HttpStatus.BAD_REQUEST, message));
    }

    public static LogoutRequest of(HttpServletRequest request) {
        return new LogoutRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new LogoutAction();
    }

    private static class LogoutAction extends SecurityAction implements InitializingBean {
        private KeyPair keyPair;

        private SessionContainer session;

        @Override
        public void afterPropertiesSet() throws Exception {
            this.keyPair = this.getBean(KeyPair.class);
            this.session = this.getBean(SessionContainer.class);
        }

        @Override
        public void execute(SecurityExchange exchange) {
            var request = (LogoutRequest) exchange.getRequest();

            try {
                var session = JWT.require(Algorithm.RSA256((RSAPublicKey) keyPair.getVerifyKey()))
                        .withIssuer(exchange.getRequiredAttribute(SessionAttributes.ISSUER))
                        .withClaim(SessionClaims.TENANT_CODE, request.getTenantCode())
                        .build()
                        .verify(request.getParams().getToken());
                this.session.invalid(session);
            } catch (JWTVerificationException ignored) {

            }

            exchange.getResponse().setBody(new JsonBody(Mapx.newHashMap("message", "注销成功")));
        }
    }
}
