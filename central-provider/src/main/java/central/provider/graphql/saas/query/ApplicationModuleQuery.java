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

package central.provider.graphql.saas.query;

import central.api.DTO;
import central.bean.Page;
import central.lang.Assertx;
import central.provider.graphql.saas.dto.ApplicationModuleDTO;
import central.provider.graphql.saas.entity.ApplicationModuleEntity;
import central.provider.graphql.saas.mapper.ApplicationModuleMapper;
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
 * ApplicationModule Query
 * <p>
 * 应用模块查询
 *
 * @author Alan Yeh
 * @since 2022/10/03
 */
@Component
@GraphQLSchema(path = "saas/query", types = ApplicationModuleDTO.class)
public class ApplicationModuleQuery {
    @Setter(onMethod_ = @Autowired)
    private ApplicationModuleMapper mapper;

    /**
     * 批量数据加载器
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    @GraphQLBatchLoader
    public @Nonnull Map<String, ApplicationModuleDTO> batchLoader(@RequestParam List<String> ids,
                                                                  @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        return this.mapper.findByIds(ids)
                .stream()
                .map(it -> DTO.wrap(it, ApplicationModuleDTO.class))
                .collect(Collectors.toMap(ApplicationModuleDTO::getId, it -> it));
    }

    /**
     * 根据主键查询数据
     *
     * @param id     主键
     * @param tenant 租户标识
     */
    @GraphQLFetcher
    public @Nullable ApplicationModuleDTO findById(@RequestParam String id,
                                                   @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        var entity = this.mapper.findById(id);
        return DTO.wrap(entity, ApplicationModuleDTO.class);
    }


    /**
     * 查询数据
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<ApplicationModuleDTO> findByIds(@RequestParam List<String> ids,
                                                         @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        var entities = this.mapper.findByIds(ids);

        return DTO.wrap(entities, ApplicationModuleDTO.class);
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
    public @Nonnull List<ApplicationModuleDTO> findBy(@RequestParam(required = false) Long limit,
                                                      @RequestParam(required = false) Long offset,
                                                      @RequestParam Conditions<ApplicationModuleEntity> conditions,
                                                      @RequestParam Orders<ApplicationModuleEntity> orders,
                                                      @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        var list = this.mapper.findBy(limit, offset, conditions, orders);
        return DTO.wrap(list, ApplicationModuleDTO.class);
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
    public @Nonnull Page<ApplicationModuleDTO> pageBy(@RequestParam long pageIndex,
                                                      @RequestParam long pageSize,
                                                      @RequestParam Conditions<ApplicationModuleEntity> conditions,
                                                      @RequestParam Orders<ApplicationModuleEntity> orders,
                                                      @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        var page = this.mapper.findPageBy(pageIndex, pageSize, conditions, orders);
        return DTO.wrap(page, ApplicationModuleDTO.class);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public Long countBy(@RequestParam Conditions<ApplicationModuleEntity> conditions,
                        @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        return this.mapper.countBy(conditions);
    }
}
