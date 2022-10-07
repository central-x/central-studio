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

import central.provider.graphql.sec.SecQuery;
import central.provider.graphql.org.OrgQuery;
import central.provider.graphql.sys.SysQuery;
import central.provider.graphql.ten.TenQuery;
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
@GraphQLSchema(types = {SecQuery.class, OrgQuery.class, SysQuery.class, TenQuery.class})
public class Query {
    /**
     * Security Query
     * 安全相关查询
     */
    @GraphQLGetter
    public SecQuery getSec(@Autowired SecQuery query) {
        return query;
    }

    /**
     * Organization Query
     * 组织架构相关查询
     */
    @GraphQLGetter
    public OrgQuery getOrg(@Autowired OrgQuery query) {
        return query;
    }

    /**
     * System Query
     * 系统配置相关查询
     */
    @GraphQLGetter
    public SysQuery getSys(@Autowired SysQuery query) {
        return query;
    }

    /**
     * Tenant Query
     * 租户相关查询
     */
    @GraphQLGetter
    public TenQuery getTen(@Autowired TenQuery query) {
        return query;
    }
}
