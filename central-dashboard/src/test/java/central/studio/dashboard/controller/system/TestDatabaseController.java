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

package central.studio.dashboard.controller.system;

import central.data.saas.Application;
import central.data.system.Database;
import central.data.system.option.DatabaseType;
import central.lang.reflect.TypeRef;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.starter.test.cookie.CookieStore;
import central.studio.dashboard.DashboardApplication;
import central.studio.dashboard.controller.TestController;
import central.studio.dashboard.controller.system.controller.DatabaseController;
import central.studio.dashboard.controller.system.param.DatabaseParams;
import central.studio.dashboard.controller.system.param.DatabasePropertiesParams;
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
 * Database Controller Test Cases
 *
 * @author Alan Yeh
 * @since 2024/10/20
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = DashboardApplication.class)
public class TestDatabaseController extends TestController {

    private static final String PATH = "/dashboard/api/system/databases";

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
     * @see DatabaseController#add
     * @see DatabaseController#details
     * @see DatabaseController#page
     * @see DatabaseController#delete
     */
    @Test
    public void case0(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore, @Autowired DataContext context) throws Exception {
        var application = this.getApplication(context);

        // 新增
        var addRequest = MockMvcRequestBuilders.post(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(DatabaseParams.builder()
                        .applicationId(application.getId())
                        .code("test")
                        .name("测试数据库")
                        .type(DatabaseType.H2.getValue())
                        .enabled(Boolean.TRUE)
                        .remark("测试用数据库")
                        .master(DatabasePropertiesParams.builder()
                                .driver("org.h2.Driver")
                                .url("jdbc:h2:mem:master")
                                .username("centralx")
                                .password("x.123456")
                                .build())
                        .slaves(Listx.of(DatabasePropertiesParams.builder()
                                        .driver("org.h2.Driver")
                                        .url("jdbc:h2:mem:slave1")
                                        .username("centralx")
                                        .password("x.123456")
                                        .build(),
                                DatabasePropertiesParams.builder()
                                        .driver("org.h2.Driver")
                                        .url("jdbc:h2:mem:slave2")
                                        .username("centralx")
                                        .password("x.123456")
                                        .build()))
                        .params(Jsonx.Default().serialize(Mapx.of(
                                Mapx.entry("initial-size", 3),
                                Mapx.entry("max-active", 50))))
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
                .andExpect(jsonPath("$.enabled").isNotEmpty())
                .andExpect(jsonPath("$.remark").isNotEmpty())
                .andExpect(jsonPath("$.master").isMap())
                .andExpect(jsonPath("$.master.driver").value("org.h2.Driver"))
                .andExpect(jsonPath("$.master.url").value("jdbc:h2:mem:master"))
                .andExpect(jsonPath("$.slaves").isArray())
                .andExpect(jsonPath("$.slaves[0].driver").value("org.h2.Driver"))
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
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.applicationId").isNotEmpty())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.enabled").isNotEmpty())
                .andExpect(jsonPath("$.remark").isNotEmpty())
                .andExpect(jsonPath("$.master").isMap())
                .andExpect(jsonPath("$.master.driver").value("org.h2.Driver"))
                .andExpect(jsonPath("$.master.url").value("jdbc:h2:mem:master"))
                .andExpect(jsonPath("$.slaves").isArray())
                .andExpect(jsonPath("$.slaves[0].driver").value("org.h2.Driver"));

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
                .andExpect(jsonPath("$.data[0].id").isNotEmpty())
                .andExpect(jsonPath("$.data[0].applicationId").isNotEmpty())
                .andExpect(jsonPath("$.data[0].code").isNotEmpty())
                .andExpect(jsonPath("$.data[0].name").isNotEmpty())
                .andExpect(jsonPath("$.data[0].enabled").isNotEmpty())
                .andExpect(jsonPath("$.data[0].remark").isNotEmpty())
                .andExpect(jsonPath("$.data[0].master").isMap())
                .andExpect(jsonPath("$.data[0].master.driver").value("org.h2.Driver"))
                .andExpect(jsonPath("$.data[0].master.url").value("jdbc:h2:mem:master"))
                .andExpect(jsonPath("$.data[0].slaves").isArray())
                .andExpect(jsonPath("$.data[0].slaves[0].driver").value("org.h2.Driver"));

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
     * @see DatabaseController#add
     * @see DatabaseController#update
     * @see DatabaseController#details
     * @see DatabaseController#delete
     */
    @Test
    public void case1(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore, @Autowired DataContext context) throws Exception {
        var application = this.getApplication(context);

        // 新增
        var addRequest = MockMvcRequestBuilders.post(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(DatabaseParams.builder()
                        .applicationId(application.getId())
                        .code("test")
                        .name("测试数据库")
                        .type(DatabaseType.H2.getValue())
                        .enabled(Boolean.TRUE)
                        .remark("测试用数据库")
                        .master(DatabasePropertiesParams.builder()
                                .driver("org.h2.Driver")
                                .url("jdbc:h2:mem:master")
                                .username("centralx")
                                .password("x.123456")
                                .build())
                        .slaves(Listx.of(DatabasePropertiesParams.builder()
                                        .driver("org.h2.Driver")
                                        .url("jdbc:h2:mem:slave1")
                                        .username("centralx")
                                        .password("x.123456")
                                        .build(),
                                DatabasePropertiesParams.builder()
                                        .driver("org.h2.Driver")
                                        .url("jdbc:h2:mem:slave2")
                                        .username("centralx")
                                        .password("x.123456")
                                        .build()))
                        .params(Jsonx.Default().serialize(Mapx.of(
                                Mapx.entry("initial-size", 3),
                                Mapx.entry("max-active", 50))))
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
                .andExpect(jsonPath("$.enabled").isNotEmpty())
                .andExpect(jsonPath("$.remark").isNotEmpty())
                .andExpect(jsonPath("$.master").isMap())
                .andExpect(jsonPath("$.master.driver").value("org.h2.Driver"))
                .andExpect(jsonPath("$.master.url").value("jdbc:h2:mem:master"))
                .andExpect(jsonPath("$.slaves").isArray())
                .andExpect(jsonPath("$.slaves[0].driver").value("org.h2.Driver"))
                .andReturn().getResponse();

        var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(Database.class));
        assertNotNull(body);

        // 更新
        var updateRequest = MockMvcRequestBuilders.put(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(DatabaseParams.builder()
                        .id(body.getId())
                        .applicationId(body.getApplicationId())
                        .code(body.getCode())
                        .name(body.getName())
                        .type(body.getType())
                        // 修改为禁用
                        .enabled(Boolean.FALSE)
                        .remark(body.getRemark())
                        .master(DatabasePropertiesParams.builder()
                                .driver(body.getMaster().getDriver())
                                .url(body.getMaster().getUrl())
                                .username(body.getMaster().getUsername())
                                .password(body.getMaster().getPassword())
                                .build())
                        .slaves(Listx.of(
                                // 移除了一个 Slave
                                DatabasePropertiesParams.builder()
                                        .driver(body.getSlaves().get(1).getDriver())
                                        .url(body.getSlaves().get(1).getUrl())
                                        .username(body.getSlaves().get(1).getUsername())
                                        .password(body.getSlaves().get(1).getPassword())
                                        .build()
                        ))
                        .params(body.getParams())
                        .build()))
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");

        mvc.perform(updateRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.applicationId").isNotEmpty())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.remark").isNotEmpty())
                .andExpect(jsonPath("$.master").isMap())
                .andExpect(jsonPath("$.master.driver").value("org.h2.Driver"))
                .andExpect(jsonPath("$.master.url").value("jdbc:h2:mem:master"))
                .andExpect(jsonPath("$.slaves").isArray())
                .andExpect(jsonPath("$.slaves[0].driver").value("org.h2.Driver"))
                .andExpect(jsonPath("$.slaves[0].url").value("jdbc:h2:mem:slave2"));

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
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.applicationId").isNotEmpty())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.remark").isNotEmpty())
                .andExpect(jsonPath("$.master").isMap())
                .andExpect(jsonPath("$.master.driver").value("org.h2.Driver"))
                .andExpect(jsonPath("$.master.url").value("jdbc:h2:mem:master"))
                .andExpect(jsonPath("$.slaves").isArray())
                .andExpect(jsonPath("$.slaves[0].driver").value("org.h2.Driver"))
                .andExpect(jsonPath("$.slaves[0].url").value("jdbc:h2:mem:slave2"));

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
