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

import central.data.authority.RolePermission;
import central.data.authority.RolePermissionInput;
import central.data.authority.option.MenuType;
import central.provider.graphql.authority.RolePermissionProvider;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.ProviderProperties;
import central.studio.provider.graphql.authority.entity.MenuEntity;
import central.studio.provider.graphql.authority.entity.PermissionEntity;
import central.studio.provider.graphql.authority.entity.RoleEntity;
import central.studio.provider.graphql.authority.mapper.MenuMapper;
import central.studio.provider.graphql.authority.mapper.PermissionMapper;
import central.studio.provider.graphql.authority.mapper.RoleMapper;
import central.studio.provider.graphql.authority.mapper.RolePermissionMapper;
import central.studio.provider.graphql.saas.entity.ApplicationEntity;
import central.studio.provider.graphql.saas.mapper.ApplicationMapper;
import central.util.Guidx;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Role Permission Provider Test Cases
 *
 * @author Alan Yeh
 * @since 2024/12/15
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestRolePermissionProvider {

    @Setter(onMethod_ = @Autowired)
    private ProviderProperties properties;

    @Setter(onMethod_ = @Autowired)
    private RolePermissionProvider provider;

    @Setter(onMethod_ = @Autowired)
    private RolePermissionMapper mapper;

    @BeforeAll
    public static void setup(@Autowired ProviderProperties properties,
                             @Autowired ApplicationMapper applicationMapper,
                             @Autowired MenuMapper menuMapper,
                             @Autowired PermissionMapper permissionMapper,
                             @Autowired RoleMapper roleMapper) {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("test");
        applicationEntity.setName("测试应用");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/test");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("测试应用");
        applicationEntity.setRoutesJson("[]");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        applicationMapper.insert(applicationEntity);

        var menuEntity = new MenuEntity();
        menuEntity.setApplicationId(applicationEntity.getId());
        menuEntity.setParentId("");
        menuEntity.setCode("test");
        menuEntity.setName("测试菜单");
        menuEntity.setIcon("icon");
        menuEntity.setUrl("");
        menuEntity.setType(MenuType.BACKEND.getValue());
        menuEntity.setEnabled(Boolean.TRUE);
        menuEntity.setOrder(0);
        menuEntity.setRemark("菜单");
        menuEntity.setTenantCode("master");
        menuEntity.updateCreator(properties.getSupervisor().getUsername());
        menuMapper.insert(menuEntity);

        var permissionEntity = new PermissionEntity();
        permissionEntity.setApplicationId(applicationEntity.getId());
        permissionEntity.setMenuId(menuEntity.getId());
        permissionEntity.setCode("test-add");
        permissionEntity.setName("添加");
        permissionEntity.setTenantCode("master");
        permissionEntity.updateCreator(properties.getSupervisor().getUsername());
        permissionMapper.insert(permissionEntity);

        var roleEntity = new RoleEntity();
        roleEntity.setApplicationId(applicationEntity.getId());
        roleEntity.setCode("test");
        roleEntity.setName("测试角色");
        roleEntity.setUnitId("");
        roleEntity.setEnabled(Boolean.TRUE);
        roleEntity.setRemark("测试角色");
        roleEntity.setTenantCode("master");
        roleEntity.updateCreator(properties.getSupervisor().getUsername());
        roleMapper.insert(roleEntity);
    }

    @AfterAll
    public static void cleanup(@Autowired ApplicationMapper applicationMapper,
                               @Autowired MenuMapper menuMapper,
                               @Autowired PermissionMapper permissionMapper,
                               @Autowired RoleMapper roleMapper) {
        applicationMapper.deleteBy(Conditions.of(ApplicationEntity.class).eq(ApplicationEntity::getCode, "test"));
        menuMapper.deleteBy(Conditions.of(MenuEntity.class).eq(MenuEntity::getCode, "test"));
        roleMapper.deleteBy(Conditions.of(RoleEntity.class).eq(RoleEntity::getCode, "test"));
        permissionMapper.deleteBy(Conditions.of(PermissionEntity.class).eq(PermissionEntity::getCode, "test-add"));
    }

    @BeforeEach
    @AfterEach
    public void clear() {
        this.mapper.deleteAll();
    }

    @Setter(onMethod_ = @Autowired)
    private ApplicationMapper applicationMapper;

    private ApplicationEntity getApplication() {
        return applicationMapper.findFirstBy(Conditions.of(ApplicationEntity.class).eq(ApplicationEntity::getCode, "test"));
    }

    @Setter(onMethod_ = @Autowired)
    private PermissionMapper permissionMapper;

    private PermissionEntity getPermission() {
        return permissionMapper.findFirstBy(Conditions.of(PermissionEntity.class).eq(PermissionEntity::getCode, "test-add"));
    }

    @Setter(onMethod_ = @Autowired)
    private RoleMapper roleMapper;

    private RoleEntity getRole() {
        return roleMapper.findFirstBy(Conditions.of(RoleEntity.class).eq(RoleEntity::getCode, "test"));
    }

    /**
     * @see RolePermissionProvider#insert
     * @see RolePermissionProvider#findById
     * @see RolePermissionProvider#countBy
     * @see RolePermissionProvider#deleteByIds
     */
    @Test
    public void case1() {
        var application = this.getApplication();
        var permission = this.getPermission();
        var role = this.getRole();

        var input = RolePermissionInput.builder()
                .applicationId(application.getId())
                .roleId(role.getId())
                .permissionId(permission.getId())
                .build();

        var inserted = this.provider.insert(input, properties.getSupervisor().getUsername(), "master");
        assertNotNull(inserted);
        assertEquals(input.getApplicationId(), inserted.getApplicationId());
        assertEquals(input.getApplicationId(), inserted.getApplication().getId());
        assertEquals(input.getRoleId(), inserted.getRoleId());
        assertEquals(input.getRoleId(), inserted.getRole().getId());
        assertEquals(input.getPermissionId(), inserted.getPermissionId());
        assertEquals(input.getPermissionId(), inserted.getPermission().getId());

        var found = this.provider.findById(inserted.getId(), "master");
        assertNotNull(found);
        assertEquals(input.getApplicationId(), found.getApplicationId());
        assertEquals(input.getApplicationId(), found.getApplication().getId());
        assertEquals(input.getRoleId(), found.getRoleId());
        assertEquals(input.getRoleId(), found.getRole().getId());
        assertEquals(input.getPermissionId(), found.getPermissionId());
        assertEquals(input.getPermissionId(), found.getPermission().getId());

        var count = this.provider.countBy(Conditions.of(RolePermission.class).eq(RolePermission::getRoleId, role.getId()), "master");
        assertEquals(1, count);

        var deleted = this.provider.deleteByIds(List.of(inserted.getId()), "master");
        assertEquals(1, deleted);
    }

    /**
     * @see RolePermissionProvider#insertBatch
     * @see RolePermissionProvider#findByIds
     * @see RolePermissionProvider#findBy
     * @see RolePermissionProvider#pageBy
     * @see RolePermissionProvider#deleteBy
     */
    @Test
    public void case2() {
        var application = this.getApplication();
        var permission = this.getPermission();
        var role = this.getRole();

        var input = RolePermissionInput.builder()
                .applicationId(application.getId())
                .roleId(role.getId())
                .permissionId(permission.getId())
                .build();

        // insertBatch
        var batch = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername(), "master");
        assertNotNull(batch);
        assertEquals(1, batch.size());
        var inserted = Listx.getFirstOrNull(batch);
        assertEquals(input.getApplicationId(), inserted.getApplicationId());
        assertEquals(input.getApplicationId(), inserted.getApplication().getId());
        assertEquals(input.getRoleId(), inserted.getRoleId());
        assertEquals(input.getRoleId(), inserted.getRole().getId());
        assertEquals(input.getPermissionId(), inserted.getPermissionId());
        assertEquals(input.getPermissionId(), inserted.getPermission().getId());

        // findByIds
        var found = this.provider.findByIds(List.of(inserted.getId()), "master");
        assertNotNull(found);
        assertEquals(1, found.size());
        inserted = Listx.getFirstOrNull(found);
        assertEquals(input.getApplicationId(), inserted.getApplicationId());
        assertEquals(input.getApplicationId(), inserted.getApplication().getId());
        assertEquals(input.getRoleId(), inserted.getRoleId());
        assertEquals(input.getRoleId(), inserted.getRole().getId());
        assertEquals(input.getPermissionId(), inserted.getPermissionId());
        assertEquals(input.getPermissionId(), inserted.getPermission().getId());

        // findBy
        var list = this.provider.findBy(null, null, Conditions.of(RolePermission.class).eq(RolePermission::getRoleId, role.getId()), null, "master");
        assertNotNull(list);
        assertEquals(1, list.size());
        inserted = Listx.getFirstOrNull(list);
        assertEquals(input.getApplicationId(), inserted.getApplicationId());
        assertEquals(input.getApplicationId(), inserted.getApplication().getId());
        assertEquals(input.getRoleId(), inserted.getRoleId());
        assertEquals(input.getRoleId(), inserted.getRole().getId());
        assertEquals(input.getPermissionId(), inserted.getPermissionId());
        assertEquals(input.getPermissionId(), inserted.getPermission().getId());

        // pageBy
        var page = this.provider.pageBy(1, 20, Conditions.of(RolePermission.class).eq(RolePermission::getRoleId, role.getId()), null, "master");
        assertNotNull(page);
        assertEquals(1, page.getPager().getPageIndex());
        assertEquals(20, page.getPager().getPageSize());
        assertEquals(1, page.getPager().getPageCount());
        assertEquals(1, page.getPager().getItemCount());
        inserted = Listx.getFirstOrNull(page.getData());
        assertEquals(input.getApplicationId(), inserted.getApplicationId());
        assertEquals(input.getApplicationId(), inserted.getApplication().getId());
        assertEquals(input.getRoleId(), inserted.getRoleId());
        assertEquals(input.getRoleId(), inserted.getRole().getId());
        assertEquals(input.getPermissionId(), inserted.getPermissionId());
        assertEquals(input.getPermissionId(), inserted.getPermission().getId());

        var deleted = this.provider.deleteBy(Conditions.of(RolePermission.class).eq(RolePermission::getRoleId, role.getId()), "master");
        assertEquals(1, deleted);
    }
}
