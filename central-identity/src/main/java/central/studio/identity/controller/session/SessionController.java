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

package central.studio.identity.controller.session;

import central.provider.graphql.organization.AccountProvider;
import central.starter.webmvc.servlet.WebMvcRequest;
import central.starter.webmvc.servlet.WebMvcResponse;
import central.studio.identity.controller.session.param.*;
import central.studio.identity.core.session.SessionManager;
import central.studio.identity.logic.SessionLogic;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/// Session
///
/// 会话管理
///
/// @author Alan Yeh
@Slf4j
@RestController
@RequestMapping("/identity/api/sessions")
public class SessionController {

    @Setter(onMethod_ = @Autowired)
    private AccountProvider accountProvider;

    @Setter(onMethod_ = @Autowired)
    private SessionManager manager;

    @Setter(onMethod_ = @Autowired)
    private SessionLogic logic;

    /// 用于客户端自行验证会话有效性
    @GetMapping("/pubkey")
    public String getPublicKey() {
        return this.logic.getPublicKey();
    }

    /// 获取登录凭证
    ///
    /// 通过密码进行登录，此方法主要用于使用帐户名与密码进行登录的场景。
    ///
    /// 登录成功之后，返回 JWT 格式的认证信息，该认证信息可以通过公钥[#getPublicKey]校验是否由此认证服务器颁发的
    ///
    /// 认证信息只能通过服务器的验证接口[#verify]来校验是否有效
    @PostMapping("/login")
    public String login(@RequestBody @Validated LoginParams params,
                        WebMvcRequest request, WebMvcResponse response) {
        return this.logic.login(params.getAccount(), params.getPassword(), params.getSecret(), params.getClaims(), request.getAttributes(), request.getRemoteHost(), request.getTenantCode());
    }

    /// 获取登录凭证
    ///
    /// 通过凭证进行登录，此方法主要用于通过 CA、第三方认证（如 QQ、微信）等方式进行登录的场景。用户需要提前在凭证管理中录入登录凭证。
    ///
    /// 登录成功之后，返回 JWT 格式的认证信息，该认证信息可以通过公钥[#getPublicKey]校验是否由此认证服务器颁发的
    ///
    /// 认证信息只能通过服务器的验证接口[#verify]来校验是否有效
    @PostMapping("/login/credentials")
    public void loginByCredential(WebMvcRequest request, WebMvcResponse response) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, HttpStatus.NOT_IMPLEMENTED.getReasonPhrase());
    }

    /// 获取登录凭证
    ///
    /// 通过凭证进行登录，此方法主要用于通过扫一扫二维码等场景，通过已授权的凭证来给另一个端进行授权登录。当该会话过期时，通过该凭证授权的期它会话也会过期。
    ///
    /// 登录成功之后，返回 JWT 格式的认证信息，该认证信息可以通过公钥[#getPublicKey]校验是否由此认证服务器颁发的
    ///
    /// 认证信息只能通过服务器的验证接口[#verify]来校验是否有效
    @PostMapping("/login/token")
    public String loginByToken(@RequestBody @Validated LoginByTokenParams params,
                               WebMvcRequest request) {
        return this.logic.loginByToken(params.getToken(), params.getSecret(), params.getClaims(), request.getAttributes(), request.getRemoteHost(), request.getTenantCode());
    }

    /// 检测登录凭证是否有效
    ///
    /// 由于会话可能因为超时、被踢、主动退出等原因变为无效，因此客户端必须通过服务端的接口进行验证
    @PostMapping("/verify")
    public boolean verify(@RequestBody @Validated VerifyParams params,
                          WebMvcRequest request) {
        return this.logic.verify(params.getToken(), request.getAttributes(), request.getTenantCode());
    }

    /// 退出登录，将指定凭证置为无效
    @GetMapping("/logout")
    public void logout(@Validated LogoutParams params,
                       WebMvcRequest request) {
        this.logic.logout(params.getToken(), request.getAttributes(), request.getTenantCode());
    }

    /// 清除指定帐户的所有会话
    @PostMapping("/invalid")
    public void invalid(@RequestBody @Validated InvalidParams params,
                        WebMvcRequest request) {
        this.logic.clear(params.getAccountId(), request.getAttributes(), request.getTenantCode());
    }
}
