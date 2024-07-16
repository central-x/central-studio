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

package central.studio.identity.controller.sso.cas;

import central.data.identity.IdentityStrategyInput;
import central.lang.Stringx;
import central.provider.graphql.identity.IdentityStrategyProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.identity.IdentityContainer;
import central.starter.test.cookie.CookieStore;
import central.studio.identity.IdentityApplication;
import central.studio.identity.controller.TestController;
import central.studio.identity.controller.index.IdentityIndexController;
import central.studio.identity.core.strategy.StrategyType;
import central.util.Jsonx;
import central.util.Listx;
import central.util.Mapx;
import central.web.XForwardedHeaders;
import jakarta.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Cas Controller Test Cases
 * <p>
 * CAS 协议测试
 *
 * @author Alan Yeh
 * @since 2024/07/12
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = IdentityApplication.class)
public class TestCasController extends TestController {

    /**
     * 新增 CAS 策略
     */
    @BeforeAll
    public static void beforeAll(@Autowired DataContext context, @Autowired IdentityStrategyProvider provider) throws InterruptedException {
        var strategy = IdentityStrategyInput.builder()
                .code("cas")
                .name("启用 CAS 协议")
                .type(StrategyType.CAS.getValue())
                .enabled(Boolean.TRUE)
                .remark("测试 CAS 协议")
                .params(Jsonx.Default().serialize(Mapx.of(
                        Mapx.entry("enabled", "1"),
                        Mapx.entry("scopes", "user:basic"),
                        Mapx.entry("singleLogout", "1")
                ))).build();

        provider.insert(strategy, "syssa", "master");

        var container = (IdentityContainer) context.getData(DataFetcherType.IDENTITY);
        while (container == null || Listx.isNullOrEmpty(container.getStrategies("master"))) {
            Thread.sleep(100);
            container = context.getData(DataFetcherType.IDENTITY);
        }
    }


