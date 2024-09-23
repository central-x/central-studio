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

package central.studio.dashboard.logic.system;

import central.bean.Page;
import central.data.system.Dictionary;
import central.data.system.DictionaryInput;
import central.data.system.DictionaryItem;
import central.data.system.DictionaryItemInput;
import central.lang.Stringx;
import central.provider.graphql.system.DictionaryItemProvider;
import central.provider.graphql.system.DictionaryProvider;
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
 * Dictionary Logic
 * <p>
 * 字典业务逻辑
 *
 * @author Alan Yeh
 * @since 2024/09/24
 */
@Service
public class DictionaryLogic {

    @Setter(onMethod_ = @Autowired)
    private DictionaryProvider provider;

    @Setter(onMethod_ = @Autowired)
    private DictionaryItemProvider itemProvider;

    @Setter(onMethod_ = @Autowired)
    private DataContext context;

    private SaasContainer getSaaSContainer() {
        return context.getData(DataFetcherType.SAAS);
    }

    /**
     * 如用用户没有指定排序条件，则构建默认的排序条件
     *
     * @param orders 用户指定的排序条件
     */
    private Orders<Dictionary> getDefaultOrders(@Nullable Orders<Dictionary> orders) {
        if (Collectionx.isNullOrEmpty(orders)) {
            return orders;
        }
        return Orders.of(Dictionary.class).asc(Dictionary::getCode);
    }

    private Orders<DictionaryItem> getDefaultItemOrders(@Nullable Orders<DictionaryItem> orders) {
        if (Collectionx.isNullOrEmpty(orders)) {
            return orders;
        }
        return Orders.of(DictionaryItem.class).asc(DictionaryItem::getOrder).asc(DictionaryItem::getCode);
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
    public Page<Dictionary> pageBy(@Nonnull Long pageIndex, @Nonnull Long pageSize, @Nullable Conditions<Dictionary> conditions, @Nullable Orders<Dictionary> orders, @Nonnull String tenant) {
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
    public Dictionary findById(@Nonnull String id, @Nonnull String tenant) {
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
    public Dictionary insert(@Nonnull @Validated({Insert.class, Default.class}) DictionaryInput input, @Nonnull String accountId, @Nonnull String tenant) {
        if (this.getSaaSContainer().getApplicationById(input.getApplicationId()) == null) {
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
    public Dictionary update(@Nonnull @Validated({Update.class, Default.class}) DictionaryInput input, @Nonnull String accountId, @Nonnull String tenant) {
        if (this.getSaaSContainer().getApplicationById(input.getApplicationId()) == null) {
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
        // 删除对应的字典项
        this.itemProvider.deleteBy(Conditions.of(DictionaryItem.class).in(DictionaryItem::getDictionaryId, ids));
        return this.provider.deleteByIds(ids, tenant);
    }

    /**
     * 根据字典主键获取字典项
     *
     * @param dictionaryId 字典主键
     * @param tenant       租户标识
     * @return 字典项
     */
    public List<DictionaryItem> getItems(@Nonnull String dictionaryId, @Nonnull String tenant) {
        return this.itemProvider.findBy(null, null, Conditions.of(DictionaryItem.class).eq(DictionaryItem::getDictionaryId, dictionaryId), this.getDefaultItemOrders(null), tenant);
    }

    /**
     * 添加字典项
     *
     * @param input     字典项输入
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 插入后的数据
     */
    public DictionaryItem insertItem(@Nonnull @Validated({Insert.class, Default.class}) DictionaryItemInput input, @Nonnull String accountId, @Nonnull String tenant) {
        return this.itemProvider.insert(input, accountId, tenant);
    }

    /**
     * 更新字典项
     *
     * @param input     字典项输入
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 更新后的数据
     */
    public DictionaryItem updateItem(@Nonnull @Validated({Update.class, Default.class}) DictionaryItemInput input, @Nonnull String accountId, @Nonnull String tenant) {
        return this.itemProvider.update(input, accountId, tenant);
    }

    /**
     * 删除字典项
     *
     * @param ids       字典项主键
     * @param accountId 操作帐号主键
     * @param tenant    租户标识
     * @return 受影响数据行数
     */
    public long deleteItems(@Nullable List<String> ids, @Nonnull String accountId, @Nonnull String tenant) {
        return this.itemProvider.deleteByIds(ids, tenant);
    }
}
