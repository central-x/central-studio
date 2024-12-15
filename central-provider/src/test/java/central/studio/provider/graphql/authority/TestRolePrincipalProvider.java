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

import central.data.authority.RolePrincipal;
import central.data.authority.RolePrincipalInput;
import central.data.authority.option.MenuType;
import central.data.authority.option.PrincipalType;
import central.data.organization.option.AreaType;
import central.provider.graphql.authority.RolePrincipalProvider;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.ProviderProperties;
import central.studio.provider.graphql.authority.entity.MenuEntity;
import central.studio.provider.graphql.authority.entity.RoleEntity;
import central.studio.provider.graphql.authority.mapper.MenuMapper;
import central.studio.provider.graphql.authority.mapper.RoleMapper;
import central.studio.provider.graphql.authority.mapper.RolePrincipalMapper;
import central.studio.provider.graphql.organization.entity.AccountEntity;
import central.studio.provider.graphql.organization.entity.AreaEntity;
import central.studio.provider.graphql.organization.entity.DepartmentEntity;
import central.studio.provider.graphql.organization.entity.UnitEntity;
import central.studio.provider.graphql.organization.mapper.AccountMapper;
import central.studio.provider.graphql.organization.mapper.AreaMapper;
import central.studio.provider.graphql.organization.mapper.DepartmentMapper;
import central.studio.provider.graphql.organization.mapper.UnitMapper;
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
 * Role Principal Provider Test Cases
 *
 * @author Alan Yeh
 * @since 2024/12/15
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestRolePrincipalProvider {

    @Setter(onMethod_ = @Autowired)
    private ProviderProperties properties;

    @Setter(onMethod_ = @Autowired)
    private RolePrincipalProvider provider;

    @Setter(onMethod_ = @Autowired)
    private RolePrincipalMapper mapper;

    @BeforeAll
    public static void setup(@Autowired ProviderProperties properties,
                             @Autowired ApplicationMapper applicationMapper,
                             @Autowired MenuMapper menuMapper,
                             @Autowired RoleMapper roleMapper,
                             @Autowired AccountMapper accountMapper,
                             @Autowired DepartmentMapper departmentMapper,
                             @Autowired UnitMapper unitMapper,
                             @Autowired AreaMapper areaMapper) {
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

        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("test");
        areaEntity.setName("测试行政区划");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("test");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        unitMapper.insert(unitEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("test");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        departmentMapper.insert(departmentEntity);

        var accountEntity = new AccountEntity();
        accountEntity.setUsername("test");
        accountEntity.setEmail("test@central-x.com");
        accountEntity.setMobile("18888888888");
        accountEntity.setName("测试帐号");
        accountEntity.setAvatar("1234");
        accountEntity.setAdmin(Boolean.FALSE);
        accountEntity.setEnabled(Boolean.TRUE);
        accountEntity.setDeleted(Boolean.FALSE);
        accountEntity.setTenantCode("master");
        accountEntity.updateCreator(properties.getSupervisor().getUsername());
        accountMapper.insert(accountEntity);
    }

    @AfterAll
    public static void cleanup(@Autowired ApplicationMapper applicationMapper,
                               @Autowired MenuMapper menuMapper,
                               @Autowired RoleMapper roleMapper,
                               @Autowired AccountMapper accountMapper,
                               @Autowired DepartmentMapper departmentMapper,
                               @Autowired UnitMapper unitMapper,
                               @Autowired AreaMapper areaMapper) {
        applicationMapper.deleteBy(Conditions.of(ApplicationEntity.class).eq(ApplicationEntity::getCode, "test"));
        menuMapper.deleteBy(Conditions.of(MenuEntity.class).eq(MenuEntity::getCode, "test"));
        roleMapper.deleteBy(Conditions.of(RoleEntity.class).eq(RoleEntity::getCode, "test"));
        accountMapper.deleteBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getUsername, "test"));
        areaMapper.deleteBy(Conditions.of(AreaEntity.class).eq(AreaEntity::getCode, "test"));
        unitMapper.deleteBy(Conditions.of(UnitEntity.class).eq(UnitEntity::getCode, "test"));
        departmentMapper.deleteBy(Conditions.of(DepartmentEntity.class).eq(DepartmentEntity::getCode, "test"));
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
    private RoleMapper roleMapper;

    private RoleEntity getRole() {
        return roleMapper.findFirstBy(Conditions.of(RoleEntity.class).eq(RoleEntity::getCode, "test"));
    }

    @Setter(onMethod_ = @Autowired)
    private AccountMapper accountMapper;

    private AccountEntity getAccount(){
        return accountMapper.findFirstBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getUsername, "test"));
    }

    @Setter(onMethod_ = @Autowired)
    private UnitMapper unitMapper;

    private UnitEntity getUnit(){
        return unitMapper.findFirstBy(Conditions.of(UnitEntity.class).eq(UnitEntity::getCode, "test"));
    }

    @Setter(onMethod_ = @Autowired)
    private DepartmentMapper departmentMapper;

    private DepartmentEntity getDepartment(){
        return departmentMapper.findFirstBy(Conditions.of(DepartmentEntity.class).eq(DepartmentEntity::getCode, "test"));
    }

    /**
     * @see RolePrincipalProvider#insert
     * @see RolePrincipalProvider#findById
     * @see RolePrincipalProvider#countBy
     * @see RolePrincipalProvider#deleteByIds
     */
    @Test
    public void case1() {
        var application = this.getApplication();
        var role = this.getRole();
        var account = this.getAccount();

        var input = RolePrincipalInput.builder()
                .applicationId(application.getId())
                .roleId(role.getId())
                .principalId(account.getId())
                .type(PrincipalType.ACCOUNT.getValue())
                .build();

        var inserted = this.provider.insert(input, properties.getSupervisor().getUsername(), "master");
        assertNotNull(inserted);
        assertEquals(input.getApplicationId(), inserted.getApplicationId());
        assertEquals(input.getApplicationId(), inserted.getApplication().getId());
        assertEquals(input.getRoleId(), inserted.getRoleId());
        assertEquals(input.getRoleId(), inserted.getRole().getId());
        assertEquals(input.getPrincipalId(), inserted.getPrincipalId());
        assertNotNull(inserted.getAccount());
        assertEquals(input.getPrincipalId(), inserted.getAccount().getId());

        var found = this.provider.findById(inserted.getId(), "master");
        assertNotNull(found);
        assertEquals(input.getApplicationId(), found.getApplicationId());
        assertEquals(input.getApplicationId(), found.getApplication().getId());
        assertEquals(input.getRoleId(), found.getRoleId());
        assertEquals(input.getRoleId(), found.getRole().getId());
        assertEquals(input.getPrincipalId(), found.getPrincipalId());
        assertNotNull(found.getAccount());
        assertEquals(input.getPrincipalId(), found.getAccount().getId());

        var count = this.provider.countBy(Conditions.of(RolePrincipal.class).eq(RolePrincipal::getRoleId, role.getId()), "master");
        assertEquals(1, count);

        var deleted = this.provider.deleteByIds(List.of(inserted.getId()), "master");
        assertEquals(1, deleted);
    }

    /**
     * @see RolePrincipalProvider#insertBatch
     * @see RolePrincipalProvider#findByIds
     * @see RolePrincipalProvider#findBy
     * @see RolePrincipalProvider#pageBy
     * @see RolePrincipalProvider#deleteBy
     */
    @Test
    public void case2() {
        var application = this.getApplication();
        var role = this.getRole();
        var account = this.getAccount();

        var input = RolePrincipalInput.builder()
                .applicationId(application.getId())
                .roleId(role.getId())
                .principalId(account.getId())
                .type(PrincipalType.ACCOUNT.getValue())
                .build();

        // insertBatch
        var batch = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername(), "master");
        assertNotNull(batch);
        assertEquals(1, batch.size());
        var inserted = Listx.getFirstOrNull(batch);
        assertNotNull(inserted);
        assertEquals(input.getApplicationId(), inserted.getApplicationId());
        assertEquals(input.getApplicationId(), inserted.getApplication().getId());
        assertEquals(input.getRoleId(), inserted.getRoleId());
        assertEquals(input.getRoleId(), inserted.getRole().getId());
        assertEquals(input.getPrincipalId(), inserted.getPrincipalId());
        assertNotNull(inserted.getAccount());
        assertEquals(input.getPrincipalId(), inserted.getAccount().getId());

        // findByIds
        var found = this.provider.findByIds(List.of(inserted.getId()), "master");
        assertNotNull(found);
        assertEquals(1, found.size());
        inserted = Listx.getFirstOrNull(found);
        assertNotNull(inserted);
        assertEquals(input.getApplicationId(), inserted.getApplicationId());
        assertEquals(input.getApplicationId(), inserted.getApplication().getId());
        assertEquals(input.getRoleId(), inserted.getRoleId());
        assertEquals(input.getRoleId(), inserted.getRole().getId());
        assertEquals(input.getPrincipalId(), inserted.getPrincipalId());
        assertNotNull(inserted.getAccount());
        assertEquals(input.getPrincipalId(), inserted.getAccount().getId());

        // findBy
        var list = this.provider.findBy(null, null, Conditions.of(RolePrincipal.class).eq(RolePrincipal::getRoleId, role.getId()), null, "master");
        assertNotNull(list);
        assertEquals(1, list.size());
        inserted = Listx.getFirstOrNull(list);
        assertNotNull(inserted);
        assertEquals(input.getApplicationId(), inserted.getApplicationId());
        assertEquals(input.getApplicationId(), inserted.getApplication().getId());
        assertEquals(input.getRoleId(), inserted.getRoleId());
        assertEquals(input.getRoleId(), inserted.getRole().getId());
        assertEquals(input.getPrincipalId(), inserted.getPrincipalId());
        assertNotNull(inserted.getAccount());
        assertEquals(input.getPrincipalId(), inserted.getAccount().getId());

        // pageBy
        var page = this.provider.pageBy(1, 20, Conditions.of(RolePrincipal.class).eq(RolePrincipal::getRoleId, role.getId()), null, "master");
        assertNotNull(page);
        assertEquals(1, page.getPager().getPageIndex());
        assertEquals(20, page.getPager().getPageSize());
        assertEquals(1, page.getPager().getPageCount());
        assertEquals(1, page.getPager().getItemCount());
        inserted = Listx.getFirstOrNull(page.getData());
        assertNotNull(inserted);
        assertEquals(input.getApplicationId(), inserted.getApplicationId());
        assertEquals(input.getApplicationId(), inserted.getApplication().getId());
        assertEquals(input.getRoleId(), inserted.getRoleId());
        assertEquals(input.getRoleId(), inserted.getRole().getId());
        assertEquals(input.getPrincipalId(), inserted.getPrincipalId());
        assertNotNull(inserted.getAccount());
        assertEquals(input.getPrincipalId(), inserted.getAccount().getId());

        var deleted = this.provider.deleteBy(Conditions.of(RolePrincipal.class).eq(RolePrincipal::getRoleId, role.getId()), "master");
        assertEquals(1, deleted);
    }
}
