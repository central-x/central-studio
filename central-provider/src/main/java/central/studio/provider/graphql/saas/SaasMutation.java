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

package central.studio.provider.graphql.saas;

import central.studio.provider.graphql.saas.mutation.ApplicationMutation;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLSchema;
import central.studio.provider.graphql.saas.mutation.TenantMutation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Tenant Mutation
 * <p>
 * 租户中心修改
 *
 * @author Alan Yeh
 * @since 2022/10/03
 */
@Component
@GraphQLSchema(path = "saas", types = {TenantMutation.class, ApplicationMutation.class})
public class SaasMutation {

    /**
     * Tenant Mutation
     * 租户修改
     */
    @GraphQLGetter
    public TenantMutation getTenants(@Autowired TenantMutation mutation) {
        return mutation;
    }

    /**
     * Application Mutation
     * 应用修改
     */
    @GraphQLGetter
    public ApplicationMutation getApplications(@Autowired ApplicationMutation mutation) {
        return mutation;
    }
}
