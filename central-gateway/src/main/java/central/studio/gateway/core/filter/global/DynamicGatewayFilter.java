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

package central.studio.gateway.core.filter.global;

import central.starter.web.reactive.extension.ServerWebExchangex;
import central.studio.gateway.core.attribute.ExchangeAttributes;
import central.studio.gateway.core.filter.*;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Comparator;

/**
 * 用户自定义网关
 *
 * @author Alan Yeh
 * @since 2022/10/13
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 3)
@ExtensionMethod(ServerWebExchangex.class)
public class DynamicGatewayFilter implements GlobalFilter {

    @Setter(onMethod_ = @Autowired)
    private Container container;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, FilterChain chain) {
        var tenant = exchange.getRequiredAttribute(ExchangeAttributes.TENANT);

        var filters = container.getFilters(tenant.getCode()).stream()
                .sorted(Comparator.comparing(DynamicFilter::getOrder).reversed())
                .toList();

        // 执行用户定义的过滤器
        return StandardFilterChain.of(filters).filter(exchange)
                .then(chain.filter(exchange));
    }
}
