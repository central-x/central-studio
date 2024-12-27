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

package central.studio.provider.database.persistence.storage;

import central.bean.Page;
import central.data.storage.StorageBucketInput;
import central.lang.Stringx;
import central.sql.data.Entity;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.studio.provider.database.persistence.storage.entity.StorageBucketEntity;
import central.studio.provider.database.persistence.storage.entity.StorageObjectEntity;
import central.studio.provider.database.persistence.storage.mapper.StorageBucketMapper;
import central.studio.provider.database.persistence.storage.mapper.StorageObjectMapper;
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
 * Storage Bucket Persistence
 * <p>
 * 存储桶持久化
 *
 * @author Alan Yeh
 * @since 2024/12/27
 */
@Component
public class StorageBucketPersistence {

    @Setter(onMethod_ = @Autowired)
    private StorageBucketMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private StorageObjectMapper objectMapper;

    /**
     * 根据主键查询数据
     *
     * @param id      主键
     * @param columns 字段列表
     * @param tenant  租户标识
     */
    public @Nullable StorageBucketEntity findById(@Nullable String id,
                                                  @Nullable Columns<? extends StorageBucketEntity> columns,
                                                  @Nonnull String tenant) {

        if (Stringx.isNullOrBlank(id)) {
            return null;
        }

        var conditions = Conditions.of(StorageBucketEntity.class).eq(StorageBucketEntity::getId, id).eq(StorageBucketEntity::getTenantCode, tenant);
        return this.mapper.findFirstBy(columns, conditions);
    }

    /**
     * 查询数据
     *
     * @param ids     主键
     * @param columns 字段列表
     * @param tenant  租户标识
     */
    public @Nonnull List<StorageBucketEntity> findByIds(@Nullable List<String> ids,
                                                        @Nullable Columns<? extends StorageBucketEntity> columns,
                                                        @Nonnull String tenant) {
        if (Listx.isNullOrEmpty(ids)) {
            return Collections.emptyList();
        }

        var conditions = Conditions.of(StorageBucketEntity.class).in(StorageBucketEntity::getId, ids).eq(StorageBucketEntity::getTenantCode, tenant);
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
    public @Nonnull List<StorageBucketEntity> findBy(@Nullable Long limit,
                                                     @Nullable Long offset,
                                                     @Nullable Columns<? extends StorageBucketEntity> columns,
                                                     @Nullable Conditions<? extends StorageBucketEntity> conditions,
                                                     @Nullable Orders<? extends StorageBucketEntity> orders,
                                                     @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(StorageBucketEntity::getTenantCode, tenant);
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
    public @Nonnull Page<StorageBucketEntity> pageBy(@Nonnull Long pageIndex,
                                                     @Nonnull Long pageSize,
                                                     @Nullable Columns<? extends StorageBucketEntity> columns,
                                                     @Nullable Conditions<? extends StorageBucketEntity> conditions,
                                                     @Nullable Orders<? extends StorageBucketEntity> orders,
                                                     @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(StorageBucketEntity::getTenantCode, tenant);
        return this.mapper.findPageBy(pageIndex, pageSize, columns, conditions, orders);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    public Long countBy(@Nullable Conditions<? extends StorageBucketEntity> conditions,
                        @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(StorageBucketEntity::getTenantCode, tenant);
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
    public StorageBucketEntity insert(@Validated({Insert.class, Default.class}) StorageBucketInput input, @Nonnull String operator, @Nonnull String tenant) {
        // 标识唯一性校验
        if (this.mapper.existsBy(Conditions.of(StorageBucketEntity.class).eq(StorageBucketEntity::getCode, input.getCode()).eq(StorageBucketEntity::getTenantCode, tenant))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在相同标识[code={}]的数据", input.getCode()));
        }

        var entity = new StorageBucketEntity();
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
    public List<StorageBucketEntity> insertBatch(@Validated({Insert.class, Default.class}) List<StorageBucketInput> inputs, @Nonnull String operator, @Nonnull String tenant) {
        return Listx.asStream(inputs).map(it -> this.insert(it, operator, tenant)).toList();
    }

    /**
     * 更新数据
     *
     * @param input    数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    public StorageBucketEntity update(@Validated({Update.class, Default.class}) StorageBucketInput input, @Nonnull String operator, @Nonnull String tenant) {
        var entity = this.mapper.findFirstBy(Conditions.of(StorageBucketEntity.class).eq(StorageBucketEntity::getId, input.getId()).eq(StorageBucketEntity::getTenantCode, tenant));
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("数据[id={}]不存在", input.getId()));
        }

        // 标识唯一性校验
        if (!Objects.equals(entity.getCode(), input.getCode())) {
            if (this.mapper.existsBy(Conditions.of(StorageBucketEntity.class).eq(StorageBucketEntity::getCode, input.getCode()))) {
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
    public List<StorageBucketEntity> updateBatch(@Validated({Update.class, Default.class}) List<StorageBucketInput> inputs, @Nonnull String operator, @Nonnull String tenant) {
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
        var conditions = Conditions.of(StorageBucketEntity.class).in(StorageBucketEntity::getId, ids).eq(StorageBucketEntity::getTenantCode, tenant);
        var effected = this.mapper.deleteBy(conditions);

        if (effected > 0) {
            this.objectMapper.deleteBy(Conditions.of(StorageObjectEntity.class).in(StorageObjectEntity::getBucketId, ids));
        }

        return effected;
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     * @param tenant     租户标识
     */
    public long deleteBy(@Nullable Conditions<? extends StorageBucketEntity> conditions, @Nonnull String tenant) {
        var ids = this.mapper.findBy(Columns.of(Entity::getId), Conditions.group(conditions).eq(StorageBucketEntity::getTenantCode, tenant)).stream()
                .map(Entity::getId).toList();
        return this.deleteByIds(ids, tenant);
    }
}
