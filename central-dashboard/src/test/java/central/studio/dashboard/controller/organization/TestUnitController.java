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

import central.data.organization.Area;
import central.data.organization.AreaInput;
import central.data.organization.Department;
import central.data.organization.Unit;
import central.data.organization.option.AreaType;
import central.lang.reflect.TypeRef;
import central.starter.test.cookie.CookieStore;
import central.studio.dashboard.DashboardApplication;
import central.studio.dashboard.controller.TestController;
import central.studio.dashboard.controller.organization.controller.UnitController;
import central.studio.dashboard.controller.organization.param.DepartmentParams;
import central.studio.dashboard.logic.organization.AreaLogic;
import central.util.Jsonx;
import central.util.Mapx;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit Controller Test Cases
 *
 * @author Alan Yeh
 * @since 2024/09/16
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = DashboardApplication.class)
public class TestUnitController extends TestController {

    private static final String PATH = "/dashboard/api/organization/units";

    @Setter(onMethod_ = @Autowired)
    private AreaLogic areaLogic;

    private Area getArea(String code) {
        return areaLogic.insert(AreaInput.builder()
                .parentId(null)
                .code(code)
                .name("中国")
                .type(AreaType.COUNTRY.getValue())
                .order(0)
                .build(), "syssa", "master");
    }

    private void deleteArea(String id) {
        areaLogic.deleteByIds(List.of(id), "syssa", "master");
    }

    /**
     * @see UnitController#add
     * @see UnitController#details
     * @see UnitController#addDepartment
     * @see UnitController#detailsDepartment
     * @see UnitController#listDepartments
     * @see UnitController#deleteDepartment
     * @see UnitController#delete
     */
    @Test
    public void case0(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore) throws Exception {
        // 新增行政区划
        var area = this.getArea("0000000001");

        try {
            // 新增
            var addRequest = MockMvcRequestBuilders.post(PATH)
                    .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Jsonx.Default().serialize(Mapx.of(
                            Mapx.entry("parentId", ""),
                            Mapx.entry("code", "central-x"),
                            Mapx.entry("name", "CentralX"),
                            Mapx.entry("areaId", area.getId()),
                            Mapx.entry("order", 0)
                    )))
                    .header(XForwardedHeaders.TENANT, "master")
                    .accept(MediaType.APPLICATION_JSON);

            var response = mvc.perform(addRequest)
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.parentId").isEmpty())
                    .andExpect(jsonPath("$.code").value("central-x"))
                    .andExpect(jsonPath("$.areaId").value(area.getId()))
                    .andExpect(jsonPath("$.order").value(0))
                    .andReturn().getResponse();

            var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(Unit.class));
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
                    .andExpect(jsonPath("$.parentId").value(body.getParentId()))
                    .andExpect(jsonPath("$.code").value(body.getCode()))
                    .andExpect(jsonPath("$.areaId").value(body.getAreaId()))
                    .andExpect(jsonPath("$.order").value(body.getOrder()));

