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

package central.gateway;

import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.gateway.core.filter.GlobalFilter;
import central.gateway.core.filter.StandardFilterChain;
import central.gateway.core.attribute.ExchangeAttributes;
import central.lang.Stringx;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.web.XForwardedHeaders;
import central.starter.web.reactive.extension.ServerWebExchangex;
import central.util.Objectx;
import central.starter.web.reactive.support.RemoteAddressResolver;
import central.starter.web.reactive.support.resolver.*;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 网关请求分发
 *
 * @author Alan Yeh
 * @since 2022/10/13
 */
@Slf4j
@Component
@ExtensionMethod(ServerWebExchangex.class)
public class ApplicationDispatcher implements WebHandler, HandlerMapping, Ordered {

    @Setter(onMethod_ = @Autowired)
    private List<GlobalFilter> filters;

    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    @Setter(onMethod_ = @Autowired)
    private DataContext dataContext;

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }

    /**
     * 返回当前对象作为请求处理器
     */
    @Override
    public @Nonnull Mono<Object> getHandler(@Nonnull ServerWebExchange exchange) {
        return Mono.just(this);
    }

    /**
     * 处理请求
     *
     * @param exchange the current server exchange
     * @return 用于指示当前请求是否已经完成处理
     */
    @Override
    public @Nonnull Mono<Void> handle(@Nonnull ServerWebExchange exchange) {
        var request = exchange.getRequest();
        // TODO: WebFlux 的底层 ReactorServerHttpRequest 类，只处理了 HOST、PORT，没有处理 SSL 请求，因此这里拿到的永远都是 http 协议，因此需要我们自己处理一下
        var originUri = rebuildUri(request);

        // 保存最原始的请求路径，因为后面可能会被网关插件改动，通过这个属性可以获取最原始的请求路径
        exchange.setAttribute(ExchangeAttributes.ORIGIN_URI, originUri);
        var remoteAddress = this.getRemoteAddress(exchange);
        exchange.setAttribute(ExchangeAttributes.REMOTE_ADDRESS, remoteAddress);

        exchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .uri(originUri)
                        .headers(headers -> {
                            // 将原始请求 URL 传递给后面的微服务
                            headers.set(XForwardedHeaders.ORIGIN_URI, originUri.toString());
                            headers.set(XForwardedHeaders.SCHEMA, originUri.getScheme());
                            headers.set(XForwardedHeaders.HOST, originUri.getHost());
                            headers.set(XForwardedHeaders.PORT, String.valueOf(originUri.getPort()));
                            headers.set(XForwardedHeaders.FOR, remoteAddress.getAddress().getHostAddress());
                        })
                        .remoteAddress(remoteAddress)
                        .build())
                .build();

        log.info("接收请求: '{} {}'", request.getMethod().name(), originUri);

        // 租户标识
        final var tenantCode = Objectx.getOrDefault(request.getHeaders().getFirst(XForwardedHeaders.TENANT), "master");

        try {
            // 判断是否支持当前的请求方法
            if (!this.properties.getSupportedMethods().contains(request.getMethod())) {
                throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED,
                        Stringx.format("Request method '{}' not supported, only {} are supported",
                                request.getMethod().name(),
                                properties.getSupportedMethods().stream().map(HttpMethod::name).collect(Collectors.joining("/"))));
            }

            // 如果 tenantPath 不为空，就需要截取掉 tenantPath 再转发
            var tenantPath = exchange.getRequest().getHeaders().getFirst(XForwardedHeaders.PATH);
            if (Stringx.isNotBlank(tenantPath)) {
                if (!tenantPath.startsWith("/")) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad tenant path: MUST start with '/'");
                }
                if (tenantPath.endsWith("/")) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad tenant path: must NOT end with '/'");
                }
                if (!exchange.getRequest().getPath().value().equals(tenantPath) && !exchange.getRequest().getPath().value().startsWith(tenantPath + "/")) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad tenant path: request path does not start with tenant path");
                }

                var newPath = exchange.getRequest().getPath().value().substring(tenantPath.length());
                if (Stringx.isNullOrBlank(newPath)) {
                    newPath = "/";
                }

                // 修改路径并添加请求头
                exchange = exchange.mutate().request(exchange.getRequest().mutate()
                        .path(newPath)
                        .headers(headers -> {
                            headers.set(XForwardedHeaders.TENANT, tenantCode);
                            headers.set(XForwardedHeaders.PATH, tenantPath);
                        }).build()).build();

