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

package central.gateway.core;

import lombok.Getter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 标准的网关过滤器调用链
 *
 * @author Alan Yeh
 * @since 2022/10/13
 */
public class StandardGatewayFilterChain implements GatewayFilterChain {

    private final int index;

    @Getter
    private final List<? extends GatewayFilter> filters;

    public StandardGatewayFilterChain(List<? extends GatewayFilter> filters) {
        this.filters = filters;
        this.index = 0;
    }

    public StandardGatewayFilterChain(StandardGatewayFilterChain parent, int index) {
        this.filters = parent.getFilters();
        this.index = index;
    }

    public static StandardGatewayFilterChain of(List<? extends GatewayFilter> filters) {
        return new StandardGatewayFilterChain(filters);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange) {
        return Mono.defer(() -> {
            if (this.index < filters.size()) {
                // 还有下一个
                var filter = this.filters.get(this.index);
                var chain = new StandardGatewayFilterChain(this, this.index + 1);
                if (filter.predicate(exchange)) {
                    // 断言成功，则执行过滤器
                    return filter.filter(exchange, chain);
                } else {
                    // 断言失败，直接执行下一个过滤器
                    return chain.filter(exchange);
                }
            } else {
                // 完成所有过滤器
                return Mono.empty();
            }
        });
    }
}
