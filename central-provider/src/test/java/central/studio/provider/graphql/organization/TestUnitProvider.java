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

import central.data.organization.Unit;
import central.data.organization.UnitInput;
import central.data.organization.option.AreaType;
import central.provider.graphql.organization.UnitProvider;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.ProviderProperties;
import central.studio.provider.graphql.TestProvider;
import central.studio.provider.database.persistence.organization.entity.AreaEntity;
import central.studio.provider.database.persistence.organization.entity.DepartmentEntity;
import central.studio.provider.database.persistence.organization.entity.UnitEntity;
import central.studio.provider.database.persistence.organization.mapper.AreaMapper;
import central.studio.provider.database.persistence.organization.mapper.DepartmentMapper;
import central.studio.provider.database.persistence.organization.mapper.UnitMapper;
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

/**
 * Unit Provider Test Cases
 * 单位
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestUnitProvider extends TestProvider {
    @Setter(onMethod_ = @Autowired)
    private UnitProvider provider;

    @Setter(onMethod_ = @Autowired)
    private ProviderProperties properties;

    @Setter(onMethod_ = @Autowired)
    private UnitMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private AreaMapper areaMapper;

    @Setter(onMethod_ = @Autowired)
    private DepartmentMapper departmentMapper;


    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空数据
        areaMapper.deleteAll();
        mapper.deleteAll();
        departmentMapper.deleteAll();
    }

    /**
     * @see UnitProvider#findById
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

        var parentEntity = new UnitEntity();
        parentEntity.setParentId("");
        parentEntity.setAreaId(areaEntity.getId());
        parentEntity.setCode("10000");
        parentEntity.setName("测试父单位");
        parentEntity.setOrder(0);
        parentEntity.setTenantCode("master");
        parentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(parentEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId(parentEntity.getId());
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(unitEntity);

        var childEntity = new UnitEntity();
        childEntity.setParentId(unitEntity.getId());
        childEntity.setAreaId(areaEntity.getId());
        childEntity.setCode("10002");
        childEntity.setName("测试子单位");
        childEntity.setOrder(0);
        childEntity.setTenantCode("master");
        childEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(childEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("20000");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        // 查询数据
        var unit = this.provider.findById(unitEntity.getId(), "master");
        assertNotNull(unit);
        assertEquals(unitEntity.getId(), unit.getId());
        // 关联查询
        assertNotNull(unit.getArea());
        assertEquals(areaEntity.getId(), unit.getArea().getId());
        // 关联查询
        assertNotNull(unit.getParent());
        assertEquals(parentEntity.getId(), unit.getParent().getId());
        // 关联查询
        assertNotNull(unit.getChildren());
        assertEquals(1, unit.getChildren().size());
        assertTrue(unit.getChildren().stream().anyMatch(it -> Objects.equals(childEntity.getId(), it.getId())));
        // 关联查询
        assertNotNull(unit.getDepartments());
        assertEquals(1, unit.getDepartments().size());
        assertTrue(unit.getDepartments().stream().anyMatch(it -> Objects.equals(departmentEntity.getId(), it.getId())));
        // 关联查询
        assertNotNull(unit.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), unit.getCreator().getId());
        assertNotNull(unit.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), unit.getModifier().getId());
    }

    /**
     * @see UnitProvider#findByIds
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

        var parentEntity = new UnitEntity();
        parentEntity.setParentId("");
        parentEntity.setAreaId(areaEntity.getId());
        parentEntity.setCode("10000");
        parentEntity.setName("测试父单位");
        parentEntity.setOrder(0);
        parentEntity.setTenantCode("master");
        parentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(parentEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId(parentEntity.getId());
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(unitEntity);

        var childEntity = new UnitEntity();
        childEntity.setParentId(unitEntity.getId());
        childEntity.setAreaId(areaEntity.getId());
        childEntity.setCode("10002");
        childEntity.setName("测试子单位");
        childEntity.setOrder(0);
        childEntity.setTenantCode("master");
        childEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(childEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("20000");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        // 查询数据
        var units = this.provider.findByIds(List.of(unitEntity.getId()), "master");
        assertNotNull(units);
        assertEquals(1, units.size());

        var unit = Listx.getFirstOrNull(units);
        assertNotNull(unit);
        assertEquals(unitEntity.getId(), unit.getId());
        // 关联查询
        assertNotNull(unit.getArea());
        assertEquals(areaEntity.getId(), unit.getArea().getId());
        // 关联查询
        assertNotNull(unit.getParent());
        assertEquals(parentEntity.getId(), unit.getParent().getId());
        // 关联查询
        assertNotNull(unit.getChildren());
        assertEquals(1, unit.getChildren().size());
        assertTrue(unit.getChildren().stream().anyMatch(it -> Objects.equals(childEntity.getId(), it.getId())));
        // 关联查询
        assertNotNull(unit.getDepartments());
        assertEquals(1, unit.getDepartments().size());
        assertTrue(unit.getDepartments().stream().anyMatch(it -> Objects.equals(departmentEntity.getId(), it.getId())));
        // 关联查询
        assertNotNull(unit.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), unit.getCreator().getId());
        assertNotNull(unit.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), unit.getModifier().getId());
    }

    /**
     * @see UnitProvider#findBy
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

        var parentEntity = new UnitEntity();
        parentEntity.setParentId("");
        parentEntity.setAreaId(areaEntity.getId());
        parentEntity.setCode("10000");
        parentEntity.setName("测试父单位");
        parentEntity.setOrder(0);
        parentEntity.setTenantCode("master");
        parentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(parentEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId(parentEntity.getId());
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(unitEntity);

        var childEntity = new UnitEntity();
        childEntity.setParentId(unitEntity.getId());
        childEntity.setAreaId(areaEntity.getId());
        childEntity.setCode("10002");
        childEntity.setName("测试子单位");
        childEntity.setOrder(0);
        childEntity.setTenantCode("master");
        childEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(childEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("20000");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        // 查询数据
        var units = this.provider.findBy(null, null, Conditions.of(Unit.class).eq(Unit::getCode, "10001"), null, "master");
        assertNotNull(units);
        assertEquals(1, units.size());

        var unit = Listx.getFirstOrNull(units);
        assertNotNull(unit);
        assertEquals(unitEntity.getId(), unit.getId());
        // 关联查询
        assertNotNull(unit.getArea());
        assertEquals(areaEntity.getId(), unit.getArea().getId());
        // 关联查询
        assertNotNull(unit.getParent());
        assertEquals(parentEntity.getId(), unit.getParent().getId());
        // 关联查询
        assertNotNull(unit.getChildren());
        assertEquals(1, unit.getChildren().size());
        assertTrue(unit.getChildren().stream().anyMatch(it -> Objects.equals(childEntity.getId(), it.getId())));
        // 关联查询
        assertNotNull(unit.getDepartments());
        assertEquals(1, unit.getDepartments().size());
        assertTrue(unit.getDepartments().stream().anyMatch(it -> Objects.equals(departmentEntity.getId(), it.getId())));
        // 关联查询
        assertNotNull(unit.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), unit.getCreator().getId());
        assertNotNull(unit.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), unit.getModifier().getId());
    }

    /**
     * @see UnitProvider#pageBy
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

        var parentEntity = new UnitEntity();
        parentEntity.setParentId("");
        parentEntity.setAreaId(areaEntity.getId());
        parentEntity.setCode("10000");
        parentEntity.setName("测试父单位");
        parentEntity.setOrder(0);
        parentEntity.setTenantCode("master");
        parentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(parentEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId(parentEntity.getId());
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(unitEntity);

        var childEntity = new UnitEntity();
        childEntity.setParentId(unitEntity.getId());
        childEntity.setAreaId(areaEntity.getId());
        childEntity.setCode("10002");
        childEntity.setName("测试子单位");
        childEntity.setOrder(0);
        childEntity.setTenantCode("master");
        childEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(childEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("20000");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(Unit.class).eq(Unit::getCode, "10001"), null, "master");
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1L, page.getPager().getPageIndex());
        assertEquals(20L, page.getPager().getPageSize());
        assertEquals(1L, page.getPager().getPageCount());
        assertEquals(1L, page.getPager().getItemCount());
        assertNotNull(page.getData());

        var unit = Listx.getFirstOrNull(page.getData());
        assertNotNull(unit);
        assertEquals(unitEntity.getId(), unit.getId());
        // 关联查询
        assertNotNull(unit.getArea());
        assertEquals(areaEntity.getId(), unit.getArea().getId());
        // 关联查询
        assertNotNull(unit.getParent());
        assertEquals(parentEntity.getId(), unit.getParent().getId());
        // 关联查询
        assertNotNull(unit.getChildren());
        assertEquals(1, unit.getChildren().size());
        assertTrue(unit.getChildren().stream().anyMatch(it -> Objects.equals(childEntity.getId(), it.getId())));
        // 关联查询
        assertNotNull(unit.getDepartments());
        assertEquals(1, unit.getDepartments().size());
        assertTrue(unit.getDepartments().stream().anyMatch(it -> Objects.equals(departmentEntity.getId(), it.getId())));
        // 关联查询
        assertNotNull(unit.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), unit.getCreator().getId());
        assertNotNull(unit.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), unit.getModifier().getId());
    }

    /**
     * @see UnitProvider#countBy
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

        var parentEntity = new UnitEntity();
        parentEntity.setParentId("");
        parentEntity.setAreaId(areaEntity.getId());
        parentEntity.setCode("10000");
        parentEntity.setName("测试父单位");
        parentEntity.setOrder(0);
        parentEntity.setTenantCode("master");
        parentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(parentEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId(parentEntity.getId());
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(unitEntity);

        var childEntity = new UnitEntity();
        childEntity.setParentId(unitEntity.getId());
        childEntity.setAreaId(areaEntity.getId());
        childEntity.setCode("10002");
        childEntity.setName("测试子单位");
        childEntity.setOrder(0);
        childEntity.setTenantCode("master");
        childEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(childEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("20000");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        // 查询数据
        var count = this.provider.countBy(Conditions.of(Unit.class).eq(Unit::getCode, "10001"), "master");
        assertNotNull(count);
        assertEquals(1, count);
    }

    /**
     * @see UnitProvider#insert
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

        var parentEntity = new UnitEntity();
        parentEntity.setParentId("");
        parentEntity.setAreaId(areaEntity.getId());
        parentEntity.setCode("10000");
        parentEntity.setName("测试父单位");
        parentEntity.setOrder(0);
        parentEntity.setTenantCode("master");
        parentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(parentEntity);

        var unitInput = UnitInput.builder()
                .parentId(parentEntity.getId())
                .areaId(areaEntity.getId())
                .code("10001")
                .name("测试单位")
                .order(0)
                .build();
        var unit = this.provider.insert(unitInput, properties.getSupervisor().getUsername(), "master");
        assertNotNull(unit);
        assertNotNull(unit.getId());

        assertTrue(this.mapper.existsBy(Conditions.of(UnitEntity.class).eq(UnitEntity::getCode, "10001")));
    }

    /**
     * @see UnitProvider#insertBatch
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

        var parentEntity = new UnitEntity();
        parentEntity.setParentId("");
        parentEntity.setAreaId(areaEntity.getId());
        parentEntity.setCode("10000");
        parentEntity.setName("测试父单位");
        parentEntity.setOrder(0);
        parentEntity.setTenantCode("master");
        parentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(parentEntity);

        var unitInput = UnitInput.builder()
                .parentId(parentEntity.getId())
                .areaId(areaEntity.getId())
                .code("10001")
                .name("测试单位")
                .order(0)
                .build();

        var units = this.provider.insertBatch(List.of(unitInput), properties.getSupervisor().getUsername(), "master");
        assertNotNull(units);
        assertEquals(1, units.size());

        var unit = Listx.getFirstOrNull(units);
        assertNotNull(unit);
        assertNotNull(unit.getId());

        assertTrue(this.mapper.existsBy(Conditions.of(UnitEntity.class).eq(UnitEntity::getCode, "10001")));
    }

    /**
     * @see UnitProvider#update
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

        var parentEntity = new UnitEntity();
        parentEntity.setParentId("");
        parentEntity.setAreaId(areaEntity.getId());
        parentEntity.setCode("10000");
        parentEntity.setName("测试父单位");
        parentEntity.setOrder(0);
        parentEntity.setTenantCode("master");
        parentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(parentEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId(parentEntity.getId());
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(unitEntity);

        var unit = this.provider.findById(unitEntity.getId(), "master");
        assertNotNull(unit);
        assertNotNull(unit.getId());

        var unitInput = unit.toInput()
                .code("10002")
                .build();

        unit = this.provider.update(unitInput, properties.getSupervisor().getUsername(), "master");
        assertNotNull(unit);
        assertNotEquals(unit.getCreateDate(), unit.getModifyDate());

        assertTrue(this.mapper.existsBy(Conditions.of(UnitEntity.class).eq(UnitEntity::getCode, "10002")));
    }

    /**
     * @see UnitProvider#updateBatch
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

        var parentEntity = new UnitEntity();
        parentEntity.setParentId("");
        parentEntity.setAreaId(areaEntity.getId());
        parentEntity.setCode("10000");
        parentEntity.setName("测试父单位");
        parentEntity.setOrder(0);
        parentEntity.setTenantCode("master");
        parentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(parentEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId(parentEntity.getId());
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(unitEntity);

        var unit = this.provider.findById(unitEntity.getId(), "master");
        assertNotNull(unit);
        assertNotNull(unit.getId());

        var unitInput = unit.toInput()
                .code("10002")
                .build();

        var units = this.provider.updateBatch(List.of(unitInput), properties.getSupervisor().getUsername(), "master");
        assertNotNull(units);
        assertEquals(1, units.size());

        unit = Listx.getFirstOrNull(units);
        assertNotNull(unit);
        assertNotEquals(unit.getCreateDate(), unit.getModifyDate());

        assertTrue(this.mapper.existsBy(Conditions.of(UnitEntity.class).eq(UnitEntity::getCode, "10002")));
    }

    /**
     * @see UnitProvider#deleteByIds
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

        var parentEntity = new UnitEntity();
        parentEntity.setParentId("");
        parentEntity.setAreaId(areaEntity.getId());
        parentEntity.setCode("10000");
        parentEntity.setName("测试父单位");
        parentEntity.setOrder(0);
        parentEntity.setTenantCode("master");
        parentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(parentEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId(parentEntity.getId());
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(unitEntity);

        var deleted = this.provider.deleteByIds(List.of(unitEntity.getId()), "master");
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(UnitEntity.class).eq(UnitEntity::getId, unitEntity.getId())));
    }

    /**
     * @see UnitProvider#deleteBy(Conditions)
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

        var parentEntity = new UnitEntity();
        parentEntity.setParentId("");
        parentEntity.setAreaId(areaEntity.getId());
        parentEntity.setCode("10000");
        parentEntity.setName("测试父单位");
        parentEntity.setOrder(0);
        parentEntity.setTenantCode("master");
        parentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(parentEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId(parentEntity.getId());
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(unitEntity);

        var deleted = this.provider.deleteBy(Conditions.of(Unit.class).eq(Unit::getCode, "10001"), "master");
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(UnitEntity.class).eq(UnitEntity::getId, unitEntity.getId())));
    }
}
