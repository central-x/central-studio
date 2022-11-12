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

package central.gateway.core.filter.impl;

import central.gateway.core.filter.Filter;
import central.gateway.core.filter.FilterChain;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.validation.Label;
import jakarta.validation.constraints.NotBlank;
import lombok.Setter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;

/**
 * 移除请求参数
 *
 * @author Alan Yeh
 * @since 2022/11/12
 */
public class RemoveRequestParameterFilter implements Filter {

    @Control(label = "说明", type = ControlType.LABEL,
            defaultValue = "　　本过滤器用于移除用户指定的请求参数。移除请求参数后，可能会影响后续网关插件的断言结果。")
    private String label;

    @Setter
    @Label("参数名")
    @NotBlank
    @Control(label = "参数名", comment = "待移除的参数名，通过 String::match 进行匹配")
    private String regexp;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, FilterChain chain) {
        var components = UriComponentsBuilder.fromUri(exchange.getRequest().getURI()).build();

        var names = new ArrayList<String>();
        for (var name : components.getQueryParams().keySet()) {
            if (name.matches(this.regexp)) {
                names.add(name);
            }
        }

        if (names.isEmpty()) {
            return chain.filter(exchange);
        } else {
            var builder = UriComponentsBuilder.fromUri(exchange.getRequest().getURI());

            names.forEach(builder::replaceQuery);

            return chain.filter(exchange.mutate().request(exchange.getRequest().mutate().uri(URI.create(builder.build().toString())).build()).build());
        }
    }
}
