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

package central.provider.graphql.authority.query;

import central.bean.Page;
import central.provider.graphql.authority.dto.PermissionDTO;
import central.provider.graphql.authority.service.PermissionService;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.starter.graphql.annotation.GraphQLBatchLoader;
import central.starter.graphql.annotation.GraphQLFetcher;
import central.starter.graphql.annotation.GraphQLSchema;
import central.web.XForwardedHeaders;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.SelectedField;
import lombok.Setter;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Permission Query
 * 权限查询
 *
 * @author Alan Yeh
 * @since 2022/10/03
 */
@Component
@GraphQLSchema(path = "authority/query", types = PermissionDTO.class)
public class PermissionQuery {

    @Setter(onMethod_ = @Autowired)
    private PermissionService service;

    /**
     * 批量数据加载器
     *
     * @param environment Graphql 批量加载上下文环境
     * @param ids         主键
     * @param tenant      租户标识
     */
    @GraphQLBatchLoader
    public @Nonnull Map<String, PermissionDTO> batchLoader(BatchLoaderEnvironment environment,
                                                           @RequestParam List<String> ids,
                                                           @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var fields = environment.getKeyContextsList().stream().filter(it -> it instanceof DataFetchingEnvironment)
                .map(it -> (DataFetchingEnvironment) it)
                .flatMap(it -> it.getSelectionSet().getFields().stream())
                .map(SelectedField::getName).distinct().toArray(String[]::new);

        return this.service.findByIds(ids, Columns.of(PermissionDTO.class, fields), tenant)
                .stream().collect(Collectors.toMap(PermissionDTO::getId, Function.identity()));
    }

    /**
     * 根据主键查询数据
     *
     * @param environment Graphql 查询上下文环境
     * @param id          主键
     * @param tenant      租户标识
     */
    @GraphQLFetcher
    public @Nullable PermissionDTO findById(DataFetchingEnvironment environment,
                                            @RequestParam String id,
                                            @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var columns = Columns.of(PermissionDTO.class, environment.getSelectionSet().getFields().stream()
                .filter(it -> "findById".equals(it.getParentField().getName()))
                .map(SelectedField::getName).toList().toArray(new String[0]));

        return this.service.findById(id, columns, tenant);
    }


    /**
     * 查询数据
     *
     * @param environment Graphql 查询上下文环境
     * @param ids         主键
     * @param tenant      租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<PermissionDTO> findByIds(DataFetchingEnvironment environment,
                                                  @RequestParam List<String> ids,
                                                  @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var columns = Columns.of(PermissionDTO.class, environment.getSelectionSet().getFields().stream()
                .filter(it -> "findByIds".equals(it.getParentField().getName()))
                .map(SelectedField::getName).toList().toArray(new String[0]));

        return this.service.findByIds(ids, columns, tenant);
    }

    /**
     * 查询数据
     *
     * @param environment Graphql 查询上下文环境
     * @param limit       获取前 N 条数据
     * @param offset      偏移量
     * @param conditions  过滤条件
     * @param orders      排序条件
     * @param tenant      租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<PermissionDTO> findBy(DataFetchingEnvironment environment,
                                               @RequestParam(required = false) Long limit,
                                               @RequestParam(required = false) Long offset,
                                               @RequestParam Conditions<PermissionDTO> conditions,
                                               @RequestParam Orders<PermissionDTO> orders,
                                               @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var columns = Columns.of(PermissionDTO.class, environment.getSelectionSet().getFields().stream()
                .filter(it -> "findBy".equals(it.getParentField().getName()))
                .map(SelectedField::getName).toList().toArray(new String[0]));

        return this.service.findBy(limit, offset, columns, conditions, orders, tenant);
    }

    /**
     * 分页查询数据
     *
     * @param environment Graphql 查询上下文环境
     * @param pageIndex   分页下标
     * @param pageSize    分页大小
     * @param conditions  过滤条件
     * @param orders      排序条件
     * @param tenant      租户标识
     */
    @GraphQLFetcher
    public @Nonnull Page<PermissionDTO> pageBy(DataFetchingEnvironment environment,
                                               @RequestParam long pageIndex,
                                               @RequestParam long pageSize,
                                               @RequestParam Conditions<PermissionDTO> conditions,
                                               @RequestParam Orders<PermissionDTO> orders,
                                               @RequestHeader(XForwardedHeaders.TENANT) String tenant) {

        var columns = Columns.of(PermissionDTO.class, environment.getSelectionSet().getFields().stream()
                .filter(it -> "Page.data".equals(it.getParentField().getName()))
                .map(SelectedField::getName).toList().toArray(new String[0]));

        return this.service.pageBy(pageIndex, pageSize, columns, conditions, orders, tenant);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public Long countBy(@RequestParam Conditions<PermissionDTO> conditions,
                        @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.service.countBy(conditions, tenant);
    }
}
