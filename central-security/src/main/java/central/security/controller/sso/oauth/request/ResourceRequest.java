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

import central.api.scheduled.ScheduledDataContext;
import central.api.scheduled.fetcher.DataFetchers;
import central.lang.Stringx;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.core.request.Request;
import central.security.signer.KeyPair;
import central.util.Listx;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;

/**
 * 资源请求
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
public abstract class ResourceRequest extends Request {

    public ResourceRequest(HttpServletRequest request) {
        super(request);
    }

    protected static abstract class ResourceAction extends SecurityAction implements InitializingBean {

        private ScheduledDataContext context;

        private KeyPair keyPair;

        @Override
        public void afterPropertiesSet() throws Exception {
            this.context = this.getBean(ScheduledDataContext.class);
            this.keyPair = this.getBean(KeyPair.class);
        }

        public DecodedJWT validate(SecurityExchange exchange) {
            var token = exchange.getRequest().getHeader("Authorization");
            if (Stringx.isNullOrBlank(token)) {
                // 没有找到请求头
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing required header 'Authorization'");
            }
            if (token.toLowerCase().startsWith("bearer ")) {
                token = token.substring("Bearer ".length());
            }
            DecodedJWT accessToken;
            try {
                accessToken = JWT.decode(token);
            } catch (Exception ex) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid OAuth access token: Bad token");
            }

            if (!Objects.equals("access_token", accessToken.getType())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid OAuth access token: Bad token");
            }

            if (accessToken.getExpiresAt() == null) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid OAuth access token: Missing required claim 'exp'");
            }

            try {
                // 使用公钥验证
                JWT.require(Algorithm.RSA256((RSAPublicKey) keyPair.getVerifyKey(), (RSAPrivateKey) keyPair.getSignKey()))
                        .build()
                        .verify(accessToken);
            } catch (TokenExpiredException ex) {
                // 过期了
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid OAuth access token: Bad token");
            } catch (SignatureVerificationException ex) {
                // 签名无效
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid OAuth access token: Invalid signature");
            } catch (AlgorithmMismatchException ex) {
                // 签名算法不匹配
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid OAuth access token: Algorithm mismatch");
            } catch (Exception ex) {
                // 其它异常
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid OAuth access token: " + ex.getLocalizedMessage());
            }

            var application = this.context.get(DataFetchers.SAAS).getApplicationByCode(Listx.getFirstOrNull(accessToken.getAudience()));
            if (application == null || !application.getEnabled()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid OAuth access token: Invalid client '" + Listx.getFirstOrNull(accessToken.getAudience()) + "'");
            }

            return accessToken;
        }
    }
}
