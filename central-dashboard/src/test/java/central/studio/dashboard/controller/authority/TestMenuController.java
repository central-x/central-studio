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

package central.studio.dashboard.controller.authority;

import central.data.authority.Menu;
import central.data.authority.Permission;
import central.data.authority.option.MenuType;
import central.data.saas.Application;
import central.lang.reflect.TypeRef;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.starter.test.cookie.CookieStore;
import central.studio.dashboard.DashboardApplication;
import central.studio.dashboard.controller.TestController;
import central.studio.dashboard.controller.authority.controller.MenuController;
import central.studio.dashboard.controller.authority.param.MenuParams;
import central.studio.dashboard.controller.authority.param.PermissionParams;
import central.util.Jsonx;
import central.util.Listx;
import central.web.XForwardedHeaders;
import lombok.Setter;
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
 * Menu Controller Test Cases
 *
 * @author Alan Yeh
 * @see MenuController
 * @since 2024/12/10
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = DashboardApplication.class)
public class TestMenuController extends TestController {
    private static final String PATH = "/dashboard/api/authority/menus";

    @BeforeAll
    public static void setup(@Autowired DataContext context) throws Exception {
        SaasContainer container = null;
        while (container == null || Listx.isNullOrEmpty(container.getApplications())) {
            Thread.sleep(100);
            container = context.getData(DataFetcherType.SAAS);
        }
    }

    @Setter(onMethod_ = @Autowired)
    private DataContext context;

    /**
     * 获取测试用的应用
     */
    private Application getApplication() {
        SaasContainer container = this.context.getData(DataFetcherType.SAAS);
        return container.getApplicationByCode("central-dashboard");
    }

    /**
     * @see MenuController#add
     * @see MenuController#details
     * @see MenuController#list
     * @see MenuController#delete
     */
    @Test
    public void case0(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore) throws Exception {
        var application = this.getApplication();

        // 新增
        var addRequest = MockMvcRequestBuilders.post(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(MenuParams.builder()
                        .applicationId(application.getId())
                        .parentId("")
                        .code("dashboard-test")
                        .name("测试菜单")
                        .icon("")
                        .url("@/test/index")
                        .type(MenuType.BACKEND.getValue())
                        .enabled(Boolean.TRUE)
                        .order(0)
                        .remark("测试时使用的菜单")
                        .build()))
                .header(XForwardedHeaders.TENANT, "master")
                .accept(MediaType.APPLICATION_JSON);

        var response = mvc.perform(addRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.applicationId").isNotEmpty())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.icon").isEmpty())
                .andExpect(jsonPath("$.url").isNotEmpty())
                .andExpect(jsonPath("$.type").isNotEmpty())
                .andExpect(jsonPath("$.enabled").isNotEmpty())
                .andExpect(jsonPath("$.order").isNotEmpty())
                .andExpect(jsonPath("$.remark").isNotEmpty())
                .andReturn().getResponse();

