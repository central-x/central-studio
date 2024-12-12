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

package central.studio.dashboard.controller.authority.query;

import central.data.authority.Permission;
import central.sql.query.Conditions;
import central.starter.web.query.ListQuery;
import central.validation.Label;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * Permission List Query
 * <p>
 * 权限列表查询
 *
 * @author Alan Yeh
 * @since 2024/12/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionListQuery extends ListQuery<Permission> {
    @Serial
    private static final long serialVersionUID = 7298018272245180943L;

    @Label("菜单主键")
    @NotBlank
    @Size(min = 1, max = 32)
    private String menuId;

    @Override
    public Conditions<Permission> build() {
        var conditions = Conditions.of(Permission.class);

        conditions.eq(Permission::getMenuId, this.getMenuId());

        return conditions;
    }
}
