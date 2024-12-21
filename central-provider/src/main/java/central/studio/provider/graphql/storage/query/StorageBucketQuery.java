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

package central.studio.provider.graphql.storage.query;

import central.provider.graphql.DTO;
import central.bean.Page;
import central.studio.provider.graphql.storage.dto.StorageBucketDTO;
import central.studio.provider.database.persistence.storage.entity.StorageBucketEntity;
import central.studio.provider.database.persistence.storage.mapper.StorageBucketMapper;
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
 * Storage Bucket
 * <p>
 * 存储桶
 *
 * @author Alan Yeh
 * @since 2022/10/30
 */
@Component
@GraphQLSchema(path = "storage/query", types = StorageBucketDTO.class)
public class StorageBucketQuery {
    @Setter(onMethod_ = @Autowired)
    private StorageBucketMapper mapper;

    /**
     * 批量数据加载器
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    @GraphQLBatchLoader
    public @Nonnull Map<String, StorageBucketDTO> batchLoader(@RequestParam List<String> ids,
                                                              @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.mapper.findBy(Conditions.of(StorageBucketEntity.class).in(StorageBucketEntity::getId, ids).eq(StorageBucketEntity::getTenantCode, tenant))
                .stream()
                .map(it -> DTO.wrap(it, StorageBucketDTO.class))
                .collect(Collectors.toMap(StorageBucketDTO::getId, it -> it));
    }

    /**
     * 根据主键查询数据
     *
     * @param id     主键
     * @param tenant 租户标识
     */
    @GraphQLFetcher
    public @Nullable StorageBucketDTO findById(@RequestParam String id,
                                               @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var entity = this.mapper.findFirstBy(Conditions.of(StorageBucketEntity.class).eq(StorageBucketEntity::getId, id).eq(StorageBucketEntity::getTenantCode, tenant));
        return DTO.wrap(entity, StorageBucketDTO.class);
    }


    /**
     * 查询数据
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<StorageBucketDTO> findByIds(@RequestParam List<String> ids,
                                                     @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var entities = this.mapper.findBy(Conditions.of(StorageBucketEntity.class).in(StorageBucketEntity::getId, ids).eq(StorageBucketEntity::getTenantCode, tenant));

        return DTO.wrap(entities, StorageBucketDTO.class);
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
    public @Nonnull List<StorageBucketDTO> findBy(@RequestParam(required = false) Long limit,
                                                  @RequestParam(required = false) Long offset,
                                                  @RequestParam Conditions<StorageBucketEntity> conditions,
                                                  @RequestParam Orders<StorageBucketEntity> orders,
                                                  @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(StorageBucketEntity::getTenantCode, tenant);
        var list = this.mapper.findBy(limit, offset, conditions, orders);
        return DTO.wrap(list, StorageBucketDTO.class);
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
    public @Nonnull Page<StorageBucketDTO> pageBy(@RequestParam long pageIndex,
                                                  @RequestParam long pageSize,
                                                  @RequestParam Conditions<StorageBucketEntity> conditions,
                                                  @RequestParam Orders<StorageBucketEntity> orders,
                                                  @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(StorageBucketEntity::getTenantCode, tenant);
        var page = this.mapper.findPageBy(pageIndex, pageSize, conditions, orders);
        return DTO.wrap(page, StorageBucketDTO.class);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public Long countBy(@RequestParam Conditions<StorageBucketEntity> conditions,
                        @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(StorageBucketEntity::getTenantCode, tenant);
        return this.mapper.countBy(conditions);
    }
}
