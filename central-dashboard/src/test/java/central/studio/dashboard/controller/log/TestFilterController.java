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

package central.studio.dashboard.controller.log;

import central.data.log.LogFilter;
import central.lang.reflect.TypeRef;
import central.provider.scheduled.DataContext;
import central.sql.query.Conditions;
import central.starter.test.cookie.CookieStore;
import central.studio.dashboard.DashboardApplication;
import central.studio.dashboard.controller.TestController;
import central.studio.dashboard.controller.log.controller.FilterController;
import central.studio.dashboard.controller.log.param.FilterParams;
import central.studio.dashboard.controller.log.param.PredicateParams;
import central.studio.provider.database.persistence.log.entity.LogCollectorEntity;
import central.studio.provider.database.persistence.log.entity.LogStorageEntity;
import central.studio.provider.database.persistence.log.mapper.LogCollectorMapper;
import central.studio.provider.database.persistence.log.mapper.LogStorageMapper;
import central.util.Jsonx;
import central.web.XForwardedHeaders;
import lombok.Setter;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Log Filter Controller Test Cases
 *
 * @author Alan Yeh
 * @since 2024/11/30
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = DashboardApplication.class)
public class TestFilterController extends TestController {
    private static final String PATH = "/dashboard/api/log/filters";

    @Setter(onMethod_ = @Autowired)
    private LogCollectorMapper collectorMapper;

    @Setter(onMethod_ = @Autowired)
    private LogStorageMapper storageMapper;

    /**
     * @see FilterController#add
     * @see FilterController#details
     * @see FilterController#page
     * @see FilterController#delete
     */
    @Test
    public void case0(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore) throws Exception {
        var collector = this.collectorMapper.findFirstBy(Conditions.of(LogCollectorEntity.class).eq(LogCollectorEntity::getCode, "local"));
        var storage = this.storageMapper.findFirstBy(Conditions.of(LogStorageEntity.class).eq(LogStorageEntity::getCode, "file"));

        // 新增
        var addRequest = MockMvcRequestBuilders.post(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(FilterParams.builder()
                        .code("test")
                        .name("测试过滤器")
                        .enabled(Boolean.TRUE)
                        .remark("测试用日志过滤器")
                        .collectorIds(List.of(collector.getId()))
                        .storageIds(List.of(storage.getId()))
                        .predicates(List.of(
                                PredicateParams.builder().type("tenant").params(Jsonx.Default().serialize(Map.of("tenantCode", "master"))).build()
                        )).build()))
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");
        var response = mvc.perform(addRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.enabled").isNotEmpty())
                .andExpect(jsonPath("$.remark").isNotEmpty())
                .andExpect(jsonPath("$.collectors").isNotEmpty())
                .andExpect(jsonPath("$.storages").isNotEmpty())
                .andExpect(jsonPath("$.predicates").isNotEmpty())
                .andReturn().getResponse();

        var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(LogFilter.class));
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
                .andExpect(jsonPath("$.enabled").value(body.getEnabled()))
                .andExpect(jsonPath("$.remark").value(body.getRemark()))
                .andExpect(jsonPath("$.collectors[0].id").value(body.getCollectors().get(0).getId()))
                .andExpect(jsonPath("$.storages[0].id").value(body.getStorages().get(0).getId()))
                .andExpect(jsonPath("$.predicates[0].type").value(body.getPredicates().get(0).getType()));

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
                .andExpect(jsonPath("$.pager.itemCount").value(1))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(body.getId()))
                .andExpect(jsonPath("$.data[0].code").value(body.getCode()))
                .andExpect(jsonPath("$.data[0].name").value(body.getName()))
                .andExpect(jsonPath("$.data[0].enabled").value(body.getEnabled()))
                .andExpect(jsonPath("$.data[0].remark").value(body.getRemark()))
                .andExpect(jsonPath("$.data[0].collectors[0].id").value(body.getCollectors().get(0).getId()))
                .andExpect(jsonPath("$.data[0].storages[0].id").value(body.getStorages().get(0).getId()))
                .andExpect(jsonPath("$.data[0].predicates[0].type").value(body.getPredicates().get(0).getType()));

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
        var collector = this.collectorMapper.findFirstBy(Conditions.of(LogCollectorEntity.class).eq(LogCollectorEntity::getCode, "local"));
        var storage = this.storageMapper.findFirstBy(Conditions.of(LogStorageEntity.class).eq(LogStorageEntity::getCode, "file"));

        // 新增
        var addRequest = MockMvcRequestBuilders.post(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(FilterParams.builder()
                        .code("test")
                        .name("测试过滤器")
                        .enabled(Boolean.TRUE)
                        .remark("测试用日志过滤器")
                        .collectorIds(List.of(collector.getId()))
                        .storageIds(List.of(storage.getId()))
                        .predicates(List.of(
                                PredicateParams.builder().type("tenant").params(Jsonx.Default().serialize(Map.of("tenantCode", "master"))).build()
                        )).build()))
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");
        var response = mvc.perform(addRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.enabled").isNotEmpty())
                .andExpect(jsonPath("$.remark").isNotEmpty())
                .andExpect(jsonPath("$.collectors").isNotEmpty())
                .andExpect(jsonPath("$.storages").isNotEmpty())
                .andExpect(jsonPath("$.predicates").isNotEmpty())
                .andReturn().getResponse();

        var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(LogFilter.class));
        assertNotNull(body);

        // 更新
        var updateRequest = MockMvcRequestBuilders.put(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(FilterParams.builder()
                        .id(body.getId())
                        .code(body.getCode())
                        .name(body.getName())
                        .enabled(Boolean.FALSE)
                        .remark(body.getRemark())
                        .collectorIds(List.of(collector.getId()))
                        .storageIds(List.of(storage.getId()))
                        .predicates(List.of(
                                PredicateParams.builder().type("tenant").params(Jsonx.Default().serialize(Map.of("tenantCode", "master"))).build()
                        )).build()))
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");

        mvc.perform(updateRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").value(body.getId()))
                .andExpect(jsonPath("$.code").value(body.getCode()))
                .andExpect(jsonPath("$.name").value(body.getName()))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.remark").value(body.getRemark()))
                .andExpect(jsonPath("$.collectors[0].id").value(body.getCollectors().get(0).getId()))
                .andExpect(jsonPath("$.storages[0].id").value(body.getStorages().get(0).getId()))
                .andExpect(jsonPath("$.predicates[0].type").value(body.getPredicates().get(0).getType()));

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
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.remark").value(body.getRemark()))
                .andExpect(jsonPath("$.collectors[0].id").value(body.getCollectors().get(0).getId()))
                .andExpect(jsonPath("$.storages[0].id").value(body.getStorages().get(0).getId()))
                .andExpect(jsonPath("$.predicates[0].type").value(body.getPredicates().get(0).getType()));

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
