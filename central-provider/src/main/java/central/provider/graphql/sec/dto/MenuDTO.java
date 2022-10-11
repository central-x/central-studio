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

package central.provider.graphql.sec.dto;

import central.api.DTO;
import central.provider.graphql.sec.entity.MenuEntity;
import central.provider.graphql.sec.entity.PermissionEntity;
import central.provider.graphql.sec.query.MenuQuery;
import central.provider.graphql.sec.query.PermissionQuery;
import central.provider.graphql.org.dto.AccountDTO;
import central.provider.graphql.ten.dto.ApplicationDTO;
import central.sql.Conditions;
import central.sql.Orders;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLType;
import central.starter.web.http.XForwardedHeaders;
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
    public CompletableFuture<ApplicationDTO> getApplication(DataLoader<String, ApplicationDTO> loader) {
        return loader.load(this.getApplicationId());
    }


    /**
     * 父菜单
     */
    @GraphQLGetter
    public CompletableFuture<MenuDTO> getParent(DataLoader<String, MenuDTO> loader) {
        return loader.load(this.getParentId());
    }

    /**
     * 子菜单
     */
    @GraphQLGetter
    public List<MenuDTO> getChildren(@Autowired MenuQuery query,
                                     @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return query.findBy(null, null, Conditions.of(MenuEntity.class).eq(MenuEntity::getParentId, this.getId()), Orders.of(MenuEntity.class).asc(MenuEntity::getOrder).asc(MenuEntity::getCode), tenant);
    }

    /**
     * 权限信息
     */
    @GraphQLGetter
    public List<PermissionDTO> getPermissions(@Autowired PermissionQuery query,
                                              @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return query.findBy(null, null, Conditions.of(PermissionEntity.class).eq(PermissionEntity::getMenuId, this.getId()), null, tenant);
    }

    /**
     * 创建人信息
     */
    @GraphQLGetter
    public CompletableFuture<AccountDTO> getCreator(DataLoader<String, AccountDTO> loader) {
        return loader.load(this.getCreatorId());
    }

    /**
     * 修改人信息
     */
    @GraphQLGetter
    public CompletableFuture<AccountDTO> getModifier(DataLoader<String, AccountDTO> loader) {
        return loader.load(this.getModifierId());
    }
}
