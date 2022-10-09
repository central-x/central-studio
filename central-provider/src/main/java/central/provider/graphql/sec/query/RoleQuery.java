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

package central.provider.graphql.sec.query;

import central.api.DTO;
import central.bean.Page;
import central.provider.graphql.sec.dto.RoleDTO;
import central.provider.graphql.sec.entity.RoleEntity;
import central.provider.graphql.sec.mapper.RoleMapper;
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
 * Role Query
 * 角色查询
 *
 * @author Alan Yeh
 * @since 2022/10/02
 */
@Component
@GraphQLSchema(path = "sec/query", types = RoleDTO.class)
public class RoleQuery {
    @Setter(onMethod_ = @Autowired)
    private RoleMapper mapper;

    /**
     * 批量数据加载器
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    @GraphQLBatchLoader
    public @Nonnull Map<String, RoleDTO> batchLoader(@RequestParam List<String> ids,
                                                     @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.mapper.findBy(Conditions.of(RoleEntity.class).in(RoleEntity::getId, ids).eq(RoleEntity::getTenantCode, tenant))
                .stream()
                .map(it -> DTO.wrap(it, RoleDTO.class))
                .collect(Collectors.toMap(RoleDTO::getId, it -> it));
    }

    /**
     * 根据主键查询数据
     *
     * @param id     主键
     * @param tenant 租户标识
     */
    @GraphQLFetcher
    public @Nullable RoleDTO findById(@RequestParam String id,
                                      @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var entity = this.mapper.findFirstBy(Conditions.of(RoleEntity.class).eq(RoleEntity::getId, id).eq(RoleEntity::getTenantCode, tenant));
        return DTO.wrap(entity, RoleDTO.class);
    }


    /**
     * 查询数据
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<RoleDTO> findByIds(@RequestParam List<String> ids,
                                            @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var entities = this.mapper.findBy(Conditions.of(RoleEntity.class).in(RoleEntity::getId, ids).eq(RoleEntity::getTenantCode, tenant));

        return DTO.wrap(entities, RoleDTO.class);
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
    public @Nonnull List<RoleDTO> findBy(@RequestParam(required = false) Long limit,
                                         @RequestParam(required = false) Long offset,
                                         @RequestParam Conditions<RoleEntity> conditions,
                                         @RequestParam Orders<RoleEntity> orders,
                                         @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(RoleEntity::getTenantCode, tenant);
        var list = this.mapper.findBy(limit, offset, conditions, orders);
        return DTO.wrap(list, RoleDTO.class);
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
    public @Nonnull Page<RoleDTO> pageBy(@RequestParam long pageIndex,
                                         @RequestParam long pageSize,
                                         @RequestParam Conditions<RoleEntity> conditions,
                                         @RequestParam Orders<RoleEntity> orders,
                                         @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(RoleEntity::getTenantCode, tenant);
        var page = this.mapper.findPageBy(pageIndex, pageSize, conditions, orders);
        return DTO.wrap(page, RoleDTO.class);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public Long countBy(@RequestParam Conditions<RoleEntity> conditions,
                        @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(RoleEntity::getTenantCode, tenant);
        return this.mapper.countBy(conditions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 关联查询
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * RolePermission Query
     * 角色权限关联关系查询
     */
    @GraphQLGetter
    public RolePermissionQuery getPermissions(@Autowired RolePermissionQuery query) {
        return query;
    }

    /**
     * RolePrincipal Query
     * 角色主体关联关系查询
     */
    @GraphQLGetter
    public RolePrincipalQuery getPrincipals(@Autowired RolePrincipalQuery query) {
        return query;
    }

    /**
     * RoleRange Query
     * 角色范围关联关系查询
     */
    @GraphQLGetter
    public RoleRangeQuery getRanges(@Autowired RoleRangeQuery query) {
        return query;
    }
}
