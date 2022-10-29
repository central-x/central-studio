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

import central.security.controller.index.request.*;
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
    public void index(HttpServletRequest request, HttpServletResponse response) {
        this.dispatcher.dispatch(SecurityExchange.of(IndexRequest.of(request), SecurityResponse.of(response)));
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
