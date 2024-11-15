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

package central.studio.dashboard.controller.gateway;

import central.data.gateway.GatewayFilter;
import central.lang.reflect.TypeRef;
import central.provider.scheduled.DataContext;
import central.starter.test.cookie.CookieStore;
import central.studio.dashboard.DashboardApplication;
import central.studio.dashboard.controller.TestController;
import central.studio.dashboard.controller.gateway.controller.FilterController;
import central.studio.dashboard.controller.gateway.params.FilterParams;
import central.studio.dashboard.controller.gateway.params.PredicateParams;
import central.util.Jsonx;
import central.util.Mapx;
import central.web.XForwardedHeaders;
import org.hamcrest.Matchers;
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
 * Gateway Filter Controller Test Cases
 *
 * @author Alan Yeh
 * @since 2024/11/15
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = DashboardApplication.class)
public class TestFilterController extends TestController {


    private static final String PATH = "/dashboard/api/gateway/filters";

    /**
     * @see FilterController#add
     * @see FilterController#details
     * @see FilterController#page
     * @see FilterController#delete
     */
    @Test
    public void case0(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore) throws Exception {
        // 新增
        var addRequest = MockMvcRequestBuilders.post(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(FilterParams.builder()
                        .type("set_request_header")
                        .path("/test")
                        .order(1000)
                        .enabled(Boolean.TRUE)
                        .remark("测试用策略")
                        .params(Jsonx.Default().serialize(
                                Mapx.of(
                                        Mapx.entry("name", "X-Forward-Test"),
                                        Mapx.entry("value", "test")
                                ))
                        )
                        .predicates(List.of())
                        .build()))
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");
        var response = mvc.perform(addRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.type").isNotEmpty())
                .andExpect(jsonPath("$.path").isNotEmpty())
                .andExpect(jsonPath("$.order").isNotEmpty())
                .andExpect(jsonPath("$.enabled").isNotEmpty())
                .andExpect(jsonPath("$.remark").isNotEmpty())
                .andExpect(jsonPath("$.params").isNotEmpty())
                .andExpect(jsonPath("$.predicates").isEmpty())
                .andReturn().getResponse();

        var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(GatewayFilter.class));
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
                .andExpect(jsonPath("$.type").value(body.getType()))
                .andExpect(jsonPath("$.path").value(body.getPath()))
                .andExpect(jsonPath("$.order").value(body.getOrder()))
                .andExpect(jsonPath("$.enabled").value(body.getEnabled()))
                .andExpect(jsonPath("$.remark").value(body.getRemark()))
                .andExpect(jsonPath("$.params").value(body.getParams()))
                .andExpect(jsonPath("$.predicates").isArray())
                .andExpect(jsonPath("$.predicates").isEmpty());

        // 分页查询
        var pageRequest = MockMvcRequestBuilders.get(PATH + "/page")
                .cookie(this.getSessionCookie(PATH + "/page", mvc, cookieStore))
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
                .andExpect(jsonPath("$.data[0].type").value(body.getType()))
                .andExpect(jsonPath("$.data[0].path").value(body.getPath()))
                .andExpect(jsonPath("$.data[0].order").value(body.getOrder()))
                .andExpect(jsonPath("$.data[0].enabled").value(body.getEnabled()))
                .andExpect(jsonPath("$.data[0].remark").value(body.getRemark()))
                .andExpect(jsonPath("$.data[0].params").value(body.getParams()))
                .andExpect(jsonPath("$.data[0].predicates").isArray())
                .andExpect(jsonPath("$.data[0].predicates").isEmpty());

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
     * @see FilterController#add
     * @see FilterController#update
     * @see FilterController#details
     * @see FilterController#delete
     */
    @Test
    public void case1(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore, @Autowired DataContext context) throws Exception {
        // 新增
        var addRequest = MockMvcRequestBuilders.post(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(FilterParams.builder()
                        .type("set_request_header")
                        .path("/test")
                        .order(1000)
                        .enabled(Boolean.TRUE)
                        .remark("测试用策略")
                        .params(Jsonx.Default().serialize(
                                Mapx.of(
                                        Mapx.entry("name", "X-Forward-Test"),
                                        Mapx.entry("value", "test")
                                ))
                        )
                        .predicates(List.of())
                        .build()))
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");
        var response = mvc.perform(addRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.type").isNotEmpty())
                .andExpect(jsonPath("$.path").isNotEmpty())
                .andExpect(jsonPath("$.order").isNotEmpty())
                .andExpect(jsonPath("$.enabled").isNotEmpty())
                .andExpect(jsonPath("$.remark").isNotEmpty())
                .andExpect(jsonPath("$.params").isNotEmpty())
                .andExpect(jsonPath("$.predicates").isEmpty())
                .andReturn().getResponse();

        var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(GatewayFilter.class));
        assertNotNull(body);

        // 更新
        var updateRequest = MockMvcRequestBuilders.put(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(FilterParams.builder()
                        .id(body.getId())
                        .type(body.getType())
                        .path(body.getPath())
                        .order(body.getOrder())
                        .enabled(Boolean.FALSE)
                        .remark(body.getRemark())
                        .params(body.getParams())
                        .predicates(List.of(
                                PredicateParams.builder()
                                        .type("method")
                                        .params(Jsonx.Default().serialize(Mapx.of(
                                                Mapx.entry("methods", List.of("GET", "POST"))
                                        )))
                                        .build()
                        ))
                        .build()))
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");

        mvc.perform(updateRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").value(body.getId()))
                .andExpect(jsonPath("$.type").value(body.getType()))
                .andExpect(jsonPath("$.path").value(body.getPath()))
                .andExpect(jsonPath("$.order").value(body.getOrder()))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.remark").value(body.getRemark()))
                .andExpect(jsonPath("$.predicates").isNotEmpty())
                .andExpect(jsonPath("$.predicates[0].type").value("method"));

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
                .andExpect(jsonPath("$.type").value(body.getType()))
                .andExpect(jsonPath("$.path").value(body.getPath()))
                .andExpect(jsonPath("$.order").value(body.getOrder()))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.remark").value(body.getRemark()))
                .andExpect(jsonPath("$.predicates").isNotEmpty())
                .andExpect(jsonPath("$.predicates[0].type").value("method"));

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
