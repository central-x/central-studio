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
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.validation.Label;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;
import org.springframework.web.server.ServerWebExchange;

import java.sql.Timestamp;


/**
 * After 断言
 * <p>
 * 判断请求进来的时间是否在指定时间之后
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
public class AfterPredicate implements Predicate {

    @Setter
    @Label("After")
    @NotNull
    @Control(label = "After", type = ControlType.DATETIME, comment = "请求时间发生在 After 之后，匹配成功")
    private Timestamp after;

    @Override
    public boolean predicate(ServerWebExchange exchange) {
        return System.currentTimeMillis() > after.getTime();
    }
}
