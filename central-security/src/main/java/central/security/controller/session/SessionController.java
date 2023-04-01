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

import central.api.client.security.SessionClaims;
import central.api.provider.organization.AccountProvider;
import central.api.provider.security.SecurityPasswordProvider;
import central.data.organization.Account;
import central.data.security.SecurityPassword;
import central.lang.Stringx;
import central.security.Passwordx;
import central.security.controller.session.param.LoginByTokenParams;
import central.security.controller.session.param.LoginParams;
import central.security.controller.session.request.InvalidRequest;
import central.security.controller.session.request.LogoutRequest;
import central.security.controller.session.request.VerifyRequest;
import central.security.controller.session.support.Endpoint;
import central.security.core.SecurityDispatcher;
import central.security.core.SecurityExchange;
import central.security.core.SecurityResponse;
import central.security.core.attribute.SessionAttributes;
import central.security.signer.KeyPair;
import central.security.support.session.SessionContainer;
import central.sql.query.Conditions;
import central.starter.webmvc.servlet.WebMvcRequest;
import central.starter.webmvc.servlet.WebMvcResponse;
import central.util.Guidx;
import central.util.Listx;
import central.util.Mapx;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * Session
 * 会管管理
 *
 * @author Alan Yeh
 * @since 2022/10/19
 */
@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    @Setter(onMethod_ = @Autowired)
    private SecurityDispatcher dispatcher;

    @Setter(onMethod_ = @Autowired)
    private KeyPair keyPair;

    @Setter(onMethod_ = @Autowired)
    private AccountProvider accountProvider;

    @Setter(onMethod_ = @Autowired)
    private SecurityPasswordProvider passwordProvider;

    @Setter(onMethod_ = @Autowired)
    private SessionContainer sessionContainer;

    /**
     * 用于客户端自行验证会话有效性
     */
    @GetMapping("/pubkey")
    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(this.keyPair.getVerifyKey().getEncoded());
    }

    /**
     * 颁发会话
     */
    private String issue(WebMvcRequest request, WebMvcResponse response, DecodedJWT source, Account account, Endpoint endpoint, Map<String, Object> claims) {
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
                .withIssuer(request.getRequiredAttribute(SessionAttributes.ISSUER))
                // 会话有效时间
                .withClaim(SessionClaims.TIMEOUT, request.getRequiredAttribute(SessionAttributes.TIMEOUT))
                // 租户标识
                .withClaim(SessionClaims.TENANT_CODE, request.getTenantCode());

        if (source != null) {
            // 记录颁发当前会话的会话标识
            jwt.withClaim(SessionClaims.SOURCE, source.getId());
        }

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

    /**
     * 获取登录凭证
     * <p>
     * 通过密码进行登录，此方法主要用于使用帐户名与密码进行登录的场景。
     * <p>
     * 登录成功之后，返回 JWT 格式的认证信息，该认证信息可以通过公钥 {@link #getPublicKey} 校验是否由此认证服务器颁发的
     * 认证信息只能通过服务器的验证接口 {@link #verify} 来校验是否有效
     */
    @PostMapping("/login")
    public String login(@RequestBody @Validated LoginParams params,
                        WebMvcRequest request, WebMvcResponse response) {
        // 检查终端密钥
        var endpoint = Endpoint.resolve(request, params.getSecret());
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

        var token = this.issue(request, response, null, account, endpoint, params.getClaims());
        var limit = request.getRequiredAttribute(endpoint.getAttribute()).getLimit();

        this.sessionContainer.save(JWT.decode(token), limit);

        return token;
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
    public String loginByToken(@RequestBody @Validated LoginByTokenParams params,
                               WebMvcRequest request, WebMvcResponse response) {
        // 检查终端密钥
        var endpoint = Endpoint.resolve(request, params.getSecret());
        if (endpoint == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "终端密钥[secret]错误");
        }

        DecodedJWT token;
        try {
            token = JWT.require(Algorithm.RSA256((RSAPublicKey) keyPair.getVerifyKey()))
                    .withClaim(SessionClaims.TENANT_CODE, request.getTenantCode())
                    .withIssuer(request.getRequiredAttribute(SessionAttributes.ISSUER))
                    .build().verify(params.getToken());
        } catch (JWTVerificationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不是有效会话凭证");
        }

        if (!token.getClaim(SessionClaims.SOURCE).isMissing()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不允许二次颁发会话");
        }

        if (!this.sessionContainer.verify(token)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "会话已过期");
        }

        var account = this.accountProvider.findById(token.getSubject());
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("帐户[account={}]不存在", token.getClaim(SessionClaims.USERNAME).asString()));
        }
        if (!account.getEnabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("用户[account={}]已禁用", token.getClaim(SessionClaims.USERNAME).asString()));
        }
        if (account.getDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("用户[account={}]已删除", token.getClaim(SessionClaims.USERNAME).asString()));
        }

        var issued = this.issue(request, response, token, account, endpoint, params.getClaims());
        var limit = request.getRequiredAttribute(endpoint.getAttribute()).getLimit();

        sessionContainer.save(JWT.decode(issued), limit);

        return issued;
    }

    /**
     * 检测登录凭证是否有效
     * <p>
     * 由于会话可能因为超时、被踢、主动退出等原因变为无效，因此客户端必须通过服务端的接口进行验证
     */
    @PostMapping("/verify")
    public void verify(HttpServletRequest request, HttpServletResponse response) {
        dispatcher.dispatch(SecurityExchange.of(VerifyRequest.of(request), SecurityResponse.of(response)));
    }

    /**
     * 退出登录，将指定凭证置为无效
     */
    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        dispatcher.dispatch(SecurityExchange.of(LogoutRequest.of(request), SecurityResponse.of(response)));
    }

    /**
     * 清除指定帐户的所有会话
     */
    @PostMapping("/invalid")
    public void invalid(HttpServletRequest request, HttpServletResponse response) {
        dispatcher.dispatch(SecurityExchange.of(InvalidRequest.of(request), SecurityResponse.of(response)));
    }
}
