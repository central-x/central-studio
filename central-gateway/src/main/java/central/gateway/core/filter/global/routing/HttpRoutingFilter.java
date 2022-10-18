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

package central.gateway.core.filter.global.routing;

import central.data.ten.Application;
import central.gateway.core.GatewayFilter;
import central.gateway.core.GatewayFilterChain;
import central.gateway.core.attribute.ExchangeAttributes;
import central.gateway.core.body.ConnectionBody;
import central.starter.web.http.XForwardedHeaders;
import central.starter.web.reactive.extension.ServerWebExchangex;
import com.auth0.jwt.algorithms.Algorithm;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelOption;
import io.netty.handler.codec.http.HttpMethod;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.NettyDataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.net.URI;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * 发送 http、https 请求
 *
 * @author Alan Yeh
 * @since 2022/10/13
 */
@Slf4j
@ExtensionMethod(ServerWebExchangex.class)
public class HttpRoutingFilter implements GatewayFilter, InitializingBean, EnvironmentAware {

    @Setter
    private Environment environment;

    /**
     * 支持的协议
     */
    private final Set<String> supportedSchemes = Set.of("http", "https");

    private HttpRoutingProperties properties = new HttpRoutingProperties();

    private HttpClient client;

    @Override
    public void afterPropertiesSet() throws Exception {
        var binder = Binder.get(this.environment).bind(HttpRoutingProperties.class.getAnnotation(ConfigurationProperties.class).prefix(), HttpRoutingProperties.class);
        if (binder.isBound()) {
            this.properties = binder.get();
        } else {
            this.properties = new HttpRoutingProperties();
        }

        var provider = ConnectionProvider.builder("http-routing")
                // 最大连接数
                .maxConnections(this.properties.getMaxConnections())
                // 超过连接数之后的队列大小
                .pendingAcquireMaxCount(this.properties.getPendingAcquireMaxCount())
                // 在队列的最大等待时间
                .pendingAcquireTimeout(Duration.ofMillis(this.properties.getPendingAcquireTimeout()))
                .evictInBackground(Duration.ZERO)
                .metrics(false)
                .build();

        this.client = HttpClient.create(provider).protocol(HttpProtocol.HTTP11, HttpProtocol.H2, HttpProtocol.H2C);
    }

