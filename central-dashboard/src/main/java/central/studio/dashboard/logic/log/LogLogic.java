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
import central.data.log.*;
import central.lang.Stringx;
import central.provider.graphql.log.LogCollectorProvider;
import central.provider.graphql.log.LogFilterProvider;
import central.provider.graphql.log.LogStorageProvider;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/// Log Logic
///
/// 日志中心业务逻辑
///
/// @author Alan Yeh
@Service
public class LogLogic {

    @Setter(onMethod_ = @Autowired)
    private LogCollectorProvider collectorProvider;

    @Setter(onMethod_ = @Autowired)
    private LogStorageProvider storageProvider;

    @Setter(onMethod_ = @Autowired)
    private LogFilterProvider filterProvider;

    /// 如用用户没有指定排序条件，则构建默认的排序条件
    ///
    /// @param orders 用户指定的排序条件
    private Orders<LogCollector> getCollectorDefaultOrders(@Nullable Orders<LogCollector> orders) {
        if (Collectionx.isNullOrEmpty(orders)) {
            return orders;
        }
        return Orders.of(LogCollector.class).asc(LogCollector::getCode);
    }

    /// 如用用户没有指定排序条件，则构建默认的排序条件
    ///
    /// @param orders 用户指定的排序条件
    private Orders<LogStorage> getStorageDefaultOrders(@Nullable Orders<LogStorage> orders) {
        if (Collectionx.isNullOrEmpty(orders)) {
            return orders;
        }
        return Orders.of(LogStorage.class).asc(LogStorage::getCode);
    }

    /// 如用用户没有指定排序条件，则构建默认的排序条件
    ///
    /// @param orders 用户指定的排序条件
    private Orders<LogFilter> getFilterDefaultOrders(@Nullable Orders<LogFilter> orders) {
        if (Collectionx.isNullOrEmpty(orders)) {
            return orders;
        }
        return Orders.of(LogFilter.class).asc(LogFilter::getCode);
    }

    /// 分页查询
    ///
    /// @param pageIndex  分页下标
    /// @param pageSize   分页大小
    /// @param conditions 筛选条件
    /// @param orders     排序条件
    /// @param tenant     租户标识
    /// @return 分页数据
    public Page<LogCollector> pageCollectorBy(@Nonnull Long pageIndex, @Nonnull Long pageSize, @Nullable Conditions<LogCollector> conditions, @Nullable Orders<LogCollector> orders, @Nonnull String tenant) {
        orders = this.getCollectorDefaultOrders(orders);
        return this.collectorProvider.pageBy(pageIndex, pageSize, conditions, orders, tenant);
    }

    /// 主键查询
    ///
    /// @param id     主键
    /// @param tenant 租户标识
    /// @return 详情
    public LogCollector findCollectorById(@Nonnull String id, @Nonnull String tenant) {
        return this.collectorProvider.findById(id, tenant);
    }

    /// 插入数据
    ///
    /// @param input     数据输入
    /// @param accountId 操作帐号主键
    /// @param tenant    租户标识
    /// @return 插入后的数据
    public LogCollector insertCollector(@Nonnull @Validated({Insert.class, Default.class}) LogCollectorInput input, @Nonnull String accountId, @Nonnull String tenant) {
        return this.collectorProvider.insert(input, accountId, tenant);
    }

    /// 更新数据
    ///
    /// @param input     数据输入
    /// @param accountId 操作帐号主键
    /// @param tenant    租户标识
    /// @return 更新后的数据
    public LogCollector updateCollector(@Nonnull @Validated({Update.class, Default.class}) LogCollectorInput input, @Nonnull String accountId, @Nonnull String tenant) {
        return this.collectorProvider.update(input, accountId, tenant);
    }

