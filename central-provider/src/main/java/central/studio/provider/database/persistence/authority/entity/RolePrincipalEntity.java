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
import central.data.authority.RolePrincipalInput;
import central.data.authority.option.PrincipalType;
import central.sql.data.Entity;
import central.validation.Enums;
import central.validation.Label;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * Role Principal Relation
 * <p>
 * 角色与主体关联关系
 *
 * @author Alan Yeh
 * @since 2022/09/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "X_AUTH_ROLE_PRINCIPAL")
@EqualsAndHashCode(callSuper = true)
public class RolePrincipalEntity extends Entity implements Tenantable {
    @Serial
    private static final long serialVersionUID = 5133609577351106865L;

    @Id
    @Label("主键")
    @Size(max = 32)
    private String id;

    @Label("应用主键")
    @NotBlank
    @Size(min = 1, max = 32)
    private String applicationId;

    @Label("角色")
    @NotBlank
    @Size(min = 1, max = 32)
    private String roleId;

    @Label("授权主体主键")
    @NotBlank
    @Size(min = 1, max = 32)
    private String principalId;

    @Label("主体类型")
    @NotBlank
    @Enums(PrincipalType.class)
    private String type;

    @Label("租户标识")
    @NotBlank
    @Size(min = 1, max = 32)
    private String tenantCode;

    public void fromInput(RolePrincipalInput input) {
        this.setApplicationId(input.getApplicationId());
        this.setRoleId(input.getRoleId());
        this.setPrincipalId(input.getPrincipalId());
        this.setType(input.getType());
    }
}
