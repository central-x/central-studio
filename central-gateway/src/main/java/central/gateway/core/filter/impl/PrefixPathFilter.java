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
 * 添加请求路径前缀
 *
 * @author Alan Yeh
 * @since 2022/11/12
 */
public class PrefixPathFilter implements Filter {

    @Control(label = "说明", type = ControlType.LABEL,
            defaultValue = "　　本过滤器用于添加用户自定义的路径前缀到当前请求的 URL。添加路径后，会影响后续的网关插件的工作。")
    private String label;

    @Setter
    @Label("前缀")
    @NotBlank
    @Size(min = 1, max = 1024)
    @Control(label = "前缀", comment = "该前缀将会被添加到目标的 URL 上。")
    private String prefix;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, FilterChain chain) {
        var uri = UriComponentsBuilder.fromUri(exchange.getRequest().getURI())
                .replacePath(Stringx.addPrefix(prefix, "/"))
                .path(exchange.getRequest().getURI().getRawPath())
                .build().toString();

        return chain.filter(exchange.mutate().request(exchange.getRequest().mutate().uri(URI.create(uri)).build()).build());
    }
}
