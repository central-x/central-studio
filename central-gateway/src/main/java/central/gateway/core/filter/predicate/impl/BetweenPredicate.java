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

import central.gateway.core.filter.predicate.Predicate;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.validation.Label;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.server.ServerWebExchange;

import java.sql.Timestamp;

/**
 * 区间断言
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
public class BetweenPredicate implements Predicate, InitializingBean {

    @Setter
    @Label("开始时间")
    @NotNull
    @Control(label = "开始时间", type = ControlType.DATETIME)
    private Timestamp begin;

    @Setter
    @Label("结束时间")
    @NotNull
    @Control(label = "结束时间", type = ControlType.DATETIME)
    private Timestamp end;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (begin.getTime() > end.getTime()) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }
    }

    @Override
    public boolean predicate(ServerWebExchange exchange) {
        var now = System.currentTimeMillis();
        return now > begin.getTime() && now < end.getTime();
    }
}
