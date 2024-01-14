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

package central.studio.gateway.core.filter.global.routing;

import central.data.saas.Application;
import central.studio.gateway.core.attribute.ExchangeAttributes;
import central.studio.gateway.core.filter.Filter;
import central.studio.gateway.core.filter.FilterChain;
import central.starter.web.reactive.extension.ServerWebExchangex;
import central.util.Listx;
import central.web.XForwardedHeaders;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNetty2WebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * WebSocket 转发
 *
 * @author Alan Yeh
 * @since 2022/10/13
 */
@Slf4j
@ExtensionMethod(ServerWebExchangex.class)
public class WebSocketRoutingFilter implements Filter, InitializingBean {
    /**
     * Sec-Websocket protocol.
     */
    public static final String SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";

    private WebSocketClient client;

    private WebSocketService service;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.client = new ReactorNetty2WebSocketClient();
        this.service = new HandshakeWebSocketService();
    }

    @Override
    public boolean predicate(ServerWebExchange exchange) {
        // 只处理 WebSocket 请求
        return "WebSocket".equalsIgnoreCase(exchange.getRequest().getHeaders().getUpgrade());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, FilterChain chain) {
        URI targetServer = exchange.getRequiredAttribute(ExchangeAttributes.TARGET_SERVER);
        final Application targetApplication = exchange.getAttribute(ExchangeAttributes.TARGET_APPLICATION);

        // 构建请求路径
        var targetUri = URI.create(UriComponentsBuilder.fromUri(exchange.getRequest().getURI())
                .scheme(convertToWebSocketScheme(targetServer.getScheme()))
                .host(targetServer.getHost())
                .port(targetServer.getPort()).build().toString());

        exchange.setAttribute(ExchangeAttributes.FORWARDING_URI, targetUri);
        var headers = getFilteredHeaders(exchange);

        if (targetApplication != null) {
            // 已注册的应用系统需要生成 token 传递过去
            // 该应用系统在接收到请求后，需验证该 token，用于防止其它系统非法调用
            var token = exchange.getRequiredAttribute(ExchangeAttributes.TOKEN).sign(Algorithm.HMAC256(targetApplication.getSecret()));
            headers.add(XForwardedHeaders.TOKEN, token);
        }

        List<String> protocols = Listx.asStream(exchange.getRequest().getHeaders().get(SEC_WEBSOCKET_PROTOCOL))
                .flatMap(header -> Arrays.stream(StringUtils.commaDelimitedListToStringArray(header)))
                .map(String::trim).toList();
        log.info("WebSocket 转发: {}", targetUri);
        return this.service.handleRequest(exchange, new ProxyWebSocketHandler(this.client, targetUri, headers, protocols));
    }

    private String convertToWebSocketScheme(String scheme) {
        scheme = scheme.toLowerCase();
        return switch (scheme) {
            case "http" -> "ws";
            case "https" -> "wss";
            default -> scheme;
        };
    }

    /**
     * 移除 WebSocket 相关的请求头 因为代理本身会生成这些请求头，如果不移除，则会生成重复的请求头
     */
    private HttpHeaders getFilteredHeaders(ServerWebExchange exchange) {
        var headers = new HttpHeaders();
        exchange.getRequest().getHeaders().entrySet().stream()
                .filter(entry -> !entry.getKey().toLowerCase().startsWith("sec-websocket"))
                .forEach(header -> headers.addAll(header.getKey(), header.getValue()));
        return headers;
    }

    private static class ProxyWebSocketHandler implements WebSocketHandler {
        private final WebSocketClient client;

        private final URI uri;

        private final HttpHeaders headers;

        @Getter
        private final List<String> subProtocols;

        public ProxyWebSocketHandler(WebSocketClient client, URI uri, HttpHeaders headers, List<String> protocols) {
            this.client = client;
            this.uri = uri;
            this.headers = headers;
            this.subProtocols = protocols;
        }

        @Override
        public @Nonnull Mono<Void> handle(@Nonnull WebSocketSession session) {
            return this.client.execute(this.uri, this.headers, new WebSocketHandler() {
                @Override
                public @Nonnull Mono<Void> handle(@Nonnull WebSocketSession session) {
                    // Use retain() for Reactor Netty
                    Mono<Void> proxySessionSend = session
                            .send(session.receive().doOnNext(WebSocketMessage::retain));
                    // .log("proxySessionSend", Level.FINE);
                    Mono<Void> serverSessionSend = session.send(
                            session.receive().doOnNext(WebSocketMessage::retain));
                    // .log("sessionSend", Level.FINE);
                    return Mono.zip(proxySessionSend, serverSessionSend).then();
                }

                @Override
                public @Nonnull List<String> getSubProtocols() {
                    return ProxyWebSocketHandler.this.getSubProtocols();
                }
            });
        }
    }
}
