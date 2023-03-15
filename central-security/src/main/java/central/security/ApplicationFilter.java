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

package central.security;

import central.security.core.strategy.GlobalStrategy;
import central.security.core.strategy.StandardStrategyChain;
import central.starter.webmvc.servlet.WebMvcRequest;
import central.starter.webmvc.servlet.WebMvcResponse;
import jakarta.servlet.*;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
public class ApplicationFilter implements Filter {

    /**
     * 全局安全策略
     */
    @Setter(onMethod_ = @Autowired)
    private List<GlobalStrategy> strategies;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof WebMvcRequest request && servletResponse instanceof WebMvcResponse response) {
            new StandardStrategyChain(strategies)
                    .addStrategy((req, resp, chain) -> {
                        filterChain.doFilter(req, resp);
                    }).execute(request, response);
        }
    }
}
