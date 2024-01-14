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

import central.bean.OptionalEnum;
import central.studio.gateway.core.filter.predicate.Predicate;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.validation.Label;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.server.ServerWebExchange;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 每周固定日期
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
public class WeeklyPredicate implements Predicate {

    @Setter
    @Label("星期")
    @NotEmpty
    @Control(label = "星期", type = ControlType.CHECKBOX, comment = "请求时间的星期与选中的星期相同时，匹配成功")
    private List<DayOfWeak> days;

    @Override
    public boolean predicate(ServerWebExchange exchange) {
        var now = OffsetDateTime.now();

        return this.days.contains(DayOfWeak.resolve(String.valueOf(now.getDayOfWeek())));
    }

    @Getter
    @AllArgsConstructor
    public enum DayOfWeak implements OptionalEnum<String> {
        MONDAY("星期一", "1"),
        TUESDAY("星期二", "2"),
        WEDNESDAY("星期三", "3"),
        THURSDAY("星期四", "4"),
        FRIDAY("星期五", "5"),
        SATURDAY("星期六", "6"),
        SUNDAY("星期天", "7");

        private final String name;
        private final String value;

        public static DayOfWeak resolve(String value) {
            return OptionalEnum.resolve(DayOfWeak.class, value);
        }
    }
}
