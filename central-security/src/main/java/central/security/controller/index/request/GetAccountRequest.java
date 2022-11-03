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

import central.api.client.security.SessionVerifier;
import central.api.provider.organization.AccountProvider;
import central.lang.Stringx;
import central.security.controller.index.IndexController;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.core.attribute.ExchangeAttributes;
import central.security.core.body.JsonBody;
import central.security.core.request.Request;
import com.auth0.jwt.JWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * 获取当前用户信息
 *
 * @author Alan Yeh
 * @see IndexController#getAccount
 * @since 2022/10/19
 */
public class GetAccountRequest extends Request {

    public GetAccountRequest(HttpServletRequest request) {
        super(request);
    }

    public static GetAccountRequest of(HttpServletRequest request) {
        return new GetAccountRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new Action();
    }

    private static class Action extends SecurityAction implements InitializingBean {

        private AccountProvider provider;

        private SessionVerifier verifier;

        @Override
        public void afterPropertiesSet() throws Exception {
            this.provider = this.getBean(AccountProvider.class);
            this.verifier = this.getBean(SessionVerifier.class);
        }

        @Override
        public void execute(SecurityExchange exchange) {
            var cookie = exchange.getRequiredAttribute(ExchangeAttributes.COOKIE);
            var token = cookie.get(exchange);

            if (Stringx.isNotBlank(token)) {
                // 验证会话有效性
                if (!this.verifier.verify(token)) {
                    token = null;
                }
            }

            if (token == null) {
                // 未登录或会话无效
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
            }

            var decodedJwt = JWT.decode(token);

            var account = this.provider.findById(decodedJwt.getSubject());
            if (account == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, Stringx.format("用户[id={}]不存在", decodedJwt.getSubject()));
            }
            exchange.getResponse().setBody(new JsonBody(account));
        }
    }
}
