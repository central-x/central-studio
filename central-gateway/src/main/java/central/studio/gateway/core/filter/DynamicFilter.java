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

package central.studio.gateway.core.filter;

import central.data.gateway.GatewayFilter;
import central.studio.gateway.core.filter.predicate.Predicate;
import central.studio.gateway.core.filter.predicate.PredicateType;
import central.studio.gateway.core.filter.predicate.impl.PathPredicate;
import central.lang.Assertx;
import central.lang.Stringx;
import central.lang.reflect.TypeRef;
import central.pluglet.PlugletFactory;
import central.util.Jsonx;
import lombok.Getter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 动态过滤器
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
public class DynamicFilter implements Filter, Ordered, DisposableBean {

    @Getter
    private final GatewayFilter data;

    @Override
    public int getOrder() {
        return this.data.getOrder();
    }

    private final List<Predicate> predicates = new ArrayList<>();

    private final Filter delegate;

    private final PlugletFactory factory;

    public DynamicFilter(GatewayFilter data, PlugletFactory factory) {
        this.data = data;
        this.factory = factory;

        {
            // 初始化断言
            // 1. 初始化路径断言
            this.predicates.add(factory.create(PathPredicate.class, Map.of("path", data.getPath())));

            // 2. 初始化其它断言
            for (var predicate : data.getPredicates()) {
                var type = Assertx.requireNotNull(PredicateType.resolve(predicate.getType()), "找不到指定的断言类型: " + predicate.getType());
                var params = Jsonx.Default().deserialize(predicate.getParams(), TypeRef.ofMap(String.class, Object.class));
                try {
                    var instance = factory.create(type.getType(), params);
                    this.predicates.add(instance);
                } catch (Exception ex) {
                    throw new IllegalStateException(Stringx.format("初始化断言插件[type={}]异常: {}", type.getName(), ex.getLocalizedMessage()), ex);
                }
            }
        }

        {
            // 初始化过滤器
            var type = Assertx.requireNotNull(FilterType.resolve(data.getType()), "找不到指定的过滤器类型: " + data.getType());
            var params = Jsonx.Default().deserialize(data.getParams(), TypeRef.ofMap(String.class, Object.class));

            try {
                this.delegate = factory.create(type.getType(), params);
            } catch (Exception ex) {
                throw new IllegalStateException(Stringx.format("初始化过滤器插件[type={}]异常: {}", type.getName(), ex.getLocalizedMessage()), ex);
            }
        }
    }

    @Override
    public boolean predicate(ServerWebExchange exchange) {
        return this.predicates.stream().allMatch(it -> it.predicate(exchange));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, FilterChain chain) {
        return this.delegate.filter(exchange, chain);
    }

    @Override
    public void destroy() throws Exception {
        for (var predicate : this.predicates) {
            factory.destroy(predicate);
        }
        factory.destroy(this.delegate);
    }
}
