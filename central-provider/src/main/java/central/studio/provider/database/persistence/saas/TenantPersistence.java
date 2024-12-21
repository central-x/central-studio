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

package central.studio.provider.database.persistence.saas;

import central.bean.Page;
import central.data.saas.TenantInput;
import central.lang.Stringx;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.studio.provider.database.persistence.saas.entity.TenantApplicationEntity;
import central.studio.provider.database.persistence.saas.entity.TenantEntity;
import central.studio.provider.database.persistence.saas.mapper.TenantApplicationMapper;
import central.studio.provider.database.persistence.saas.mapper.TenantMapper;
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
 * Tenant Persistence
 * <p>
 * 租户持久化
 *
 * @author Alan Yeh
 * @since 2024/12/21
 */
@Component
public class TenantPersistence {

    @Setter(onMethod_ = @Autowired)
    private TenantMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private TenantApplicationMapper tenantApplicationMapper;

    /**
     * 根据主键查询数据
     *
     * @param id      主键
     * @param columns 字段列表
     */
    public @Nullable TenantEntity findById(@Nullable String id, @Nullable Columns<? extends TenantEntity> columns) {
        if (Stringx.isNullOrBlank(id)) {
            return null;
        }

        return this.mapper.findById(id, columns);
    }

    /**
     * 查询数据
     *
     * @param ids     主键
     * @param columns 字段列表
     */
    public @Nonnull List<TenantEntity> findByIds(@Nullable List<String> ids, @Nullable Columns<? extends TenantEntity> columns) {
        if (Listx.isNullOrEmpty(ids)) {
            return new ArrayList<>();
        }

        return this.mapper.findByIds(ids, columns);
    }

    /**
     * 查询数据
     *
     * @param limit      获取前 N 条数据
     * @param offset     偏移量
     * @param columns    字段列表
     * @param conditions 过滤条件
     * @param orders     排序条件
     */
    public @Nonnull List<TenantEntity> findBy(@Nullable Long limit, @Nullable Long offset, @Nullable Columns<? extends TenantEntity> columns, @Nullable Conditions<? extends TenantEntity> conditions, @Nullable Orders<? extends TenantEntity> orders) {
        return this.mapper.findBy(limit, offset, columns, conditions, orders);
    }

    /**
     * 查询第一条数据
     *
     * @param columns    字段列表
     * @param conditions 过滤条件
     * @param orders     排序条件
     */
    public @Nullable TenantEntity findFirstBy(@Nullable Columns<? extends TenantEntity> columns, @Nullable Conditions<? extends TenantEntity> conditions, @Nullable Orders<? extends TenantEntity> orders) {
        return this.mapper.findFirstBy(columns, conditions, orders);
    }

    /**
     * 分页查询数据
     *
     * @param pageIndex  分页下标
     * @param pageSize   分页大小
     * @param conditions 过滤条件
     * @param orders     排序条件
     */
    public @Nonnull Page<TenantEntity> findPageBy(long pageIndex, long pageSize, @Nullable Columns<? extends TenantEntity> columns, @Nullable Conditions<? extends TenantEntity> conditions, @Nullable Orders<? extends TenantEntity> orders) {
        return this.mapper.findPageBy(pageIndex, pageSize, columns, conditions, orders);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     */
    public long countBy(@Nullable Conditions<? extends TenantEntity> conditions) {
        return this.mapper.countBy(conditions);
    }

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作人
     */
    public @Nonnull TenantEntity insert(@Validated({Insert.class, Default.class}) TenantInput input, @Nonnull String operator) {
        // 标识唯一性校验
        if (this.mapper.existsBy(Conditions.of(TenantEntity.class).eq(TenantEntity::getCode, input.getCode()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在相同标识[code={}]的数据", input.getCode()));
        }

        var entity = new TenantEntity();
        entity.fromInput(input);
        entity.updateCreator(operator);
        this.mapper.insert(entity);

        return entity;
    }

    /**
     * 批量保存数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     */
    public @Nonnull List<TenantEntity> insertBatch(@Validated({Insert.class, Default.class}) List<TenantInput> inputs, @Nonnull String operator) {
        return Listx.asStream(inputs).map(it -> this.insert(it, operator)).toList();
    }

    /**
     * 更新数据
     *
     * @param input    数据输入
     * @param operator 操作人
     */
    public @Nonnull TenantEntity update(@Validated({Update.class, Default.class}) TenantInput input, @Nonnull String operator) {
        var entity = this.mapper.findFirstBy(Conditions.of(TenantEntity.class).eq(TenantEntity::getId, input.getId()));
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("数据[id={}]不存在", input.getId()));
        }

        // 标识唯一性校验
        if (!Objects.equals(entity.getCode(), input.getCode())) {
            if (this.mapper.existsBy(Conditions.of(TenantEntity.class).eq(TenantEntity::getCode, input.getCode()))) {
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
     */
    public @Nonnull List<TenantEntity> updateBatch(@Validated({Update.class, Default.class}) List<TenantInput> inputs, @Nonnull String operator) {
        return Listx.asStream(inputs).map(it -> this.update(it, operator)).toList();
    }

    /**
     * 根据主键删除数据
     *
     * @param ids 主键
     */
    public long deleteByIds(@Nullable List<String> ids) {
        if (Listx.isNullOrEmpty(ids)) {
            return 0;
        }

        // 删除关联关系
        this.tenantApplicationMapper.deleteBy(Conditions.of(TenantApplicationEntity.class).in(TenantApplicationEntity::getTenantId, ids));

        return this.mapper.deleteByIds(ids);
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     */
    public long deleteBy(@Nullable Conditions<? extends TenantEntity> conditions) {
        var entities = this.mapper.findBy(conditions);
        if (Listx.isNullOrEmpty(entities)) {
            return 0;
        }

        var ids = entities.stream().map(TenantEntity::getId).toList();
        return this.deleteByIds(ids);
    }
}
