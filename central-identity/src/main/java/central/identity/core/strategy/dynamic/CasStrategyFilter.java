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

package central.identity.core.strategy.dynamic;

import central.lang.BooleanEnum;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.identity.controller.sso.cas.support.Scope;
import central.identity.core.attribute.CasAttributes;
import central.identity.core.strategy.StrategyFilter;
import central.identity.core.strategy.StrategyFilterChain;
import central.starter.webmvc.servlet.WebMvcRequest;
import central.starter.webmvc.servlet.WebMvcResponse;
import central.validation.Label;
import jakarta.servlet.ServletException;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

/**
 * CAS 策略
 *
 * @author Alan Yeh
 * @since 2022/11/06
 */
public class CasStrategyFilter implements StrategyFilter {

    @Control(label = "说明", type = ControlType.LABEL,
            defaultValue = "　　本策略用于控制中央认证服务（CAS）运行策略。统一认证的相关规则。")
    private String label;

    @Label("启用")
    @NotNull
    @Control(label = "启用", type = ControlType.RADIO, defaultValue = "0",
            comment = "用于控制是否启用中央认证服务（CAS）。如果不启用，则所有相关的接口将会被禁用。")
    @Setter
    private BooleanEnum enabled;

    @Label("授权范围")
    @NotEmpty
    @Control(label = "授权范围", type = ControlType.CHECKBOX, defaultValue = "user:basic",
            comment = "用于控制第三方业务系统可以获取的用户属性范围。")
    @Setter
    private List<Scope> scopes;

    @Label("单点退出")
    @NotNull
    @Control(label = "单点退出", type = ControlType.RADIO, defaultValue = "0",
            comment = "用于控制是否启用单点退出（Single Logout）功能。启用后，如果一个应用退出登录，那么该会话关联的所有应用都会一起自动退出（需业务系统支持）。")
    @Setter
    private BooleanEnum singleLogout;

    @Override
    public void execute(WebMvcRequest request, WebMvcResponse response, StrategyFilterChain chain) throws IOException, ServletException {
        request.setAttribute(CasAttributes.ENABLED, this.enabled.getJValue());
        request.setAttribute(CasAttributes.SCOPES, new HashSet<>(this.scopes));
        request.setAttribute(CasAttributes.SINGLE_LOGOUT_ENABLED, this.singleLogout.getJValue());

        chain.execute(request, response);
    }
}
