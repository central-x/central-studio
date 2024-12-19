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

import central.data.authority.*;
import central.data.authority.option.MenuType;
import central.data.authority.option.PrincipalType;
import central.data.authority.option.RangeCategory;
import central.data.authority.option.RangeType;
import central.data.organization.Account;
import central.data.organization.AccountInput;
import central.data.saas.Application;
import central.lang.reflect.TypeRef;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Conditions;
import central.starter.test.cookie.CookieStore;
import central.studio.dashboard.DashboardApplication;
import central.studio.dashboard.controller.TestController;
import central.studio.dashboard.controller.authority.controller.RoleController;
import central.studio.dashboard.controller.authority.param.RoleParams;
import central.studio.dashboard.controller.authority.param.RolePermissionParams;
import central.studio.dashboard.controller.authority.param.RolePrincipalParams;
import central.studio.dashboard.controller.authority.param.RoleRangeParams;
import central.studio.dashboard.logic.authority.MenuLogic;
import central.studio.dashboard.logic.authority.RoleLogic;
import central.studio.dashboard.logic.organization.AccountLogic;
import central.util.Jsonx;
import central.util.Listx;
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

/**
 * Role Controller Test Cases
 *
 * @author Alan Yeh
 * @see RoleController
 * @since 2024/12/13
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = DashboardApplication.class)
public class TestRoleController extends TestController {

    private static final String PATH = "/dashboard/api/authority/roles";

    @BeforeAll
    public static void setup(@Autowired DataContext context,
                             @Autowired MenuLogic menuLogic,
                             @Autowired AccountLogic accountLogic) throws Exception {
        SaasContainer container = null;
        while (container == null || Listx.isNullOrEmpty(container.getApplications())) {
            Thread.sleep(100);
            container = context.getData(DataFetcherType.SAAS);
        }

        var application = container.getApplicationByCode("central-dashboard");

        var menuInput = MenuInput.builder()
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
                .build();

        var menu = menuLogic.insert(menuInput, "syssa", "master");

        var permissionInput = PermissionInput.builder()
                .applicationId(application.getId())
                .menuId(menu.getId())
                .code("add")
                .name("添加")
                .build();

        var permission = menuLogic.insertPermission(permissionInput, "syssa", "master");

        var accountInput = AccountInput.builder()
                .username("centralx")
                .email("support@central-x.com")
                .mobile("13000000000")
                .name("CentralX")
                .avatar("man")
                .enabled(Boolean.TRUE)
                .deleted(Boolean.FALSE)
                .build();
        accountLogic.insert(accountInput, "syssa", "master");
    }

    @AfterAll
    public static void cleanup(@Autowired MenuLogic menuLogic,
                               @Autowired AccountLogic accountLogic) {
        var menus = menuLogic.findBy(null, null, Conditions.of(Menu.class).eq(Menu::getCode, "dashboard-test"), null, "master");
        var menuIds = menus.stream().map(Menu::getId).toList();
        menuLogic.deletePermissionsBy(Conditions.of(Permission.class).in(Permission::getMenuId, menuIds), "syssa", "master");
        menuLogic.deleteByIds(menuIds, "syssa", "master");
        accountLogic.deleteBy(Conditions.of(Account.class).eq(Account::getUsername, "centralx"), "syssa", "master");
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

    @Setter(onMethod_ = @Autowired)
    private MenuLogic menuLogic;

    private Permission getPermission() {
        var menus = menuLogic.findBy(null, null, Conditions.of(Menu.class).eq(Menu::getCode, "dashboard-test"), null, "master");
        var menu = Listx.getFirstOrNull(menus);
        return Listx.getFirstOrNull(menu.getPermissions());
    }

    @Setter(onMethod_ = @Autowired)
    private AccountLogic accountLogic;

    private Account getAccount() {
        var accounts = accountLogic.findBy(1L, 0L, Conditions.of(Account.class).eq(Account::getUsername, "centralx"), null, "master");
        return Listx.getFirstOrNull(accounts);
    }

    @Setter(onMethod_ = @Autowired)
    private RoleLogic roleLogic;

    /**
     * @see RoleController#add
     * @see RoleController#details
     * @see RoleController#page
     * @see RoleController#delete
     */
    @Test
    public void case0(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore) throws Exception {
        var application = this.getApplication();

        // 新增
        var addRequest = MockMvcRequestBuilders.post(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(RoleParams.builder()
                        .applicationId(application.getId())
                        .code("test-role")
                        .name("测试角色")
                        .enabled(Boolean.TRUE)
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
                .andExpect(jsonPath("$.enabled").isNotEmpty())
                .andExpect(jsonPath("$.remark").isNotEmpty())
                .andReturn().getResponse();

        var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(Role.class));
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
                .andExpect(jsonPath("$.enabled").value(body.getEnabled()))
                .andExpect(jsonPath("$.remark").value(body.getRemark()));

        // 列表查询
        var pageRequest = MockMvcRequestBuilders.get(PATH + "/page")
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .queryParam("applicationId", application.getId())
                .queryParam("code", "test-role")
                .header(XForwardedHeaders.TENANT, "master")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(pageRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
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
                .andExpect(jsonPath("$.data[0].remark").value(body.getRemark()));


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
     * @see RoleController#add
     * @see RoleController#update
     * @see RoleController#details
     * @see RoleController#delete
     */
    @Test
    public void case1(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore) throws Exception {
        var application = this.getApplication();

        // 新增
        var addRequest = MockMvcRequestBuilders.post(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(RoleParams.builder()
                        .applicationId(application.getId())
                        .code("test-role")
                        .name("测试角色")
                        .enabled(Boolean.TRUE)
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
                .andExpect(jsonPath("$.enabled").isNotEmpty())
                .andExpect(jsonPath("$.remark").isNotEmpty())
                .andReturn().getResponse();

        var body = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.of(Role.class));
        assertNotNull(body);

        // 更新(更新了 code 属性)
        var updateRequest = MockMvcRequestBuilders.put(PATH)
                .cookie(this.getSessionCookie(PATH, mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(RoleParams.builder()
                        .id(body.getId())
                        .applicationId(application.getId())
                        .code("test-role2")
                        .name(body.getName())
                        .enabled(Boolean.TRUE)
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
                .andExpect(jsonPath("$.code").value("test-role2"))
                .andExpect(jsonPath("$.name").value(body.getName()))
                .andExpect(jsonPath("$.enabled").value(body.getEnabled()))
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
                .andExpect(jsonPath("$.code").value("test-role2"))
                .andExpect(jsonPath("$.name").value(body.getName()))
                .andExpect(jsonPath("$.enabled").value(body.getEnabled()))
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
     * @see RoleController#addPermissions
     * @see RoleController#getPermissions
     * @see RoleController#deletePermissions
     */
    @Test
    public void case2(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore) throws Exception {
        var application = this.getApplication();
        var permission = this.getPermission();
        var role = this.roleLogic.insert(RoleInput.builder()
                .applicationId(application.getId())
                .code("test")
                .name("测试角色")
                .unitId("")
                .enabled(Boolean.TRUE)
                .remark("测试用的角色")
                .build(), "syssa", "master");

        // 新增
        var addRequest = MockMvcRequestBuilders.post(PATH + "/permissions")
                .cookie(this.getSessionCookie(PATH + "/permissions", mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(RolePermissionParams.builder()
                        .applicationId(application.getId())
                        .roleId(role.getId())
                        .permissionIds(List.of(permission.getId()))
                        .build()))
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");

        var response = mvc.perform(addRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].applicationId").isNotEmpty())
                .andExpect(jsonPath("$[0].application.id").isNotEmpty())
                .andExpect(jsonPath("$[0].roleId").isNotEmpty())
                .andExpect(jsonPath("$[0].role.id").isNotEmpty())
                .andExpect(jsonPath("$[0].permissionId").isNotEmpty())
                .andExpect(jsonPath("$[0].permission.id").isNotEmpty())
                .andReturn().getResponse();

        var list = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.ofList(RolePermission.class));
        var body = Listx.getFirstOrNull(list);
        assertNotNull(body);

        // 查询角色已授权权限
        var getRequest = MockMvcRequestBuilders.get(PATH + "/permissions")
                .cookie(this.getSessionCookie(PATH + "/permissions", mvc, cookieStore))
                .queryParam("id", role.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");

        mvc.perform(getRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(permission.getMenuId()))
                .andExpect(jsonPath("$[0].applicationId").value(body.getApplicationId()))
                .andExpect(jsonPath("$[0].permissions[0].id").value(body.getPermissionId()));

        // 删除角色权限
        var deleteRequest = MockMvcRequestBuilders.delete(PATH + "/permissions")
                .cookie(this.getSessionCookie(PATH + "/permissions", mvc, cookieStore))
                .queryParam("ids", body.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");

        mvc.perform(deleteRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("1"));

        this.roleLogic.deleteByIds(List.of(role.getId()), "syssa", "master");
    }

    /**
     * @see RoleController#addPrincipals
     * @see RoleController#getPrincipals
     * @see RoleController#deletePrincipals
     */
    @Test
    public void case3(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore) throws Exception {
        var application = this.getApplication();
        var account = this.getAccount();
        var role = this.roleLogic.insert(RoleInput.builder()
                .applicationId(application.getId())
                .code("test")
                .name("测试角色")
                .unitId("")
                .enabled(Boolean.TRUE)
                .remark("测试用的角色")
                .build(), "syssa", "master");

        // 添加角色授权
        var addRequest = MockMvcRequestBuilders.post(PATH + "/principals")
                .cookie(this.getSessionCookie(PATH + "/principals", mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(RolePrincipalParams.builder()
                        .applicationId(application.getId())
                        .roleId(role.getId())
                        .principalIds(List.of(account.getId()))
                        .type(PrincipalType.ACCOUNT.getValue())
                        .build()))
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");

        var response = mvc.perform(addRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].applicationId").isNotEmpty())
                .andExpect(jsonPath("$[0].application.id").isNotEmpty())
                .andExpect(jsonPath("$[0].roleId").isNotEmpty())
                .andExpect(jsonPath("$[0].role.id").isNotEmpty())
                .andExpect(jsonPath("$[0].principalId").isNotEmpty())
                .andExpect(jsonPath("$[0].type").isNotEmpty())
                .andExpect(jsonPath("$[0].account").isMap())
                .andExpect(jsonPath("$[0].account.id").isNotEmpty())
                .andReturn().getResponse();

        var list = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.ofList(RolePrincipal.class));
        var body = Listx.getFirstOrNull(list);
        assertNotNull(body);

        // 查询角色授权
        var getRequest = MockMvcRequestBuilders.get(PATH + "/principals")
                .cookie(this.getSessionCookie(PATH + "/principals", mvc, cookieStore))
                .queryParam("id", role.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");

        mvc.perform(getRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(body.getId()))
                .andExpect(jsonPath("$[0].applicationId").value(body.getApplicationId()))
                .andExpect(jsonPath("$[0].application.id").value(body.getApplication().getId()))
                .andExpect(jsonPath("$[0].roleId").value(body.getRoleId()))
                .andExpect(jsonPath("$[0].role.id").value(body.getRole().getId()))
                .andExpect(jsonPath("$[0].principalId").value(body.getPrincipalId()))
                .andExpect(jsonPath("$[0].type").value(body.getType()))
                .andExpect(jsonPath("$[0].account.id").value(body.getAccount().getId()));

        // 删除角色授权
        var deleteRequest = MockMvcRequestBuilders.delete(PATH + "/principals")
                .cookie(this.getSessionCookie(PATH + "/principals", mvc, cookieStore))
                .queryParam("ids", body.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");

        mvc.perform(deleteRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("1"));

        this.roleLogic.deleteByIds(List.of(role.getId()), "syssa", "master");
    }

    /**
     * @see RoleController#addRanges
     * @see RoleController#getRanges
     * @see RoleController#deleteRanges
     */
    @Test
    public void case4(@Autowired MockMvc mvc, @Autowired CookieStore cookieStore) throws Exception {
        var application = this.getApplication();
        var account = this.getAccount();
        var role = this.roleLogic.insert(RoleInput.builder()
                .applicationId(application.getId())
                .code("test")
                .name("测试角色")
                .unitId("")
                .enabled(Boolean.TRUE)
                .remark("测试用的角色")
                .build(), "syssa", "master");

        // 添加角色授权范围
        var addRequest = MockMvcRequestBuilders.post(PATH + "/ranges")
                .cookie(this.getSessionCookie(PATH + "/ranges", mvc, cookieStore))
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jsonx.Default().serialize(RoleRangeParams.builder()
                        .applicationId(application.getId())
                        .roleId(role.getId())
                        .category(RangeCategory.ORGANIZATION.getValue())
                        .dataIds(List.of(account.getId()))
                        .type(RangeType.ACCOUNT.getValue())
                        .build()))
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");

        var response = mvc.perform(addRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].applicationId").isNotEmpty())
                .andExpect(jsonPath("$[0].application.id").isNotEmpty())
                .andExpect(jsonPath("$[0].roleId").isNotEmpty())
                .andExpect(jsonPath("$[0].role.id").isNotEmpty())
                .andExpect(jsonPath("$[0].category").isNotEmpty())
                .andExpect(jsonPath("$[0].type").isNotEmpty())
                .andExpect(jsonPath("$[0].dataId").isNotEmpty())
                .andReturn().getResponse();

        var list = Jsonx.Default().deserialize(response.getContentAsString(), TypeRef.ofList(RoleRange.class));
        var body = Listx.getFirstOrNull(list);
        assertNotNull(body);

        // 查询角色授权范围
        var getRequest = MockMvcRequestBuilders.get(PATH + "/ranges")
                .cookie(this.getSessionCookie(PATH + "/ranges", mvc, cookieStore))
                .queryParam("id", role.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");
        mvc.perform(getRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.not(Matchers.emptyString())))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(body.getId()))
                .andExpect(jsonPath("$[0].applicationId").value(body.getApplicationId()))
                .andExpect(jsonPath("$[0].application.id").value(body.getApplication().getId()))
                .andExpect(jsonPath("$[0].roleId").value(body.getRoleId()))
                .andExpect(jsonPath("$[0].role.id").value(body.getRole().getId()))
                .andExpect(jsonPath("$[0].category").value(body.getCategory()))
                .andExpect(jsonPath("$[0].type").value(body.getType()))
                .andExpect(jsonPath("$[0].dataId").value(body.getDataId()));

        // 删除角色授权范围
        var deleteRequest = MockMvcRequestBuilders.delete(PATH + "/ranges")
                .cookie(this.getSessionCookie(PATH + "/ranges", mvc, cookieStore))
                .queryParam("ids", body.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header(XForwardedHeaders.TENANT, "master");
        mvc.perform(deleteRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("1"));

        this.roleLogic.deleteByIds(List.of(role.getId()), "syssa", "master");
    }
}
