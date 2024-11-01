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

package central.studio.dashboard.logic.storage;

import central.bean.Page;
import central.data.storage.StorageBucket;
import central.data.storage.StorageBucketInput;
import central.data.storage.StorageObject;
import central.data.storage.StorageObjectInput;
import central.lang.Stringx;
import central.provider.graphql.storage.StorageBucketProvider;
import central.provider.graphql.storage.StorageObjectProvider;
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
 * Storage Logic
 * <p>
 * 存储中心业务逻辑
 *
 * @author Alan Yeh
 * @since 2024/10/29
 */
@Service
public class StorageLogic {

    @Setter(onMethod_ = @Autowired)
    private StorageBucketProvider bucketProvider;

    @Setter(onMethod_ = @Autowired)
    private StorageObjectProvider objectProvider;

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
    private Orders<StorageBucket> getBucketDefaultOrders(@Nullable Orders<StorageBucket> orders) {
        if (Collectionx.isNullOrEmpty(orders)) {
            return orders;
        }
        return Orders.of(StorageBucket.class).asc(StorageBucket::getCode);
    }

    /**
     * 如用用户没有指定排序条件，则构建默认的排序条件
     *
     * @param orders 用户指定的排序条件
     */
    private Orders<StorageObject> getObjectDefaultOrders(@Nullable Orders<StorageObject> orders) {
        if (Collectionx.isNullOrEmpty(orders)) {
            return orders;
        }
        return Orders.of(StorageObject.class).asc(StorageObject::getName);
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
    public Page<StorageBucket> pageBy(@Nonnull Long pageIndex, @Nonnull Long pageSize, @Nullable Conditions<StorageBucket> conditions, @Nullable Orders<StorageBucket> orders, @Nonnull String tenant) {
        orders = this.getBucketDefaultOrders(orders);
        return this.bucketProvider.pageBy(pageIndex, pageSize, conditions, orders, tenant);
    }

    /**
     * 主键查询
     *
     * @param id     主键
     * @param tenant 租户标识
     * @return 详情
     */
    public StorageBucket findById(@Nonnull String id, @Nonnull String tenant) {
        return this.bucketProvider.findById(id, tenant);
    }

    /**
     * 插入数据
     *
     * @param input     数据输入
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 插入后的数据
     */
    public StorageBucket insert(@Nonnull @Validated({Insert.class, Default.class}) StorageBucketInput input, @Nonnull String accountId, @Nonnull String tenant) {
        if (this.getSaasContainer().getApplicationById(input.getApplicationId()) == null) {
            throw new IllegalArgumentException(Stringx.format("应用[id={}]不存在", input.getApplicationId()));
        }

        return this.bucketProvider.insert(input, accountId, tenant);
    }

    /**
     * 更新数据
     *
     * @param input     数据输入
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 更新后的数据
     */
    public StorageBucket update(@Nonnull @Validated({Update.class, Default.class}) StorageBucketInput input, @Nonnull String accountId, @Nonnull String tenant) {
        if (this.getSaasContainer().getApplicationById(input.getApplicationId()) == null) {
            throw new IllegalArgumentException(Stringx.format("应用[id={}]不存在", input.getApplicationId()));
        }

        return this.bucketProvider.update(input, accountId, tenant);
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
        return this.bucketProvider.deleteByIds(ids, tenant);
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
    public Page<StorageObject> pageObjects(@Nonnull Long pageIndex, @Nonnull Long pageSize, @Nullable Conditions<StorageObject> conditions, @Nullable Orders<StorageObject> orders, @Nonnull String tenant) {
        return this.objectProvider.pageBy(pageIndex, pageSize, conditions, this.getObjectDefaultOrders(orders), tenant);
    }

    /**
     * 主键查询
     *
     * @param id     主键
     * @param tenant 租户标识
     * @return 详情
     */
    public StorageObject findObjectById(@Nonnull String id, @Nonnull String tenant) {
        return this.objectProvider.findById(id, tenant);
    }

    /**
     * 插入数据
     *
     * @param input     数据输入
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 插入后的数据
     */
    public StorageObject insertObject(@Nonnull @Validated({Insert.class, Default.class}) StorageObjectInput input, @Nonnull String accountId, @Nonnull String tenant) {
        return this.objectProvider.insert(input, tenant);
    }

    /**
     * 更新数据
     *
     * @param input     数据输入
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 更新后的数据
     */
    public StorageObject updateObject(@Nonnull @Validated({Update.class, Default.class}) StorageObjectInput input, @Nonnull String accountId, @Nonnull String tenant) {
        return this.objectProvider.update(input, tenant);
    }

    /**
     * 根据主键删除数据
     *
     * @param ids       主键
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 受影响数据行数
     */
    public long deleteObjectByIds(@Nullable List<String> ids, @Nonnull String accountId, @Nonnull String tenant) {
        return this.objectProvider.deleteByIds(ids, tenant);
    }
}
