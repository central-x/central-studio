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

import central.bean.OptionalEnum;
import central.gateway.core.attribute.ExchangeAttributes;
import central.gateway.core.body.EmptyBody;
import central.gateway.core.filter.Filter;
import central.gateway.core.filter.FilterChain;
import central.lang.Arrayx;
import central.lang.BooleanEnum;
import central.lang.Stringx;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.starter.web.reactive.extension.ServerWebExchangex;
import central.validation.Label;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 允许跨域
 *
 * @author Alan Yeh
 * @since 2022/11/12
 */
@ExtensionMethod(ServerWebExchangex.class)
public class CrossOriginFilter implements Filter, InitializingBean {

    @Control(label = "说明", type = ControlType.LABEL,
            defaultValue = """
                    　　本过滤器用于添加跨域相关的响应头。注意不要将 Origin 设置为 *，以免引起安全问题。在使用本插件的时候，应尽量减少允许跨域访问的范围。
                    使用 CrossOrigin 时，需要注意正确设置 Cookie 的 SameSite、SameParty 策略，否则 Cookie 将有可能失效。
                    　　Origin 的填写规则如下：<br/>
                    <ul>
                        <li>test.example.com：精确匹配域名，只允许该域名发起跨域访问</li>
                        <li>*.example.com：允许 example.com 的所有子域名发起跨域访问</li>
                        <li>192.168.*.*：允许 192.168.0.0/16 网段下的所有 IP 访问</li>
                        <li>如果有多种匹配规则，使用 <code>,</code> 连接</li>
                    <ul>
                    """)
    private String label;

    @Setter
    @Label("Origin")
    @NotBlank
    @Size(min = 1, max = 4096)
    @Control(label = "Origin", defaultValue = "*", comment = "允许指定来源的网站跨域访问")
    private String accessControlAllowOrigin;

    @Setter
    @Label("Methods")
    @NotEmpty
    @Control(label = "Methods", type = ControlType.CHECKBOX, defaultValue = "GET", comment = "添加 Access-Control-Allow-Methods 响应头，允许指定的请求方法跨域访问")
    private List<MethodType> methods;

    @Setter
    @Label("Max Age")
    @Min(0)
    @Max(1800)
    @NotNull
    @Control(label = "Max Age", type = ControlType.NUMBER, defaultValue = "1800", comment = "添加 Access-Control-Max-Age 响应头，指定本次预检请求的有效期，单位为秒。在有效期内，浏览器将不再发起跨域检查（OPTIONS 请求）")
    private Integer maxAge;

    @Setter
    @Label("Headers")
    @NotBlank
    @Control(label = "Headers", defaultValue = "Origin, Referer, Content-Type, Content-Language, Accept, Accept-Language, Accept-Encoding, Authorization",
            comment = "添加 Access-Control-Allow-Headers 响应头，指定允许跨域的头部字段")
    private String headers;

    @Setter
    @Label("Credentials")
    @NotNull
    @Control(label = "Credentials", defaultValue = "1", type = ControlType.RADIO, comment = "添加 Access-Control-Allow-Credentials 响应头，指定是否允许协带 Cookies")
    private BooleanEnum credentials;

    private final AntPathMatcher matcher = new AntPathMatcher();
    private List<String> allowedOrigins;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.allowedOrigins = Arrayx.asStream(this.accessControlAllowOrigin.split("[,]")).map(String::trim).toList();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, FilterChain chain) {
        var origin = exchange.getRequest().getHeaders().getFirst(HttpHeaders.ORIGIN);

        if (Stringx.isNullOrBlank(origin)) {
            // 如果没有 Origin 请求头，说明这个请求不是跨域请求
            // 如果不是跨域请求的话，那这个请求可以不需要处理跨域响应头，直接交给服务器处理即可
            return chain.filter(exchange);
        }

        // 根据主机名匹配
        var originHost = URI.create(origin).getHost();

        if (this.allowedOrigins.stream().noneMatch(it -> this.matcher.match(it, originHost))) {
            // 没有匹配上，说明该请求不在允许跨域的范围
            return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden: Cross origin"));
        }

        // 添加跨域访问响应头
        exchange.getResponse().getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        exchange.getResponse().getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, this.methods.stream().map(MethodType::getValue).collect(Collectors.joining(", ")));
        exchange.getResponse().getHeaders().add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, this.matcher.toString());
        exchange.getResponse().getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, this.headers);
        exchange.getResponse().getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, String.valueOf(this.credentials.getJValue()));

        if (HttpMethod.OPTIONS.matches(exchange.getRequest().getMethod().name())) {
            // 如果是 OPTIONS 请求，则代表这是一个跨域检查请求，直接返回 204 状态码即可
            // 浏览器在执行复杂跨域请求时，如跨域执行 POST、PUT 请求时，在真正发起请求前，会先发起一个 OPTIONS 请求，询问服务器端是否接收该跨域请求
            // 该 OPTIONS 请求只需要将跨域响应头返回给浏览器即可，不需要转发到服务端
            exchange.getResponse().setStatusCode(HttpStatus.NO_CONTENT);
            exchange.setAttribute(ExchangeAttributes.RESPONSE_BODY, new EmptyBody());
            return Mono.empty();
        } else {
            // 非 OPTIONS 请求需要转发到服务器端
            return chain.filter(exchange);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum MethodType implements OptionalEnum<String> {
        GET("GET", "GET"),
        POST("POST", "POST"),
        PUT("PUT", "PUT"),
        DELETE("DELETE", "DELETE"),
        PATCH("PATCH", "PATCH"),
        HEAD("HEAD", "HEAD");

        private final String name;
        private final String value;
    }
}
