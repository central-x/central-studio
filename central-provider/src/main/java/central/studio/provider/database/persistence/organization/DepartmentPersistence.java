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
import central.data.organization.DepartmentInput;
import central.lang.Stringx;
import central.sql.data.Entity;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.studio.provider.database.persistence.organization.entity.AccountDepartmentEntity;
import central.studio.provider.database.persistence.organization.entity.DepartmentEntity;
import central.studio.provider.database.persistence.organization.mapper.DepartmentMapper;
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
 * Department Persistence
 * <p>
 * 部门持久化
 *
 * @author Alan Yeh
 * @since 2024/12/21
 */
@Component
public class DepartmentPersistence {

    @Setter(onMethod_ = @Autowired)
    private DepartmentMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private AccountDepartmentPersistence accountDepartmentPersistence;

    /**
     * 根据主键查询数据
     *
     * @param id      主键
     * @param columns 查询字段
     * @param tenant  租户标识
     */
    public @Nullable DepartmentEntity findById(@Nullable String id,
                                               @Nonnull Columns<? extends DepartmentEntity> columns,
                                               @Nonnull String tenant) {
        if (Stringx.isNullOrBlank(id)) {
            return null;
        }

        return this.mapper.findFirstBy(columns, Conditions.of(DepartmentEntity.class).eq(DepartmentEntity::getId, id).eq(DepartmentEntity::getTenantCode, tenant));
    }

    /**
     * 查询数据
     *
     * @param ids     主键
     * @param columns 查询字段
     * @param tenant  租户标识
     */
    public @Nonnull List<DepartmentEntity> findByIds(@Nullable List<String> ids,
                                                     @Nonnull Columns<? extends DepartmentEntity> columns,
                                                     @Nonnull String tenant) {
        if (Listx.isNullOrEmpty(ids)) {
            return new ArrayList<>();
        }

        return this.mapper.findBy(columns, Conditions.of(DepartmentEntity.class).in(DepartmentEntity::getId, ids).eq(DepartmentEntity::getTenantCode, tenant));
    }

    /**
     * 查询数据
     *
     * @param limit      获取前 N 条数据
     * @param offset     偏移量
     * @param columns    查询字段
     * @param conditions 过滤条件
     * @param orders     排序条件
     * @param tenant     租户标识
     */
    public @Nonnull List<DepartmentEntity> findBy(@Nullable Long limit,
                                                  @Nullable Long offset,
                                                  @Nonnull Columns<? extends DepartmentEntity> columns,
                                                  @Nullable Conditions<? extends DepartmentEntity> conditions,
                                                  @Nullable Orders<? extends DepartmentEntity> orders,
                                                  @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(DepartmentEntity::getTenantCode, tenant);
        return this.mapper.findBy(limit, offset, columns, conditions, orders);
    }

    /**
     * 分页查询数据
     *
     * @param pageIndex  分页下标
     * @param pageSize   分页大小
     * @param columns    查询字段
     * @param conditions 过滤条件
     * @param orders     排序条件
     * @param tenant     租户标识
     */
    public @Nonnull Page<DepartmentEntity> pageBy(long pageIndex,
                                                  long pageSize,
                                                  @Nonnull Columns<? extends DepartmentEntity> columns,
                                                  @Nullable Conditions<? extends DepartmentEntity> conditions,
                                                  @Nullable Orders<? extends DepartmentEntity> orders,
                                                  @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(DepartmentEntity::getTenantCode, tenant);
        return this.mapper.findPageBy(pageIndex, pageSize, columns, conditions, orders);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    public Long countBy(@Nullable Conditions<? extends DepartmentEntity> conditions,
                        @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(DepartmentEntity::getTenantCode, tenant);
        return this.mapper.countBy(conditions);
    }

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    public @Nonnull DepartmentEntity insert(@Nonnull @Validated({Insert.class, Default.class}) DepartmentInput input,
                                            @Nonnull String operator,
                                            @Nonnull String tenant) {
        // 标识唯一性校验
        if (this.mapper.existsBy(Conditions.of(DepartmentEntity.class).eq(DepartmentEntity::getCode, input.getCode()).eq(DepartmentEntity::getTenantCode, tenant))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在相同标识[code={}]的数据", input.getCode()));
        }

        var entity = new DepartmentEntity();
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
    public @Nonnull List<DepartmentEntity> insertBatch(@Nullable @Validated({Insert.class, Default.class}) List<DepartmentInput> inputs,
                                                       @Nonnull String operator,
                                                       @Nonnull String tenant) {
        return Listx.asStream(inputs).map(it -> this.insert(it, operator, tenant)).toList();
    }

    /**
     * 更新数据
     *
     * @param input    数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    public @Nonnull DepartmentEntity update(@Nonnull @Validated({Update.class, Default.class}) DepartmentInput input,
                                            @Nonnull String operator,
                                            @Nonnull String tenant) {
        var entity = this.mapper.findFirstBy(Conditions.of(DepartmentEntity.class).eq(DepartmentEntity::getId, input.getId()).eq(DepartmentEntity::getTenantCode, tenant));
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("数据[id={}]不存在", input.getId()));
        }

        // 标识唯一性校验
        if (!Objects.equals(entity.getCode(), input.getCode())) {
            if (this.mapper.existsBy(Conditions.of(DepartmentEntity.class).eq(DepartmentEntity::getCode, input.getCode()).eq(DepartmentEntity::getTenantCode, tenant))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在相同标识[code={}]的数据", input.getCode()));
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
    public @Nonnull List<DepartmentEntity> updateBatch(@Nullable @Validated({Update.class, Default.class}) List<DepartmentInput> inputs,
                                                       @Nonnull String operator,
                                                       @Nonnull String tenant) {
        return Listx.asStream(inputs).map(it -> this.update(it, operator, tenant)).toList();
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

        var effected = this.mapper.deleteBy(Conditions.of(DepartmentEntity.class).in(DepartmentEntity::getId, ids).eq(DepartmentEntity::getTenantCode, tenant));

        if (effected > 0L) {
            // 级联删除
            accountDepartmentPersistence.deleteBy(Conditions.of(AccountDepartmentEntity.class).in(AccountDepartmentEntity::getDepartmentId, ids), tenant);
        }
        return effected;
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     * @param tenant     租户标识
     */
    public long deleteBy(@Nullable Conditions<? extends DepartmentEntity> conditions,
                         @Nonnull String tenant) {
        var ids = this.mapper.findBy(Columns.of(Entity::getId), Conditions.group(conditions).eq(DepartmentEntity::getTenantCode, tenant)).stream()
                .map(Entity::getId).toList();
        return this.deleteByIds(ids, tenant);
    }
}
