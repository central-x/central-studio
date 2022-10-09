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

package central.provider.graphql.ten;

import central.api.provider.ten.TenantApplicationProvider;
import central.api.provider.ten.TenantProvider;
import central.data.ten.Tenant;
import central.data.ten.TenantApplication;
import central.data.ten.TenantApplicationInput;
import central.data.ten.TenantInput;
import central.provider.ApplicationProperties;
import central.provider.ProviderApplication;
import central.provider.graphql.TestProvider;
import central.provider.graphql.sys.entity.DatabaseEntity;
import central.provider.graphql.sys.mapper.DatabaseMapper;
import central.provider.graphql.ten.entity.ApplicationEntity;
import central.provider.graphql.ten.entity.TenantApplicationEntity;
import central.provider.graphql.ten.entity.TenantEntity;
import central.provider.graphql.ten.mapper.ApplicationMapper;
import central.provider.graphql.ten.mapper.TenantApplicationMapper;
import central.provider.graphql.ten.mapper.TenantMapper;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    private ApplicationProperties properties;

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

    /**
     * @see TenantProvider#findById
     */
    @Test
    public void case1() {
        var database = new DatabaseEntity();
        database.setCode("test");
        database.setName("测试数据库");
        database.setType("mysql");
        database.setEnabled(Boolean.TRUE);
        database.setRemark("测试用的数据库");
        database.setTenantCode("master");
        database.updateCreator(properties.getSupervisor().getUsername());
        this.databaseMapper.insert(database);

        var application = new ApplicationEntity();
        application.setCode("central-security");
        application.setName("统一认证");
        application.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        application.setUrl("http://127.0.0.1:3110");
        application.setContextPath("/security");
        application.setKey(Guidx.nextID());
        application.setEnabled(Boolean.TRUE);
        application.setRemark("统一认证");
        application.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(application);

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
        var rel = this.provider.findById(entity.getId());
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
     * @see TenantProvider#findByIds
     */
    @Test
    public void case2() {
        var database = new DatabaseEntity();
        database.setCode("test");
        database.setName("测试数据库");
        database.setType("mysql");
        database.setEnabled(Boolean.TRUE);
        database.setRemark("测试用的数据库");
        database.setTenantCode("master");
        database.updateCreator(properties.getSupervisor().getUsername());
        this.databaseMapper.insert(database);

        var application = new ApplicationEntity();
        application.setCode("central-security");
        application.setName("统一认证");
        application.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        application.setUrl("http://127.0.0.1:3110");
        application.setContextPath("/security");
        application.setKey(Guidx.nextID());
        application.setEnabled(Boolean.TRUE);
        application.setRemark("统一认证");
        application.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(application);

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
        var rels = this.provider.findByIds(List.of(entity.getId()));
        assertNotNull(rels);
        var rel = Listx.getFirst(rels);
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
     * @see TenantProvider#findBy
     */
    @Test
    public void case3() {
        var database = new DatabaseEntity();
        database.setCode("test");
        database.setName("测试数据库");
        database.setType("mysql");
        database.setEnabled(Boolean.TRUE);
        database.setRemark("测试用的数据库");
        database.setTenantCode("master");
        database.updateCreator(properties.getSupervisor().getUsername());
        this.databaseMapper.insert(database);

        var application = new ApplicationEntity();
        application.setCode("central-security");
        application.setName("统一认证");
        application.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        application.setUrl("http://127.0.0.1:3110");
        application.setContextPath("/security");
        application.setKey(Guidx.nextID());
        application.setEnabled(Boolean.TRUE);
        application.setRemark("统一认证");
        application.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(application);

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
        var rels = this.provider.findBy(null, null, Conditions.of(TenantApplication.class).eq(TenantApplication::getId, entity.getId()), null);
        assertNotNull(rels);
        var rel = Listx.getFirst(rels);
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
     * @see TenantProvider#pageBy
     */
    @Test
    public void case4() {
        var database = new DatabaseEntity();
        database.setCode("test");
        database.setName("测试数据库");
        database.setType("mysql");
        database.setEnabled(Boolean.TRUE);
        database.setRemark("测试用的数据库");
        database.setTenantCode("master");
        database.updateCreator(properties.getSupervisor().getUsername());
        this.databaseMapper.insert(database);

        var application = new ApplicationEntity();
        application.setCode("central-security");
        application.setName("统一认证");
        application.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        application.setUrl("http://127.0.0.1:3110");
        application.setContextPath("/security");
        application.setKey(Guidx.nextID());
        application.setEnabled(Boolean.TRUE);
        application.setRemark("统一认证");
        application.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(application);

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
        var page = this.provider.pageBy(1L, 20L, Conditions.of(TenantApplication.class).eq(TenantApplication::getId, entity.getId()), null);
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
        var database = new DatabaseEntity();
        database.setCode("test");
        database.setName("测试数据库");
        database.setType("mysql");
        database.setEnabled(Boolean.TRUE);
        database.setRemark("测试用的数据库");
        database.setTenantCode("master");
        database.updateCreator(properties.getSupervisor().getUsername());
        this.databaseMapper.insert(database);

        var application = new ApplicationEntity();
        application.setCode("central-security");
        application.setName("统一认证");
        application.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        application.setUrl("http://127.0.0.1:3110");
        application.setContextPath("/security");
        application.setKey(Guidx.nextID());
        application.setEnabled(Boolean.TRUE);
        application.setRemark("统一认证");
        application.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(application);

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
        var count = this.provider.countBy(Conditions.of(TenantApplication.class).eq(TenantApplication::getId, entity.getId()));
        assertEquals(1L, count);
    }

    /**
     * @see TenantProvider#insert
     */
    @Test
    public void case6() {
        var database = new DatabaseEntity();
        database.setCode("test");
        database.setName("测试数据库");
        database.setType("mysql");
        database.setEnabled(Boolean.TRUE);
        database.setRemark("测试用的数据库");
        database.setTenantCode("master");
        database.updateCreator(properties.getSupervisor().getUsername());
        this.databaseMapper.insert(database);

        var application = new ApplicationEntity();
        application.setCode("central-security");
        application.setName("统一认证");
        application.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        application.setUrl("http://127.0.0.1:3110");
        application.setContextPath("/security");
        application.setKey(Guidx.nextID());
        application.setEnabled(Boolean.TRUE);
        application.setRemark("统一认证");
        application.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(application);

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

        var tenantApplication = this.provider.insert(input, properties.getSupervisor().getUsername());

        assertNotNull(tenantApplication);

        assertTrue(this.mapper.existsBy(Conditions.of(TenantApplicationEntity.class).eq(TenantApplicationEntity::getId, tenantApplication.getId())));
    }

    /**
     * @see TenantProvider#insertBatch
     */
    @Test
    public void case7() {
        var database = new DatabaseEntity();
        database.setCode("test");
        database.setName("测试数据库");
        database.setType("mysql");
        database.setEnabled(Boolean.TRUE);
        database.setRemark("测试用的数据库");
        database.setTenantCode("master");
        database.updateCreator(properties.getSupervisor().getUsername());
        this.databaseMapper.insert(database);

        var application = new ApplicationEntity();
        application.setCode("central-security");
        application.setName("统一认证");
        application.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        application.setUrl("http://127.0.0.1:3110");
        application.setContextPath("/security");
        application.setKey(Guidx.nextID());
        application.setEnabled(Boolean.TRUE);
        application.setRemark("统一认证");
        application.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(application);

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

        var tenantApplications = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername());

        assertNotNull(tenantApplications);
        assertEquals(1, tenantApplications.size());

        assertTrue(this.mapper.existsBy(Conditions.of(TenantApplicationEntity.class).eq(TenantApplicationEntity::getId, tenantApplications.get(0).getId())));
    }

    /**
     * @see TenantProvider#update
     */
    @Test
    public void case8() {
        var database = new DatabaseEntity();
        database.setCode("test");
        database.setName("测试数据库");
        database.setType("mysql");
        database.setEnabled(Boolean.TRUE);
        database.setRemark("测试用的数据库");
        database.setTenantCode("master");
        database.updateCreator(properties.getSupervisor().getUsername());
        this.databaseMapper.insert(database);

        var application = new ApplicationEntity();
        application.setCode("central-security");
        application.setName("统一认证");
        application.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        application.setUrl("http://127.0.0.1:3110");
        application.setContextPath("/security");
        application.setKey(Guidx.nextID());
        application.setEnabled(Boolean.TRUE);
        application.setRemark("统一认证");
        application.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(application);

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

        var tenantApplications = this.provider.findById(entity.getId());
        assertNotNull(tenantApplications);

        var input = tenantApplications.toInput().toBuilder()
                .enabled(Boolean.FALSE)
                .build();
        tenantApplications = this.provider.update(input, properties.getSupervisor().getUsername());
        assertNotNull(tenantApplications);

        assertTrue(this.mapper.existsBy(Conditions.of(TenantApplicationEntity.class).eq(TenantApplicationEntity::getEnabled, Boolean.FALSE)));
    }

    /**
     * @see TenantProvider#updateBatch
     */
    @Test
    public void case9() {
        var database = new DatabaseEntity();
        database.setCode("test");
        database.setName("测试数据库");
        database.setType("mysql");
        database.setEnabled(Boolean.TRUE);
        database.setRemark("测试用的数据库");
        database.setTenantCode("master");
        database.updateCreator(properties.getSupervisor().getUsername());
        this.databaseMapper.insert(database);

        var application = new ApplicationEntity();
        application.setCode("central-security");
        application.setName("统一认证");
        application.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        application.setUrl("http://127.0.0.1:3110");
        application.setContextPath("/security");
        application.setKey(Guidx.nextID());
        application.setEnabled(Boolean.TRUE);
        application.setRemark("统一认证");
        application.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(application);

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

        var tenantApplications = this.provider.findById(entity.getId());
        assertNotNull(tenantApplications);

        var input = tenantApplications.toInput().toBuilder()
                .enabled(Boolean.FALSE)
                .build();
        var updated = this.provider.updateBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(updated);
        assertEquals(1, updated.size());

        assertTrue(this.mapper.existsBy(Conditions.of(TenantApplicationEntity.class).eq(TenantApplicationEntity::getEnabled, Boolean.FALSE)));
    }

    /**
     * @see TenantProvider#deleteByIds
     */
    @Test
    public void case10() {
        var database = new DatabaseEntity();
        database.setCode("test");
        database.setName("测试数据库");
        database.setType("mysql");
        database.setEnabled(Boolean.TRUE);
        database.setRemark("测试用的数据库");
        database.setTenantCode("master");
        database.updateCreator(properties.getSupervisor().getUsername());
        this.databaseMapper.insert(database);

        var application = new ApplicationEntity();
        application.setCode("central-security");
        application.setName("统一认证");
        application.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        application.setUrl("http://127.0.0.1:3110");
        application.setContextPath("/security");
        application.setKey(Guidx.nextID());
        application.setEnabled(Boolean.TRUE);
        application.setRemark("统一认证");
        application.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(application);

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

        var deleted = this.provider.deleteByIds(List.of(entity.getId()));
        assertEquals(1, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(TenantApplicationEntity.class).eq(TenantApplicationEntity::getId, entity.getId())));
    }

    /**
     * @see TenantProvider#deleteBy
     */
    @Test
    public void case11() {
        var database = new DatabaseEntity();
        database.setCode("test");
        database.setName("测试数据库");
        database.setType("mysql");
        database.setEnabled(Boolean.TRUE);
        database.setRemark("测试用的数据库");
        database.setTenantCode("master");
        database.updateCreator(properties.getSupervisor().getUsername());
        this.databaseMapper.insert(database);

        var application = new ApplicationEntity();
        application.setCode("central-security");
        application.setName("统一认证");
        application.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        application.setUrl("http://127.0.0.1:3110");
        application.setContextPath("/security");
        application.setKey(Guidx.nextID());
        application.setEnabled(Boolean.TRUE);
        application.setRemark("统一认证");
        application.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(application);

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

        var deleted = this.provider.deleteBy(Conditions.of(TenantApplication.class).eq(TenantApplication::getTenantId, tenant.getId()));
        assertEquals(1, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(TenantApplicationEntity.class).eq(TenantApplicationEntity::getId, entity.getId())));
    }
}
