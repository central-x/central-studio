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
 * 每月固定日期
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
public class MonthlyPredicate implements Predicate {

    @Setter
    @Label("日期")
    @NotEmpty
    @Control(label = "日期", type = ControlType.CHECKBOX, comment = "请求时间与选中的日期相同时，匹配成功")
    private List<DayOfMonth> days;

    @Override
    public boolean predicate(ServerWebExchange exchange) {
        var now = OffsetDateTime.now();

        return days.contains(DayOfMonth.resolve(String.valueOf(now.getDayOfMonth())));
    }

    @Getter
    @AllArgsConstructor
    public enum DayOfMonth implements OptionalEnum<String> {
        DAY1("1", "1"),
        DAY2("2", "2"),
        DAY3("3", "3"),
        DAY4("4", "4"),
        DAY5("5", "5"),
        DAY6("6", "6"),
        DAY7("7", "7"),
        DAY8("8", "8"),
        DAY9("9", "9"),
        DAY10("10", "10"),
        DAY11("11", "11"),
        DAY12("12", "12"),
        DAY13("13", "13"),
        DAY14("14", "14"),
        DAY15("15", "15"),
        DAY16("16", "16"),
        DAY17("17", "17"),
        DAY18("18", "18"),
        DAY19("19", "19"),
        DAY20("20", "20"),
        DAY21("21", "21"),
        DAY22("22", "22"),
        DAY23("23", "23"),
        DAY24("24", "24"),
        DAY25("25", "25"),
        DAY26("26", "26"),
        DAY27("27", "27"),
        DAY28("28", "28"),
        DAY29("29", "29"),
        DAY30("30", "30"),
        DAY31("31", "31");

        private final String name;
        private final String value;

        public static DayOfMonth resolve(String value) {
            return OptionalEnum.resolve(DayOfMonth.class, value);
        }
    }
}
