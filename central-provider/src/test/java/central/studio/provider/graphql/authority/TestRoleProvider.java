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

import central.data.authority.Role;
import central.data.authority.RoleInput;
import central.data.organization.AreaInput;
import central.data.organization.UnitInput;
import central.data.organization.option.AreaType;
import central.provider.graphql.authority.RoleProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.authority.RolePersistence;
import central.studio.provider.database.persistence.authority.entity.RoleEntity;
import central.studio.provider.database.persistence.organization.AreaPersistence;
import central.studio.provider.database.persistence.organization.UnitPersistence;
import central.studio.provider.database.persistence.organization.entity.AreaEntity;
import central.studio.provider.database.persistence.organization.entity.UnitEntity;
import central.studio.provider.graphql.TestContext;
import central.studio.provider.graphql.TestProvider;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Role Provider Test Cases
 * 角色
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestRoleProvider extends TestProvider {

    @Setter(onMethod_ = @Autowired)
    private RoleProvider provider;

    @Setter(onMethod_ = @Autowired)
    private RolePersistence persistence;

    @Setter(onMethod_ = @Autowired)
    private TestContext context;

    @BeforeAll
    public static void setup(@Autowired DataContext context,
                             @Autowired TestContext testContext,
                             @Autowired UnitPersistence unitPersistence,
                             @Autowired AreaPersistence areaPersistence) throws Exception {
        SaasContainer container = null;
        while (container == null || container.getApplications().isEmpty()) {
            Thread.sleep(100);
            container = context.getData(DataFetcherType.SAAS);
        }

        var tenant = testContext.getTenant();
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
    }

    @AfterAll
    public static void cleanup(@Autowired TestContext context,
                               @Autowired UnitPersistence unitPersistence,
                               @Autowired AreaPersistence areaPersistence) {
        var tenant = context.getTenant();

        unitPersistence.deleteBy(Conditions.of(UnitEntity.class).eq(UnitEntity::getCode, "test"), tenant.getCode());
        areaPersistence.deleteBy(Conditions.of(AreaEntity.class).eq(AreaEntity::getCode, "test"), tenant.getCode());
    }

    @BeforeEach
    public void clear() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();

        // 清空数据
        this.persistence.deleteBy(Conditions.of(RoleEntity.class).eq(RoleEntity::getApplicationId, application.getId()), tenant.getCode());
    }

    @Setter(onMethod_ = @Autowired)
    private UnitPersistence unitPersistence;

    public UnitEntity getUnit() {
        return this.unitPersistence.findFirstBy(Columns.all(), Conditions.of(UnitEntity.class).eq(UnitEntity::getCode, "test"), null, this.context.getTenant().getCode());
    }

    /**
     * @see RoleProvider#insert
     * @see RoleProvider#findById
     * @see RoleProvider#update
     * @see RoleProvider#findByIds
     * @see RoleProvider#countBy
     * @see RoleProvider#deleteByIds
     */
    @Test
    public void case1() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();
        var unit = this.getUnit();

        var input = RoleInput.builder()
                .applicationId(application.getId())
                .unitId(unit.getId())
                .code("test")
                .name("测试角色")
                .enabled(Boolean.TRUE)
                .remark("测试角色")
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", tenant.getCode());
        assertNotNull(insert);
        assertEquals(input.getApplicationId(), insert.getApplicationId());
        assertEquals(input.getApplicationId(), insert.getApplication().getId());
        assertEquals(input.getUnitId(), insert.getUnitId());
        assertEquals(input.getUnitId(), insert.getUnit().getId());
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getRemark(), insert.getRemark());
        var entity = this.persistence.findById(insert.getId(), Columns.all(), tenant.getCode());
        assertNotNull(entity);

        // test findById
        var findById = this.provider.findById(insert.getId(), tenant.getCode());
        assertNotNull(findById);
        assertEquals(input.getApplicationId(), findById.getApplicationId());
        assertEquals(input.getApplicationId(), findById.getApplication().getId());
        assertEquals(input.getUnitId(), findById.getUnitId());
        assertEquals(input.getUnitId(), findById.getUnit().getId());
        assertEquals(input.getCode(), findById.getCode());
        assertEquals(input.getName(), findById.getName());
        assertEquals(input.getEnabled(), findById.getEnabled());
        assertEquals(input.getRemark(), findById.getRemark());

        // test countBy
        var count = this.provider.countBy(Conditions.of(Role.class).eq(Role::getApplicationId, application.getId()), tenant.getCode());
        assertEquals(1, count);

        // test update
        this.provider.update(insert.toInput().code("test2").enabled(Boolean.FALSE).build(), "syssa", tenant.getCode());

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), tenant.getCode());
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertEquals(input.getApplicationId(), fetched.getApplicationId());
        assertEquals(input.getApplicationId(), fetched.getApplication().getId());
        assertEquals(input.getUnitId(), fetched.getUnitId());
        assertEquals(input.getUnitId(), fetched.getUnit().getId());
        assertEquals("test2", fetched.getCode());
        assertEquals(input.getName(), fetched.getName());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(input.getRemark(), fetched.getRemark());

        // test deleteById
        count = this.provider.deleteByIds(List.of(insert.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(RoleEntity.class).eq(RoleEntity::getApplicationId, application.getId()), tenant.getCode());
        assertEquals(0, count);
    }

    /**
     * @see RoleProvider#insertBatch
     * @see RoleProvider#findBy
     * @see RoleProvider#updateBatch
     * @see RoleProvider#pageBy
     * @see RoleProvider#deleteBy
     */
    @Test
    public void case2() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();
        var unit = this.getUnit();

        var input = RoleInput.builder()
                .applicationId(application.getId())
                .unitId(unit.getId())
                .code("test")
                .name("测试角色")
                .enabled(Boolean.TRUE)
                .remark("测试角色")
                .build();

        // test insert
        var insertBatch = this.provider.insertBatch(List.of(input), "syssa", tenant.getCode());
        assertNotNull(insertBatch);
        assertEquals(1, insertBatch.size());

        var insert = Listx.getFirstOrNull(insertBatch);
        assertNotNull(insert);
        assertEquals(input.getApplicationId(), insert.getApplicationId());
        assertEquals(input.getApplicationId(), insert.getApplication().getId());
        assertEquals(input.getUnitId(), insert.getUnitId());
        assertEquals(input.getUnitId(), insert.getUnit().getId());
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getRemark(), insert.getRemark());
        var entity = this.persistence.findById(insert.getId(), Columns.all(), tenant.getCode());
        assertNotNull(entity);

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(Role.class).eq(Role::getApplicationId, application.getId()), null, tenant.getCode());
        assertNotNull(findBy);
        assertEquals(1, findBy.size());

        var fetched = Listx.getFirstOrNull(findBy);
        assertNotNull(fetched);
        assertEquals(input.getApplicationId(), fetched.getApplicationId());
        assertEquals(input.getApplicationId(), fetched.getApplication().getId());
        assertEquals(input.getUnitId(), fetched.getUnitId());
        assertEquals(input.getUnitId(), fetched.getUnit().getId());
        assertEquals(input.getCode(), fetched.getCode());
        assertEquals(input.getName(), fetched.getName());
        assertEquals(input.getEnabled(), fetched.getEnabled());
        assertEquals(input.getRemark(), fetched.getRemark());

        // test updateBatch
        this.provider.updateBatch(List.of(fetched.toInput().code("test2").enabled(Boolean.FALSE).build()), "syssa", tenant.getCode());

        // test pageBy
        var pageBy = this.provider.pageBy(1, 10, Conditions.of(Role.class).eq(Role::getApplicationId, application.getId()), null, tenant.getCode());
        assertNotNull(pageBy);
        assertEquals(1, pageBy.getPager().getPageIndex());
        assertEquals(10, pageBy.getPager().getPageSize());
        assertEquals(1, pageBy.getPager().getPageCount());
        assertEquals(1, pageBy.getPager().getItemCount());
        assertEquals(1, pageBy.getData().size());

        fetched = Listx.getFirstOrNull(pageBy.getData());
        assertNotNull(fetched);
        assertEquals(input.getApplicationId(), fetched.getApplicationId());
        assertEquals(input.getApplicationId(), fetched.getApplication().getId());
        assertEquals(input.getUnitId(), fetched.getUnitId());
        assertEquals(input.getUnitId(), fetched.getUnit().getId());
        assertEquals("test2", fetched.getCode());
        assertEquals(input.getName(), fetched.getName());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(input.getRemark(), fetched.getRemark());

        // test deleteById
        var count = this.provider.deleteBy(Conditions.of(Role.class).eq(Role::getApplicationId, application.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(RoleEntity.class).eq(RoleEntity::getApplicationId, application.getId()), tenant.getCode());
        assertEquals(0, count);
    }
}