    /// 启用数据
    ///
    /// @param id        待启用主键
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 启用后的数据
    public @Nonnull LogCollector enableCollector(@Nonnull String id, @Nonnull String accountId, @Nonnull String tenant) {
        var data = this.collectorProvider.findById(id, tenant);
        if (data == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("找不到网关数据[id={}]", id));
        }
        return this.collectorProvider.update(data.toInput().enabled(Boolean.TRUE).build(), accountId, tenant);
    }

    /// 禁用数据
    ///
    /// @param id        待禁用主键
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 禁用后的数据
    public @Nonnull LogCollector disableCollector(@Nonnull String id, @Nonnull String accountId, @Nonnull String tenant) {
        var data = this.collectorProvider.findById(id, tenant);
        if (data == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("找不到网关数据[id={}]", id));
        }
        return this.collectorProvider.update(data.toInput().enabled(Boolean.FALSE).build(), accountId, tenant);
    }

    /// 根据主键删除数据
    ///
    /// @param ids       主键
    /// @param accountId 操作帐号主键
    /// @param tenant    租户标识
    /// @return 受影响数据行数
    public long deleteCollectorByIds(@Nullable List<String> ids, @Nonnull String accountId, @Nonnull String tenant) {
        return this.collectorProvider.deleteByIds(ids, tenant);
    }


    /// 分页查询
    ///
    /// @param pageIndex  分页下标
    /// @param pageSize   分页大小
    /// @param conditions 筛选条件
    /// @param orders     排序条件
    /// @param tenant     租户标识
    /// @return 分页数据
    public Page<LogStorage> pageStorageBy(@Nonnull Long pageIndex, @Nonnull Long pageSize, @Nullable Conditions<LogStorage> conditions, @Nullable Orders<LogStorage> orders, @Nonnull String tenant) {
        orders = this.getStorageDefaultOrders(orders);
        return this.storageProvider.pageBy(pageIndex, pageSize, conditions, orders, tenant);
    }

    /// 主键查询
    ///
    /// @param id     主键
    /// @param tenant 租户标识
    /// @return 详情
    public LogStorage findStorageById(@Nonnull String id, @Nonnull String tenant) {
        return this.storageProvider.findById(id, tenant);
    }

    /// 插入数据
    ///
    /// @param input     数据输入
    /// @param accountId 操作帐号主键
    /// @param tenant    租户标识
    /// @return 插入后的数据
    public LogStorage insertStorage(@Nonnull @Validated({Insert.class, Default.class}) LogStorageInput input, @Nonnull String accountId, @Nonnull String tenant) {
        return this.storageProvider.insert(input, accountId, tenant);
    }

    /// 更新数据
    ///
    /// @param input     数据输入
    /// @param accountId 操作帐号主键
    /// @param tenant    租户标识
    /// @return 更新后的数据
    public LogStorage updateStorage(@Nonnull @Validated({Update.class, Default.class}) LogStorageInput input, @Nonnull String accountId, @Nonnull String tenant) {
        return this.storageProvider.update(input, accountId, tenant);
    }

    /// 启用数据
    ///
    /// @param id        待启用主键
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 启用后的数据
    public @Nonnull LogStorage enableStorage(@Nonnull String id, @Nonnull String accountId, @Nonnull String tenant) {
        var data = this.storageProvider.findById(id, tenant);
        if (data == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("找不到网关数据[id={}]", id));
        }
        return this.storageProvider.update(data.toInput().enabled(Boolean.TRUE).build(), accountId, tenant);
    }

    /// 禁用数据
    ///
    /// @param id        待禁用主键
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 禁用后的数据
    public @Nonnull LogStorage disableStorage(@Nonnull String id, @Nonnull String accountId, @Nonnull String tenant) {
        var data = this.storageProvider.findById(id, tenant);
        if (data == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("找不到网关数据[id={}]", id));
        }
        return this.storageProvider.update(data.toInput().enabled(Boolean.FALSE).build(), accountId, tenant);
    }

    /// 根据主键删除数据
    ///
    /// @param ids       主键
    /// @param accountId 操作帐号主键
    /// @param tenant    租户标识
    /// @return 受影响数据行数
    public long deleteStorageByIds(@Nullable List<String> ids, @Nonnull String accountId, @Nonnull String tenant) {
        return this.storageProvider.deleteByIds(ids, tenant);
    }

    /// 分页查询
    ///
    /// @param pageIndex  分页下标
    /// @param pageSize   分页大小
    /// @param conditions 筛选条件
    /// @param orders     排序条件
    /// @param tenant     租户标识
    /// @return 分页数据
    public Page<LogFilter> pageFilterBy(@Nonnull Long pageIndex, @Nonnull Long pageSize, @Nullable Conditions<LogFilter> conditions, @Nullable Orders<LogFilter> orders, @Nonnull String tenant) {
        orders = this.getFilterDefaultOrders(orders);
        return this.filterProvider.pageBy(pageIndex, pageSize, conditions, orders, tenant);
    }

    /// 主键查询
    ///
    /// @param id     主键
    /// @param tenant 租户标识
    /// @return 详情
    public LogFilter findFilterById(@Nonnull String id, @Nonnull String tenant) {
        return this.filterProvider.findById(id, tenant);
    }

    /// 插入数据
    ///
    /// @param input     数据输入
    /// @param accountId 操作帐号主键
    /// @param tenant    租户标识
    /// @return 插入后的数据
    public LogFilter insertFilter(@Nonnull @Validated({Insert.class, Default.class}) LogFilterInput input, @Nonnull String accountId, @Nonnull String tenant) {
        return this.filterProvider.insert(input, accountId, tenant);
    }

    /// 更新数据
    ///
    /// @param input     数据输入
    /// @param accountId 操作帐号主键
    /// @param tenant    租户标识
    /// @return 更新后的数据
    public LogFilter updateFilter(@Nonnull @Validated({Update.class, Default.class}) LogFilterInput input, @Nonnull String accountId, @Nonnull String tenant) {
        return this.filterProvider.update(input, accountId, tenant);
    }

    /// 启用数据
    ///
    /// @param id        待启用主键
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 启用后的数据
    public @Nonnull LogFilter enableFilter(@Nonnull String id, @Nonnull String accountId, @Nonnull String tenant) {
        var data = this.filterProvider.findById(id, tenant);
        if (data == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("找不到网关数据[id={}]", id));
        }
        return this.filterProvider.update(data.toInput().enabled(Boolean.TRUE).build(), accountId, tenant);
    }

    /// 禁用数据
    ///
    /// @param id        待禁用主键
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 禁用后的数据
    public @Nonnull LogFilter disableFilter(@Nonnull String id, @Nonnull String accountId, @Nonnull String tenant) {
        var data = this.filterProvider.findById(id, tenant);
        if (data == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("找不到网关数据[id={}]", id));
        }
        return this.filterProvider.update(data.toInput().enabled(Boolean.FALSE).build(), accountId, tenant);
    }

    /// 根据主键删除数据
    ///
    /// @param ids       主键
    /// @param accountId 操作帐号主键
    /// @param tenant    租户标识
    /// @return 受影响数据行数
    public long deleteFilterByIds(@Nullable List<String> ids, @Nonnull String accountId, @Nonnull String tenant) {
        return this.filterProvider.deleteByIds(ids, tenant);
    }
}
