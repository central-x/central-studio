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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 每年固定月份
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
public class AnnuallyPredicate implements Predicate {

    @Setter
    @Label("月份")
    @NotEmpty
    @Control(label = "月份", type = ControlType.CHECKBOX, comment = "请求时间的月份与选中的月份相同时，匹配成功")
    private List<Month> months;

    @Override
    public boolean predicate(ServerWebExchange exchange) {
        var now = OffsetDateTime.now();
        return this.months.stream().anyMatch(it -> it.isCompatibleWith(now.getMonthValue()));
    }

    @Getter
    @AllArgsConstructor
    public enum Month implements OptionalEnum<String> {
        JANUARY("一月", "1"),
        FEBRUARY("二月", "2"),
        MARCH("三月", "3"),
        APRIL("四月", "4"),
        MAY("五月", "5"),
        JUNE("六月", "6"),
        JULY("七月", "7"),
        AUGUST("八月", "8"),
        SEPTEMBER("九月", "9"),
        OCTOBER("十月", "10"),
        NOVEMBER("十一月", "11"),
        DECEMBER("十二月", "12");

        private final String name;
        private final String value;

        @Override
        public boolean isCompatibleWith(Object value) {
            if (value instanceof Month month) {
                return this == month;
            } else {
                return Objects.equals(this.getValue(), value.toString());
            }
        }

        public static Month resolve(String value) {
            return OptionalEnum.resolve(Month.class, value);
        }
    }
}
