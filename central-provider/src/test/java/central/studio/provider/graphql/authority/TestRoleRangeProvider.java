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
import central.data.authority.option.RangeCategory;
import central.data.authority.option.RangeType;
import central.data.organization.AccountInput;
import central.provider.graphql.authority.RoleRangeProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.authority.MenuPersistence;
import central.studio.provider.database.persistence.authority.PermissionPersistence;
import central.studio.provider.database.persistence.authority.RolePersistence;
import central.studio.provider.database.persistence.authority.RoleRangePersistence;
import central.studio.provider.database.persistence.authority.entity.*;
import central.studio.provider.database.persistence.organization.AccountPersistence;
import central.studio.provider.database.persistence.organization.entity.AccountEntity;
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
 * Role Range Provider Test Cases
 *
 * @author Alan Yeh
 * @since 2024/12/15
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestRoleRangeProvider {

    @Setter(onMethod_ = @Autowired)
    private RoleRangeProvider provider;

    @Setter(onMethod_ = @Autowired)
    private RoleRangePersistence persistence;

    @Setter(onMethod_ = @Autowired)
    private TestContext context;

    @BeforeAll
    public static void setup(@Autowired DataContext context,
                             @Autowired TestContext testContext,
                             @Autowired MenuPersistence menuPersistence,
                             @Autowired PermissionPersistence permissionPersistence,
                             @Autowired RolePersistence rolePersistence,
                             @Autowired AccountPersistence accountPersistence) throws Exception {
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
                               @Autowired PermissionPersistence permissionPersistence,
                               @Autowired RolePersistence rolePersistence,
                               @Autowired AccountPersistence accountPersistence) {
        var tenant = testContext.getTenant();
        var application = testContext.getApplication();

        menuPersistence.deleteBy(Conditions.of(MenuEntity.class).eq(MenuEntity::getApplicationId, application.getId()), tenant.getCode());
        permissionPersistence.deleteBy(Conditions.of(PermissionEntity.class).eq(PermissionEntity::getApplicationId, application.getId()), tenant.getCode());
        rolePersistence.deleteBy(Conditions.of(RoleEntity.class).eq(RoleEntity::getApplicationId, application.getId()), tenant.getCode());
        accountPersistence.deleteBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getUsername, "test"), tenant.getCode());
    }

    @AfterEach
    public void clear() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();

        this.persistence.deleteBy(Conditions.of(RoleRangeEntity.class).eq(RoleRangeEntity::getApplicationId, application.getId()), tenant.getCode());
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

    /**
     * @see RoleRangeProvider#insert
     * @see RoleRangeProvider#findById
     * @see RoleRangeProvider#countBy
     * @see RoleRangeProvider#deleteByIds
     */
    @Test
    public void case1() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();
        var role = this.getRole();
        var account = this.getAccount();

        var input = RoleRangeInput.builder()
                .applicationId(application.getId())
                .roleId(role.getId())
                .category(RangeCategory.ORGANIZATION.getValue())
                .type(RangeType.ACCOUNT.getValue())
                .dataId(account.getId())
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", tenant.getCode());
        assertNotNull(insert);
        assertEquals(input.getApplicationId(), insert.getApplicationId());
        assertEquals(input.getApplicationId(), insert.getApplication().getId());
        assertEquals(input.getRoleId(), insert.getRoleId());
        assertEquals(input.getRoleId(), insert.getRole().getId());
        assertEquals(input.getCategory(), insert.getCategory());
        assertEquals(input.getType(), insert.getType());
        assertEquals(input.getDataId(), insert.getDataId());

        // test findById
        var findById = this.provider.findById(insert.getId(), tenant.getCode());
        assertNotNull(findById);
        assertEquals(insert.getApplicationId(), findById.getApplicationId());
        assertEquals(insert.getApplicationId(), findById.getApplication().getId());
        assertEquals(insert.getRoleId(), findById.getRoleId());
        assertEquals(insert.getRoleId(), findById.getRole().getId());
        assertEquals(insert.getCategory(), findById.getCategory());
        assertEquals(insert.getType(), findById.getType());
        assertEquals(insert.getDataId(), findById.getDataId());

        // test countBy
        var count = this.provider.countBy(Conditions.of(RoleRange.class).eq(RoleRange::getRoleId, role.getId()), tenant.getCode());
        assertEquals(1, count);

        // test deleteByIds
        count = this.provider.deleteByIds(List.of(insert.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(RoleRangeEntity.class).eq(RoleRangeEntity::getApplicationId, application.getId()), tenant.getCode());
        assertEquals(0, count);
    }

    /**
     * @see RoleRangeProvider#insertBatch
     * @see RoleRangeProvider#findByIds
     * @see RoleRangeProvider#findBy
     * @see RoleRangeProvider#pageBy
     * @see RoleRangeProvider#deleteBy
     */
    @Test
    public void case2() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();
        var role = this.getRole();
        var account = this.getAccount();

        var input = RoleRangeInput.builder()
                .applicationId(application.getId())
                .roleId(role.getId())
                .category(RangeCategory.ORGANIZATION.getValue())
                .type(RangeType.ACCOUNT.getValue())
                .dataId(account.getId())
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
        assertEquals(input.getCategory(), insert.getCategory());
        assertEquals(input.getType(), insert.getType());
        assertEquals(input.getDataId(), insert.getDataId());

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
        assertEquals(insert.getCategory(), fetched.getCategory());
        assertEquals(insert.getType(), fetched.getType());
        assertEquals(insert.getDataId(), fetched.getDataId());

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(RoleRange.class).eq(RoleRange::getRoleId, role.getId()), null, tenant.getCode());
        assertNotNull(findBy);
        assertEquals(1, findBy.size());

        fetched = Listx.getFirstOrNull(findBy);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getApplicationId(), fetched.getApplicationId());
        assertEquals(insert.getApplicationId(), fetched.getApplication().getId());
        assertEquals(insert.getRoleId(), fetched.getRoleId());
        assertEquals(insert.getRoleId(), fetched.getRole().getId());
        assertEquals(insert.getCategory(), fetched.getCategory());
        assertEquals(insert.getType(), fetched.getType());
        assertEquals(insert.getDataId(), fetched.getDataId());

        // test pageBy
        var pageBy = this.provider.pageBy(1, 20, Conditions.of(RoleRange.class).eq(RoleRange::getRoleId, role.getId()), null, tenant.getCode());
        assertNotNull(pageBy);
        assertEquals(1, pageBy.getPager().getPageIndex());
        assertEquals(20, pageBy.getPager().getPageSize());
        assertEquals(1, pageBy.getPager().getPageCount());
        assertEquals(1, pageBy.getPager().getItemCount());

        fetched = Listx.getFirstOrNull(pageBy.getData());
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getApplicationId(), fetched.getApplicationId());
        assertEquals(insert.getApplicationId(), fetched.getApplication().getId());
        assertEquals(insert.getRoleId(), fetched.getRoleId());
        assertEquals(insert.getRoleId(), fetched.getRole().getId());
        assertEquals(insert.getCategory(), fetched.getCategory());
        assertEquals(insert.getType(), fetched.getType());
        assertEquals(insert.getDataId(), fetched.getDataId());

        // test deleteBy
        var count = this.provider.deleteBy(Conditions.of(RoleRange.class).eq(RoleRange::getRoleId, role.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(RoleRangeEntity.class).eq(RoleRangeEntity::getApplicationId, application.getId()), tenant.getCode());
        assertEquals(0, count);
    }
}
