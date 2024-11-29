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

package central.studio.dashboard.controller.log.query;

import central.data.log.LogFilter;
import central.lang.Stringx;
import central.sql.query.Conditions;
import central.starter.web.query.PageQuery;
import central.validation.Label;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * Log Filter Page Query
 * <p>
 * 日志过滤器分页查询
 *
 * @author Alan Yeh
 * @since 2024/11/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FilterPageQuery extends PageQuery<LogFilter> {

    @Serial
    private static final long serialVersionUID = -8888925514137961065L;

    @Label("主键")
    private String id;

    @Label("标识")
    private String code;

    @Label("名称")
    private String name;

    @Override
    public Conditions<LogFilter> build() {
        var conditions = Conditions.of(LogFilter.class);

        // 精确字段搜索
        if (Stringx.isNotEmpty(this.getId())) {
            conditions.eq(LogFilter::getId, this.getId());
        }
        if (Stringx.isNotEmpty(this.getCode())) {
            conditions.like(LogFilter::getCode, this.getCode());
        }
        if (Stringx.isNotEmpty(this.getName())) {
            conditions.like(LogFilter::getName, this.getName());
        }

        // 模糊搜索
        for (String keyword : this.getKeywords()) {
            conditions.and(filter -> filter.like(LogFilter::getCode, keyword).or().like(LogFilter::getName, keyword));
        }
        return conditions;
    }
}
