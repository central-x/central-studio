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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

/**
 * Storage Bucket Controller
 * <p>
 * 存储桶管理
 *
 * @author Alan Yeh
 * @since 2024/10/29
 */
@RestController
@RequiresAuthentication
@RequestMapping("/dashboard/api/storage/buckets")
public class BucketController {

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

    /**
     * 按条件分页查询列表
     *
     * @param query  查询
     * @param tenant 租户标识
     * @return 分页结果
     */
    @GetMapping("/page")
    public Page<StorageBucket> page(@Validated BucketPageQuery query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.pageBy(query.getPageIndex(), query.getPageSize(), query.build(), null, tenant);
    }

    /**
     * 根据主键查询详情
     *
     * @param query  查询
     * @param tenant 租户标识
     * @return 详情
     */
    @GetMapping("/details")
    public StorageBucket details(@Validated IdQuery<StorageBucket> query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.findById(query.getId(), tenant);
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
    public StorageBucket add(@RequestBody @Validated({Insert.class, Default.class}) BucketParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.insert(params.toInput(), accountId, tenant);
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
    public StorageBucket update(@RequestBody @Validated({Update.class, Default.class}) BucketParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.update(params.toInput(), accountId, tenant);
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
        return this.logic.deleteByIds(params.getIds(), accountId, tenant);
    }

    /**
     * 按条件分页查询存储对象列表
     *
     * @param query  查询
     * @param tenant 租户标识
     * @return 列表结果
     */
    @GetMapping("/objects/page")
    public Page<StorageObject> pageObjects(@Validated ObjectPageQuery query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.pageObjects(query.getPageIndex(), query.getPageSize(), query.build(), null, tenant);
    }

    /**
     * 修改存储对象
     * <p>
     * 只能修改对象文件名和确认状态
     *
     * @param params    待修改数据
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 更新后的数据
     */
    public StorageObject updateObject(@Validated @RequestBody ObjectParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var object = this.logic.findObjectById(params.getId(), tenant);
        if (object == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("文件[id={}]不存在", params.getId()));
        }
        if (!Objects.equals(object.getBucketId(), params.getBucketId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("文件[id={}]不存在", params.getId()));
        }

        var input = object.toInput();
        input.setName(params.getName());
        input.setConfirmed(params.getConfirmed());
        return this.logic.updateObject(input, accountId, tenant);
    }

    /**
     * 根据主键删除存储对象数据
     *
     * @param params    待删除主键列表
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 受影响数据行数
     */
    @DeleteMapping("/objects")
    public long deleteObjects(@Validated IdsParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.deleteObjectByIds(params.getIds(), accountId, tenant);
    }
}
