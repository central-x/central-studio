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

import central.data.organization.AreaInput;
import central.data.organization.Post;
import central.data.organization.PostInput;
import central.data.organization.UnitInput;
import central.data.organization.option.AreaType;
import central.provider.graphql.organization.PostProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.organization.AreaPersistence;
import central.studio.provider.database.persistence.organization.PostPersistence;
import central.studio.provider.database.persistence.organization.UnitPersistence;
import central.studio.provider.database.persistence.organization.entity.AreaEntity;
import central.studio.provider.database.persistence.organization.entity.PostEntity;
import central.studio.provider.database.persistence.organization.entity.UnitEntity;
import central.studio.provider.graphql.TestContext;
import central.studio.provider.graphql.TestProvider;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Post Provider Test Cases
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestPostProvider extends TestProvider {

    @Setter(onMethod_ = @Autowired)
    private PostProvider provider;

    @Setter(onMethod_ = @Autowired)
    private PostPersistence persistence;

    @Setter(onMethod_ = @Autowired)
    private UnitPersistence unitPersistence;

    @Setter(onMethod_ = @Autowired)
    private AreaPersistence areaPersistence;

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
    @AfterEach
    public void clear() {
        // 清空数据
        var tenant = this.context.getTenant();

        this.persistence.deleteBy(Conditions.of(PostEntity.class).like(PostEntity::getCode, "test%"), tenant.getCode());
        this.unitPersistence.deleteBy(Conditions.of(UnitEntity.class).like(UnitEntity::getCode, "test%"), tenant.getCode());
        this.areaPersistence.deleteBy(Conditions.of(AreaEntity.class).like(AreaEntity::getCode, "test%"), tenant.getCode());
    }

    /**
     * @see PostProvider#insert
     * @see PostProvider#findById
     * @see PostProvider#update
     * @see PostProvider#findByIds
     * @see PostProvider#countBy
     * @see PostProvider#deleteByIds
     */
    @Test
    public void case1() {
        var tenant = this.context.getTenant();

        var area = this.areaPersistence.insert(AreaInput.builder()
                .parentId("")
                .code("test")
                .name("测试")
                .type(AreaType.COUNTRY.getValue())
                .order(0)
                .build(), "syssa", tenant.getCode());

        var unit = this.unitPersistence.insert(UnitInput.builder()
                .parentId("")
                .areaId(area.getId())
                .code("test")
                .name("测试单位")
                .order(0)
                .build(), "syssa", tenant.getCode());

        var input = PostInput.builder()
                .unitId(unit.getId())
                .code("test")
                .name("测试职务")
                .order(0)
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", tenant.getCode());
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getUnitId(), insert.getUnitId());
        assertEquals(input.getUnitId(), insert.getUnit().getId());
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getOrder(), insert.getOrder());

        var entity = this.persistence.findById(insert.getId(), Columns.all(), tenant.getCode());
        assertNotNull(entity);

        // test findById
        var findById = this.provider.findById(insert.getId(), tenant.getCode());
        assertNotNull(findById);
        assertEquals(insert.getId(), findById.getId());
        assertEquals(insert.getUnitId(), findById.getUnitId());
        assertEquals(insert.getUnitId(), findById.getUnit().getId());
        assertEquals(insert.getCode(), findById.getCode());
        assertEquals(insert.getName(), findById.getName());
        assertEquals(insert.getOrder(), findById.getOrder());

        // test countBy
        var count = this.provider.countBy(Conditions.of(Post.class).eq(Post::getUnitId, unit.getId()), tenant.getCode());
        assertEquals(1, count);

        // test update
        this.provider.update(insert.toInput().code("test2").order(1).build(), "syssa", tenant.getCode());

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), tenant.getCode());
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertEquals(input.getId(), fetched.getId());
        assertEquals(insert.getUnitId(), fetched.getUnitId());
        assertEquals(insert.getUnitId(), fetched.getUnit().getId());
        assertEquals("test2", fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(1, fetched.getOrder());

        // test deleteById
        count = this.provider.deleteByIds(List.of(insert.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(PostEntity.class).eq(PostEntity::getUnitId, unit.getId()), tenant.getCode());
        assertEquals(0, count);
    }

    /**
     * @see PostProvider#insertBatch
     * @see PostProvider#findBy
     * @see PostProvider#updateBatch
     * @see PostProvider#pageBy
     * @see PostProvider#deleteBy
     */
    @Test
    public void case2() {
        var tenant = this.context.getTenant();

        var area = this.areaPersistence.insert(AreaInput.builder()
                .parentId("")
                .code("test")
                .name("测试")
                .type(AreaType.COUNTRY.getValue())
                .order(0)
                .build(), "syssa", tenant.getCode());

        var unit = this.unitPersistence.insert(UnitInput.builder()
                .parentId("")
                .areaId(area.getId())
                .code("test")
                .name("测试单位")
                .order(0)
                .build(), "syssa", tenant.getCode());

        var input = PostInput.builder()
                .unitId(unit.getId())
                .code("test")
                .name("测试职务")
                .order(0)
                .build();

        // test insertBatch
        var insertBatch = this.provider.insertBatch(List.of(input), "syssa", tenant.getCode());
        assertNotNull(insertBatch);
        assertEquals(1, insertBatch.size());

        var insert = Listx.getFirstOrNull(insertBatch);
        assertNotNull(insert);
        assertEquals(input.getUnitId(), insert.getUnitId());
        assertEquals(input.getUnitId(), insert.getUnit().getId());
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getOrder(), insert.getOrder());

        var entity = this.persistence.findById(insert.getId(), Columns.all(), tenant.getCode());
        assertNotNull(entity);

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(Post.class).eq(Post::getUnitId, unit.getId()), null, tenant.getCode());
        assertNotNull(findBy);
        assertEquals(1, findBy.size());

        var fetched = Listx.getFirstOrNull(findBy);
        assertNotNull(fetched);
        assertEquals(insert.getUnitId(), fetched.getUnitId());
        assertEquals(insert.getUnitId(), fetched.getUnit().getId());
        assertEquals(insert.getCode(), fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getOrder(), fetched.getOrder());

        // test updateBatch
        this.provider.updateBatch(List.of(fetched.toInput().code("test2").order(1).build()), "syssa", tenant.getCode());

        // test pageBy
        var pageBy = this.provider.pageBy(1, 10, Conditions.of(Post.class).eq(Post::getUnitId, unit.getId()), null, tenant.getCode());
        assertNotNull(pageBy);
        assertEquals(1, pageBy.getPager().getPageIndex());
        assertEquals(10, pageBy.getPager().getPageSize());
        assertEquals(1, pageBy.getPager().getPageCount());
        assertEquals(1, pageBy.getPager().getItemCount());
        assertEquals(1, pageBy.getData().size());

        fetched = Listx.getFirstOrNull(pageBy.getData());
        assertNotNull(fetched);
        assertEquals(insert.getUnitId(), fetched.getUnitId());
        assertEquals(insert.getUnitId(), fetched.getUnit().getId());
        assertEquals("test2", fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(1, fetched.getOrder());

        // test deleteBy
        var count = this.provider.deleteBy(Conditions.of(Post.class).eq(Post::getUnitId, unit.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(PostEntity.class).eq(PostEntity::getUnitId, unit.getId()), tenant.getCode());
        assertEquals(0, count);
    }
}
