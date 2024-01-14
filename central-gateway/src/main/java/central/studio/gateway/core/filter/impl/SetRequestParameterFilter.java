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
import central.lang.Stringx;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.validation.Label;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Setter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * 设置请求参数
 *
 * @author Alan Yeh
 * @since 2022/11/12
 */
public class SetRequestParameterFilter implements Filter {

    @Control(label = "说明", type = ControlType.LABEL,
            defaultValue = "　　本过滤器用于设置用户自定义的 URL 参数到当前请求中。如果有重名的参数，将会覆盖原有的参数。")
    private String label;

    @Setter
    @Label("参数名")
    @NotBlank
    @Size(min = 1, max = 50)
    @Control(label = "参数名")
    private String name;

    @Setter
    @Label("参数值")
    @NotBlank
    @Size(min = 1, max = 4096)
    @Control(label = "参数值")
    private String value;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, FilterChain chain) {
        var uri = UriComponentsBuilder.fromUri(exchange.getRequest().getURI())
                .replaceQueryParam(Stringx.encodeUrl(this.name), Stringx.encodeUrl(this.value))
                .build().toString();

        return chain.filter(exchange.mutate().request(exchange.getRequest().mutate().uri(URI.create(uri)).build()).build());
    }
}
