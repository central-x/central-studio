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

package central.studio.dashboard.controller.logging.controller;

import central.bean.Page;
import central.data.log.LogCollector;
import central.starter.web.param.IdParams;
import central.starter.web.param.IdsParams;
import central.starter.web.query.IdQuery;
import central.studio.dashboard.controller.logging.param.CollectorParams;
import central.studio.dashboard.controller.logging.query.CollectorPageQuery;
import central.studio.dashboard.logic.log.LogLogic;
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

/// Log Collector Controller
///
/// 日志采集器管理
///
/// @author Alan Yeh
@RestController("logCollectorController")
@RequiresAuthentication
@RequestMapping("/dashboard/api/logging/collectors")
public class CollectorController {

    /// 权限
    public interface Permissions {
        String VIEW = "logging:collector:view";
        String ADD = "logging:collector:add";
        String EDIT = "logging:collector:edit";
        String DELETE = "logging:collector:delete";
        String ENABLE = "logging:collector:enable";
        String DISABLE = "logging:collector:disable";
    }

    @Setter(onMethod_ = @Autowired)
    private LogLogic logic;

    /// 按条件分页查询列表
    ///
    /// @param query  查询
    /// @param tenant 租户标识
    /// @return 分页结果
    @GetMapping("/page")
    public Page<LogCollector> page(@Validated CollectorPageQuery query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.pageCollectorBy(query.getPageIndex(), query.getPageSize(), query.build(), null, tenant);
    }

    /// 根据主键查询详情
    ///
    /// @param query  查询
    /// @param tenant 租户标识
    /// @return 详情
    @GetMapping("/details")
    public LogCollector details(@Validated IdQuery<LogCollector> query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.findCollectorById(query.getId(), tenant);
    }

    /// 更新行政数据
    ///
    /// @param params    数据入参
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 新增后的数据
    @PostMapping
    public LogCollector add(@RequestBody @Validated({Insert.class, Default.class}) CollectorParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.insertCollector(params.toInput(), accountId, tenant);
    }

    /// 更新数据
    ///
    /// @param params    数据入参
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 更新后的数据
    @PutMapping
    public LogCollector update(@RequestBody @Validated({Update.class, Default.class}) CollectorParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.updateCollector(params.toInput(), accountId, tenant);
    }

    /// 启用数据
    ///
    /// @param params    待启用主键
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 启用后的数据
    @PutMapping("/enable")
    @RequiresPermissions(Permissions.ENABLE)
    public LogCollector enable(@RequestBody @Validated IdParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.enableCollector(params.getId(), accountId, tenant);
    }

    /// 禁用数据
    ///
    /// @param params    待禁用主键
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 禁用后的数据
    @PutMapping("/disable")
    @RequiresPermissions(Permissions.DISABLE)
    public LogCollector disable(@RequestBody @Validated IdParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.disableCollector(params.getId(), accountId, tenant);
    }

    /// 根据主键删除数据
    ///
    /// @param params    待删除主键列表
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 受影响数据行数
    @DeleteMapping
    public long delete(@Validated IdsParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.deleteCollectorByIds(params.getIds(), accountId, tenant);
    }
}
