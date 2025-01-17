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

import central.bean.Page;
import central.data.authority.*;
import central.starter.web.param.IdParams;
import central.starter.web.param.IdsParams;
import central.starter.web.query.IdQuery;
import central.studio.dashboard.controller.authority.param.RoleParams;
import central.studio.dashboard.controller.authority.param.RolePermissionParams;
import central.studio.dashboard.controller.authority.param.RolePrincipalParams;
import central.studio.dashboard.controller.authority.param.RoleRangeParams;
import central.studio.dashboard.controller.authority.query.RolePageQuery;
import central.studio.dashboard.logic.authority.MenuLogic;
import central.studio.dashboard.logic.authority.RoleLogic;
import central.validation.group.Insert;
import central.validation.group.Update;
import central.web.XForwardedHeaders;
import jakarta.validation.groups.Default;
import lombok.Setter;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * System Role Controller
 * <p>
 * 系统角色管理
 *
 * @author Alan Yeh
 * @since 2024/12/13
 */
@RestController
@RequiresAuthentication
@RequestMapping("/dashboard/api/authority/roles")
public class RoleController {
    /**
     * 权限
     */
    public interface Permissions {
        String VIEW = "*:authority:system:role:view";
        String ADD = "*:authority:system:role:add";
        String EDIT = "*:authority:system:role:edit";
        String DELETE = "*:authority:system:role:delete";
        String ENABLE = "*:authority:system:role:enable";
        String DISABLE = "*:authority:system:role:disable";

        String GRANT = "*:authority:system:role:grant";
    }

    @Setter(onMethod_ = @Autowired)
    private RoleLogic logic;

    /**
     * 按条件分页查询数据列表
     *
     * @param query  查询
     * @param tenant 租户标识
     * @return 分页结果
     */
    @GetMapping("/page")
    @RequiresPermissions(Permissions.VIEW)
    public Page<Role> page(@Validated RolePageQuery query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.pageBy(query.getPageIndex(), query.getPageSize(), query.build(), null, tenant);
    }

    /**
     * 根据主键查询数据详情
     *
     * @param query  查询
     * @param tenant 租户标识
     * @return 详情
     */
    @GetMapping("/details")
    @RequiresPermissions(Permissions.VIEW)
    public Role details(@Validated IdQuery<Role> query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
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
    @RequiresPermissions(Permissions.ADD)
    public Role add(@RequestBody @Validated({Insert.class, Default.class}) RoleParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
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
    @RequiresPermissions(Permissions.EDIT)
    public Role update(@RequestBody @Validated({Update.class, Default.class}) RoleParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.update(params.toInput(), accountId, tenant);
    }

    /**
     * 启用数据
     *
     * @param params    待启用主键
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 启用后的数据
     */
    @PutMapping("/enable")
    @RequiresPermissions(Permissions.ENABLE)
    public Role enable(@RequestBody @Validated IdParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.enable(params.getId(), accountId, tenant);
    }

    /**
     * 禁用数据
     *
     * @param params    待禁用主键
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 禁用后的数据
     */
    @PutMapping("/disable")
    @RequiresPermissions(Permissions.DISABLE)
    public Role disable(@RequestBody @Validated IdParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.disable(params.getId(), accountId, tenant);
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
    @RequiresPermissions(Permissions.DELETE)
    public long delete(@Validated IdsParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.deleteByIds(params.getIds(), accountId, tenant);
    }

    @Setter(onMethod_ = @Autowired)
    private MenuLogic menuLogic;

    /**
     * 获取已授权的权限
     * <p>
     * 以菜单树的形式，方便前端展示
     *
     * @param query  角色查询
     * @param tenant 租户标识
     * @return 授权菜单树
     */
    @GetMapping("/permissions")
    @RequiresPermissions(Permissions.GRANT)
    public List<Menu> getPermissions(@Validated IdQuery<Role> query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var relations = this.logic.findPermissions(query.getId(), tenant);
        return this.menuLogic.getMenuTreeByPermissionIds(relations.stream().map(RolePermission::getPermissionId).toList(), tenant);
    }

    /**
     * 为角色授权权限
     *
     * @param params 授权参数
     * @param tenant 租户标识
     * @return 已添加的授权
     */
    @PostMapping("/permissions")
    @RequiresPermissions(Permissions.GRANT)
    public List<RolePermission> addPermissions(@Validated @RequestBody RolePermissionParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.insertPermissions(params.toInputs(), accountId, tenant);
    }

    /**
     * 移除角色的权限
     *
     * @param params    待移除的权限主键
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 受影响数据行数
     */
    @DeleteMapping("/permissions")
    @RequiresPermissions(Permissions.GRANT)
    public long deletePermissions(@Validated IdsParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.deletePermissions(params.getIds(), accountId, tenant);
    }

    /**
     * 获取已授权的主体
     *
     * @param query  角色查询
     * @param tenant 租户标识
     * @return 授权主体列表
     */
    @GetMapping("/principals")
    @RequiresPermissions(Permissions.GRANT)
    public List<RolePrincipal> getPrincipals(@Validated IdQuery<Role> query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.findPrincipals(query.getId(), tenant);
    }

    /**
     * 添加授权主体
     *
     * @param params    授权主体参数
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 已添加的授权主体
     */
    @PostMapping("/principals")
    @RequiresPermissions(Permissions.GRANT)
    public List<RolePrincipal> addPrincipals(@Validated @RequestBody RolePrincipalParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.insertPrincipals(params.toInputs(), accountId, tenant);
    }

    /**
     * 删除授权主体
     *
     * @param params    待移除的授权主体主键
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 受影响数据行数
     */
    @DeleteMapping("/principals")
    @RequiresPermissions(Permissions.GRANT)
    public long deletePrincipals(@Validated IdsParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.deletePrincipals(params.getIds(), accountId, tenant);
    }

    /**
     * 获取已授权的范围
     *
     * @param query  角色查询
     * @param tenant 租户标识
     * @return 授权范围列表
     */
    @GetMapping("/ranges")
    @RequiresPermissions(Permissions.GRANT)
    public List<RoleRange> getRanges(@Validated IdQuery<Role> query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.findRanges(query.getId(), tenant);
    }

    /**
     * 添加授权范围
     *
     * @param params    授权范围参数
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 已添加的授权范围
     */
    @PostMapping("/ranges")
    @RequiresPermissions(Permissions.GRANT)
    public List<RoleRange> addRanges(@Validated @RequestBody RoleRangeParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.insertRanges(params.toInputs(), accountId, tenant);
    }

    /**
     * 删除数据范围
     *
     * @param params    待删除的数据范围主键
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 受影响数据行数
     */
    @DeleteMapping("/ranges")
    @RequiresPermissions(Permissions.GRANT)
    public long deleteRanges(@Validated IdsParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.deleteRanges(params.getIds(), accountId, tenant);
    }
}
