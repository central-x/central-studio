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

package central.studio.provider.database.persistence.system;

import central.bean.Page;
import central.data.system.DictionaryInput;
import central.lang.Stringx;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.studio.provider.database.persistence.system.entity.DictionaryEntity;
import central.studio.provider.database.persistence.system.mapper.DictionaryMapper;
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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Dictionary Persistence
 * <p>
 * 字典持久化
 *
 * @author Alan Yeh
 * @since 2024/12/21
 */
@Component
public class DictionaryPersistence {

    @Setter(onMethod_ = @Autowired)
    private DictionaryMapper mapper;

    /**
     * 根据主键查询数据
     *
     * @param id      主键
     * @param columns 字段列表
     * @param tenant  租户标识
     */
    public @Nullable DictionaryEntity findById(@Nullable String id,
                                               @Nullable Columns<? extends DictionaryEntity> columns,
                                               @Nonnull String tenant) {

        if (Stringx.isNullOrBlank(id)) {
            return null;
        }

        var conditions = Conditions.of(DictionaryEntity.class).eq(DictionaryEntity::getId, id).eq(DictionaryEntity::getTenantCode, tenant);
        return this.mapper.findFirstBy(columns, conditions);
    }

    /**
     * 查询数据
     *
     * @param ids     主键
     * @param columns 字段列表
     * @param tenant  租户标识
     */
    public @Nonnull List<DictionaryEntity> findByIds(@Nullable List<String> ids,
                                                     @Nullable Columns<? extends DictionaryEntity> columns,
                                                     @Nonnull String tenant) {
        if (Listx.isNullOrEmpty(ids)) {
            return Collections.emptyList();
        }

        var conditions = Conditions.of(DictionaryEntity.class).in(DictionaryEntity::getId, ids).eq(DictionaryEntity::getTenantCode, tenant);
        return this.mapper.findBy(columns, conditions);
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
    public @Nonnull List<DictionaryEntity> findBy(@Nullable Long limit,
                                                  @Nullable Long offset,
                                                  @Nullable Columns<? extends DictionaryEntity> columns,
                                                  @Nullable Conditions<? extends DictionaryEntity> conditions,
                                                  @Nullable Orders<? extends DictionaryEntity> orders,
                                                  @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(DictionaryEntity::getTenantCode, tenant);
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
    public @Nonnull Page<DictionaryEntity> pageBy(@Nonnull Long pageIndex,
                                                  @Nonnull Long pageSize,
                                                  @Nullable Columns<? extends DictionaryEntity> columns,
                                                  @Nullable Conditions<? extends DictionaryEntity> conditions,
                                                  @Nullable Orders<? extends DictionaryEntity> orders,
                                                  @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(DictionaryEntity::getTenantCode, tenant);
        return this.mapper.findPageBy(pageIndex, pageSize, columns, conditions, orders);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    public Long countBy(@Nullable Conditions<? extends DictionaryEntity> conditions,
                        @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(DictionaryEntity::getTenantCode, tenant);
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
    public DictionaryEntity insert(@Validated({Insert.class, Default.class}) DictionaryInput input, @Nonnull String operator, @Nonnull String tenant) {
        // 标识唯一性校验
        if (this.mapper.existsBy(Conditions.of(DictionaryEntity.class).eq(DictionaryEntity::getCode, input.getCode()).eq(DictionaryEntity::getTenantCode, tenant))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在相同标识[code={}]的数据", input.getCode()));
        }

        var entity = new DictionaryEntity();
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
    public List<DictionaryEntity> insertBatch(@Validated({Insert.class, Default.class}) List<DictionaryInput> inputs, @Nonnull String operator, @Nonnull String tenant) {
        return Listx.asStream(inputs).map(it -> this.insert(it, operator, tenant)).toList();
    }

    /**
     * 更新数据
     *
     * @param input    数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    public DictionaryEntity update(@Validated({Update.class, Default.class}) DictionaryInput input, @Nonnull String operator, @Nonnull String tenant) {
        var entity = this.mapper.findFirstBy(Conditions.of(DictionaryEntity.class).eq(DictionaryEntity::getId, input.getId()).eq(DictionaryEntity::getTenantCode, tenant));
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("数据[id={}]不存在", input.getId()));
        }

        // 标识唯一性校验
        if (!Objects.equals(entity.getCode(), input.getCode())) {
            if (this.mapper.existsBy(Conditions.of(DictionaryEntity.class).eq(DictionaryEntity::getCode, input.getCode()).eq(DictionaryEntity::getTenantCode, tenant))) {
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
    public List<DictionaryEntity> updateBatch(@Validated({Update.class, Default.class}) List<DictionaryInput> inputs, @Nonnull String operator, @Nonnull String tenant) {
        return Listx.asStream(inputs).map(it -> this.update(it, operator, tenant)).toList();
    }

    /**
     * 根据主键删除数据
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    public long deleteByIds(@Nullable List<String> ids, @Nonnull String tenant) {
        if (Listx.isNullOrEmpty(ids)) {
            return 0;
        }

        return this.mapper.deleteBy(Conditions.of(DictionaryEntity.class).in(DictionaryEntity::getId, ids).eq(DictionaryEntity::getTenantCode, tenant));
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     * @param tenant     租户标识
     */
    public long deleteBy(@Nullable Conditions<? extends DictionaryEntity> conditions, @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(DictionaryEntity::getTenantCode, tenant);
        return this.mapper.deleteBy(conditions);
    }
}
