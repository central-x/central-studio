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

package central.studio.provider.graphql;

import central.studio.provider.graphql.authority.AuthorityMutation;
import central.studio.provider.graphql.gateway.GatewayMutation;
import central.studio.provider.graphql.identity.IdentityMutation;
import central.studio.provider.graphql.log.LogMutation;
import central.studio.provider.graphql.multicast.MulticastMutation;
import central.studio.provider.graphql.organization.OrganizationMutation;
import central.studio.provider.graphql.storage.StorageMutation;
import central.studio.provider.graphql.system.SystemMutation;
import central.studio.provider.graphql.saas.SaasMutation;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * GraphQL Mutation
 *
 * @author Alan Yeh
 * @since 2022/10/02
 */
@Component
@GraphQLSchema(types = {AuthorityMutation.class, IdentityMutation.class, OrganizationMutation.class, SystemMutation.class, SaasMutation.class, LogMutation.class, StorageMutation.class, MulticastMutation.class, GatewayMutation.class})
public class Mutation {

    /**
     * Authority Mutation
     * <p>
     * 权限相关修改
     */
    @GraphQLGetter
    public AuthorityMutation getAuthority(@Autowired AuthorityMutation mutation) {
        return mutation;
    }

    /**
     * Organization Mutation
     * <p>
     * 组织架构相关修改
     */
    @GraphQLGetter
    public OrganizationMutation getOrganization(@Autowired OrganizationMutation mutation) {
        return mutation;
    }

    /**
     * System Mutation
     * <p>
     * 系统配置相关修改
     */
    @GraphQLGetter
    public SystemMutation getSystem(@Autowired SystemMutation mutation) {
        return mutation;
    }

    /**
     * Tenant Mutation
     * <p>
     * 租户相关修改
     */
    @GraphQLGetter
    public SaasMutation getSaas(@Autowired SaasMutation mutation) {
        return mutation;
    }

    /**
     * Identity Mutation
     * <p>
     * 认证相关修改
     */
    @GraphQLGetter
    public IdentityMutation getIdentity(@Autowired IdentityMutation mutation) {
        return mutation;
    }

    /**
     * Log Mutation
     * <p>
     * 日志中心查询
     */
    @GraphQLGetter
    public LogMutation getLog(@Autowired LogMutation mutation) {
        return mutation;
    }

    /**
     * Storage Mutation
     * <p>
     * 存储中心修改
     */
    @GraphQLGetter
    public StorageMutation getStorage(@Autowired StorageMutation mutation) {
        return mutation;
    }

    /**
     * Multicast Mutation
     * <p>
     * 广播中心修改
     */
    @GraphQLGetter
    public MulticastMutation getMulticast(@Autowired MulticastMutation mutation) {
        return mutation;
    }

    /**
     * Gateway Mutation
     * <p>
     * 网关中心修改
     */
    @GraphQLGetter
    public GatewayMutation getGateway(@Autowired GatewayMutation mutation) {
        return mutation;
    }
}
