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

package central.studio.dashboard.controller.log.controller;

import central.bean.Page;
import central.data.log.LogStorage;
import central.starter.web.param.IdsParams;
import central.starter.web.query.IdQuery;
import central.studio.dashboard.controller.log.param.StorageParams;
import central.studio.dashboard.controller.log.query.StoragePageQuery;
import central.studio.dashboard.logic.log.LogLogic;
import central.validation.group.Insert;
import central.validation.group.Update;
import central.web.XForwardedHeaders;
import jakarta.validation.groups.Default;
import lombok.Setter;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Log Storage Controller
 * <p>
 * <p>
 * 日志存储器管理
 *
 * @author Alan Yeh
 * @since 2024/11/28
 */
@RestController("logStorageController")
@RequiresAuthentication
@RequestMapping("/dashboard/api/log/storages")
public class StorageController {

    @Setter(onMethod_ = @Autowired)
    private LogLogic logic;

    /**
     * 按条件分页查询列表
     *
     * @param query  查询
     * @param tenant 租户标识
     * @return 分页结果
     */
    @GetMapping("/page")
    public Page<LogStorage> page(@Validated StoragePageQuery query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.pageStorageBy(query.getPageIndex(), query.getPageSize(), query.build(), null, tenant);
    }

    /**
     * 根据主键查询详情
     *
     * @param query  查询
     * @param tenant 租户标识
     * @return 详情
     */
    @GetMapping("/details")
    public LogStorage details(@Validated IdQuery<LogStorage> query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.findStorageById(query.getId(), tenant);
    }

    /**
     * 更新行政数据
     *
     * @param params    数据入参
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 新增后的数据
     */
    @PostMapping
    public LogStorage add(@RequestBody @Validated({Insert.class, Default.class}) StorageParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.insertStorage(params.toInput(), accountId, tenant);
    }

    /**
     * 更新数据
     *
     * @param params    数据入参
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 更新后的数据
     */
    @PutMapping
    public LogStorage update(@RequestBody @Validated({Update.class, Default.class}) StorageParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.updateStorage(params.toInput(), accountId, tenant);
    }

    /**
     * 根据主键删除数据
     *
     * @param params    待删除主键列表
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 受影响数据行数
     */
    @DeleteMapping
    public long delete(@Validated IdsParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.deleteStorageByIds(params.getIds(), accountId, tenant);
    }
}
