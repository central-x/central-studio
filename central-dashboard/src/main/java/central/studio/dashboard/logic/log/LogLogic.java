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

package central.studio.dashboard.logic.log;

import central.bean.Page;
import central.data.log.LogCollector;
import central.data.log.LogCollectorInput;
import central.provider.graphql.log.LogCollectorProvider;
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
 * Log Logic
 * <p>
 * 日志中心业务逻辑
 *
 * @author Alan Yeh
 * @since 2024/11/24
 */
@Service
public class LogLogic {

    @Setter(onMethod_ = @Autowired)
    private LogCollectorProvider collectorProvider;

    /**
     * 如用用户没有指定排序条件，则构建默认的排序条件
     *
     * @param orders 用户指定的排序条件
     */
    private Orders<LogCollector> getCollectorDefaultOrders(@Nullable Orders<LogCollector> orders) {
        if (Collectionx.isNullOrEmpty(orders)) {
            return orders;
        }
        return Orders.of(LogCollector.class).asc(LogCollector::getCode).asc(LogCollector::getName);
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
    public Page<LogCollector> pageCollectorBy(@Nonnull Long pageIndex, @Nonnull Long pageSize, @Nullable Conditions<LogCollector> conditions, @Nullable Orders<LogCollector> orders, @Nonnull String tenant) {
        orders = this.getCollectorDefaultOrders(orders);
        return this.collectorProvider.pageBy(pageIndex, pageSize, conditions, orders, tenant);
    }

    /**
     * 主键查询
     *
     * @param id     主键
     * @param tenant 租户标识
     * @return 详情
     */
    public LogCollector findCollectorById(@Nonnull String id, @Nonnull String tenant) {
        return this.collectorProvider.findById(id, tenant);
    }

    /**
     * 插入数据
     *
     * @param input     数据输入
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 插入后的数据
     */
    public LogCollector insertCollector(@Nonnull @Validated({Insert.class, Default.class}) LogCollectorInput input, @Nonnull String accountId, @Nonnull String tenant) {
        return this.collectorProvider.insert(input, accountId, tenant);
    }

    /**
     * 更新数据
     *
     * @param input     数据输入
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 更新后的数据
     */
    public LogCollector updateCollector(@Nonnull @Validated({Update.class, Default.class}) LogCollectorInput input, @Nonnull String accountId, @Nonnull String tenant) {
        return this.collectorProvider.update(input, accountId, tenant);
    }

    /**
     * 根据主键删除数据
     *
     * @param ids       主键
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 受影响数据行数
     */
    public long deleteCollectorByIds(@Nullable List<String> ids, @Nonnull String accountId, @Nonnull String tenant) {
        return this.collectorProvider.deleteByIds(ids, tenant);
    }
}
