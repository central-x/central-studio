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
import central.lang.Arrayx;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.stream.Collectors;

/**
 * 移除路径前缀
 *
 * @author Alan Yeh
 * @since 2022/11/12
 */
public class StripPrefixFilter implements Filter {

    @Control(label = "说明", type = ControlType.LABEL,
            defaultValue = "　　本过滤器用于移除当前请求的 URL 的路径前缀。移除路径后，会影响后续的网关断言结果。")
    private String label;

    @Setter
    @NotNull(message = "parts 必须不为空")
    @Min(value = 1, message = "parts 必须大于0")
    @Control(label = "parts", type = ControlType.NUMBER, defaultValue = "1", comment = "移除前缀的前几部份")
    private Integer parts;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, FilterChain chain) {
        var newPath = Arrayx.asStream(StringUtils.tokenizeToStringArray(exchange.getRequest().getURI().getRawPath(), "/"))
                .skip(this.parts).collect(Collectors.joining("/"));
        if (exchange.getRequest().getURI().getRawPath().endsWith("/")) {
            newPath += "/";
        }

        var uri = UriComponentsBuilder.fromUri(exchange.getRequest().getURI())
                .replacePath(newPath)
                .build().toString();

        return chain.filter(exchange.mutate().request(exchange.getRequest().mutate().uri(URI.create(uri)).build()).build());
    }
}
