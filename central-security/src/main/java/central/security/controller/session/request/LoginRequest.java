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
import central.api.provider.organization.AccountProvider;
import central.api.provider.security.SecurityPasswordProvider;
import central.data.organization.Account;
import central.data.security.SecurityPassword;
import central.lang.Stringx;
import central.security.Passwordx;
import central.security.controller.session.SessionController;
import central.security.controller.session.support.Endpoint;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.core.SecuritySession;
import central.security.core.attribute.SessionAttributes;
import central.security.core.body.StringBody;
import central.security.core.request.Request;
import central.security.signer.KeyPair;
import central.sql.query.Conditions;
import central.util.Guidx;
import central.util.Listx;
import central.util.Mapx;
import central.validation.Label;
import central.validation.Validatex;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Map;

/**
 * 帐户密码登录
 *
 * @author Alan Yeh
 * @see SessionController#login
 * @since 2022/10/19
 */
public class LoginRequest extends Request {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Params {
        @Label("帐户")
        @NotBlank
        private String account;

        @Label("密码")
        @NotBlank
        private String password;

        @Label("终端密钥")
        @NotBlank
        private String secret;

        @Label("JWT Claim")
        private Map<String, Object> claims;
    }

    @Getter
    private final Params params;

    public LoginRequest(HttpServletRequest request) {
        super(request);

        if (MediaType.APPLICATION_JSON.isCompatibleWith(this.getContentType())) {
            this.params = this.bindBody(Params.class);
        } else {
            this.params = this.bindParameter(Params.class);
        }

        Validatex.Default().validate(params, new Class[0], (message) -> new ResponseStatusException(HttpStatus.BAD_REQUEST, message));
    }

    public static LoginRequest of(HttpServletRequest request) {
        return new LoginRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new LoginAction();
    }

    private static class LoginAction extends SecurityAction implements InitializingBean {

        private AccountProvider accountProvider;

        private SecurityPasswordProvider passwordProvider;

        private KeyPair keyPair;

        private SecuritySession session;

        @Override
        public void afterPropertiesSet() throws Exception {
            this.accountProvider = this.getBean(AccountProvider.class);
            this.passwordProvider = this.getBean(SecurityPasswordProvider.class);
            this.keyPair = this.getBean(KeyPair.class);
            this.session = this.getBean(SecuritySession.class);
        }

        @Override
        public void execute(SecurityExchange exchange) {
            var params = ((LoginRequest) exchange.getRequest()).getParams();
            // 检查终端密钥
            var endpoint = Endpoint.resolve(exchange, params.getSecret());
            if (endpoint == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "终端密钥[secret]错误");
            }

            // 查询超级管理员
            // 超级管理员才可以通过主键来查询
            Account account = this.accountProvider.findById(params.getAccount());
            if (account != null) {
                if (!account.getSupervisor()) {
                    account = null;
                }
            }
            if (account == null) {
                // 查询普通用户
                var accounts = this.accountProvider.findBy(1L, 1L, Conditions.of(Account.class)
                        .eq(Account::getUsername, params.getAccount())
                        .or()
                        .eq(Account::getMobile, params.getAccount())
                        .or()
                        .eq(Account::getEmail, params.getAccount()), null);
                account = Listx.getFirstOrNull(accounts);
            }

            if (account == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("用户[account={}]不存在", params.getAccount()));
            }

            if (!account.getEnabled()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("用户[account={}]已禁用", params.getAccount()));
            }

            if (account.getDeleted()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("用户[account={}]已删除", params.getAccount()));
            }

            // 查找用户密码

            SecurityPassword password;
            if (account.getSupervisor()) {
                // 超级管理员
                password = this.passwordProvider.findById(account.getId());
            } else {
                var passwords = this.passwordProvider.findBy(null, null, Conditions.of(SecurityPassword.class).eq(SecurityPassword::getAccountId, account.getId()), null);
                password = Listx.getFirstOrNull(passwords);
            }

            if (password == null) {
                // 当前用户没有设置初始密码
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, Stringx.format("用户[account={}]未设置密码", params.getAccount()));
            }

            // 验证密码正确性
            if (!Passwordx.verify(params.getPassword(), password.getValue())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("用户[account={}]密码错误", params.getAccount()));
            }

            var token = this.issue(exchange, account, endpoint, params.getClaims());
            var limit = exchange.getRequiredAttribute(endpoint.getAttribute()).getLimit();

            session.save(JWT.decode(token), limit);

            exchange.getResponse().setBody(new StringBody(token));
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
                    .withIssuer(exchange.getRequiredAttribute(SessionAttributes.ISSUER))
                    // 会话有效时间
                    .withClaim(SessionClaims.TIMEOUT, exchange.getRequiredAttribute(SessionAttributes.TIMEOUT))
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
