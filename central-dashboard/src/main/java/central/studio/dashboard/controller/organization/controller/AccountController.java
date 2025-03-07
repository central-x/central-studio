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

package central.studio.dashboard.controller.organization.controller;

import central.bean.Page;
import central.data.organization.Account;
import central.starter.web.param.IdParams;
import central.starter.web.param.IdsParams;
import central.starter.web.query.IdQuery;
import central.studio.dashboard.controller.organization.param.AccountParams;
import central.studio.dashboard.controller.organization.query.AccountPageQuery;
import central.studio.dashboard.logic.organization.AccountLogic;
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

/// Account Controller
///
/// 帐户管理
///
/// @author Alan Yeh
@RestController
@RequiresAuthentication
@RequestMapping("/dashboard/api/organization/accounts")
public class AccountController {

    /// 权限
    public interface Permissions {
        String VIEW = "organization:account:view";
        String ADD = "organization:account:add";
        String EDIT = "organization:account:edit";
        String DELETE = "organization:account:delete";
        String ENABLE = "organization:account:enable";
        String DISABLE = "organization:account:disable";
    }

    @Setter(onMethod_ = @Autowired)
    private AccountLogic logic;

    /// 按条件分页查询数据列表
    ///
    /// @param query  查询
    /// @param tenant 租户标识
    /// @return 分页结果
    @GetMapping("/page")
    @RequiresPermissions(Permissions.VIEW)
    public Page<Account> page(@Validated AccountPageQuery query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.pageBy(query.getPageIndex(), query.getPageSize(), query.build(), null, tenant);
    }

    /// 根据主键查询数据详情
    ///
    /// @param query  查询
    /// @param tenant 租户标识
    /// @return 详情
    @GetMapping("/details")
    @RequiresPermissions(Permissions.VIEW)
    public Account details(@Validated IdQuery<Account> query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.findById(query.getId(), tenant);
    }

    /// 新增数据
    ///
    /// @param params    数据入参
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 新增后数据
    @PostMapping
    @RequiresPermissions(Permissions.ADD)
    public Account add(@RequestBody @Validated({Insert.class, Default.class}) AccountParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.insert(params.toInput(), accountId, tenant);
    }

    /// 更新数据
    ///
    /// @param params    数据入参
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 更新后数据
    @PutMapping
    @RequiresPermissions(Permissions.EDIT)
    public Account update(@RequestBody @Validated({Update.class, Default.class}) AccountParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.update(params.toInput(), accountId, tenant);
    }

    /// 启用数据
    ///
    /// @param params    待启用主键
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 启用后的数据
    @PutMapping("/enable")
    @RequiresPermissions(Permissions.ENABLE)
    public Account enable(@RequestBody @Validated IdParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.enable(params.getId(), accountId, tenant);
    }

    /// 禁用数据
    ///
    /// @param params    待禁用主键
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 禁用后的数据
    @PutMapping("/disable")
    @RequiresPermissions(Permissions.DISABLE)
    public Account disable(@RequestBody @Validated IdParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.disable(params.getId(), accountId, tenant);
    }

    /// 根据主键删除数据
    ///
    /// @param params    待删除主键
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 受影响数据行数
    @DeleteMapping
    @RequiresPermissions(Permissions.DELETE)
    public long delete(@Validated IdsParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.deleteByIds(params.getIds(), accountId, tenant);
    }
}
