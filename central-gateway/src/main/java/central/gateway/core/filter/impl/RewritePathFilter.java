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
import jakarta.validation.constraints.Size;
import lombok.Setter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * 重写路径
 *
 * @author Alan Yeh
 * @since 2022/11/12
 */
public class RewritePathFilter implements Filter {

    @Control(label = "说明", type = ControlType.LABEL,
            defaultValue = """
                    　　本过滤器用于重写当前请求的 URL 路径。重写机制使用 <code>String::replaceAll</code> 方法实现。
                    重写路径后，会影响后续的网关插件的工作。<br/>
                    　　例：<br/>
                    <code>regexp = /test/api/accounts</code><br/>
                    <code>replacement = /oa/$1</code><br/>
                    则最后替换的结果为 <code>/oa/api/accounts</code>
                    """)
    private String label;

    @Setter
    @Label("Regexp")
    @NotBlank
    @Size(min = 1, max = 1024)
    @Control(label = "Regexp", comment = "正则表达式，用于匹配路径")
    private String regexp;

    @Setter()
    @Label("Replacement")
    @NotBlank
    @Size(min = 1, max = 1024)
    private String replacement;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, FilterChain chain) {
        var newPath = exchange.getRequest().getURI().getRawPath().replaceAll(this.regexp, replacement);

        var uri = UriComponentsBuilder.fromUri(exchange.getRequest().getURI())
                .replacePath(newPath)
                .build().toString();

        return chain.filter(exchange.mutate().request(exchange.getRequest().mutate().uri(URI.create(uri)).build()).build());
    }
}
