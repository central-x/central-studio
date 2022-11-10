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

package central.gateway.core.filter.global;

import central.gateway.core.filter.FilterChain;
import central.gateway.core.filter.GlobalFilter;
import central.gateway.core.attribute.ExchangeAttributes;
import central.starter.web.reactive.extension.ServerWebExchangex;
import lombok.experimental.ExtensionMethod;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 写响应过滤器
 *
 * @author Alan Yeh
 * @since 2022/10/13
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ExtensionMethod(ServerWebExchangex.class)
public class ResponseWritingFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, FilterChain chain) {
        return chain.filter(exchange)
                .doOnError(throwable -> cleanup(exchange))
                .then(Mono.defer(() -> {
                    var body = exchange.getRequiredAttribute(ExchangeAttributes.RESPONSE_BODY);

                    var response = exchange.getResponse();

                    if (isStreamingMediaType(response.getHeaders().getContentType())) {
                        return response.writeAndFlushWith(Flux.just(body.get(response.bufferFactory())));
                    } else {
                        return response.writeWith(body.get(response.bufferFactory()));
                    }
                }))
                .doOnCancel(() -> cleanup(exchange));
    }

    private final List<MediaType> streamingMediaTypes = List.of(MediaType.TEXT_EVENT_STREAM);

    private boolean isStreamingMediaType(@Nullable MediaType contentType) {
        if (contentType != null) {
            for (var streamingMediaType : streamingMediaTypes) {
                if (streamingMediaType.isCompatibleWith(contentType)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void cleanup(ServerWebExchange exchange) {
        exchange.getRequiredAttribute(ExchangeAttributes.RESPONSE_BODY).dispose();
    }
}
