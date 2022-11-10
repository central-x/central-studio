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

package central.security.controller.index.request;

import central.api.client.security.SessionClient;
import central.api.client.security.SessionVerifier;
import central.lang.Stringx;
import central.security.controller.index.IndexController;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.core.attribute.ExchangeAttributes;
import central.security.core.body.StringBody;
import central.security.core.request.Request;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.InitializingBean;

/**
 * 退出登录接口
 *
 * @author Alan Yeh
 * @see IndexController#logout
 * @since 2022/10/19
 */
public class LogoutRequest extends Request {
    public LogoutRequest(HttpServletRequest request) {
        super(request);
    }

    public static LogoutRequest of(HttpServletRequest request) {
        return new LogoutRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new LogoutAction();
    }

    private static class LogoutAction extends SecurityAction implements InitializingBean {

        private SessionVerifier verifier;

        @Override
        public void afterPropertiesSet() throws Exception {
            this.verifier = this.getBean(SessionVerifier.class);
        }

        @Override
        public void execute(SecurityExchange exchange) {
            var cookie = exchange.getRequiredAttribute(ExchangeAttributes.Session.COOKIE);
            var token = cookie.get(exchange);
            if (Stringx.isNotBlank(token)) {
                this.verifier.invalid(token);
            }
            exchange.getResponse().setBody(new StringBody("操作成功"));
        }
    }
}
