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
import central.data.system.Dictionary;
import central.lang.reflect.TypeRef;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.starter.test.cookie.CookieStore;
import central.studio.dashboard.DashboardApplication;
import central.studio.dashboard.controller.TestController;
import central.studio.dashboard.controller.system.controller.DictionaryController;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Dictionary Controller Test Cases
 *
 * @author Alan Yeh
 * @since 2024/09/30
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = DashboardApplication.class)
public class TestDictionaryController extends TestController {

    private static final String PATH = "/dashboard/api/system/dictionaries";

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
     * @see DictionaryController#add
     * @see DictionaryController#page
     * @see DictionaryController#details
     * @see DictionaryController#delete
     */
    @Test
    public void case0(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore, @Autowired DataContext context) throws Exception {
        var application = this.getApplication(context);

        // 新增
        var addRequest = MockMvcRequestBuilders.post(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(Mapx.of(
                        Mapx.entry("applicationId", application.getId()),
                        Mapx.entry("code", "sex"),
                        Mapx.entry("name", "性别"),
                        Mapx.entry("enabled", "true"),
                        Mapx.entry("remark", "性别"),
                        Mapx.entry("items", List.of(
                                Mapx.of(
                                        Mapx.entry("code", "unknown"),
                                        Mapx.entry("name", "未知"),
                                        Mapx.entry("primary", true),
                                        Mapx.entry("order", -1)
                                ), Mapx.of(
                                        Mapx.entry("code", "male"),
                                        Mapx.entry("name", "男性"),
                                        Mapx.entry("primary", false),
                                        Mapx.entry("order", 0)
                                ), Mapx.of(
                                        Mapx.entry("code", "female"),
                                        Mapx.entry("name", "女性"),
                                        Mapx.entry("primary", false),
                                        Mapx.entry("order", 1)
                                )
                        ))
                )))
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
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.items[0].code").value("unknown"))
                .andExpect(jsonPath("$.items[0].name").value("未知"))
                .andExpect(jsonPath("$.items[1].code").value("male"))
                .andExpect(jsonPath("$.items[1].name").value("男性"))
                .andExpect(jsonPath("$.items[2].code").value("female"))
                .andExpect(jsonPath("$.items[2].name").value("女性"))
                .andReturn().getResponse();

        var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(Dictionary.class));
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
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.items[0].code").value("unknown"))
                .andExpect(jsonPath("$.items[0].name").value("未知"))
                .andExpect(jsonPath("$.items[1].code").value("male"))
                .andExpect(jsonPath("$.items[1].name").value("男性"))
                .andExpect(jsonPath("$.items[2].code").value("female"))
                .andExpect(jsonPath("$.items[2].name").value("女性"));

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
                .andExpect(jsonPath("$.data[0].items").isArray())
                .andExpect(jsonPath("$.data[0].items").isNotEmpty())
                .andExpect(jsonPath("$.data[0].items[0].code").value("unknown"))
                .andExpect(jsonPath("$.data[0].items[0].name").value("未知"))
                .andExpect(jsonPath("$.data[0].items[1].code").value("male"))
                .andExpect(jsonPath("$.data[0].items[1].name").value("男性"))
                .andExpect(jsonPath("$.data[0].items[2].code").value("female"))
                .andExpect(jsonPath("$.data[0].items[2].name").value("女性"));

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
     * @see DictionaryController#add
     * @see DictionaryController#update
     * @see DictionaryController#details
     * @see DictionaryController#delete
     */
    @Test
    public void case1(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore, @Autowired DataContext context) throws Exception {
        var application = this.getApplication(context);

        // 新增
        var addRequest = MockMvcRequestBuilders.post(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(Mapx.of(
                        Mapx.entry("applicationId", application.getId()),
                        Mapx.entry("code", "sex"),
                        Mapx.entry("name", "性别"),
                        Mapx.entry("enabled", "true"),
                        Mapx.entry("remark", "性别"),
                        Mapx.entry("items", List.of(
                                Mapx.of(
                                        Mapx.entry("code", "unknown"),
                                        Mapx.entry("name", "未知"),
                                        Mapx.entry("primary", true),
                                        Mapx.entry("order", -1)
                                ), Mapx.of(
                                        Mapx.entry("code", "male"),
                                        Mapx.entry("name", "男性"),
                                        Mapx.entry("primary", false),
                                        Mapx.entry("order", 0)
                                ), Mapx.of(
                                        Mapx.entry("code", "female"),
                                        Mapx.entry("name", "女性"),
                                        Mapx.entry("primary", false),
                                        Mapx.entry("order", 1)
                                )
                        ))
                )))
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
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.items[0].code").value("unknown"))
                .andExpect(jsonPath("$.items[0].name").value("未知"))
                .andExpect(jsonPath("$.items[1].code").value("male"))
                .andExpect(jsonPath("$.items[1].name").value("男性"))
                .andExpect(jsonPath("$.items[2].code").value("female"))
                .andExpect(jsonPath("$.items[2].name").value("女性"))
                .andReturn().getResponse();

        var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(Dictionary.class));
        assertNotNull(body);

        // 更新
        var updateRequest = MockMvcRequestBuilders.put(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(Mapx.of(
                        Mapx.entry("id", body.getId()),
                        Mapx.entry("applicationId", application.getId()),
                        Mapx.entry("code", body.getCode()),
                        Mapx.entry("name", body.getName()),
                        Mapx.entry("enabled", "false"),
                        Mapx.entry("remark", body.getRemark()),
                        Mapx.entry("items", List.of(
                                Mapx.of(
                                        Mapx.entry("code", "male"),
                                        Mapx.entry("name", "男性"),
                                        Mapx.entry("primary", false),
                                        Mapx.entry("order", 0)),
                                Mapx.of(
                                        Mapx.entry("code", "female"),
                                        Mapx.entry("name", "女性"),
                                        Mapx.entry("primary", false),
                                        Mapx.entry("order", 1)
                                )))
                )))
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
                .andExpect(jsonPath("$.enabled").value("false"))
                .andExpect(jsonPath("$.remark").value(body.getRemark()))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.items[0].code").value("male"))
                .andExpect(jsonPath("$.items[0].name").value("男性"))
                .andExpect(jsonPath("$.items[1].code").value("female"))
                .andExpect(jsonPath("$.items[1].name").value("女性"));

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
                .andExpect(jsonPath("$.enabled").value("false"))
                .andExpect(jsonPath("$.remark").value(body.getRemark()))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.items[0].code").value("male"))
                .andExpect(jsonPath("$.items[0].name").value("男性"))
                .andExpect(jsonPath("$.items[1].code").value("female"))
                .andExpect(jsonPath("$.items[1].name").value("女性"));

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
