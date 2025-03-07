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

package central.studio.identity.logic;

import central.data.identity.IdentityPassword;
import central.data.organization.Account;
import central.identity.client.Session;
import central.lang.Stringx;
import central.provider.graphql.identity.IdentityPasswordProvider;
import central.provider.graphql.identity.IdentityRecordProvider;
import central.provider.graphql.organization.AccountProvider;
import central.security.Passwordx;
import central.sql.query.Conditions;
import central.starter.webmvc.servlet.HttpAttributes;
import central.studio.identity.controller.session.support.Endpoint;
import central.studio.identity.core.attribute.AuthenticateAttributes;
import central.studio.identity.core.attribute.SessionAttributes;
import central.studio.identity.core.session.SessionManager;
import central.util.Listx;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Objects;

/// Session Logic
///
/// 会话逻辑
///
/// @author Alan Yeh
@Slf4j
@Service
public class SessionLogic {

    @Setter(onMethod_ = @Autowired)
    private AccountProvider accountProvider;

    @Setter(onMethod_ = @Autowired)
    private IdentityPasswordProvider passwordProvider;

    @Setter(onMethod_ = @Autowired)
    private IdentityRecordProvider recordProvider;

    @Setter(onMethod_ = @Autowired)
    private SessionManager manager;

    /// 获取公钥
    public String getPublicKey() {
        return this.manager.getPublicKey();
    }

    /// 登录
    ///
    /// @param accountStr  帐户名
    /// @param passwordStr 密码
    /// @param secret      终端密钥
    /// @param claims      JWT Claim
    /// @param attributes  认证属性
    /// @param remoteHost  用户地址
    public String login(String accountStr, String passwordStr, String secret, Map<String, Object> claims, HttpAttributes attributes, String remoteHost, String tenant) {
        // 检查终端密钥
        var endpoint = Endpoint.resolve(attributes, secret);
        if (endpoint == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "终端密钥[secret]错误");
        }

        // 查询超级管理员
        // 超级管理员才可以通过主键来查询
        var account = this.accountProvider.findById(accountStr);
        if (account != null) {
            if (!account.getSupervisor()) {
                account = null;
            }
        }
        if (account == null) {
            // 查询普通用户
            var loginFields = attributes.getRequiredAttribute(AuthenticateAttributes.LOGIN_FIELD);
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("用户[account={}]不存在", accountStr));
        }

        if (!account.getEnabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("用户[account={}]已禁用", accountStr));
        }

        if (account.getDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("用户[account={}]已删除", accountStr));
        }

        // 查找用户密码
        IdentityPassword password;
        if (account.getSupervisor()) {
            // 超级管理员
            password = this.passwordProvider.findById(account.getId());
        } else {
            var passwords = this.passwordProvider.findBy(null, null, Conditions.of(IdentityPassword.class).eq(IdentityPassword::getAccountId, account.getId()).eq(IdentityPassword::getEnabled, Boolean.TRUE), null);
            password = Listx.getFirstOrNull(passwords);
        }

        if (password == null) {
            // 当前用户没有设置初始密码
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, Stringx.format("用户[account={}]未设置密码", accountStr));
        }

        // 验证密码正确性
        if (!Passwordx.verify(passwordStr, password.getValue())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("用户[account={}]密码错误", accountStr));
        }

        // 签发会话凭证
        var issuer = attributes.getRequiredAttribute(SessionAttributes.ISSUER);
        var timeout = attributes.getRequiredAttribute(SessionAttributes.TIMEOUT);
        var endpointLimit = attributes.getRequiredAttribute(endpoint.getAttribute()).getLimit();
        var session = this.manager.issue(tenant, issuer, timeout, account, endpoint, endpointLimit, remoteHost, claims);
        return session.getToken();
    }

    /// 获取登录凭证
    ///
    /// 通过凭证进行登录，此方法主要用于通过扫一扫二维码等场景，通过已授权的凭证来给另一个端进行授权登录。当该会话过期时，通过该凭证授权的
    /// 期它会话也会过期。
    ///
    /// 登录成功之后，返回 JWT 格式的认证信息，该认证信息可以通过公钥[#getPublicKey]校验是否由此认证服务器颁发的
    ///
    /// 认证信息只能通过服务器的验证接口[#verify]来校验是否有效
    public String loginByToken(String token, String secret, Map<String, Object> claims, HttpAttributes attributes, String remoteHost, String tenant) {
        // 检查终端密钥
        var endpoint = Endpoint.resolve(attributes, secret);
        if (endpoint == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "终端密钥[secret]错误");
        }
        Session session = Session.of(token);

        if (!this.manager.verify(tenant, session)) {
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

        var issuer = attributes.getRequiredAttribute(SessionAttributes.ISSUER);
        var timeout = attributes.getRequiredAttribute(SessionAttributes.TIMEOUT);
        var endpointLimit = attributes.getRequiredAttribute(endpoint.getAttribute()).getLimit();

        return this.manager.issue(tenant, session, issuer, timeout, endpoint, endpointLimit, remoteHost, claims)
                .getToken();
    }

    /// 检测登录凭证是否有效
    ///
    /// 由于会话可能因为超时、被踢、主动退出等原因变为无效，因此客户端必须通过服务端的接口进行验证
    public boolean verify(String token, HttpAttributes attributes, String tenant) {
        var session = Session.of(token);

        if (!Objects.equals(attributes.getRequiredAttribute(SessionAttributes.ISSUER), session.getIssuer())) {
            log.info("会话凭证[token]验证不通过: 不是本系统颁发的凭证");
            return false;
        }

        if (!Objects.equals(tenant, session.getTenantCode())) {
            log.info("会话凭证[token]验证不通过: 租户不匹配");
            return false;
        }

        return this.manager.verify(tenant, session);
    }

    /// 退出登录，将指定凭证置为无效
    public void logout(String token, HttpAttributes attributes, String tenant) {
        this.manager.invalid(tenant, Session.of(token));
    }

    /// 清除指定用户所有会话
    public void clear(String accountId, HttpAttributes attributes, String tenant) {
        this.manager.clear(tenant, accountId);
    }
}
