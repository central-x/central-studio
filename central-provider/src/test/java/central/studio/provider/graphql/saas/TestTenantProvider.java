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

package central.studio.provider.graphql.saas;

import central.data.saas.Tenant;
import central.data.saas.TenantInput;
import central.data.system.DatabaseProperties;
import central.provider.graphql.saas.TenantProvider;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.ProviderProperties;
import central.studio.provider.database.core.DatabaseType;
import central.studio.provider.graphql.TestProvider;
import central.studio.provider.database.persistence.saas.entity.ApplicationEntity;
import central.studio.provider.database.persistence.saas.entity.TenantApplicationEntity;
import central.studio.provider.database.persistence.saas.entity.TenantEntity;
import central.studio.provider.database.persistence.saas.mapper.ApplicationMapper;
import central.studio.provider.database.persistence.saas.mapper.TenantApplicationMapper;
import central.studio.provider.database.persistence.saas.mapper.TenantMapper;
import central.studio.provider.database.persistence.system.entity.DatabaseEntity;
import central.studio.provider.database.persistence.system.mapper.DatabaseMapper;
import central.util.Guidx;
import central.util.Jsonx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tenant Provider Test Cases
 * 租户
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestTenantProvider extends TestProvider {
    @Setter(onMethod_ = @Autowired)
    private TenantProvider provider;

    @Setter(onMethod_ = @Autowired)
    private ProviderProperties properties;

    @Setter(onMethod_ = @Autowired)
    private TenantMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private ApplicationMapper applicationMapper;

    @Setter(onMethod_ = @Autowired)
    private TenantApplicationMapper relMapper;

    @Setter(onMethod_ = @Autowired)
    private DatabaseMapper databaseMapper;

    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空数据
        mapper.deleteAll();
        applicationMapper.deleteAll();
        relMapper.deleteAll();
        databaseMapper.deleteAll();
    }

    private ApplicationEntity buildApplication() {
        var application = new ApplicationEntity();
        application.setCode("central-security");
        application.setName("统一认证");
        application.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        application.setUrl("http://127.0.0.1:3110");
        application.setContextPath("/security");
        application.setSecret(Guidx.nextID());
        application.setEnabled(Boolean.TRUE);
        application.setRemark("统一认证");
        application.setRoutesJson("[]");
        application.updateCreator(properties.getSupervisor().getUsername());
        return application;
    }

    private DatabaseEntity buildDatabase(ApplicationEntity application) {
        var databaseEntity = new DatabaseEntity();
        databaseEntity.setApplicationId(application.getId());
        databaseEntity.setCode("test");
        databaseEntity.setName("测试数据库");
        databaseEntity.setType(DatabaseType.H2.getValue());
        databaseEntity.setEnabled(Boolean.TRUE);
        databaseEntity.setRemark("测试");
        databaseEntity.setMasterJson(Jsonx.Default().serialize(new DatabaseProperties("org.h2.Driver", "jdbc:h2:mem:central-provider", "centralx", "central.x")));
        databaseEntity.setSlavesJson(Jsonx.Default().serialize(List.of(
                new DatabaseProperties("org.h2.Driver", "jdbc:h2:mem:central-provider-slave1", "centralx", "central.x"),
                new DatabaseProperties("org.h2.Driver", "jdbc:h2:mem:central-provider-slave2", "centralx", "central.x")
        )));
        databaseEntity.setParams(Jsonx.Default().serialize(Map.of(
                "master", Map.of(
                        "name", "central-provider",
                        "memoryMode", "1",
                        "username", "centralx",
                        "password", "central.x"
                ),
                "slaves", List.of(
                        Map.of(
                                "name", "central-provider-slave1",
                                "memoryMode", "1",
                                "username", "centralx",
                                "password", "central.x"
                        ),
                        Map.of(
                                "name", "central-provider-slave2",
                                "memoryMode", "1",
                                "username", "centralx",
                                "password", "central.x"
                        )
                )
        )));
        databaseEntity.setTenantCode("master");
        databaseEntity.updateCreator(properties.getSupervisor().getUsername());
        return databaseEntity;
    }

    /**
     * @see TenantProvider#findById
     */
    @Test
    public void case1() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var entity = new TenantEntity();
        entity.setCode("test");
        entity.setName("测试租户");
        entity.setDatabaseId(database.getId());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var tenantApplication = new TenantApplicationEntity();
        tenantApplication.setTenantId(entity.getId());
        tenantApplication.setApplicationId(application.getId());
        tenantApplication.setEnabled(Boolean.TRUE);
        tenantApplication.setPrimary(Boolean.TRUE);
        tenantApplication.updateCreator(properties.getSupervisor().getUsername());
        this.relMapper.insert(tenantApplication);


        // 查询数据
        var tenant = this.provider.findById(entity.getId(), "master");
        assertNotNull(tenant);
        assertEquals(entity.getId(), tenant.getId());
        // 关联查询数据库
        assertNotNull(tenant.getDatabase());
        assertEquals(database.getId(), tenant.getDatabase().getId());
        // 关联查询应用
        assertNotNull(tenant.getApplications());
        assertEquals(1, tenant.getApplications().size());
        assertTrue(tenant.getApplications().stream().anyMatch(it -> Objects.equals(tenantApplication.getId(), it.getId())));
        // 关联查询创建人与修改人
        assertNotNull(tenant.getCreator());
        assertNotNull(tenant.getModifier());
    }

    /**
     * @see TenantProvider#findByIds
     */
    @Test
    public void case2() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var entity = new TenantEntity();
        entity.setCode("test");
        entity.setName("测试租户");
        entity.setDatabaseId(database.getId());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var tenantApplication = new TenantApplicationEntity();
        tenantApplication.setTenantId(entity.getId());
        tenantApplication.setApplicationId(application.getId());
        tenantApplication.setEnabled(Boolean.TRUE);
        tenantApplication.setPrimary(Boolean.TRUE);
        tenantApplication.updateCreator(properties.getSupervisor().getUsername());
        this.relMapper.insert(tenantApplication);


        // 查询数据
        var tenants = this.provider.findByIds(List.of(entity.getId()), "master");
        assertNotNull(tenants);
        assertEquals(1, tenants.size());
        assertTrue(tenants.stream().anyMatch(it -> Objects.equals(entity.getId(), it.getId())));
    }

    /**
     * @see TenantProvider#findBy
     */
    @Test
    public void case3() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var entity = new TenantEntity();
        entity.setCode("test");
        entity.setName("测试租户");
        entity.setDatabaseId(database.getId());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var tenantApplication = new TenantApplicationEntity();
        tenantApplication.setTenantId(entity.getId());
        tenantApplication.setApplicationId(application.getId());
        tenantApplication.setEnabled(Boolean.TRUE);
        tenantApplication.setPrimary(Boolean.TRUE);
        tenantApplication.updateCreator(properties.getSupervisor().getUsername());
        this.relMapper.insert(tenantApplication);


        // 查询数据
        var tenants = this.provider.findBy(null, null, Conditions.of(Tenant.class).eq(Tenant::getCode, "test"), null, "master");

        assertNotNull(tenants);
        assertEquals(1, tenants.size());
        assertTrue(tenants.stream().anyMatch(it -> Objects.equals(entity.getId(), it.getId())));
    }

    /**
     * @see TenantProvider#pageBy
     */
    @Test
    public void case4() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var entity = new TenantEntity();
        entity.setCode("test");
        entity.setName("测试租户");
        entity.setDatabaseId(database.getId());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var tenantApplication = new TenantApplicationEntity();
        tenantApplication.setTenantId(entity.getId());
        tenantApplication.setApplicationId(application.getId());
        tenantApplication.setEnabled(Boolean.TRUE);
        tenantApplication.setPrimary(Boolean.TRUE);
        tenantApplication.updateCreator(properties.getSupervisor().getUsername());
        this.relMapper.insert(tenantApplication);


        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(Tenant.class).eq(Tenant::getCode, "test"), null, "master");
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertNotNull(page.getData());
        assertTrue(page.getData().stream().anyMatch(it -> Objects.equals(entity.getId(), it.getId())));
    }

    /**
     * @see TenantProvider#countBy
     */
    @Test
    public void case5() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var entity = new TenantEntity();
        entity.setCode("test");
        entity.setName("测试租户");
        entity.setDatabaseId(database.getId());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var count = this.provider.countBy(Conditions.of(Tenant.class).eq(Tenant::getCode, "test"), "master");
        assertEquals(1L, count);
    }

    /**
     * @see TenantProvider#insert
     */
    @Test
    public void case6() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var input = TenantInput.builder()
                .code("test")
                .name("测试租户")
                .databaseId(database.getId())
                .enabled(Boolean.TRUE)
                .remark("用于所有应用的认证处理")
                .build();

        var tenant = this.provider.insert(input, properties.getSupervisor().getUsername(), "master");

        assertNotNull(tenant);
        assertNotNull(tenant.getId());

        assertTrue(this.mapper.existsBy(Conditions.of(TenantEntity.class).eq(TenantEntity::getId, tenant.getId())));
    }

    /**
     * @see TenantProvider#insertBatch
     */
    @Test
    public void case7() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var input = TenantInput.builder()
                .code("test")
                .name("测试租户")
                .databaseId(database.getId())
                .enabled(Boolean.TRUE)
                .remark("用于所有应用的认证处理")
                .build();

        var tenants = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername(), "master");
        assertNotNull(tenants);
        assertEquals(1, tenants.size());

        assertTrue(this.mapper.existsBy(Conditions.of(TenantEntity.class).eq(TenantEntity::getId, tenants.get(0).getId())));
    }

    /**
     * @see TenantProvider#update
     */
    @Test
    public void case8() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var entity = new TenantEntity();
        entity.setCode("test");
        entity.setName("测试租户");
        entity.setDatabaseId(database.getId());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var tenant = this.provider.findById(entity.getId(), "master");
        assertNotNull(tenant);

        var input = tenant.toInput()
                .code("master")
                .build();
        tenant = this.provider.update(input, properties.getSupervisor().getUsername(), "master");
        assertNotNull(tenant);

        assertTrue(this.mapper.existsBy(Conditions.of(TenantEntity.class).eq(TenantEntity::getCode, input.getCode())));
    }

    /**
     * @see TenantProvider#updateBatch
     */
    @Test
    public void case9() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var entity = new TenantEntity();
        entity.setCode("test");
        entity.setName("测试租户");
        entity.setDatabaseId(database.getId());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var tenant = this.provider.findById(entity.getId(), "master");
        assertNotNull(tenant);

        var input = tenant.toInput()
                .code("master")
                .build();
        var updated = this.provider.updateBatch(List.of(input), properties.getSupervisor().getUsername(), "master");
        assertNotNull(updated);
        assertEquals(1, updated.size());

        assertTrue(this.mapper.existsBy(Conditions.of(TenantEntity.class).eq(TenantEntity::getCode, input.getCode())));
    }

    /**
     * @see TenantProvider#deleteByIds
     */
    @Test
    public void case10() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var entity = new TenantEntity();
        entity.setCode("test");
        entity.setName("测试租户");
        entity.setDatabaseId(database.getId());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var deleted = this.provider.deleteByIds(List.of(entity.getId()), "master");
        assertEquals(1, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(TenantEntity.class).eq(TenantEntity::getId, entity.getId())));
    }

    /**
     * @see TenantProvider#deleteBy
     */
    @Test
    public void case11() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var entity = new TenantEntity();
        entity.setCode("test");
        entity.setName("测试租户");
        entity.setDatabaseId(database.getId());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var deleted = this.provider.deleteBy(Conditions.of(Tenant.class).eq(Tenant::getId, entity.getId()), "master");
        assertEquals(1, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(TenantEntity.class).eq(TenantEntity::getId, entity.getId())));
    }
}
