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

package central.data.organization;

import central.bean.*;
import central.sql.data.ModifiableEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serial;
import java.util.List;

/**
 * 部门信息
 *
 * @author Alan Yeh
 * @since 2022/09/12
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Department extends ModifiableEntity implements Codeable, Orderable<Department>, Treeable<Department> {
    @Serial
    private static final long serialVersionUID = 3624723639865181188L;

    /**
     * 所属单位主键
     */
    @Nonnull
    private String unitId;

    /**
     * 所属单位信息
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Unit unit;

    /**
     * 上级部门主键
     * 如果为空，则表示该部门是根部门
     */
    @Nullable
    private String parentId;

    /**
     * 上级部门信息
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Department parent;

    /**
     * 标识
     */
    @Nonnull
    private String code;

    /**
     * 名称
     */
    @Nonnull
    private String name;

    /**
     * 排序号
     */
    @Nonnull
    private Integer order;

    /**
     * 子部门
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Department> children;

    /**
     * 创建人信息
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Account creator;

    /**
     * 修改人信息
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Account modifier;

    public static class Tree {

    }

    public DepartmentInput.Builder toInput() {
        return DepartmentInput.builder()
                .id(this.getId())
                .unitId(this.getUnitId())
                .parentId(this.getParentId())
                .code(this.getCode())
                .name(this.getName())
                .order(this.getOrder());
    }
}
