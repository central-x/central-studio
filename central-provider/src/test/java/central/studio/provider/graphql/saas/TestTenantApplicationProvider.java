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

import central.data.saas.TenantApplication;
import central.data.saas.TenantApplicationInput;
import central.data.system.DatabaseProperties;
import central.provider.graphql.saas.TenantApplicationProvider;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.ProviderProperties;
import central.studio.provider.graphql.TestProvider;
import central.studio.provider.graphql.saas.entity.ApplicationEntity;
import central.studio.provider.graphql.saas.entity.TenantApplicationEntity;
import central.studio.provider.graphql.saas.entity.TenantEntity;
import central.studio.provider.graphql.saas.mapper.ApplicationMapper;
import central.studio.provider.graphql.saas.mapper.TenantApplicationMapper;
import central.studio.provider.graphql.saas.mapper.TenantMapper;
import central.studio.provider.graphql.system.entity.DatabaseEntity;
import central.studio.provider.graphql.system.mapper.DatabaseMapper;
import central.util.Guidx;
import central.util.Jsonx;
import central.util.Listx;
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
 * Tenant Application Provider Test Cases
 * 租户与应用关联关系
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestTenantApplicationProvider extends TestProvider {
    @Setter(onMethod_ = @Autowired)
    private TenantApplicationProvider provider;

    @Setter(onMethod_ = @Autowired)
    private ProviderProperties properties;

    @Setter(onMethod_ = @Autowired)
    private TenantMapper tenantMapper;

    @Setter(onMethod_ = @Autowired)
    private ApplicationMapper applicationMapper;

    @Setter(onMethod_ = @Autowired)
    private TenantApplicationMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private DatabaseMapper databaseMapper;

    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空数据
        mapper.deleteAll();
        tenantMapper.deleteAll();
        applicationMapper.deleteAll();
        databaseMapper.deleteAll();
    }

    private ApplicationEntity buildApplication() {
        var application = new ApplicationEntity();
        application.setCode("central-identity");
        application.setName("统一认证");
        application.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        application.setUrl("http://127.0.0.1:3110");
        application.setContextPath("/identity");
        application.setSecret(Guidx.nextID());
        application.setEnabled(Boolean.TRUE);
        application.setRemark("统一认证");
        application.updateCreator(properties.getSupervisor().getUsername());
        return application;
    }

    private DatabaseEntity buildDatabase(ApplicationEntity application) {
        var databaseEntity = new DatabaseEntity();
        databaseEntity.setApplicationId(application.getId());
        databaseEntity.setCode("test");
        databaseEntity.setName("测试数据库");
        databaseEntity.setType("mysql");
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
     * @see TenantApplicationProvider#findById
     */
    @Test
    public void case1() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var tenant = new TenantEntity();
        tenant.setCode("test");
        tenant.setName("测试租户");
        tenant.setDatabaseId(database.getId());
        tenant.setEnabled(Boolean.TRUE);
        tenant.setRemark("用于所有应用的认证处理");
        tenant.updateCreator(properties.getSupervisor().getUsername());
        this.tenantMapper.insert(tenant);

        var entity = new TenantApplicationEntity();
        entity.setTenantId(tenant.getId());
        entity.setApplicationId(application.getId());
        entity.setEnabled(Boolean.TRUE);
        entity.setPrimary(Boolean.TRUE);
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);


        // 查询数据
        var rel = this.provider.findById(entity.getId(), "master");
        assertNotNull(rel);
        assertEquals(entity.getId(), rel.getId());
        // 关联查询
        assertNotNull(rel.getTenant());
        assertEquals(tenant.getId(), rel.getTenant().getId());
        // 关联查询应用
        assertNotNull(rel.getApplication());
        assertEquals(application.getId(), rel.getApplication().getId());
        // 关联查询创建人与修改人
        assertNotNull(rel.getCreator());
        assertNotNull(rel.getModifier());
    }

    /**
     * @see TenantApplicationProvider#findByIds
     */
    @Test
    public void case2() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var tenant = new TenantEntity();
        tenant.setCode("test");
        tenant.setName("测试租户");
        tenant.setDatabaseId(database.getId());
        tenant.setEnabled(Boolean.TRUE);
        tenant.setRemark("用于所有应用的认证处理");
        tenant.updateCreator(properties.getSupervisor().getUsername());
        this.tenantMapper.insert(tenant);

        var entity = new TenantApplicationEntity();
        entity.setTenantId(tenant.getId());
        entity.setApplicationId(application.getId());
        entity.setEnabled(Boolean.TRUE);
        entity.setPrimary(Boolean.TRUE);
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);


        // 查询数据
        var rels = this.provider.findByIds(List.of(entity.getId()), "master");
        assertNotNull(rels);
        var rel = Listx.getFirstOrNull(rels);
        assertNotNull(rel);
        assertEquals(entity.getId(), rel.getId());
        // 关联查询
        assertNotNull(rel.getTenant());
        assertEquals(tenant.getId(), rel.getTenant().getId());
        // 关联查询应用
        assertNotNull(rel.getApplication());
        assertEquals(application.getId(), rel.getApplication().getId());
        // 关联查询创建人与修改人
        assertNotNull(rel.getCreator());
        assertNotNull(rel.getModifier());
    }

    /**
     * @see TenantApplicationProvider#findBy
     */
    @Test
    public void case3() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var tenant = new TenantEntity();
        tenant.setCode("test");
        tenant.setName("测试租户");
        tenant.setDatabaseId(database.getId());
        tenant.setEnabled(Boolean.TRUE);
        tenant.setRemark("用于所有应用的认证处理");
        tenant.updateCreator(properties.getSupervisor().getUsername());
        this.tenantMapper.insert(tenant);

        var entity = new TenantApplicationEntity();
        entity.setTenantId(tenant.getId());
        entity.setApplicationId(application.getId());
        entity.setEnabled(Boolean.TRUE);
        entity.setPrimary(Boolean.TRUE);
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);


        // 查询数据
        var rels = this.provider.findBy(null, null, Conditions.of(TenantApplication.class).eq(TenantApplication::getId, entity.getId()), null, "master");
        assertNotNull(rels);
        var rel = Listx.getFirstOrNull(rels);
        assertNotNull(rel);
        assertEquals(entity.getId(), rel.getId());
        // 关联查询
        assertNotNull(rel.getTenant());
        assertEquals(tenant.getId(), rel.getTenant().getId());
        // 关联查询应用
        assertNotNull(rel.getApplication());
        assertEquals(application.getId(), rel.getApplication().getId());
        // 关联查询创建人与修改人
        assertNotNull(rel.getCreator());
        assertNotNull(rel.getModifier());
    }

    /**
     * @see TenantApplicationProvider#pageBy
     */
    @Test
    public void case4() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var tenant = new TenantEntity();
        tenant.setCode("test");
        tenant.setName("测试租户");
        tenant.setDatabaseId(database.getId());
        tenant.setEnabled(Boolean.TRUE);
        tenant.setRemark("用于所有应用的认证处理");
        tenant.updateCreator(properties.getSupervisor().getUsername());
        this.tenantMapper.insert(tenant);

        var entity = new TenantApplicationEntity();
        entity.setTenantId(tenant.getId());
        entity.setApplicationId(application.getId());
        entity.setEnabled(Boolean.TRUE);
        entity.setPrimary(Boolean.TRUE);
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(TenantApplication.class).eq(TenantApplication::getId, entity.getId()), null, "master");
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertNotNull(page.getData());
        assertTrue(page.getData().stream().anyMatch(it -> Objects.equals(entity.getId(), it.getId())));
    }

    /**
     * @see TenantApplicationProvider#countBy
     */
    @Test
    public void case5() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var tenant = new TenantEntity();
        tenant.setCode("test");
        tenant.setName("测试租户");
        tenant.setDatabaseId(database.getId());
        tenant.setEnabled(Boolean.TRUE);
        tenant.setRemark("用于所有应用的认证处理");
        tenant.updateCreator(properties.getSupervisor().getUsername());
        this.tenantMapper.insert(tenant);

        var entity = new TenantApplicationEntity();
        entity.setTenantId(tenant.getId());
        entity.setApplicationId(application.getId());
        entity.setEnabled(Boolean.TRUE);
        entity.setPrimary(Boolean.TRUE);
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var count = this.provider.countBy(Conditions.of(TenantApplication.class).eq(TenantApplication::getId, entity.getId()), "master");
        assertEquals(1L, count);
    }

    /**
     * @see TenantApplicationProvider#insert
     */
    @Test
    public void case6() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var tenant = new TenantEntity();
        tenant.setCode("test");
        tenant.setName("测试租户");
        tenant.setDatabaseId(database.getId());
        tenant.setEnabled(Boolean.TRUE);
        tenant.setRemark("用于所有应用的认证处理");
        tenant.updateCreator(properties.getSupervisor().getUsername());
        this.tenantMapper.insert(tenant);


        var input = TenantApplicationInput.builder()
                .tenantId(tenant.getId())
                .applicationId(application.getId())
                .enabled(Boolean.TRUE)
                .primary(Boolean.TRUE)
                .build();

        var tenantApplication = this.provider.insert(input, properties.getSupervisor().getUsername(), "master");

        assertNotNull(tenantApplication);

        assertTrue(this.mapper.existsBy(Conditions.of(TenantApplicationEntity.class).eq(TenantApplicationEntity::getId, tenantApplication.getId())));
    }

    /**
     * @see TenantApplicationProvider#insertBatch
     */
    @Test
    public void case7() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var tenant = new TenantEntity();
        tenant.setCode("test");
        tenant.setName("测试租户");
        tenant.setDatabaseId(database.getId());
        tenant.setEnabled(Boolean.TRUE);
        tenant.setRemark("用于所有应用的认证处理");
        tenant.updateCreator(properties.getSupervisor().getUsername());
        this.tenantMapper.insert(tenant);


        var input = TenantApplicationInput.builder()
                .tenantId(tenant.getId())
                .applicationId(application.getId())
                .enabled(Boolean.TRUE)
                .primary(Boolean.TRUE)
                .build();

        var tenantApplications = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername(), "master");

        assertNotNull(tenantApplications);
        assertEquals(1, tenantApplications.size());

        assertTrue(this.mapper.existsBy(Conditions.of(TenantApplicationEntity.class).eq(TenantApplicationEntity::getId, tenantApplications.get(0).getId())));
    }

    /**
     * @see TenantApplicationProvider#update
     */
    @Test
    public void case8() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var tenant = new TenantEntity();
        tenant.setCode("test");
        tenant.setName("测试租户");
        tenant.setDatabaseId(database.getId());
        tenant.setEnabled(Boolean.TRUE);
        tenant.setRemark("用于所有应用的认证处理");
        tenant.updateCreator(properties.getSupervisor().getUsername());
        this.tenantMapper.insert(tenant);

        var entity = new TenantApplicationEntity();
        entity.setTenantId(tenant.getId());
        entity.setApplicationId(application.getId());
        entity.setEnabled(Boolean.TRUE);
        entity.setPrimary(Boolean.TRUE);
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var tenantApplications = this.provider.findById(entity.getId(), "master");
        assertNotNull(tenantApplications);

        var input = tenantApplications.toInput().toBuilder()
                .enabled(Boolean.FALSE)
                .build();
        tenantApplications = this.provider.update(input, properties.getSupervisor().getUsername(), "master");
        assertNotNull(tenantApplications);

        assertTrue(this.mapper.existsBy(Conditions.of(TenantApplicationEntity.class).eq(TenantApplicationEntity::getEnabled, Boolean.FALSE)));
    }

    /**
     * @see TenantApplicationProvider#updateBatch
     */
    @Test
    public void case9() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var tenant = new TenantEntity();
        tenant.setCode("test");
        tenant.setName("测试租户");
        tenant.setDatabaseId(database.getId());
        tenant.setEnabled(Boolean.TRUE);
        tenant.setRemark("用于所有应用的认证处理");
        tenant.updateCreator(properties.getSupervisor().getUsername());
        this.tenantMapper.insert(tenant);

        var entity = new TenantApplicationEntity();
        entity.setTenantId(tenant.getId());
        entity.setApplicationId(application.getId());
        entity.setEnabled(Boolean.TRUE);
        entity.setPrimary(Boolean.TRUE);
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var tenantApplications = this.provider.findById(entity.getId(), "master");
        assertNotNull(tenantApplications);

        var input = tenantApplications.toInput().toBuilder()
                .enabled(Boolean.FALSE)
                .build();
        var updated = this.provider.updateBatch(List.of(input), properties.getSupervisor().getUsername(), "master");
        assertNotNull(updated);
        assertEquals(1, updated.size());

        assertTrue(this.mapper.existsBy(Conditions.of(TenantApplicationEntity.class).eq(TenantApplicationEntity::getEnabled, Boolean.FALSE)));
    }

    /**
     * @see TenantApplicationProvider#deleteByIds
     */
    @Test
    public void case10() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var tenant = new TenantEntity();
        tenant.setCode("test");
        tenant.setName("测试租户");
        tenant.setDatabaseId(database.getId());
        tenant.setEnabled(Boolean.TRUE);
        tenant.setRemark("用于所有应用的认证处理");
        tenant.updateCreator(properties.getSupervisor().getUsername());
        this.tenantMapper.insert(tenant);

        var entity = new TenantApplicationEntity();
        entity.setTenantId(tenant.getId());
        entity.setApplicationId(application.getId());
        entity.setEnabled(Boolean.TRUE);
        entity.setPrimary(Boolean.TRUE);
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var deleted = this.provider.deleteByIds(List.of(entity.getId()), "master");
        assertEquals(1, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(TenantApplicationEntity.class).eq(TenantApplicationEntity::getId, entity.getId())));
    }

    /**
     * @see TenantApplicationProvider#deleteBy
     */
    @Test
    public void case11() {
        var application = this.buildApplication();
        this.applicationMapper.insert(application);

        var database = this.buildDatabase(application);
        this.databaseMapper.insert(database);

        var tenant = new TenantEntity();
        tenant.setCode("test");
        tenant.setName("测试租户");
        tenant.setDatabaseId(database.getId());
        tenant.setEnabled(Boolean.TRUE);
        tenant.setRemark("用于所有应用的认证处理");
        tenant.updateCreator(properties.getSupervisor().getUsername());
        this.tenantMapper.insert(tenant);

        var entity = new TenantApplicationEntity();
        entity.setTenantId(tenant.getId());
        entity.setApplicationId(application.getId());
        entity.setEnabled(Boolean.TRUE);
        entity.setPrimary(Boolean.TRUE);
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var deleted = this.provider.deleteBy(Conditions.of(TenantApplication.class).eq(TenantApplication::getTenantId, tenant.getId()), "master");
        assertEquals(1, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(TenantApplicationEntity.class).eq(TenantApplicationEntity::getId, entity.getId())));
    }
}
