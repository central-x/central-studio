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
import central.lang.Stringx;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.security.core.CookieManager;
import central.security.core.SecurityExchange;
import central.security.core.ability.CaptchableRequest;
import central.security.core.attribute.CaptchaAttributes;
import central.security.core.strategy.Strategy;
import central.security.core.strategy.StrategyChain;
import central.security.support.captcha.CaptchaContainer;
import central.validation.Label;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * 验证码策略
 *
 * @author Alan Yeh
 * @since 2022/10/23
 */
public class CaptchaStrategy implements Strategy {

    @Control(label = "说明", type = ControlType.LABEL, defaultValue = "　　本策略用于控制登录时的验证码和策略")
    private String label;

    @Label("启用")
    @NotNull
    @Control(label = "启用", type = ControlType.RADIO, defaultValue = "1", comment = "用于控制登录时是否需要输入验证码")
    @Setter
    private BooleanEnum enabled;

    @NotNull
    @Control(label = "有效期", type = ControlType.NUMBER, defaultValue = "180000", comment = "单位（毫秒）。用于控制验证码的失效时间，失效后需要重新获取验证码")
    @Setter
    private Long timeout;

    @Label("大小写敏感")
    @NotNull
    @Control(label = "大小写敏感", type = ControlType.RADIO, defaultValue = "0", comment = "校验验证码时，是否大小写敏感")
    @Setter
    private BooleanEnum caseSensitive;

    @Label("Cookie")
    @NotBlank
    @Size(min = 1, max = 36)
    @Control(label = "Cookie", defaultValue = "X-Auth-Captcha", comment = "用于控制验证码跟踪的 Cookie 字段")
    @Setter
    private String cookie;

    @Override
    public void execute(SecurityExchange exchange, StrategyChain chain) {
        // 设置请求属性
        exchange.setAttribute(CaptchaAttributes.ENABLED, this.enabled.getJValue());
        exchange.setAttribute(CaptchaAttributes.CASE_SENSITIVE, this.caseSensitive.getJValue());
        exchange.setAttribute(CaptchaAttributes.COOKIE, new CookieManager(this.cookie));

        if (BooleanEnum.TRUE.isCompatibleWith(this.enabled)) {

            if (exchange.getRequest() instanceof CaptchableRequest) {
                // 需要处理验证码
                this.validateCaptcha(exchange);
            }
        }

        chain.execute(exchange);
    }

    @Setter(onMethod_ = @Autowired)
    private CaptchaContainer container;

    /**
     * 验证验证码
     */
    private void validateCaptcha(SecurityExchange exchange) {
        var cookieManager = exchange.getRequiredAttribute(CaptchaAttributes.COOKIE);
        var captchaKey = cookieManager.get(exchange);

        if (Stringx.isNullOrBlank(captchaKey)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请获取验证码后再登录");
        }

        // 获取用户输入的验证码信息
        var value = ((CaptchableRequest) exchange.getRequest()).getCaptcha();
        if (Stringx.isNullOrBlank(value)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "验输入验证码");
        }

        // 从容器获取验证码信息
        var captcha = this.container.get(captchaKey);

        if (Stringx.isNullOrBlank(captcha)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "验证码已过期，请重新获取");
        }

        // 验证
        if (exchange.getRequiredAttribute(CaptchaAttributes.CASE_SENSITIVE)) {
            if (!captcha.equals(value)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "验证码错误");
            }
        } else {
            if (!captcha.equalsIgnoreCase(value)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "验证码错误");
            }
        }

        // 验证通过
        cookieManager.remove(exchange);
    }
}