//                var location = URI.create(UriComponentsBuilder.newInstance()
//                        .scheme(originUri.getScheme())
//                        .host(originUri.getHost())
//                        .port(originUri.getPort())
//                        .path(originUri.getPath())
//                        .build().toString());
//                exchange.getResponse().getHeaders().set(XForwardedHeaders.LOCATION, location.toString());

                log.info("租户标识: {}, 路径: {}, 路径重写: {} -> {}", tenantCode, tenantPath, originUri, exchange.getRequest().getURI());
            } else {
                // 添加请求头
                exchange = exchange.mutate().request(exchange.getRequest().mutate().headers(headers -> {
                    headers.set(XForwardedHeaders.TENANT, tenantCode);
                    headers.remove(XForwardedHeaders.PATH);
                }).build()).build();

//                URI location = URI.create(UriComponentsBuilder.newInstance()
//                        .scheme(originUri.getScheme())
//                        .host(originUri.getHost())
//                        .port(originUri.getPort())
//                        .build().toString());
//                exchange.getResponse().getHeaders().set(XForwardedHeaders.LOCATION, location.toString());
                log.info("租户标识: {}", tenantCode);
            }
        } catch (ResponseStatusException ex) {
            log.error("{}({}): {}", HttpStatus.resolve(ex.getStatusCode().value()).name(), ex.getStatusCode().value(), ex.getLocalizedMessage());
            return Mono.error(ex);
        }

        SaasContainer container = this.dataContext.getData(DataFetcherType.SAAS);
        var tenant = container.getTenantByCode(tenantCode);
        if (tenant == null) {
            log.info("租户[{}]不存在", tenantCode);
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("Invalid tenant '{}'", tenantCode)));
        }
        if (Objects.equals(Boolean.FALSE, tenant.getEnabled())) {
            log.info("租户[{}]已禁用", tenantCode);
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("Disabled tenant '{}'", tenantCode)));
        }

        log.info("匹配租户成功[code: {}, name: {}]", tenant.getCode(), tenant.getName());

        // 将租户信息放到 Attributes 中，后面的 Filter 可以通过 Attributes 获取
        exchange.setAttribute(ExchangeAttributes.TENANT, tenant);
        exchange.getRequiredAttribute(ExchangeAttributes.TOKEN).withClaim("tc", tenant.getCode());

        return StandardFilterChain.of(this.filters).filter(exchange);
    }

    private URI rebuildUri(ServerHttpRequest request) {
        String forwardedScheme = Objectx.getOrDefault(request.getHeaders().getFirst(XForwardedHeaders.SCHEMA), () -> request.getURI().getScheme());
        String forwardedHost = Objectx.getOrDefault(request.getHeaders().getFirst(XForwardedHeaders.HOST), () -> request.getURI().getHost());
        String forwardedPort = Objectx.getOrDefault(request.getHeaders().getFirst(XForwardedHeaders.PORT), () -> String.valueOf(request.getURI().getPort()));

        // 修改协议名
        return URI.create(UriComponentsBuilder.fromUri(request.getURI())
                .scheme(forwardedScheme)
                .host(forwardedHost)
                .port(forwardedPort)
                .build().toString());
    }

    private final List<RemoteAddressResolver> remoteAddressResolvers = List.of(
            new XForwardedRemoteAddressResolver(),
            new ProxyRemoteAddressResolver("Proxy-Client-IP"),
            new ProxyRemoteAddressResolver("WL-Proxy-Client-IP"),
            new ProxyRemoteAddressResolver("X-Real-IP"),
            new DefaultRemoteAddressResolver()
    );

    /**
     * 解析调用方 IP
     */
    protected InetSocketAddress getRemoteAddress(ServerWebExchange exchange) {
        for (var resolver : this.remoteAddressResolvers) {
            var remoteAddress = resolver.resolve(exchange);
            if (remoteAddress != null) {
                return remoteAddress;
            }
        }
        return null;
    }
}
