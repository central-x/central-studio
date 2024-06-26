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

package central.studio.dashboard.controller.index;

import central.data.organization.Account;
import central.lang.Stringx;
import central.studio.dashboard.logic.organization.AccountLogic;
import jakarta.annotation.Nullable;
import lombok.Setter;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.InternalResourceView;

/**
 * Index Controller
 * <p>
 * 首页
 *
 * @author Alan Yeh
 * @since 2023/10/07
 */
@Controller
@RequestMapping("/dashboard")
public class IndexController {

    @Setter(onMethod_ = @Autowired)
    private AccountLogic accountLogic;

    /**
     * 返回首页静态页面
     */
    @GetMapping("/")
    public View index() {
        return new InternalResourceView("index.html");
    }

    /**
     * 获取当前用户信息
     */
    @ResponseBody
    @RequiresAuthentication
    @GetMapping("/api/account")
    public @Nullable Account getAccount(@RequestAttribute(required = false) String accountId) {
        if (Stringx.isNullOrBlank(accountId)) {
            return null;
        }
        return accountLogic.findById(accountId);
    }
}
