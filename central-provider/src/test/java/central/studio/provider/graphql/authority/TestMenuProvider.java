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

import central.data.authority.Menu;
import central.data.authority.MenuInput;
import central.data.authority.option.MenuType;
import central.provider.graphql.authority.MenuProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.authority.MenuPersistence;
import central.studio.provider.database.persistence.authority.entity.MenuEntity;
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
 * MenuProvider Test Cases
 *
 * @author Alan Yeh
 * @since 2022/09/27
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestMenuProvider extends TestProvider {

    @Setter(onMethod_ = @Autowired)
    private MenuProvider provider;

    @Setter(onMethod_ = @Autowired)
    private MenuPersistence persistence;

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
        this.persistence.deleteBy(Conditions.of(MenuEntity.class).eq(MenuEntity::getApplicationId, application.getId()), tenant.getCode());
    }

    /**
     * @see MenuProvider#insert
     * @see MenuProvider#findById
     * @see MenuProvider#update
     * @see MenuProvider#findByIds
     * @see MenuProvider#countBy
     * @see MenuProvider#deleteByIds
     */
    @Test
    public void case1() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();

        var input = MenuInput.builder()
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
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", tenant.getCode());
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getApplicationId(), insert.getApplicationId());
        assertEquals(input.getApplicationId(), insert.getApplication().getId());
        assertEquals(input.getParentId(), insert.getParentId());
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getUrl(), insert.getUrl());
        assertEquals(input.getType(), insert.getType());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getOrder(), insert.getOrder());
        assertEquals(input.getRemark(), insert.getRemark());
        assertEquals("syssa", insert.getCreatorId());
        assertEquals("syssa", insert.getModifierId());
        var entity = this.persistence.findById(insert.getId(), Columns.all(), tenant.getCode());
        assertNotNull(entity);

        // test findById
        var findById = this.provider.findById(insert.getId(), tenant.getCode());
        assertNotNull(findById);
        assertEquals(insert.getId(), findById.getId());
        assertEquals(insert.getApplicationId(), findById.getApplicationId());
        assertEquals(insert.getApplicationId(), findById.getApplication().getId());
        assertEquals(insert.getParentId(), findById.getParentId());
        assertEquals(insert.getCode(), findById.getCode());
        assertEquals(insert.getName(), findById.getName());
        assertEquals(insert.getUrl(), findById.getUrl());
        assertEquals(insert.getType(), findById.getType());
        assertEquals(insert.getEnabled(), findById.getEnabled());
        assertEquals(insert.getOrder(), findById.getOrder());
        assertEquals(insert.getRemark(), findById.getRemark());
        assertEquals(insert.getCreatorId(), findById.getCreatorId());
        assertEquals(insert.getModifierId(), findById.getModifierId());

        // test countBy
        var count = this.provider.countBy(Conditions.of(Menu.class).eq(Menu::getApplicationId, application.getId()).like(Menu::getCode, "test%"), tenant.getCode());
        assertEquals(1, count);

        // test update
        this.provider.update(insert.toInput().code("test2").enabled(Boolean.FALSE).build(), "syssa", tenant.getCode());

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), tenant.getCode());
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getApplicationId(), fetched.getApplicationId());
        assertEquals(insert.getApplicationId(), fetched.getApplication().getId());
        assertEquals(insert.getParentId(), fetched.getParentId());
        assertEquals("test2", fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getUrl(), fetched.getUrl());
        assertEquals(insert.getType(), fetched.getType());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(insert.getOrder(), fetched.getOrder());
        assertEquals(insert.getRemark(), fetched.getRemark());
        assertEquals(insert.getCreatorId(), fetched.getCreatorId());
        assertEquals(insert.getModifierId(), fetched.getModifierId());
        assertNotEquals(fetched.getCreateDate(), fetched.getModifyDate()); // 修改日期不同

        // test deleteById
        count = this.provider.deleteByIds(List.of(insert.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(MenuEntity.class).eq(MenuEntity::getApplicationId, application.getId()).like(MenuEntity::getCode, "test%"), tenant.getCode());
        assertEquals(0, count);
    }

    /**
     * @see MenuProvider#insertBatch
     * @see MenuProvider#findBy
     * @see MenuProvider#updateBatch
     * @see MenuProvider#pageBy
     * @see MenuProvider#deleteBy
     */
    @Test
    public void case2() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();

        var input_parent = MenuInput.builder()
                .applicationId(application.getId())
                .parentId("")
                .code("test_parent")
                .name("测试父菜单")
                .icon("icon")
                .url("@/parent")
                .type(MenuType.BACKEND.getValue())
                .enabled(Boolean.TRUE)
                .order(0)
                .remark("测试父菜单")
                .build();

        var input = MenuInput.builder()
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
                .build();

        // test insertBatch
        var insertBatch = this.provider.insertBatch(List.of(input_parent, input), "syssa", tenant.getCode());
        assertNotNull(insertBatch);
        assertEquals(2, insertBatch.size());

        assertTrue(insertBatch.stream().anyMatch(it -> it.getCode().equals("test")));
        assertTrue(insertBatch.stream().anyMatch(it -> it.getCode().equals("test_parent")));
        assertTrue(insertBatch.stream().noneMatch(it -> it.getParent() != null));

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(Menu.class).eq(Menu::getApplicationId, application.getId()).like(Menu::getCode, "test%"), null, tenant.getCode());
        assertNotNull(findBy);
        assertEquals(2, findBy.size());
        assertTrue(findBy.stream().anyMatch(it -> it.getCode().equals("test")));
        assertTrue(findBy.stream().anyMatch(it -> it.getCode().equals("test_parent")));
        assertTrue(findBy.stream().noneMatch(it -> it.getParent() != null));

        // test updateBatch
        var parent = findBy.stream().filter(it -> it.getCode().equals("test_parent")).findFirst().orElse(null);
        var child = findBy.stream().filter(it -> it.getCode().equals("test")).findFirst().orElse(null);
        assertNotNull(parent);
        assertNotNull(child);

        this.provider.updateBatch(List.of(child.toInput().parentId(parent.getId()).build()), "syssa", tenant.getCode());

        // test pageBy
        var pageBy = this.provider.pageBy(1L, 10L, Conditions.of(Menu.class).eq(Menu::getApplicationId, application.getId()).like(Menu::getCode, "test%"), null, tenant.getCode());
        assertNotNull(pageBy);
        assertEquals(1, pageBy.getPager().getPageIndex());
        assertEquals(10, pageBy.getPager().getPageSize());
        assertEquals(1, pageBy.getPager().getPageCount());
        assertEquals(2, pageBy.getPager().getItemCount());
        assertEquals(2, pageBy.getData().size());
        parent = pageBy.getData().stream().filter(it -> it.getCode().equals("test_parent")).findFirst().orElse(null);
        child = pageBy.getData().stream().filter(it -> it.getCode().equals("test")).findFirst().orElse(null);
        assertNotNull(parent);
        assertNotNull(child);
        assertNotNull(parent.getChildren());
        assertEquals(1, parent.getChildren().size());
        assertEquals(child.getId(), parent.getChildren().get(0).getId());
        assertNotNull(child.getParent());
        assertEquals(parent.getId(), child.getParent().getId());

        // test deleteBy
        var count = this.provider.deleteBy(Conditions.of(Menu.class).eq(Menu::getApplicationId, application.getId()).like(Menu::getCode, "test%"), tenant.getCode());
        assertEquals(2, count);

        count = this.persistence.countBy(Conditions.of(MenuEntity.class).eq(MenuEntity::getApplicationId, application.getId()).like(MenuEntity::getCode, "test%"), tenant.getCode());
        assertEquals(0, count);
    }
}
