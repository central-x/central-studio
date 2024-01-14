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

package central.studio.gateway.core.filter.impl;

import central.studio.gateway.core.filter.Filter;
import central.studio.gateway.core.filter.FilterChain;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.validation.Label;
import jakarta.validation.constraints.NotBlank;
import lombok.Setter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 添加请求头
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
public class AddRequestHeaderFilter implements Filter {

    @Control(label = "说明", type = ControlType.LABEL,
            defaultValue = "　　本过滤器用于添加用户自定义的请求头到当前请求中。如果有重名的请求头不会覆盖。")
    private String label;

    @Setter
    @Label("请求头名")
    @NotBlank
    @Control(label = "请求头名")
    private String name;

    @Setter
    @Label("请求头值")
    @NotBlank
    @Control(label = "请求头值", comment = "支持模板语法")
    private String value;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, FilterChain chain) {
        var request = exchange.getRequest().mutate().header(this.name, this.value).build();
        return chain.filter(exchange.mutate().request(request).build());
    }
}
