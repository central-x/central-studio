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
import central.data.saas.TenantApplicationInput;
import central.lang.Stringx;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.studio.provider.database.persistence.saas.entity.TenantApplicationEntity;
import central.studio.provider.database.persistence.saas.mapper.TenantApplicationMapper;
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

/**
 * Tenant Application Relation Persistence
 * <p>
 * 租户与应用租凭关系持久化
 *
 * @author Alan Yeh
 * @since 2024/12/21
 */
@Component
public class TenantApplicationPersistence {

    @Setter(onMethod_ = @Autowired)
    private TenantApplicationMapper mapper;

    /**
     * 根据主键查询数据
     *
     * @param id 主键
     */
    public @Nullable TenantApplicationEntity findById(@Nullable String id) {
        if (Stringx.isNullOrBlank(id)) {
            return null;
        }
        return this.mapper.findById(id);
    }

    /**
     * 查询数据
     *
     * @param ids 主键
     */
    public @Nonnull List<TenantApplicationEntity> findByIds(@Nullable List<String> ids) {
        if (Listx.isNullOrEmpty(ids)) {
            return new ArrayList<>();
        }
        return this.mapper.findByIds(ids);
    }

    /**
     * 查询数据
     *
     * @param limit      获取前 N 条数据
     * @param offset     偏移量
     * @param conditions 过滤条件
     * @param orders     排序条件
     */
    public @Nonnull List<TenantApplicationEntity> findBy(@Nullable Long limit, @Nullable Long offset, @Nullable Conditions<? extends TenantApplicationEntity> conditions, @Nullable Orders<? extends TenantApplicationEntity> orders) {
        return this.mapper.findBy(limit, offset, conditions, orders);
    }

    /**
     * 分页查询数据
     *
     * @param pageIndex  分页下标
     * @param pageSize   分页大小
     * @param conditions 过滤条件
     * @param orders     排序条件
     */
    public @Nonnull Page<TenantApplicationEntity> findPageBy(long pageIndex, long pageSize, @Nullable Conditions<? extends TenantApplicationEntity> conditions, @Nullable Orders<? extends TenantApplicationEntity> orders) {
        return this.mapper.findPageBy(pageIndex, pageSize, conditions, orders);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     */
    public long countBy(@Nullable Conditions<? extends TenantApplicationEntity> conditions) {
        return this.mapper.countBy(conditions);
    }

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作人
     */
    public @Nonnull TenantApplicationEntity insert(@Validated({Insert.class, Default.class}) TenantApplicationInput input, @Nonnull String operator) {
        var entity = new TenantApplicationEntity();
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
    public @Nonnull List<TenantApplicationEntity> insertBatch(@Validated({Insert.class, Default.class}) List<TenantApplicationInput> inputs, @Nonnull String operator) {
        return Listx.asStream(inputs).map(it -> this.insert(it, operator)).toList();
    }

    /**
     * 更新数据
     *
     * @param input    数据输入
     * @param operator 操作人
     */
    public @Nonnull TenantApplicationEntity update(@Validated({Update.class, Default.class}) TenantApplicationInput input, @Nonnull String operator) {
        var entity = this.mapper.findFirstBy(Conditions.of(TenantApplicationEntity.class).eq(TenantApplicationEntity::getId, input.getId()));
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("数据[id={}]不存在", input.getId()));
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
    public @Nonnull List<TenantApplicationEntity> updateBatch(@Validated({Update.class, Default.class}) List<TenantApplicationInput> inputs, @Nonnull String operator) {
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
        return this.mapper.deleteByIds(ids);
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     */
    public long deleteBy(@Nullable Conditions<? extends TenantApplicationEntity> conditions) {
        return this.mapper.deleteBy(conditions);
    }
}
