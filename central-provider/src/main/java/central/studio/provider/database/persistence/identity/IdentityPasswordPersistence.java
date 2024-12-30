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

package central.studio.provider.database.persistence.identity;

import central.bean.Page;
import central.data.identity.IdentityPasswordInput;
import central.security.Passwordx;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.studio.provider.ProviderProperties;
import central.studio.provider.database.persistence.identity.entity.IdentityPasswordEntity;
import central.studio.provider.database.persistence.identity.mapper.IdentityPasswordMapper;
import central.util.Listx;
import central.validation.group.Insert;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.groups.Default;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Identity Password Persistence
 * <p>
 * 帐户认证密码持久化
 *
 * @author Alan Yeh
 * @since 2024/12/24
 */
@Component
public class IdentityPasswordPersistence {

    @Setter(onMethod_ = @Autowired)
    private IdentityPasswordMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private ProviderProperties properties;

    /**
     * 根据主键查询数据
     *
     * @param id      主键
     * @param columns 字段列表
     * @param tenant  租户标识
     */
    public @Nullable IdentityPasswordEntity findById(@Nullable String id,
                                                     @Nullable Columns<? extends IdentityPasswordEntity> columns,
                                                     @Nonnull String tenant) {
        if (Objects.equals(properties.getSupervisor().getUsername(), id)) {
            return getSupervisorPassword(tenant);
        } else {
            return this.mapper.findFirstBy(Conditions.of(IdentityPasswordEntity.class).eq(IdentityPasswordEntity::getId, id).eq(IdentityPasswordEntity::getTenantCode, tenant));
        }
    }

    /**
     * 获取超级管理员密码
     */
    private IdentityPasswordEntity getSupervisorPassword(String tenant) {
        var password = new IdentityPasswordEntity();
        password.setId(properties.getSupervisor().getUsername());
        password.setAccountId(properties.getSupervisor().getUsername());
        password.setValue(Passwordx.encrypt(Passwordx.digest(properties.getSupervisor().getPassword())));
        password.setTenantCode(tenant);
        password.updateCreator(properties.getSupervisor().getUsername());
        return password;
    }

    /**
     * 查询数据
     *
     * @param ids     主键
     * @param columns 字段列表
     * @param tenant  租户标识
     */
    public @Nonnull List<IdentityPasswordEntity> findByIds(@Nullable List<String> ids,
                                                           @Nullable Columns<? extends IdentityPasswordEntity> columns,
                                                           @Nonnull String tenant) {
        if (Listx.isNullOrEmpty(ids)) {
            return new ArrayList<>();
        }

        var entities = this.mapper.findBy(Conditions.of(IdentityPasswordEntity.class).in(IdentityPasswordEntity::getId, ids).eq(IdentityPasswordEntity::getTenantCode, tenant));
        if (ids.contains(this.properties.getSupervisor().getUsername())) {
            entities.add(this.getSupervisorPassword(tenant));
        }
        return entities;
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
    public @Nonnull List<IdentityPasswordEntity> findBy(@Nullable Long limit,
                                                        @Nullable Long offset,
                                                        @Nullable Columns<? extends IdentityPasswordEntity> columns,
                                                        @Nullable Conditions<? extends IdentityPasswordEntity> conditions,
                                                        @Nullable Orders<? extends IdentityPasswordEntity> orders,
                                                        @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(IdentityPasswordEntity::getTenantCode, tenant);
        return this.mapper.findBy(limit, offset, columns, conditions, orders);
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
    public @Nonnull Page<IdentityPasswordEntity> pageBy(@Nonnull Long pageIndex,
                                                        @Nonnull Long pageSize,
                                                        @Nullable Columns<? extends IdentityPasswordEntity> columns,
                                                        @Nullable Conditions<? extends IdentityPasswordEntity> conditions,
                                                        @Nullable Orders<? extends IdentityPasswordEntity> orders,
                                                        @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(IdentityPasswordEntity::getTenantCode, tenant);
        return this.mapper.findPageBy(pageIndex, pageSize, columns, conditions, orders);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    public Long countBy(@Nullable Conditions<? extends IdentityPasswordEntity> conditions,
                        @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(IdentityPasswordEntity::getTenantCode, tenant);
        return this.mapper.countBy(conditions);
    }

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作帐号
     * @param tenant   租户标识
     * @return 保存后的数据
     */
    public IdentityPasswordEntity insert(@Nonnull @Validated({Insert.class, Default.class}) IdentityPasswordInput input,
                                         @Nonnull String operator,
                                         @Nonnull String tenant) {
        var entity = new IdentityPasswordEntity();
        entity.fromInput(input);
        entity.setTenantCode(tenant);
        entity.updateCreator(operator);
        this.mapper.insert(entity);

        return entity;
    }

    /**
     * 批量保存数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    public List<IdentityPasswordEntity> insertBatch(@Nullable @Validated({Insert.class, Default.class}) List<IdentityPasswordInput> inputs,
                                                    @Nonnull String operator,
                                                    @Nonnull String tenant) {
        return Listx.asStream(inputs).map(it -> this.insert(it, operator, tenant)).toList();
    }

    /**
     * 根据主键删除数据
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    public long deleteByIds(@Nullable List<String> ids,
                            @Nonnull String tenant) {
        if (Listx.isNullOrEmpty(ids)) {
            return 0;
        }

        return this.mapper.deleteBy(Conditions.of(IdentityPasswordEntity.class).in(IdentityPasswordEntity::getId, ids).eq(IdentityPasswordEntity::getTenantCode, tenant));
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     * @param tenant     租户标识
     */
    public long deleteBy(@Nullable Conditions<? extends IdentityPasswordEntity> conditions,
                         @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(IdentityPasswordEntity::getTenantCode, tenant);
        return this.mapper.deleteBy(conditions);
    }
}