            // 添加部门
            var addDepartmentRequest = MockMvcRequestBuilders.post(PATH + "/departments")
                    .cookie(this.getSessionCookie(PATH + "/departments", mvc, cookieStore))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Jsonx.Default().serialize(DepartmentParams.builder()
                            .unitId(body.getId())
                            .parentId("")
                            .code("00001")
                            .name("测试部门")
                            .order(0)
                            .build()
                    ))
                    .header(XForwardedHeaders.TENANT, "master")
                    .accept(MediaType.APPLICATION_JSON);

            var addDepartmentResponse = mvc.perform(addDepartmentRequest)
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.unitId").isNotEmpty())
                    .andExpect(jsonPath("$.parentId").isEmpty())
                    .andExpect(jsonPath("$.code").isNotEmpty())
                    .andExpect(jsonPath("$.name").isNotEmpty())
                    .andExpect(jsonPath("$.order").isNotEmpty())
                    .andReturn().getResponse();

            var departmentBody = Jsonx.Default().deserialize(addDepartmentResponse.getContentAsString(), TypeRef.of(Department.class));
            assertNotNull(departmentBody);

            // 部门详情查询
            var departmentDetailsRequest = MockMvcRequestBuilders.get(PATH + "/departments/details")
                    .cookie(this.getSessionCookie(PATH + "/departments/details", mvc, cookieStore))
                    .queryParam("id", departmentBody.getId())
                    .header(XForwardedHeaders.TENANT, "master")
                    .accept(MediaType.APPLICATION_JSON);

            mvc.perform(departmentDetailsRequest)
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(departmentBody.getId()))
                    .andExpect(jsonPath("$.unitId").value(body.getId()))
                    .andExpect(jsonPath("$.parentId").value(departmentBody.getParentId()))
                    .andExpect(jsonPath("$.code").value(departmentBody.getCode()))
                    .andExpect(jsonPath("$.name").value(departmentBody.getName()))
                    .andExpect(jsonPath("$.order").value(departmentBody.getOrder()));

            // 查询部门列表
            var listDepartmentsRequest = MockMvcRequestBuilders.get(PATH + "/departments")
                    .cookie(this.getSessionCookie(PATH + "/departments", mvc, cookieStore))
                    .queryParam("unitId", body.getId())
                    .accept(MediaType.APPLICATION_JSON);

            mvc.perform(listDepartmentsRequest)
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(departmentBody.getId()))
                    .andExpect(jsonPath("$[0].unitId").value(body.getId()))
                    .andExpect(jsonPath("$[0].parentId").value(departmentBody.getParentId()))
                    .andExpect(jsonPath("$[0].code").value(departmentBody.getCode()))
                    .andExpect(jsonPath("$[0].name").value(departmentBody.getName()))
                    .andExpect(jsonPath("$[0].order").value(departmentBody.getOrder()));

            // 更新部门
            var updateDepartmentsRequest = MockMvcRequestBuilders.put(PATH + "/departments")
                    .cookie(this.getSessionCookie(PATH + "/departments", mvc, cookieStore))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Jsonx.Default().serialize(DepartmentParams.builder()
                            .id(departmentBody.getId())
                            .unitId(body.getId())
                            .parentId("")
                            .code("00002")
                            .name("测试部门")
                            .order(0)
                            .build()
                    ))
                    .header(XForwardedHeaders.TENANT, "master")
                    .accept(MediaType.APPLICATION_JSON);
            mvc.perform(updateDepartmentsRequest)
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(departmentBody.getId()))
                    .andExpect(jsonPath("$.unitId").value(body.getId()))
                    .andExpect(jsonPath("$.parentId").value(departmentBody.getParentId()))
                    .andExpect(jsonPath("$.code").value("00002"))
                    .andExpect(jsonPath("$.name").value(departmentBody.getName()))
                    .andExpect(jsonPath("$.order").value(departmentBody.getOrder()));

            // 再次查询详情
            mvc.perform(departmentDetailsRequest)
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(departmentBody.getId()))
                    .andExpect(jsonPath("$.unitId").value(body.getId()))
                    .andExpect(jsonPath("$.parentId").value(departmentBody.getParentId()))
                    .andExpect(jsonPath("$.code").value("00002"))
                    .andExpect(jsonPath("$.name").value(departmentBody.getName()))
                    .andExpect(jsonPath("$.order").value(departmentBody.getOrder()));

            // 删除部门
            mvc.perform(MockMvcRequestBuilders.delete(PATH + "/departments")
                            .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                            .queryParam("ids", departmentBody.getId())
                            .header(XForwardedHeaders.TENANT, "master")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string("1"));


            // 删除数据
            mvc.perform(MockMvcRequestBuilders.delete(PATH)
                            .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                            .queryParam("ids", body.getId())
                            .header(XForwardedHeaders.TENANT, "master")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string("1"));
        } finally {
            this.deleteArea(area.getId());
        }
    }

    /**
     * @see UnitController#add
     * @see UnitController#list
     * @see UnitController#delete
     */
    @Test
    public void case1(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore) throws Exception {
        // 新增行政区划
        var area = this.getArea("0000000002");

        try {

            // 新增
            var addRequest = MockMvcRequestBuilders.post(PATH)
                    .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Jsonx.Default().serialize(Mapx.of(
                            Mapx.entry("parentId", ""),
                            Mapx.entry("code", "central-x"),
                            Mapx.entry("name", "CentralX"),
                            Mapx.entry("areaId", area.getId()),
                            Mapx.entry("order", 0)
                    )))
                    .header(XForwardedHeaders.TENANT, "master")
                    .accept(MediaType.APPLICATION_JSON);

            var response = mvc.perform(addRequest)
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                    .andReturn().getResponse();

            var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(Unit.class));
            assertNotNull(body);

            // 分页查询
            var pageRequest = MockMvcRequestBuilders.get(PATH)
                    .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                    .queryParam("areaId", area.getId())
                    .header(XForwardedHeaders.TENANT, "master")
                    .accept(MediaType.APPLICATION_JSON);

            mvc.perform(pageRequest)
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value(body.getId()));

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
        } finally {
            this.deleteArea(area.getId());
        }
    }

    /**
     * @see UnitController#add
     * @see UnitController#update
     * @see UnitController#details
     * @see UnitController#delete
     */
    @Test
    public void case2(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore) throws Exception {
        // 新增行政区划
        var area = this.getArea("0000000003");

        try {
            // 新增
            var addRequest = MockMvcRequestBuilders.post(PATH)
                    .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Jsonx.Default().serialize(Mapx.of(
                            Mapx.entry("parentId", ""),
                            Mapx.entry("code", "central-x"),
                            Mapx.entry("name", "CentralX"),
                            Mapx.entry("areaId", area.getId()),
                            Mapx.entry("order", 0)
                    )))
                    .header(XForwardedHeaders.TENANT, "master")
                    .accept(MediaType.APPLICATION_JSON);

            var response = mvc.perform(addRequest)
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                    .andReturn().getResponse();

            var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(Unit.class));
            assertNotNull(body);

            // 更新(更新了 code 属性)
            var updateRequest = MockMvcRequestBuilders.put(PATH)
                    .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Jsonx.Default().serialize(Mapx.of(
                            Mapx.entry("id", body.getId()),
                            Mapx.entry("parentId", ""),
                            Mapx.entry("code", "centralx"),
                            Mapx.entry("name", "CentralX"),
                            Mapx.entry("areaId", area.getId()),
                            Mapx.entry("order", 0)
                    )))
                    .header(XForwardedHeaders.TENANT, "master")
                    .accept(MediaType.APPLICATION_JSON);

            mvc.perform(updateRequest)
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                    .andExpect(jsonPath("$.id").value(body.getId()))
                    .andExpect(jsonPath("$.parentId").value(body.getParentId()))
                    .andExpect(jsonPath("$.code").value("centralx"))
                    .andExpect(jsonPath("$.name").value(body.getName()))
                    .andExpect(jsonPath("$.areaId").value(body.getAreaId()))
                    .andExpect(jsonPath("$.order").value(0));

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
                    .andExpect(jsonPath("$.parentId").value(body.getParentId()))
                    .andExpect(jsonPath("$.code").value("centralx"))
                    .andExpect(jsonPath("$.name").value(body.getName()))
                    .andExpect(jsonPath("$.areaId").value(body.getAreaId()))
                    .andExpect(jsonPath("$.order").value(0));

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
        } finally {
            this.deleteArea(area.getId());
        }
    }

    /**
     * @see UnitController#add
     * @see UnitController#details
     * @see UnitController#addDepartment
     * @see UnitController#detailsDepartment
     * @see UnitController#deleteDepartment
     * @see UnitController#delete
     */
    @Test
    public void case3(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore) throws Exception {
        // 新增行政区划
        var area = this.getArea("0000000001");

        try {
            // 新增
            var addRequest = MockMvcRequestBuilders.post(PATH)
                    .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Jsonx.Default().serialize(Mapx.of(
                            Mapx.entry("parentId", ""),
                            Mapx.entry("code", "central-x"),
                            Mapx.entry("name", "CentralX"),
                            Mapx.entry("areaId", area.getId()),
                            Mapx.entry("order", 0)
                    )))
                    .header(XForwardedHeaders.TENANT, "master")
                    .accept(MediaType.APPLICATION_JSON);

            var response = mvc.perform(addRequest)
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.parentId").isEmpty())
                    .andExpect(jsonPath("$.code").value("central-x"))
                    .andExpect(jsonPath("$.areaId").value(area.getId()))
                    .andExpect(jsonPath("$.order").value(0))
                    .andReturn().getResponse();

            var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(Unit.class));
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
                    .andExpect(jsonPath("$.parentId").value(body.getParentId()))
                    .andExpect(jsonPath("$.code").value(body.getCode()))
                    .andExpect(jsonPath("$.areaId").value(body.getAreaId()))
                    .andExpect(jsonPath("$.order").value(body.getOrder()));

            // 添加部门
            var addDepartmentRequest = MockMvcRequestBuilders.post(PATH + "/departments")
                    .cookie(this.getSessionCookie(PATH + "/departments", mvc, cookieStore))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(Jsonx.Default().serialize(DepartmentParams.builder()
                            .unitId(body.getId())
                            .parentId("")
                            .code("00001")
                            .name("测试部门")
                            .order(0)
                            .build()
                    ))
                    .header(XForwardedHeaders.TENANT, "master")
                    .accept(MediaType.APPLICATION_JSON);

            var addDepartmentResponse = mvc.perform(addDepartmentRequest)
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.unitId").isNotEmpty())
                    .andExpect(jsonPath("$.parentId").isEmpty())
                    .andExpect(jsonPath("$.code").isNotEmpty())
                    .andExpect(jsonPath("$.name").isNotEmpty())
                    .andExpect(jsonPath("$.order").isNotEmpty())
                    .andReturn().getResponse();

            var departmentBody = Jsonx.Default().deserialize(addDepartmentResponse.getContentAsString(), TypeRef.of(Department.class));
            assertNotNull(departmentBody);

            // 部门详情查询
            var departmentDetailsRequest = MockMvcRequestBuilders.get(PATH + "/departments/details")
                    .cookie(this.getSessionCookie(PATH + "/departments/details", mvc, cookieStore))
                    .queryParam("id", departmentBody.getId())
                    .header(XForwardedHeaders.TENANT, "master")
                    .accept(MediaType.APPLICATION_JSON);

            mvc.perform(departmentDetailsRequest)
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(jsonPath("$.id").value(departmentBody.getId()))
                    .andExpect(jsonPath("$.unitId").value(body.getId()))
                    .andExpect(jsonPath("$.parentId").value(departmentBody.getParentId()))
                    .andExpect(jsonPath("$.code").value(departmentBody.getCode()))
                    .andExpect(jsonPath("$.name").value(departmentBody.getName()))
                    .andExpect(jsonPath("$.order").value(departmentBody.getOrder()));

            // 删除部门
            mvc.perform(MockMvcRequestBuilders.delete(PATH + "/departments")
                            .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                            .queryParam("ids", departmentBody.getId())
                            .header(XForwardedHeaders.TENANT, "master")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string("1"));


            // 删除数据
            mvc.perform(MockMvcRequestBuilders.delete(PATH)
                            .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                            .queryParam("ids", body.getId())
                            .header(XForwardedHeaders.TENANT, "master")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string("1"));
        } finally {
            this.deleteArea(area.getId());
        }
    }
}
