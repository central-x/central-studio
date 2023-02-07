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

package central.provider.graphql.organization;

import central.api.provider.organization.DepartmentProvider;
import central.data.organization.Department;
import central.data.organization.DepartmentInput;
import central.data.organization.option.AreaType;
import central.lang.Stringx;
import central.provider.ApplicationProperties;
import central.provider.ProviderApplication;
import central.provider.graphql.TestProvider;
import central.provider.graphql.organization.entity.AreaEntity;
import central.provider.graphql.organization.entity.DepartmentEntity;
import central.provider.graphql.organization.entity.UnitEntity;
import central.provider.graphql.organization.mapper.AreaMapper;
import central.provider.graphql.organization.mapper.DepartmentMapper;
import central.provider.graphql.organization.mapper.UnitMapper;
import central.sql.query.Conditions;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Department Provider Test Cases
 * 部门
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestDepartmentProvider extends TestProvider {
    @Setter(onMethod_ = @Autowired)
    private DepartmentProvider provider;

    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    @Setter(onMethod_ = @Autowired)
    private UnitMapper unitMapper;

    @Setter(onMethod_ = @Autowired)
    private AreaMapper areaMapper;

    @Setter(onMethod_ = @Autowired)
    private DepartmentMapper departmentMapper;


    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空数据
        areaMapper.deleteAll();
        unitMapper.deleteAll();
        departmentMapper.deleteAll();
    }

    /**
     * @see DepartmentProvider#findById
     */
    @Test
    public void case1() {
        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("86");
        areaEntity.setName("中国");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        this.areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var parentDepartmentEntity = new DepartmentEntity();
        parentDepartmentEntity.setUnitId(unitEntity.getId());
        parentDepartmentEntity.setParentId("");
        parentDepartmentEntity.setCode("20001");
        parentDepartmentEntity.setName("测试父部门");
        parentDepartmentEntity.setOrder(0);
        parentDepartmentEntity.setTenantCode("master");
        parentDepartmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(parentDepartmentEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId(parentDepartmentEntity.getId());
        departmentEntity.setCode("20002");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        var childDepartmentEntity = new DepartmentEntity();
        childDepartmentEntity.setUnitId(unitEntity.getId());
        childDepartmentEntity.setParentId(departmentEntity.getId());
        childDepartmentEntity.setCode("20003");
        childDepartmentEntity.setName("测试子部门");
        childDepartmentEntity.setOrder(0);
        childDepartmentEntity.setTenantCode("master");
        childDepartmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(childDepartmentEntity);

        // 查询数据
        var department = this.provider.findById(departmentEntity.getId());
        assertNotNull(department);
        assertEquals(departmentEntity.getId(), department.getId());
        // 关联查询
        assertNotNull(department.getUnit());
        assertEquals(unitEntity.getId(), department.getUnit().getId());
        // 关联查询
        assertNotNull(department.getParent());
        assertEquals(parentDepartmentEntity.getId(), department.getParent().getId());
        // 关联查询
        assertNotNull(department.getChildren());
        assertEquals(1, department.getChildren().size());
        assertTrue(department.getChildren().stream().anyMatch(it -> Objects.equals(childDepartmentEntity.getId(), it.getId())));
        // 关联查询
        assertNotNull(department.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), department.getCreator().getId());
        assertNotNull(department.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), department.getModifier().getId());
    }

    /**
     * @see DepartmentProvider#findByIds
     */
    @Test
    public void case2() {
        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("86");
        areaEntity.setName("中国");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        this.areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("20002");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        // 查询数据
        var departments = this.provider.findByIds(List.of(departmentEntity.getId()));
        assertNotNull(departments);
        assertEquals(1, departments.size());

        var department = Listx.getFirstOrNull(departments);
        assertNotNull(department);
        assertEquals(departmentEntity.getId(), department.getId());
        // 关联查询
        assertNotNull(department.getUnit());
        assertEquals(unitEntity.getId(), department.getUnit().getId());
        // 关联查询
        assertNotNull(department.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), department.getCreator().getId());
        assertNotNull(department.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), department.getModifier().getId());
    }

    /**
     * @see DepartmentProvider#findBy
     */
    @Test
    public void case3() {
        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("86");
        areaEntity.setName("中国");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        this.areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("20002");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        // 查询数据
        var departments = this.provider.findBy(null, null, Conditions.of(Department.class).eq(Department::getCode, "20002"), null);
        assertNotNull(departments);
        assertEquals(1, departments.size());

        var department = Listx.getFirstOrNull(departments);
        assertNotNull(department);
        assertEquals(departmentEntity.getId(), department.getId());
        // 关联查询
        assertNotNull(department.getUnit());
        assertEquals(unitEntity.getId(), department.getUnit().getId());
        // 关联查询
        assertNotNull(department.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), department.getCreator().getId());
        assertNotNull(department.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), department.getModifier().getId());
    }

    /**
     * @see DepartmentProvider#pageBy
     */
    @Test
    public void case4() {
        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("86");
        areaEntity.setName("中国");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        this.areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("20002");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(Department.class).eq(Department::getCode, "20002"), null);
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1L, page.getPager().getPageIndex());
        assertEquals(20L, page.getPager().getPageSize());
        assertEquals(1L, page.getPager().getPageCount());
        assertEquals(1L, page.getPager().getItemCount());
        assertNotNull(page.getData());

        var department = Listx.getFirstOrNull(page.getData());
        assertNotNull(department);
        assertEquals(departmentEntity.getId(), department.getId());
        // 关联查询
        assertNotNull(department.getUnit());
        assertEquals(unitEntity.getId(), department.getUnit().getId());
        // 关联查询
        assertNotNull(department.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), department.getCreator().getId());
        assertNotNull(department.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), department.getModifier().getId());
    }

    /**
     * @see DepartmentProvider#countBy
     */
    @Test
    public void case5() {
        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("86");
        areaEntity.setName("中国");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        this.areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("20002");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        // 查询数据
        var count = this.provider.countBy(Conditions.of(Department.class).eq(Department::getCode, "20002"));
        assertNotNull(count);
        assertEquals(1, count);
    }

    /**
     * @see DepartmentProvider#insert
     */
    @Test
    public void case6() {
        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("86");
        areaEntity.setName("中国");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        this.areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var input = DepartmentInput.builder()
                .unitId(unitEntity.getId())
                .parentId("")
                .code("20002")
                .name("测试部门")
                .order(0)
                .build();

        var department = this.provider.insert(input, properties.getSupervisor().getUsername());
        assertNotNull(department);
        assertNotNull(department.getId());

        assertTrue(this.departmentMapper.existsBy(Conditions.of(DepartmentEntity.class).eq(DepartmentEntity::getId, department.getId())));
    }

    /**
     * @see DepartmentProvider#insertBatch
     */
    @Test
    public void case7() {
        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("86");
        areaEntity.setName("中国");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        this.areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var input = DepartmentInput.builder()
                .unitId(unitEntity.getId())
                .parentId("")
                .code("20002")
                .name("测试部门")
                .order(0)
                .build();

        var departments = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(departments);
        assertEquals(1, departments.size());
        assertTrue(departments.stream().allMatch(it -> Stringx.isNotBlank(it.getId())));

        assertTrue(this.departmentMapper.existsBy(Conditions.of(DepartmentEntity.class).eq(DepartmentEntity::getId, departments.get(0).getId())));
    }

    /**
     * @see DepartmentProvider#update
     */
    @Test
    public void case8() {
        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("86");
        areaEntity.setName("中国");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        this.areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("20002");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        var department = this.provider.findById(departmentEntity.getId());
        assertNotNull(department);
        assertNotNull(department.getId());

        var input = department.toInput().toBuilder()
                .code("20003")
                .build();

        department = this.provider.update(input, properties.getSupervisor().getUsername());
        assertNotNull(department);
        assertNotEquals(department.getCreateDate(), department.getModifyDate());

        assertTrue(this.departmentMapper.existsBy(Conditions.of(DepartmentEntity.class).eq(DepartmentEntity::getCode, "20003")));
    }

    /**
     * @see DepartmentProvider#updateBatch
     */
    @Test
    public void case9() {
        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("86");
        areaEntity.setName("中国");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        this.areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("20002");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        var department = this.provider.findById(departmentEntity.getId());
        assertNotNull(department);
        assertNotNull(department.getId());

        var input = department.toInput().toBuilder()
                .code("20003")
                .build();

        var departments = this.provider.updateBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(departments);
        assertEquals(1, departments.size());
        assertTrue(departments.stream().noneMatch(it -> Objects.equals(it.getCreateDate(), it.getModifyDate())));

        assertTrue(this.departmentMapper.existsBy(Conditions.of(DepartmentEntity.class).eq(DepartmentEntity::getCode, "20003")));
    }

    /**
     * @see DepartmentProvider#deleteByIds
     */
    @Test
    public void case10() {
        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("86");
        areaEntity.setName("中国");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        this.areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("20002");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        var deleted = this.provider.deleteByIds(List.of(departmentEntity.getId()));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.departmentMapper.existsBy(Conditions.of(DepartmentEntity.class).eq(DepartmentEntity::getId, unitEntity.getId())));
    }

    /**
     * @see DepartmentProvider#deleteBy(Conditions)
     */
    @Test
    public void case11() {
        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("86");
        areaEntity.setName("中国");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        this.areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("20002");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        var deleted = this.provider.deleteBy(Conditions.of(Department.class).eq(Department::getCode, "20002"));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.departmentMapper.existsBy(Conditions.of(DepartmentEntity.class).eq(DepartmentEntity::getId, departmentEntity.getId())));
    }
}
