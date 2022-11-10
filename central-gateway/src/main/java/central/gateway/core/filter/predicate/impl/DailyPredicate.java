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
import org.springframework.web.server.ServerWebExchange;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

/**
 * 每日区间断言
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
public class DailyPredicate implements Predicate {

    @Setter
    @Label("开始时间")
    @NotNull
    @Control(label = "开始时间", type = ControlType.TIME)
    private Timestamp beginTime;

    @Setter
    @Label("结束时间")
    @NotNull
    @Control(label = "结束时间", type = ControlType.TIME)
    private Timestamp endTime;

    @Override
    public boolean predicate(ServerWebExchange exchange) {
        // 当前时间
        var now = OffsetDateTime.now();

        // 每日开始时间
        var offsetBeginTime = OffsetDateTime.ofInstant(beginTime.toInstant(), now.getOffset());
        var begin = OffsetDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), offsetBeginTime.getHour(), offsetBeginTime.getMinute(), offsetBeginTime.getSecond(), 0, now.getOffset());
        if (now.isBefore(begin)) {
            return false;
        }

        // 每日结束时间
        var offsetEndTime = OffsetDateTime.ofInstant(endTime.toInstant(), now.getOffset());
        var end = OffsetDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), offsetEndTime.getHour(), offsetEndTime.getMinute(), offsetEndTime.getSecond(), 0, now.getOffset());
        return !now.isAfter(end);
    }
}
