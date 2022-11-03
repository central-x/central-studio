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

package central.provider.graphql.authority;

import central.api.provider.authority.MenuProvider;
import central.data.authority.Menu;
import central.data.authority.MenuInput;
import central.data.authority.option.MenuType;
import central.provider.ApplicationProperties;
import central.provider.ProviderApplication;
import central.provider.graphql.TestProvider;
import central.provider.graphql.authority.entity.MenuEntity;
import central.provider.graphql.authority.mapper.MenuMapper;
import central.provider.graphql.authority.mapper.PermissionMapper;
import central.provider.graphql.saas.entity.ApplicationEntity;
import central.provider.graphql.saas.mapper.ApplicationMapper;
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
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MenuProvider Test Cases
 * 菜单
 *
 * @author Alan Yeh
 * @since 2022/09/27
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestMenuProvider extends TestProvider {

    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    @Setter(onMethod_ = @Autowired)
    private MenuProvider provider;

    @Setter(onMethod_ = @Autowired)
    private MenuMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private PermissionMapper permissionMapper;

    @Setter(onMethod_ = @Autowired)
    private ApplicationMapper applicationMapper;

    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空测试数据
        this.mapper.deleteAll();
        this.applicationMapper.deleteAll();
        this.permissionMapper.deleteAll();
    }

    /**
     * @see MenuProvider#findById
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

        var parentEntity = new MenuEntity();
        parentEntity.setApplicationId(applicationEntity.getId());
        parentEntity.setParentId("");
        parentEntity.setCode("test-parent");
        parentEntity.setName("测试父菜单");
        parentEntity.setIcon("icon-parent");
        parentEntity.setType(MenuType.BACKEND.getValue());
        parentEntity.setEnabled(Boolean.TRUE);
        parentEntity.setOrder(0);
        parentEntity.setRemark("父菜单");
        parentEntity.setTenantCode("master");
        parentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(parentEntity);

        var menuEntity = new MenuEntity();
        menuEntity.setApplicationId(applicationEntity.getId());
        menuEntity.setParentId(parentEntity.getId());
        menuEntity.setCode("test");
        menuEntity.setName("测试菜单");
        menuEntity.setIcon("icon");
        menuEntity.setType(MenuType.BACKEND.getValue());
        menuEntity.setEnabled(Boolean.TRUE);
        menuEntity.setOrder(0);
        menuEntity.setRemark("菜单");
        menuEntity.setTenantCode("master");
        menuEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(menuEntity);

        var childEntity = new MenuEntity();
        childEntity.setApplicationId(applicationEntity.getId());
        childEntity.setParentId(menuEntity.getId());
        childEntity.setCode("test-child");
        childEntity.setName("测试子菜单");
        childEntity.setIcon("icon-child");
        childEntity.setType(MenuType.BACKEND.getValue());
        childEntity.setEnabled(Boolean.TRUE);
        childEntity.setOrder(0);
        childEntity.setRemark("子菜单");
        childEntity.setTenantCode("master");
        childEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(childEntity);

        // 查询数据
        var menu = this.provider.findById(menuEntity.getId());
        assertNotNull(menu);
        assertEquals(menu.getId(), menu.getId());
        // 关联查询
        assertNotNull(menu.getApplication());
        assertEquals(applicationEntity.getId(), menu.getApplication().getId());
        // 关联查询
        assertNotNull(menu.getParent());
        assertEquals(parentEntity.getId(), menu.getParent().getId());
        // 关联查询
        assertNotNull(menu.getChildren());
        assertEquals(1, menu.getChildren().size());
        assertTrue(menu.getChildren().stream().allMatch(it -> Objects.equals(childEntity.getId(), it.getId())));
        // 关联查询
        assertNotNull(menu.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), menu.getCreator().getId());
        assertNotNull(menu.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), menu.getModifier().getId());
    }

    /**
     * @see MenuProvider#findByIds
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

        var parentEntity = new MenuEntity();
        parentEntity.setApplicationId(applicationEntity.getId());
        parentEntity.setParentId("");
        parentEntity.setCode("test-parent");
        parentEntity.setName("测试父菜单");
        parentEntity.setIcon("icon-parent");
        parentEntity.setType(MenuType.BACKEND.getValue());
        parentEntity.setEnabled(Boolean.TRUE);
        parentEntity.setOrder(0);
        parentEntity.setRemark("父菜单");
        parentEntity.setTenantCode("master");
        parentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(parentEntity);

        var menuEntity = new MenuEntity();
        menuEntity.setApplicationId(applicationEntity.getId());
        menuEntity.setParentId(parentEntity.getId());
        menuEntity.setCode("test");
        menuEntity.setName("测试菜单");
        menuEntity.setIcon("icon");
        menuEntity.setType(MenuType.BACKEND.getValue());
        menuEntity.setEnabled(Boolean.TRUE);
        menuEntity.setOrder(0);
        menuEntity.setRemark("菜单");
        menuEntity.setTenantCode("master");
        menuEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(menuEntity);

        var childEntity = new MenuEntity();
        childEntity.setApplicationId(applicationEntity.getId());
        childEntity.setParentId(menuEntity.getId());
        childEntity.setCode("test-child");
        childEntity.setName("测试子菜单");
        childEntity.setIcon("icon-child");
        childEntity.setType(MenuType.BACKEND.getValue());
        childEntity.setEnabled(Boolean.TRUE);
        childEntity.setOrder(0);
        childEntity.setRemark("子菜单");
        childEntity.setTenantCode("master");
        childEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(childEntity);

        // 查询数据
        var menus = this.provider.findByIds(List.of(menuEntity.getId()));
        assertNotNull(menus);
        assertEquals(1, menus.size());

        var menu = Listx.getFirstOrNull(menus);
        assertNotNull(menu);
        assertEquals(menu.getId(), menu.getId());
        // 关联查询
        assertNotNull(menu.getApplication());
        assertEquals(applicationEntity.getId(), menu.getApplication().getId());
        // 关联查询
        assertNotNull(menu.getParent());
        assertEquals(parentEntity.getId(), menu.getParent().getId());
        // 关联查询
        assertNotNull(menu.getChildren());
        assertEquals(1, menu.getChildren().size());
        assertTrue(menu.getChildren().stream().allMatch(it -> Objects.equals(childEntity.getId(), it.getId())));
        // 关联查询
        assertNotNull(menu.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), menu.getCreator().getId());
        assertNotNull(menu.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), menu.getModifier().getId());
    }

    /**
     * @see MenuProvider#findBy
     */
    @Test
    public void case3() {
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

        var parentEntity = new MenuEntity();
        parentEntity.setApplicationId(applicationEntity.getId());
        parentEntity.setParentId("");
        parentEntity.setCode("test-parent");
        parentEntity.setName("测试父菜单");
        parentEntity.setIcon("icon-parent");
        parentEntity.setType(MenuType.BACKEND.getValue());
        parentEntity.setEnabled(Boolean.TRUE);
        parentEntity.setOrder(0);
        parentEntity.setRemark("父菜单");
        parentEntity.setTenantCode("master");
        parentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(parentEntity);

        var menuEntity = new MenuEntity();
        menuEntity.setApplicationId(applicationEntity.getId());
        menuEntity.setParentId(parentEntity.getId());
        menuEntity.setCode("test");
        menuEntity.setName("测试菜单");
        menuEntity.setIcon("icon");
        menuEntity.setType(MenuType.BACKEND.getValue());
        menuEntity.setEnabled(Boolean.TRUE);
        menuEntity.setOrder(0);
        menuEntity.setRemark("菜单");
        menuEntity.setTenantCode("master");
        menuEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(menuEntity);

        var childEntity = new MenuEntity();
        childEntity.setApplicationId(applicationEntity.getId());
        childEntity.setParentId(menuEntity.getId());
        childEntity.setCode("test-child");
        childEntity.setName("测试子菜单");
        childEntity.setIcon("icon-child");
        childEntity.setType(MenuType.BACKEND.getValue());
        childEntity.setEnabled(Boolean.TRUE);
        childEntity.setOrder(0);
        childEntity.setRemark("子菜单");
        childEntity.setTenantCode("master");
        childEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(childEntity);

        // 查询数据
        var menus = this.provider.findBy(null, null, Conditions.of(Menu.class).eq("application.code", applicationEntity.getCode()), null);
        assertNotNull(menus);
        assertEquals(3, menus.size());

        var menu = menus.stream().filter(it -> Objects.equals(menuEntity.getId(), it.getId())).findFirst().orElse(null);
        assertNotNull(menu);
        assertEquals(menu.getId(), menu.getId());
        // 关联查询
        assertNotNull(menu.getApplication());
        assertEquals(applicationEntity.getId(), menu.getApplication().getId());
        // 关联查询
        assertNotNull(menu.getParent());
        assertEquals(parentEntity.getId(), menu.getParent().getId());
        // 关联查询
        assertNotNull(menu.getChildren());
        assertEquals(1, menu.getChildren().size());
        assertTrue(menu.getChildren().stream().allMatch(it -> Objects.equals(childEntity.getId(), it.getId())));
        // 关联查询
        assertNotNull(menu.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), menu.getCreator().getId());
        assertNotNull(menu.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), menu.getModifier().getId());
    }

    /**
     * @see MenuProvider#pageBy
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

        var parentEntity = new MenuEntity();
        parentEntity.setApplicationId(applicationEntity.getId());
        parentEntity.setParentId("");
        parentEntity.setCode("test-parent");
        parentEntity.setName("测试父菜单");
        parentEntity.setIcon("icon-parent");
        parentEntity.setType(MenuType.BACKEND.getValue());
        parentEntity.setEnabled(Boolean.TRUE);
        parentEntity.setOrder(0);
        parentEntity.setRemark("父菜单");
        parentEntity.setTenantCode("master");
        parentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(parentEntity);

        var menuEntity = new MenuEntity();
        menuEntity.setApplicationId(applicationEntity.getId());
        menuEntity.setParentId(parentEntity.getId());
        menuEntity.setCode("test");
        menuEntity.setName("测试菜单");
        menuEntity.setIcon("icon");
        menuEntity.setType(MenuType.BACKEND.getValue());
        menuEntity.setEnabled(Boolean.TRUE);
        menuEntity.setOrder(0);
        menuEntity.setRemark("菜单");
        menuEntity.setTenantCode("master");
        menuEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(menuEntity);

        var childEntity = new MenuEntity();
        childEntity.setApplicationId(applicationEntity.getId());
        childEntity.setParentId(menuEntity.getId());
        childEntity.setCode("test-child");
        childEntity.setName("测试子菜单");
        childEntity.setIcon("icon-child");
        childEntity.setType(MenuType.BACKEND.getValue());
        childEntity.setEnabled(Boolean.TRUE);
        childEntity.setOrder(0);
        childEntity.setRemark("子菜单");
        childEntity.setTenantCode("master");
        childEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(childEntity);

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(Menu.class).eq("application.code", applicationEntity.getCode()), null);
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1, page.getPager().getPageIndex());
        assertEquals(20, page.getPager().getPageSize());
        assertEquals(1, page.getPager().getPageCount());
        assertEquals(3, page.getPager().getItemCount());

        var menu = page.getData().stream().filter(it -> Objects.equals(menuEntity.getId(), it.getId())).findFirst().orElse(null);
        assertNotNull(menu);
        assertEquals(menu.getId(), menu.getId());
        // 关联查询
        assertNotNull(menu.getApplication());
        assertEquals(applicationEntity.getId(), menu.getApplication().getId());
        // 关联查询
        assertNotNull(menu.getParent());
        assertEquals(parentEntity.getId(), menu.getParent().getId());
        // 关联查询
        assertNotNull(menu.getChildren());
        assertEquals(1, menu.getChildren().size());
        assertTrue(menu.getChildren().stream().allMatch(it -> Objects.equals(childEntity.getId(), it.getId())));
        // 关联查询
        assertNotNull(menu.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), menu.getCreator().getId());
        assertNotNull(menu.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), menu.getModifier().getId());
    }

    /**
     * @see MenuProvider#countBy
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

        var parentEntity = new MenuEntity();
        parentEntity.setApplicationId(applicationEntity.getId());
        parentEntity.setParentId("");
        parentEntity.setCode("test-parent");
        parentEntity.setName("测试父菜单");
        parentEntity.setIcon("icon-parent");
        parentEntity.setType(MenuType.BACKEND.getValue());
        parentEntity.setEnabled(Boolean.TRUE);
        parentEntity.setOrder(0);
        parentEntity.setRemark("父菜单");
        parentEntity.setTenantCode("master");
        parentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(parentEntity);

        var menuEntity = new MenuEntity();
        menuEntity.setApplicationId(applicationEntity.getId());
        menuEntity.setParentId(parentEntity.getId());
        menuEntity.setCode("test");
        menuEntity.setName("测试菜单");
        menuEntity.setIcon("icon");
        menuEntity.setType(MenuType.BACKEND.getValue());
        menuEntity.setEnabled(Boolean.TRUE);
        menuEntity.setOrder(0);
        menuEntity.setRemark("菜单");
        menuEntity.setTenantCode("master");
        menuEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(menuEntity);

        var childEntity = new MenuEntity();
        childEntity.setApplicationId(applicationEntity.getId());
        childEntity.setParentId(menuEntity.getId());
        childEntity.setCode("test-child");
        childEntity.setName("测试子菜单");
        childEntity.setIcon("icon-child");
        childEntity.setType(MenuType.BACKEND.getValue());
        childEntity.setEnabled(Boolean.TRUE);
        childEntity.setOrder(0);
        childEntity.setRemark("子菜单");
        childEntity.setTenantCode("master");
        childEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(childEntity);

        // 查询数据
        var count = this.provider.countBy(Conditions.of(Menu.class).eq("application.code", applicationEntity.getCode()));
        assertNotNull(count);
        assertEquals(3, count);
    }

    /**
     * @see MenuProvider#insert
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

        var input = MenuInput.builder()
                .applicationId(applicationEntity.getId())
                .parentId("")
                .code("test")
                .name("测试菜单")
                .icon("icon")
                .type(MenuType.BACKEND.getValue())
                .enabled(Boolean.TRUE)
                .order(0)
                .remark("菜单")
                .build();

        // 查询数据
        var menu = this.provider.insert(input, properties.getSupervisor().getUsername());

        assertNotNull(menu);
        assertNotNull(menu.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(MenuEntity.class).eq(MenuEntity::getId, menu.getId())));
    }

    /**
     * @see MenuProvider#insertBatch
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

        var input = MenuInput.builder()
                .applicationId(applicationEntity.getId())
                .parentId("")
                .code("test")
                .name("测试菜单")
                .icon("icon")
                .type(MenuType.BACKEND.getValue())
                .enabled(Boolean.TRUE)
                .order(0)
                .remark("菜单")
                .build();

        // 查询数据
        var menus = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(menus);
        assertEquals(1, menus.size());

        var menu = Listx.getFirstOrNull(menus);
        assertNotNull(menu);
        assertNotNull(menu.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(MenuEntity.class).eq(MenuEntity::getId, menu.getId())));
    }

    /**
     * @see MenuProvider#update
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
        this.mapper.insert(menuEntity);

        // 查询数据
        var menu = this.provider.findById(menuEntity.getId());
        assertNotNull(menu);
        assertEquals(menuEntity.getId(), menu.getId());

        var input = menu.toInput().toBuilder()
                .code("test1")
                .build();

        // 更新数据
        menu = this.provider.update(input, properties.getSupervisor().getUsername());
        assertNotNull(menu);
        assertEquals(menuEntity.getId(), menu.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(MenuEntity.class).eq(MenuEntity::getCode, "test1")));
    }

    /**
     * @see MenuProvider#updateBatch
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
        this.mapper.insert(menuEntity);

        // 查询数据
        var menu = this.provider.findById(menuEntity.getId());
        assertNotNull(menu);
        assertEquals(menuEntity.getId(), menu.getId());

        var input = menu.toInput().toBuilder()
                .code("test1")
                .build();

        // 更新数据
        var menus = this.provider.updateBatch(Listx.of(input), properties.getSupervisor().getUsername());
        assertNotNull(menus);
        assertEquals(1, menus.size());

        menu = Listx.getFirstOrNull(menus);
        assertNotNull(menu);
        assertEquals(menuEntity.getId(), menu.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(MenuEntity.class).eq(MenuEntity::getCode, "test1")));
    }

    /**
     * @see MenuProvider#deleteByIds
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
        this.mapper.insert(menuEntity);

        var deleted = this.provider.deleteByIds(List.of(menuEntity.getId()));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(MenuEntity.class).eq(MenuEntity::getId, menuEntity.getId())));
    }

    /**
     * @see MenuProvider#deleteBy(Conditions)
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
        this.mapper.insert(menuEntity);

        var deleted = this.provider.deleteBy(Conditions.of(Menu.class).eq(Menu::getCode, menuEntity.getCode()));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(MenuEntity.class).eq(MenuEntity::getId, menuEntity.getId())));
    }
}
