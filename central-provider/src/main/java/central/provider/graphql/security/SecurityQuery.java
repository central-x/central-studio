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

package central.provider.graphql.security;

import central.provider.graphql.security.query.SecurityPasswordQuery;
import central.provider.graphql.security.query.SecurityStrategyQuery;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Authority Query
 * <p>
 * 认证中心查询
 *
 * @author Alan Yeh
 * @since 2022/10/02
 */
@Component
@GraphQLSchema(path = "security", types = {SecurityPasswordQuery.class, SecurityStrategyQuery.class})
public class SecurityQuery {

    /**
     * Password Query
     * <p>
     * 密码查询
     */
    @GraphQLGetter
    public SecurityPasswordQuery getPasswords(@Autowired SecurityPasswordQuery query) {
        return query;
    }

    /**
     * Strategy Query
     * <p>
     * 安全策略查询
     */
    @GraphQLGetter
    public SecurityStrategyQuery getStrategies(@Autowired SecurityStrategyQuery query) {
        return query;
    }
}
