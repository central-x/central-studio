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

package central.studio.dashboard.logic.multicast;

import central.bean.Page;
import central.data.multicast.MulticastBroadcaster;
import central.data.multicast.MulticastBroadcasterInput;
import central.data.multicast.MulticastMessage;
import central.lang.Stringx;
import central.provider.graphql.multicast.MulticastBroadcasterProvider;
import central.provider.graphql.multicast.MulticastMessageProvider;
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
 * Multicast Logic
 * <p>
 * 广播中心业务逻辑
 *
 * @author Alan Yeh
 * @since 2024/11/07
 */
@Service
public class MulticastLogic {

    @Setter(onMethod_ = @Autowired)
    private MulticastBroadcasterProvider broadcasterProvider;

    @Setter(onMethod_ = @Autowired)
    private MulticastMessageProvider messageProvider;

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
    private Orders<MulticastBroadcaster> getBroadcasterDefaultOrders(@Nullable Orders<MulticastBroadcaster> orders) {
        if (Collectionx.isNullOrEmpty(orders)) {
            return orders;
        }
        return Orders.of(MulticastBroadcaster.class).asc(MulticastBroadcaster::getCode).asc(MulticastBroadcaster::getName);
    }

    /**
     * 如用用户没有指定排序条件，则构建默认的排序条件
     *
     * @param orders 用户指定的排序条件
     */
    private Orders<MulticastMessage> getMessageDefaultOrders(@Nullable Orders<MulticastMessage> orders) {
        if (Collectionx.isNullOrEmpty(orders)) {
            return orders;
        }
        return Orders.of(MulticastMessage.class).desc(MulticastMessage::getCreateDate);
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
    public Page<MulticastBroadcaster> pageBy(@Nonnull Long pageIndex, @Nonnull Long pageSize, @Nullable Conditions<MulticastBroadcaster> conditions, @Nullable Orders<MulticastBroadcaster> orders, @Nonnull String tenant) {
        orders = this.getBroadcasterDefaultOrders(orders);
        return this.broadcasterProvider.pageBy(pageIndex, pageSize, conditions, orders, tenant);
    }

    /**
     * 主键查询
     *
     * @param id     主键
     * @param tenant 租户标识
     * @return 详情
     */
    public MulticastBroadcaster findById(@Nonnull String id, @Nonnull String tenant) {
        return this.broadcasterProvider.findById(id, tenant);
    }

    /**
     * 插入数据
     *
     * @param input     数据输入
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 插入后的数据
     */
    public MulticastBroadcaster insert(@Nonnull @Validated({Insert.class, Default.class}) MulticastBroadcasterInput input, @Nonnull String accountId, @Nonnull String tenant) {
        if (this.getSaasContainer().getApplicationById(input.getApplicationId()) == null) {
            throw new IllegalArgumentException(Stringx.format("应用[id={}]不存在", input.getApplicationId()));
        }

        return this.broadcasterProvider.insert(input, accountId, tenant);
    }

    /**
     * 更新数据
     *
     * @param input     数据输入
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 更新后的数据
     */
    public MulticastBroadcaster update(@Nonnull @Validated({Update.class, Default.class}) MulticastBroadcasterInput input, @Nonnull String accountId, @Nonnull String tenant) {
        if (this.getSaasContainer().getApplicationById(input.getApplicationId()) == null) {
            throw new IllegalArgumentException(Stringx.format("应用[id={}]不存在", input.getApplicationId()));
        }

        return this.broadcasterProvider.update(input, accountId, tenant);
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
        return this.broadcasterProvider.deleteByIds(ids, tenant);
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
    public Page<MulticastMessage> pageMessages(@Nonnull Long pageIndex, @Nonnull Long pageSize, @Nullable Conditions<MulticastMessage> conditions, @Nullable Orders<MulticastMessage> orders, @Nonnull String tenant) {
        return this.messageProvider.pageBy(pageIndex, pageSize, conditions, this.getMessageDefaultOrders(orders), tenant);
    }

    /**
     * 根据主键删除数据
     *
     * @param ids       主键
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 受影响数据行数
     */
    public long deleteMessagesByIds(@Nullable List<String> ids, @Nonnull String accountId, @Nonnull String tenant) {
        return this.messageProvider.deleteByIds(ids, tenant);
    }
}
