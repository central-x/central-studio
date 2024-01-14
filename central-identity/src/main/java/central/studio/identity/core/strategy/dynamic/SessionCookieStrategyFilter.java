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

package central.studio.identity.core.strategy.dynamic;

import central.lang.BooleanEnum;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.studio.identity.core.CookieManager;
import central.studio.identity.core.attribute.SessionAttributes;
import central.studio.identity.core.strategy.StrategyFilter;
import central.studio.identity.core.strategy.StrategyFilterChain;
import central.starter.webmvc.servlet.WebMvcRequest;
import central.starter.webmvc.servlet.WebMvcResponse;
import central.validation.Label;
import jakarta.servlet.ServletException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;

/**
 * 会话 Cookie 策略
 *
 * @author Alan Yeh
 * @since 2022/11/06
 */
public class SessionCookieStrategyFilter implements StrategyFilter, InitializingBean {
    @Control(label = "说明", type = ControlType.LABEL, defaultValue = "　　本策略用于控制会话的 Cookie 生成规则。")
    private String label;

    @Label("Cookie")
    @NotBlank
    @Size(min = 1, max = 50)
    @Control(label = "Cookie", defaultValue = "Authorization", comment = "设置 Cookie 名")
    private String name;

    @Label("Domain")
    @Size(min = 1, max = 50)
    @Control(label = "Domain", required = false, comment = "设置 Cookie 的 domain 属性，用于控制 Cookie 是否可以跨域访问")
    private String domain;

    @Label("HttpOnly")
    @NotNull
    @Control(label = "HttpOnly", defaultValue = "1", type = ControlType.RADIO, comment = "设置 Cookie 的 httpOnly 属性，用于控制 JavaScript 是否能访问 Cookie。")
    private BooleanEnum httpOnly;

    @Label("Secure")
    @NotNull
    @Control(label = "Secure", defaultValue = "0", type = ControlType.RADIO, comment = "设置 Cookie 的 Secure 属性，用于控制 Cookie 是否仅在 HTTPS 协议下传输")
    private BooleanEnum secure;

    private CookieManager cookie;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.cookie = new CookieManager(this.name, this.domain, httpOnly.getJValue(), secure.getJValue());
    }

    @Override
    public void execute(WebMvcRequest request, WebMvcResponse response, StrategyFilterChain chain) throws IOException, ServletException {
        request.setAttribute(SessionAttributes.COOKIE, this.cookie);
        chain.execute(request, response);
    }
}