    @Override
    public boolean predicate(ServerWebExchange exchange) {
        URI targetServer = exchange.getRequiredAttribute(ExchangeAttributes.TARGET_SERVER);

        return this.supportedSchemes.contains(targetServer.getScheme().toLowerCase());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI targetServer = exchange.getRequiredAttribute(ExchangeAttributes.TARGET_SERVER);
        final Application targetApplication = exchange.getAttribute(ExchangeAttributes.TARGET_APPLICATION);
        final HttpHeaders headers = new HttpHeaders(new LinkedMultiValueMap<>(exchange.getRequest().getHeaders()));

        exchange.setAttribute(ExchangeAttributes.FORWARDING_URI, targetServer);

        if (targetApplication != null) {
            // 已注册的应用系统需要生成 token 传递过去
            // 该应用系统在接收到请求后，需验证该 token，用于防止其它系统非法调用
            var token = exchange.getRequiredAttribute(ExchangeAttributes.TOKEN)
                    .sign(Algorithm.HMAC256(targetApplication.getKey()));
            headers.add(XForwardedHeaders.TOKEN, token);
        }

        var provider = ConnectionProvider.builder("http-routing")
                // 最大连接数
                .maxConnections(this.properties.getMaxConnections())
                // 超过连接数之后的队列大小
                .pendingAcquireMaxCount(this.properties.getPendingAcquireMaxCount())
                // 在队列的最大等待时间
                .pendingAcquireTimeout(Duration.ofMillis(this.properties.getPendingAcquireTimeout()))
                .evictInBackground(Duration.ZERO)
                .metrics(false)
                .build();

        this.client = HttpClient.create(provider).protocol(HttpProtocol.HTTP11);

        // 设置超时时间
        int timeout = exchange.getAttributeOrDefault(ExchangeAttributes.TIMEOUT, this.properties.getTimeout());
        var responseFlux = this.client
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
                // 转发请求头
                .headers(it -> {
                    it.remove(HttpHeaders.HOST);
                    headers.forEach(it::add);
                })
                .responseTimeout(Duration.ofMillis(timeout))
                .request(HttpMethod.valueOf(exchange.getRequest().getMethod().name()))
                .uri(targetServer)
                .send((req, nettOutbound) -> {
                    return nettOutbound.send(exchange.getRequest().getBody().map(this::getByteBuf));
//                    return nettOutbound.withConnection(connection -> )
                }).responseConnection((res, connection) -> {
                    exchange.setAttribute(ExchangeAttributes.RESPONSE, res);
                    exchange.setAttribute(ExchangeAttributes.RESPONSE_BODY, new ConnectionBody(connection));

                    exchange.getResponse().setRawStatusCode(res.status().code());
                    res.responseHeaders().forEach(entry -> exchange.getResponse().getHeaders().add(entry.getKey(), entry.getValue()));

                    return Mono.just(res);
                });

        return responseFlux
                .timeout(Duration.ofMillis(timeout), Mono.error(new TimeoutException("Response timeout: " + timeout)))
                .onErrorMap(TimeoutException.class, th -> new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, th.getMessage(), th))
                .then(Mono.empty());


//        var provider = ConnectionProvider.builder("http-routing")
//                // 最大连接数
//                .maxConnections(this.properties.getMaxConnections())
//                // 超过连接数之后的队列大小
//                .pendingAcquireMaxCount(this.properties.getPendingAcquireMaxCount())
//                // 在队列的最大等待时间
//                .pendingAcquireTimeout(Duration.ofMillis(this.properties.getPendingAcquireTimeout())).build();
//        var client = HttpClient.create(provider).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout).responseTimeout(Duration.ofMillis(timeout));
//
//        var spec = WebClient.builder()
//                .clientConnector(new ReactorClientHttpConnector(client)).build()
//                .method(exchange.getRequest().getMethod())
//                .uri(targetServer)
//                // 转发请求头
//                .headers(it -> it.addAll(headers))
//                // 根据 RFC2616 规范，GET、DELETE 不应该有 Body。但是根据 RFC7230 规范，又放松了这个限制，允许 GET 请求里携带请求体。
//                // Postman 在新版里也支持在 GET 请求里携带 Body，因此网关这里做了兼容，无论任何请求都转发 Body。
//                .body(BodyInserters.fromDataBuffers(exchange.getRequest().getBody()));
//
//        log.info("请求转发: '{} {}'", exchange.getRequest().getMethod().name(), targetServer);
//
//        return spec.exchangeToMono(Mono::just).flatMap(resp -> {
//            // 生成请求响应
//            var response = exchange.getResponse();
//            // 移除被请求方的 X-Forwarded-Token 响应头
//            var respHeaders = new HttpHeaders(new LinkedMultiValueMap<>(resp.headers().asHttpHeaders()));
//            respHeaders.remove(XForwardedHeaders.TOKEN);
//            response.getHeaders().putAll(respHeaders);
//
//            response.setStatusCode(resp.statusCode());
//
//            exchange.getAttributes().put(ExchangeAttributes.RESPONSE.getCode(), resp);
//            return Mono.empty();
//        }).onErrorResume(it -> {
//            if (it instanceof ConnectException ex) {
//                // 连接异常
//                return Mono.error(new ResponseStatusException(HttpStatus.BAD_GATEWAY, Stringx.format("连接服务器失败({}): {}", it.getLocalizedMessage(), targetServer), ex));
//            } else if (it instanceof ReadTimeoutException ex) {
//                // 读超时
//                return Mono.error(new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, Stringx.format("响应超时(Read Timeout): {}", targetServer), ex));
//            } else if (it instanceof WriteTimeoutException ex) {
//                // 写超时
//                return Mono.error(new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, Stringx.format("响应超时(Write Timeout): {}", targetServer), ex));
//            } else if (it instanceof TimeoutException ex) {
//                // 其它超时
//                return Mono.error(new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, Stringx.format("服务器超时(Timeout): {}", targetServer), ex));
//            } else {
//                // 其它异常
//                return Mono.error(it);
//            }
//        }).then();
    }

    protected ByteBuf getByteBuf(DataBuffer dataBuffer) {
        if (dataBuffer instanceof NettyDataBuffer buffer) {
            return buffer.getNativeBuffer();
        }
        // MockServerHttpResponse creates these
        else if (dataBuffer instanceof DefaultDataBuffer buffer) {
            return Unpooled.wrappedBuffer(buffer.getNativeBuffer());
        }
        throw new IllegalArgumentException("Unable to handle DataBuffer of type " + dataBuffer.getClass());
    }

    @Data
    @ConfigurationProperties(prefix = "studio.gateway.filter.http-routing")
    private static class HttpRoutingProperties {
        /**
         * 全局超时时间(ms)
         */
        private int timeout = 60000;
        /**
         * 最大连接数
         */
        private int maxConnections = Integer.MAX_VALUE;
        /**
         * 超过最大连接数之后，进入队列的请求最大限制
         * -1 为不设上限（容易爆内存）
         */
        private int pendingAcquireMaxCount = -1;
        /**
         * 请求进入队列后的最大等待时间（ms）
         */
        private int pendingAcquireTimeout = 60000;
    }
}
