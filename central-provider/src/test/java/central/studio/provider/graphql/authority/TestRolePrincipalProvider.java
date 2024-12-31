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

import central.data.authority.MenuInput;
import central.data.authority.RoleInput;
import central.data.authority.RolePrincipal;
import central.data.authority.RolePrincipalInput;
import central.data.authority.option.MenuType;
import central.data.authority.option.PrincipalType;
import central.data.organization.AccountInput;
import central.data.organization.AreaInput;
import central.data.organization.DepartmentInput;
import central.data.organization.UnitInput;
import central.data.organization.option.AreaType;
import central.provider.graphql.authority.RolePrincipalProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.authority.MenuPersistence;
import central.studio.provider.database.persistence.authority.RolePersistence;
import central.studio.provider.database.persistence.authority.RolePrincipalPersistence;
import central.studio.provider.database.persistence.authority.entity.MenuEntity;
import central.studio.provider.database.persistence.authority.entity.RoleEntity;
import central.studio.provider.database.persistence.authority.entity.RolePrincipalEntity;
import central.studio.provider.database.persistence.organization.AccountPersistence;
import central.studio.provider.database.persistence.organization.AreaPersistence;
import central.studio.provider.database.persistence.organization.DepartmentPersistence;
import central.studio.provider.database.persistence.organization.UnitPersistence;
import central.studio.provider.database.persistence.organization.entity.AccountEntity;
import central.studio.provider.database.persistence.organization.entity.AreaEntity;
import central.studio.provider.database.persistence.organization.entity.DepartmentEntity;
import central.studio.provider.database.persistence.organization.entity.UnitEntity;
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
 * Role Principal Provider Test Cases
 *
 * @author Alan Yeh
 * @since 2024/12/15
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestRolePrincipalProvider {

    @Setter(onMethod_ = @Autowired)
    private RolePrincipalProvider provider;

    @Setter(onMethod_ = @Autowired)
    private RolePrincipalPersistence persistence;

    @Setter(onMethod_ = @Autowired)
    private TestContext context;

    @BeforeAll
    public static void setup(@Autowired DataContext context,
                             @Autowired TestContext testContext,
                             @Autowired MenuPersistence menuPersistence,
                             @Autowired RolePersistence rolePersistence,
                             @Autowired AccountPersistence accountPersistence,
                             @Autowired DepartmentPersistence departmentPersistence,
                             @Autowired UnitPersistence unitPersistence,
                             @Autowired AreaPersistence areaPersistence) throws Exception {
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

        rolePersistence.insert(RoleInput.builder()
                .applicationId(application.getId())
                .code("test")
                .name("测试角色")
                .unitId("")
                .enabled(Boolean.TRUE)
                .remark("测试角色")
                .build(), "syssa", tenant.getCode());

        var area = areaPersistence.insert(AreaInput.builder()
                .parentId("")
                .code("test")
                .name("测试行政区划")
                .type(AreaType.COUNTRY.getValue())
                .order(0)
                .build(), "syssa", tenant.getCode());

        var unit = unitPersistence.insert(UnitInput.builder()
                .parentId("")
                .areaId(area.getId())
                .code("test")
                .name("测试单位")
                .order(0)
                .build(), "syssa", tenant.getCode());

        var department = departmentPersistence.insert(DepartmentInput.builder()
                .parentId("")
                .unitId(unit.getId())
                .code("test")
                .name("测试部门")
                .order(0)
                .build(), "syssa", tenant.getCode());

        accountPersistence.insert(AccountInput.builder()
                .username("test")
                .email("test@central-x.com")
                .mobile("18888888888")
                .name("测试帐号")
                .avatar("1234")
                .enabled(Boolean.TRUE)
                .deleted(Boolean.FALSE)
                .build(), "syssa", tenant.getCode());
    }

    @AfterAll
    public static void cleanup(@Autowired TestContext testContext,
                               @Autowired MenuPersistence menuPersistence,
                               @Autowired RolePersistence rolePersistence,
                               @Autowired AccountPersistence accountPersistence,
                               @Autowired DepartmentPersistence departmentPersistence,
                               @Autowired UnitPersistence unitPersistence,
                               @Autowired AreaPersistence areaPersistence) {
        var tenant = testContext.getTenant();

        menuPersistence.deleteBy(Conditions.of(MenuEntity.class).eq(MenuEntity::getCode, "test"), tenant.getCode());
        rolePersistence.deleteBy(Conditions.of(RoleEntity.class).eq(RoleEntity::getCode, "test"), tenant.getCode());
        accountPersistence.deleteBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getUsername, "test"), tenant.getCode());
        areaPersistence.deleteBy(Conditions.of(AreaEntity.class).eq(AreaEntity::getCode, "test"), tenant.getCode());
        unitPersistence.deleteBy(Conditions.of(UnitEntity.class).eq(UnitEntity::getCode, "test"), tenant.getCode());
        departmentPersistence.deleteBy(Conditions.of(DepartmentEntity.class).eq(DepartmentEntity::getCode, "test"), tenant.getCode());
    }

    @BeforeEach
    @AfterEach
    public void clear() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();

        this.persistence.deleteBy(Conditions.of(RolePrincipalEntity.class).eq(RolePrincipalEntity::getApplicationId, application.getId()), tenant.getCode());
    }

    @Setter(onMethod_ = @Autowired)
    private RolePersistence rolePersistence;

    private RoleEntity getRole() {
        return rolePersistence.findFirstBy(Columns.all(), Conditions.of(RoleEntity.class).eq(RoleEntity::getCode, "test"), null, this.context.getTenant().getCode());
    }

    @Setter(onMethod_ = @Autowired)
    private AccountPersistence accountPersistence;

    private AccountEntity getAccount() {
        return accountPersistence.findFirstBy(Columns.all(), Conditions.of(AccountEntity.class).eq(AccountEntity::getUsername, "test"), null, this.context.getTenant().getCode());
    }

    @Setter(onMethod_ = @Autowired)
    private UnitPersistence unitPersistence;

    private UnitEntity getUnit() {
        return unitPersistence.findFirstBy(Columns.all(), Conditions.of(UnitEntity.class).eq(UnitEntity::getCode, "test"), null, this.context.getTenant().getCode());
    }

    @Setter(onMethod_ = @Autowired)
    private DepartmentPersistence departmentPersistence;

    private DepartmentEntity getDepartment() {
        return departmentPersistence.findFirstBy(Columns.all(), Conditions.of(DepartmentEntity.class).eq(DepartmentEntity::getCode, "test"), null, this.context.getTenant().getCode());
    }

    /**
     * @see RolePrincipalProvider#insert
     * @see RolePrincipalProvider#findById
     * @see RolePrincipalProvider#countBy
     * @see RolePrincipalProvider#deleteByIds
     */
    @Test
    public void case1() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();
        var role = this.getRole();
        var account = this.getAccount();

        var input = RolePrincipalInput.builder()
                .applicationId(application.getId())
                .roleId(role.getId())
                .principalId(account.getId())
                .type(PrincipalType.ACCOUNT.getValue())
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", tenant.getCode());
        assertNotNull(insert);
        assertEquals(input.getApplicationId(), insert.getApplicationId());
        assertEquals(input.getApplicationId(), insert.getApplication().getId());
        assertEquals(input.getRoleId(), insert.getRoleId());
        assertEquals(input.getRoleId(), insert.getRole().getId());
        assertEquals(input.getPrincipalId(), insert.getPrincipalId());
        assertNotNull(insert.getAccount());
        assertEquals(input.getPrincipalId(), insert.getAccount().getId());

        // test findById
        var findById = this.provider.findById(insert.getId(), tenant.getCode());
        assertNotNull(findById);
        assertEquals(insert.getApplicationId(), findById.getApplicationId());
        assertEquals(insert.getApplicationId(), findById.getApplication().getId());
        assertEquals(insert.getRoleId(), findById.getRoleId());
        assertEquals(insert.getRoleId(), findById.getRole().getId());
        assertEquals(insert.getPrincipalId(), findById.getPrincipalId());
        assertNotNull(findById.getAccount());
        assertEquals(insert.getPrincipalId(), findById.getAccount().getId());

        // test countBy
        var count = this.provider.countBy(Conditions.of(RolePrincipal.class).eq(RolePrincipal::getRoleId, role.getId()), tenant.getCode());
        assertEquals(1, count);

        // test deleteByIds
        count = this.provider.deleteByIds(List.of(insert.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(RolePrincipalEntity.class).eq(RolePrincipalEntity::getApplicationId, application.getId()), tenant.getCode());
        assertEquals(0, count);
    }

    /**
     * @see RolePrincipalProvider#insertBatch
     * @see RolePrincipalProvider#findByIds
     * @see RolePrincipalProvider#findBy
     * @see RolePrincipalProvider#pageBy
     * @see RolePrincipalProvider#deleteBy
     */
    @Test
    public void case2() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();
        var role = this.getRole();
        var account = this.getAccount();

        var input = RolePrincipalInput.builder()
                .applicationId(application.getId())
                .roleId(role.getId())
                .principalId(account.getId())
                .type(PrincipalType.ACCOUNT.getValue())
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
        assertEquals(input.getPrincipalId(), insert.getPrincipalId());
        assertNotNull(insert.getAccount());
        assertEquals(input.getPrincipalId(), insert.getAccount().getId());

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), tenant.getCode());
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertEquals(insert.getApplicationId(), fetched.getApplicationId());
        assertEquals(insert.getApplicationId(), fetched.getApplication().getId());
        assertEquals(insert.getRoleId(), fetched.getRoleId());
        assertEquals(insert.getRoleId(), fetched.getRole().getId());
        assertEquals(insert.getPrincipalId(), fetched.getPrincipalId());
        assertNotNull(fetched.getAccount());
        assertEquals(insert.getPrincipalId(), fetched.getAccount().getId());

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(RolePrincipal.class).eq(RolePrincipal::getRoleId, role.getId()), null, tenant.getCode());
        assertNotNull(findBy);
        assertEquals(1, findBy.size());

        fetched = Listx.getFirstOrNull(findBy);
        assertNotNull(fetched);
        assertEquals(insert.getApplicationId(), fetched.getApplicationId());
        assertEquals(insert.getApplicationId(), fetched.getApplication().getId());
        assertEquals(insert.getRoleId(), fetched.getRoleId());
        assertEquals(insert.getRoleId(), fetched.getRole().getId());
        assertEquals(insert.getPrincipalId(), fetched.getPrincipalId());
        assertNotNull(fetched.getAccount());
        assertEquals(insert.getPrincipalId(), fetched.getAccount().getId());

        // test pageBy
        var page = this.provider.pageBy(1, 20, Conditions.of(RolePrincipal.class).eq(RolePrincipal::getRoleId, role.getId()), null, tenant.getCode());
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
        assertEquals(insert.getPrincipalId(), fetched.getPrincipalId());
        assertNotNull(fetched.getAccount());
        assertEquals(insert.getPrincipalId(), fetched.getAccount().getId());

        // test deleteBy
        var count = this.provider.deleteBy(Conditions.of(RolePrincipal.class).eq(RolePrincipal::getRoleId, role.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(RolePrincipalEntity.class).eq(RolePrincipalEntity::getApplicationId, application.getId()), tenant.getCode());
        assertEquals(0, count);
    }
}
