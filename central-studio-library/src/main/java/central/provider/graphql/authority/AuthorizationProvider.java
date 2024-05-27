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

import central.data.authority.Menu;
import central.data.authority.MenuInput;
import central.data.authority.Permission;
import central.data.authority.Role;
import central.data.saas.Application;
import central.starter.graphql.stub.Provider;
import central.starter.graphql.stub.annotation.BodyPath;
import central.starter.graphql.stub.annotation.GraphQLStub;
import central.web.XForwardedHeaders;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 授权查询
 *
 * @author Alan Yeh
 * @since 2024/05/27
 */
@Repository
@BodyPath("authority.authorizations")
@GraphQLStub(path = "authority", client = "providerClient")
public interface AuthorizationProvider extends Provider<Menu, MenuInput> {

    /**
     * 根据应用标识和应用密钥获取应用信息
     *
     * @param code   应用标识
     * @param secret 应用密钥
     * @param tenant 租户标识
     * @return 应用信息
     */
    Application findApplication(@RequestParam String code, @RequestParam String secret, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    Application findApplication(String code, String secret);

    /**
     * 获取指定帐户允许访问的应用列表
     *
     * @param accountId 帐户主键
     * @param type      应用菜单类型
     * @param tenant    租户标识
     * @return 应用列表
     */
    List<Application> findApplications(@RequestParam String accountId, @RequestParam String type, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    List<Application> findApplications(@RequestParam String accountId, @RequestParam String type);

    /**
     * 获取指定帐户在指定应用下被授权的角色清单
     *
     * @param accountId     帐户主键
     * @param permission    菜单权限。如果此值不为空，则返回包含该权限的角色；如果此值为空，则返回该帐户所有角色
     * @param applicationId 应用主键
     * @param tenant        租户标识
     * @return 角色清单
     */
    List<Role> findRole(@RequestParam String accountId, @RequestParam(required = false) String permission, @RequestParam String applicationId, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    List<Role> findRole(@RequestParam String accountId, @RequestParam(required = false) String permission, @RequestParam String applicationId);

    /**
     * 获取指定帐户在指定应用下被授权的菜单清单
     *
     * @param accountId     帐户主键
     * @param type          应用菜单类型
     * @param applicationId 应用主键
     * @param tenant        租户标识
     * @return 菜单清单
     */
    List<Menu> findMenus(@RequestParam String accountId, @RequestParam String type, @RequestParam String applicationId, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    List<Menu> findMenus(@RequestParam String accountId, @RequestParam String type, @RequestParam String applicationId);

    /**
     * 获取指定帐户在指定应用下被授权的权限列表
     *
     * @param accountId     帐户主键
     * @param applicationId 应用主键
     * @param tenant        租户标识
     * @return 权限清单
     */
    List<Permission> findPermissions(@RequestParam String accountId, @RequestParam String applicationId, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    List<Permission> findPermissions(@RequestParam String accountId, @RequestParam String applicationId);
}
