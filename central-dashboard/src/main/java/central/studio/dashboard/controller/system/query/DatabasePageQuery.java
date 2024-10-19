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

package central.studio.dashboard.controller.system.query;

import central.data.system.Database;
import central.lang.Stringx;
import central.sql.query.Conditions;
import central.starter.web.query.PageQuery;
import central.validation.Label;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * Database Page Query
 * <p>
 * 数据库分页查询
 *
 * @author Alan Yeh
 * @since 2024/10/20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DatabasePageQuery extends PageQuery<Database> {
    @Serial
    private static final long serialVersionUID = 5563847496249217986L;

    @NotBlank
    @Label("应用主键")
    private String applicationId;

    @Label("标识")
    private String code;

    @Label("名称")
    private String name;

    @Override
    public Conditions<Database> build() {
        var conditions = Conditions.of(Database.class);

        conditions.eq(Database::getApplicationId, this.getApplicationId());

        // 精确搜索
        if (Stringx.isNotBlank(this.getCode())) {
            conditions.like(Database::getCode, this.getCode());
        }
        if (Stringx.isNotBlank(this.getName())) {
            conditions.like(Database::getName, this.getName());
        }

        // 模糊搜索
        for (var keyword : this.getKeywords()) {
            conditions.and(filter -> filter.like(Database::getCode, keyword).or().like(Database::getName, keyword));
        }

        return conditions;
    }
}
