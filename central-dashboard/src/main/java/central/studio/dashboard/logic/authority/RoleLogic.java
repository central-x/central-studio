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

package central.studio.dashboard.logic.authority;

import central.bean.Page;
import central.data.authority.*;
import central.lang.Stringx;
import central.provider.graphql.authority.RolePermissionProvider;
import central.provider.graphql.authority.RolePrincipalProvider;
import central.provider.graphql.authority.RoleProvider;
import central.provider.graphql.authority.RoleRangeProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.util.Collectionx;
import central.validation.group.Insert;
import central.validation.group.Update;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.groups.Default;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Role Logic
 * <p>
 * 角色业务逻辑
 *
 * @author Alan Yeh
 * @since 2024/12/13
 */
@Service
public class RoleLogic {

    @Setter(onMethod_ = @Autowired)
    private RoleProvider provider;

    /**
     * 角色与权限关联关系
     */
    @Setter(onMethod_ = @Autowired)
    private RolePermissionProvider permissionProvider;

    /**
     * 角色与授权主体关联关系
     */
    @Setter(onMethod_ = @Autowired)
    private RolePrincipalProvider principalProvider;

    /**
     * 角色与授权范围关联关系
     */
    @Setter(onMethod_ = @Autowired)
    private RoleRangeProvider rangeProvider;


    @Setter(onMethod_ = @Autowired)
    private DataContext context;

    private SaasContainer getSaasContainer() {
        return context.getData(DataFetcherType.SAAS);
    }

    /**
     * 如用用户没有指定排序条件，则构建默认的排序条件
     *
     * @param orders 用户指定的排序条件
     */
    private Orders<Role> getDefaultOrders(@Nullable Orders<Role> orders) {
        if (Collectionx.isNullOrEmpty(orders)) {
            return orders;
        }
        return Orders.of(Role.class).asc(Role::getCode);
    }

    /**
     * 列表查询
     *
     * @param limit      数据量（不传的话，就返回所有数据）
     * @param offset     偏移量（跳过前 N 条数据）
     * @param conditions 筛选条件
     * @param orders     排序条件
     * @param tenant     租户标识
     * @return 分页数据
     */
    public List<Role> findBy(Long limit, Long offset, @Nullable Conditions<Role> conditions, @Nullable Orders<Role> orders, @Nonnull String tenant) {
        orders = this.getDefaultOrders(orders);
        return this.provider.findBy(limit, offset, conditions, orders, tenant);
    }

    /**
     * 分页查询
     *
     * @param pageIndex  分页下标
     * @param pageSize   分页大小
     * @param conditions 筛选条件
     * @param orders     排序条件
     * @param tenant     租户标识
     * @return 分页数据
     */
    public Page<Role> pageBy(@Nonnull Long pageIndex, @Nonnull Long pageSize, @Nullable Conditions<Role> conditions, @Nullable Orders<Role> orders, @Nonnull String tenant) {
        orders = this.getDefaultOrders(orders);
        return this.provider.pageBy(pageIndex, pageSize, conditions, orders, tenant);
    }

    /**
     * 主键查询
     *
     * @param id     主键
     * @param tenant 租户标识
     * @return 详情
     */
    public Role findById(@Nonnull String id, @Nonnull String tenant) {
        return this.provider.findById(id, tenant);
    }

    /**
     * 插入数据
     *
     * @param input     数据输入
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 插入后的数据
     */
    public Role insert(@Nonnull @Validated({Insert.class, Default.class}) RoleInput input, @Nonnull String accountId, @Nonnull String tenant) {
        if (this.getSaasContainer().getApplicationById(input.getApplicationId()) == null) {
            throw new IllegalArgumentException(Stringx.format("应用[id={}]不存在", input.getApplicationId()));
        }

        return this.provider.insert(input, accountId, tenant);
    }

