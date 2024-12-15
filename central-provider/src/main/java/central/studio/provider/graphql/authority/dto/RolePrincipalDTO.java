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

package central.studio.provider.graphql.authority.dto;

import central.data.authority.option.PrincipalType;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLType;
import central.studio.provider.graphql.authority.entity.RolePrincipalEntity;
import central.studio.provider.graphql.organization.dto.AccountDTO;
import central.studio.provider.graphql.organization.dto.DepartmentDTO;
import central.studio.provider.graphql.organization.dto.UnitDTO;
import central.studio.provider.graphql.saas.dto.ApplicationDTO;
import lombok.EqualsAndHashCode;
import org.dataloader.DataLoader;

import java.io.Serial;
import java.util.concurrent.CompletableFuture;

/**
 * Role Principal Relation
 * <p>
 * 角色与主体关联关系
 *
 * @author Alan Yeh
 * @since 2024/12/14
 */
@GraphQLType("RolePrincipal")
@EqualsAndHashCode(callSuper = true)
public class RolePrincipalDTO extends RolePrincipalEntity {
    @Serial
    private static final long serialVersionUID = 1176408641686043776L;

    /**
     * 应用信息
     */
    @GraphQLGetter
    public CompletableFuture<ApplicationDTO> getApplication(DataLoader<String, ApplicationDTO> loader) {
        return loader.load(this.getApplicationId());
    }

    /**
     * 角色信息
     */
    @GraphQLGetter
    public CompletableFuture<RoleDTO> getRole(DataLoader<String, RoleDTO> loader) {
        return loader.load(this.getRoleId());
    }

    /**
     * 帐号信息（当 type 为 account 时）
     */
    @GraphQLGetter
    public CompletableFuture<AccountDTO> getAccount(DataLoader<String, AccountDTO> loader) {
        if (PrincipalType.resolve(this.getType()) == PrincipalType.ACCOUNT) {
            return loader.load(this.getPrincipalId());
        } else {
            return loader.load(null);
        }
    }

    /**
     * 单位信息（当 type 为 unit 时）
     */
    @GraphQLGetter
    public CompletableFuture<UnitDTO> getUnit(DataLoader<String, UnitDTO> loader) {
        if (PrincipalType.resolve(this.getType()) == PrincipalType.UNIT) {
            return loader.load(this.getPrincipalId());
        } else {
            return loader.load(null);
        }
    }

    /**
     * 部门信息（当 type 为 department 时）
     */
    @GraphQLGetter
    public CompletableFuture<DepartmentDTO> getDepartment(DataLoader<String, DepartmentDTO> loader) {
        if (PrincipalType.resolve(this.getType()) == PrincipalType.DEPARTMENT) {
            return loader.load(this.getPrincipalId());
        } else {
            return loader.load(null);
        }
    }

    /**
     * 创建人信息
     */
    @GraphQLGetter
    public CompletableFuture<AccountDTO> getCreator(DataLoader<String, AccountDTO> loader) {
        return loader.load(this.getCreatorId());
    }
}
