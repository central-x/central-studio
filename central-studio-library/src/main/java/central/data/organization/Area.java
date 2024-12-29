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
import central.data.organization.option.AreaType;
import central.sql.data.ModifiableEntity;
import central.util.Objectx;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serial;
import java.util.Comparator;
import java.util.List;

/**
 * 行政区划
 *
 * @author Alan Yeh
 * @since 2022/09/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Area extends ModifiableEntity implements Codeable, Orderable<Area>, Treeable<Area> {
    @Serial
    private static final long serialVersionUID = 734938509208964402L;

    /**
     * 所属区划
     * 如果为空的话，则表示其为根
     */
    @Nullable
    private String parentId;

    /**
     * 所属区划信息
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Area parent;

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
     * 类型
     *
     * @see AreaType
     */
    @Nonnull
    private String type;

    /**
     * 排序号
     */
    @Nonnull
    private Integer order;

    /**
     * 子列单
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Area> children;

    /**
     * 单位
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Unit> units;

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

    /**
     * 树构建工具
     */
    public static class Tree {

        public static final Comparator<Area> DEFAULT_COMPARATOR = Comparator.comparing((Area it) -> AreaType.resolve(it.getType())).thenComparing(Area::getOrder).thenComparing(Area::getName);

        /**
         * 构建行政区划树
         *
         * @param areas      行政区划
         * @param comparator 排序器
         * @return 行政区划树
         */
        public static List<Area> build(List<Area> areas, @Nullable Comparator<Area> comparator) {
            return Treeable.build(areas, Objectx.getOrDefault(comparator, DEFAULT_COMPARATOR));
        }
    }

    public AreaInput.Builder toInput() {
        return AreaInput.builder()
                .id(this.getId())
                .parentId(this.getParentId())
                .code(this.getCode())
                .name(this.getName())
                .type(this.getType())
                .order(this.getOrder());
    }
}
