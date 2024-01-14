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

package central.studio.provider.graphql.organization.service;

import central.bean.Page;
import central.studio.provider.ApplicationProperties;
import central.provider.graphql.DTO;
import central.studio.provider.graphql.organization.dto.AccountDTO;
import central.studio.provider.graphql.organization.entity.AccountEntity;
import central.studio.provider.graphql.organization.mapper.AccountMapper;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.util.Listx;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Account Service
 *
 * @author Alan Yeh
 * @since 2023/02/07
 */
@Component
public class AccountService {

    @Setter(onMethod_ = @Autowired)
    private AccountMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    /**
     * 获取超级管理员帐号信息
     *
     * @param tenant 租户标识
     */
    public @Nonnull AccountEntity getSupervisor(@Nonnull String tenant) {
        var sa = properties.getSupervisor();

        var supervisor = new AccountEntity();
        supervisor.setId(sa.getUsername());
        supervisor.setUsername(sa.getUsername());
        supervisor.setEmail(sa.getEmail());
        supervisor.setMobile(null);
        supervisor.setName(sa.getName());
        supervisor.setAvatar(sa.getAvatar());
        supervisor.setAdmin(Boolean.TRUE);
        supervisor.setEnabled(sa.getEnabled());
        supervisor.setDeleted(Boolean.FALSE);
        supervisor.setTenantCode(tenant);
        supervisor.updateCreator(sa.getUsername());
        return supervisor;
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
     * 根据主键查询数据
     *
     * @param id      主键
     * @param columns 字段列表
     * @param tenant  租户标识
     */
    public @Nullable AccountDTO findById(@Nullable String id,
                                         @Nullable Columns<AccountDTO> columns,
                                         @Nonnull String tenant) {
        if (Objects.equals(this.properties.getSupervisor().getUsername(), id)) {
            var entity = this.getSupervisor(tenant);
            return DTO.wrap(entity, AccountDTO.class);
        }

        var entity = this.mapper.findFirstBy(columns, Conditions.of(AccountEntity.class).eq(AccountEntity::getId, id).eq(AccountEntity::getTenantCode, tenant));
        return DTO.wrap(entity, AccountDTO.class);
    }

    /**
     * 查询数据
     *
     * @param ids     主键
     * @param columns 字段列表
     * @param tenant  租户标识
     */
    public @Nonnull List<AccountDTO> findByIds(@Nullable List<String> ids,
                                               @Nullable Columns<AccountDTO> columns,
                                               @Nonnull String tenant) {
        if (Listx.isNullOrEmpty(ids)) {
            return Collections.emptyList();
        }

        var entities = this.mapper.findBy(columns, Conditions.of(AccountEntity.class).in(AccountEntity::getId, ids).eq(AccountEntity::getTenantCode, tenant));

        // 添加超级管理员数据，因此超级管理员的数据不在数据库里
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
     * @param columns    字段列表
     * @param conditions 过滤条件
     * @param orders     排序条件
     * @param tenant     租户标识
     */
    public @Nonnull List<AccountDTO> findBy(@Nullable Long limit,
                                            @Nullable Long offset,
                                            @Nullable Columns<AccountDTO> columns,
                                            @Nullable Conditions<AccountDTO> conditions,
                                            @Nullable Orders<AccountDTO> orders,
                                            @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(AccountEntity::getTenantCode, tenant);
        var list = this.mapper.findBy(limit, offset, columns, conditions, orders);
        return DTO.wrap(list, AccountDTO.class);
    }

    /**
     * 分页查询数据
     *
     * @param pageIndex  分页下标
     * @param pageSize   分页大小
     * @param columns    字段列表
     * @param conditions 过滤条件
     * @param orders     排序条件
     * @param tenant     租户标识
     */
    public @Nonnull Page<AccountDTO> pageBy(@Nonnull Long pageIndex,
                                            @Nonnull Long pageSize,
                                            @Nullable Columns<AccountDTO> columns,
                                            @Nullable Conditions<AccountDTO> conditions,
                                            @Nullable Orders<AccountDTO> orders,
                                            @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(AccountEntity::getTenantCode, tenant);
        var page = this.mapper.findPageBy(pageIndex, pageSize, columns, conditions, orders);
        return DTO.wrap(page, AccountDTO.class);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    public Long countBy(@Nullable Conditions<AccountDTO> conditions,
                        @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(AccountEntity::getTenantCode, tenant);
        return this.mapper.countBy(conditions);
    }
}
