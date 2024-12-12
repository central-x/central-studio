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

package central.studio.dashboard.controller.authority.controller;

import central.data.authority.Menu;
import central.data.authority.Permission;
import central.starter.web.param.IdsParams;
import central.starter.web.query.IdQuery;
import central.studio.dashboard.controller.authority.param.MenuParams;
import central.studio.dashboard.controller.authority.param.PermissionParams;
import central.studio.dashboard.controller.authority.query.MenuListQuery;
import central.studio.dashboard.controller.authority.query.PermissionListQuery;
import central.studio.dashboard.logic.authority.MenuLogic;
import central.validation.group.Insert;
import central.validation.group.Update;
import central.web.XForwardedHeaders;
import jakarta.validation.groups.Default;
import lombok.Setter;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Menu Controller
 * <p>
 * 菜单管理
 *
 * @author Alan Yeh
 * @since 2024/12/10
 */
@RestController
@RequiresAuthentication
@RequestMapping("/dashboard/api/authority/menus")
public class MenuController {
    /**
     * 权限
     */
    public interface Permissions {
        String VIEW = "${application}:authority:menu:view";
        String ADD = "${application}:authority:menu:add";
        String EDIT = "${application}:authority:menu:edit";
        String REMOVE = "${application}:authority:menu:remove";
        String ENABLE = "${application}:authority:menu:enable";
        String DISABLE = "${application}:authority:menu:disable";

        String PERMISSION = "${application}:authority:menu:permission";
    }


    @Setter(onMethod_ = @Autowired)
    private MenuLogic logic;

    /**
     * 按条件查询数据列表
     *
     * @param query  查询
     * @param tenant 租户标识
     * @return 分页结果
     */
    @GetMapping
    public List<Menu> list(@Validated MenuListQuery query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.findBy(query.getLimit(), query.getOffset(), query.build(), null, tenant);
    }

    /**
     * 根据主键查询数据详情
     *
     * @param query  查询
     * @param tenant 租户标识
     * @return 详情
     */
    @GetMapping("/details")
    public Menu details(@Validated IdQuery<Menu> query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.findById(query.getId(), tenant);
    }

    /**
     * 新增数据
     *
     * @param params    数据入参
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 新增后数据
     */
    @PostMapping
    public Menu add(@RequestBody @Validated({Insert.class, Default.class}) MenuParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.insert(params.toInput(), accountId, tenant);
    }

    /**
     * 更新数据
     *
     * @param params    数据入参
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 更新后数据
     */
    @PutMapping
    public Menu update(@RequestBody @Validated({Update.class, Default.class}) MenuParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.update(params.toInput(), accountId, tenant);
    }

    /**
     * 根据主键删除数据
     *
     * @param params    待删除主键
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 受影响数据行数
     */
    @DeleteMapping
    public long delete(@Validated IdsParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.deleteByIds(params.getIds(), accountId, tenant);
    }

    /**
     * 查询权限列表
     *
     * @param query  查询
     * @param tenant 租户标识
     * @return 列表结果
     */
    @GetMapping("/permissions")
    public List<Permission> listPermissions(@Validated PermissionListQuery query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.findPermissionsBy(query.getLimit(), query.getOffset(), query.build(), null, tenant);
    }

    /**
     * 添加权限数据
     *
     * @param params    权限入参
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 新增后数据
     */
    @PostMapping("/permissions")
    public Permission addPermission(@RequestBody @Validated({Insert.class, Default.class}) PermissionParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.insertPermission(params.toInput(), accountId, tenant);
    }


    /**
     * 更新权限数据
     *
     * @param params    权限入参
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 更新后数据
     */
    @PutMapping("/permissions")
    public Permission updatePermission(@RequestBody @Validated({Update.class, Default.class}) PermissionParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.updatePermission(params.toInput(), accountId, tenant);
    }

    /**
     * 根据主键删除权限数据
     *
     * @param params    待删除主键
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 受影响数据行数
     */
    @DeleteMapping("/permissions")
    public long deletePermissions(@Validated IdsParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.deletePermissionsByIds(params.getIds(), accountId, tenant);
    }

}
