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

import central.data.authority.Permission;
import central.data.authority.PermissionInput;
import central.data.authority.option.MenuType;
import central.provider.graphql.authority.PermissionProvider;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.ProviderProperties;
import central.studio.provider.graphql.TestProvider;
import central.studio.provider.graphql.authority.entity.MenuEntity;
import central.studio.provider.graphql.authority.entity.PermissionEntity;
import central.studio.provider.graphql.authority.mapper.MenuMapper;
import central.studio.provider.graphql.authority.mapper.PermissionMapper;
import central.studio.provider.graphql.saas.entity.ApplicationEntity;
import central.studio.provider.graphql.saas.mapper.ApplicationMapper;
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
 * Provider Provider Test Cases
 * 权限测试
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestPermissionProvider extends TestProvider {
    @Setter(onMethod_ = @Autowired)
    private ProviderProperties properties;

    @Setter(onMethod_ = @Autowired)
    private PermissionProvider provider;

    @Setter(onMethod_ = @Autowired)
    private MenuMapper menuMapper;

    @Setter(onMethod_ = @Autowired)
    private PermissionMapper permissionMapper;

    @Setter(onMethod_ = @Autowired)
    private ApplicationMapper applicationMapper;

    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空测试数据
        this.menuMapper.deleteAll();
        this.applicationMapper.deleteAll();
        this.permissionMapper.deleteAll();
    }

    /**
     * @see PermissionProvider#findById
     */
    @Test
    public void case1() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var menuEntity = new MenuEntity();
        menuEntity.setApplicationId(applicationEntity.getId());
        menuEntity.setParentId("");
        menuEntity.setCode("test");
        menuEntity.setName("测试菜单");
        menuEntity.setIcon("icon");
        menuEntity.setType(MenuType.BACKEND.getValue());
        menuEntity.setEnabled(Boolean.TRUE);
        menuEntity.setOrder(0);
        menuEntity.setRemark("菜单");
        menuEntity.setTenantCode("master");
        menuEntity.updateCreator(properties.getSupervisor().getUsername());
        this.menuMapper.insert(menuEntity);

        var permissionEntity = new PermissionEntity();
        permissionEntity.setApplicationId(applicationEntity.getId());
        permissionEntity.setMenuId(menuEntity.getId());
        permissionEntity.setCode("test-add");
        permissionEntity.setName("添加");
        permissionEntity.setTenantCode("master");
        permissionEntity.updateCreator(properties.getSupervisor().getUsername());
        this.permissionMapper.insert(permissionEntity);

        // 查询数据
        var permission = this.provider.findById(permissionEntity.getId(), "master");
        assertNotNull(permission);
        assertEquals(permissionEntity.getId(), permission.getId());
        // 关联查询
        assertNotNull(permission.getApplication());
        assertEquals(applicationEntity.getId(), permission.getApplication().getId());
        // 关联查询
        assertNotNull(permission.getMenu());
        assertEquals(menuEntity.getId(), permission.getMenu().getId());
        // 关联查询
        assertNotNull(permission.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), permission.getCreator().getId());
        assertNotNull(permission.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), permission.getModifier().getId());
    }

    /**
     * @see PermissionProvider#findByIds
     */
    @Test
    public void case2() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var menuEntity = new MenuEntity();
        menuEntity.setApplicationId(applicationEntity.getId());
        menuEntity.setParentId("");
        menuEntity.setCode("test");
        menuEntity.setName("测试菜单");
        menuEntity.setIcon("icon");
        menuEntity.setType(MenuType.BACKEND.getValue());
        menuEntity.setEnabled(Boolean.TRUE);
        menuEntity.setOrder(0);
        menuEntity.setRemark("菜单");
        menuEntity.setTenantCode("master");
        menuEntity.updateCreator(properties.getSupervisor().getUsername());
        this.menuMapper.insert(menuEntity);

        var permissionEntity = new PermissionEntity();
        permissionEntity.setApplicationId(applicationEntity.getId());
        permissionEntity.setMenuId(menuEntity.getId());
        permissionEntity.setCode("test-add");
        permissionEntity.setName("添加");
        permissionEntity.setTenantCode("master");
        permissionEntity.updateCreator(properties.getSupervisor().getUsername());
        this.permissionMapper.insert(permissionEntity);

        // 查询数据
        var permissions = this.provider.findBy(null, null, Conditions.of(Permission.class).eq(Permission::getCode, "test-add"), null, "master");
        assertNotNull(permissions);
        assertEquals(1, permissions.size());

        var permission = Listx.getFirstOrNull(permissions);
        assertNotNull(permission);
        assertEquals(permissionEntity.getId(), permission.getId());
        // 关联查询
        assertNotNull(permission.getApplication());
        assertEquals(applicationEntity.getId(), permission.getApplication().getId());
        // 关联查询
        assertNotNull(permission.getMenu());
        assertEquals(menuEntity.getId(), permission.getMenu().getId());
        // 关联查询
        assertNotNull(permission.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), permission.getCreator().getId());
        assertNotNull(permission.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), permission.getModifier().getId());
    }

    /**
     * @see PermissionProvider#pageBy
     */
    @Test
    public void case4() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var menuEntity = new MenuEntity();
        menuEntity.setApplicationId(applicationEntity.getId());
        menuEntity.setParentId("");
        menuEntity.setCode("test");
        menuEntity.setName("测试菜单");
        menuEntity.setIcon("icon");
        menuEntity.setType(MenuType.BACKEND.getValue());
        menuEntity.setEnabled(Boolean.TRUE);
        menuEntity.setOrder(0);
        menuEntity.setRemark("菜单");
        menuEntity.setTenantCode("master");
        menuEntity.updateCreator(properties.getSupervisor().getUsername());
        this.menuMapper.insert(menuEntity);

        var permissionEntity = new PermissionEntity();
        permissionEntity.setApplicationId(applicationEntity.getId());
        permissionEntity.setMenuId(menuEntity.getId());
        permissionEntity.setCode("test-add");
        permissionEntity.setName("添加");
        permissionEntity.setTenantCode("master");
        permissionEntity.updateCreator(properties.getSupervisor().getUsername());
        this.permissionMapper.insert(permissionEntity);

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(Permission.class).eq(Permission::getCode, "test-add"), null, "master");
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1, page.getPager().getPageIndex());
        assertEquals(20, page.getPager().getPageSize());
        assertEquals(1, page.getPager().getPageCount());
        assertEquals(1, page.getPager().getItemCount());

        var permission = Listx.getFirstOrNull(page.getData());
        assertNotNull(permission);
        assertEquals(permissionEntity.getId(), permission.getId());
        // 关联查询
        assertNotNull(permission.getApplication());
        assertEquals(applicationEntity.getId(), permission.getApplication().getId());
        // 关联查询
        assertNotNull(permission.getMenu());
        assertEquals(menuEntity.getId(), permission.getMenu().getId());
        // 关联查询
        assertNotNull(permission.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), permission.getCreator().getId());
        assertNotNull(permission.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), permission.getModifier().getId());
    }

    /**
     * @see PermissionProvider#countBy
     */
    @Test
    public void case5() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var menuEntity = new MenuEntity();
        menuEntity.setApplicationId(applicationEntity.getId());
        menuEntity.setParentId("");
        menuEntity.setCode("test");
        menuEntity.setName("测试菜单");
        menuEntity.setIcon("icon");
        menuEntity.setType(MenuType.BACKEND.getValue());
        menuEntity.setEnabled(Boolean.TRUE);
        menuEntity.setOrder(0);
        menuEntity.setRemark("菜单");
        menuEntity.setTenantCode("master");
        menuEntity.updateCreator(properties.getSupervisor().getUsername());
        this.menuMapper.insert(menuEntity);

        var permissionEntity = new PermissionEntity();
        permissionEntity.setApplicationId(applicationEntity.getId());
        permissionEntity.setMenuId(menuEntity.getId());
        permissionEntity.setCode("test-add");
        permissionEntity.setName("添加");
        permissionEntity.setTenantCode("master");
        permissionEntity.updateCreator(properties.getSupervisor().getUsername());
        this.permissionMapper.insert(permissionEntity);

        // 查询数据
        var count = this.provider.countBy(Conditions.of(Permission.class).eq(Permission::getCode, "test-add"), "master");
        assertNotNull(count);
        assertEquals(1, count);
    }

    /**
     * @see PermissionProvider#insert
     */
    @Test
    public void case6() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var menuEntity = new MenuEntity();
        menuEntity.setApplicationId(applicationEntity.getId());
        menuEntity.setParentId("");
        menuEntity.setCode("test");
        menuEntity.setName("测试菜单");
        menuEntity.setIcon("icon");
        menuEntity.setType(MenuType.BACKEND.getValue());
        menuEntity.setEnabled(Boolean.TRUE);
        menuEntity.setOrder(0);
        menuEntity.setRemark("菜单");
        menuEntity.setTenantCode("master");
        menuEntity.updateCreator(properties.getSupervisor().getUsername());
        this.menuMapper.insert(menuEntity);

        var input = PermissionInput.builder()
                .applicationId(applicationEntity.getId())
                .menuId(menuEntity.getId())
                .code("test-add")
                .name("添加")
                .build();

        // 查询数据
        var permission = this.provider.insert(input, properties.getSupervisor().getUsername(), "master");

        assertNotNull(permission);
        assertNotNull(permission.getId());

        // 查询数据库
        assertTrue(this.permissionMapper.existsBy(Conditions.of(PermissionEntity.class).eq(PermissionEntity::getId, permission.getId())));
    }

    /**
     * @see PermissionProvider#insertBatch
     */
    @Test
    public void case7() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var menuEntity = new MenuEntity();
        menuEntity.setApplicationId(applicationEntity.getId());
        menuEntity.setParentId("");
        menuEntity.setCode("test");
        menuEntity.setName("测试菜单");
        menuEntity.setIcon("icon");
        menuEntity.setType(MenuType.BACKEND.getValue());
        menuEntity.setEnabled(Boolean.TRUE);
        menuEntity.setOrder(0);
        menuEntity.setRemark("菜单");
        menuEntity.setTenantCode("master");
        menuEntity.updateCreator(properties.getSupervisor().getUsername());
        this.menuMapper.insert(menuEntity);

        var input = PermissionInput.builder()
                .applicationId(applicationEntity.getId())
                .menuId(menuEntity.getId())
                .code("test-add")
                .name("添加")
                .build();

        // 查询数据
        var permissions = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername(), "master");
        assertNotNull(permissions);
        assertEquals(1, permissions.size());

        var permission = Listx.getFirstOrNull(permissions);
        assertNotNull(permission);
        assertNotNull(permission.getId());

        // 查询数据库
        assertTrue(this.permissionMapper.existsBy(Conditions.of(PermissionEntity.class).eq(PermissionEntity::getId, permission.getId())));
    }

    /**
     * @see PermissionProvider#update
     */
    @Test
    public void case8() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var menuEntity = new MenuEntity();
        menuEntity.setApplicationId(applicationEntity.getId());
        menuEntity.setParentId("");
        menuEntity.setCode("test");
        menuEntity.setName("测试菜单");
        menuEntity.setIcon("icon");
        menuEntity.setType(MenuType.BACKEND.getValue());
        menuEntity.setEnabled(Boolean.TRUE);
        menuEntity.setOrder(0);
        menuEntity.setRemark("菜单");
        menuEntity.setTenantCode("master");
        menuEntity.updateCreator(properties.getSupervisor().getUsername());
        this.menuMapper.insert(menuEntity);

        var permissionEntity = new PermissionEntity();
        permissionEntity.setApplicationId(applicationEntity.getId());
        permissionEntity.setMenuId(menuEntity.getId());
        permissionEntity.setCode("test-add");
        permissionEntity.setName("添加");
        permissionEntity.setTenantCode("master");
        permissionEntity.updateCreator(properties.getSupervisor().getUsername());
        this.permissionMapper.insert(permissionEntity);

        // 查询数据
        var permission = this.provider.findById(permissionEntity.getId(), "master");
        assertNotNull(permission);
        assertEquals(permissionEntity.getId(), permission.getId());

        var input = permission.toInput().toBuilder()
                .code("test-delete")
                .build();

        // 更新数据
        permission = this.provider.update(input, properties.getSupervisor().getUsername(), "master");
        assertNotNull(permission);
        assertEquals(permissionEntity.getId(), permission.getId());

        // 查询数据库
        assertTrue(this.permissionMapper.existsBy(Conditions.of(PermissionEntity.class).eq(PermissionEntity::getCode, "test-delete")));
    }

    /**
     * @see PermissionProvider#updateBatch
     */
    @Test
    public void case9() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var menuEntity = new MenuEntity();
        menuEntity.setApplicationId(applicationEntity.getId());
        menuEntity.setParentId("");
        menuEntity.setCode("test");
        menuEntity.setName("测试菜单");
        menuEntity.setIcon("icon");
        menuEntity.setType(MenuType.BACKEND.getValue());
        menuEntity.setEnabled(Boolean.TRUE);
        menuEntity.setOrder(0);
        menuEntity.setRemark("菜单");
        menuEntity.setTenantCode("master");
        menuEntity.updateCreator(properties.getSupervisor().getUsername());
        this.menuMapper.insert(menuEntity);

        var permissionEntity = new PermissionEntity();
        permissionEntity.setApplicationId(applicationEntity.getId());
        permissionEntity.setMenuId(menuEntity.getId());
        permissionEntity.setCode("test-add");
        permissionEntity.setName("添加");
        permissionEntity.setTenantCode("master");
        permissionEntity.updateCreator(properties.getSupervisor().getUsername());
        this.permissionMapper.insert(permissionEntity);

        // 查询数据
        var permission = this.provider.findById(permissionEntity.getId(), "master");
        assertNotNull(permission);
        assertEquals(permissionEntity.getId(), permission.getId());

        var input = permission.toInput().toBuilder()
                .code("test-delete")
                .build();

        // 更新数据
        var permissions = this.provider.updateBatch(Listx.of(input), properties.getSupervisor().getUsername(), "master");
        assertNotNull(permissions);
        assertEquals(1, permissions.size());

        permission = Listx.getFirstOrNull(permissions);
        assertNotNull(permission);
        assertEquals(permissionEntity.getId(), permission.getId());

        // 查询数据库
        assertTrue(this.permissionMapper.existsBy(Conditions.of(PermissionEntity.class).eq(PermissionEntity::getCode, "test-delete")));
    }

    /**
     * @see PermissionProvider#deleteByIds
     */
    @Test
    public void case10() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var menuEntity = new MenuEntity();
        menuEntity.setApplicationId(applicationEntity.getId());
        menuEntity.setParentId("");
        menuEntity.setCode("test");
        menuEntity.setName("测试菜单");
        menuEntity.setIcon("icon");
        menuEntity.setType(MenuType.BACKEND.getValue());
        menuEntity.setEnabled(Boolean.TRUE);
        menuEntity.setOrder(0);
        menuEntity.setRemark("菜单");
        menuEntity.setTenantCode("master");
        menuEntity.updateCreator(properties.getSupervisor().getUsername());
        this.menuMapper.insert(menuEntity);

        var permissionEntity = new PermissionEntity();
        permissionEntity.setApplicationId(applicationEntity.getId());
        permissionEntity.setMenuId(menuEntity.getId());
        permissionEntity.setCode("test-add");
        permissionEntity.setName("添加");
        permissionEntity.setTenantCode("master");
        permissionEntity.updateCreator(properties.getSupervisor().getUsername());
        this.permissionMapper.insert(permissionEntity);

        var deleted = this.provider.deleteByIds(List.of(permissionEntity.getId()), "master");
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.permissionMapper.existsBy(Conditions.of(PermissionEntity.class).eq(PermissionEntity::getId, permissionEntity.getId())));
    }

    /**
     * @see PermissionProvider#deleteBy(Conditions)
     */
    @Test
    public void case11() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证中心");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("用于所有应用的认证处理");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var menuEntity = new MenuEntity();
        menuEntity.setApplicationId(applicationEntity.getId());
        menuEntity.setParentId("");
        menuEntity.setCode("test");
        menuEntity.setName("测试菜单");
        menuEntity.setIcon("icon");
        menuEntity.setType(MenuType.BACKEND.getValue());
        menuEntity.setEnabled(Boolean.TRUE);
        menuEntity.setOrder(0);
        menuEntity.setRemark("菜单");
        menuEntity.setTenantCode("master");
        menuEntity.updateCreator(properties.getSupervisor().getUsername());
        this.menuMapper.insert(menuEntity);

        var permissionEntity = new PermissionEntity();
        permissionEntity.setApplicationId(applicationEntity.getId());
        permissionEntity.setMenuId(menuEntity.getId());
        permissionEntity.setCode("test-add");
        permissionEntity.setName("添加");
        permissionEntity.setTenantCode("master");
        permissionEntity.updateCreator(properties.getSupervisor().getUsername());
        this.permissionMapper.insert(permissionEntity);

        var deleted = this.provider.deleteBy(Conditions.of(Permission.class).eq(Permission::getCode, permissionEntity.getCode()), "master");
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.permissionMapper.existsBy(Conditions.of(PermissionEntity.class).eq(PermissionEntity::getId, permissionEntity.getId())));
    }
}
