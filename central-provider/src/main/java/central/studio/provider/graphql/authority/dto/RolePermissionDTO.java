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

import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLType;
import central.studio.provider.database.persistence.authority.entity.RolePermissionEntity;
import central.studio.provider.graphql.organization.dto.AccountDTO;
import central.studio.provider.graphql.saas.dto.ApplicationDTO;
import lombok.EqualsAndHashCode;
import org.dataloader.DataLoader;

import java.io.Serial;
import java.util.concurrent.CompletableFuture;

/**
 * Role Permission Relation
 * <p>
 * 角色与权限关联关系
 *
 * @author Alan Yeh
 * @since 2024/12/14
 */
@GraphQLType("RolePermission")
@EqualsAndHashCode(callSuper = true)
public class RolePermissionDTO extends RolePermissionEntity {
    @Serial
    private static final long serialVersionUID = 3467870578800222096L;

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
     * 权限信息
     */
    @GraphQLGetter
    public CompletableFuture<PermissionDTO> getPermission(DataLoader<String, PermissionDTO> loader) {
        return loader.load(this.getPermissionId());
    }

    /**
     * 创建人信息
     */
    @GraphQLGetter
    public CompletableFuture<AccountDTO> getCreator(DataLoader<String, AccountDTO> loader) {
        return loader.load(this.getCreatorId());
    }
}
