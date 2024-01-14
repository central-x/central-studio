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

package central.studio.gateway.core.filter.predicate.impl;

import central.studio.gateway.core.filter.predicate.Predicate;
import central.lang.Stringx;
import central.pluglet.annotation.Control;
import central.util.Listx;
import central.validation.Label;
import central.web.XForwardedHeaders;
import jakarta.validation.constraints.NotBlank;
import lombok.Setter;
import org.springframework.web.server.ServerWebExchange;

/**
 * 主机名断言
 * <p>
 * 判断当前请求的主机名是否是指定主机名
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
public class HostPredicate implements Predicate {
    @Setter
    @Label("主机名")
    @NotBlank
    @Control(label = "主机名", comment = "用于判断 X-Forwarded-Host 请求头的值是否符合正则表达式，使用 String::matches 进行匹配")
    private String regexp;

    @Override
    public boolean predicate(ServerWebExchange exchange) {
        var hosts = exchange.getRequest().getHeaders().get(XForwardedHeaders.HOST);
        if (Listx.isNullOrEmpty(hosts)) {
            return false;
        }
        return hosts.stream().anyMatch(it -> Stringx.isNotBlank(it) && it.matches(this.regexp));
    }
}
