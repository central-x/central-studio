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

package central.security.controller.sso.oauth;

import central.security.controller.sso.oauth.request.*;
import central.security.core.SecurityDispatcher;
import central.security.core.SecurityExchange;
import central.security.core.SecurityResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * OAuth2.0
 * <p>
 * 不支持 refresh_token，因为这个认证接口不是一直调用的，认证一次之后，应用系统与认证中心就基本没什么关系了，所以 refresh_token 没什么必要。
 *
 * @author Alan Yeh
 * @see <a href="https://oauth.net/2/">OAuth 2.0</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749">RFC6749</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1">Authorization Code Grant</a>
 * @since 2022/10/19
 */
@Controller
@RequestMapping("/sso/oauth2")
public class OAuthController {

    @Setter(onMethod_ = @Autowired)
    private SecurityDispatcher dispatcher;

    /**
     * OAuth 2.0 认证
     * <p>
     * 获取授权码（Authorization Code）
     * <p>
     * 完成认证之后，本接口会添加 code 参数和 state 参数重定向到 redirect_uri。
     * 业务系统在接收到这个 code 之后，需要在后台访问开放平台 /api/sso/oauth/token 获取会话凭证，通过会话凭证获取用户信息
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-10.5">Authorization Codes</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.1">Authorization Request</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2">Authorization Response</a>
     */
    @GetMapping("/authorize")
    public void authorize(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.dispatcher.dispatch(SecurityExchange.of(AuthorizeRequest.of(request), SecurityResponse.of(response)));
    }

    /**
     * 获取待授权范围列表
     */
    @GetMapping("/scopes")
    public void getScopes(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.dispatcher.dispatch(SecurityExchange.of(GetScopesRequest.of(request), SecurityResponse.of(response)));
    }

    /**
     * 授权
     */
    @PostMapping("/scopes")
    public void grant(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.dispatcher.dispatch(SecurityExchange.of(GrantRequest.of(request), SecurityResponse.of(response)));
    }

    /**
     * 获取访问凭证（Access Token）
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-10.3">Access Tokens</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.3">Access Token Request</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.4">Access Token Response</a>
     */
    @PostMapping("/access_token")
    public void getAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.dispatcher.dispatch(SecurityExchange.of(AccessTokenRequest.of(request), SecurityResponse.of(response)));
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/user")
    public void getUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.dispatcher.dispatch(SecurityExchange.of(GetUserRequest.of(request), SecurityResponse.of(response)));
    }
}
