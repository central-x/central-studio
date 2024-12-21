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

package central.studio.provider.graphql.authority;

import central.data.authority.RoleInput;
import central.data.authority.RolePermissionInput;
import central.data.authority.RolePrincipalInput;
import central.data.authority.option.MenuType;
import central.data.authority.option.PrincipalType;
import central.data.organization.AccountInput;
import central.data.saas.Application;
import central.data.saas.ApplicationInput;
import central.provider.graphql.authority.AuthorizationProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.SqlExecutor;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.initialization.ApplicationInitializer;
import central.studio.provider.database.persistence.authority.*;
import central.studio.provider.database.persistence.authority.entity.*;
import central.studio.provider.database.persistence.organization.AccountPersistence;
import central.studio.provider.database.persistence.saas.ApplicationPersistence;
import central.studio.provider.database.persistence.saas.entity.ApplicationEntity;
import central.studio.provider.database.persistence.system.DictionaryPersistence;
import central.studio.provider.database.persistence.system.entity.DictionaryEntity;
import central.util.Guidx;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AuthorizationProvider Test Cases
 * <p>
 * 授权
 *
 * @author Alan Yeh
 * @since 2024/05/27
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestAuthorizationProvider {

    @Setter(onMethod_ = @Autowired)
    private AuthorizationProvider provider;

    @BeforeAll
    public static void setup(@Autowired DataContext context, @Autowired SqlExecutor executor, @Autowired ApplicationPersistence applicationPersistence) throws Exception {
        var application = applicationPersistence.insert(ApplicationInput.builder()
                .code("test")
                .name("测试应用")
                .logo("1234")
                .url("http://127.0.0.1:13100")
                .contextPath("/test")
                .secret(Guidx.nextID())
                .enabled(Boolean.TRUE)
                .remark("测试应用")
                .routes(new ArrayList<>())
                .build(), "syssa");

        var initializer = new ApplicationInitializer(executor);
        initializer.initialize("master", application);

        SaasContainer container = null;
        while (container == null || Listx.isNullOrEmpty(container.getApplications())) {
            Thread.sleep(100);
            container = context.getData(DataFetcherType.SAAS);
        }
    }


    @AfterAll
    public static void cleanup(@Autowired ApplicationPersistence applicationPersistence,
                               @Autowired MenuPersistence menuPersistence,
                               @Autowired DictionaryPersistence dictionaryPersistence,
                               @Autowired RolePermissionPersistence rolePermissionPersistence,
                               @Autowired RolePrincipalPersistence rolePrincipalPersistence,
                               @Autowired RoleRangePersistence roleRangePersistence) {
        // 清理数据
        var application = applicationPersistence.findFirstBy(Columns.all(), Conditions.of(ApplicationEntity.class).eq(ApplicationEntity::getCode, "test"), null);
        menuPersistence.deleteBy(Conditions.of(MenuEntity.class).eq(MenuEntity::getApplicationId, application.getId()), "master");
        dictionaryPersistence.deleteBy(Conditions.of(DictionaryEntity.class).eq(DictionaryEntity::getApplicationId, application.getId()), "master");
        rolePermissionPersistence.deleteBy(Conditions.of(RolePermissionEntity.class).eq(RolePermissionEntity::getApplicationId, application.getId()), "master");
        rolePrincipalPersistence.deleteBy(Conditions.of(RolePrincipalEntity.class).eq(RolePrincipalEntity::getApplicationId, application.getId()), "master");
        roleRangePersistence.deleteBy(Conditions.of(RoleRangeEntity.class).eq(RoleRangeEntity::getApplicationId, application.getId()), "master");
        applicationPersistence.deleteByIds(List.of(application.getId()));
    }

    @Setter(onMethod_ = @Autowired)
    private ApplicationPersistence applicationPersistence;

    private ApplicationEntity getApplication() {
        return applicationPersistence.findFirstBy(Columns.all(), Conditions.of(ApplicationEntity.class).eq(ApplicationEntity::getCode, "test"), null);
    }

    /**
     * @see AuthorizationProvider#findApplication
     */
    @Test
    public void case1() {
        var application = this.getApplication();
        var code = application.getCode();
        var secret = application.getSecret();

        // 测试错误的 secret
        {
            Application result = null;
            Exception exception = null;
            try {
                result = provider.findApplication(code, "wrong secret");
            } catch (Exception ex) {
                exception = ex;
            }
            assertNotNull(exception);
            assertNull(result);
        }

        // 测试正确的的 secret
        {
            Application result = null;
            Exception exception = null;
            try {
                result = provider.findApplication(code, secret);
            } catch (Exception ex) {
                exception = ex;
            }
            if (exception != null) {
                exception.printStackTrace(System.err);
            }
            assertNull(exception);
            assertNotNull(result);
        }
    }

    /**
     * @see AuthorizationProvider#findApplications
     */
    @Test
    public void case2() {
        // syssa 有所有权限
        var applications = provider.findApplications("syssa", MenuType.BACKEND.getValue(), "master");

        assertNotNull(applications);
        assertFalse(applications.isEmpty());
    }

    /**
     * @see AuthorizationProvider#findRoles
     * @see AuthorizationProvider#findMenus
     * @see AuthorizationProvider#findPermissions
     */
    @Test
    public void case3(@Autowired AccountPersistence accountPersistence,
                      @Autowired RolePersistence rolePersistence,
                      @Autowired PermissionPersistence permissionPersistence,
                      @Autowired RolePermissionPersistence rolePermissionPersistence,
                      @Autowired RolePrincipalPersistence rolePrincipalPersistence) {
        // 测试准备数据
        var application = this.getApplication();
        var account = accountPersistence.insert(AccountInput.builder()
                .username("centralx")
                .email("support@central-x.com")
                .mobile("13111111111")
                .name("CentralX")
                .avatar("1234")
                .enabled(Boolean.TRUE)
                .deleted(Boolean.FALSE)
                .build(), "syssa", "master");
        var role = rolePersistence.insert(RoleInput.builder()
                .applicationId(application.getId())
                .code("10000")
                .name("测试角色")
                .unitId("")
                .enabled(Boolean.TRUE)
                .remark("测试角色")
                .build(), "syssa", "master");

        var permission = permissionPersistence.findFirstBy(Columns.all(), Conditions.of(PermissionEntity.class).eq(PermissionEntity::getApplicationId, application.getId()).eq(PermissionEntity::getCode, application.getCode() + ":system:dictionary:view"), null, "master");

        // 为角色分配权限
        rolePermissionPersistence.insert(RolePermissionInput.builder()
                .applicationId(application.getId())
                .roleId(role.getId())
                .permissionId(permission.getId())
                .build(), "syssa", "master");

        // 为帐户分配角色
        rolePrincipalPersistence.insert(RolePrincipalInput.builder()
                .applicationId(application.getId())
                .roleId(role.getId())
                .principalId(account.getId())
                .type(PrincipalType.ACCOUNT.getValue())
                .build(), "syssa", "master");

        try {
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            var roles = provider.findRoles(account.getId(), application.getId(), "master");
            assertNotNull(roles);
            assertEquals(1, roles.size());
            assertEquals(role.getCode(), roles.get(0).getCode());

            var menus = provider.findMenus(account.getId(), MenuType.BACKEND.getValue(), application.getId(), "master");
            assertNotNull(menus);
            assertEquals(2, menus.size());
            assertTrue(menus.stream().anyMatch(it -> Objects.equals(permission.getMenuId(), it.getId())));

            var permissions = provider.findPermissions(account.getId(), application.getId(), "master");
            assertNotNull(permissions);
            assertEquals(1, permissions.size());
            assertEquals(permission.getCode(), permissions.get(0).getCode());
        } finally {
            rolePersistence.deleteByIds(List.of(role.getId()), "master");
            accountPersistence.deleteByIds(List.of(account.getId()), "master");
        }
    }
}
