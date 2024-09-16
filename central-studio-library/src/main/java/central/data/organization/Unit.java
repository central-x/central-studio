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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.List;

/**
 * 单位
 *
 * @author Alan Yeh
 * @since 2022/09/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Unit extends ModifiableEntity implements Codeable, Orderable<Unit>, Treeable<Unit> {
    @Serial
    private static final long serialVersionUID = 1043257253332275916L;

    /**
     * 父单位主键
     * <p>
     * 如果为空，则表示该单位是独立单位
     */
    @Nullable
    private String parentId;

    /**
     * 父单位
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Unit parent;

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
     * 所属行政区划主键
     */
    @Nonnull
    private String areaId;

    /**
     * 所属行政区划
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Area area;

    /**
     * 排序号
     */
    @Nonnull
    private Integer order;

    /**
     * 子单位
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Unit> children;

    /**
     * 子部门
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Department> departments;

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

    public UnitInput toInput() {
        return UnitInput.builder()
                .id(this.getId())
                .parentId(this.getParentId())
                .code(this.getCode())
                .name(this.getName())
                .areaId(this.getAreaId())
                .order(this.getOrder())
                .build();
    }
}
