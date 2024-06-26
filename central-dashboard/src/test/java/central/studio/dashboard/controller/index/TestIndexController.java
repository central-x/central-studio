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
import central.lang.reflect.TypeRef;
import central.security.Digestx;
import central.studio.dashboard.ApplicationCookieStore;
import central.studio.dashboard.DashboardApplication;
import central.util.Jsonx;
import central.util.Mapx;
import central.web.XForwardedHeaders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Index Controller Test Cases
 *
 * @author Alan Yeh
 * @since 2024/03/09
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = DashboardApplication.class)
public class TestIndexController {

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

        var response = mvc.perform(request)
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        var content = response.getContentAsString();
        // expect json: {"message": "未登录"}
        var body = Jsonx.Default().deserialize(content, TypeRef.ofMap(String.class, Object.class));

        Assertions.assertNotNull(body);
        assertEquals(1, body.size());
        assertEquals("未登录", body.get("message"));
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

        var response = mvc
                .perform(request)
                .andExpect(status().is(HttpStatus.FOUND.value()))
                .andReturn().getResponse();

        var location = response.getHeader(HttpHeaders.LOCATION);
        // expect location: /identity/?redirect_uri=https%3A%2F%2Ftest.central-x.com%3A9443%2Fdashboard%2F
        assertNotNull(location);
        assertEquals("/identity/?redirect_uri=https%3A%2F%2Ftest.central-x.com%3A9443%2Fdashboard%2F", location);
    }

    /**
     * 登录之后
     */
    @Test
    public void case3(@Autowired MockMvc mvc, @Autowired ApplicationCookieStore store) throws Exception {
        // 登录
        {
            var request = MockMvcRequestBuilders.post("/identity/api/login")
                    .content(Jsonx.Default().serialize(Mapx.of(
                            Mapx.entry("account", "syssa"),
                            Mapx.entry("password", Digestx.SHA256.digest("x.123456", StandardCharsets.UTF_8)),
                            Mapx.entry("secret", "lLS4p6skBbBVZX30zR5")
                    )))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(XForwardedHeaders.TENANT, "master")
                    .accept(MediaType.APPLICATION_JSON);

            var response = mvc.perform(request)
                    .andExpect(status().is(HttpStatus.OK.value()))
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            var content = response.getContentAsString();
            assertEquals("true", content);

            store.put(URI.create("/identity/api/login"), response);
        }

        // 验证会话
        {
            var request = MockMvcRequestBuilders.get("/dashboard/api/account")
                    .header(XForwardedHeaders.TENANT, "master")
                    .cookie(store.getCookies(URI.create("/dashboard/api/account")))
                    .accept(MediaType.APPLICATION_JSON);

            var response = mvc.perform(request)
                    .andExpect(status().is(HttpStatus.OK.value()))
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            var content = response.getContentAsString();
            var body = Jsonx.Default().deserialize(content, TypeRef.of(Account.class));

            Assertions.assertNotNull(body);
        }
    }
}
