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

package central.provider.graphql.authority;

import central.data.authority.RolePermission;
import central.data.authority.RolePermissionInput;
import central.starter.graphql.stub.Provider;
import central.starter.graphql.stub.annotation.BodyPath;
import central.starter.graphql.stub.annotation.GraphQLStub;
import org.springframework.stereotype.Repository;

/**
 * Role Permission Relation
 * <p>
 * 角色与权限关联关系修改
 *
 * @author Alan Yeh
 * @since 2024/12/15
 */
@Repository
@BodyPath("authority.roles.permissions")
@GraphQLStub(path = "authority", client = "providerClient")
public interface RolePermissionProvider extends Provider<RolePermission, RolePermissionInput> {
}
