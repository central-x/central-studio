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

package central.security.controller.sso.cas;

import central.security.controller.sso.cas.request.LoginRequest;
import central.security.controller.sso.cas.request.LogoutRequest;
import central.security.controller.sso.cas.request.ValidateRequest;
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
 * Central Authentication Service
 *
 * @author Alan Yeh
 * @see <a href="https://apereo.github.io/cas/6.6.x/protocol/CAS-Protocol.html">CAS Protocol</a>
 * @see <a href="https://apereo.github.io/cas/6.6.x/protocol/CAS-Protocol-Specification.html">CAS Protocol Specification</a>
 * @since 2022/10/19
 */
@Controller
@RequestMapping("/sso/cas")
public class CasController {
    @Setter(onMethod_ = @Autowired)
    private SecurityDispatcher dispatcher;

    /**
     * 认证入口
     */
    @GetMapping("/login")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.dispatcher.dispatch(SecurityExchange.of(LoginRequest.of(request), SecurityResponse.of(response)));
    }

    /**
     * ST 认证
     */
    @PostMapping({"/serviceValidate", "/p3/serviceValidate"})
    public void validate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.dispatcher.dispatch(SecurityExchange.of(ValidateRequest.of(request), SecurityResponse.of(response)));
    }

    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.dispatcher.dispatch(SecurityExchange.of(LogoutRequest.of(request), SecurityResponse.of(response)));
    }
}
