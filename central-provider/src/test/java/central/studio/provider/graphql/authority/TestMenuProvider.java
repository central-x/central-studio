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
import lombok.Setter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    public void clear() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();
        this.persistence.deleteBy(Conditions.of(MenuEntity.class).eq(MenuEntity::getApplicationId, application.getId()), tenant.getCode());
    }

    /**
     * @see MenuProvider#insert
     * @see MenuProvider#findById
     * @see MenuProvider#update
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
        var data = this.provider.insert(input, "syssa", tenant.getCode());
        assertNotNull(data);
        assertNotNull(data.getId());
        assertEquals(input.getApplicationId(), data.getApplicationId());
        assertEquals(input.getApplicationId(), data.getApplication().getId());
        assertEquals(input.getParentId(), data.getParentId());
        assertEquals(input.getCode(), data.getCode());
        assertEquals(input.getName(), data.getName());
        assertEquals(input.getUrl(), data.getUrl());
        assertEquals(input.getType(), data.getType());
        assertEquals(input.getEnabled(), data.getEnabled());
        assertEquals(input.getOrder(), data.getOrder());
        assertEquals(input.getRemark(), data.getRemark());
        var entity = this.persistence.findById(data.getId(), Columns.all(), tenant.getCode());
        assertNotNull(entity);

        // test findById
        var fetched = this.provider.findById(data.getId(), tenant.getCode());
        assertEquals(data.getId(), fetched.getId());
        assertEquals(data.getApplicationId(), fetched.getApplicationId());
        assertEquals(data.getApplicationId(), fetched.getApplication().getId());
        assertEquals(data.getParentId(), fetched.getParentId());
        assertEquals(data.getCode(), fetched.getCode());
        assertEquals(data.getName(), fetched.getName());
        assertEquals(data.getUrl(), fetched.getUrl());
        assertEquals(data.getType(), fetched.getType());
        assertEquals(data.getEnabled(), fetched.getEnabled());
        assertEquals(data.getOrder(), fetched.getOrder());
        assertEquals(data.getRemark(), fetched.getRemark());

        // test update
        this.provider.update(fetched.toInput().code("test2").enabled(Boolean.FALSE).build(), "syssa", tenant.getCode());
        fetched = this.provider.findById(data.getId(), tenant.getCode());
        assertEquals(data.getId(), fetched.getId());
        assertEquals(data.getApplicationId(), fetched.getApplicationId());
        assertEquals(data.getApplicationId(), fetched.getApplication().getId());
        assertEquals(data.getParentId(), fetched.getParentId());
        assertEquals("test2", fetched.getCode());
        assertEquals(data.getName(), fetched.getName());
        assertEquals(data.getUrl(), fetched.getUrl());
        assertEquals(data.getType(), fetched.getType());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(data.getOrder(), fetched.getOrder());
        assertEquals(data.getRemark(), fetched.getRemark());

        // test deleteById
        this.provider.deleteByIds(List.of(data.getId()), tenant.getCode());
        entity = this.persistence.findById(data.getId(), Columns.all(), tenant.getCode());
        assertNull(entity);
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
        var data = this.provider.insertBatch(List.of(input_parent, input), "syssa", tenant.getCode());
        assertNotNull(data);
        assertEquals(2, data.size());
        assertTrue(data.stream().anyMatch(it -> it.getCode().equals("test")));
        assertTrue(data.stream().anyMatch(it -> it.getCode().equals("test_parent")));
        assertTrue(data.stream().noneMatch(it -> it.getParent() != null));
        var count = this.persistence.countBy(Conditions.of(MenuEntity.class).eq(MenuEntity::getApplicationId, application.getId()), tenant.getCode());
        assertEquals(2, count);

        // test findBy
        var fetched = this.provider.findBy(null, null, Conditions.of(Menu.class).eq(Menu::getApplicationId, application.getId()), null, tenant.getCode());
        assertNotNull(fetched);
        assertEquals(2, fetched.size());
        assertTrue(fetched.stream().anyMatch(it -> it.getCode().equals("test")));
        assertTrue(fetched.stream().anyMatch(it -> it.getCode().equals("test_parent")));
        assertTrue(fetched.stream().noneMatch(it -> it.getParent() != null));

        // test updateBatch
        var parent = fetched.stream().filter(it -> it.getCode().equals("test_parent")).findFirst().orElse(null);
        var child = fetched.stream().filter(it -> it.getCode().equals("test")).findFirst().orElse(null);
        assertNotNull(parent);
        assertNotNull(child);

        this.provider.updateBatch(List.of(child.toInput().parentId(parent.getId()).build()), "syssa", tenant.getCode());
        var page = this.provider.pageBy(1L, 10L, Conditions.of(Menu.class).eq(Menu::getApplicationId, application.getId()), null, tenant.getCode());
        assertNotNull(page);
        assertEquals(1, page.getPager().getPageIndex());
        assertEquals(10, page.getPager().getPageSize());
        assertEquals(1, page.getPager().getPageCount());
        assertEquals(2, page.getPager().getItemCount());
        assertEquals(2, page.getData().size());
        parent = page.getData().stream().filter(it -> it.getCode().equals("test_parent")).findFirst().orElse(null);
        child = page.getData().stream().filter(it -> it.getCode().equals("test")).findFirst().orElse(null);
        assertNotNull(parent);
        assertNotNull(child);
        assertNotNull(parent.getChildren());
        assertEquals(1, parent.getChildren().size());
        assertEquals(child.getId(), parent.getChildren().get(0).getId());
        assertNotNull(child.getParent());
        assertEquals(parent.getId(), child.getParent().getId());

        // test deleteBy
        this.provider.deleteBy(Conditions.of(Menu.class).eq(Menu::getApplicationId, application.getId()), tenant.getCode());
        count = this.persistence.countBy(Conditions.of(MenuEntity.class).eq(MenuEntity::getApplicationId, application.getId()), tenant.getCode());
        assertEquals(0, count);
    }
}
