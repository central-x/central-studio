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

package central.studio.provider.graphql.authority.query;

import central.starter.graphql.annotation.GraphQLFetcher;
import central.starter.graphql.annotation.GraphQLSchema;
import central.studio.provider.graphql.authority.dto.MenuDTO;
import central.studio.provider.graphql.authority.dto.PermissionDTO;
import central.studio.provider.graphql.authority.dto.RoleDTO;
import central.studio.provider.graphql.authority.service.AuthorizationService;
import central.studio.provider.graphql.saas.dto.ApplicationDTO;
import central.web.XForwardedHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Authorization Query
 * <p>
 * 授权查询
 *
 * @author Alan Yeh
 * @since 2024/05/27
 */
@Component
@GraphQLSchema(path = "authority/query")
public class AuthorizationQuery {
    @Autowired
    private AuthorizationService service;

    /**
     * 根据应用标识和应用密钥获取应用信息
     *
     * @param code   应用标识
     * @param secret 应用密钥
     * @return 应用信息
     */
    @GraphQLFetcher
    public ApplicationDTO findApplication(@RequestParam String code, @RequestParam String secret) {
        return service.findApplication(code, secret);
    }

    /**
     * 获取指定帐户允许访问的应用列表
     *
     * @param accountId 帐户主键
     * @param type      应用菜单类型
     * @param tenant    租户标识
     * @return 应用列表
     */
    public List<ApplicationDTO> findApplications(@RequestParam String accountId, @RequestParam String type,
                                                 @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * 获取指定帐户在指定应用下被授权的角色清单
     *
     * @param accountId     帐户主键
     * @param permission    菜单权限。如果此值不为空，则返回包含该权限的角色；如果此值为空，则返回该帐户所有角色
     * @param applicationId 应用主键
     * @param tenant        租户标识
     * @return 角色清单
     */
    public List<RoleDTO> findRoles(@RequestParam String accountId, @RequestParam(required = false) String permission, @RequestParam String applicationId,
                                   @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * 获取指定帐户在指定应用下被授权的菜单清单
     *
     * @param accountId     帐户主键
     * @param type          应用菜单类型
     * @param applicationId 应用主键
     * @param tenant        租户标识
     * @return 菜单清单
     */
    public List<MenuDTO> findMenus(@RequestParam String accountId, @RequestParam String type, @RequestParam String applicationId,
                                   @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * 获取指定帐户在指定应用下被授权的权限列表
     *
     * @param accountId     帐户主键
     * @param applicationId 应用主键
     * @param tenant        租户标识
     * @return 权限清单
     */
    public List<PermissionDTO> findPermissions(@RequestParam String accountId, @RequestParam String applicationId,
                                               @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
