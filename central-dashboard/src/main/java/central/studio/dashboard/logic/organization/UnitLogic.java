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

package central.studio.dashboard.logic.organization;

import central.bean.Page;
import central.data.organization.Department;
import central.data.organization.DepartmentInput;
import central.data.organization.Unit;
import central.data.organization.UnitInput;
import central.provider.graphql.organization.DepartmentProvider;
import central.provider.graphql.organization.UnitProvider;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.util.Collectionx;
import central.util.Listx;
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
 * Unit Logic
 * <p>
 * 组织机构业务逻辑
 *
 * @author Alan Yeh
 * @since 2024/09/16
 */
@Service
public class UnitLogic {

    @Setter(onMethod_ = @Autowired)
    private UnitProvider provider;

    @Setter(onMethod_ = @Autowired)
    private DepartmentProvider departmentProvider;

    /**
     * 如用用户没有指定排序条件，则构建默认的排序条件
     *
     * @param orders 用户指定的排序条件
     */
    private Orders<Unit> getUnitDefaultOrders(Orders<Unit> orders) {
        if (Collectionx.isNotEmpty(orders)) {
            return orders;
        }
        return Orders.of(Unit.class).desc(Unit::getOrder).asc(Unit::getName);
    }

    /**
     * 如用用户没有指定排序条件，则构建默认的排序条件
     *
     * @param orders 用户指定的排序条件
     */
    private Orders<Department> getDepartmentDefaultOrders(Orders<Department> orders) {
        if (Collectionx.isNotEmpty(orders)) {
            return orders;
        }
        return Orders.of(Department.class).desc(Department::getOrder).asc(Department::getName);
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
    public @Nonnull Page<Unit> pageBy(@Nonnull Long pageIndex, @Nonnull Long pageSize, @Nullable Conditions<Unit> conditions, @Nullable Orders<Unit> orders, @Nonnull String tenant) {
        orders = this.getUnitDefaultOrders(orders);
        return this.provider.pageBy(pageIndex, pageSize, conditions, orders, tenant);
    }

    /**
     * 列表查询
     *
     * @param conditions 筛选条件
     * @param orders     排序条件
     * @param tenant     租户标识
     * @return 列表数据
     */
    public @Nonnull List<Unit> listBy(@Nullable Conditions<Unit> conditions, @Nullable Orders<Unit> orders, @Nonnull String tenant) {
        orders = this.getUnitDefaultOrders(orders);
        return this.provider.findBy(null, null, conditions, orders, tenant);
    }

    /**
     * 主键查询
     *
     * @param id     主键
     * @param tenant 租户标识
     * @return 详情
     */
    public @Nullable Unit findById(@Nonnull String id, @Nonnull String tenant) {
        return this.provider.findById(id, tenant);
    }

    /**
     * 主键查询
     *
     * @param code   标识
     * @param tenant 租户标识
     * @return 详情
     */
    public @Nullable Unit findByCode(@Nonnull String code, @Nonnull String tenant) {
        var units = this.provider.findBy(1L, 0L, Conditions.of(Unit.class).eq(Unit::getCode, code), null, tenant);
        return Listx.getFirstOrNull(units);
    }

    /**
     * 插入数据
     *
     * @param input     数据输入
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 插入后的数据
     */
    public Unit insert(@Nonnull @Validated({Insert.class, Default.class}) UnitInput input, @Nonnull String accountId, @Nonnull String tenant) {
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
    public Unit update(@Nonnull @Validated({Update.class, Default.class}) UnitInput input, @Nonnull String accountId, @Nonnull String tenant) {
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
     * 根据标识删除数据
     *
     * @param codes     标识
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 受影响数据行数
     */
    public long deleteByCodes(@Nullable List<String> codes, @Nonnull String accountId, @Nonnull String tenant) {
        return this.provider.deleteBy(Conditions.of(Unit.class).in(Unit::getCode, codes));
    }


    /**
     * 列表查询
     *
     * @param conditions 筛选条件
     * @param orders     排序条件
     * @param tenant     租户标识
     * @return 列表数据
     */
    public List<Department> listDepartmentBy(@Nullable Conditions<Department> conditions, @Nullable Orders<Department> orders, @Nonnull String tenant) {
        orders = this.getDepartmentDefaultOrders(orders);
        return this.departmentProvider.findBy(null, null, conditions, orders, tenant);
    }

    /**
     * 主键查询
     *
     * @param id     主键
     * @param tenant 租户标识
     * @return 详情
     */
    public Department findDepartmentById(@Nonnull String id, @Nonnull String tenant) {
        return this.departmentProvider.findById(id, tenant);
    }

    /**
     * 插入数据
     *
     * @param input     数据输入
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 插入后的数据
     */
    public Department insertDepartment(@Nonnull @Validated({Insert.class, Default.class}) DepartmentInput input, @Nonnull String accountId, @Nonnull String tenant) {
        return this.departmentProvider.insert(input, accountId, tenant);
    }

    /**
     * 更新数据
     *
     * @param input     数据输入
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 更新后的数据
     */
    public Department updateDepartment(@Nonnull @Validated({Update.class, Default.class}) DepartmentInput input, @Nonnull String accountId, @Nonnull String tenant) {
        return this.departmentProvider.update(input, accountId, tenant);
    }

    /**
     * 根据主键删除数据
     *
     * @param ids       主键
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 受影响数据行数
     */
    public long deleteDepartmentByIds(@Nullable List<String> ids, @Nonnull String accountId, @Nonnull String tenant) {
        return this.departmentProvider.deleteByIds(ids, tenant);
    }
}
