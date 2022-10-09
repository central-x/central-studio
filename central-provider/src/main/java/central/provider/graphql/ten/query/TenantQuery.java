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

package central.provider.graphql.ten.query;

import central.api.DTO;
import central.bean.Page;
import central.lang.Assertx;
import central.provider.graphql.ten.dto.TenantDTO;
import central.provider.graphql.ten.entity.TenantEntity;
import central.provider.graphql.ten.mapper.TenantMapper;
import central.sql.Conditions;
import central.sql.Orders;
import central.starter.graphql.annotation.GraphQLBatchLoader;
import central.starter.graphql.annotation.GraphQLFetcher;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLSchema;
import central.starter.web.http.XForwardedHeaders;
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
 * Tenant Query
 * 租户查询
 *
 * @author Alan Yeh
 * @since 2022/10/03
 */
@Component
@GraphQLSchema(path = "ten/query", types = {TenantDTO.class, TenantApplicationQuery.class})
public class TenantQuery {
    @Setter(onMethod_ = @Autowired)
    private TenantMapper mapper;

    /**
     * 批量数据加载器
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    @GraphQLBatchLoader
    public @Nonnull Map<String, TenantDTO> batchLoader(@RequestParam List<String> ids,
                                                       @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        return this.mapper.findBy(Conditions.of(TenantEntity.class).in(TenantEntity::getId, ids))
                .stream()
                .map(it -> DTO.wrap(it, TenantDTO.class))
                .collect(Collectors.toMap(TenantDTO::getId, it -> it));
    }

    /**
     * 根据主键查询数据
     *
     * @param id     主键
     * @param tenant 租户标识
     */
    @GraphQLFetcher
    public @Nullable TenantDTO findById(@RequestParam String id,
                                        @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        var entity = this.mapper.findFirstBy(Conditions.of(TenantEntity.class).eq(TenantEntity::getId, id));
        return DTO.wrap(entity, TenantDTO.class);
    }


    /**
     * 查询数据
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<TenantDTO> findByIds(@RequestParam List<String> ids,
                                              @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        var entities = this.mapper.findBy(Conditions.of(TenantEntity.class).in(TenantEntity::getId, ids));

        return DTO.wrap(entities, TenantDTO.class);
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
    public @Nonnull List<TenantDTO> findBy(@RequestParam(required = false) Long limit,
                                           @RequestParam(required = false) Long offset,
                                           @RequestParam Conditions<TenantEntity> conditions,
                                           @RequestParam Orders<TenantEntity> orders,
                                           @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        var list = this.mapper.findBy(limit, offset, conditions, orders);
        return DTO.wrap(list, TenantDTO.class);
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
    public @Nonnull Page<TenantDTO> pageBy(@RequestParam long pageIndex,
                                           @RequestParam long pageSize,
                                           @RequestParam Conditions<TenantEntity> conditions,
                                           @RequestParam Orders<TenantEntity> orders,
                                           @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        var page = this.mapper.findPageBy(pageIndex, pageSize, conditions, orders);
        return DTO.wrap(page, TenantDTO.class);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public Long countBy(@RequestParam Conditions<TenantEntity> conditions,
                        @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        return this.mapper.countBy(conditions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 关联查询
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Tenant Application Query
     * 租户与应用关联关系查询
     */
    @GraphQLGetter
    public TenantApplicationQuery getApplications(@Autowired TenantApplicationQuery query) {
        return query;
    }
}
