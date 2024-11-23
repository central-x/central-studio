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

package central.studio.dashboard.controller.saas;

import central.data.saas.Application;
import central.io.IOStreamx;
import central.lang.reflect.TypeRef;
import central.provider.scheduled.DataContext;
import central.starter.test.cookie.CookieStore;
import central.studio.dashboard.DashboardApplication;
import central.studio.dashboard.controller.TestController;
import central.studio.dashboard.controller.saas.controller.ApplicationController;
import central.studio.dashboard.controller.saas.param.ApplicationParams;
import central.studio.dashboard.controller.saas.param.ApplicationRouteParams;
import central.studio.dashboard.logic.system.DatabaseLogic;
import central.util.Jsonx;
import central.web.XForwardedHeaders;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Application Controller Test Cases
 *
 * @author Alan Yeh
 * @since 2024/11/19
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = DashboardApplication.class)
public class TestApplicationController extends TestController {

    private static final String PATH = "/dashboard/api/saas/applications";

    /**
     * @see ApplicationController#add
     * @see ApplicationController#details
     * @see ApplicationController#page
     * @see ApplicationController#delete
     */
    @Test
    public void case0(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore, @Autowired DatabaseLogic databaseLogic) throws Exception {
        // 新增
        var addRequest = MockMvcRequestBuilders.post(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(ApplicationParams.builder()
                        .code("test")
                        .name("测试应用")
                        .logo(Base64.getEncoder().encodeToString(IOStreamx.readBytes(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/logo-80x80.png"))))
                        .url("http://localhost:8080")
                        .contextPath("/test")
                        .secret("test-secret")
                        .enabled(Boolean.TRUE)
                        .remark("测试应用")
                        .routes(List.of(
                                ApplicationRouteParams.builder().contextPath("/test/abc").url("http://localhost:8081").enabled(Boolean.TRUE).remark("测试路由").build()
                        ))
                        .build()))
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");
        var response = mvc.perform(addRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.logo").isNotEmpty())
                .andExpect(jsonPath("$.contextPath").isNotEmpty())
                .andExpect(jsonPath("$.url").isNotEmpty())
                .andExpect(jsonPath("$.secret").isNotEmpty())
                .andExpect(jsonPath("$.enabled").isNotEmpty())
                .andExpect(jsonPath("$.remark").isNotEmpty())
                .andExpect(jsonPath("$.routes").isNotEmpty())
                .andReturn().getResponse();

        var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(Application.class));
        assertNotNull(body);

        // 详情查询
        var detailsRequest = MockMvcRequestBuilders.get(PATH + "/details")
                .cookie(this.getSessionCookie(PATH + "/details", mvc, cookieStore))
                .queryParam("id", body.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");

        mvc.perform(detailsRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").value(body.getId()))
                .andExpect(jsonPath("$.code").value(body.getCode()))
                .andExpect(jsonPath("$.name").value(body.getName()))
                .andExpect(jsonPath("$.logo").value(body.getLogo()))
                .andExpect(jsonPath("$.contextPath").value(body.getContextPath()))
                .andExpect(jsonPath("$.url").value(body.getUrl()))
                .andExpect(jsonPath("$.secret").value(body.getSecret()))
                .andExpect(jsonPath("$.enabled").value(body.getEnabled()))
                .andExpect(jsonPath("$.remark").value(body.getRemark()))
                .andExpect(jsonPath("$.routes[0].contextPath").value(body.getRoutes().get(0).getContextPath()))
                .andExpect(jsonPath("$.routes[0].url").value(body.getRoutes().get(0).getUrl()))
                .andExpect(jsonPath("$.routes[0].enabled").value(body.getRoutes().get(0).getEnabled()))
                .andExpect(jsonPath("$.routes[0].remark").value(body.getRoutes().get(0).getRemark()));

        // 分页查询
        var pageRequest = MockMvcRequestBuilders.get(PATH + "/page")
                .cookie(this.getSessionCookie(PATH + "/page", mvc, cookieStore))
                .queryParam("pageIndex", "1")
                .queryParam("pageSize", "20")
                .queryParam("code", "test")
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");
        mvc.perform(pageRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pager.pageIndex").value(1))
                .andExpect(jsonPath("$.pager.pageSize").value(20))
                .andExpect(jsonPath("$.pager.pageCount").value(1))
                // master 租户 + 新创建的租户，所以是 2 个租户
                .andExpect(jsonPath("$.pager.itemCount").value(1))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(body.getId()))
                .andExpect(jsonPath("$.data[0].code").value(body.getCode()))
                .andExpect(jsonPath("$.data[0].name").value(body.getName()))
                .andExpect(jsonPath("$.data[0].logo").value(body.getLogo()))
                .andExpect(jsonPath("$.data[0].contextPath").value(body.getContextPath()))
                .andExpect(jsonPath("$.data[0].url").value(body.getUrl()))
                .andExpect(jsonPath("$.data[0].secret").value(body.getSecret()))
                .andExpect(jsonPath("$.data[0].enabled").value(body.getEnabled()))
                .andExpect(jsonPath("$.data[0].remark").value(body.getRemark()))
                .andExpect(jsonPath("$.data[0].routes[0].contextPath").value(body.getRoutes().get(0).getContextPath()))
                .andExpect(jsonPath("$.data[0].routes[0].url").value(body.getRoutes().get(0).getUrl()))
                .andExpect(jsonPath("$.data[0].routes[0].enabled").value(body.getRoutes().get(0).getEnabled()))
                .andExpect(jsonPath("$.data[0].routes[0].remark").value(body.getRoutes().get(0).getRemark()));

        // 删除数据
        var deleteRequest = MockMvcRequestBuilders.delete(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .queryParam("ids", body.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");

        mvc.perform(deleteRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("1"));
    }

    /**
     * @see ApplicationController#add
     * @see ApplicationController#update
     * @see ApplicationController#details
     * @see ApplicationController#delete
     */
    @Test
    public void case1(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore, @Autowired DataContext context) throws Exception {
        // 新增
        var addRequest = MockMvcRequestBuilders.post(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(ApplicationParams.builder()
                        .code("test")
                        .name("测试应用")
                                .logo(Base64.getEncoder().encodeToString(IOStreamx.readBytes(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/logo-80x80.png"))))
                        .url("http://localhost:8080")
                        .contextPath("/test")
                        .secret("test-secret")
                        .enabled(Boolean.TRUE)
                        .remark("测试应用")
                        .routes(List.of(
                                ApplicationRouteParams.builder().contextPath("/test/abc").url("http://localhost:8081").enabled(Boolean.TRUE).remark("测试路由").build()
                        ))
                        .build()))
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");
        var response = mvc.perform(addRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.logo").isNotEmpty())
                .andExpect(jsonPath("$.contextPath").isNotEmpty())
                .andExpect(jsonPath("$.url").isNotEmpty())
                .andExpect(jsonPath("$.secret").isNotEmpty())
                .andExpect(jsonPath("$.enabled").isNotEmpty())
                .andExpect(jsonPath("$.remark").isNotEmpty())
                .andExpect(jsonPath("$.routes").isNotEmpty())
                .andReturn().getResponse();

        var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(Application.class));
        assertNotNull(body);

        // 更新
        var updateRequest = MockMvcRequestBuilders.put(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(ApplicationParams.builder()
                        .id(body.getId())
                        .code("test")
                        .name("测试应用")
                        .logo(Base64.getEncoder().encodeToString(IOStreamx.readBytes(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/logo-80x80.png"))))
                        .url("http://localhost:8080")
                        .contextPath("/test")
                        .secret("test-secret")
                        .enabled(Boolean.FALSE)
                        .remark("测试应用")
                        .routes(List.of(
                                ApplicationRouteParams.builder().contextPath("/test/abc").url("http://localhost:8081").enabled(Boolean.TRUE).remark("测试路由").build()
                        ))
                        .build()))
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");

        mvc.perform(updateRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").value(body.getId()))
                .andExpect(jsonPath("$.code").value(body.getCode()))
                .andExpect(jsonPath("$.name").value(body.getName()))
                .andExpect(jsonPath("$.logo").value(body.getLogo()))
                .andExpect(jsonPath("$.contextPath").value(body.getContextPath()))
                .andExpect(jsonPath("$.url").value(body.getUrl()))
                .andExpect(jsonPath("$.secret").value(body.getSecret()))
                .andExpect(jsonPath("$.enabled").value(Boolean.FALSE))
                .andExpect(jsonPath("$.remark").value(body.getRemark()))
                .andExpect(jsonPath("$.routes[0].contextPath").value(body.getRoutes().get(0).getContextPath()))
                .andExpect(jsonPath("$.routes[0].url").value(body.getRoutes().get(0).getUrl()))
                .andExpect(jsonPath("$.routes[0].enabled").value(body.getRoutes().get(0).getEnabled()))
                .andExpect(jsonPath("$.routes[0].remark").value(body.getRoutes().get(0).getRemark()));

        // 详情查询
        var detailsRequest = MockMvcRequestBuilders.get(PATH + "/details")
                .cookie(this.getSessionCookie(PATH + "/details", mvc, cookieStore))
                .queryParam("id", body.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");

        mvc.perform(detailsRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").value(body.getId()))
                .andExpect(jsonPath("$.code").value(body.getCode()))
                .andExpect(jsonPath("$.name").value(body.getName()))
                .andExpect(jsonPath("$.logo").value(body.getLogo()))
                .andExpect(jsonPath("$.contextPath").value(body.getContextPath()))
                .andExpect(jsonPath("$.url").value(body.getUrl()))
                .andExpect(jsonPath("$.secret").value(body.getSecret()))
                .andExpect(jsonPath("$.enabled").value(Boolean.FALSE))
                .andExpect(jsonPath("$.remark").value(body.getRemark()))
                .andExpect(jsonPath("$.routes[0].contextPath").value(body.getRoutes().get(0).getContextPath()))
                .andExpect(jsonPath("$.routes[0].url").value(body.getRoutes().get(0).getUrl()))
                .andExpect(jsonPath("$.routes[0].enabled").value(body.getRoutes().get(0).getEnabled()))
                .andExpect(jsonPath("$.routes[0].remark").value(body.getRoutes().get(0).getRemark()));

        // 删除数据
        var deleteRequest = MockMvcRequestBuilders.delete(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .queryParam("ids", body.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");

        mvc.perform(deleteRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("1"));
    }
}
