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

import central.api.provider.organization.AccountProvider;
import central.lang.Arrayx;
import central.lang.Stringx;
import central.security.controller.sso.oauth.option.GrantScope;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.core.body.JsonBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;

/**
 * 获取当前用户信息
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
public class GetUserRequest extends ResourceRequest {

    public GetUserRequest(HttpServletRequest request) {
        super(request);
    }

    public static GetUserRequest of(HttpServletRequest request) {
        return new GetUserRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new Action();
    }

    private static class Action extends ResourceAction implements InitializingBean {
        private AccountProvider provider;

        @Override
        public void afterPropertiesSet() throws Exception {
            super.afterPropertiesSet();
            this.provider = this.getBean(AccountProvider.class);
        }

        @Override
        public void execute(SecurityExchange exchange) {
            // 获取 access_token
            var token = this.validate(exchange);

            var scopes = Arrayx.asStream(token.getClaim("scope").asArray(String.class))
                    .map(GrantScope::resolve)
                    .toList();

            var account = this.provider.findById(token.getSubject());
            if (account == null) {
                // 一般情况下不会报这个异常，以防万一吧
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, Stringx.format("User '{}' not found", token.getSubject()));
            }

            var result = new HashMap<String, Object>();
            for (var scope : scopes) {
                for (var fetcher : scope.getFetchers()) {
                    result.put(fetcher.field(), fetcher.getter().apply(account));
                }
            }

            exchange.getResponse().setBody(new JsonBody(result));
        }
    }
}
