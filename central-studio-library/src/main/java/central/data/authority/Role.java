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

package central.data.authority;

import central.bean.*;
import central.data.organization.Account;
import central.data.organization.Unit;
import central.data.saas.Application;
import central.sql.data.ModifiableEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * 角色
 *
 * @author Alan Yeh
 * @since 2022/09/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Role extends ModifiableEntity implements Codeable, Available, Remarkable {
    @Serial
    private static final long serialVersionUID = 4370958444526615039L;

    /**
     * 应用标识
     */
    @Nonnull
    private String applicationId;

    /**
     * 应用
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Application application;

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
     * 所属单位主键
     * 如果为空，则该角色属于系统角色
     */
    @Nullable
    private String unitId;

    /**
     * 所属单位信息
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Unit unit;

    /**
     * 是否启用
     */
    @Nonnull
    private Boolean enabled;

    /**
     * 备注
     */
    @Nullable
    private String remark;

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

    public RoleInput.Builder toInput() {
        return RoleInput.builder()
                .id(this.getId())
                .applicationId(this.getApplicationId())
                .code(this.getCode())
                .name(this.getName())
                .unitId(this.getUnitId())
                .enabled(this.getEnabled())
                .remark(this.getRemark());
    }
}
