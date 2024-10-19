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

package central.studio.dashboard.controller.system.controller;

import central.bean.Page;
import central.data.system.Dictionary;
import central.starter.web.param.IdsParams;
import central.starter.web.query.IdQuery;
import central.studio.dashboard.controller.system.param.DictionaryParams;
import central.studio.dashboard.controller.system.query.DictionaryPageQuery;
import central.studio.dashboard.logic.system.DictionaryLogic;
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
 * Dictionary Controller
 * <p>
 * 字典管理
 *
 * @author Alan Yeh
 * @since 2024/09/24
 */
@RestController
@RequiresAuthentication
@RequestMapping("/dashboard/api/system/dictionaries")
public class DictionaryController {

    /**
     * 权限
     */
    public interface Permissions {
        String VIEW = "${application}:system:dictionary:view";
        String ADD = "${application}:system:dictionary:add";
        String EDIT = "${application}:system:dictionary:edit";
        String REMOVE = "${application}:system:dictionary:remove";
        String ENABLE = "${application}:system:dictionary:enable";
        String DISABLE = "${application}:system:dictionary:disable";
    }

    @Setter(onMethod_ = @Autowired)
    private DictionaryLogic logic;


    /**
     * 按条件分页查询字典列表
     *
     * @param query  查询
     * @param tenant 租户标识
     * @return 分页结果
     */
    @GetMapping("/page")
    public Page<Dictionary> page(@Validated DictionaryPageQuery query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.pageBy(query.getPageIndex(), query.getPageSize(), query.build(), null, tenant);
    }

    /**
     * 根据主键查询字典详情
     *
     * @param query  查询
     * @param tenant 租户标识
     * @return 详情
     */
    @GetMapping("/details")
    public Dictionary details(@Validated IdQuery<Dictionary> query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.findById(query.getId(), tenant);
    }

    /**
     * 新增字典
     *
     * @param params 字典入参
     * @param tenant 租户标识
     * @return 新增后的字典
     */
    @PostMapping
    public Dictionary add(@RequestBody @Validated({Insert.class, Default.class}) DictionaryParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.insert(params.toInput(), accountId, tenant);
    }

    /**
     * 更新字典
     *
     * @param params    字典入参
     * @param accountId 当前登录帐号
     * @param tenant    租户标识
     * @return 更新后字典数据
     */
    @PutMapping
    public Dictionary update(@RequestBody @Validated({Update.class, Default.class}) DictionaryParams params, @RequestAttribute String accountId, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.logic.update(params.toInput(), accountId, tenant);
    }

    /**
     * 根据主键删除字典数据
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
