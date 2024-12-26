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
import central.data.log.LogStorageInput;
import central.lang.Stringx;
import central.sql.data.Entity;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.studio.provider.database.persistence.log.entity.LogStorageEntity;
import central.studio.provider.database.persistence.log.entity.LogStorageFilterEntity;
import central.studio.provider.database.persistence.log.mapper.LogStorageFilterMapper;
import central.studio.provider.database.persistence.log.mapper.LogStorageMapper;
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

import java.util.List;
import java.util.Objects;

/**
 * Log Storage Persistence
 * <p>
 * 日志存储器持久化
 *
 * @author Alan Yeh
 * @since 2024/12/26
 */
@Component
public class LogStoragePersistence {

    @Setter(onMethod_ = @Autowired)
    private LogStorageMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private LogStorageFilterMapper relMapper;

    /**
     * 根据主键查询数据
     *
     * @param id      主键
     * @param columns 字段列表
     */
    public @Nullable LogStorageEntity findById(@Nullable String id,
                                               @Nullable Columns<? extends LogStorageEntity> columns) {

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
    public @Nonnull List<LogStorageEntity> findByIds(@Nullable List<String> ids,
                                                     @Nullable Columns<? extends LogStorageEntity> columns) {
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
    public @Nonnull List<LogStorageEntity> findBy(@Nullable Long limit,
                                                  @Nullable Long offset,
                                                  @Nullable Columns<? extends LogStorageEntity> columns,
                                                  @Nullable Conditions<? extends LogStorageEntity> conditions,
                                                  @Nullable Orders<? extends LogStorageEntity> orders) {
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
    public @Nonnull Page<LogStorageEntity> pageBy(@Nonnull Long pageIndex,
                                                  @Nonnull Long pageSize,
                                                  @Nullable Columns<? extends LogStorageEntity> columns,
                                                  @Nullable Conditions<? extends LogStorageEntity> conditions,
                                                  @Nullable Orders<? extends LogStorageEntity> orders) {

        return this.mapper.findPageBy(pageIndex, pageSize, columns, conditions, orders);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     */
    public Long countBy(@Nullable Conditions<? extends LogStorageEntity> conditions) {
        return this.mapper.countBy(conditions);
    }

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作帐号
     * @return 保存后的数据
     */
    public LogStorageEntity insert(@Validated({Insert.class, Default.class}) LogStorageInput input, @Nonnull String operator) {
        // 标识唯一性校验
        if (this.mapper.existsBy(Conditions.of(LogStorageEntity.class).eq(LogStorageEntity::getCode, input.getCode()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在相同标识[code={}]的数据", input.getCode()));
        }

        var entity = new LogStorageEntity();
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
    public List<LogStorageEntity> insertBatch(@Validated({Insert.class, Default.class}) List<LogStorageInput> inputs, @Nonnull String operator) {
        return Listx.asStream(inputs).map(it -> this.insert(it, operator)).toList();
    }

    /**
     * 更新数据
     *
     * @param input    数据输入
     * @param operator 操作人
     */
    public LogStorageEntity update(@Validated({Update.class, Default.class}) LogStorageInput input, @Nonnull String operator) {
        var entity = this.mapper.findFirstBy(Conditions.of(LogStorageEntity.class).eq(LogStorageEntity::getId, input.getId()));
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("数据[id={}]不存在", input.getId()));
        }

        // 标识唯一性校验
        if (!Objects.equals(entity.getCode(), input.getCode())) {
            if (this.mapper.existsBy(Conditions.of(LogStorageEntity.class).eq(LogStorageEntity::getCode, input.getCode()))) {
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
    public List<LogStorageEntity> updateBatch(@Validated({Update.class, Default.class}) List<LogStorageInput> inputs, @Nonnull String operator) {
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

        var effected = this.mapper.deleteBy(Conditions.of(LogStorageEntity.class).in(LogStorageEntity::getId, ids));

        if (effected > 0L) {
            // 级联删除
            this.relMapper.deleteBy(Conditions.of(LogStorageFilterEntity.class).in(LogStorageFilterEntity::getStorageId, ids));
        }

        return effected;
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     */
    public long deleteBy(@Nullable Conditions<? extends LogStorageEntity> conditions) {
        var ids = this.mapper.findBy(Columns.of(LogStorageEntity::getId), conditions).stream()
                .map(Entity::getId).toList();

        return this.deleteByIds(ids);
    }
}
