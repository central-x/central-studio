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

package central.security.controller.index;

import central.api.client.security.SessionVerifier;
import central.lang.Stringx;
import central.security.controller.index.param.IndexParams;
import central.security.controller.index.request.*;
import central.security.core.CookieManager;
import central.security.core.SecurityDispatcher;
import central.security.core.SecurityExchange;
import central.security.core.SecurityResponse;
import central.security.core.attribute.SessionAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 首页
 *
 * @author Alan Yeh
 * @since 2022/10/14
 */
@Controller
public class IndexController {

    @Setter(onMethod_ = @Autowired)
    private SecurityDispatcher dispatcher;

    /**
     * 获取首页
     */
    @GetMapping("/")
    public View index(@Validated IndexParams params,
                      @Autowired SessionVerifier verifier,
                      HttpServletRequest request) {

        if (Stringx.isNotBlank(params.getRedirectUri())) {
            // 检测 redirectUrl 是否跨域
            // 统一认证不允许进行跨域验证，只能重定向到本域的地址
            if (!params.getRedirectUri().startsWith(request.getScheme() + "://" + request.getServerName())) {
                // 如果出现跨域，直接重定向到统一认证自己的界面
                var location = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString()).replaceQuery("").build().toString();

                return new RedirectView(location);
            }

            // 检测会话有效性
            // 如果当前会话有效，则直接重定向到指定的地址
            var cookieManager = (CookieManager) request.getAttribute(SessionAttributes.COOKIE.getCode());
            var token = cookieManager.get(request);

            if (verifier.verify(token)) {
                return new RedirectView(params.getRedirectUri());
            }
        }

        // 其余情况，返回登录界面
        return new InternalResourceView("index.html");
    }

    /**
     * 获取当前已登录的用户信息
     */
    @GetMapping("/api/account")
    public void getAccount(HttpServletRequest request, HttpServletResponse response) {
        this.dispatcher.dispatch(SecurityExchange.of(GetAccountRequest.of(request), SecurityResponse.of(response)));
    }

    /**
     * 获取登录选项
     */
    @GetMapping("/api/options")
    public void getOptions(HttpServletRequest request, HttpServletResponse response) {
        this.dispatcher.dispatch(SecurityExchange.of(GetOptionsRequest.of(request), SecurityResponse.of(response)));
    }

    /**
     * 获取验证码
     */
    @GetMapping("/api/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
        this.dispatcher.dispatch(SecurityExchange.of(GetCaptchaRequest.of(request), SecurityResponse.of(response)));
    }

    /**
     * 登录
     */
    @PostMapping("/api/login")
    public void login(HttpServletRequest request, HttpServletResponse response) {
        this.dispatcher.dispatch(SecurityExchange.of(LoginRequest.of(request), SecurityResponse.of(response)));
    }

    /**
     * 退出登录
     */
    @GetMapping("/api/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        this.dispatcher.dispatch(SecurityExchange.of(LogoutRequest.of(request), SecurityResponse.of(response)));
    }
}
