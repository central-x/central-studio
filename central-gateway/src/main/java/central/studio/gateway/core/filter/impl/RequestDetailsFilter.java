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

import central.studio.gateway.core.attribute.ExchangeAttributes;
import central.studio.gateway.core.filter.Filter;
import central.studio.gateway.core.filter.FilterChain;
import central.lang.Attribute;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.starter.web.reactive.extension.ServerWebExchangex;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * 请求详情
 *
 * @author Alan Yeh
 * @since 2022/11/12
 */
@Slf4j
@ExtensionMethod(ServerWebExchangex.class)
public class RequestDetailsFilter implements Filter {
    private static final Attribute<Long> BEGIN_TIME = Attribute.of(RequestDetailsFilter.class.getSimpleName() + ".begin_time");

    @Control(label = "说明", type = ControlType.LABEL,
            defaultValue = "　　本过滤器用于输出请求的详细信息到日志中，方便开发过程中调试。")
    private String label;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, FilterChain chain) {
        exchange.setAttribute(BEGIN_TIME, System.currentTimeMillis());

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            var responseTime = System.currentTimeMillis();
            var accessTime = exchange.getRequiredAttribute(BEGIN_TIME);

            var originUri = exchange.getRequiredAttribute(ExchangeAttributes.ORIGIN_URI);
            var forwardingUri = exchange.getAttribute(ExchangeAttributes.FORWARDING_URI);

            var builder = new StringBuilder("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            builder.append("┣ Request:\n");
            builder.append("┣ Method: ").append(exchange.getRequest().getMethod().name()).append("\n");
            builder.append("┣ Access URL: ").append(originUri).append("\n");
            if (forwardingUri != null) {
                builder.append("┣ Forwarding URL: ").append(forwardingUri).append("\n");
            }
            builder.append("┣ Headers: (").append(exchange.getRequest().getHeaders().size()).append(")\n");
            for (Map.Entry<String, List<String>> header : exchange.getRequest().getHeaders().entrySet()) {
                for (String value : header.getValue()) {
                    builder.append("┣ - ").append(header.getKey()).append(": ").append(value).append("\n");
                }
            }

            builder.append("┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            builder.append("┣ Response:\n");
            builder.append("┣ Cost: ").append(responseTime - accessTime).append("ms\n");

            var status = exchange.getResponse().getStatusCode();
            if (status == null) {
                builder.append("┣ Status: 0 (Unknown)\n");
            } else {
                var httpStatus = HttpStatus.resolve(status.value());
                if (httpStatus != null) {
                    builder.append("┣ Status: ").append(httpStatus.value()).append(" (").append(httpStatus.value()).append(")\n");
                } else {
                    builder.append("┣ Status: ").append(status.value()).append(" (Unknown)\n");
                }
            }

            builder.append("┣ Headers: (").append(exchange.getResponse().getHeaders().size()).append(")\n");

            for (Map.Entry<String, List<String>> header : exchange.getResponse().getHeaders().entrySet()) {
                for (String value : header.getValue()) {
                    builder.append("┣ - ").append(header.getKey()).append(": ").append(value).append("\n");
                }
            }
            builder.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            log.info(builder.toString());
        }));
    }
}
