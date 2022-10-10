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

package central.provider.graphql.sec;

import central.api.provider.org.PostProvider;
import central.api.provider.sec.RoleProvider;
import central.data.org.Post;
import central.data.org.PostInput;
import central.data.org.option.AreaType;
import central.data.sec.Role;
import central.data.sec.RoleInput;
import central.provider.ApplicationProperties;
import central.provider.ProviderApplication;
import central.provider.graphql.TestProvider;
import central.provider.graphql.org.entity.AreaEntity;
import central.provider.graphql.org.entity.PostEntity;
import central.provider.graphql.org.entity.UnitEntity;
import central.provider.graphql.org.mapper.AreaMapper;
import central.provider.graphql.org.mapper.PostMapper;
import central.provider.graphql.org.mapper.UnitMapper;
import central.provider.graphql.sec.entity.RoleEntity;
import central.provider.graphql.sec.mapper.RoleMapper;
import central.provider.graphql.ten.entity.ApplicationEntity;
import central.provider.graphql.ten.mapper.ApplicationMapper;
import central.sql.Conditions;
import central.util.Guidx;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    private ApplicationProperties properties;

    @Setter(onMethod_ = @Autowired)
    private RoleMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private UnitMapper unitMapper;

    @Setter(onMethod_ = @Autowired)
    private AreaMapper areaMapper;

    @Setter(onMethod_ = @Autowired)
    private ApplicationMapper applicationMapper;

    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空数据
        mapper.deleteAll();
        unitMapper.deleteAll();
        areaMapper.deleteAll();
        applicationMapper.deleteAll();
    }

    /**
     * @see PostProvider#findById
     */
    @Test
    public void case1() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setKey(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var roleEntity = new RoleEntity();
        roleEntity.setApplicationId(applicationEntity.getId());
        roleEntity.setCode("10000");
        roleEntity.setName("测试角色");
        roleEntity.setUnitId(unitEntity.getId());
        roleEntity.setEnabled(Boolean.TRUE);
        roleEntity.setRemark("测试角色");
        roleEntity.setTenantCode("master");
        roleEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(roleEntity);

        // 查询数据
        var role = this.provider.findById(roleEntity.getId());
        assertNotNull(role);
        assertEquals(roleEntity.getId(), role.getId());
        // 关联查询
        assertNotNull(role.getApplication());
        assertEquals(applicationEntity.getId(), role.getApplication().getId());
        // 关联查询
        assertNotNull(role.getUnit());
        assertEquals(unitEntity.getId(), role.getUnit().getId());
        // 关联查询
        assertNotNull(role.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), role.getCreator().getId());
        assertNotNull(role.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), role.getModifier().getId());
    }

    /**
     * @see PostProvider#findByIds
     */
    @Test
    public void case2() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setKey(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var roleEntity = new RoleEntity();
        roleEntity.setApplicationId(applicationEntity.getId());
        roleEntity.setCode("10000");
        roleEntity.setName("测试角色");
        roleEntity.setUnitId(unitEntity.getId());
        roleEntity.setEnabled(Boolean.TRUE);
        roleEntity.setRemark("测试角色");
        roleEntity.setTenantCode("master");
        roleEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(roleEntity);

        // 查询数据
        var roles = this.provider.findByIds(List.of(roleEntity.getId()));
        assertNotNull(roles);
        assertEquals(1, roles.size());

        var role = Listx.getFirst(roles);
        assertNotNull(role);
        assertEquals(roleEntity.getId(), role.getId());
        // 关联查询
        assertNotNull(role.getApplication());
        assertEquals(applicationEntity.getId(), role.getApplication().getId());
        // 关联查询
        assertNotNull(role.getUnit());
        assertEquals(unitEntity.getId(), role.getUnit().getId());
        // 关联查询
        assertNotNull(role.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), role.getCreator().getId());
        assertNotNull(role.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), role.getModifier().getId());
    }

    /**
     * @see PostProvider#findBy
     */
    @Test
    public void case3() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setKey(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var roleEntity = new RoleEntity();
        roleEntity.setApplicationId(applicationEntity.getId());
        roleEntity.setCode("10000");
        roleEntity.setName("测试角色");
        roleEntity.setUnitId(unitEntity.getId());
        roleEntity.setEnabled(Boolean.TRUE);
        roleEntity.setRemark("测试角色");
        roleEntity.setTenantCode("master");
        roleEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(roleEntity);

        // 查询数据
        var roles = this.provider.findBy(null, null, Conditions.of(Role.class).eq(Role::getCode, "10000"), null);
        assertNotNull(roles);
        assertEquals(1, roles.size());

        var role = Listx.getFirst(roles);
        assertNotNull(role);
        assertEquals(roleEntity.getId(), role.getId());
        // 关联查询
        assertNotNull(role.getApplication());
        assertEquals(applicationEntity.getId(), role.getApplication().getId());
        // 关联查询
        assertNotNull(role.getUnit());
        assertEquals(unitEntity.getId(), role.getUnit().getId());
        // 关联查询
        assertNotNull(role.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), role.getCreator().getId());
        assertNotNull(role.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), role.getModifier().getId());
    }

    /**
     * @see PostProvider#pageBy
     */
    @Test
    public void case4() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setKey(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var roleEntity = new RoleEntity();
        roleEntity.setApplicationId(applicationEntity.getId());
        roleEntity.setCode("10000");
        roleEntity.setName("测试角色");
        roleEntity.setUnitId(unitEntity.getId());
        roleEntity.setEnabled(Boolean.TRUE);
        roleEntity.setRemark("测试角色");
        roleEntity.setTenantCode("master");
        roleEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(roleEntity);

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(Role.class).eq(Role::getCode, "10000"), null);
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1, page.getPager().getPageIndex());
        assertEquals(20, page.getPager().getPageSize());
        assertEquals(1, page.getPager().getPageCount());
        assertEquals(1, page.getPager().getItemCount());

        var role = Listx.getFirst(page.getData());
        assertNotNull(role);
        assertEquals(roleEntity.getId(), role.getId());
        // 关联查询
        assertNotNull(role.getApplication());
        assertEquals(applicationEntity.getId(), role.getApplication().getId());
        // 关联查询
        assertNotNull(role.getUnit());
        assertEquals(unitEntity.getId(), role.getUnit().getId());
        // 关联查询
        assertNotNull(role.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), role.getCreator().getId());
        assertNotNull(role.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), role.getModifier().getId());
    }

    /**
     * @see PostProvider#countBy
     */
    @Test
    public void case5() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setKey(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var roleEntity = new RoleEntity();
        roleEntity.setApplicationId(applicationEntity.getId());
        roleEntity.setCode("10000");
        roleEntity.setName("测试角色");
        roleEntity.setUnitId(unitEntity.getId());
        roleEntity.setEnabled(Boolean.TRUE);
        roleEntity.setRemark("测试角色");
        roleEntity.setTenantCode("master");
        roleEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(roleEntity);

        // 查询数据
        var count = this.provider.countBy(Conditions.of(Role.class).eq(Role::getCode, "10000"));
        assertNotNull(count);
        assertEquals(1, count);
    }

    /**
     * @see PostProvider#insert
     */
    @Test
    public void case6() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setKey(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var roleInput = RoleInput.builder()
                .applicationId(applicationEntity.getId())
                .code("10000")
                .name("测试角色")
                .unitId(unitEntity.getId())
                .enabled(Boolean.TRUE)
                .remark("测试角色")
                .build();

        // 查询数据
        var role = this.provider.insert(roleInput, properties.getSupervisor().getUsername());
        assertNotNull(role);
        assertNotNull(role.getId());
        // 关联查询
        assertNotNull(role.getApplication());
        assertEquals(applicationEntity.getId(), role.getApplication().getId());
        // 关联查询
        assertNotNull(role.getUnit());
        assertEquals(unitEntity.getId(), role.getUnit().getId());
        // 关联查询
        assertNotNull(role.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), role.getCreator().getId());
        assertNotNull(role.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), role.getModifier().getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(RoleEntity.class).eq(RoleEntity::getId, role.getId())));
    }

    /**
     * @see PostProvider#insertBatch
     */
    @Test
    public void case7() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setKey(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var roleInput = RoleInput.builder()
                .applicationId(applicationEntity.getId())
                .code("10000")
                .name("测试角色")
                .unitId(unitEntity.getId())
                .enabled(Boolean.TRUE)
                .remark("测试角色")
                .build();

        // 查询数据
        var roles = this.provider.insertBatch(List.of(roleInput), properties.getSupervisor().getUsername());
        assertNotNull(roles);
        assertEquals(1, roles.size());
        var role = Listx.getFirst(roles);

        assertNotNull(role);
        assertNotNull(role.getId());
        // 关联查询
        assertNotNull(role.getApplication());
        assertEquals(applicationEntity.getId(), role.getApplication().getId());
        // 关联查询
        assertNotNull(role.getUnit());
        assertEquals(unitEntity.getId(), role.getUnit().getId());
        // 关联查询
        assertNotNull(role.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), role.getCreator().getId());
        assertNotNull(role.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), role.getModifier().getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(RoleEntity.class).eq(RoleEntity::getId, role.getId())));
    }

    /**
     * @see PostProvider#update
     */
    @Test
    public void case8() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setKey(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var roleEntity = new RoleEntity();
        roleEntity.setApplicationId(applicationEntity.getId());
        roleEntity.setCode("10000");
        roleEntity.setName("测试角色");
        roleEntity.setUnitId(unitEntity.getId());
        roleEntity.setEnabled(Boolean.TRUE);
        roleEntity.setRemark("测试角色");
        roleEntity.setTenantCode("master");
        roleEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(roleEntity);

        // 查询数据
        var role = this.provider.findById(roleEntity.getId());
        assertNotNull(role);
        assertEquals(roleEntity.getId(), role.getId());

        var roleInput = role.toInput().toBuilder()
                .code("10001")
                .build();

        role = this.provider.update(roleInput, properties.getSupervisor().getUsername());
        assertNotNull(role);
        assertEquals(roleEntity.getId(), role.getId());

        // 关联查询
        assertNotNull(role.getApplication());
        assertEquals(applicationEntity.getId(), role.getApplication().getId());
        // 关联查询
        assertNotNull(role.getUnit());
        assertEquals(unitEntity.getId(), role.getUnit().getId());
        // 关联查询
        assertNotNull(role.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), role.getCreator().getId());
        assertNotNull(role.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), role.getModifier().getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(RoleEntity.class).eq(RoleEntity::getCode, "10001")));
    }

    /**
     * @see PostProvider#updateBatch
     */
    @Test
    public void case9() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setKey(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var roleEntity = new RoleEntity();
        roleEntity.setApplicationId(applicationEntity.getId());
        roleEntity.setCode("10000");
        roleEntity.setName("测试角色");
        roleEntity.setUnitId(unitEntity.getId());
        roleEntity.setEnabled(Boolean.TRUE);
        roleEntity.setRemark("测试角色");
        roleEntity.setTenantCode("master");
        roleEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(roleEntity);

        // 查询数据
        var role = this.provider.findById(roleEntity.getId());
        assertNotNull(role);
        assertEquals(roleEntity.getId(), role.getId());

        var roleInput = role.toInput().toBuilder()
                .code("10001")
                .build();

        var roles = this.provider.updateBatch(List.of(roleInput), properties.getSupervisor().getUsername());
        assertNotNull(roles);
        assertEquals(1, roles.size());

        role = Listx.getFirst(roles);
        assertNotNull(role);
        // 关联查询
        assertNotNull(role.getApplication());
        assertEquals(applicationEntity.getId(), role.getApplication().getId());
        // 关联查询
        assertNotNull(role.getUnit());
        assertEquals(unitEntity.getId(), role.getUnit().getId());
        // 关联查询
        assertNotNull(role.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), role.getCreator().getId());
        assertNotNull(role.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), role.getModifier().getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(RoleEntity.class).eq(RoleEntity::getCode, "10001")));
    }

    /**
     * @see PostProvider#deleteByIds
     */
    @Test
    public void case10() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setKey(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var roleEntity = new RoleEntity();
        roleEntity.setApplicationId(applicationEntity.getId());
        roleEntity.setCode("10000");
        roleEntity.setName("测试角色");
        roleEntity.setUnitId(unitEntity.getId());
        roleEntity.setEnabled(Boolean.TRUE);
        roleEntity.setRemark("测试角色");
        roleEntity.setTenantCode("master");
        roleEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(roleEntity);

        var deleted = this.provider.deleteByIds(List.of(roleEntity.getId()));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(RoleEntity.class).eq(RoleEntity::getId, roleEntity.getId())));
    }

    /**
     * @see PostProvider#deleteBy(Conditions)
     */
    @Test
    public void case11() {

        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setKey(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var roleEntity = new RoleEntity();
        roleEntity.setApplicationId(applicationEntity.getId());
        roleEntity.setCode("10000");
        roleEntity.setName("测试角色");
        roleEntity.setUnitId(unitEntity.getId());
        roleEntity.setEnabled(Boolean.TRUE);
        roleEntity.setRemark("测试角色");
        roleEntity.setTenantCode("master");
        roleEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(roleEntity);

        var deleted = this.provider.deleteBy(Conditions.of(Role.class).eq(Role::getCode, "10000"));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(RoleEntity.class).eq(RoleEntity::getId, roleEntity.getId())));
    }
}
