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

package central.provider.graphql.auth;

import central.provider.graphql.auth.mutation.MenuMutation;
import central.provider.graphql.auth.mutation.RoleMutation;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Authority Mutation
 * <p>
 * 权限类数据修改
 *
 * @author Alan Yeh
 * @since 2022/10/26
 */
@Component
@GraphQLSchema(path = "auth", types = {MenuMutation.class, RoleMutation.class})
public class AuthMutation {
    /**
     * Menu Mutation
     * 菜单修改
     */
    @GraphQLGetter
    public MenuMutation getMenus(@Autowired MenuMutation mutation) {
        return mutation;
    }

    /**
     * Role Mutation
     * 角色修改
     */
    @GraphQLGetter
    public RoleMutation getRoles(@Autowired RoleMutation mutation) {
        return mutation;
    }
}
