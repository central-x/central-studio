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

import central.gateway.core.attribute.ExchangeAttributes;
import central.gateway.core.filter.Filter;
import central.gateway.core.filter.FilterChain;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.starter.web.reactive.extension.ServerWebExchangex;
import central.validation.Label;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 设置代理请求的超时时间
 *
 * @author Alan Yeh
 * @since 2022/11/12
 */
@ExtensionMethod(ServerWebExchangex.class)
public class TimeoutFilter implements Filter {

    @Control(label = "说明", type = ControlType.LABEL, required = false,
            defaultValue = "　　本过滤器用于设置网关转发到目标服务器的<code>超时时间</code>。")
    private String label;

    @Setter
    @Label("超时时间")
    @NotNull
    @Min(1000)
    @Max(60000)
    @Control(label = "超时时间", type = ControlType.NUMBER, defaultValue = "30000", comment = "单位毫秒")
    private Integer timeout;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, FilterChain chain) {
        exchange.setAttribute(ExchangeAttributes.TIMEOUT, this.timeout);
        return chain.filter(exchange);
    }
}
