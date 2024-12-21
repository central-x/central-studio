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

import central.provider.graphql.DTO;
import central.studio.provider.database.persistence.authority.entity.MenuEntity;
import central.studio.provider.database.persistence.authority.MenuPersistence;
import central.studio.provider.database.persistence.authority.PermissionPersistence;
import central.studio.provider.graphql.organization.dto.AccountDTO;
import central.studio.provider.graphql.saas.dto.ApplicationDTO;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLType;
import central.web.XForwardedHeaders;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.SelectedField;
import lombok.EqualsAndHashCode;
import org.dataloader.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.Serial;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Menu
 * 菜单
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
@GraphQLType("Menu")
@EqualsAndHashCode(callSuper = true)
public class MenuDTO extends MenuEntity implements DTO {
    @Serial
    private static final long serialVersionUID = 7340521210113303617L;

    /**
     * 应用
     */
    @GraphQLGetter
    public CompletableFuture<ApplicationDTO> getApplication(DataFetchingEnvironment environment,
                                                            DataLoader<String, ApplicationDTO> loader) {
        return loader.load(this.getApplicationId(), environment);
    }


    /**
     * 父菜单
     */
    @GraphQLGetter
    public CompletableFuture<MenuDTO> getParent(DataFetchingEnvironment environment,
                                                DataLoader<String, MenuDTO> loader) {
        return loader.load(this.getParentId(), environment);
    }

    /**
     * 子菜单
     */
    @GraphQLGetter
    public List<MenuDTO> getChildren(DataFetchingEnvironment environment,
                                     @Autowired MenuPersistence persistence,
                                     @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var columns = Columns.of(MenuDTO.class, environment.getSelectionSet().getFields().stream()
                .filter(it -> "children".equals(it.getParentField().getName()))
                .map(SelectedField::getName).toList().toArray(new String[0]));

        var data = persistence.findBy(null, null, columns, Conditions.of(MenuDTO.class).eq(MenuDTO::getParentId, this.getId()), Orders.of(MenuDTO.class).asc(MenuDTO::getOrder).asc(MenuDTO::getCode), tenant);
        return DTO.wrap(data, MenuDTO.class);
    }

    /**
     * 权限信息
     */
    @GraphQLGetter
    public List<PermissionDTO> getPermissions(DataFetchingEnvironment environment,
                                              @Autowired PermissionPersistence persistence,
                                              @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var columns = Columns.of(PermissionDTO.class, environment.getSelectionSet().getFields().stream()
                .filter(it -> "permissions".equals(it.getParentField().getName()))
                .map(SelectedField::getName).toList().toArray(new String[0]));

        var data = persistence.findBy(null, null, columns, Conditions.of(PermissionDTO.class).eq(PermissionDTO::getMenuId, this.getId()), null, tenant);
        return DTO.wrap(data, PermissionDTO.class);
    }

    /**
     * 创建人信息
     */
    @GraphQLGetter
    public CompletableFuture<AccountDTO> getCreator(DataFetchingEnvironment environment,
                                                    DataLoader<String, AccountDTO> loader) {
        return loader.load(this.getCreatorId(), environment);
    }

    /**
     * 修改人信息
     */
    @GraphQLGetter
    public CompletableFuture<AccountDTO> getModifier(DataFetchingEnvironment environment,
                                                     DataLoader<String, AccountDTO> loader) {
        return loader.load(this.getModifierId(), environment);
    }
}
