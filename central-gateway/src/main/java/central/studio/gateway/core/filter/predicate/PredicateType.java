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

package central.studio.gateway.core.filter.predicate;

import central.bean.OptionalEnum;
import central.gateway.core.filter.predicate.impl.*;
import central.studio.gateway.core.filter.predicate.impl.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 断言类型
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
@Getter
@RequiredArgsConstructor
public enum PredicateType implements OptionalEnum<String> {
    BEFORE("Before", "before", BeforePredicate.class),
    AFTER("After", "after", AfterPredicate.class),
    BETWEEN("Between", "between", BetweenPredicate.class),
    METHOD("Method", "method", MethodPredicate.class),
    REMOTE_ADDR("RemoteAddr", "remote_addr", RemoteAddrPredicate.class),
    QUERY("Query", "query", QueryPredicate.class),
    HEADER("Header", "header", HeaderPredicate.class),
    COOKIE("Cookie", "Cookie", CookiePredicate.class),
    HOST("Host", "host", HostPredicate.class),
    DAILY("Daily", "daily", DailyPredicate.class),
    WEEKLY("Weekly", "weekly", WeeklyPredicate.class),
    MONTHLY("Monthly", "monthly", MonthlyPredicate.class),
    ANNUALLY("Annually", "annually", AnnuallyPredicate.class);

    private final String name;
    private final String value;
    private final Class<? extends Predicate> type;

    public static PredicateType resolve(String value) {
        return OptionalEnum.resolve(PredicateType.class, value);
    }
}
