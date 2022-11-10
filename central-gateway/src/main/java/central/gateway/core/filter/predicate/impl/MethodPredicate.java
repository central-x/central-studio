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

package central.gateway.core.filter.predicate.impl;

import central.bean.OptionalEnum;
import central.gateway.core.filter.predicate.Predicate;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.validation.Label;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

/**
 * 请求方法断言
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
public class MethodPredicate implements Predicate {

    @Setter
    @Label("请求方法")
    @NotEmpty
    @Control(label = "请求方法", type = ControlType.CHECKBOX, defaultValue = "GET", comment = "请求方法在指定的方法列表中，匹配成功")
    private List<HttpMethod> methods;

    @Override
    public boolean predicate(ServerWebExchange exchange) {
        return this.methods.contains(HttpMethod.resolve(exchange.getRequest().getMethod().name()));
    }

    @Getter
    @AllArgsConstructor
    public enum HttpMethod implements OptionalEnum<String> {
        GET("GET", "GET"),
        POST("POST", "POST"),
        PUT("PUT", "PUT"),
        DELETE("DELETE", "DELETE"),
        PATCH("PATCH", "PATCH"),
        HEAD("HEAD", "HEAD");

        private final String name;
        private final String value;

        public static HttpMethod resolve(String value) {
            return OptionalEnum.resolve(HttpMethod.class, value.toUpperCase());
        }
    }
}
