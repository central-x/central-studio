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

package central.provider.graphql.security.query;

import central.api.DTO;
import central.bean.Page;
import central.provider.ApplicationProperties;
import central.provider.graphql.security.dto.PasswordDTO;
import central.provider.graphql.security.entity.PasswordEntity;
import central.provider.graphql.security.mapper.PasswordMapper;
import central.security.Passwordx;
import central.sql.Conditions;
import central.sql.Orders;
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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Password Query
 * 密码查询
 *
 * @author Alan Yeh
 * @since 2022/10/07
 */
@Component
@GraphQLSchema(path = "security/query", types = PasswordDTO.class)
public class PasswordQuery {
    @Setter(onMethod_ = @Autowired)
    private PasswordMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    private PasswordDTO getSupervisorPassword(String tenant) {
        var password = new PasswordEntity();
        password.setId(properties.getSupervisor().getUsername());
        password.setAccountId(properties.getSupervisor().getUsername());
        password.setValue(Passwordx.encrypt(Passwordx.digest(properties.getSupervisor().getPassword())));
        password.setTenantCode(tenant);
        password.updateCreator(properties.getSupervisor().getUsername());
        return DTO.wrap(password, PasswordDTO.class);
    }

    /**
     * 批量数据加载器
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    @GraphQLBatchLoader
    public @Nonnull Map<String, PasswordDTO> batchLoader(@RequestParam List<String> ids,
                                                         @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var result = this.mapper.findBy(Conditions.of(PasswordEntity.class).in(PasswordEntity::getId, ids).eq(PasswordEntity::getTenantCode, tenant))
                .stream()
                .map(it -> DTO.wrap(it, PasswordDTO.class))
                .collect(Collectors.toMap(PasswordDTO::getId, it -> it));
        if (ids.contains(properties.getSupervisor().getUsername())) {
            result.put(properties.getSupervisor().getUsername(), getSupervisorPassword(tenant));
        }
        return result;
    }

    /**
     * 根据主键查询数据
     *
     * @param id     主键
     * @param tenant 租户标识
     */
    @GraphQLFetcher
    public @Nullable PasswordDTO findById(@RequestParam String id,
                                          @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        if (Objects.equals(properties.getSupervisor().getUsername(), id)) {
            return getSupervisorPassword(tenant);
        } else {
            var entity = this.mapper.findFirstBy(Conditions.of(PasswordEntity.class).eq(PasswordEntity::getId, id).eq(PasswordEntity::getTenantCode, tenant));
            return DTO.wrap(entity, PasswordDTO.class);
        }
    }


    /**
     * 查询数据
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<PasswordDTO> findByIds(@RequestParam List<String> ids,
                                                @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var entities = this.mapper.findBy(Conditions.of(PasswordEntity.class).in(PasswordEntity::getId, ids).eq(PasswordEntity::getTenantCode, tenant));

        return DTO.wrap(entities, PasswordDTO.class);
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
    public @Nonnull List<PasswordDTO> findBy(@RequestParam(required = false) Long limit,
                                             @RequestParam(required = false) Long offset,
                                             @RequestParam Conditions<PasswordEntity> conditions,
                                             @RequestParam Orders<PasswordEntity> orders,
                                             @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(PasswordEntity::getTenantCode, tenant);
        var list = this.mapper.findBy(limit, offset, conditions, orders);
        return DTO.wrap(list, PasswordDTO.class);
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
    public @Nonnull Page<PasswordDTO> pageBy(@RequestParam long pageIndex,
                                             @RequestParam long pageSize,
                                             @RequestParam Conditions<PasswordEntity> conditions,
                                             @RequestParam Orders<PasswordEntity> orders,
                                             @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(PasswordEntity::getTenantCode, tenant);
        var page = this.mapper.findPageBy(pageIndex, pageSize, conditions, orders);
        return DTO.wrap(page, PasswordDTO.class);
    }

    /**
     * 查询符合条件的数据数量
     *
     * @param conditions 筛选条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public Long countBy(@RequestParam Conditions<PasswordEntity> conditions,
                        @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(PasswordEntity::getTenantCode, tenant);
        return this.mapper.countBy(conditions);
    }
}
