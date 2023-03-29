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

import central.security.controller.session.request.*;
import central.security.core.SecurityDispatcher;
import central.security.core.SecurityExchange;
import central.security.core.SecurityResponse;
import central.security.signer.KeyPair;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Base64;

/**
 * Session
 * 会管管理
 *
 * @author Alan Yeh
 * @since 2022/10/19
 */
@Controller
@RequestMapping("/api/sessions")
public class SessionController {

    @Setter(onMethod_ = @Autowired)
    private SecurityDispatcher dispatcher;

    @Setter(onMethod_ = @Autowired)
    private KeyPair keyPair;

    /**
     * 用于客户端自行验证会话有效性
     */
    @GetMapping("/pubkey")
    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(this.keyPair.getVerifyKey().getEncoded());
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
    public void login(HttpServletRequest request, HttpServletResponse response) {
        dispatcher.dispatch(SecurityExchange.of(LoginRequest.of(request), SecurityResponse.of(response)));
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
    public void loginByCredential(HttpServletRequest request, HttpServletResponse response) {
        dispatcher.dispatch(SecurityExchange.of(LoginByCredentialRequest.of(request), SecurityResponse.of(response)));
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
    public void loginByToken(HttpServletRequest request, HttpServletResponse response) {
        dispatcher.dispatch(SecurityExchange.of(LoginByTokenRequest.of(request), SecurityResponse.of(response)));
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
