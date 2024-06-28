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

package central.studio.dashboard.controller;

import central.lang.Arrayx;
import central.security.Digestx;
import central.studio.dashboard.ApplicationCookieStore;
import central.util.Jsonx;
import central.util.Mapx;
import central.web.XForwardedHeaders;
import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller 测试基类
 *
 * @author Alan Yeh
 * @since 2024/06/29
 */
public class TestController {

    public Cookie[] getSessionCookie(URI targetUri, MockMvc mvc, ApplicationCookieStore cookieStore) throws Exception {
        var cookies = cookieStore.getCookies(targetUri);
        if (Arrayx.isNotEmpty(cookies)) {
            return cookies;
        }

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

        cookieStore.put(URI.create("/identity/api/login"), response);
        return cookieStore.getCookies(targetUri);
    }
}
