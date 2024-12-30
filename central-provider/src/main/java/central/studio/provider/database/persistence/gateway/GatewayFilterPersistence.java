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

package central.studio.provider.database.persistence.gateway;

import central.bean.Page;
import central.data.gateway.GatewayFilterInput;
import central.lang.Stringx;
import central.sql.data.Entity;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.studio.provider.database.persistence.gateway.entity.GatewayFilterEntity;
import central.studio.provider.database.persistence.gateway.mapper.GatewayFilterMapper;
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

/**
 * GatewayFilter Persistence
 * <p>
 * 网关拦截器持久化
 *
 * @author Alan Yeh
 * @since 2024/12/23
 */
@Component
public class GatewayFilterPersistence {

    @Setter(onMethod_ = @Autowired)
    private GatewayFilterMapper mapper;

    /**
     * 根据主键查询数据
     *
     * @param id      主键
     * @param columns 字段列表
     * @param tenant  租户标识
     */
    public @Nullable GatewayFilterEntity findById(@Nullable String id,
                                                  @Nullable Columns<? extends GatewayFilterEntity> columns,
                                                  @Nonnull String tenant) {
        if (Stringx.isNullOrBlank(id)) {
            return null;
        }

        var conditions = Conditions.of(GatewayFilterEntity.class).eq(GatewayFilterEntity::getId, id).eq(GatewayFilterEntity::getTenantCode, tenant);
        return this.mapper.findFirstBy(columns, conditions);
    }

    /**
     * 查询数据
     *
     * @param ids     主键
     * @param columns 字段列表
     * @param tenant  租户标识
     */
    public @Nonnull List<GatewayFilterEntity> findByIds(@Nullable List<String> ids,
                                                        @Nullable Columns<? extends GatewayFilterEntity> columns,
                                                        @Nonnull String tenant) {
        if (Listx.isNullOrEmpty(ids)) {
            return Collections.emptyList();
        }

        var conditions = Conditions.of(GatewayFilterEntity.class).in(GatewayFilterEntity::getId, ids).eq(GatewayFilterEntity::getTenantCode, tenant);
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
    public @Nonnull List<GatewayFilterEntity> findBy(@Nullable Long limit,
                                                     @Nullable Long offset,
                                                     @Nullable Columns<? extends GatewayFilterEntity> columns,
                                                     @Nullable Conditions<? extends GatewayFilterEntity> conditions,
                                                     @Nullable Orders<? extends GatewayFilterEntity> orders,
                                                     @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(GatewayFilterEntity::getTenantCode, tenant);
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
    public @Nonnull Page<GatewayFilterEntity> pageBy(@Nonnull Long pageIndex,
                                                     @Nonnull Long pageSize,
                                                     @Nullable Columns<? extends GatewayFilterEntity> columns,
                                                     @Nullable Conditions<? extends GatewayFilterEntity> conditions,
                                                     @Nullable Orders<? extends GatewayFilterEntity> orders,
                                                     @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(GatewayFilterEntity::getTenantCode, tenant);
        return this.mapper.findPageBy(pageIndex, pageSize, columns, conditions, orders);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    public Long countBy(@Nullable Conditions<? extends GatewayFilterEntity> conditions,
                        @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(GatewayFilterEntity::getTenantCode, tenant);
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
    public GatewayFilterEntity insert(@Nonnull @Validated({Insert.class, Default.class}) GatewayFilterInput input,
                                      @Nonnull String operator,
                                      @Nonnull String tenant) {
        var entity = new GatewayFilterEntity();
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
    public List<GatewayFilterEntity> insertBatch(@Nullable @Validated({Insert.class, Default.class}) List<GatewayFilterInput> inputs,
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
    public GatewayFilterEntity update(@Nonnull @Validated({Update.class, Default.class}) GatewayFilterInput input,
                                      @Nonnull String operator,
                                      @Nonnull String tenant) {
        var entity = this.mapper.findFirstBy(Conditions.of(GatewayFilterEntity.class).eq(GatewayFilterEntity::getId, input.getId()).eq(GatewayFilterEntity::getTenantCode, tenant));
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("数据[id={}]不存在", input.getId()));
        }

        entity.fromInput(input);
        entity.setTenantCode(tenant);
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
    public List<GatewayFilterEntity> updateBatch(@Nullable @Validated({Update.class, Default.class}) List<GatewayFilterInput> inputs,
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

        return this.mapper.deleteBy(Conditions.of(GatewayFilterEntity.class).in(GatewayFilterEntity::getId, ids).eq(GatewayFilterEntity::getTenantCode, tenant));
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     * @param tenant     租户标识
     */
    public long deleteBy(@Nullable Conditions<? extends GatewayFilterEntity> conditions,
                         @Nonnull String tenant) {
        var ids = this.mapper.findBy(Columns.of(Entity::getId), Conditions.group(conditions).eq(GatewayFilterEntity::getTenantCode, tenant)).stream()
                .map(Entity::getId).toList();
        return this.deleteByIds(ids, tenant);
    }
}
