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

package central.studio.provider.graphql.authority;

import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLSchema;
import central.studio.provider.graphql.authority.query.AuthorizationQuery;
import central.studio.provider.graphql.authority.query.MenuQuery;
import central.studio.provider.graphql.authority.query.RoleQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Authority Query
 * <p>
 * 权限类查询
 *
 * @author Alan Yeh
 * @since 2022/10/26
 */
@Component
@GraphQLSchema(path = "authority", types = {MenuQuery.class, RoleQuery.class, AuthorizationQuery.class})
public class AuthorityQuery {

    /**
     * Menu Query
     * <p>
     * 菜单查询
     */
    @GraphQLGetter
    public MenuQuery getMenus(@Autowired MenuQuery query) {
        return query;
    }

    /**
     * Role Query
     * <p>
     * 角色查询
     */
    @GraphQLGetter
    public RoleQuery getRoles(@Autowired RoleQuery query) {
        return query;
    }

    /**
     * Authorization Query
     * <p>
     * 授权查询
     */
    @GraphQLGetter
    public AuthorizationQuery getAuthorizations(@Autowired AuthorizationQuery query) {
        return query;
    }
}
