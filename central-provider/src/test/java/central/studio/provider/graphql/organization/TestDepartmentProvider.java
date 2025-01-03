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

import central.data.organization.*;
import central.data.organization.option.AreaType;
import central.provider.graphql.organization.DepartmentProvider;
import central.provider.graphql.organization.UnitProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.organization.AreaPersistence;
import central.studio.provider.database.persistence.organization.DepartmentPersistence;
import central.studio.provider.database.persistence.organization.UnitPersistence;
import central.studio.provider.database.persistence.organization.entity.AreaEntity;
import central.studio.provider.database.persistence.organization.entity.DepartmentEntity;
import central.studio.provider.database.persistence.organization.entity.UnitEntity;
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
 * Department Provider Test Cases
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestDepartmentProvider extends TestProvider {

    @Setter(onMethod_ = @Autowired)
    private DepartmentProvider provider;

    @Setter(onMethod_ = @Autowired)
    private DepartmentPersistence persistence;

    @Setter(onMethod_ = @Autowired)
    private AreaPersistence areaPersistence;

    @Setter(onMethod_ = @Autowired)
    private UnitPersistence unitPersistence;

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
        // 清空数据
        var tenant = this.context.getTenant();
        this.persistence.deleteBy(Conditions.of(DepartmentEntity.class).like(DepartmentEntity::getCode, "test%"), tenant.getCode());
        this.unitPersistence.deleteBy(Conditions.of(UnitEntity.class).like(UnitEntity::getCode, "test%"), tenant.getCode());
        this.areaPersistence.deleteBy(Conditions.of(AreaEntity.class).like(AreaEntity::getCode, "test%"), tenant.getCode());
    }

    /**
     * @see UnitProvider#insert
     * @see UnitProvider#findById
     * @see UnitProvider#update
     * @see UnitProvider#findByIds
     * @see UnitProvider#countBy
     * @see UnitProvider#deleteByIds
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

        var input = DepartmentInput.builder()
                .parentId("")
                .unitId(unit.getId())
                .code("test")
                .name("测试部门")
                .order(0)
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", tenant.getCode());
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getParentId(), insert.getParentId());
        assertEquals(input.getUnitId(), insert.getUnitId());
        assertNotNull(insert.getUnit());
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
        assertEquals(insert.getParentId(), findById.getParentId());
        assertEquals(insert.getUnitId(), findById.getUnitId());
        assertNotNull(findById.getUnit());
        assertEquals(insert.getUnitId(), findById.getUnit().getId());
        assertEquals(insert.getCode(), findById.getCode());
        assertEquals(insert.getName(), findById.getName());
        assertEquals(insert.getOrder(), findById.getOrder());

        // test countBy
        var count = this.provider.countBy(Conditions.of(Department.class).like(Department::getCode, "test%"), tenant.getCode());
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
        assertEquals(insert.getUnitId(), fetched.getUnitId());
        assertNotNull(fetched.getUnit());
        assertEquals(insert.getUnitId(), fetched.getUnit().getId());
        assertEquals("test2", fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(1, fetched.getOrder());
        assertNotEquals(fetched.getCreateDate(), fetched.getModifyDate()); // 修改日期不同

        // test deleteByIds
        count = this.provider.deleteByIds(List.of(insert.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(DepartmentEntity.class).like(DepartmentEntity::getCode, "test%"), tenant.getCode());
        assertEquals(0, count);
    }

    /**
     * @see DepartmentProvider#insertBatch
     * @see DepartmentProvider#findBy
     * @see DepartmentProvider#updateBatch
     * @see DepartmentProvider#pageBy
     * @see DepartmentProvider#deleteBy
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

        var input_parent = DepartmentInput.builder()
                .parentId("")
                .unitId(unit.getId())
                .code("test_parent")
                .name("测试父部门")
                .order(0)
                .build();

        var input = DepartmentInput.builder()
                .parentId("")
                .unitId(unit.getId())
                .code("test")
                .name("测试部门")
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
        assertNull(insert_parent.getParent());
        assertEquals(input_parent.getCode(), insert_parent.getCode());
        assertEquals(input_parent.getName(), insert_parent.getName());
        assertEquals(input_parent.getOrder(), insert_parent.getOrder());

        var insert = insertBatch.stream().filter(it -> "test".equals(it.getCode())).findFirst().orElse(null);
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getParentId(), insert.getParentId());
        assertNull(insert.getParent());
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getOrder(), insert.getOrder());

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(Department.class).like(Department::getCode, "test%"), null, tenant.getCode());
        assertNotNull(findBy);
        assertEquals(2, findBy.size());

        var fetched_parent = findBy.stream().filter(it -> "test_parent".equals(it.getCode())).findFirst().orElse(null);
        assertNotNull(fetched_parent);
        assertEquals(insert_parent.getId(), fetched_parent.getId());
        assertEquals(insert_parent.getParentId(), fetched_parent.getParentId());
        assertEquals(insert_parent.getCode(), fetched_parent.getCode());
        assertEquals(insert_parent.getName(), fetched_parent.getName());
        assertEquals(insert_parent.getOrder(), fetched_parent.getOrder());

        var fetched = findBy.stream().filter(it -> "test".equals(it.getCode())).findFirst().orElse(null);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getParentId(), fetched.getParentId());
        assertEquals(insert.getCode(), fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getOrder(), fetched.getOrder());

        // test updateBatch
        this.provider.updateBatch(List.of(insert.toInput().parentId(insert_parent.getId()).order(1).build()), "syssa", tenant.getCode());

        // test pageBy
        var pageBy = this.provider.pageBy(1L, 10L, Conditions.of(Department.class).like(Department::getCode, "test%"), null, tenant.getCode());
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
        assertEquals(insert_parent.getOrder(), fetched_parent.getOrder());

        fetched = pageBy.getData().stream().filter(it -> "test".equals(it.getCode())).findFirst().orElse(null);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert_parent.getId(), fetched.getParentId());
        assertNotNull(fetched.getParent());
        assertEquals(insert_parent.getId(), fetched.getParent().getId());
        assertEquals(insert.getCode(), fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(1, fetched.getOrder());

        // test deleteBy
        var count = this.provider.deleteBy(Conditions.of(Department.class).like(Department::getCode, "test%"), tenant.getCode());
        assertEquals(2, count);

        count = this.persistence.countBy(Conditions.of(DepartmentEntity.class).like(DepartmentEntity::getCode, "test%"), tenant.getCode());
        assertEquals(0, count);
    }
}
