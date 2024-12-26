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

package central.studio.provider.database.persistence.organization;

import central.bean.Page;
import central.data.organization.AccountInput;
import central.lang.Stringx;
import central.sql.data.Entity;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.studio.provider.ProviderProperties;
import central.studio.provider.database.persistence.organization.entity.AccountDepartmentEntity;
import central.studio.provider.database.persistence.organization.entity.AccountEntity;
import central.studio.provider.database.persistence.organization.entity.AccountUnitEntity;
import central.studio.provider.database.persistence.organization.mapper.AccountMapper;
import central.util.Listx;
import central.validation.group.Insert;
import central.validation.group.Update;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.groups.Default;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Account Persistence
 * <p>
 * 帐户持久化
 *
 * @author Alan Yeh
 * @since 2023/02/07
 */
@Component
public class AccountPersistence {

    @Setter(onMethod_ = @Autowired)
    private AccountMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private ProviderProperties properties;

    @Setter(onMethod_ = @Autowired)
    private AccountDepartmentPersistence accountDepartmentPersistence;

    @Setter(onMethod_ = @Autowired)
    private AccountUnitPersistence accountUnitPersistence;

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
     * 判断指定主键的帐户是否超级管理员
     *
     * @param accountId 帐户主键
     */
    public Boolean isSupervisor(String accountId) {
        return Objects.equals(this.properties.getSupervisor().getUsername(), accountId);
    }

    /**
     * 根据主键查询数据
     *
     * @param id      主键
     * @param columns 字段列表
     * @param tenant  租户标识
     */
    public @Nullable AccountEntity findById(@Nullable String id,
                                            @Nullable Columns<? extends AccountEntity> columns,
                                            @Nonnull String tenant) {
        if (Stringx.isNullOrBlank(id)) {
            return null;
        }

        if (Objects.equals(this.properties.getSupervisor().getUsername(), id)) {
            return this.getSupervisor(tenant);
        }

        return this.mapper.findFirstBy(columns, Conditions.of(AccountEntity.class).eq(AccountEntity::getId, id).eq(AccountEntity::getTenantCode, tenant));
    }

    /**
     * 查询数据
     *
     * @param ids     主键
     * @param columns 字段列表
     * @param tenant  租户标识
     */
    public @Nonnull List<AccountEntity> findByIds(@Nullable List<String> ids,
                                                  @Nullable Columns<? extends AccountEntity> columns,
                                                  @Nonnull String tenant) {
        if (Listx.isNullOrEmpty(ids)) {
            return new ArrayList<>();
        }

        var entities = this.mapper.findBy(columns, Conditions.of(AccountEntity.class).in(AccountEntity::getId, ids).eq(AccountEntity::getTenantCode, tenant));

        // 添加超级管理员数据，因此超级管理员的数据不在数据库里
        if (ids.contains(this.properties.getSupervisor().getUsername())) {
            entities.add(this.getSupervisor(tenant));
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
    public @Nonnull List<AccountEntity> findBy(@Nullable Long limit,
                                               @Nullable Long offset,
                                               @Nullable Columns<? extends AccountEntity> columns,
                                               @Nullable Conditions<? extends AccountEntity> conditions,
                                               @Nullable Orders<? extends AccountEntity> orders,
                                               @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(AccountEntity::getTenantCode, tenant);
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
    public @Nonnull Page<AccountEntity> pageBy(@Nonnull Long pageIndex,
                                               @Nonnull Long pageSize,
                                               @Nullable Columns<? extends AccountEntity> columns,
                                               @Nullable Conditions<? extends AccountEntity> conditions,
                                               @Nullable Orders<? extends AccountEntity> orders,
                                               @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(AccountEntity::getTenantCode, tenant);
        return this.mapper.findPageBy(pageIndex, pageSize, columns, conditions, orders);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    public Long countBy(@Nullable Conditions<? extends AccountEntity> conditions,
                        @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(AccountEntity::getTenantCode, tenant);
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
    public @Nonnull AccountEntity insert(@Validated({Insert.class, Default.class}) AccountInput input, @Nonnull String operator, @Nonnull String tenant) {
        // 帐号唯一性校验
        if (this.mapper.existsBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getUsername, input.getUsername()).eq(AccountEntity::getTenantCode, tenant))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在帐号[username={}]", input.getUsername()));
        }

        // 邮箱唯一性校验
        if (this.mapper.existsBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getEmail, input.getEmail()).eq(AccountEntity::getTenantCode, tenant))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在帐号[email={}]", input.getEmail()));
        }

        // 手机号唯一性校验
        if (this.mapper.existsBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getMobile, input.getMobile()).eq(AccountEntity::getTenantCode, tenant))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在帐号[mobile={}]", input.getMobile()));
        }

