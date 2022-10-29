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

import central.security.controller.session.SessionController;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.core.body.StringBody;
import central.security.core.request.Request;
import central.security.signer.KeyPair;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.InitializingBean;

import java.util.Base64;

/**
 * 获取公钥请求
 *
 * @author Alan Yeh
 * @see SessionController#getPublicKey
 * @since 2022/10/19
 */
public class GetPublicKeyRequest extends Request {
    public GetPublicKeyRequest(HttpServletRequest request) {
        super(request);
    }

    public static GetPublicKeyRequest of(HttpServletRequest request) {
        return new GetPublicKeyRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new Action();
    }

    private static class Action extends SecurityAction implements InitializingBean {

        private KeyPair keyPair;

        @Override
        public void afterPropertiesSet() throws Exception {
            this.keyPair = this.getBean(KeyPair.class);
        }

        @Override
        public void execute(SecurityExchange exchange) {
            exchange.getResponse().setBody(new StringBody(Base64.getEncoder().encodeToString(this.keyPair.getVerifyKey().getEncoded())));
        }
    }
}