        var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(Menu.class));
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
                .andExpect(jsonPath("$.applicationId").value(body.getApplicationId()))
                .andExpect(jsonPath("$.code").value(body.getCode()))
                .andExpect(jsonPath("$.name").value(body.getName()))
                .andExpect(jsonPath("$.icon").value(body.getIcon()))
                .andExpect(jsonPath("$.url").value(body.getUrl()))
                .andExpect(jsonPath("$.type").value(body.getType()))
                .andExpect(jsonPath("$.enabled").value(body.getEnabled()))
                .andExpect(jsonPath("$.order").value(body.getOrder()))
                .andExpect(jsonPath("$.remark").value(body.getRemark()));

        // 列表查询
        var listRequest = MockMvcRequestBuilders.get(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .queryParam("applicationId", application.getId())
                .queryParam("code", "dashboard-test")
                .header(XForwardedHeaders.TENANT, "master")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(listRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$[0].id").value(body.getId()))
                .andExpect(jsonPath("$[0].applicationId").value(body.getApplicationId()))
                .andExpect(jsonPath("$[0].code").value(body.getCode()))
                .andExpect(jsonPath("$[0].name").value(body.getName()))
                .andExpect(jsonPath("$[0].icon").value(body.getIcon()))
                .andExpect(jsonPath("$[0].url").value(body.getUrl()))
                .andExpect(jsonPath("$[0].type").value(body.getType()))
                .andExpect(jsonPath("$[0].enabled").value(body.getEnabled()))
                .andExpect(jsonPath("$[0].order").value(body.getOrder()))
                .andExpect(jsonPath("$[0].remark").value(body.getRemark()));


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
     * @see MenuController#add
     * @see MenuController#update
     * @see MenuController#details
     * @see MenuController#delete
     */
    @Test
    public void case1(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore) throws Exception {
        var application = this.getApplication();

        // 新增
        var addRequest = MockMvcRequestBuilders.post(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(MenuParams.builder()
                        .applicationId(application.getId())
                        .parentId("")
                        .code("dashboard-test")
                        .name("测试菜单")
                        .icon("")
                        .url("@/test/index")
                        .type(MenuType.BACKEND.getValue())
                        .enabled(Boolean.TRUE)
                        .order(0)
                        .remark("测试时使用的菜单")
                        .build()))
                .header(XForwardedHeaders.TENANT, "master")
                .accept(MediaType.APPLICATION_JSON);

        var response = mvc.perform(addRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.applicationId").isNotEmpty())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.icon").isEmpty())
                .andExpect(jsonPath("$.url").isNotEmpty())
                .andExpect(jsonPath("$.type").isNotEmpty())
                .andExpect(jsonPath("$.enabled").isNotEmpty())
                .andExpect(jsonPath("$.order").isNotEmpty())
                .andExpect(jsonPath("$.remark").isNotEmpty())
                .andReturn().getResponse();

        var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(Menu.class));
        assertNotNull(body);

        // 更新(更新了 code 属性)
        var updateRequest = MockMvcRequestBuilders.put(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(MenuParams.builder()
                        .id(body.getId())
                        .applicationId(application.getId())
                        .parentId(body.getParentId())
                        .code("dashboard-test")
                        .name(body.getName())
                        .icon(body.getIcon())
                        .url("@/test/index")
                        .type(MenuType.BACKEND.getValue())
                        .enabled(Boolean.TRUE)
                        .order(0)
                        .remark("测试时使用的菜单")
                        .build()))
                .header(XForwardedHeaders.TENANT, "master")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(updateRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").value(body.getId()))
                .andExpect(jsonPath("$.applicationId").value(body.getApplicationId()))
                .andExpect(jsonPath("$.code").value(body.getCode()))
                .andExpect(jsonPath("$.name").value(body.getName()))
                .andExpect(jsonPath("$.icon").value(body.getIcon()))
                .andExpect(jsonPath("$.url").value(body.getUrl()))
                .andExpect(jsonPath("$.type").value(body.getType()))
                .andExpect(jsonPath("$.enabled").value(body.getEnabled()))
                .andExpect(jsonPath("$.order").value(body.getOrder()))
                .andExpect(jsonPath("$.remark").value(body.getRemark()));

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
                .andExpect(jsonPath("$.applicationId").value(body.getApplicationId()))
                .andExpect(jsonPath("$.code").value(body.getCode()))
                .andExpect(jsonPath("$.name").value(body.getName()))
                .andExpect(jsonPath("$.icon").value(body.getIcon()))
                .andExpect(jsonPath("$.url").value(body.getUrl()))
                .andExpect(jsonPath("$.type").value(body.getType()))
                .andExpect(jsonPath("$.enabled").value(body.getEnabled()))
                .andExpect(jsonPath("$.order").value(body.getOrder()))
                .andExpect(jsonPath("$.remark").value(body.getRemark()));

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
     * @see MenuController#add
     * @see MenuController#addPermission
     * @see MenuController#updatePermission
     * @see MenuController#listPermissions
     * @see MenuController#deletePermissions
     * @see MenuController#delete
     */
    @Test
    public void case2(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore) throws Exception {
        var application = this.getApplication();

        // 新增菜单
        var addRequest = MockMvcRequestBuilders.post(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(MenuParams.builder()
                        .applicationId(application.getId())
                        .parentId("")
                        .code("dashboard-test")
                        .name("测试菜单")
                        .icon("")
                        .url("@/test/index")
                        .type(MenuType.BACKEND.getValue())
                        .enabled(Boolean.TRUE)
                        .order(0)
                        .remark("测试时使用的菜单")
                        .build()))
                .header(XForwardedHeaders.TENANT, "master")
                .accept(MediaType.APPLICATION_JSON);

        var response = mvc.perform(addRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.applicationId").isNotEmpty())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.icon").isEmpty())
                .andExpect(jsonPath("$.url").isNotEmpty())
                .andExpect(jsonPath("$.type").isNotEmpty())
                .andExpect(jsonPath("$.enabled").isNotEmpty())
                .andExpect(jsonPath("$.order").isNotEmpty())
                .andExpect(jsonPath("$.remark").isNotEmpty())
                .andReturn().getResponse();

        var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(Menu.class));
        assertNotNull(body);

        // 新增权限
        var addPermissionRequest = MockMvcRequestBuilders.post(PATH + "/permissions")
                .cookie(this.getSessionCookie(PATH + "/permissions", mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(PermissionParams.builder()
                        .applicationId(application.getId())
                        .menuId(body.getId())
                        .code("test")
                        .name("测试权限")
                        .build())
                )
                .header(XForwardedHeaders.TENANT, "master")
                .accept(MediaType.APPLICATION_JSON);

        response = mvc.perform(addPermissionRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.applicationId").value(application.getId()))
                .andExpect(jsonPath("$.menuId").value(body.getId()))
                .andExpect(jsonPath("$.code").value("test"))
                .andExpect(jsonPath("$.name").value("测试权限"))
                .andReturn().getResponse();

        var permissionBody = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(Permission.class));
        assertNotNull(permissionBody);

        // 更新权限(更新标识)
        var updatePermissionRequest = MockMvcRequestBuilders.put(PATH + "/permissions")
                .cookie(this.getSessionCookie(PATH + "/permissions", mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(PermissionParams.builder()
                        .id(permissionBody.getId())
                        .applicationId(application.getId())
                        .menuId(body.getId())
                        .code("test2")
                        .name("测试权限")
                        .build())
                )
                .header(XForwardedHeaders.TENANT, "master")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(updatePermissionRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$.id").value(permissionBody.getId()))
                .andExpect(jsonPath("$.applicationId").value(permissionBody.getApplicationId()))
                .andExpect(jsonPath("$.menuId").value(permissionBody.getMenuId()))
                .andExpect(jsonPath("$.code").value("test2"))
                .andExpect(jsonPath("$.name").value(permissionBody.getName()));

        // 列表查询
        var listPermissionRequest = MockMvcRequestBuilders.get(PATH + "/permissions")
                .cookie(this.getSessionCookie(PATH + "/permissions", mvc, cookieStore))
                .queryParam("applicationId", application.getId())
                .queryParam("menuId", permissionBody.getMenuId())
                .header(XForwardedHeaders.TENANT, "master")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(listPermissionRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$[0].id").value(permissionBody.getId()))
                .andExpect(jsonPath("$[0].applicationId").value(permissionBody.getApplicationId()))
                .andExpect(jsonPath("$[0].menuId").value(permissionBody.getMenuId()))
                .andExpect(jsonPath("$[0].code").value("test2"))
                .andExpect(jsonPath("$[0].name").value(permissionBody.getName()));

        // 删除权限
        var deletePermissionRequest = MockMvcRequestBuilders.delete(PATH + "/permissions")
                .cookie(this.getSessionCookie(PATH+ "/permissions", mvc, cookieStore))
                .queryParam("ids", permissionBody.getId())
                .header(XForwardedHeaders.TENANT, "master")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(deletePermissionRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("1"));


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
