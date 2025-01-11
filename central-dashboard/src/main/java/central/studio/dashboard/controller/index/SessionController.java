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

package central.studio.dashboard.controller.index;

import central.bean.Treeable;
import central.data.authority.Menu;
import central.data.authority.Permission;
import central.data.authority.option.MenuType;
import central.data.organization.Account;
import central.data.saas.Application;
import central.provider.graphql.authority.AuthorizationProvider;
import central.studio.dashboard.controller.index.query.ApplicationQuery;
import central.studio.dashboard.logic.organization.AccountLogic;
import central.web.XForwardedHeaders;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Setter;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

/**
 * Session Controller
 * <p>
 * 会话控制器
 *
 * @author Alan Yeh
 * @since 2025/01/10
 */
@RequiresAuthentication
@RequestMapping("/dashboard/api/session")
@RestController("dashboardSessionController")
public class SessionController {

    @Setter(onMethod_ = @Autowired)
    private AccountLogic accountLogic;

    @Setter(onMethod_ = @Autowired)
    private AuthorizationProvider provider;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/account")
    public @Nullable Account getAccount(@RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var account = accountLogic.findById(accountId, tenant);
        account.setCreator(null);
        account.setModifier(null);
        return account;
    }

    /**
     * 获取当前用户可以访问的应用列表
     */
    @GetMapping("/applications")
    public @Nonnull List<Application> getApplications(@RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return provider.findApplications(accountId, MenuType.BACKEND.getValue(), tenant).stream()
                .peek(application -> {
                    application.setCreator(null);
                    application.setModifier(null);
                    application.setSecret(null);
                    application.setUrl(null);
                    application.setRoutes(null);
                }).toList();
    }

    /**
     * 获取当前用户在指定应用下可以访问的菜单列表
     */
    @GetMapping("/applications/menus")
    public @Nonnull List<Menu> getMenus(@Validated ApplicationQuery query, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var menus = provider.findMenus(accountId, MenuType.BACKEND.getValue(), query.getApplicationId(), tenant).stream()
                .peek(menu -> {
                    menu.setParent(null);
                    menu.setApplication(null);
                    menu.setPermissions(null);
                    menu.setCreator(null);
                    menu.setModifier(null);
                }).toList();
        return Treeable.build(menus, Comparator.comparing(Menu::getOrder));
    }

    /**
     * 获取当前用户在指定应用下可以访问的权限列表
     */
    @GetMapping("/applications/permissions")
    public @Nonnull List<String> getPermissions(@Validated ApplicationQuery query, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return provider.findPermissions(accountId, query.getApplicationId(), tenant).stream()
                .map(Permission::getCode).toList();
    }
}