    /**
     * 未登录时，重定向到 /identity/ 进行认证
     *
     * @see CasController#login
     * @see IdentityIndexController#login
     */
    @Test
    public void case0(@Autowired MockMvc mvc) throws Exception {
        var request = MockMvcRequestBuilders.get("/identity/sso/cas/login")
                .queryParam("service", "http://central-identity/identity/sso/cas/login-result")
                .header(XForwardedHeaders.TENANT, "master")
                .with(req -> {
                    req.setScheme("https");
                    req.setServerName("identity.central-x.com");
                    req.setServerPort(443);
                    return req;
                });

        mvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.LOCATION,
                                UriComponentsBuilder.fromUriString("/identity/")
                                        .scheme("https")
                                        .host("identity.central-x.com")
                                        .queryParam("redirect_uri",
                                                Stringx.encodeUrl(UriComponentsBuilder.fromUriString("https://identity.central-x.com/identity/sso/cas/login")
                                                        .queryParam("service", Stringx.encodeUrl("http://central-identity/identity/sso/cas/login-result"))
                                                        .build().toString())
                                        )
                                        .build().toString()
                        )
                );
    }

    /**
     * 无效 Cookie 时，重定向到 /identity/ 进行认证
     *
     * @see CasController#login
     * @see IdentityIndexController#login
     */
    @Test
    public void case1(@Autowired MockMvc mvc) throws Exception {
        var request = MockMvcRequestBuilders.get("/identity/sso/cas/login")
                .queryParam("service", "http://central-identity/identity/sso/cas/login-result")
                .cookie(new Cookie("Authentication", "invalid"))
                .header(XForwardedHeaders.TENANT, "master")
                .with(req -> {
                    req.setScheme("https");
                    req.setServerName("identity.central-x.com");
                    req.setServerPort(443);
                    return req;
                });

        mvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.LOCATION,
                                UriComponentsBuilder.fromUriString("/identity/")
                                        .scheme("https")
                                        .host("identity.central-x.com")
                                        .queryParam("redirect_uri",
                                                Stringx.encodeUrl(UriComponentsBuilder.fromUriString("https://identity.central-x.com/identity/sso/cas/login")
                                                        .queryParam("service", Stringx.encodeUrl("http://central-identity/identity/sso/cas/login-result"))
                                                        .build().toString())
                                        )
                                        .build().toString()
                        )
                );
    }

    /**
     * 已登录时，但 renew 参数为 true 时，则要求重定向到 /identity/ 进行认证
     *
     * @see CasController#login
     * @see IdentityIndexController#login
     */
    @Test
    public void case2(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore) throws Exception {
        var request = MockMvcRequestBuilders.get("/identity/sso/cas/login")
                .queryParam("service", "http://central-identity/identity/sso/cas/login-result")
                .queryParam("renew", "true")
                .cookie(this.getSessionCookie("/identity/sso/cas/login", mvc, cookieStore))
                .header(XForwardedHeaders.TENANT, "master")
                .with(req -> {
                    req.setScheme("https");
                    req.setServerName("identity.central-x.com");
                    req.setServerPort(443);
                    return req;
                });

        mvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.LOCATION,
                                UriComponentsBuilder.fromUriString("/identity/")
                                        .scheme("https")
                                        .host("identity.central-x.com")
                                        .queryParam("redirect_uri",
                                                Stringx.encodeUrl(UriComponentsBuilder.fromUriString("https://identity.central-x.com/identity/sso/cas/login")
                                                        .queryParam("service", Stringx.encodeUrl("http://central-identity/identity/sso/cas/login-result"))
                                                        .build().toString())
                                        )
                                        .build().toString()
                        )
                );
    }

    /**
     * 已登录时，协带参数跳转到用户指定的地址
     *
     * @see CasController#login
     * @see CasController#validate
     */
    @Test
    public void case3(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore) throws Exception {
        var request = MockMvcRequestBuilders.get("/identity/sso/cas/login")
                .queryParam("service", "http://central-identity/identity/sso/cas/login-result")
                .cookie(this.getSessionCookie("/identity/sso/cas/login", mvc, cookieStore))
                .header(XForwardedHeaders.TENANT, "master")
                .with(req -> {
                    req.setScheme("https");
                    req.setServerName("identity.central-x.com");
                    req.setServerPort(443);
                    return req;
                });

        var response = mvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.LOCATION, Matchers.startsWith("http://central-identity/identity/sso/cas/login-result?ticket=")))
                .andReturn().getResponse();

        // 获取 ST
        var location = UriComponentsBuilder.fromUriString(Objects.requireNonNull(response.getHeader(HttpHeaders.LOCATION))).build();
        var serviceTicket = location.getQueryParams().getFirst("ticket");

        // 验证 ST
        var validateRequest = MockMvcRequestBuilders.post("/identity/sso/cas/serviceValidate")
                .queryParam("service", "http://central-identity/identity/sso/cas/login-result")
                .queryParam("ticket", serviceTicket)
                .queryParam("format", "json")
//                .content("service=" + Stringx.encodeUrl("http://central-identity/identity/sso/cas/login-result") + "&ticket=" + serviceTicket + "&format=json")
                .header(XForwardedHeaders.TENANT, "master")
                .with(req -> {
                    req.setScheme("https");
                    req.setServerName("identity.central-x.com");
                    req.setServerPort(443);
                    return req;
                });

        mvc.perform(validateRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user").value("syssa"))
                .andExpect(jsonPath("$.attributes.id").value("syssa"))
                .andExpect(jsonPath("$.attributes.name").value("超级管理员"));
    }

    /**
     * 测试无效的 ST
     *
     * @see CasController#validate
     */
    @Test
    public void case4(@Autowired MockMvc mvc) throws Exception {
        // 验证 ST
        var request = MockMvcRequestBuilders.post("/identity/sso/cas/serviceValidate")
                .queryParam("service", "http://central-identity/identity/sso/cas/login-result")
                .queryParam("ticket", "invalid")
                .queryParam("format", "json")
                .header(XForwardedHeaders.TENANT, "master")
                .with(req -> {
                    req.setScheme("https");
                    req.setServerName("identity.central-x.com");
                    req.setServerPort(443);
                    return req;
                });

        mvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
