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

package central.studio.dashboard.controller.identity.query;

import central.data.identity.IdentityStrategy;
import central.data.organization.option.AreaType;
import central.lang.Stringx;
import central.sql.query.Conditions;
import central.starter.web.query.PageQuery;
import central.validation.Enums;
import central.validation.Label;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Identity Strategy Page Query
 * <p>
 * 认证策略分页查询
 *
 * @author Alan Yeh
 * @since 2024/11/11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StrategyPageQuery extends PageQuery<IdentityStrategy> {
    @Label("主键")
    private String id;

    @Label("名称")
    private String name;

    @Label("标识")
    private String code;

    @Label("类型")
    @Enums(value = AreaType.class)
    private String type;

    @Override
    public Conditions<IdentityStrategy> build() {
        var conditions = Conditions.of(IdentityStrategy.class);

        // 精确字段搜索
        if (Stringx.isNotEmpty(this.getId())) {
            conditions.eq(IdentityStrategy::getId, this.getId());
        }
        if (Stringx.isNotEmpty(this.getName())) {
            conditions.like(IdentityStrategy::getName, this.getName());
        }
        if (Stringx.isNotEmpty(this.getCode())) {
            conditions.like(IdentityStrategy::getCode, this.getCode());
        }

        // 模糊搜索
        for (String keyword : this.getKeywords()) {
            conditions.and(filter -> filter.like(IdentityStrategy::getCode, keyword).or().like(IdentityStrategy::getName, keyword));
        }
        return conditions;
    }
}
