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

import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.studio.identity.core.attribute.SessionAttributes;
import central.studio.identity.core.strategy.StrategyFilter;
import central.studio.identity.core.strategy.StrategyFilterChain;
import central.starter.webmvc.servlet.WebMvcRequest;
import central.starter.webmvc.servlet.WebMvcResponse;
import central.validation.Label;
import jakarta.servlet.ServletException;
import jakarta.validation.constraints.*;

import java.io.IOException;
import java.time.Duration;

/**
 * 会话策略
 *
 * @author Alan Yeh
 * @since 2022/11/06
 */
public class SessionStrategyFilter implements StrategyFilter {
    @Control(label = "说明", type = ControlType.LABEL, defaultValue = "　　本策略用于管理会话的相关属性。")
    private String label;

    @Label("超时时间")
    @NotNull
    @Min(value = 5 * 60 * 1000L)
    @Max(value = 24 * 60 * 60 * 1000L)
    @Control(label = "超时时间", type = ControlType.NUMBER, defaultValue = "1800000", comment = "单位（毫秒）。用于控制会话的超时时间")
    private Long timeout;

    @Label("颁发者")
    @NotBlank
    @Size(min = 1, max = 50)
    @Control(label = "颁发者", defaultValue = "central-x.com", comment = "用于控制 JWT 的 issuer 属性")
    private String issuer;

    @Override
    public void execute(WebMvcRequest request, WebMvcResponse response, StrategyFilterChain chain) throws IOException, ServletException {
        request.setAttribute(SessionAttributes.TIMEOUT, Duration.ofMillis(this.timeout));
        request.setAttribute(SessionAttributes.ISSUER, this.issuer);
        chain.execute(request, response);
    }
}
