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

package central.studio.provider.graphql.saas.query;

import central.bean.Page;
import central.lang.Assertx;
import central.provider.graphql.DTO;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.starter.graphql.annotation.GraphQLBatchLoader;
import central.starter.graphql.annotation.GraphQLFetcher;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLSchema;
import central.studio.provider.database.persistence.saas.TenantPersistence;
import central.studio.provider.database.persistence.saas.entity.TenantEntity;
import central.studio.provider.graphql.saas.dto.TenantDTO;
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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Tenant Query
 * <p>
 * 租户查询
 *
 * @author Alan Yeh
 * @since 2022/10/03
 */
@Component
@GraphQLSchema(path = "saas/query", types = {TenantDTO.class, TenantApplicationQuery.class})
public class TenantQuery {

    @Setter(onMethod_ = @Autowired)
    private TenantPersistence persistence;

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
        var data = this.persistence.findByIds(ids, Columns.all());
        return DTO.wrap(data, TenantDTO.class).stream().collect(Collectors.toMap(TenantDTO::getId, Function.identity()));
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
        var data = this.persistence.findById(id, Columns.all());
        return DTO.wrap(data, TenantDTO.class);
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

        var data = this.persistence.findByIds(ids, Columns.all());
        return DTO.wrap(data, TenantDTO.class);
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

        var data = this.persistence.findBy(limit, offset, Columns.all(), conditions, orders);
        return DTO.wrap(data, TenantDTO.class);
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

        var data = this.persistence.findPageBy(pageIndex, pageSize, Columns.all(), conditions, orders);
        return DTO.wrap(data, TenantDTO.class);
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

        return this.persistence.countBy(conditions);
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
