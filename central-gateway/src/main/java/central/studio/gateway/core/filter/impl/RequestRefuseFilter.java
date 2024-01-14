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

import central.bean.OptionalEnum;
import central.studio.gateway.core.filter.Filter;
import central.studio.gateway.core.filter.FilterChain;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.util.Objectx;
import central.validation.Label;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 拒绝访问
 *
 * @author Alan Yeh
 * @since 2022/11/12
 */
public class RequestRefuseFilter implements Filter {

    @Control(label = "说明", type = ControlType.LABEL,
            defaultValue = """
                    　　本过滤器用于拒绝指定请求。本过滤器的判断条件由断言提供，所有符合断言的请求都将被<code>拒绝访问</code>。
                    拒绝访问时，网关将返回用户指定的状态码和错误信息。
                    """)
    private String label;

    @Setter
    @Label("状态码")
    @NotNull
    @Control(label = "状态码", type = ControlType.RADIO, defaultValue = "403")
    private Status status;

    @Setter
    @Label("错误信息")
    @NotBlank
    @Size(min = 1, max = 1024)
    @Control(label = "错误信息", defaultValue = "Forbidden")
    private String message;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, FilterChain chain) {
        return Mono.error(new ResponseStatusException(Objectx.getOrDefault(HttpStatus.resolve(this.status.getCode()), HttpStatus.FORBIDDEN), this.message));
    }

    @Getter
    @AllArgsConstructor
    public enum Status implements OptionalEnum<String> {
        BAD_REQUEST("Bad Request (400)", "400", 400),
        UNAUTHORIZED("Unauthorized (401)", "401", 401),
        FORBIDDEN("Forbidden (403)", "403", 403),
        NOT_FOUND("Not Found (404)", "404", 404),
        METHOD_NOT_ALLOWED("Method Not Allowed (405)", "405", 405),
        REQUEST_TIMEOUT("Request Timeout (408)", "408", 408),
        INTERNAL_SERVER_ERROR("Internal Server Error (500)", "500", 500),
        BAD_GATEWAY("Bad Gateway (502)", "502", 502),
        SERVICE_UNAVAILABLE("Service Unavailable (503)", "503", 503),
        GATEWAY_TIMEOUT("Gateway Timeout (504)", "504", 504),
        HTTP_VERSION_NOT_SUPPORTED("HTTP Version Not Supported (505)", "505", 505);

        private final String name;
        private final String value;
        private final int code;
    }
}
