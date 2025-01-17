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

package central.studio.dashboard.controller.saas.controller;

import central.bean.Page;
import central.data.saas.Tenant;
import central.starter.web.param.IdParams;
import central.starter.web.param.IdsParams;
import central.starter.web.query.IdQuery;
import central.studio.dashboard.controller.saas.param.TenantParams;
import central.studio.dashboard.controller.saas.query.TenantPageQuery;
import central.studio.dashboard.logic.saas.TenantLogic;
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

/**
 * Tenant Controller
 * <p>
 * 租户管理
 *
 * @author Alan Yeh
 * @since 2024/11/19
 */
@RestController
@RequiresAuthentication
@RequestMapping("/dashboard/api/saas/tenants")
public class TenantController {

    /**
     * 权限
     */
    public interface Permissions {
        String VIEW = "saas:tenant:view";
        String ADD = "saas:tenant:add";
        String EDIT = "saas:tenant:edit";
        String DELETE = "saas:tenant:delete";
        String ENABLE = "saas:tenant:enable";
        String DISABLE = "saas:tenant:disable";
    }

    @Setter(onMethod_ = @Autowired)
    private TenantLogic logic;

    /**
     * 按条件分页查询列表
     *
     * @param query 查询
     * @return 分页结果
     */
    @GetMapping("/page")
    @RequiresPermissions(Permissions.VIEW)
    public Page<Tenant> page(@Validated TenantPageQuery query) {
        return this.logic.pageBy(query.getPageIndex(), query.getPageSize(), query.build(), null);
    }

    /**
     * 根据主键查询详情
     *
     * @param query 查询
     * @return 详情
     */
    @GetMapping("/details")
    @RequiresPermissions(Permissions.VIEW)
    public Tenant details(@Validated IdQuery<Tenant> query) {
        return this.logic.findById(query.getId());
    }

    /**
     * 更新行政数据
     *
     * @param params    数据入参
     * @param accountId 当前登录帐号
     * @return 新增后的数据
     */
    @PostMapping
    @RequiresPermissions(Permissions.ADD)
    public Tenant add(@RequestBody @Validated({Insert.class, Default.class}) TenantParams params, @RequestAttribute String accountId) {
        return this.logic.insert(params.toInput(), accountId);
    }

    /**
     * 更新数据
     *
     * @param params    数据入参
     * @param accountId 当前登录帐号
     * @return 更新后的数据
     */
    @PutMapping
    @RequiresPermissions(Permissions.EDIT)
    public Tenant update(@RequestBody @Validated({Update.class, Default.class}) TenantParams params, @RequestAttribute String accountId) {
        return this.logic.update(params.toInput(), accountId);
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
    public Tenant enable(@RequestBody @Validated IdParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
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
    public Tenant disable(@RequestBody @Validated IdParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.disable(params.getId(), accountId, tenant);
    }

    /**
     * 根据主键删除数据
     *
     * @param params    待删除主键列表
     * @param accountId 当前登录帐号
     * @return 受影响数据行数
     */
    @DeleteMapping
    @RequiresPermissions(Permissions.DELETE)
    public long delete(@Validated IdsParams params, @RequestAttribute String accountId) {
        return this.logic.deleteByIds(params.getIds(), accountId);
    }
}
