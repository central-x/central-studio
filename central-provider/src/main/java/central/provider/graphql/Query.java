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

package central.provider.graphql;

import central.provider.graphql.authority.AuthorityQuery;
import central.provider.graphql.gateway.GatewayQuery;
import central.provider.graphql.log.LogQuery;
import central.provider.graphql.multicast.MulticastQuery;
import central.provider.graphql.security.SecurityQuery;
import central.provider.graphql.organization.OrganizationQuery;
import central.provider.graphql.storage.StorageQuery;
import central.provider.graphql.system.SystemQuery;
import central.provider.graphql.saas.SaasQuery;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * GraphQL Query
 *
 * @author Alan Yeh
 * @since 2022/10/02
 */
@Component
@GraphQLSchema(types = {AuthorityQuery.class, OrganizationQuery.class, SystemQuery.class, SaasQuery.class, SecurityQuery.class, LogQuery.class, StorageQuery.class, MulticastQuery.class, GatewayQuery.class})
public class Query {
    /**
     * Authority Query
     * <p>
     * 权限相关相询
     */
    @GraphQLGetter
    public AuthorityQuery getAuthority(@Autowired AuthorityQuery query) {
        return query;
    }

    /**
     * Organization Query
     * <p>
     * 组织架构相关查询
     */
    @GraphQLGetter
    public OrganizationQuery getOrganization(@Autowired OrganizationQuery query) {
        return query;
    }

    /**
     * System Query
     * <p>
     * 系统配置相关查询
     */
    @GraphQLGetter
    public SystemQuery getSystem(@Autowired SystemQuery query) {
        return query;
    }

    /**
     * Tenant Query
     * <p>
     * 租户相关查询
     */
    @GraphQLGetter
    public SaasQuery getSaas(@Autowired SaasQuery query) {
        return query;
    }

    /**
     * Security Query
     * <p>
     * 认证相关查询
     */
    @GraphQLGetter
    public SecurityQuery getSecurity(@Autowired SecurityQuery query) {
        return query;
    }

    /**
     * Log Query
     * <p>
     * 日志中心相关查询
     */
    @GraphQLGetter
    public LogQuery getLog(@Autowired LogQuery query) {
        return query;
    }

    /**
     * Storage Query
     * <p>
     * 存储中心相关查询
     */
    @GraphQLGetter
    public StorageQuery getStorage(@Autowired StorageQuery query) {
        return query;
    }

    /**
     * Multicast Query
     * <p>
     * 广播中心查询
     */
    @GraphQLGetter
    public MulticastQuery getMulticast(@Autowired MulticastQuery query) {
        return query;
    }

    /**
     * Gateway Query
     * <p>
     * 网关中心查询
     */
    @GraphQLGetter
    public GatewayQuery getGateway(@Autowired GatewayQuery query) {
        return query;
    }
}