    /**
     * 更新数据
     *
     * @param input     数据输入
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 更新后的数据
     */
    public Role update(@Nonnull @Validated({Update.class, Default.class}) RoleInput input, @Nonnull String accountId, @Nonnull String tenant) {
        if (this.getSaasContainer().getApplicationById(input.getApplicationId()) == null) {
            throw new IllegalArgumentException(Stringx.format("应用[id={}]不存在", input.getApplicationId()));
        }
        return this.provider.update(input, accountId, tenant);
    }

    /**
     * 根据主键删除数据
     *
     * @param ids       主键
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 受影响数据行数
     */
    public long deleteByIds(@Nullable List<String> ids, @Nonnull String accountId, @Nonnull String tenant) {
        return this.provider.deleteByIds(ids, tenant);
    }

    /**
     * 获取角色已授权的权限
     *
     * @param id     角色主键
     * @param tenant 租户标识
     * @return 权限列表
     */
    public List<RolePermission> findPermissions(@Nonnull String id, @Nonnull String tenant) {
        return this.permissionProvider.findBy(null, null, Conditions.of(RolePermission.class).eq(RolePermission::getRoleId, id), null, tenant);
    }

    /**
     * 插入角色权限关联关系
     *
     * @param inputs 关联关系输入
     * @param tenant 租户标识
     * @return 已插入的数据
     */
    public List<RolePermission> insertPermissions(@Nonnull List<RolePermissionInput> inputs, @Nonnull String accountId, @Nonnull String tenant) {
        return this.permissionProvider.insertBatch(inputs, accountId, tenant);
    }

    /**
     * 删除角色授权
     *
     * @param ids       待删除的授权主键
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 受影响数据行数
     */
    public long deletePermissions(@Nonnull List<String> ids, @Nonnull String accountId, @Nonnull String tenant) {
        return this.permissionProvider.deleteByIds(ids, tenant);
    }

    /**
     * 查询角色已授权的授权主体
     *
     * @param id     角色主键
     * @param tenant 租户标识
     * @return 授权主体列表
     */
    public List<RolePrincipal> findPrincipals(@Nonnull String id, @Nonnull String tenant) {
        return this.principalProvider.findBy(null, null, Conditions.of(RolePrincipal.class).eq(RolePrincipal::getRoleId, id), null, tenant);
    }

    /**
     * 插入角色授权主体关联关系
     *
     * @param inputs    关联关系输入
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 已插入的数据
     */
    public List<RolePrincipal> insertPrincipals(@Nonnull List<RolePrincipalInput> inputs, @Nonnull String accountId, @Nonnull String tenant) {
        return this.principalProvider.insertBatch(inputs, accountId, tenant);
    }

    /**
     * 删除授权主体关联关系
     *
     * @param ids       待删除的授权主体主键
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 受影响数据行数
     */
    public long deletePrincipals(@Nonnull List<String> ids, @Nonnull String accountId, @Nonnull String tenant) {
        return this.principalProvider.deleteByIds(ids, tenant);
    }

    /**
     * 查询角色已授权的数据范围
     *
     * @param id     角色主键
     * @param tenant 租户标识
     * @return 数据范围列表
     */
    public List<RoleRange> findRanges(@Nonnull String id, @Nonnull String tenant) {
        return this.rangeProvider.findBy(null, null, Conditions.of(RoleRange.class).eq(RoleRange::getRoleId, id), null, tenant);
    }

    /**
     * 插入角色数据范围关联关系
     *
     * @param inputs    关联关系输入
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 已插入的数据
     */
    public List<RoleRange> insertRanges(@Nonnull List<RoleRangeInput> inputs, @Nonnull String accountId, @Nonnull String tenant) {
        return this.rangeProvider.insertBatch(inputs, accountId, tenant);
    }

    /**
     * 删除数据范围关联关系
     *
     * @param ids       待删除的数据范围主键
     * @param accountId 操作帐号主键
     * @param tenant    租主标识
     * @return 受影响数据行数
     */
    public long deleteRanges(@Nonnull List<String> ids, @Nonnull String accountId, @Nonnull String tenant) {
        return this.rangeProvider.deleteByIds(ids, tenant);
    }
}
