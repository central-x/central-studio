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

package central.provider.graphql.gateway.query;

import central.provider.graphql.DTO;
import central.bean.Page;
import central.provider.graphql.gateway.dto.GatewayFilterDTO;
import central.provider.graphql.gateway.entity.GatewayFilterEntity;
import central.provider.graphql.gateway.mapper.GatewayFilterMapper;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.starter.graphql.annotation.GraphQLBatchLoader;
import central.starter.graphql.annotation.GraphQLFetcher;
import central.starter.graphql.annotation.GraphQLSchema;
import central.web.XForwardedHeaders;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Gateway Filter
 * <p>
 * 网关过滤器
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
@Component
@GraphQLSchema(path = "gateway/query", types = GatewayFilterDTO.class)
public class GatewayFilterQuery {
    @Setter(onMethod_ = @Autowired)
    private GatewayFilterMapper mapper;

    /**
     * 批量数据加载器
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    @GraphQLBatchLoader
    public @Nonnull Map<String, GatewayFilterDTO> batchLoader(@RequestParam List<String> ids,
                                                                     @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.mapper.findBy(Conditions.of(GatewayFilterEntity.class).in(GatewayFilterEntity::getId, ids).eq(GatewayFilterEntity::getTenantCode, tenant))
                .stream()
                .map(it -> DTO.wrap(it, GatewayFilterDTO.class))
                .collect(Collectors.toMap(GatewayFilterDTO::getId, it -> it));
    }

    /**
     * 根据主键查询数据
     *
     * @param id     主键
     * @param tenant 租户标识
     */
    @GraphQLFetcher
    public @Nullable GatewayFilterDTO findById(@RequestParam String id,
                                                      @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var entity = this.mapper.findFirstBy(Conditions.of(GatewayFilterEntity.class).eq(GatewayFilterEntity::getId, id).eq(GatewayFilterEntity::getTenantCode, tenant));
        return DTO.wrap(entity, GatewayFilterDTO.class);
    }


    /**
     * 查询数据
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<GatewayFilterDTO> findByIds(@RequestParam List<String> ids,
                                                            @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var entities = this.mapper.findBy(Conditions.of(GatewayFilterEntity.class).in(GatewayFilterEntity::getId, ids).eq(GatewayFilterEntity::getTenantCode, tenant));

        return DTO.wrap(entities, GatewayFilterDTO.class);
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
    public @Nonnull List<GatewayFilterDTO> findBy(@RequestParam(required = false) Long limit,
                                                         @RequestParam(required = false) Long offset,
                                                         @RequestParam Conditions<GatewayFilterEntity> conditions,
                                                         @RequestParam Orders<GatewayFilterEntity> orders,
                                                         @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(GatewayFilterEntity::getTenantCode, tenant);
        var list = this.mapper.findBy(limit, offset, conditions, orders);
        return DTO.wrap(list, GatewayFilterDTO.class);
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
    public @Nonnull Page<GatewayFilterDTO> pageBy(@RequestParam long pageIndex,
                                                         @RequestParam long pageSize,
                                                         @RequestParam Conditions<GatewayFilterEntity> conditions,
                                                         @RequestParam Orders<GatewayFilterEntity> orders,
                                                         @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(GatewayFilterEntity::getTenantCode, tenant);
        var page = this.mapper.findPageBy(pageIndex, pageSize, conditions, orders);
        return DTO.wrap(page, GatewayFilterDTO.class);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public Long countBy(@RequestParam Conditions<GatewayFilterEntity> conditions,
                        @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(GatewayFilterEntity::getTenantCode, tenant);
        return this.mapper.countBy(conditions);
    }
}
