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

package central.studio.dashboard.controller.organization;

import central.data.organization.AreaInput;
import central.data.organization.Rank;
import central.data.organization.Unit;
import central.data.organization.UnitInput;
import central.data.organization.option.AreaType;
import central.lang.reflect.TypeRef;
import central.starter.test.cookie.CookieStore;
import central.studio.dashboard.DashboardApplication;
import central.studio.dashboard.controller.TestController;
import central.studio.dashboard.controller.organization.controller.RankController;
import central.studio.dashboard.controller.organization.param.RankParams;
import central.studio.dashboard.logic.organization.AreaLogic;
import central.studio.dashboard.logic.organization.UnitLogic;
import central.util.Jsonx;
import central.web.XForwardedHeaders;
import lombok.Setter;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * Rank Controller Test Cases
 *
 * @author Alan Yeh
 * @see RankController
 * @since 2024/12/06
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = DashboardApplication.class)
public class TestRankController extends TestController {

    private static final String PATH = "/dashboard/api/organization/ranks";

    @BeforeAll
    public static void setup(@Autowired AreaLogic areaLogic, @Autowired UnitLogic unitLogic) {
        var area = areaLogic.insert(AreaInput.builder()
                .parentId(null)
                .code("00001")
                .name("中国")
                .type(AreaType.COUNTRY.getValue())
                .order(0)
                .build(), "syssa", "master");

        unitLogic.insert(UnitInput.builder()
                .areaId(area.getId())
                .parentId("")
                .code("central-x")
                .name("CentralX")
                .order(0)
                .build(), "syssa", "master");
    }

    @AfterAll
    public static void cleanup(@Autowired AreaLogic areaLogic, @Autowired UnitLogic unitLogic) {
        unitLogic.deleteByCodes(List.of("central-x"), "syssa", "master");
        areaLogic.deleteByCodes(List.of("00001"), "syssa", "master");
    }


    @Setter(onMethod_ = @Autowired)
    private UnitLogic unitLogic;

    private Unit getUnit() {
        return unitLogic.findByCode("central-x", "master");
    }

    /**
     * @see RankController#add
     * @see RankController#details
     * @see RankController#list
     * @see RankController#delete
     */
    @Test
    public void case0(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore) throws Exception {
        var unit = this.getUnit();

        // 新增
        var addRequest = MockMvcRequestBuilders.post(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(RankParams.builder()
                        .code("00001")
                        .name("测试职级")
                        .unitId(unit.getId())
                        .order(0)
                        .build()))
                .header(XForwardedHeaders.TENANT, "master")
                .accept(MediaType.APPLICATION_JSON);

        var response = mvc.perform(addRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.unitId").isNotEmpty())
                .andExpect(jsonPath("$.order").isNotEmpty())
                .andReturn().getResponse();

        var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(Rank.class));
        assertNotNull(body);

        // 详情查询
        var detailsRequest = MockMvcRequestBuilders.get(PATH + "/details")
                .cookie(this.getSessionCookie(PATH + "/details", mvc, cookieStore))
                .queryParam("id", body.getId())
                .header(XForwardedHeaders.TENANT, "master")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(detailsRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").value(body.getId()))
                .andExpect(jsonPath("$.code").value(body.getCode()))
                .andExpect(jsonPath("$.name").value(body.getName()))
                .andExpect(jsonPath("$.unitId").value(body.getUnitId()))
                .andExpect(jsonPath("$.order").value(body.getOrder()));

        // 列表查询
        var listRequest = MockMvcRequestBuilders.get(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .queryParam("unitId", unit.getId())
                .header(XForwardedHeaders.TENANT, "master")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(listRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$[0].id").value(body.getId()))
                .andExpect(jsonPath("$[0].code").value(body.getCode()))
                .andExpect(jsonPath("$[0].name").value(body.getName()))
                .andExpect(jsonPath("$[0].unitId").value(body.getUnitId()))
                .andExpect(jsonPath("$[0].order").value(body.getOrder()));

        // 删除数据
        var deleteRequest = MockMvcRequestBuilders.delete(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .queryParam("ids", body.getId())
                .header(XForwardedHeaders.TENANT, "master")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(deleteRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("1"));

    }

    /**
     * @see RankController#add
     * @see RankController#update
     * @see RankController#details
     * @see RankController#delete
     */
    @Test
    public void case1(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore) throws Exception {
        var unit = this.getUnit();

        // 新增
        var addRequest = MockMvcRequestBuilders.post(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(RankParams.builder()
                        .code("00001")
                        .name("测试职级")
                        .unitId(unit.getId())
                        .order(0)
                        .build()))
                .header(XForwardedHeaders.TENANT, "master")
                .accept(MediaType.APPLICATION_JSON);

        var response = mvc.perform(addRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.unitId").isNotEmpty())
                .andExpect(jsonPath("$.order").isNotEmpty())
                .andReturn().getResponse();

        var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(Rank.class));
        assertNotNull(body);

        // 更新(更新了 code 属性)
        var updateRequest = MockMvcRequestBuilders.put(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(RankParams.builder()
                        .id(body.getId())
                        .code("00002")
                        .name(body.getName())
                        .unitId(body.getUnitId())
                        .order(body.getOrder())
                        .build()))
                .header(XForwardedHeaders.TENANT, "master")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(updateRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").value(body.getId()))
                .andExpect(jsonPath("$.code").value("00002"))
                .andExpect(jsonPath("$.name").value(body.getName()))
                .andExpect(jsonPath("$.unitId").value(body.getUnitId()))
                .andExpect(jsonPath("$.order").value(body.getOrder()));

        // 详情查询(更新了 code 属性)
        var detailsRequest = MockMvcRequestBuilders.get(PATH + "/details")
                .cookie(this.getSessionCookie(PATH + "/details", mvc, cookieStore))
                .queryParam("id", body.getId())
                .header(XForwardedHeaders.TENANT, "master")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(detailsRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").value(body.getId()))
                .andExpect(jsonPath("$.code").value("00002"))
                .andExpect(jsonPath("$.name").value(body.getName()))
                .andExpect(jsonPath("$.unitId").value(body.getUnitId()))
                .andExpect(jsonPath("$.order").value(body.getOrder()));

        // 删除数据
        var deleteRequest = MockMvcRequestBuilders.delete(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .queryParam("ids", body.getId())
                .header(XForwardedHeaders.TENANT, "master")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(deleteRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("1"));
    }
}
