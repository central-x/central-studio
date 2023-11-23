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

package central.provider.graphql.system.query;

import central.bean.Page;
import central.provider.graphql.DTO;
import central.provider.graphql.system.dto.DictionaryItemDTO;
import central.provider.graphql.system.entity.DictionaryItemEntity;
import central.provider.graphql.system.mapper.DictionaryItemMapper;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.starter.graphql.annotation.GraphQLBatchLoader;
import central.starter.graphql.annotation.GraphQLFetcher;
import central.starter.graphql.annotation.GraphQLSchema;
import central.web.XForwardedHeaders;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DictionaryItem Query
 * 字典项查询
 *
 * @author Alan Yeh
 * @since 2022/10/03
 */
@Component
@GraphQLSchema(path = "system/query", types = DictionaryItemDTO.class)
public class DictionaryItemQuery {
    @Setter(onMethod_ = @Autowired)
    private DictionaryItemMapper mapper;

    /**
     * 批量数据加载器
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    @GraphQLBatchLoader
    public @Nonnull Map<String, DictionaryItemDTO> batchLoader(@RequestParam List<String> ids,
                                                               @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.mapper.findBy(Conditions.of(DictionaryItemEntity.class).in(DictionaryItemEntity::getId, ids).eq(DictionaryItemEntity::getTenantCode, tenant))
                .stream()
                .map(it -> DTO.wrap(it, DictionaryItemDTO.class))
                .collect(Collectors.toMap(DictionaryItemDTO::getId, it -> it));
    }

    /**
     * 根据主键查询数据
     *
     * @param id     主键
     * @param tenant 租户标识
     */
    @GraphQLFetcher
    public @Nullable DictionaryItemDTO findById(@RequestParam String id,
                                                @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var entity = this.mapper.findFirstBy(Conditions.of(DictionaryItemEntity.class).eq(DictionaryItemEntity::getId, id).eq(DictionaryItemEntity::getTenantCode, tenant));
        return DTO.wrap(entity, DictionaryItemDTO.class);
    }


    /**
     * 查询数据
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<DictionaryItemDTO> findByIds(@RequestParam List<String> ids,
                                                      @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var entities = this.mapper.findBy(Conditions.of(DictionaryItemEntity.class).in(DictionaryItemEntity::getId, ids).eq(DictionaryItemEntity::getTenantCode, tenant));

        return DTO.wrap(entities, DictionaryItemDTO.class);
    }

    /**
     * 查询数据
     *
     * @param limit      获取前 N 条数据
     * @param offset     偏移量
     * @param conditions 过滤条件
     * @param orders     排序条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<DictionaryItemDTO> findBy(@RequestParam(required = false) Long limit,
                                                   @RequestParam(required = false) Long offset,
                                                   @RequestParam Conditions<DictionaryItemEntity> conditions,
                                                   @RequestParam Orders<DictionaryItemEntity> orders,
                                                   @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(DictionaryItemEntity::getTenantCode, tenant);
        var list = this.mapper.findBy(limit, offset, conditions, orders);
        return DTO.wrap(list, DictionaryItemDTO.class);
    }

    /**
     * 分页查询数据
     *
     * @param pageIndex  分页下标
     * @param pageSize   分页大小
     * @param conditions 过滤条件
     * @param orders     排序条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public @Nonnull Page<DictionaryItemDTO> pageBy(@RequestParam long pageIndex,
                                                   @RequestParam long pageSize,
                                                   @RequestParam Conditions<DictionaryItemEntity> conditions,
                                                   @RequestParam Orders<DictionaryItemEntity> orders,
                                                   @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(DictionaryItemEntity::getTenantCode, tenant);
        var page = this.mapper.findPageBy(pageIndex, pageSize, conditions, orders);
        return DTO.wrap(page, DictionaryItemDTO.class);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public Long countBy(@RequestParam Conditions<DictionaryItemEntity> conditions,
                        @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(DictionaryItemEntity::getTenantCode, tenant);
        return this.mapper.countBy(conditions);
    }
}
