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

package central.studio.dashboard.controller.storage.controller;

import central.bean.Page;
import central.data.storage.StorageBucket;
import central.data.storage.StorageObject;
import central.lang.Stringx;
import central.starter.web.param.IdParams;
import central.starter.web.param.IdsParams;
import central.starter.web.query.IdQuery;
import central.studio.dashboard.controller.storage.param.BucketParams;
import central.studio.dashboard.controller.storage.param.ObjectParams;
import central.studio.dashboard.controller.storage.query.BucketPageQuery;
import central.studio.dashboard.controller.storage.query.ObjectPageQuery;
import central.studio.dashboard.logic.storage.StorageLogic;
import central.validation.group.Insert;
import central.validation.group.Update;
import central.web.XForwardedHeaders;
import jakarta.validation.groups.Default;
import lombok.Setter;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

/// Storage Bucket Controller
///
/// 存储桶管理
///
/// @author Alan Yeh
@RestController
@RequiresAuthentication
@RequestMapping("/dashboard/api/storage/buckets")
public class BucketController {

    /// 权限
    public interface Permissions {
        String VIEW = "storage:bucket:view";
        String ADD = "storage:bucket:add";
        String EDIT = "storage:bucket:edit";
        String DELETE = "storage:bucket:delete";
        String ENABLE = "storage:bucket:enable";
        String DISABLE = "storage:bucket:disable";
    }

    @Setter(onMethod_ = @Autowired)
    private StorageLogic logic;

    /// 按条件分页查询列表
    ///
    /// @param query  查询
    /// @param tenant 租户标识
    /// @return 分页结果
    @GetMapping("/page")
    @RequiresPermissions(Permissions.VIEW)
    public Page<StorageBucket> page(@Validated BucketPageQuery query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.pageBy(query.getPageIndex(), query.getPageSize(), query.build(), null, tenant);
    }

    /// 根据主键查询详情
    ///
    /// @param query  查询
    /// @param tenant 租户标识
    /// @return 详情
    @GetMapping("/details")
    @RequiresPermissions(Permissions.VIEW)
    public StorageBucket details(@Validated IdQuery<StorageBucket> query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.findById(query.getId(), tenant);
    }

    /// 更新行政数据
    ///
    /// @param params    数据入参
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 新增后的数据
    @PostMapping
    @RequiresPermissions(Permissions.ADD)
    public StorageBucket add(@RequestBody @Validated({Insert.class, Default.class}) BucketParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.insert(params.toInput(), accountId, tenant);
    }

    /// 更新数据
    ///
    /// @param params    数据入参
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 更新后的数据
    @PutMapping
    @RequiresPermissions(Permissions.EDIT)
    public StorageBucket update(@RequestBody @Validated({Update.class, Default.class}) BucketParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
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
    public StorageBucket enable(@RequestBody @Validated IdParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
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
    public StorageBucket disable(@RequestBody @Validated IdParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.disable(params.getId(), accountId, tenant);
    }

    /// 根据主键删除数据
    ///
    /// @param params    待删除主键列表
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 受影响数据行数
    @DeleteMapping
    @RequiresPermissions(Permissions.DELETE)
    public long delete(@Validated IdsParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.deleteByIds(params.getIds(), accountId, tenant);
    }

    /// 按条件分页查询存储对象列表
    ///
    /// @param query  查询
    /// @param tenant 租户标识
    /// @return 列表结果
    @GetMapping("/objects/page")
    public Page<StorageObject> pageObjects(@Validated ObjectPageQuery query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.pageObjects(query.getPageIndex(), query.getPageSize(), query.build(), null, tenant);
    }

    /// 修改存储对象
    ///
    /// 只能修改对象文件名和确认状态
    ///
    /// @param params    待修改数据
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 更新后的数据
    @PutMapping("/objects")
    public StorageObject updateObject(@Validated @RequestBody ObjectParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var object = this.logic.findObjectById(params.getId(), tenant);
        if (object == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("文件[id={}]不存在", params.getId()));
        }
        if (!Objects.equals(object.getBucketId(), params.getBucketId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("文件[id={}]不存在", params.getId()));
        }

        var input = object.toInput()
                .name(params.getName())
                .confirmed(params.getConfirmed())
                .build();
        return this.logic.updateObject(input, accountId, tenant);
    }

    /// 根据主键删除存储对象数据
    ///
    /// @param params    待删除主键列表
    /// @param accountId 当前登录帐号
    /// @param tenant    租户标识
    /// @return 受影响数据行数
    @DeleteMapping("/objects")
    public long deleteObjects(@Validated IdsParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.deleteObjectByIds(params.getIds(), accountId, tenant);
    }
}
