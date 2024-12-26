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

package central.studio.provider.database.persistence.log;

import central.bean.Page;
import central.data.log.LogFilterInput;
import central.lang.Stringx;
import central.sql.data.Entity;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.studio.provider.database.persistence.log.entity.*;
import central.studio.provider.database.persistence.log.mapper.LogCollectorFilterMapper;
import central.studio.provider.database.persistence.log.mapper.LogFilterMapper;
import central.studio.provider.database.persistence.log.mapper.LogStorageFilterMapper;
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

import java.util.*;

/**
 * Log Filter Persistence
 * <p>
 * 日志过滤器持久化
 *
 * @author Alan Yeh
 * @since 2024/12/26
 */
@Component
public class LogFilterPersistence {

    @Setter(onMethod_ = @Autowired)
    private LogFilterMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private LogCollectorFilterMapper collectorRelMapper;

    @Setter(onMethod_ = @Autowired)
    private LogStorageFilterMapper storageRelMapper;

    @Setter(onMethod_ = @Autowired)
    private LogCollectorPersistence collectorPersistence;

    @Setter(onMethod_ = @Autowired)
    private LogStoragePersistence storagePersistence;

    /**
     * 根据主键查询数据
     *
     * @param id      主键
     * @param columns 字段列表
     */
    public @Nullable LogFilterEntity findById(@Nullable String id,
                                              @Nullable Columns<? extends LogFilterEntity> columns) {
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
    public @Nonnull List<LogFilterEntity> findByIds(@Nullable List<String> ids,
                                                    @Nullable Columns<? extends LogFilterEntity> columns) {
        if (Listx.isNullOrEmpty(ids)) {
            return Collections.emptyList();
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
    public @Nonnull List<LogFilterEntity> findBy(@Nullable Long limit,
                                                 @Nullable Long offset,
                                                 @Nullable Columns<? extends LogFilterEntity> columns,
                                                 @Nullable Conditions<? extends LogFilterEntity> conditions,
                                                 @Nullable Orders<? extends LogFilterEntity> orders) {
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
     */
    public @Nonnull Page<LogFilterEntity> pageBy(@Nonnull Long pageIndex,
                                                 @Nonnull Long pageSize,
                                                 @Nullable Columns<? extends LogFilterEntity> columns,
                                                 @Nullable Conditions<? extends LogFilterEntity> conditions,
                                                 @Nullable Orders<? extends LogFilterEntity> orders) {
        return this.mapper.findPageBy(pageIndex, pageSize, columns, conditions, orders);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     */
    public Long countBy(@Nullable Conditions<? extends LogFilterEntity> conditions) {
        return this.mapper.countBy(conditions);
    }

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作帐号
     * @return 保存后的数据
     */
    public LogFilterEntity insert(@Validated({Insert.class, Default.class}) LogFilterInput input, @Nonnull String operator) {
        // 标识唯一性校验
        if (this.mapper.existsBy(Conditions.of(LogFilterEntity.class).eq(LogFilterEntity::getCode, input.getCode()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在相同标识[code={}]的数据", input.getCode()));
        }

        var entity = new LogFilterEntity();
        entity.fromInput(input);
        entity.updateCreator(operator);
        this.mapper.insert(entity);

        this.saveRelations(entity, new HashSet<>(input.getCollectorIds()), new HashSet<>(input.getStorageIds()));

        return entity;
    }

    /**
     * 批量保存数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     */
    public List<LogFilterEntity> insertBatch(@Validated({Insert.class, Default.class}) List<LogFilterInput> inputs, @Nonnull String operator) {
        return Listx.asStream(inputs).map(it -> this.insert(it, operator)).toList();
    }

    /**
     * 更新数据
     *
     * @param input    数据输入
     * @param operator 操作人
     */
    public LogFilterEntity update(@Validated({Update.class, Default.class}) LogFilterInput input, @Nonnull String operator) {
        var entity = this.mapper.findFirstBy(Conditions.of(LogFilterEntity.class).eq(LogFilterEntity::getId, input.getId()));
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("数据[id={}]不存在", input.getId()));
        }

        // 标识唯一性校验
        if (!Objects.equals(entity.getCode(), input.getCode())) {
            if (this.mapper.existsBy(Conditions.of(LogFilterEntity.class).eq(LogFilterEntity::getCode, input.getCode()))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在相同标识[code={}]的数据", input.getCode()));
            }
        }

        entity.fromInput(input);
        entity.updateModifier(operator);
        this.mapper.update(entity);

        this.saveRelations(entity, new HashSet<>(input.getCollectorIds()), new HashSet<>(input.getStorageIds()));

        return entity;
    }

    /**
     * 批量更新数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     */
    public List<LogFilterEntity> updateBatch(@Validated({Update.class, Default.class}) List<LogFilterInput> inputs, @Nonnull String operator) {
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

        var effected = this.mapper.deleteBy(Conditions.of(LogFilterEntity.class).in(LogFilterEntity::getId, ids));

        // 级联删除
        this.collectorRelMapper.deleteBy(Conditions.of(LogCollectorFilterEntity.class).in(LogCollectorFilterEntity::getFilterId, ids));
        this.storageRelMapper.deleteBy(Conditions.of(LogStorageFilterEntity.class).in(LogStorageFilterEntity::getFilterId, ids));

        return effected;
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     */
    public long deleteBy(@Nullable Conditions<? extends LogFilterEntity> conditions) {
        var ids = this.mapper.findBy(Columns.of(Entity::getId), conditions).stream()
                .map(Entity::getId).toList();
        return this.deleteByIds(ids);
    }

    private void saveRelations(LogFilterEntity entity, Set<String> collectorIds, Set<String> storageIds) {
        // 检查关联关系
        var collectors = collectorPersistence.findBy(null, null, Columns.of(Entity::getId), Conditions.of(LogCollectorEntity.class).in(LogCollectorEntity::getId, collectorIds), null);
        if (collectors.size() != collectorIds.size()) {
            // 出现不存在的主键
            for (var collector : collectors) {
                collectorIds.remove(collector.getId());
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("找不到指定的采集器[id={}]", String.join(", ", collectorIds)));
        }

        var storages = storagePersistence.findBy(null, null, Columns.of(Entity::getId), Conditions.of(LogStorageEntity.class).in(LogStorageEntity::getId, storageIds), null);
        if (storages.size() != storageIds.size()) {
            // 出现不存在的主键
            for (var storage : storages) {
                storageIds.remove(storage.getId());
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("找不到指定的存储器[id={}]", String.join(", ", storageIds)));
        }


        // 保存关联关系
        var collectorRelations = collectorIds.stream().map(it -> {
            var rel = new LogCollectorFilterEntity();
            rel.setFilterId(entity.getId());
            rel.setCollectorId(it);
            rel.updateCreator(entity.getModifierId());
            return rel;
        }).toList();
        this.collectorRelMapper.deleteBy(Conditions.of(LogCollectorFilterEntity.class).eq(LogCollectorFilterEntity::getFilterId, entity.getId()));
        this.collectorRelMapper.insertBatch(collectorRelations);

        var storageRelations = storageIds.stream().map(it -> {
            var rel = new LogStorageFilterEntity();
            rel.setFilterId(entity.getId());
            rel.setStorageId(it);
            rel.updateCreator(entity.getModifierId());
            return rel;
        }).toList();
        this.storageRelMapper.deleteBy(Conditions.of(LogStorageFilterEntity.class).eq(LogStorageFilterEntity::getFilterId, entity.getId()));
        this.storageRelMapper.insertBatch(storageRelations);
    }
}
