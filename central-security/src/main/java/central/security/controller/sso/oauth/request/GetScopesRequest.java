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
import central.api.provider.organization.AccountProvider;
import central.api.scheduled.ScheduledDataContext;
import central.api.scheduled.fetcher.DataFetcherType;
import central.data.organization.Account;
import central.data.saas.Application;
import central.lang.Stringx;
import central.security.controller.sso.oauth.OAuthController;
import central.security.controller.sso.oauth.option.GrantScope;
import central.security.controller.sso.oauth.support.OAuthSession;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.core.attribute.OAuthAttributes;
import central.security.core.attribute.SessionAttributes;
import central.security.core.body.JsonBody;
import central.security.core.request.Request;
import com.auth0.jwt.JWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取待授权范围
 *
 * @author Alan Yeh
 * @see OAuthController#getScopes
 * @since 2022/11/08
 */
public class GetScopesRequest extends Request {

    public GetScopesRequest(HttpServletRequest request) {
        super(request);
    }

    public static GetScopesRequest of(HttpServletRequest request) {
        return new GetScopesRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new Action();
    }

    private static class Action extends SecurityAction implements InitializingBean {
        private ScheduledDataContext context;

        private OAuthSession authContainer;

        private AccountProvider provider;

        private SessionVerifier verifier;

        @Override
        public void afterPropertiesSet() throws Exception {
            this.context = this.getBean(ScheduledDataContext.class);
            this.authContainer = this.getBean(OAuthSession.class);
            this.verifier = this.getBean(SessionVerifier.class);
            this.provider = this.getBean(AccountProvider.class);
        }

        @Override
        public void execute(SecurityExchange exchange) {
            var sessionCookie = exchange.getRequiredAttribute(SessionAttributes.COOKIE);
            var session = sessionCookie.get(exchange);
            if (!verifier.verify(session)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
            }

            var sessionJwt = JWT.decode(session);

            // 查找待授权事务
            var transCookie = exchange.getRequiredAttribute(OAuthAttributes.GRANTING_TRANS_COOKIE);
            var transId = transCookie.get(exchange);

            if (Stringx.isNullOrEmpty(transId)) {
                // 没有找到待授权事务
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "找不到待授权事务");
            }

            var transaction = this.authContainer.getAndRemoveTransaction(exchange.getRequest().getTenantCode(), transId);
            if (transaction == null) {
                // 事务过期了
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "找不到待授权事务");
            }

            // 组装授权信息
            var account = this.provider.findById(sessionJwt.getSubject());
            var application = this.context.getData(DataFetcherType.SAAS).getApplicationByCode(transaction.getClientId());
            var scopes = transaction.getScopes();

            var vo = new GetScopeVO();
            vo.getAccount().setId(account.getId());
            vo.getAccount().setUsername(account.getUsername());
            vo.getAccount().setName(account.getName());
            vo.getAccount().setAvatar(account.getAvatar());

            vo.getApplication().setCode(application.getCode());
            vo.getApplication().setName(application.getName());
            vo.getApplication().setLogo(application.getLogo());

            vo.getScopes().addAll(scopes.stream().map(it -> new Scope(it.getName(), it.getValue(), true, it == GrantScope.BASIC)).toList());

            exchange.getResponse().setBody(new JsonBody(vo));
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        private static class GetScopeVO implements Serializable {
            @Serial
            private static final long serialVersionUID = 1099644431930255452L;

            private Account account = new Account();

            private Application application = new Application();

            private List<Scope> scopes = new ArrayList<>();
        }

        /**
         * 待授权项
         */
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        private static class Scope implements Serializable {
            @Serial
            private static final long serialVersionUID = -2194870315185874029L;

            /**
             * 名称
             */
            private String name;
            /**
             * 值
             */
            private String value;
            /**
             * 是否默认选中
             */
            private boolean checked;
            /**
             * 是否必要授权（必要授权不可取消）
             */
            private boolean required;
        }
    }
}
