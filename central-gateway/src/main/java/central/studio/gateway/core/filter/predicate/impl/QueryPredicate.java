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
import jakarta.validation.constraints.NotBlank;
import lombok.Setter;
import org.springframework.web.server.ServerWebExchange;

/**
 * Query 断言
 * <p>
 * 判断当前请求的 URL 参数是否包含指定参数和值
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
public class QueryPredicate implements Predicate {

    @Setter
    @Label("参数名")
    @NotBlank
    @Control(label = "参数名", comment = "参数名名，如果请求中没有包含此请求头，断言失败")
    private String query;

    @Setter
    @Label("匹配规则")
    @Control(label = "匹配规则", required = false, comment = "用于判断参数值是否符合正则表达式，使用 String::matches 进行匹配。如果为空，则仅判断是否包含指定参数名")
    private String regexp;

    @Override
    public boolean predicate(ServerWebExchange exchange) {
        var values = exchange.getRequest().getQueryParams().get(this.query);
        if (Listx.isNullOrEmpty(values)) {
            return false;
        }
        if (Stringx.isNullOrBlank(regexp)) {
            return true;
        }
        return values.stream().anyMatch(it -> Stringx.isNotBlank(it) && it.matches(this.regexp));
    }
}
