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
import central.bean.Treeable;
import central.data.authority.Menu;
import central.data.authority.MenuInput;
import central.data.authority.Permission;
import central.data.authority.PermissionInput;
import central.lang.Stringx;
import central.provider.graphql.authority.MenuProvider;
import central.provider.graphql.authority.PermissionProvider;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Menu Logic
 * <p>
 * 菜单业务逻辑
 *
 * @author Alan Yeh
 * @since 2024/12/10
 */
@Service
public class MenuLogic {

    @Setter(onMethod_ = @Autowired)
    private MenuProvider provider;

    @Setter(onMethod_ = @Autowired)
    private PermissionProvider permissionProvider;

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
    private Orders<Menu> getMenuDefaultOrders(@Nullable Orders<Menu> orders) {
        if (Collectionx.isNullOrEmpty(orders)) {
            return orders;
        }
        return Orders.of(Menu.class).asc(Menu::getCode);
    }

    /**
     * 如用用户没有指定排序条件，则构建默认的排序条件
     *
     * @param orders 用户指定的排序条件
     */
    private Orders<Permission> getPermissionDefaultOrders(@Nullable Orders<Permission> orders) {
        if (Collectionx.isNullOrEmpty(orders)) {
            return orders;
        }
        return Orders.of(Permission.class).asc(Permission::getCode);
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
    public List<Menu> findBy(Long limit, Long offset, @Nullable Conditions<Menu> conditions, @Nullable Orders<Menu> orders, @Nonnull String tenant) {
        orders = this.getMenuDefaultOrders(orders);
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
    public Page<Menu> pageBy(@Nonnull Long pageIndex, @Nonnull Long pageSize, @Nullable Conditions<Menu> conditions, @Nullable Orders<Menu> orders, @Nonnull String tenant) {
        orders = this.getMenuDefaultOrders(orders);
        return this.provider.pageBy(pageIndex, pageSize, conditions, orders, tenant);
    }

    /**
     * 主键查询
     *
     * @param id     主键
     * @param tenant 租户标识
     * @return 详情
     */
    public Menu findById(@Nonnull String id, @Nonnull String tenant) {
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
    public Menu insert(@Nonnull @Validated({Insert.class, Default.class}) MenuInput input, @Nonnull String accountId, @Nonnull String tenant) {
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
    public Menu update(@Nonnull @Validated({Update.class, Default.class}) MenuInput input, @Nonnull String accountId, @Nonnull String tenant) {
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
     * 删除数据
     *
     * @param conditions 筛选条件
     * @return 己删除的数据量
     */
    public long deleteBy(@Nonnull Conditions<Menu> conditions, @Nonnull String accountId, @Nonnull String tenant) {
        return this.provider.deleteBy(conditions, tenant);
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
    public List<Permission> findPermissionsBy(Long limit, Long offset, @Nullable Conditions<Permission> conditions, @Nullable Orders<Permission> orders, @Nonnull String tenant) {
        orders = this.getPermissionDefaultOrders(orders);
        return this.permissionProvider.findBy(limit, offset, conditions, orders, tenant);
    }

    /**
     * 插入数据
     *
     * @param input     数据输入
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 插入后的数据
     */
    public Permission insertPermission(@Nonnull @Validated({Insert.class, Default.class}) PermissionInput input, @Nonnull String accountId, @Nonnull String tenant) {
        if (this.getSaasContainer().getApplicationById(input.getApplicationId()) == null) {
            throw new IllegalArgumentException(Stringx.format("应用[id={}]不存在", input.getApplicationId()));
        }

        return this.permissionProvider.insert(input, accountId, tenant);
    }

    /**
     * 更新数据
     *
     * @param input     数据输入
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 更新后的数据
     */
    public Permission updatePermission(@Nonnull @Validated({Update.class, Default.class}) PermissionInput input, @Nonnull String accountId, @Nonnull String tenant) {
        if (this.getSaasContainer().getApplicationById(input.getApplicationId()) == null) {
            throw new IllegalArgumentException(Stringx.format("应用[id={}]不存在", input.getApplicationId()));
        }
        return this.permissionProvider.update(input, accountId, tenant);
    }

    /**
     * 根据主键删除数据
     *
     * @param ids       主键
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 受影响数据行数
     */
    public long deletePermissionsByIds(@Nullable List<String> ids, @Nonnull String accountId, @Nonnull String tenant) {
        return this.permissionProvider.deleteByIds(ids, tenant);
    }

    /**
     * 根据主键删除数据
     *
     * @param conditions 筛选条件
     * @param accountId  操作帐号主键
     * @param tenant     租户标识
     * @return 受影响数据行数
     */
    public long deletePermissionsBy(@Nonnull Conditions<Permission> conditions, @Nonnull String accountId, @Nonnull String tenant) {
        return this.permissionProvider.deleteBy(conditions, tenant);
    }

    /**
     * 根据权限主键查询菜单树
     *
     * @param permissionIds 权限主键
     * @param tenant        租户标识
     * @return 菜单树
     */
    public List<Menu> getMenuTreeByPermissionIds(@Nonnull List<String> permissionIds, @Nonnull String tenant) {
        var permissions = this.permissionProvider.findByIds(permissionIds, tenant);

        var menus = new ArrayList<Menu>();
        // 保存那些已经查出来的菜单主键
        var pendingIds = new HashSet<>(permissions.stream().map(Permission::getMenuId).toList());

        // 循环向上获取菜单，直到没有父级菜单为止
        while (!pendingIds.isEmpty()) {
            var fetched = this.provider.findBy(null, null, Conditions.of(Menu.class).in(Menu::getId, pendingIds), null, tenant);
            menus.addAll(fetched);

            pendingIds.clear();
            pendingIds.addAll(menus.stream().map(Menu::getParentId).filter(Stringx::isNotBlank).toList());
        }

        // 清空这些菜单下面的权限
        menus.forEach(it -> it.getPermissions().clear());
        // 将已经查出来的权限添加到菜单中
        permissions.forEach(it -> menus.stream().filter(menu -> menu.getId().equals(it.getMenuId())).findFirst().ifPresent(menu -> menu.getPermissions().add(it)));

        return Treeable.build(menus);
    }
}
