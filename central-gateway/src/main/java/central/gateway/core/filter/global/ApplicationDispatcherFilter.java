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

import central.bean.Orderable;
import central.data.ten.ApplicationModule;
import central.data.ten.Tenant;
import central.gateway.core.GatewayFilterChain;
import central.gateway.core.GlobalGatewayFilter;
import central.gateway.core.attribute.ExchangeAttributes;
import central.lang.Stringx;
import central.starter.web.reactive.extension.ServerWebExchangex;
import central.starter.web.reactive.render.RedirectRender;
import central.util.Listx;
import com.auth0.jwt.RegisteredClaims;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;

/**
 * 网关分发
 *
 * @author Alan Yeh
 * @since 2022/10/13
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@ExtensionMethod(ServerWebExchangex.class)
public class ApplicationDispatcherFilter implements GlobalGatewayFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Tenant tenant = exchange.getRequiredAttribute(ExchangeAttributes.TENANT);

        var application = tenant.getApplications().stream()
                // 过滤掉已禁用的
                .filter(it -> Objects.equals(Boolean.TRUE, it.getEnabled()) && Objects.equals(Boolean.TRUE, it.getApplication().getEnabled()))
                // 确认匹配度
                .map(it -> {
                    var path = exchange.getRequest().getPath().value();
                    if (path.equals(it.getApplication().getContextPath()) || path.equals(Stringx.addSuffix(it.getApplication().getContextPath(), "/"))) {
                        // 全匹配，匹配度最高
                        return Orderable.Holder.of(it, Integer.MAX_VALUE);
                    } else if (path.startsWith(Stringx.addSuffix(it.getApplication().getContextPath(), "/"))) {
                        return Orderable.Holder.of(it, it.getApplication().getContextPath().length());
                    } else {
                        // 不匹配
                        return Orderable.Holder.of(it, 0);
                    }
                })
                // 过滤掉不匹配的
                .filter(it -> it.getOrder() > 0)
                // 排序
                .sorted(Comparator.comparing(Orderable::getOrder))
                .map(Orderable.Holder::getData)
                // 取匹配度最高的应用
                .findFirst().orElse(null);

        if (application == null) {
            log.info("应用匹配失败");
            // 如果访问的路径是空的，就重定向到默认的应用
            if (Stringx.isNullOrEmpty(exchange.getRequest().getPath().value()) || "/".equals(exchange.getRequest().getPath().value())) {
                var defaultApp = tenant.getApplications().stream()
                        .filter(it -> Objects.equals(Boolean.TRUE, it.getEnabled()))
                        .filter(it -> Objects.equals(Boolean.TRUE, it.getPrimary()))
                        .findFirst().orElse(null);

                if (defaultApp == null) {
                    // 找不到默认应用，则直接返回错误信息
                    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, exchange.getRequiredAttribute(ExchangeAttributes.ORIGIN_URI).toString()));
                }

                log.info("重定向到默认应用[code: {}, name: {}, contextPath: {}]", defaultApp.getApplication().getCode(), defaultApp.getApplication().getName(), defaultApp.getApplication().getContextPath());
                return RedirectRender.of(exchange).redirect(URI.create(Stringx.addSuffix(defaultApp.getApplication().getContextPath(), "/"))).render();
            } else {
                // 找不到默认应用，则直接返回错误信息
                return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, exchange.getRequiredAttribute(ExchangeAttributes.ORIGIN_URI).toString()));
            }
        }

        var target = application.getApplication();

        ApplicationModule module = null;

        if (Listx.isNotEmpty(target.getModules())) {
            // 是否有模块
            module = target.getModules()
                    .stream()
                    .map(it -> {
                        // 子应用的 contextPath 已经以应用的 contextPath 开头了
                        var contextPath = it.getContextPath();
                        var path = exchange.getRequest().getPath().value();

                        if (path.equals(contextPath) || path.equals(Stringx.addSuffix(contextPath, "/"))) {
                            // 全匹配
                            return Orderable.Holder.of(it, Integer.MAX_VALUE);
                        } else if (path.startsWith(Stringx.addSuffix(contextPath, "/"))) {
                            // 根据路径匹配长度
                            return Orderable.Holder.of(it, contextPath.length());
                        } else {
                            // 不匹配
                            return Orderable.Holder.of(it, 0);
                        }
                    })
                    // 过滤掉不匹配的
                    .filter(it -> it.getOrder() > 0)
                    // 根据匹配度排序
                    .sorted(Comparator.comparing(Orderable.Holder::getOrder))
                    .map(Orderable.Holder::getData)
                    // 获取匹配度最高的
                    .findFirst().orElse(null);
        }

        // 目标路径
        String url;
        // 上下文路径
        String contextPath;

        if (module == null) {
            // 没有子模块匹配上，那么就转发到主应用
            log.info("匹配应用成功[code: {}, name: {}, contextPath: {}, url: {}]", target.getCode(), target.getName(), target.getContextPath(), target.getUrl());
            contextPath = target.getContextPath();
            url = target.getUrl();
        } else {
            // 子模块匹配上了
            log.info("匹配应用模块成功[code: {}, name: {}, contextPath: {}, url: {}]", target.getCode(), target.getName(), module.getContextPath(), module.getUrl());
            contextPath = module.getContextPath();
            url = module.getUrl();
        }

        if (exchange.getRequest().getPath().value().equals(contextPath) && !exchange.getRequest().getPath().value().endsWith("/")) {
            // 这里用于修复 SpringMVC 项目自定重定向到 / 的问题
            // 意思是网关已经帮 SpringMVC 项目做了重定向了，那么项目就不需要处理了
            log.info("重定向: '{}'", Stringx.addSuffix(contextPath, "/"));

            URI originUri = exchange.getRequiredAttribute(ExchangeAttributes.ORIGIN_URI);
            return RedirectRender.of(exchange).redirect(URI.create(UriComponentsBuilder.fromUri(originUri).path(originUri.getPath() + "/").build().toString())).render();
        }

        // 构建 URI
        var appUri = URI.create(url);
        URI targetUri;
        if ("file".equals(appUri.getScheme().toLowerCase())) {
            targetUri = appUri;
        } else {
            // 构建请求路径
            targetUri = URI.create(UriComponentsBuilder.fromUri(exchange.getRequest().getURI())
                    .scheme(appUri.getScheme())
                    .host(appUri.getHost())
                    .port(appUri.getPort())
                    .build().toString());
        }

        // 将目标应用的 URI 放到 Attributes，后面的 Filter 可以通过 Attributes 获取或修改
        exchange.setAttribute(ExchangeAttributes.TARGET_SERVER, targetUri);
        log.info("目标地址: {}", targetUri);

        // 将目标应用放到 Attributes，后面的 Filter 可以通过 Attributes 获取
        exchange.setAttribute(ExchangeAttributes.TARGET_APPLICATION, target);
        if (module != null) {
            exchange.setAttribute(ExchangeAttributes.TARGET_APPLICATION_MODULE, module);
        }

        exchange.getRequiredAttribute(ExchangeAttributes.TOKEN).withClaim(RegisteredClaims.ISSUER, "gateway");

        return chain.filter(exchange);
    }
}
