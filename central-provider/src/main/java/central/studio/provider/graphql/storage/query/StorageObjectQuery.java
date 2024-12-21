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

import central.bean.Page;
import central.provider.graphql.DTO;
import central.studio.provider.graphql.storage.dto.StorageObjectDTO;
import central.studio.provider.database.persistence.storage.entity.StorageObjectEntity;
import central.studio.provider.database.persistence.storage.mapper.StorageObjectMapper;
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
 * Storage Object
 * <p>
 * 存储对象
 *
 * @author Alan Yeh
 * @since 2022/10/30
 */
@Component
@GraphQLSchema(path = "storage/query", types = StorageObjectDTO.class)
public class StorageObjectQuery {
    @Setter(onMethod_ = @Autowired)
    private StorageObjectMapper mapper;

    /**
     * 批量数据加载器
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    @GraphQLBatchLoader
    public @Nonnull Map<String, StorageObjectDTO> batchLoader(@RequestParam List<String> ids,
                                                              @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.mapper.findBy(Conditions.of(StorageObjectEntity.class).in(StorageObjectEntity::getId, ids).eq(StorageObjectEntity::getTenantCode, tenant))
                .stream()
                .map(it -> DTO.wrap(it, StorageObjectDTO.class))
                .collect(Collectors.toMap(StorageObjectDTO::getId, it -> it));
    }

    /**
     * 根据主键查询数据
     *
     * @param id     主键
     * @param tenant 租户标识
     */
    @GraphQLFetcher
    public @Nullable StorageObjectDTO findById(@RequestParam String id,
                                               @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var entity = this.mapper.findFirstBy(Conditions.of(StorageObjectEntity.class).eq(StorageObjectEntity::getId, id).eq(StorageObjectEntity::getTenantCode, tenant));
        return DTO.wrap(entity, StorageObjectDTO.class);
    }


    /**
     * 查询数据
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<StorageObjectDTO> findByIds(@RequestParam List<String> ids,
                                                     @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var entities = this.mapper.findBy(Conditions.of(StorageObjectEntity.class).in(StorageObjectEntity::getId, ids).eq(StorageObjectEntity::getTenantCode, tenant));

        return DTO.wrap(entities, StorageObjectDTO.class);
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
    public @Nonnull List<StorageObjectDTO> findBy(@RequestParam(required = false) Long limit,
                                                  @RequestParam(required = false) Long offset,
                                                  @RequestParam Conditions<StorageObjectEntity> conditions,
                                                  @RequestParam Orders<StorageObjectEntity> orders,
                                                  @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(StorageObjectEntity::getTenantCode, tenant);
        var list = this.mapper.findBy(limit, offset, conditions, orders);
        return DTO.wrap(list, StorageObjectDTO.class);
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
    public @Nonnull Page<StorageObjectDTO> pageBy(@RequestParam long pageIndex,
                                                  @RequestParam long pageSize,
                                                  @RequestParam Conditions<StorageObjectEntity> conditions,
                                                  @RequestParam Orders<StorageObjectEntity> orders,
                                                  @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(StorageObjectEntity::getTenantCode, tenant);
        var page = this.mapper.findPageBy(pageIndex, pageSize, conditions, orders);
        return DTO.wrap(page, StorageObjectDTO.class);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public Long countBy(@RequestParam Conditions<StorageObjectEntity> conditions,
                        @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(StorageObjectEntity::getTenantCode, tenant);
        return this.mapper.countBy(conditions);
    }
}
