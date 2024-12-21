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

package central.studio.provider.graphql.organization.query;

import central.bean.Page;
import central.provider.graphql.DTO;
import central.studio.provider.graphql.organization.dto.AccountDTO;
import central.studio.provider.database.persistence.organization.AccountPersistence;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.starter.graphql.annotation.GraphQLBatchLoader;
import central.starter.graphql.annotation.GraphQLFetcher;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLSchema;
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
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Account Query
 * 帐户查询
 *
 * @author Alan Yeh
 * @since 2022/10/03
 */
@Component
@GraphQLSchema(path = "organization/query", types = {AccountDTO.class, AccountUnitQuery.class})
public class AccountQuery {

    @Setter(onMethod_ = @Autowired)
    private AccountPersistence persistence;


    /**
     * 批量数据加载器
     *
     * @param environment Graphql 批量加载上下文环境
     * @param ids         主键
     * @param tenant      租户标识
     */
    @GraphQLBatchLoader
    public @Nonnull Map<String, AccountDTO> batchLoader(BatchLoaderEnvironment environment,
                                                        @RequestParam Set<String> ids,
                                                        @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var fields = environment.getKeyContextsList().stream().filter(it -> it instanceof DataFetchingEnvironment)
                .map(it -> (DataFetchingEnvironment) it)
                .flatMap(it -> it.getSelectionSet().getFields().stream())
                .map(SelectedField::getName).distinct().toArray(String[]::new);

        var data = this.persistence.findByIds(ids.stream().toList(), Columns.of(AccountDTO.class, fields), tenant);
        return DTO.wrap(data, AccountDTO.class).stream()
                .collect(Collectors.toMap(AccountDTO::getId, Function.identity()));
    }

    /**
     * 查询数据
     *
     * @param environment Graphql 查询上下文环境
     * @param id          主键
     * @param tenant      租户标识
     */
    @GraphQLFetcher
    public @Nullable AccountDTO findById(DataFetchingEnvironment environment,
                                         @RequestParam String id,
                                         @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var columns = Columns.of(AccountDTO.class, environment.getSelectionSet().getFields().stream()
                .filter(it -> "findById".equals(it.getParentField().getName()))
                .map(SelectedField::getName).toList().toArray(new String[0]));

        var data = this.persistence.findById(id, columns, tenant);
        return DTO.wrap(data, AccountDTO.class);
    }

    /**
     * 查询数据
     *
     * @param environment Graphql 查询上下文环境
     * @param ids         主键
     * @param tenant      租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<AccountDTO> findByIds(DataFetchingEnvironment environment,
                                               @RequestParam List<String> ids,
                                               @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var columns = Columns.of(AccountDTO.class, environment.getSelectionSet().getFields().stream()
                .filter(it -> "findByIds".equals(it.getParentField().getName()))
                .map(SelectedField::getName).toList().toArray(new String[0]));

        var data = this.persistence.findByIds(ids, columns, tenant);
        return DTO.wrap(data, AccountDTO.class);
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
    public @Nonnull List<AccountDTO> findBy(DataFetchingEnvironment environment,
                                            @RequestParam(required = false) Long limit,
                                            @RequestParam(required = false) Long offset,
                                            @RequestParam Conditions<AccountDTO> conditions,
                                            @RequestParam Orders<AccountDTO> orders,
                                            @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var columns = Columns.of(AccountDTO.class, environment.getSelectionSet().getFields().stream()
                .filter(it -> "findBy".equals(it.getParentField().getName()))
                .map(SelectedField::getName).toList().toArray(new String[0]));

        var data = this.persistence.findBy(limit, offset, columns, conditions, orders, tenant);
        return DTO.wrap(data, AccountDTO.class);
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
    public @Nonnull Page<AccountDTO> pageBy(DataFetchingEnvironment environment,
                                            @RequestParam long pageIndex,
                                            @RequestParam long pageSize,
                                            @RequestParam Conditions<AccountDTO> conditions,
                                            @RequestParam Orders<AccountDTO> orders,
                                            @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var columns = Columns.of(AccountDTO.class, environment.getSelectionSet().getFields().stream()
                .filter(it -> "Page.data".equals(it.getParentField().getName()))
                .map(SelectedField::getName).toList().toArray(new String[0]));

        var data = this.persistence.pageBy(pageIndex, pageSize, columns, conditions, orders, tenant);
        return DTO.wrap(data, AccountDTO.class);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public Long countBy(@RequestParam Conditions<AccountDTO> conditions,
                        @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.persistence.countBy(conditions, tenant);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 关联查询
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Account Unit Query
     * 帐户与单位关联关系查询
     */
    @GraphQLGetter
    public AccountUnitQuery getUnits(@Autowired AccountUnitQuery query) {
        return query;
    }
}
