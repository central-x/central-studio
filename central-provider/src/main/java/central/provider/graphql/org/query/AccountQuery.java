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

package central.provider.graphql.org.query;

import central.api.DTO;
import central.bean.Page;
import central.provider.ApplicationProperties;
import central.provider.graphql.org.dto.AccountDTO;
import central.provider.graphql.org.entity.AccountEntity;
import central.provider.graphql.org.mapper.AccountMapper;
import central.sql.Conditions;
import central.sql.Orders;
import central.starter.graphql.annotation.GraphQLBatchLoader;
import central.starter.graphql.annotation.GraphQLFetcher;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLSchema;
import central.web.XForwardedHeaders;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Account Query
 * 帐户查询
 *
 * @author Alan Yeh
 * @since 2022/10/03
 */
@Component
@GraphQLSchema(path = "org/query", types = {AccountDTO.class, AccountUnitQuery.class})
public class AccountQuery {
    @Setter(onMethod_ = @Autowired)
    private AccountMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    /**
     * 获取超级管理员帐号信息
     *
     * @param tenant 租户标识
     */
    public AccountEntity getSupervisor(String tenant) {
        var sa = properties.getSupervisor();

        var superAdmin = new AccountEntity();
        superAdmin.setId(sa.getUsername());
        superAdmin.setUsername(sa.getUsername());
        superAdmin.setEmail(sa.getEmail());
        superAdmin.setMobile(null);
        superAdmin.setName(sa.getName());
        superAdmin.setAvatar(sa.getAvatar());
        superAdmin.setAdmin(Boolean.TRUE);
        superAdmin.setEnabled(sa.getEnabled());
        superAdmin.setDeleted(Boolean.FALSE);
        superAdmin.setTenantCode(tenant);
        superAdmin.updateCreator(sa.getUsername());
        return superAdmin;
    }

    /**
     * 判断指定主键的帐号是否超级管理员
     *
     * @param id 主键
     */
    public Boolean isSupervisor(String id) {
        return Objects.equals(this.properties.getSupervisor().getUsername(), id);
    }

    /**
     * 批量数据加载器
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    @GraphQLBatchLoader
    public @Nonnull Map<String, AccountDTO> batchLoader(@RequestParam Set<String> ids,
                                                        @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var result = this.mapper.findBy(Conditions.of(AccountEntity.class).in(AccountEntity::getId, ids).eq(AccountEntity::getTenantCode, tenant))
                .stream()
                .map(it -> DTO.wrap(it, AccountDTO.class))
                .collect(Collectors.toMap(AccountDTO::getId, it -> it));
        // 添加超级管理员数据，因此超级管理员的数据不在数据库里
        if (ids.contains(this.properties.getSupervisor().getUsername())) {
            result.put(this.properties.getSupervisor().getUsername(), DTO.wrap(this.getSupervisor(tenant), AccountDTO.class));
        }
        return result;
    }

    /**
     * 查询数据
     *
     * @param id     主键
     * @param tenant 租户标识
     */
    @GraphQLFetcher
    public @Nullable AccountDTO findById(@RequestParam String id,
                                         @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        if (Objects.equals(this.properties.getSupervisor().getUsername(), id)) {
            var entity = this.getSupervisor(tenant);
            return DTO.wrap(entity, AccountDTO.class);
        }

        var entity = this.mapper.findFirstBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getId, id).eq(AccountEntity::getTenantCode, tenant));
        return DTO.wrap(entity, AccountDTO.class);
    }

    /**
     * 查询数据
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<AccountDTO> findByIds(@RequestParam List<String> ids,
                                               @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var entities = this.mapper.findBy(Conditions.of(AccountEntity.class).in(AccountEntity::getId, ids).eq(AccountEntity::getTenantCode, tenant));
        if (ids.contains(this.properties.getSupervisor().getUsername())) {
            entities.add(this.getSupervisor(tenant));
        }
        return DTO.wrap(entities, AccountDTO.class);
    }

    /**
     * 查询数据
     *
     * @param limit      获取前 N 条数据
     * @param offset     偏移量
     * @param conditions 过滤条件
     * @param orders     排序条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<AccountDTO> findBy(@RequestParam(required = false) Long limit,
                                            @RequestParam(required = false) Long offset,
                                            @RequestParam Conditions<AccountEntity> conditions,
                                            @RequestParam Orders<AccountEntity> orders,
                                            @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(AccountEntity::getTenantCode, tenant);
        var list = this.mapper.findBy(limit, offset, conditions, orders);
        return DTO.wrap(list, AccountDTO.class);
    }

    /**
     * 分页查询数据
     *
     * @param pageIndex  分页下标
     * @param pageSize   分页大小
     * @param conditions 过滤条件
     * @param orders     排序条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public @Nonnull Page<AccountDTO> pageBy(@RequestParam long pageIndex,
                                            @RequestParam long pageSize,
                                            @RequestParam Conditions<AccountEntity> conditions,
                                            @RequestParam Orders<AccountEntity> orders,
                                            @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(AccountEntity::getTenantCode, tenant);
        var page = this.mapper.findPageBy(pageIndex, pageSize, conditions, orders);
        return DTO.wrap(page, AccountDTO.class);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public Long countBy(@RequestParam Conditions<AccountEntity> conditions,
                        @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(AccountEntity::getTenantCode, tenant);
        return this.mapper.countBy(conditions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 关联查询
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Account Unit Query
     * 帐户与单位关联关系查询
     */
    @GraphQLGetter
    public AccountUnitQuery getUnits(@Autowired AccountUnitQuery query) {
        return query;
    }
}
