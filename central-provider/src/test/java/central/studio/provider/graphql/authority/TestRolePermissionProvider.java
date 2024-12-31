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

import central.data.authority.*;
import central.data.authority.option.MenuType;
import central.provider.graphql.authority.RolePermissionProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.authority.MenuPersistence;
import central.studio.provider.database.persistence.authority.PermissionPersistence;
import central.studio.provider.database.persistence.authority.RolePermissionPersistence;
import central.studio.provider.database.persistence.authority.RolePersistence;
import central.studio.provider.database.persistence.authority.entity.MenuEntity;
import central.studio.provider.database.persistence.authority.entity.PermissionEntity;
import central.studio.provider.database.persistence.authority.entity.RoleEntity;
import central.studio.provider.database.persistence.authority.entity.RolePermissionEntity;
import central.studio.provider.graphql.TestContext;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Role Permission Provider Test Cases
 *
 * @author Alan Yeh
 * @since 2024/12/15
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestRolePermissionProvider {

    @Setter(onMethod_ = @Autowired)
    private RolePermissionProvider provider;

    @Setter(onMethod_ = @Autowired)
    private RolePermissionPersistence persistence;

    @Setter(onMethod_ = @Autowired)
    private TestContext context;

    @BeforeAll
    public static void setup(@Autowired DataContext context,
                             @Autowired TestContext testContext,
                             @Autowired MenuPersistence menuPersistence,
                             @Autowired PermissionPersistence permissionPersistence,
                             @Autowired RolePersistence rolePersistence) throws Exception {
        SaasContainer container = null;
        while (container == null || container.getApplications().isEmpty()) {
            Thread.sleep(100);
            container = context.getData(DataFetcherType.SAAS);
        }

        var tenant = testContext.getTenant();
        var application = testContext.getApplication();

        var menu = menuPersistence.insert(MenuInput.builder()
                .applicationId(application.getId())
                .parentId("")
                .code("test")
                .name("测试菜单")
                .icon("icon")
                .url("@/test")
                .type(MenuType.BACKEND.getValue())
                .enabled(Boolean.TRUE)
                .order(0)
                .remark("测试菜单")
                .build(), "syssa", tenant.getCode());

        permissionPersistence.insert(PermissionInput.builder()
                .applicationId(application.getId())
                .menuId(menu.getId())
                .code("add")
                .name("添加")
                .build(), "syssa", tenant.getCode());

        rolePersistence.insert(RoleInput.builder()
                .applicationId(application.getId())
                .code("test")
                .name("测试角色")
                .unitId("")
                .enabled(Boolean.TRUE)
                .remark("测试角色")
                .build(), "syssa", tenant.getCode());
    }

    @AfterAll
    public static void cleanup(@Autowired TestContext testContext,
                               @Autowired MenuPersistence menuPersistence,
                               @Autowired PermissionPersistence permissionPersistence,
                               @Autowired RolePersistence rolePersistence) {
        var tenant = testContext.getTenant();
        var application = testContext.getApplication();

        menuPersistence.deleteBy(Conditions.of(MenuEntity.class).eq(MenuEntity::getApplicationId, application.getId()), tenant.getCode());
        permissionPersistence.deleteBy(Conditions.of(PermissionEntity.class).eq(PermissionEntity::getApplicationId, application.getId()), tenant.getCode());
        rolePersistence.deleteBy(Conditions.of(RoleEntity.class).eq(RoleEntity::getApplicationId, application.getId()), tenant.getCode());
    }

    @BeforeEach
    @AfterEach
    public void clear() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();

        this.persistence.deleteBy(Conditions.of(RolePermissionEntity.class).eq(RolePermissionEntity::getApplicationId, application.getId()), tenant.getCode());
    }


    @Setter(onMethod_ = @Autowired)
    private PermissionPersistence permissionPersistence;

    private PermissionEntity getPermission() {
        return permissionPersistence.findFirstBy(Columns.all(), Conditions.of(PermissionEntity.class).eq(PermissionEntity::getCode, "add"), null, this.context.getTenant().getCode());
    }

    @Setter(onMethod_ = @Autowired)
    private RolePersistence rolePersistence;

    private RoleEntity getRole() {
        return rolePersistence.findFirstBy(Columns.all(), Conditions.of(RoleEntity.class).eq(RoleEntity::getCode, "test"), null, this.context.getTenant().getCode());
    }

    /**
     * @see RolePermissionProvider#insert
     * @see RolePermissionProvider#countBy
     * @see RolePermissionProvider#findById
     * @see RolePermissionProvider#deleteByIds
     */
    @Test
    public void case1() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();
        var permission = this.getPermission();
        var role = this.getRole();

        var input = RolePermissionInput.builder()
                .applicationId(application.getId())
                .roleId(role.getId())
                .permissionId(permission.getId())
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", tenant.getCode());
        assertNotNull(insert);
        assertEquals(input.getApplicationId(), insert.getApplicationId());
        assertEquals(input.getApplicationId(), insert.getApplication().getId());
        assertEquals(input.getRoleId(), insert.getRoleId());
        assertEquals(input.getRoleId(), insert.getRole().getId());
        assertEquals(input.getPermissionId(), insert.getPermissionId());
        assertEquals(input.getPermissionId(), insert.getPermission().getId());

        // test findById
        var findById = this.provider.findById(insert.getId(), tenant.getCode());
        assertNotNull(findById);
        assertEquals(input.getApplicationId(), findById.getApplicationId());
        assertEquals(input.getApplicationId(), findById.getApplication().getId());
        assertEquals(input.getRoleId(), findById.getRoleId());
        assertEquals(input.getRoleId(), findById.getRole().getId());
        assertEquals(input.getPermissionId(), findById.getPermissionId());
        assertEquals(input.getPermissionId(), findById.getPermission().getId());

        // test countBy
        var count = this.provider.countBy(Conditions.of(RolePermission.class).eq(RolePermission::getRoleId, role.getId()), tenant.getCode());
        assertEquals(1, count);

        // test deleteByIds
        var deleted = this.provider.deleteByIds(List.of(insert.getId()), tenant.getCode());
        assertEquals(1, deleted);
    }

    /**
     * @see RolePermissionProvider#insertBatch
     * @see RolePermissionProvider#findByIds
     * @see RolePermissionProvider#findBy
     * @see RolePermissionProvider#pageBy
     * @see RolePermissionProvider#deleteBy
     */
    @Test
    public void case2() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();
        var permission = this.getPermission();
        var role = this.getRole();

        var input = RolePermissionInput.builder()
                .applicationId(application.getId())
                .roleId(role.getId())
                .permissionId(permission.getId())
                .build();

        // test insertBatch
        var insertBatch = this.provider.insertBatch(List.of(input), "syssa", tenant.getCode());
        assertNotNull(insertBatch);
        assertEquals(1, insertBatch.size());

        var insert = Listx.getFirstOrNull(insertBatch);
        assertNotNull(insert);
        assertEquals(input.getApplicationId(), insert.getApplicationId());
        assertEquals(input.getApplicationId(), insert.getApplication().getId());
        assertEquals(input.getRoleId(), insert.getRoleId());
        assertEquals(input.getRoleId(), insert.getRole().getId());
        assertEquals(input.getPermissionId(), insert.getPermissionId());
        assertEquals(input.getPermissionId(), insert.getPermission().getId());

        // findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), tenant.getCode());
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertEquals(insert.getApplicationId(), fetched.getApplicationId());
        assertEquals(insert.getApplicationId(), fetched.getApplication().getId());
        assertEquals(insert.getRoleId(), fetched.getRoleId());
        assertEquals(insert.getRoleId(), fetched.getRole().getId());
        assertEquals(insert.getPermissionId(), fetched.getPermissionId());
        assertEquals(insert.getPermissionId(), fetched.getPermission().getId());

        // findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(RolePermission.class).eq(RolePermission::getRoleId, role.getId()), null, tenant.getCode());
        assertNotNull(findBy);
        assertEquals(1, findBy.size());

        fetched = Listx.getFirstOrNull(findBy);
        assertNotNull(fetched);
        assertEquals(insert.getApplicationId(), fetched.getApplicationId());
        assertEquals(insert.getApplicationId(), fetched.getApplication().getId());
        assertEquals(insert.getRoleId(), fetched.getRoleId());
        assertEquals(insert.getRoleId(), fetched.getRole().getId());
        assertEquals(insert.getPermissionId(), fetched.getPermissionId());
        assertEquals(insert.getPermissionId(), fetched.getPermission().getId());

        // pageBy
        var page = this.provider.pageBy(1, 20, Conditions.of(RolePermission.class).eq(RolePermission::getRoleId, role.getId()), null, tenant.getCode());
        assertNotNull(page);
        assertEquals(1, page.getPager().getPageIndex());
        assertEquals(20, page.getPager().getPageSize());
        assertEquals(1, page.getPager().getPageCount());
        assertEquals(1, page.getPager().getItemCount());

        fetched = Listx.getFirstOrNull(page.getData());
        assertNotNull(fetched);
        assertEquals(insert.getApplicationId(), fetched.getApplicationId());
        assertEquals(insert.getApplicationId(), fetched.getApplication().getId());
        assertEquals(insert.getRoleId(), fetched.getRoleId());
        assertEquals(insert.getRoleId(), fetched.getRole().getId());
        assertEquals(insert.getPermissionId(), fetched.getPermissionId());
        assertEquals(insert.getPermissionId(), fetched.getPermission().getId());

        var deleted = this.provider.deleteBy(Conditions.of(RolePermission.class).eq(RolePermission::getRoleId, role.getId()), tenant.getCode());
        assertEquals(1, deleted);
    }
}
