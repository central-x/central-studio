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

import central.data.organization.Area;
import central.starter.web.param.IdsParams;
import central.starter.web.query.IdQuery;
import central.studio.dashboard.controller.organization.param.AreaParams;
import central.studio.dashboard.controller.organization.query.AreaListQuery;
import central.studio.dashboard.logic.organization.AreaLogic;
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
 * Area Controller
 * <p>
 * 行政区划管理
 *
 * @author Alan Yeh
 * @since 2024/09/14
 */
@RestController
@RequiresAuthentication
@RequestMapping("/dashboard/api/organization/areas")
public class AreaController {

    public interface Permissions {
        String VIEW = "organization:area:view";
        String ADD = "organization:area:add";
        String EDIT = "organization:area:edit";
        String DELETE = "organization:area:delete";
    }

    @Setter(onMethod_ = @Autowired)
    private AreaLogic logic;

    /**
     * 按条件查询行政区划列表
     *
     * @param query  查询
     * @param tenant 租户标识
     * @return 列表结果
     */
    @GetMapping
    public List<Area> list(@Validated AreaListQuery query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.listBy(query.build(), null, tenant);
    }

    /**
     * 根据主键查询行政区划详情
     *
     * @param query  查询
     * @param tenant 租户标识
     * @return 详情
     */
    @GetMapping("/details")
    public Area details(@Validated IdQuery<Area> query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.findById(query.getId(), tenant);
    }

    /**
     * 新增行政区划
     *
     * @param params    行政区划入参
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 新增后行政区划数据
     */
    @PostMapping
    public Area add(@RequestBody @Validated({Insert.class, Default.class}) AreaParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.insert(params.toInput(), accountId, tenant);
    }

    /**
     * 更新行政区划
     *
     * @param params    行政区划数据
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 更新后行政区划数据
     */
    @PutMapping
    public Area update(@RequestBody @Validated({Update.class, Default.class}) AreaParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.update(params.toInput(), accountId, tenant);
    }

    /**
     * 根据主键删除行政区划数据
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
}
