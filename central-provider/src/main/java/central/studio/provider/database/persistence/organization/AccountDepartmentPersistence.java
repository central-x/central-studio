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
import central.data.organization.AccountDepartmentInput;
import central.lang.Stringx;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.studio.provider.database.persistence.organization.entity.AccountDepartmentEntity;
import central.studio.provider.database.persistence.organization.mapper.AccountDepartmentMapper;
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

/**
 * AccountDepartment Persistence
 * <p>
 * 帐户部门关联关系持久化
 *
 * @author Alan Yeh
 * @since 2024/12/21
 */
@Component
public class AccountDepartmentPersistence {

    @Setter(onMethod_ = @Autowired)
    private AccountDepartmentMapper mapper;

    /**
     * 根据主键查询数据
     *
     * @param id     主键
     * @param tenant 租户标识
     */
    public @Nullable AccountDepartmentEntity findById(@Nullable String id,
                                                      @Nonnull Columns<? extends AccountDepartmentEntity> columns,
                                                      @Nonnull String tenant) {
        if (Stringx.isNullOrBlank(id)) {
            return null;
        }

        var conditions = Conditions.of(AccountDepartmentEntity.class).eq(AccountDepartmentEntity::getId, id).eq(AccountDepartmentEntity::getTenantCode, tenant);
        return this.mapper.findFirstBy(columns, conditions);
    }

    /**
     * 查询数据
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    public @Nonnull List<AccountDepartmentEntity> findByIds(@Nullable List<String> ids,
                                                            @Nonnull Columns<? extends AccountDepartmentEntity> columns,
                                                            @Nonnull String tenant) {
        if (Listx.isNullOrEmpty(ids)) {
            return new ArrayList<>();
        }
        return this.mapper.findBy(columns, Conditions.of(AccountDepartmentEntity.class).in(AccountDepartmentEntity::getId, ids).eq(AccountDepartmentEntity::getTenantCode, tenant));
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
    public @Nonnull List<AccountDepartmentEntity> findBy(@Nullable Long limit,
                                                         @Nullable Long offset,
                                                         @Nullable Columns<? extends AccountDepartmentEntity> columns,
                                                         @Nullable Conditions<? extends AccountDepartmentEntity> conditions,
                                                         @Nullable Orders<? extends AccountDepartmentEntity> orders,
                                                         @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(AccountDepartmentEntity::getTenantCode, tenant);
        return this.mapper.findBy(limit, offset, columns, conditions, orders);
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
    public @Nonnull Page<AccountDepartmentEntity> pageBy(long pageIndex,
                                                         long pageSize,
                                                         @Nullable Columns<? extends AccountDepartmentEntity> columns,
                                                         @Nullable Conditions<? extends AccountDepartmentEntity> conditions,
                                                         @Nullable Orders<? extends AccountDepartmentEntity> orders,
                                                         @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(AccountDepartmentEntity::getTenantCode, tenant);
        return this.mapper.findPageBy(pageIndex, pageSize, columns, conditions, orders);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    public Long countBy(@Nullable Conditions<? extends AccountDepartmentEntity> conditions,
                        @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(AccountDepartmentEntity::getTenantCode, tenant);
        return this.mapper.countBy(conditions);
    }

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    public AccountDepartmentEntity insert(@Nonnull @Validated({Insert.class, Default.class}) AccountDepartmentInput input,
                                          @Nonnull String operator,
                                          @Nonnull String tenant) {
        var entity = new AccountDepartmentEntity();
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
    public @Nonnull List<AccountDepartmentEntity> insertBatch(@Nullable @Validated({Insert.class, Default.class}) List<AccountDepartmentInput> inputs,
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

        return this.mapper.deleteBy(Conditions.of(AccountDepartmentEntity.class).in(AccountDepartmentEntity::getId, ids).eq(AccountDepartmentEntity::getTenantCode, tenant));
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     * @param tenant     租户标识
     */
    public long deleteBy(@Nullable Conditions<? extends AccountDepartmentEntity> conditions,
                         @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(AccountDepartmentEntity::getTenantCode, tenant);
        return this.mapper.deleteBy(conditions);
    }
}