        var entity = new AccountEntity();
        entity.fromInput(input);
        entity.setAdmin(Boolean.FALSE);
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
    public @Nonnull List<AccountEntity> insertBatch(@Validated({Update.class, Default.class}) List<AccountInput> inputs, @Nonnull String operator, @Nonnull String tenant) {
        return Listx.asStream(inputs).map(it -> this.insert(it, operator, tenant)).toList();
    }

    /**
     * 更新数据
     *
     * @param input    数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    public @Nonnull AccountEntity update(@Validated({Update.class, Default.class}) AccountInput input, @Nonnull String operator, @Nonnull String tenant) {
        var entity = this.mapper.findFirstBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getId, input.getId()).eq(AccountEntity::getTenantCode, tenant));
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("数据[id={}]不存在", input.getId()));
        }

        // 帐号唯一性校验
        if (!Objects.equals(entity.getUsername(), input.getUsername())) {
            if (this.mapper.existsBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getUsername, input.getUsername()).eq(AccountEntity::getTenantCode, tenant))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在帐号[username={}]", input.getUsername()));
            }
        }

        // 邮箱唯一性校验
        if (Stringx.isNotBlank(input.getEmail()) && !Objects.equals(entity.getEmail(), input.getEmail())) {
            if (this.mapper.existsBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getEmail, input.getEmail()).eq(AccountEntity::getTenantCode, tenant))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在帐号[email={}]", input.getEmail()));
            }
        }

        // 手机号唯一性校验
        if (Stringx.isNotBlank(input.getMobile()) && !Objects.equals(entity.getMobile(), input.getMobile())) {
            if (this.mapper.existsBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getMobile, input.getMobile()).eq(AccountEntity::getTenantCode, tenant))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在帐号[mobile={}]", input.getMobile()));
            }
        }

        entity.fromInput(input);
        entity.updateModifier(operator);
        this.mapper.update(entity);

        return entity;
    }

    /**
     * 批量更新数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    public @Nonnull List<AccountEntity> updateBatch(@Validated({Update.class, Default.class}) List<AccountInput> inputs, @Nonnull String operator, @Nonnull String tenant) {
        return Listx.asStream(inputs).map(it -> this.update(it, operator, tenant)).toList();
    }

    /**
     * 根据主键删除数据
     *
     * @param ids    主键
     * @param tenant 租户标识
     * @return 受影响数据量
     */
    public long deleteByIds(@Nonnull List<String> ids, @Nonnull String tenant) {
        if (Listx.isNullOrEmpty(ids)) {
            return 0;
        }

        var effected = this.mapper.deleteBy(Conditions.of(AccountEntity.class).in(AccountEntity::getId, ids).eq(AccountEntity::getTenantCode, tenant));
        if (effected > 0L) {
            // 级联删除
            accountUnitPersistence.deleteBy(Conditions.of(AccountUnitEntity.class).in(AccountUnitEntity::getAccountId, ids), tenant);
            accountDepartmentPersistence.deleteBy(Conditions.of(AccountDepartmentEntity.class).in(AccountDepartmentEntity::getAccountId, ids), tenant);
        }
        return effected;
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     * @param tenant     租户标识
     * @return 受影响数据量
     */
    public long deleteBy(@Nonnull Conditions<? extends AccountEntity> conditions, @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(AccountEntity::getTenantCode, tenant);

        var ids = this.mapper.findBy(Columns.of(Entity::getId), conditions).stream()
                .map(Entity::getId).toList();
        return this.deleteByIds(ids, tenant);
    }
}
