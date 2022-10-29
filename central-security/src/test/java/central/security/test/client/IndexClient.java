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

package central.security.test.client;

import central.data.org.Account;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;

import java.util.Map;

/**
 * 首页客户端
 *
 * @author Alan Yeh
 * @since 2022/10/23
 */
public interface IndexClient {

    /**
     * 获取登录选项
     */
    @GetMapping("/api/options")
    Map<String, Object> getOptions();

    /**
     * 登录
     *
     * @param account  帐号
     * @param password 密码（需将明文密码通过 sha256 摘要）
     * @param captcha  验证码
     * @param secret   终端密钥
     */
    @PostMapping("/api/login")
    String login(@RequestPart String account, @RequestPart String password, @RequestPart String captcha, @RequestPart String secret);

    /**
     * 获取当前登录的用户信息
     */
    @GetMapping("/api/account")
    Account getAccount();

    /**
     * 退出登录
     */
    @GetMapping("/api/logout")
    String logout();
}
