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

package central.studio.dashboard.controller.organization.query;

import central.data.organization.Area;
import central.data.organization.option.AreaType;
import central.lang.Stringx;
import central.sql.query.Conditions;
import central.starter.web.query.ListQuery;
import central.validation.Enums;
import central.validation.Label;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * Area List Query
 * <p>
 * 行政区划列表查询
 *
 * @author Alan Yeh
 * @since 2024/09/20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AreaListQuery extends ListQuery<Area> {
    @Serial
    private static final long serialVersionUID = -5166956508840261658L;

    @Label("父节点主键")
    private String parentId;

    @Label("主键")
    private String id;

    @Label("名称")
    private String name;

    @Label("区划代码")
    private String code;

    @Label("类型")
    @Enums(value = AreaType.class)
    private String type;

    @Override
    public Conditions<Area> build() {
        var conditions = Conditions.of(Area.class);

        if (Stringx.isNotEmpty(this.getParentId())) {
            conditions.eq(Area::getParentId, this.getParentId());
        }

        // 精确搜索
        if (Stringx.isNotEmpty(this.getId())) {
            conditions.eq(Area::getId, this.getId());
        }
        if (Stringx.isNotEmpty(this.getName())) {
            conditions.like(Area::getName, this.getName());
        }
        if (Stringx.isNotEmpty(this.getCode())) {
            conditions.like(Area::getCode, this.getCode());
        }
        if (Stringx.isNotEmpty(this.getType())) {
            conditions.eq(Area::getType, this.getType());
        }

        // 模糊搜索
        for (String keyword : this.getKeywords()) {
            conditions.and(filter -> filter.like(Area::getName, keyword));
        }

        return conditions;
    }
}
