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

import central.studio.gateway.core.filter.Filter;
import central.studio.gateway.core.filter.FilterChain;
import central.studio.gateway.core.filter.GlobalFilter;
import central.studio.gateway.core.filter.StandardFilterChain;
import central.studio.gateway.core.filter.global.routing.HttpRoutingFilter;
import central.studio.gateway.core.filter.global.routing.NotSupportedProtocolRoutingFilter;
import central.studio.gateway.core.filter.global.routing.ResourceRoutingFilter;
import central.studio.gateway.core.filter.global.routing.WebSocketRoutingFilter;
import central.pluglet.PlugletFactory;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * 请求路由
 *
 * @author Alan Yeh
 * @since 2022/10/13
 */
@Component
@Order
public class RequestRoutingFilter implements GlobalFilter, ApplicationContextAware, InitializingBean {

    @Setter
    private ApplicationContext applicationContext;

    private final List<Filter> filters = new ArrayList<>(5);

    @Override
    public void afterPropertiesSet() throws Exception {
        var factory = new PlugletFactory();
        this.filters.add(factory.create(WebSocketRoutingFilter.class, null));
        this.filters.add(factory.create(HttpRoutingFilter.class, null));
        this.filters.add(factory.create(ResourceRoutingFilter.class, null));
        this.filters.add(factory.create(NotSupportedProtocolRoutingFilter.class, null));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, FilterChain chain) {
        return StandardFilterChain.of(this.filters).filter(exchange);
    }
}
