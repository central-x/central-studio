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

package central.api.provider.sys;

import central.bean.Page;
import central.data.sys.DictionaryItem;
import central.data.sys.DictionaryItemInput;
import central.sql.Conditions;
import central.sql.Orders;
import central.starter.graphql.stub.Provider;
import central.starter.graphql.stub.annotation.BodyPath;
import central.starter.graphql.stub.annotation.GraphQLStub;
import central.starter.web.http.XForwardedHeaders;
import central.validation.group.Insert;
import central.validation.group.Update;
import jakarta.validation.groups.Default;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * 字典项
 * @author Alan Yeh
 * @since 2022/10/06
 */
@Repository
@BodyPath("sys.dictionaries.items")
@GraphQLStub(path = "sys", client = "providerClient")
public interface DictionaryItemProvider extends Provider {
    /**
     * 查询数据
     *
     * @param id 主键
     * @return 数据
     */
    DictionaryItem findById(String id);
    DictionaryItem findById(String id, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    /**
     * 查询数据
     *
     * @param ids 主键
     * @return 数据
     */
    List<DictionaryItem> findByIds(List<String> ids);
    List<DictionaryItem> findByIds(List<String> ids, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    /**
     * 查询数据
     *
     * @param limit      数据量（不传的话，就返回所有数据）
     * @param offset     偏移量（跳过前 N 条数据）
     * @param conditions 筛选条件
     * @param orders     排序条件
     * @return 数据列表
     */
    List<DictionaryItem> findBy(Long limit, Long offset, Conditions<DictionaryItem> conditions, Orders<DictionaryItem> orders);
    List<DictionaryItem> findBy(Long limit, Long offset, Conditions<DictionaryItem> conditions, Orders<DictionaryItem> orders, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    /**
     * 分页查询数据
     *
     * @param pageIndex  分页下标（最小值为 1，最大值为 100）
     * @param pageSize   分页大小（最小值为 1，最大值为 100）
     * @param conditions 筛选条件
     * @param orders     排序条件
     * @return 分页数据
     */
    Page<DictionaryItem> pageBy(Long pageIndex, Long pageSize, Conditions<DictionaryItem> conditions, Orders<DictionaryItem> orders);
    Page<DictionaryItem> pageBy(Long pageIndex, Long pageSize, Conditions<DictionaryItem> conditions, Orders<DictionaryItem> orders, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @return 数据数量
     */
    Long countBy(Conditions<DictionaryItem> conditions);
    Long countBy(Conditions<DictionaryItem> conditions, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作人
     * @return 保存后的实体数据
     */
    DictionaryItem insert(@Validated({Insert.class, Default.class}) DictionaryItemInput input, String operator);
    DictionaryItem insert(@Validated({Insert.class, Default.class}) DictionaryItemInput input, String operator, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    /**
     * 批量保存数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     * @return 保存后的实体数据
     */
    List<DictionaryItem> insertBatch(@Validated({Insert.class, Default.class}) List<DictionaryItemInput> inputs, String operator);
    List<DictionaryItem> insertBatch(@Validated({Insert.class, Default.class}) List<DictionaryItemInput> inputs, String operator, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    /**
     * 更新数据
     *
     * @param input    数据输入
     * @param operator 操作人
     * @return 更新后的实体数据
     */
    DictionaryItem update(@Validated({Update.class, Default.class}) DictionaryItemInput input, String operator);
    DictionaryItem update(@Validated({Update.class, Default.class}) DictionaryItemInput input, String operator, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    /**
     * 批量更新数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     * @return 更新后的实体数据
     */
    List<DictionaryItem> updateBatch(@Validated({Update.class, Default.class}) List<DictionaryItemInput> inputs, String operator);
    List<DictionaryItem> updateBatch(@Validated({Update.class, Default.class}) List<DictionaryItemInput> inputs, String operator, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    /**
     * 删除数据
     *
     * @param ids 主键
     * @return 己删除的数据量
     */
    Long deleteByIds(List<String> ids);
    Long deleteByIds(List<String> ids, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    /**
     * 删除数据
     *
     * @param conditions 筛选条件
     * @return 己删除的数据量
     */
    Long deleteBy(Conditions<DictionaryItem> conditions);
    Long deleteBy(Conditions<DictionaryItem> conditions, @RequestHeader(XForwardedHeaders.TENANT) String tenant);
}
