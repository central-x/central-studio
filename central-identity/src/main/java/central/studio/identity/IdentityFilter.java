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

package central.studio.identity;

import central.starter.webmvc.servlet.WebMvcRequest;
import central.starter.webmvc.servlet.WebMvcResponse;
import central.studio.identity.core.strategy.GlobalStrategyFilter;
import central.studio.identity.core.strategy.StandardStrategyChain;
import central.studio.identity.core.strategy.StrategyFilter;
import jakarta.servlet.*;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用过滤器
 * <p>
 * 在此过滤器中注入安全策略
 *
 * @author Alan Yeh
 * @since 2022/10/19
 */
@Component
public class IdentityFilter implements Filter {

    /**
     * 全局安全策略
     */
    @Setter(onMethod_ = @Autowired)
    private List<GlobalStrategyFilter> strategies;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof WebMvcRequest request && servletResponse instanceof WebMvcResponse response) {
            // 执行认证中心过滤器
            var strategies = new ArrayList<StrategyFilter>(this.strategies);

            new StandardStrategyChain(strategies).complete(filterChain::doFilter).execute(request, response);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
