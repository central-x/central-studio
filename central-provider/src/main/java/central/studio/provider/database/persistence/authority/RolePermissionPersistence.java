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

package central.studio.provider.database.persistence.authority;

import central.bean.Page;
import central.data.authority.RolePermissionInput;
import central.lang.Stringx;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.studio.provider.graphql.authority.dto.RolePermissionDTO;
import central.studio.provider.database.persistence.authority.entity.RolePermissionEntity;
import central.studio.provider.database.persistence.authority.mapper.RolePermissionMapper;
import central.util.Listx;
import central.validation.group.Insert;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.groups.Default;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

/**
 * Role Permission Relation Persistence
 * <p>
 * 角色与权限关联关系持久化
 *
 * @author Alan Yeh
 * @since 2024/12/15
 */
@Component
public class RolePermissionPersistence {

    @Setter(onMethod_ = @Autowired)
    private RolePermissionMapper mapper;

    /**
     * 根据主键查询数据
     *
     * @param id      主键
     * @param columns 字段列表
     * @param tenant  租户标识
     */
    public @Nullable RolePermissionEntity findById(@Nullable String id,
                                                   @Nullable Columns<? extends RolePermissionEntity> columns,
                                                   @Nonnull String tenant) {
        if (Stringx.isNullOrBlank(id)) {
            return null;
        }

        var conditions = Conditions.of(RolePermissionDTO.class).eq(RolePermissionDTO::getId, id).eq(RolePermissionDTO::getTenantCode, tenant);
        return this.mapper.findFirstBy(columns, conditions);
    }

    /**
     * 查询数据
     *
     * @param ids     主键
     * @param columns 字段列表
     * @param tenant  租户标识
     */
    public @Nonnull List<RolePermissionEntity> findByIds(@Nullable List<String> ids,
                                                         @Nullable Columns<? extends RolePermissionEntity> columns,
                                                         @Nonnull String tenant) {
        if (Listx.isNullOrEmpty(ids)) {
            return Collections.emptyList();
        }

        var conditions = Conditions.of(RolePermissionDTO.class).in(RolePermissionDTO::getId, ids).eq(RolePermissionDTO::getTenantCode, tenant);
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
    public @Nonnull List<RolePermissionEntity> findBy(@Nullable Long limit,
                                                      @Nullable Long offset,
                                                      @Nullable Columns<? extends RolePermissionEntity> columns,
                                                      @Nullable Conditions<? extends RolePermissionEntity> conditions,
                                                      @Nullable Orders<? extends RolePermissionEntity> orders,
                                                      @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(RolePermissionEntity::getTenantCode, tenant);
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
    public @Nonnull Page<RolePermissionEntity> pageBy(@Nonnull Long pageIndex,
                                                      @Nonnull Long pageSize,
                                                      @Nullable Columns<? extends RolePermissionEntity> columns,
                                                      @Nullable Conditions<? extends RolePermissionEntity> conditions,
                                                      @Nullable Orders<? extends RolePermissionEntity> orders,
                                                      @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(RolePermissionEntity::getTenantCode, tenant);
        return this.mapper.findPageBy(pageIndex, pageSize, columns, conditions, orders);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    public Long countBy(@Nullable Conditions<? extends RolePermissionEntity> conditions,
                        @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(RolePermissionEntity::getTenantCode, tenant);
        return this.mapper.countBy(conditions);
    }

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    public @Nonnull RolePermissionEntity insert(@Nonnull @Validated({Insert.class, Default.class}) RolePermissionInput input,
                                                @Nonnull String operator,
                                                @Nonnull String tenant) {
        var entity = new RolePermissionEntity();
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
    public @Nonnull List<RolePermissionEntity> insertBatch(@Nullable @Validated({Insert.class, Default.class}) List<RolePermissionInput> inputs,
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

        return this.mapper.deleteBy(Conditions.of(RolePermissionEntity.class).in(RolePermissionEntity::getId, ids).eq(RolePermissionEntity::getTenantCode, tenant));
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     * @param tenant     租户标识
     */
    public long deleteBy(@Nullable Conditions<? extends RolePermissionEntity> conditions,
                         @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(RolePermissionEntity::getTenantCode, tenant);
        return this.mapper.deleteBy(conditions);
    }
}
