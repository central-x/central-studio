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

package central.security.test;

import central.security.Digestx;
import central.security.SecurityApplication;
import central.security.controller.index.IndexController;
import central.security.controller.index.support.LoginOptions;
import central.security.test.client.IndexClient;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Index Controller Test Cases
 * 首页测试
 *
 * @author Alan Yeh
 * @see IndexController
 * @since 2022/10/23
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = SecurityApplication.class)
public class TestIndexController {

    @Setter(onMethod_ = @Autowired)
    private IndexClient client;

    /**
     * @see IndexController#getOptions
     */
    @Test
    public void case1() {
        var options = client.getOptions();
        for (var option : LoginOptions.values()) {
            assertTrue(options.containsKey(option.getName()));
        }
    }

    /**
     * @see IndexController#login
     * @see IndexController#getAccount
     */
    @Test
    public void case2() {
        // 登录
        var login = this.client.login("syssa", Digestx.SHA256.digest("x.123456", StandardCharsets.UTF_8), "1234", "lLS4p6skBbBVZX30zR5");

        // 获取当前用户信息
        var account = this.client.getAccount();
        assertNotNull(account);
        assertEquals(account.getId(), "syssa");
        assertEquals(account.getUsername(), "syssa");
    }

    @Test
    public void case3(){

    }
}
