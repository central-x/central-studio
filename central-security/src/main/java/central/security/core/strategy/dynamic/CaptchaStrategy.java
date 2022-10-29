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

package central.security.core.strategy.dynamic;

import central.lang.BooleanEnum;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.security.core.SecurityExchange;
import central.security.core.attribute.ExchangeAttributes;
import central.security.core.strategy.Strategy;
import central.security.core.strategy.StrategyChain;
import jakarta.validation.constraints.NotNull;

/**
 * 验证码策略
 *
 * @author Alan Yeh
 * @since 2022/10/23
 */
public class CaptchaStrategy implements Strategy {

    @Control(label = "说明", type = ControlType.LABEl, defaultValue = "　　本策略用于控制登录时的验证码和策略")
    private String label;

    @NotNull
    @Control(label = "启用", type = ControlType.RADIO, defaultValue = "1", comment = "用于控制登录时是否需要输入验证码")
    private BooleanEnum enabled;

    @NotNull
    @Control(label = "有效期", type = ControlType.NUMBER, defaultValue = "180000", comment = "单位（毫秒）。用于控制验证码的失效时间，失效后需要重新获取验证码")
    private Long timeout;

    @NotNull
    @Control(label = "大小写敏感", type = ControlType.RADIO, defaultValue = "0", comment = "校验验证码时，是否大小写敏感")
    private BooleanEnum caseSensitive;

    @NotNull
    @Control(label = "Cookie", defaultValue = "X-Auth-Captcha", comment = "用于控制验证码跟踪的 Cookie 字段")
    private String cookie;

    @Override
    public void execute(SecurityExchange exchange, StrategyChain chain) {
        // 保存
        exchange.setAttribute(ExchangeAttributes.Captcha.Options.ENABLED, this.enabled.getJValue());



        chain.execute(exchange);
    }
}
