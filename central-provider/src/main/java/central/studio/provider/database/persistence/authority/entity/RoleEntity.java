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

package central.studio.provider.database.persistence.authority.entity;

import central.bean.Tenantable;
import central.data.authority.RoleInput;
import central.sql.data.ModifiableEntity;
import central.sql.meta.annotation.TableRelation;
import central.validation.Label;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * 角色
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "X_AUTH_ROLE")
@EqualsAndHashCode(callSuper = true)
@TableRelation(alias = "permission", table = RolePermissionEntity.class, target = PermissionEntity.class, relationProperty = "roleId", targetRelationProperty = "permissionId")
public class RoleEntity extends ModifiableEntity implements Tenantable {
    @Serial
    private static final long serialVersionUID = 2645765844802062761L;

    @Id
    @Label("主键")
    @Size(max = 32)
    private String id;

    @Label("应用主键")
    @NotBlank
    @Size(min = 1, max = 32)
    private String applicationId;

    @Label("标识")
    @NotBlank
    @Size(min = 1, max = 32)
    @Pattern(regexp = "^[0-9a-zA-Z_-]+$", message = "${label}[${property}]只能由数字、英文字母、中划线、下划线组成")
    @Pattern(regexp = "^(?!-)(?!_).*$", message = "${label}[${property}]不能由中划线或下划线开头")
    @Pattern(regexp = "^(?!.*?[-_]$).*$", message = "${label}[${property}]不能由中划线或下划线结尾")
    @Pattern(regexp = "^(?!.*(--).*$).*$", message = "${label}[${property}]不能出现连续中划线")
    @Pattern(regexp = "^(?!.*(__).*$).*$", message = "${label}[${property}]不能出现连续下划线")
    @Pattern(regexp = "^(?!.*(-_|_-).*$).*$", message = "${label}[${property}]不能出现连续中划线、下划线")
    private String code;

    @Label("名称")
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @Label("所属单位主键")
    @Size(max = 32)
    private String unitId;

    @Label("是否启用")
    @NotNull
    private Boolean enabled;

    @Label("备注")
    @Size(max = 1024)
    private String remark;

    @Label("租户标识")
    @NotBlank
    @Size(min = 1, max = 32)
    private String tenantCode;

    public void fromInput(RoleInput input) {
        this.setId(input.getId());
        this.setApplicationId(input.getApplicationId());
        this.setCode(input.getCode());
        this.setName(input.getName());
        this.setUnitId(input.getUnitId());
        this.setEnabled(input.getEnabled());
        this.setRemark(input.getRemark());
    }
}
