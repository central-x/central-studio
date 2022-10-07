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

package central.data.sec;

import central.bean.*;
import central.data.sec.option.MenuType;
import central.data.org.Account;
import central.sql.data.ModifiableEntity;
import central.util.Listx;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serial;
import java.util.List;

/**
 * 菜单
 *
 * @author Alan Yeh
 * @since 2022/09/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Menu extends ModifiableEntity implements Codeable, Available, Remarkable, Orderable<Menu>, Treeable<Menu> {
    @Serial
    private static final long serialVersionUID = -6200313970489750035L;

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
     * 图标
     */
    @Nullable
    private String icon;

    /**
     * 菜单类型
     *
     * @see MenuType
     */
    @Nonnull
    private String type;

    /**
     * 是否启用
     */
    @Nonnull
    private Boolean enabled;

    /**
     * 排序号
     */
    @Nonnull
    private Integer order;

    /**
     * 描述
     */
    @Nullable
    private String remark;

    /**
     * 父菜单主键
     * 如果为空，则表示该菜单为根菜单
     */
    @Nullable
    private String parentId;

    /**
     * 父菜单
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Menu parent;

    /**
     * 子菜单
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Menu> children;

    /**
     * 权限
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Permission> permissions;

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

    public MenuInput toInput() {
        return MenuInput.builder()
                .id(this.getId())
                .parentId(this.getParentId())
                .code(this.getCode())
                .name(this.getName())
                .icon(this.getIcon())
                .type(this.getType())
                .enabled(this.getEnabled())
                .order(this.getOrder())
                .remark(this.getRemark())
                .permissions(Listx.asStream(this.getPermissions()).map(Permission::toInput).toList())
                .build();
    }
}
