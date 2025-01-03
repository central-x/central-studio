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

package central.studio.provider.graphql.organization;

import central.data.organization.Area;
import central.data.organization.AreaInput;
import central.data.organization.option.AreaType;
import central.provider.graphql.organization.AreaProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.organization.AreaPersistence;
import central.studio.provider.database.persistence.organization.entity.AreaEntity;
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
 * Area Provider Test Cases
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestAreaProvider extends TestProvider {

    @Setter(onMethod_ = @Autowired)
    private AreaProvider provider;

    @Setter(onMethod_ = @Autowired)
    private AreaPersistence persistence;

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
        this.persistence.deleteBy(Conditions.of(AreaEntity.class).like(AreaEntity::getCode, "test%"), tenant.getCode());
    }

    /**
     * @see AreaProvider#insert
     * @see AreaProvider#findById
     * @see AreaProvider#update
     * @see AreaProvider#findByIds
     * @see AreaProvider#countBy
     * @see AreaProvider#deleteByIds
     */
    @Test
    public void case1() {
        var tenant = this.context.getTenant();

        var input = AreaInput.builder()
                .parentId("")
                .code("test")
                .name("测试")
                .type(AreaType.COUNTRY.getValue())
                .order(0)
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", tenant.getCode());
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getParentId(), insert.getParentId());
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getType(), insert.getType());
        assertEquals(input.getOrder(), insert.getOrder());
        var entity = this.persistence.findById(insert.getId(), Columns.all(), tenant.getCode());
        assertNotNull(entity);

        // test findById
        var findById = this.provider.findById(insert.getId(), tenant.getCode());
        assertNotNull(findById);
        assertEquals(insert.getId(), findById.getId());
        assertEquals(insert.getParentId(), findById.getParentId());
        assertEquals(insert.getCode(), findById.getCode());
        assertEquals(insert.getName(), findById.getName());
        assertEquals(insert.getType(), findById.getType());
        assertEquals(insert.getOrder(), findById.getOrder());

        // test countBy
        var count = this.provider.countBy(Conditions.of(Area.class).like(Area::getCode, "test%"), tenant.getCode());
        assertEquals(1, count);

        // test update
        this.provider.update(insert.toInput().code("test2").order(1).build(), "syssa", tenant.getCode());

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), tenant.getCode());
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getParentId(), fetched.getParentId());
        assertEquals("test2", fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getType(), fetched.getType());
        assertEquals(1, fetched.getOrder());
        assertNotEquals(fetched.getCreateDate(), fetched.getModifyDate()); // 修改日期不同

        // test deleteByIds
        count = this.provider.deleteByIds(List.of(insert.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(AreaEntity.class).like(AreaEntity::getCode, "test%"), tenant.getCode());
        assertEquals(0, count);
    }

    /**
     * @see AreaProvider#insertBatch
     * @see AreaProvider#findBy
     * @see AreaProvider#updateBatch
     * @see AreaProvider#pageBy
     * @see AreaProvider#deleteBy
     */
    @Test
    public void case2() {

        var tenant = this.context.getTenant();

        var input_parent = AreaInput.builder()
                .parentId("")
                .code("test_parent")
                .name("测试父节点")
                .type(AreaType.COUNTRY.getValue())
                .order(0)
                .build();

        var input = AreaInput.builder()
                .parentId("")
                .code("test")
                .name("测试省市")
                .type(AreaType.PROVINCE.getValue())
                .order(0)
                .build();

        // test insertBatch
        var insertBatch = this.provider.insertBatch(List.of(input_parent, input), "syssa", tenant.getCode());
        assertNotNull(insertBatch);
        assertEquals(2, insertBatch.size());

        var insert_parent = insertBatch.stream().filter(it -> "test_parent".equals(it.getCode())).findFirst().orElse(null);
        assertNotNull(insert_parent);
        assertNotNull(insert_parent.getId());
        assertEquals(input_parent.getParentId(), insert_parent.getParentId());
        assertEquals(input_parent.getCode(), insert_parent.getCode());
        assertEquals(input_parent.getName(), insert_parent.getName());
        assertEquals(input_parent.getType(), insert_parent.getType());
        assertEquals(input_parent.getOrder(), insert_parent.getOrder());

        var insert = insertBatch.stream().filter(it -> "test".equals(it.getCode())).findFirst().orElse(null);
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getParentId(), insert.getParentId());
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getType(), insert.getType());
        assertEquals(input.getOrder(), insert.getOrder());

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(Area.class).like(Area::getCode, "test%"), null, tenant.getCode());
        assertNotNull(findBy);
        assertEquals(2, findBy.size());

        var fetched_parent = findBy.stream().filter(it -> "test_parent".equals(it.getCode())).findFirst().orElse(null);
        assertNotNull(fetched_parent);
        assertEquals(insert_parent.getId(), fetched_parent.getId());
        assertEquals(insert_parent.getParentId(), fetched_parent.getParentId());
        assertEquals(insert_parent.getCode(), fetched_parent.getCode());
        assertEquals(insert_parent.getName(), fetched_parent.getName());
        assertEquals(insert_parent.getType(), fetched_parent.getType());
        assertEquals(insert_parent.getOrder(), fetched_parent.getOrder());

        var fetched = findBy.stream().filter(it -> "test".equals(it.getCode())).findFirst().orElse(null);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getParentId(), fetched.getParentId());
        assertEquals(insert.getCode(), fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getType(), fetched.getType());
        assertEquals(insert.getOrder(), fetched.getOrder());

        // test updateBatch
        this.provider.updateBatch(List.of(insert.toInput().parentId(insert_parent.getId()).build()), "syssa", tenant.getCode());

        // test pageBy
        var pageBy = this.provider.pageBy(1L, 10L, Conditions.of(Area.class).like(Area::getCode, "test%"), null, tenant.getCode());
        assertNotNull(pageBy);
        assertEquals(1, pageBy.getPager().getPageIndex());
        assertEquals(10, pageBy.getPager().getPageSize());
        assertEquals(1, pageBy.getPager().getPageCount());
        assertEquals(2, pageBy.getPager().getItemCount());
        assertEquals(2, pageBy.getData().size());

        fetched_parent = pageBy.getData().stream().filter(it -> "test_parent".equals(it.getCode())).findFirst().orElse(null);
        assertNotNull(fetched_parent);
        assertEquals(insert_parent.getId(), fetched_parent.getId());
        assertEquals(insert_parent.getParentId(), fetched_parent.getParentId());
        assertEquals(insert_parent.getCode(), fetched_parent.getCode());
        assertEquals(insert_parent.getName(), fetched_parent.getName());
        assertEquals(insert_parent.getType(), fetched_parent.getType());
        assertEquals(insert_parent.getOrder(), fetched_parent.getOrder());

        fetched = pageBy.getData().stream().filter(it -> "test".equals(it.getCode())).findFirst().orElse(null);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert_parent.getId(), fetched.getParentId());
        assertNotNull(fetched.getParent());
        assertEquals(insert_parent.getId(), fetched.getParent().getId());
        assertEquals(insert.getCode(), fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getType(), fetched.getType());
        assertEquals(insert.getOrder(), fetched.getOrder());

        // test deleteBy
        var count = this.provider.deleteBy(Conditions.of(Area.class).like(Area::getCode, "test%"), tenant.getCode());
        assertEquals(2, count);

        count = this.persistence.countBy(Conditions.of(AreaEntity.class).like(AreaEntity::getCode, "test%"), tenant.getCode());
        assertEquals(0, count);
    }
}
