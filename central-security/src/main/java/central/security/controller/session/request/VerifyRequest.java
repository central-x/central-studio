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
import central.security.core.SecuritySession;
import central.security.core.attribute.ExchangeAttributes;
import central.security.core.body.StringBody;
import central.security.core.request.Request;
import central.security.signer.KeyPair;
import central.validation.Label;
import central.validation.Validatex;
import com.auth0.jwt.JWT;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

import java.security.interfaces.RSAPublicKey;
import java.util.Objects;

/**
 * 验证会话有效性
 *
 * @author Alan Yeh
 * @see SessionController#verify
 * @since 2022/10/19
 */
@Slf4j
public class VerifyRequest extends Request {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Params {
        @Label("会话凭证")
        @NotBlank
        @Size(min = 1, max = 2000)
        private String token;
    }

    @Getter
    private final Params params;

    public VerifyRequest(HttpServletRequest request) {
        super(request);
        if (MediaType.APPLICATION_JSON.isCompatibleWith(this.getContentType())) {
            this.params = this.getBody(Params.class);
        } else {
            this.params = this.getParameter(Params.class);
        }

        Validatex.Default().validate(params, new Class[0], (message) -> new ResponseStatusException(HttpStatus.BAD_REQUEST, message));
    }

    public static VerifyRequest of(HttpServletRequest request) {
        return new VerifyRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new VerifyAction();
    }

    private static class VerifyAction extends SecurityAction implements InitializingBean {
        private KeyPair keyPair;

        private SecuritySession session;

        @Override
        public void afterPropertiesSet() throws Exception {
            this.keyPair = this.getBean(KeyPair.class);
            this.session = this.getBean(SecuritySession.class);
        }

        @Override
        public void execute(SecurityExchange exchange) {
            var request = (VerifyRequest) exchange.getRequest();

            DecodedJWT token;
            try {
                token = JWT.decode(request.getParams().getToken());
            } catch (Exception ex) {
                log.info("不是有效的会话凭证");
                exchange.getResponse().setBody(new StringBody("false"));
                return;
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不是有效的会话凭证");
            }

            if (!Objects.equals(token.getIssuer(), exchange.getRequiredAttribute(ExchangeAttributes.SESSION_ISSUER))) {
                log.info("不是本系统颁发的会话凭证");
                exchange.getResponse().setBody(new StringBody("false"));
                return;
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不是本系统颁发的会话凭证");
            }

            if (!Objects.equals(token.getClaim(SessionClaims.TENANT_CODE).asString(), request.getTenantCode())) {
                log.info("租户不匹配");
                exchange.getResponse().setBody(new StringBody("false"));
                return;
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "租户不匹配");
            }

            try {
                JWT.require(Algorithm.RSA256((RSAPublicKey) keyPair.getVerifyKey()))
                        .build()
                        .verify(token);
            } catch (JWTVerificationException ex) {
                log.info("密钥不匹配");
                exchange.getResponse().setBody(new StringBody("false"));
                return;
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "密钥不匹配");
            }

            // 验证会主知是否过期
            if (!this.session.verify(token)) {
                log.info("会话不存在");
                exchange.getResponse().setBody(new StringBody("false"));
                return;
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "会话不存在");
            }

            exchange.getResponse().setBody(new StringBody("true"));
        }
    }
}
