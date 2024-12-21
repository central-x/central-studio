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

package central.studio.provider.graphql.organization.dto;

import central.provider.graphql.DTO;
import central.studio.provider.database.persistence.organization.AccountPersistence;
import central.studio.provider.graphql.authority.dto.RoleDTO;
import central.studio.provider.database.persistence.authority.RolePersistence;
import central.studio.provider.database.persistence.organization.entity.AccountEntity;
import central.studio.provider.database.persistence.organization.entity.AccountUnitEntity;
import central.studio.provider.graphql.organization.query.AccountUnitQuery;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serial;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 帐户信息
 *
 * @author Alan Yeh
 * @since 2022/09/24
 */
@GraphQLType("Account")
@EqualsAndHashCode(callSuper = true)
public class AccountDTO extends AccountEntity implements DTO {
    @Serial
    private static final long serialVersionUID = -4014716766819431471L;

    /**
     * 是否超级管理员
     */
    @GraphQLGetter
    public Boolean getSupervisor(@Autowired AccountPersistence persistence) {
        return persistence.isSupervisor(this.getId());
    }

    /**
     * 。
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

    /**
     * 获取所属单位信息
     */
    @GraphQLGetter
    public List<AccountUnitDTO> getUnits(@RequestParam(required = false) Long first,
                                         @RequestParam(required = false) Long offset,
                                         @RequestParam Conditions<AccountUnitDTO> conditions,
                                         @RequestParam Orders<AccountUnitDTO> orders,
                                         @RequestHeader(XForwardedHeaders.TENANT) String tenant,
                                         @Autowired AccountUnitQuery query) {
        return query.findBy(first, offset, Conditions.group(conditions).eq(AccountUnitEntity::getAccountId, this.getId()), orders, tenant);
    }

    /**
     * 获取角色信息
     */
    @GraphQLGetter
    public List<RoleDTO> getRoles(DataFetchingEnvironment environment,
                                  @RequestParam(required = false) Long first,
                                  @RequestParam(required = false) Long offset,
                                  @RequestParam Conditions<RoleDTO> conditions,
                                  @RequestParam Orders<RoleDTO> orders,
                                  @RequestHeader(XForwardedHeaders.TENANT) String tenant,
                                  @Autowired RolePersistence persistence) {
        var columns = Columns.of(RoleDTO.class, environment.getSelectionSet().getFields().stream()
                .filter(it -> "roles".equals(it.getParentField().getName()))
                .map(SelectedField::getName).toList().toArray(new String[0]));

        var data = persistence.findBy(first, offset, columns, Conditions.group(conditions).eq("account.id", this.getId()), orders, tenant);
        return DTO.wrap(data, RoleDTO.class);
    }
}
