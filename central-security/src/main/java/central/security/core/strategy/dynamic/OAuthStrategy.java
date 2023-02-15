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
import central.security.controller.sso.oauth.option.GrantScope;
import central.security.core.SecurityExchange;
import central.security.core.attribute.OAuthAttributes;
import central.security.core.strategy.Strategy;
import central.security.core.strategy.StrategyChain;
import central.validation.Label;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;

/**
 * OAuth 策略
 *
 * @author Alan Yeh
 * @since 2022/11/06
 */
public class OAuthStrategy implements Strategy {

    @Control(label = "说明", type = ControlType.LABEL,
            defaultValue = "　　本策略用于 OAuth 2.0 认证服务。认证中心实现了 OAuth 2.0 标准里面的授权码（Authorization Code）模式。")
    private String label;

    @Label("启用")
    @NotNull
    @Control(label = "启用", type = ControlType.RADIO, defaultValue = "0", comment = "用于控制是否启")
    @Setter
    private BooleanEnum enabled;

    @Label("授权范围")
    @NotEmpty
    @Control(label = "授权范围", type = ControlType.CHECKBOX, defaultValue = "user:basic",
            comment = "用于控制第三方业务系统可以获取的用户属性范围。")
    @Setter
    private List<GrantScope> scopes;

    @Label("自动授权")
    @NotNull
    @Control(label = "自动授权", type = ControlType.RADIO, defaultValue = "1", comment = "用于控制是否自动完成授权过程。" +
            "如果选择了自动授权，则用户在完成登录后，系统会将授权范围内的所有梳限授予应用系统；" +
            "如果没有自动授权，则用户在完成登录后，需要进行二次确认授权才会跳转到应用系统。")
    @Setter
    private BooleanEnum authGranting;

    @Label("过期时间")
    @NotNull
    @Min(60 * 1000L)
    @Max(24 * 60 * 60 * 1000L)
    @Control(label = "过期时间", type = ControlType.NUMBER, defaultValue = "180000", comment = "用于控制访问凭证（Access Token）的超时时间")
    @Setter
    private Long timeout;

    @Override
    public void execute(SecurityExchange exchange, StrategyChain chain) {
        exchange.setAttribute(OAuthAttributes.ENABLED, this.enabled.getJValue());
        exchange.setAttribute(OAuthAttributes.SCOPES, new HashSet<>(this.scopes));
        exchange.setAttribute(OAuthAttributes.AUTO_GRANTING, this.authGranting.getJValue());
        exchange.setAttribute(OAuthAttributes.ACCESS_TOKEN_TIMEOUT, Duration.ofMillis(this.timeout));

        chain.execute(exchange);
    }
}
