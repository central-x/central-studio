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

import central.api.client.security.SessionVerifier;
import central.lang.Stringx;
import central.security.controller.sso.oauth.OAuthController;
import central.security.controller.sso.oauth.option.GrantScope;
import central.security.controller.sso.oauth.support.OAuthSession;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.core.attribute.ExchangeAttributes;
import central.security.core.body.JsonBody;
import central.security.core.request.Request;
import central.validation.Enums;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 授权接口
 *
 * @author Alan Yeh
 * @see OAuthController#grant
 * @since 2022/11/08
 */
public class GrantRequest extends Request {

    @Data
    public static class Params {
        /**
         * 用户同意的授权列表
         */
        @Enums(GrantScope.class)
        private Set<String> scope;
    }

    @Getter
    private final Params params;

    public GrantRequest(HttpServletRequest request) {
        super(request);

        if (MediaType.APPLICATION_JSON.isCompatibleWith(this.getContentType())) {
            // 使用 JSON 提交
            this.params = this.bindBody(Params.class);
        } else {
            this.params = this.bindParameter(Params.class);
        }
    }

    public static GrantRequest of(HttpServletRequest request) {
        return new GrantRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new Action();
    }

    private static class Action extends SecurityAction implements InitializingBean {

        private SessionVerifier verifier;

        private OAuthSession authContainer;

        @Override
        public void afterPropertiesSet() throws Exception {
            this.verifier = this.getBean(SessionVerifier.class);
            this.authContainer = this.getBean(OAuthSession.class);
        }

        @Override
        public void execute(SecurityExchange exchange) {
            var request = (GrantRequest) exchange.getRequest();

            // 验证会话
            var sessionCookie = exchange.getRequiredAttribute(ExchangeAttributes.Session.COOKIE);
            var session = sessionCookie.get(exchange);
            if (!verifier.verify(session)) {
                // 一般情况下不会出现这种情况，因为只有登录之后才有授权界面，除非是直接调接口
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
            }

            // 查找授权事务
            var transCookie = exchange.getRequiredAttribute(ExchangeAttributes.OAuth.GRANTING_TRANS_COOKIE);
            var transId = transCookie.get(exchange);

            if (Stringx.isNullOrBlank(transId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "授权错误: 找不到待授权请求");
            }

            var transaction = this.authContainer.getAndRemoveTransaction(request.getTenantCode(), transId);
            if (transaction == null) {
                // 过期了
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "授权错误: 找不到待授权请求");
            }
            // 开发者申请的范围
            var requestedScopes = transaction.getScopes();
            // 用户授权的范围
            var grantedScopes = request.getParams().getScope().stream().map(GrantScope::resolve).collect(Collectors.toSet());

            if (grantedScopes.size() > requestedScopes.size() || !requestedScopes.containsAll(grantedScopes)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "授权错误: 用户授权超出范围");
            }

            // 更新事务
            transaction.setExpires(exchange.getRequiredAttribute(ExchangeAttributes.OAuth.GRANTING_TRANS_TIMEOUT));
            transaction.setGranted(true);
            transaction.setGrantedScope(request.getParams().getScope());
            transaction.setSession(session);

            this.authContainer.saveTransaction(request.getTenantCode(), transaction);

            exchange.getResponse().setBody(new JsonBody(Map.of("message", "授权成功")));
        }
    }
}
