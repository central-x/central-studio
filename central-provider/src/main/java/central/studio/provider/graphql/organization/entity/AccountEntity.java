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

package central.studio.provider.graphql.organization.entity;

import central.bean.Available;
import central.bean.Deletable;
import central.bean.Tenantable;
import central.data.organization.AccountInput;
import central.sql.data.ModifiableEntity;
import central.sql.meta.annotation.TableRelation;
import central.util.Objectx;
import central.validation.Label;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * 帐户信息
 *
 * @author Alan Yeh
 * @since 2022/09/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "X_ORG_ACCOUNT")
@EqualsAndHashCode(callSuper = true)
@TableRelation(alias = "unit", table = AccountUnitEntity.class, target = UnitEntity.class, relationProperty = "accountId", targetRelationProperty = "unitId")
@TableRelation(alias = "rank", table = AccountUnitEntity.class, target = RankEntity.class, relationProperty = "accountId", targetRelationProperty = "rankId")
@TableRelation(alias = "department", table = AccountDepartmentEntity.class, target = DepartmentEntity.class, relationProperty = "accountId", targetRelationProperty = "departmentId")
@TableRelation(alias = "post", table = AccountDepartmentEntity.class, target = PostEntity.class, relationProperty = "accountId", targetRelationProperty = "postId")
public class AccountEntity extends ModifiableEntity implements Available, Deletable, Tenantable {
    @Serial
    private static final long serialVersionUID = -7393559754393169213L;

    @Id
    @Label("主键")
    @Size(max = 32)
    private String id;

    @Label("用户名")
    @NotBlank
    @Size(min = 1, max = 16)
    @Pattern(regexp = "^[0-9a-zA-Z_-]+$", message = "${label}[${property}]只能由数字、英文字母、中划线、下划线组成")
    @Pattern(regexp = "^(?!-)(?!_).*$", message = "${label}[${property}]不能由中划线或下划线开头")
    @Pattern(regexp = "^(?!.*?[-_]$).*$", message = "${label}[${property}]不能由中划线或下划线结尾")
    @Pattern(regexp = "^(?!.*(--).*$).*$", message = "${label}[${property}]不能出现连续中划线")
    @Pattern(regexp = "^(?!.*(__).*$).*$", message = "${label}[${property}]不能出现连续下划线")
    @Pattern(regexp = "^(?!.*(-_|_-).*$).*$", message = "${label}[${property}]不能出现连续中划线、下划线")
    private String username;

    @Label("邮箱")
    @Email
    @Size(max = 50)
    private String email;

    @Label("手机号")
    @Size(max = 16)
    private String mobile;

    @Label("姓名")
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @Label("头像")
    @Size(max = 2 * 1024 * 1024)
    private String avatar;

    /**
     * 管理员是指三员（系统管理员，安全管理员、安全保密员）
     */
    @Label("是否管理员")
    @NotNull
    private Boolean admin;

    @NotNull
    @Label("是否启用")
    @NotNull
    private Boolean enabled;

    @NotNull
    @Label("是否已删除")
    @NotNull
    private Boolean deleted;

    @Label("租户标识")
    @NotBlank
    @Size(min = 1, max = 32)
    private String tenantCode;

    public void fromInput(@Nonnull AccountInput input) {
        this.setId(input.getId());
        this.setUsername(input.getUsername());
        this.setEmail(Objectx.getOrDefault(input.getEmail(), ""));
        this.setMobile(Objectx.getOrDefault(input.getMobile(), ""));
        this.setName(input.getName());
        this.setAvatar(Objectx.getOrDefault(input.getAvatar(), ""));
        this.setEnabled(input.getEnabled());
        this.setDeleted(input.getDeleted());
    }
}
