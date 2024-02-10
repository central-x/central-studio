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

package central.identity.controller.index;

import central.data.organization.Account;
import central.studio.identity.IdentityApplication;
import central.studio.identity.controller.index.IdentityIndexController;
import central.studio.identity.controller.index.support.LoginOptions;
import central.studio.identity.core.attribute.EndpointAttributes;
import central.lang.reflect.TypeRef;
import central.security.Digestx;
import central.util.Jsonx;
import central.util.Mapx;
import central.web.XForwardedHeaders;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Index Controller Test Cases
 * <p>
 * 首页测试
 *
 * @author Alan Yeh
 * @see IdentityIndexController
 * @since 2022/10/23
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = IdentityApplication.class)
public class TestIndexController {

    /**
     * 测试是否所有的登录选项都在
     *
     * @see IdentityIndexController#getOptions
     */
    @Test
    public void case1(@Autowired MockMvc mvc) throws Exception {
        var request = MockMvcRequestBuilders.get("/api/options")
                .header(XForwardedHeaders.TENANT, "master");

        var response = mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        var content = response.getContentAsString();
        var options = Jsonx.Default().deserialize(content, TypeRef.ofMap(String.class, TypeRef.ofMap(String.class, Object.class)));

        var targetOptions = new HashMap<String, List<String>>();
        for (var option : LoginOptions.values()) {
            var parts = option.getName().split("[.]");
            targetOptions.computeIfAbsent(parts[0], key -> new ArrayList<>())
                    .add(parts[1]);
        }

        for (var entries : targetOptions.entrySet()) {
            var value = options.get(entries.getKey());
            assertNotNull(value);
            for (var key : entries.getValue()) {
                assertNotNull(value.get(key));
            }
        }
    }

    /**
     * 测试获取验证码
     *
     * @see IdentityIndexController#getCaptcha
     */
    @Test
    public void case2(@Autowired MockMvc mvc) throws Exception {
        var request = MockMvcRequestBuilders.get("/api/captcha")
                .header(XForwardedHeaders.TENANT, "master");

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG));
    }

    /**
     * 测试登录
     *
     * @see IdentityIndexController#login
     * @see IdentityIndexController#getAccount
     * @see IdentityIndexController#logout
     */
    @Test
    public void case3(@Autowired MockMvc mvc) throws Exception {
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // 未登录
        // 未登录时获取用户信息，应该返回 401
        var unauthorizedRequest = MockMvcRequestBuilders.get("/api/account")
                .header(XForwardedHeaders.TENANT, "master");

        mvc.perform(unauthorizedRequest)
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(Jsonx.Default().serialize(Map.of("message", "未登录"))));


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // 登录
        var form = Mapx.of(
                Mapx.entry("account", "syssa"),
                Mapx.entry("password", Digestx.SHA256.digest("x.123456", StandardCharsets.UTF_8)),
                Mapx.entry("captcha", "1234"),
                Mapx.entry("secret", EndpointAttributes.WEB.getValue().getSecret())
        );

        var loginRequest = MockMvcRequestBuilders.post("/api/login")
                .content(Jsonx.Default().serialize(form))
                .contentType(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");

        var loginResponse = mvc.perform(loginRequest)
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(cookie().exists("Authorization"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"))
                .andReturn().getResponse();

        // 会话凭证
        var token = loginResponse.getCookie("Authorization").getValue();

        // 获取当前用户信息，应该能够正常返回用户信息
        var getAccountRequest = MockMvcRequestBuilders.get("/api/account")
                .cookie(new Cookie("Authorization", token))
                .header(XForwardedHeaders.TENANT, "master");

        var getAccountResponse = mvc.perform(getAccountRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        var account = Jsonx.Default().deserialize(getAccountResponse.getContentAsString(), Account.class);
        assertNotNull(account);
        assertEquals(account.getId(), "syssa");
        assertEquals(account.getUsername(), "syssa");

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // 退出登录
        var logoutRequest = MockMvcRequestBuilders.get("/api/logout")
                .cookie(new Cookie("Authorization", token))
                .header(XForwardedHeaders.TENANT, "master");
        mvc.perform(logoutRequest)
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));

        // 退出登录后，原来的会话不可以再次登录
        mvc.perform(getAccountRequest)
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(Jsonx.Default().serialize(Map.of("message", "未登录"))));
    }

}
