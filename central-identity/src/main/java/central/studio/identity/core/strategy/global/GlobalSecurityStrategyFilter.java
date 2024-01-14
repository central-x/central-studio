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

package central.studio.identity.core.strategy.global;

import central.studio.identity.core.strategy.GlobalStrategyFilter;
import central.studio.identity.core.strategy.StandardStrategyChain;
import central.studio.identity.core.strategy.StrategyFilterChain;
import central.studio.identity.core.strategy.StrategyContainer;
import central.starter.webmvc.servlet.WebMvcRequest;
import central.starter.webmvc.servlet.WebMvcResponse;
import central.util.Listx;
import jakarta.servlet.ServletException;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 用于处理用户定义的动态策略
 *
 * @author Alan Yeh
 * @since 2022/11/06
 */
@Component
public class GlobalSecurityStrategyFilter implements GlobalStrategyFilter {

    @Setter(onMethod_ = @Autowired)
    private StrategyContainer container;

    @Override
    public void execute(WebMvcRequest request, WebMvcResponse response, StrategyFilterChain chain) throws IOException, ServletException {
        var tenant = request.getTenantCode();

        var strategies = container.getStrategies(tenant);

        if (Listx.isNullOrEmpty(strategies)) {
            chain.execute(request, response);
        }

        // 执行用户自定义的动态策略
        new StandardStrategyChain(strategies).execute(request, response);
    }
}
