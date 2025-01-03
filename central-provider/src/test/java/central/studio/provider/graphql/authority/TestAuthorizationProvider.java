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
import central.studio.provider.database.persistence.authority.entity.MenuEntity;
import central.studio.provider.database.persistence.authority.entity.PermissionEntity;
import central.studio.provider.database.persistence.authority.entity.RoleEntity;
import central.studio.provider.database.persistence.organization.AccountPersistence;
import central.studio.provider.database.persistence.organization.entity.AccountEntity;
import central.studio.provider.database.persistence.system.DictionaryPersistence;
import central.studio.provider.database.persistence.system.entity.DictionaryEntity;
import central.studio.provider.graphql.TestContext;
import lombok.Setter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

    @Setter(onMethod_ = @Autowired)
    private AccountPersistence accountPersistence;

    @Setter(onMethod_ = @Autowired)
    private RolePersistence rolePersistence;

    @Setter(onMethod_ = @Autowired)
    private PermissionPersistence permissionPersistence;

    @Setter(onMethod_ = @Autowired)
    private RolePermissionPersistence rolePermissionPersistence;

    @Setter(onMethod_ = @Autowired)
    private RolePrincipalPersistence rolePrincipalPersistence;

    @Setter(onMethod_ = @Autowired)
    private RoleRangePersistence roleRangePersistence;

    @Setter(onMethod_ = @Autowired)
    private TestContext context;

    @BeforeAll
    public static void setup(@Autowired DataContext context, @Autowired SqlExecutor executor, @Autowired TestContext testContext) throws Exception {
        // 初始化应用
        var application = testContext.getApplication();
        var initializer = new ApplicationInitializer(executor);
        initializer.initialize("master", application);

        SaasContainer container = null;
        while (container == null || container.getApplications().isEmpty()) {
            Thread.sleep(100);
            container = context.getData(DataFetcherType.SAAS);
        }
    }


    @AfterAll
    public static void cleanup(@Autowired TestContext testContext,
                               @Autowired MenuPersistence menuPersistence,
                               @Autowired DictionaryPersistence dictionaryPersistence,
                               @Autowired RolePersistence rolePersistence,
                               @Autowired AccountPersistence accountPersistence) {
        // 清理数据
        var tenant = testContext.getTenant();
        var application = testContext.getApplication();

        menuPersistence.deleteBy(Conditions.of(MenuEntity.class).eq(MenuEntity::getApplicationId, application.getId()), tenant.getCode());
        dictionaryPersistence.deleteBy(Conditions.of(DictionaryEntity.class).eq(DictionaryEntity::getApplicationId, application.getId()), tenant.getCode());
        rolePersistence.deleteBy(Conditions.of(RoleEntity.class).eq(RoleEntity::getApplicationId, application.getId()), tenant.getCode());
        accountPersistence.deleteBy(Conditions.of(AccountEntity.class).like(AccountEntity::getUsername, "test%"), tenant.getCode());
    }

    /**
     * @see AuthorizationProvider#findApplication
     */
    @Test
    public void case1() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();

        var code = application.getCode();
        var secret = application.getSecret();

        // 测试错误的 secret
        {
            Application result = null;
            Exception exception = null;
            try {
                result = provider.findApplication(code, "wrong secret", tenant.getCode());
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
                result = provider.findApplication(code, secret, tenant.getCode());
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
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();

        // syssa 有所有权限
        var applications = provider.findApplications("syssa", MenuType.BACKEND.getValue(), tenant.getCode());

        assertNotNull(applications);
        assertFalse(applications.isEmpty());
    }

    /**
     * @see AuthorizationProvider#findRoles
     * @see AuthorizationProvider#findMenus
     * @see AuthorizationProvider#findPermissions
     */
    @Test
    public void case3() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();

        // 测试准备数据
        var account = this.accountPersistence.insert(AccountInput.builder()
                .username("test")
                .email("test@central-x.com")
                .mobile("13111111111")
                .name("CentralX")
                .avatar("1234")
                .enabled(Boolean.TRUE)
                .deleted(Boolean.FALSE)
                .build(), "syssa", tenant.getCode());
        var role = this.rolePersistence.insert(RoleInput.builder()
                .applicationId(application.getId())
                .code("10000")
                .name("测试角色")
                .unitId("")
                .enabled(Boolean.TRUE)
                .remark("测试角色")
                .build(), "syssa", tenant.getCode());

        var permission = permissionPersistence.findFirstBy(Columns.all(), Conditions.of(PermissionEntity.class).eq(PermissionEntity::getApplicationId, application.getId()).eq(PermissionEntity::getCode, application.getCode() + ":system:dictionary:view"), null, "master");

        // 为角色分配权限
        this.rolePermissionPersistence.insert(RolePermissionInput.builder()
                .applicationId(application.getId())
                .roleId(role.getId())
                .permissionId(permission.getId())
                .build(), "syssa", tenant.getCode());

        // 为帐户分配角色
        this.rolePrincipalPersistence.insert(RolePrincipalInput.builder()
                .applicationId(application.getId())
                .roleId(role.getId())
                .principalId(account.getId())
                .type(PrincipalType.ACCOUNT.getValue())
                .build(), "syssa", tenant.getCode());

        try {
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // test findRoles
            var roles = provider.findRoles(account.getId(), application.getId(), tenant.getCode());
            assertNotNull(roles);
            assertEquals(1, roles.size());
            assertEquals(role.getCode(), roles.get(0).getCode());

            // test findMenus
            var menus = provider.findMenus(account.getId(), MenuType.BACKEND.getValue(), application.getId(), tenant.getCode());
            assertNotNull(menus);
            assertEquals(2, menus.size());
            assertTrue(menus.stream().anyMatch(it -> Objects.equals(permission.getMenuId(), it.getId())));

            // test findPermissions
            var permissions = provider.findPermissions(account.getId(), application.getId(), tenant.getCode());
            assertNotNull(permissions);
            assertEquals(1, permissions.size());
            assertEquals(permission.getCode(), permissions.get(0).getCode());
        } finally {
            rolePersistence.deleteByIds(List.of(role.getId()), tenant.getCode());
            accountPersistence.deleteByIds(List.of(account.getId()), tenant.getCode());
        }
    }
}
