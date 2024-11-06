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

package central.studio.dashboard.controller.multicast;

import central.data.saas.Application;
import central.data.system.Database;
import central.lang.reflect.TypeRef;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.starter.test.cookie.CookieStore;
import central.studio.dashboard.DashboardApplication;
import central.studio.dashboard.controller.TestController;
import central.studio.dashboard.controller.multicast.controller.BroadcasterController;
import central.studio.dashboard.controller.storage.param.BucketParams;
import central.util.Jsonx;
import central.util.Listx;
import central.util.Mapx;
import central.web.XForwardedHeaders;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Multicast Broadcaster Controller Test Cases
 *
 * @author Alan Yeh
 * @since 2024/11/07
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = DashboardApplication.class)
public class TestBroadcasterController extends TestController {

    private static final String PATH = "/dashboard/api/multicast/broadcasters";

    @BeforeAll
    public static void setup(@Autowired DataContext context) throws Exception {
        SaasContainer container = null;
        while (container == null || Listx.isNullOrEmpty(container.getApplications())) {
            Thread.sleep(100);
            container = context.getData(DataFetcherType.SAAS);
        }
    }

    /**
     * 获取测试用的应用
     */
    private Application getApplication(DataContext context) {
        SaasContainer container = context.getData(DataFetcherType.SAAS);
        return container.getApplicationByCode("central-dashboard");
    }

    /**
     * @see BroadcasterController#add
     * @see BroadcasterController#details
     * @see BroadcasterController#page
     * @see BroadcasterController#delete
     */
    @Test
    public void case0(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore, @Autowired DataContext context) throws Exception {
        var application = this.getApplication(context);

        // 新增
        var addRequest = MockMvcRequestBuilders.post(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(BucketParams.builder()
                        .applicationId(application.getId())
                        .code("test")
                        .name("测试邮件广播器")
                        .type("email_smtp")
                        .enabled(Boolean.TRUE)
                        .remark("测试用存储桶")
                        .params(Jsonx.Default().serialize(
                                Mapx.of(
                                        Mapx.entry("host", "smtp.exmail.qq.com"),
                                        Mapx.entry("useSsl", "enabled"),
                                        Mapx.entry("port", 465),
                                        Mapx.entry("username", "noreply@central-x.com"),
                                        Mapx.entry("password", "x.123456"),
                                        Mapx.entry("name", "CentralX Support")
                                ))
                        )
                        .build()))
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");
        var response = mvc.perform(addRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.applicationId").isNotEmpty())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.type").isNotEmpty())
                .andExpect(jsonPath("$.enabled").isNotEmpty())
                .andExpect(jsonPath("$.remark").isNotEmpty())
                .andExpect(jsonPath("$.params").isNotEmpty())
                .andReturn().getResponse();

        var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(Database.class));
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
                .andExpect(jsonPath("$.applicationId").value(body.getApplicationId()))
                .andExpect(jsonPath("$.code").value(body.getCode()))
                .andExpect(jsonPath("$.name").value(body.getName()))
                .andExpect(jsonPath("$.enabled").value(body.getEnabled()))
                .andExpect(jsonPath("$.remark").value(body.getRemark()))
                .andExpect(jsonPath("$.params").value(body.getParams()));

        // 分页查询
        var pageRequest = MockMvcRequestBuilders.get(PATH + "/page")
                .cookie(this.getSessionCookie(PATH + "/page", mvc, cookieStore))
                .queryParam("applicationId", application.getId())
                .queryParam("pageIndex", "1")
                .queryParam("pageSize", "20")
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");
        mvc.perform(pageRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pager.pageIndex").value(1))
                .andExpect(jsonPath("$.pager.pageSize").value(20))
                .andExpect(jsonPath("$.pager.pageCount").value(1))
                .andExpect(jsonPath("$.pager.itemCount").value(1))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(body.getId()))
                .andExpect(jsonPath("$.data[0].applicationId").value(body.getApplicationId()))
                .andExpect(jsonPath("$.data[0].code").value(body.getCode()))
                .andExpect(jsonPath("$.data[0].name").value(body.getName()))
                .andExpect(jsonPath("$.data[0].enabled").value(body.getEnabled()))
                .andExpect(jsonPath("$.data[0].remark").value(body.getRemark()))
                .andExpect(jsonPath("$.data[0].params").value(body.getParams()));

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
     * @see BroadcasterController#add
     * @see BroadcasterController#update
     * @see BroadcasterController#details
     * @see BroadcasterController#delete
     */
    @Test
    public void case1(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore, @Autowired DataContext context) throws Exception {
        var application = this.getApplication(context);

        // 新增
        var addRequest = MockMvcRequestBuilders.post(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(BucketParams.builder()
                        .applicationId(application.getId())
                        .code("test")
                        .name("测试邮件广播器")
                        .type("email_smtp")
                        .enabled(Boolean.TRUE)
                        .remark("测试用存储桶")
                        .params(Jsonx.Default().serialize(
                                Mapx.of(
                                        Mapx.entry("host", "smtp.exmail.qq.com"),
                                        Mapx.entry("useSsl", "enabled"),
                                        Mapx.entry("port", 465),
                                        Mapx.entry("username", "noreply@central-x.com"),
                                        Mapx.entry("password", "x.123456"),
                                        Mapx.entry("name", "CentralX Support")
                                ))
                        )
                        .build()))
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");
        var response = mvc.perform(addRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.applicationId").isNotEmpty())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.type").isNotEmpty())
                .andExpect(jsonPath("$.enabled").isNotEmpty())
                .andExpect(jsonPath("$.remark").isNotEmpty())
                .andExpect(jsonPath("$.params").isNotEmpty())
                .andReturn().getResponse();

        var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(Database.class));
        assertNotNull(body);

        // 更新
        var updateRequest = MockMvcRequestBuilders.put(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(BucketParams.builder()
                        .id(body.getId())
                        .applicationId(body.getApplicationId())
                        .code(body.getCode())
                        .name(body.getName())
                        .type(body.getType())
                        .enabled(Boolean.FALSE)
                        .remark(body.getRemark())
                        .params(Jsonx.Default().serialize(Mapx.of(
                                Mapx.entry("host", "smtp.exmail.qq.com"),
                                Mapx.entry("useSsl", "enabled"),
                                Mapx.entry("port", 465),
                                Mapx.entry("username", "noreply@central-x.com"),
                                Mapx.entry("password", "x.123456"),
                                Mapx.entry("name", "CentralX Support"))))
                        .build()))
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");

        mvc.perform(updateRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").value(body.getId()))
                .andExpect(jsonPath("$.applicationId").value(body.getApplicationId()))
                .andExpect(jsonPath("$.code").value(body.getCode()))
                .andExpect(jsonPath("$.name").value(body.getName()))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.remark").value(body.getRemark()));

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
                .andExpect(jsonPath("$.applicationId").value(body.getApplicationId()))
                .andExpect(jsonPath("$.code").value(body.getCode()))
                .andExpect(jsonPath("$.name").value(body.getName()))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.remark").value(body.getRemark()));

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
