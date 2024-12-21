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

package central.studio.provider.graphql.authority.query;

import central.bean.Page;
import central.provider.graphql.DTO;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.starter.graphql.annotation.GraphQLBatchLoader;
import central.starter.graphql.annotation.GraphQLFetcher;
import central.starter.graphql.annotation.GraphQLSchema;
import central.studio.provider.graphql.authority.dto.RolePrincipalDTO;
import central.studio.provider.database.persistence.authority.RolePrincipalPersistence;
import central.web.XForwardedHeaders;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.SelectedField;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Setter;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Role Principal Relation Query
 * <p>
 * 角色与主体关联关系查询
 *
 * @author Alan Yeh
 * @since 2022/10/02
 */
@Component
@GraphQLSchema(path = "authority/query", types = RolePrincipalDTO.class)
public class RolePrincipalQuery {

    @Setter(onMethod_ = @Autowired)
    private RolePrincipalPersistence persistence;

    /**
     * 批量数据加载器
     *
     * @param environment Graphql 批量加载上下文环境
     * @param ids         主键
     * @param tenant      租户标识
     */
    @GraphQLBatchLoader
    public @Nonnull Map<String, RolePrincipalDTO> batchLoader(BatchLoaderEnvironment environment,
                                                              @RequestParam List<String> ids,
                                                              @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var fields = environment.getKeyContextsList().stream().filter(it -> it instanceof DataFetchingEnvironment)
                .map(it -> (DataFetchingEnvironment) it)
                .flatMap(it -> it.getSelectionSet().getFields().stream())
                .map(SelectedField::getName).distinct().toArray(String[]::new);

        var data = this.persistence.findByIds(ids, Columns.of(RolePrincipalDTO.class, fields), tenant);
        return DTO.wrap(data, RolePrincipalDTO.class).stream()
                .collect(Collectors.toMap(RolePrincipalDTO::getId, Function.identity()));
    }

    /**
     * 根据主键查询数据
     *
     * @param environment Graphql 查询上下文环境
     * @param id          主键
     * @param tenant      租户标识
     */
    @GraphQLFetcher
    public @Nullable RolePrincipalDTO findById(DataFetchingEnvironment environment,
                                               @RequestParam String id,
                                               @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var columns = Columns.of(RolePrincipalDTO.class, environment.getSelectionSet().getFields().stream()
                .filter(it -> "findById".equals(it.getParentField().getName()))
                .map(SelectedField::getName).toList().toArray(new String[0]));

        var data = this.persistence.findById(id, columns, tenant);
        return DTO.wrap(data, RolePrincipalDTO.class);
    }


    /**
     * 查询数据
     *
     * @param environment Graphql 查询上下文环境
     * @param ids         主键
     * @param tenant      租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<RolePrincipalDTO> findByIds(DataFetchingEnvironment environment,
                                                     @RequestParam List<String> ids,
                                                     @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var columns = Columns.of(RolePrincipalDTO.class, environment.getSelectionSet().getFields().stream()
                .filter(it -> "findByIds".equals(it.getParentField().getName()))
                .map(SelectedField::getName).toList().toArray(new String[0]));

        var data = this.persistence.findByIds(ids, columns, tenant);
        return DTO.wrap(data, RolePrincipalDTO.class);
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
    public @Nonnull List<RolePrincipalDTO> findBy(DataFetchingEnvironment environment,
                                                  @RequestParam(required = false) Long limit,
                                                  @RequestParam(required = false) Long offset,
                                                  @RequestParam Conditions<RolePrincipalDTO> conditions,
                                                  @RequestParam Orders<RolePrincipalDTO> orders,
                                                  @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var columns = Columns.of(RolePrincipalDTO.class, environment.getSelectionSet().getFields().stream()
                .filter(it -> "findBy".equals(it.getParentField().getName()))
                .map(SelectedField::getName).toList().toArray(new String[0]));

        var data = this.persistence.findBy(limit, offset, columns, conditions, orders, tenant);
        return DTO.wrap(data, RolePrincipalDTO.class);
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
    public @Nonnull Page<RolePrincipalDTO> pageBy(DataFetchingEnvironment environment,
                                                  @RequestParam long pageIndex,
                                                  @RequestParam long pageSize,
                                                  @RequestParam Conditions<RolePrincipalDTO> conditions,
                                                  @RequestParam Orders<RolePrincipalDTO> orders,
                                                  @RequestHeader(XForwardedHeaders.TENANT) String tenant) {

        var columns = Columns.of(RolePrincipalDTO.class, environment.getSelectionSet().getFields().stream()
                .filter(it -> "Page.data".equals(it.getParentField().getName()))
                .map(SelectedField::getName).toList().toArray(new String[0]));

        var data = this.persistence.pageBy(pageIndex, pageSize, columns, conditions, orders, tenant);
        return DTO.wrap(data, RolePrincipalDTO.class);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public Long countBy(@RequestParam Conditions<RolePrincipalDTO> conditions,
                        @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.persistence.countBy(conditions, tenant);
    }
}
