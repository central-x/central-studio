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

import central.starter.test.cookie.CookieStore;
import central.studio.dashboard.DashboardApplication;
import central.studio.dashboard.controller.TestController;
import central.web.XForwardedHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Index Controller Test Cases
 *
 * @author Alan Yeh
 * @since 2024/03/09
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = DashboardApplication.class)
public class TestIndexController extends TestController {

    /**
     * 测试会话
     * <p>
     * 获取用户
     *
     * @see IndexController#getAccount
     */
    @Test
    public void case1(@Autowired MockMvc mvc) throws Exception {
        var request = MockMvcRequestBuilders.get("/dashboard/api/account")
                .header(XForwardedHeaders.TENANT, "master")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // {"message":"未登录","timestamp":1732731511265}
                .andExpect(jsonPath("$.message").value("未登录"))
                .andReturn().getResponse();
    }

    /**
     * 测试会话
     * <p>
     * 未登录重定向到登录界面
     */
    @Test
    public void case2(@Autowired MockMvc mvc) throws Exception {
        var request = MockMvcRequestBuilders.get("/dashboard/")
                .with(req -> {
                    req.setScheme("https");
                    req.setServerName("test.central-x.com");
                    req.setServerPort(9443);
                    return req;
                })
                .header(XForwardedHeaders.TENANT, "master")
                .header(XForwardedHeaders.SCHEMA, "https")
                .header(XForwardedHeaders.HOST, "test.central-x.com")
                .header(XForwardedHeaders.PORT, "9443")
                .header(XForwardedHeaders.ORIGIN_URI, "https://test.central-x.com:9443/dashboard/");

        mvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.LOCATION, "/identity/?redirect_uri=https%3A%2F%2Ftest.central-x.com%3A9443%2Fdashboard%2F"));
    }

    /**
     * 登录之后
     */
    @Test
    public void case3(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore) throws Exception {
        // 验证会话
        var request = MockMvcRequestBuilders.get("/dashboard/api/account")
                .header(XForwardedHeaders.TENANT, "master")
                .cookie(this.getSessionCookie("/dashboard/api/account", mvc, cookieStore))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("syssa"))
                .andExpect(jsonPath("$.name").value("超级管理员"));
    }
}
