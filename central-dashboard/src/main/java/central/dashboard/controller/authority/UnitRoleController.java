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

package central.dashboard.controller.authority;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Unit Role Controller
 * <p>
 * 单位角色管理
 *
 * @author Alan Yeh
 * @since 2023/11/18
 */
@RestController
@RequiresAuthentication
@RequiresPermissions(UnitRoleController.Permissions.VIEW)
@RequestMapping("/api/authority/unit/roles")
public class UnitRoleController {

    /**
     * 权限
     */
    public interface Permissions {
        String VIEW = "${application}:authority:unit:role:view";
        String ADD = "${application}:authority:unit:role:add";
        String EDIT = "${application}:authority:unit:role:edit";
        String REMOVE = "${application}:authority:unit:role:remove";
        String ENABLE = "${application}:authority:unit:role:enable";
        String DISABLE = "${application}:authority:unit:role:disable";
    }
}
