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

import central.data.organization.Department;
import central.lang.Stringx;
import central.sql.query.Conditions;
import central.starter.web.query.ListQuery;
import central.validation.Label;
import central.validation.group.Insert;
import central.validation.group.Update;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * Department List Query
 * <p>
 * 部门列表查询
 *
 * @author Alan Yeh
 * @since 2024/12/01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DepartmentListQuery extends ListQuery<Department> {
    @Serial
    private static final long serialVersionUID = -5747968457061279810L;

    @Label("单位主键")
    @Null(groups = Insert.class)
    @NotBlank(groups = Update.class)
    @Size(min = 1, max = 32, groups = Insert.class)
    private String unitId;

    @Label("父节点主键")
    private String parentId;

    @Label("主键")
    private String id;

    @Label("名称")
    private String name;

    @Label("标识")
    private String code;

    @Override
    public Conditions<Department> build() {
        var conditions = Conditions.of(Department.class)
                .eq(Department::getUnitId, this.getUnitId());

        if (Stringx.isNotEmpty(this.getParentId())) {
            conditions.eq(Department::getParentId, this.getParentId());
        }

        // 精确字段搜索
        if (Stringx.isNotEmpty(this.getId())) {
            conditions.eq(Department::getId, this.getId());
        }
        if (Stringx.isNotEmpty(this.getName())) {
            conditions.like(Department::getName, this.getName());
        }
        if (Stringx.isNotEmpty(this.getCode())) {
            conditions.like(Department::getCode, this.getCode());
        }

        // 模糊字段搜索
        for (String keyword : this.getKeywords()) {
            conditions.and(filter -> filter.like(Department::getCode, keyword).or().like(Department::getName, keyword));
        }

        return conditions;
    }
}
