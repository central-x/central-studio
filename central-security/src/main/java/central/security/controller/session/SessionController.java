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

package central.security.controller.session;

import central.api.client.security.Session;
import central.api.provider.organization.AccountProvider;
import central.api.provider.security.SecurityPasswordProvider;
import central.data.organization.Account;
import central.data.security.SecurityPassword;
import central.lang.Stringx;
import central.security.Passwordx;
import central.security.controller.session.param.*;
import central.security.controller.session.support.Endpoint;
import central.security.core.attribute.AuthenticateAttributes;
import central.security.core.attribute.SessionAttributes;
import central.security.support.session.SessionManager;
import central.sql.query.Conditions;
import central.starter.webmvc.servlet.WebMvcRequest;
import central.starter.webmvc.servlet.WebMvcResponse;
import central.util.Listx;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Session
 * 会管管理
 *
 * @author Alan Yeh
 * @since 2022/10/19
 */
@Slf4j
@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    @Setter(onMethod_ = @Autowired)
    private AccountProvider accountProvider;

    @Setter(onMethod_ = @Autowired)
    private SecurityPasswordProvider passwordProvider;

    @Setter(onMethod_ = @Autowired)
    private SessionManager manager;

    /**
     * 用于客户端自行验证会话有效性
     */
    @GetMapping("/pubkey")
    public String getPublicKey() {
        return manager.getPublicKey();
    }

    /**
     * 获取登录凭证
     * <p>
     * 通过密码进行登录，此方法主要用于使用帐户名与密码进行登录的场景。
     * <p>
     * 登录成功之后，返回 JWT 格式的认证信息，该认证信息可以通过公钥 {@link #getPublicKey} 校验是否由此认证服务器颁发的
     * 认证信息只能通过服务器的验证接口 {@link #verify} 来校验是否有效
     */
    @PostMapping("/login")
    public Session login(@RequestBody @Validated LoginParams params,
                         WebMvcRequest request, WebMvcResponse response) {
        // 检查终端密钥
        var endpoint = Endpoint.resolve(request, params.getSecret());
        if (endpoint == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "终端密钥[secret]错误");
        }

        // 查询超级管理员
        // 超级管理员才可以通过主键来查询
        var account = this.accountProvider.findById(params.getAccount());
        if (account != null) {
            if (!account.getSupervisor()) {
                account = null;
            }
        }
        if (account == null) {
            // 查询普通用户
            var loginFields = request.getRequiredAttribute(AuthenticateAttributes.LOGIN_FIELD);
            var conditions = Conditions.of(Account.class);
            for (var loginField : loginFields) {
                if (!conditions.isEmpty()) {
                    conditions.or();
                }
                loginField.getFilter().accept(account, conditions);
            }

            var accounts = this.accountProvider.findBy(1L, 1L, conditions, null);
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

        // 签发会话凭证
        var issuer = request.getRequiredAttribute(SessionAttributes.ISSUER);
        var timeout = request.getRequiredAttribute(SessionAttributes.TIMEOUT);
        var endpointLimit = request.getRequiredAttribute(endpoint.getAttribute()).getLimit();
        return this.manager.issue(request.getTenantCode(), issuer, timeout, account, endpoint, endpointLimit, null);
    }

    /**
     * 获取登录凭证
     * <p>
     * 通过凭证进行登录，此方法主要用于通过 CA、第三方认证（如 QQ、微信）等方式进行登录的场景。用户需要提前在凭证管理中录入登录凭证。
     * <p>
     * 登录成功之后，返回 JWT 格式的认证信息，该认证信息可以通过公钥 {@link #getPublicKey} 校验是否由此认证服务器颁发的
     * 认证信息只能通过服务器的验证接口 {@link #verify} 来校验是否有效
     */
    @PostMapping("/login/credentials")
    public void loginByCredential(WebMvcRequest request, WebMvcResponse response) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, HttpStatus.NOT_IMPLEMENTED.getReasonPhrase());
    }

    /**
     * 获取登录凭证
     * <p>
     * 通过凭证进行登录，此方法主要用于通过扫一扫二维码等场景，通过已授权的凭证来给另一个端进行授权登录。当该会话过期时，通过该凭证授权的
     * 期它会话也会过期。
     * <p>
     * 登录成功之后，返回 JWT 格式的认证信息，该认证信息可以通过公钥 {@link #getPublicKey} 校验是否由此认证服务器颁发的
     * 认证信息只能通过服务器的验证接口 {@link #verify} 来校验是否有效
     */
    @PostMapping("/login/token")
    public Session loginByToken(@RequestBody @Validated LoginByTokenParams params,
                               WebMvcRequest request) {
        // 检查终端密钥
        var endpoint = Endpoint.resolve(request, params.getSecret());
        if (endpoint == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "终端密钥[secret]错误");
        }
        Session session = Session.of(params.getToken());

        if (!this.manager.verify(request.getTenantCode(), session)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "会话凭证[token]无效");
        }


        if (Stringx.isNotBlank(session.getSource())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不允许二次颁发会话");
        }

        var account = this.accountProvider.findById(session.getAccountId());
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("帐户[account={}]不存在", session.getUsername()));
        }
        if (!account.getEnabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("用户[account={}]已禁用", session.getUsername()));
        }
        if (account.getDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("用户[account={}]已删除", session.getUsername()));
        }

        var issuer = request.getRequiredAttribute(SessionAttributes.ISSUER);
        var timeout = request.getRequiredAttribute(SessionAttributes.TIMEOUT);
        var endpointLimit = request.getRequiredAttribute(endpoint.getAttribute()).getLimit();

        return this.manager.issue(request.getTenantCode(), session, issuer, timeout, endpoint, endpointLimit, null);
    }

    /**
     * 检测登录凭证是否有效
     * <p>
     * 由于会话可能因为超时、被踢、主动退出等原因变为无效，因此客户端必须通过服务端的接口进行验证
     */
    @PostMapping("/verify")
    public boolean verify(@RequestBody @Validated VerifyParams params,
                          WebMvcRequest request) {
        return this.manager.verify(request.getTenantCode(), Session.of(params.getToken()));
    }

    /**
     * 退出登录，将指定凭证置为无效
     */
    @GetMapping("/logout")
    public void logout(@Validated LogoutParams params,
                       WebMvcRequest request) {
        this.manager.invalid(request.getTenantCode(), Session.of(params.getToken()));
    }

    /**
     * 清除指定帐户的所有会话
     */
    @PostMapping("/invalid")
    public void invalid(@RequestBody @Validated InvalidParams params,
                        WebMvcRequest request) {
        this.manager.clear(request.getTenantCode(), params.getAccountId());
    }
}
