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
import central.data.authority.Permission;
import central.data.authority.PermissionInput;
import central.data.authority.option.MenuType;
import central.provider.graphql.authority.PermissionProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.authority.MenuPersistence;
import central.studio.provider.database.persistence.authority.PermissionPersistence;
import central.studio.provider.database.persistence.authority.entity.MenuEntity;
import central.studio.provider.database.persistence.authority.entity.PermissionEntity;
import central.studio.provider.graphql.TestContext;
import central.studio.provider.graphql.TestProvider;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Permission Provider Test Cases
 * <p>
 * 权限测试
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestPermissionProvider extends TestProvider {

    @Setter(onMethod_ = @Autowired)
    private PermissionProvider provider;

    @Setter(onMethod_ = @Autowired)
    private PermissionPersistence persistence;

    @Setter(onMethod_ = @Autowired)
    private MenuPersistence menuPersistence;

    @Setter(onMethod_ = @Autowired)
    private TestContext context;

    @BeforeAll
    public static void setup(@Autowired DataContext context) throws Exception {
        SaasContainer container = null;
        while (container == null || container.getApplications().isEmpty()) {
            Thread.sleep(100);
            container = context.getData(DataFetcherType.SAAS);
        }
    }

    @AfterEach
    public void clear() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();

        this.persistence.deleteBy(Conditions.of(PermissionEntity.class).eq(PermissionEntity::getApplicationId, application.getId()), tenant.getCode());
        this.menuPersistence.deleteBy(Conditions.of(MenuEntity.class).eq(MenuEntity::getApplicationId, application.getId()), tenant.getCode());
    }

    /**
     * @see PermissionProvider#insert
     * @see PermissionProvider#findById
     * @see PermissionProvider#update
     * @see PermissionProvider#findByIds
     * @see PermissionProvider#countBy
     * @see PermissionProvider#deleteByIds
     */
    @Test
    public void case1() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();

        var menu = this.menuPersistence.insert(MenuInput.builder()
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

        var input = PermissionInput.builder()
                .applicationId(application.getId())
                .menuId(menu.getId())
                .code("add")
                .name("添加")
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", tenant.getCode());
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getApplicationId(), insert.getApplicationId());
        assertEquals(input.getApplicationId(), insert.getApplication().getId());
        assertEquals(input.getMenuId(), insert.getMenuId());
        assertEquals(input.getMenuId(), insert.getMenu().getId());
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals("syssa", insert.getCreatorId());
        assertEquals("syssa", insert.getModifierId());
        var entity = this.persistence.findById(insert.getId(), Columns.all(), tenant.getCode());
        assertNotNull(entity);

        // test findById
        var findById = this.provider.findById(insert.getId(), tenant.getCode());
        assertNotNull(findById);
        assertEquals(insert.getId(), findById.getId());
        assertEquals(insert.getApplicationId(), findById.getApplicationId());
        assertEquals(insert.getApplication().getId(), findById.getApplication().getId());
        assertEquals(insert.getMenuId(), findById.getMenuId());
        assertEquals(insert.getMenu().getId(), findById.getMenu().getId());
        assertEquals(insert.getCode(), findById.getCode());
        assertEquals(insert.getName(), findById.getName());
        assertEquals(insert.getCreatorId(), findById.getCreatorId());
        assertEquals(insert.getModifierId(), findById.getModifierId());

        // test countBy
        var count = this.provider.countBy(Conditions.of(Permission.class).eq(Permission::getApplicationId, application.getId()), tenant.getCode());
        assertEquals(1, count);

        // test update
        this.provider.update(insert.toInput().code("add2").name("添加2").build(), "syssa", tenant.getCode());

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), tenant.getCode());
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getApplicationId(), fetched.getApplicationId());
        assertEquals(insert.getApplication().getId(), fetched.getApplication().getId());
        assertEquals(insert.getMenuId(), fetched.getMenuId());
        assertEquals(insert.getMenu().getId(), fetched.getMenu().getId());
        assertEquals("add2", fetched.getCode());
        assertEquals("添加2", fetched.getName());
        assertEquals(insert.getCreatorId(), fetched.getCreatorId());
        assertEquals(insert.getModifierId(), fetched.getModifierId());
        assertNotEquals(fetched.getCreateDate(), fetched.getModifyDate()); // 修改日期不同

        // test deleteById
        count = this.provider.deleteByIds(List.of(insert.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(PermissionEntity.class).eq(PermissionEntity::getApplicationId, application.getId()), tenant.getCode());
        assertEquals(0, count);
    }

    /**
     * @see PermissionProvider#insertBatch
     * @see PermissionProvider#findBy
     * @see PermissionProvider#updateBatch
     * @see PermissionProvider#pageBy
     * @see PermissionProvider#deleteBy
     */
    @Test
    public void case2() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();

        var menu = this.menuPersistence.insert(MenuInput.builder()
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

        var input = PermissionInput.builder()
                .applicationId(application.getId())
                .menuId(menu.getId())
                .code("add")
                .name("添加")
                .build();

        // test insertBatch
        var insertBatch = this.provider.insertBatch(List.of(input), "syssa", tenant.getCode());
        assertNotNull(insertBatch);
        assertEquals(1, insertBatch.size());

        var insert = Listx.getFirstOrNull(insertBatch);
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getApplicationId(), insert.getApplicationId());
        assertEquals(input.getApplicationId(), insert.getApplication().getId());
        assertEquals(input.getMenuId(), insert.getMenuId());
        assertEquals(input.getMenuId(), insert.getMenu().getId());
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals("syssa", insert.getCreatorId());
        assertEquals("syssa", insert.getModifierId());
        var entity = this.persistence.findById(insert.getId(), Columns.all(), tenant.getCode());
        assertNotNull(entity);

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(Permission.class).eq(Permission::getApplicationId, application.getId()), null, tenant.getCode());
        assertNotNull(findBy);
        assertEquals(1, findBy.size());

        var fetched = Listx.getFirstOrNull(findBy);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getApplicationId(), fetched.getApplicationId());
        assertEquals(insert.getApplication().getId(), fetched.getApplication().getId());
        assertEquals(insert.getMenuId(), fetched.getMenuId());
        assertEquals(insert.getMenu().getId(), fetched.getMenu().getId());
        assertEquals(insert.getCode(), fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getCreatorId(), fetched.getCreatorId());
        assertEquals(insert.getModifierId(), fetched.getModifierId());

        // test updateBatch
        this.provider.updateBatch(List.of(insert.toInput().code("add2").name("添加2").build()), "syssa", tenant.getCode());

        // test pageBy
        var pageBy = this.provider.pageBy(1, 10, Conditions.of(Permission.class).eq(Permission::getApplicationId, application.getId()), null, tenant.getCode());
        assertNotNull(pageBy);
        assertEquals(1, pageBy.getPager().getPageIndex());
        assertEquals(10, pageBy.getPager().getPageSize());
        assertEquals(1, pageBy.getPager().getPageCount());
        assertEquals(1, pageBy.getPager().getItemCount());
        assertEquals(1, pageBy.getData().size());

        fetched = Listx.getFirstOrNull(pageBy.getData());
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getApplicationId(), fetched.getApplicationId());
        assertEquals(insert.getApplication().getId(), fetched.getApplication().getId());
        assertEquals(insert.getMenuId(), fetched.getMenuId());
        assertEquals(insert.getMenu().getId(), fetched.getMenu().getId());
        assertEquals("add2", fetched.getCode());
        assertEquals("添加2", fetched.getName());
        assertEquals(insert.getCreatorId(), fetched.getCreatorId());
        assertEquals(insert.getModifierId(), fetched.getModifierId());
        assertNotEquals(fetched.getCreateDate(), fetched.getModifyDate());

        // test deleteBy
        var count = this.provider.deleteBy(Conditions.of(Permission.class).eq(Permission::getApplicationId, application.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(PermissionEntity.class).eq(PermissionEntity::getApplicationId, application.getId()), tenant.getCode());
        assertEquals(0, count);
    }
}
