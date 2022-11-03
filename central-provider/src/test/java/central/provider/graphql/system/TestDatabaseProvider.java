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

package central.provider.graphql.system;

import central.api.provider.system.DatabaseProvider;
import central.data.system.Database;
import central.data.system.DatabaseInput;
import central.provider.ApplicationProperties;
import central.provider.ProviderApplication;
import central.provider.graphql.TestProvider;
import central.provider.graphql.system.entity.DatabaseEntity;
import central.provider.graphql.system.mapper.DatabaseMapper;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Database Provider Test Cases
 * 数据库
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestDatabaseProvider extends TestProvider {
    @Setter(onMethod_ = @Autowired)
    private DatabaseProvider provider;

    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    @Setter(onMethod_ = @Autowired)
    private DatabaseMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private ApplicationMapper applicationMapper;

    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空数据
        mapper.deleteAll();
        applicationMapper.deleteAll();
    }

    /**
     * @see DatabaseProvider#findById
     */
    @Test
    public void case1() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var databaseEntity = new DatabaseEntity();
        databaseEntity.setApplicationId(applicationEntity.getId());
        databaseEntity.setCode("test");
        databaseEntity.setName("测试数据库");
        databaseEntity.setType("mysql");
        databaseEntity.setEnabled(Boolean.TRUE);
        databaseEntity.setRemark("测试");
        databaseEntity.setTenantCode("master");
        databaseEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(databaseEntity);

        // 查询数据
        var database = this.provider.findById(databaseEntity.getId());
        assertNotNull(database);
        assertNotNull(database.getId());

        // 关联查询
        assertNotNull(database.getApplication());
        assertEquals(applicationEntity.getId(), database.getApplication().getId());

        // 关联查询
        assertNotNull(database.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), database.getCreator().getId());
        assertNotNull(database.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), database.getModifier().getId());
    }

    /**
     * @see DatabaseProvider#findByIds
     */
    @Test
    public void case2() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var databaseEntity = new DatabaseEntity();
        databaseEntity.setApplicationId(applicationEntity.getId());
        databaseEntity.setCode("test");
        databaseEntity.setName("测试数据库");
        databaseEntity.setType("mysql");
        databaseEntity.setEnabled(Boolean.TRUE);
        databaseEntity.setRemark("测试");
        databaseEntity.setTenantCode("master");
        databaseEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(databaseEntity);

        // 查询数据
        var databases = this.provider.findByIds(List.of(databaseEntity.getId()));
        assertNotNull(databases);
        assertEquals(1, databases.size());

        var database = Listx.getFirstOrNull(databases);

        assertNotNull(database);
        assertNotNull(database.getId());

        // 关联查询
        assertNotNull(database.getApplication());
        assertEquals(applicationEntity.getId(), database.getApplication().getId());

        // 关联查询
        assertNotNull(database.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), database.getCreator().getId());
        assertNotNull(database.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), database.getModifier().getId());
    }

    /**
     * @see DatabaseProvider#findBy
     */
    @Test
    public void case3() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var databaseEntity = new DatabaseEntity();
        databaseEntity.setApplicationId(applicationEntity.getId());
        databaseEntity.setCode("test");
        databaseEntity.setName("测试数据库");
        databaseEntity.setType("mysql");
        databaseEntity.setEnabled(Boolean.TRUE);
        databaseEntity.setRemark("测试");
        databaseEntity.setTenantCode("master");
        databaseEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(databaseEntity);

        // 查询数据
        var databases = this.provider.findBy(null, null, Conditions.of(Database.class).eq("application.code", "central-security"), null);
        assertNotNull(databases);
        assertEquals(1, databases.size());

        var database = Listx.getFirstOrNull(databases);

        assertNotNull(database);
        assertNotNull(database.getId());

        // 关联查询
        assertNotNull(database.getApplication());
        assertEquals(applicationEntity.getId(), database.getApplication().getId());

        // 关联查询
        assertNotNull(database.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), database.getCreator().getId());
        assertNotNull(database.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), database.getModifier().getId());
    }

    /**
     * @see DatabaseProvider#pageBy
     */
    @Test
    public void case4() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var databaseEntity = new DatabaseEntity();
        databaseEntity.setApplicationId(applicationEntity.getId());
        databaseEntity.setCode("test");
        databaseEntity.setName("测试数据库");
        databaseEntity.setType("mysql");
        databaseEntity.setEnabled(Boolean.TRUE);
        databaseEntity.setRemark("测试");
        databaseEntity.setTenantCode("master");
        databaseEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(databaseEntity);

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(Database.class).eq("application.code", "central-security"), null);
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1L, page.getPager().getPageIndex());
        assertEquals(20L, page.getPager().getPageSize());
        assertEquals(1L, page.getPager().getPageCount());
        assertEquals(1L, page.getPager().getItemCount());
        assertNotNull(page.getData());

        var database = Listx.getFirstOrNull(page.getData());

        assertNotNull(database);
        assertNotNull(database.getId());

        // 关联查询
        assertNotNull(database.getApplication());
        assertEquals(applicationEntity.getId(), database.getApplication().getId());

        // 关联查询
        assertNotNull(database.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), database.getCreator().getId());
        assertNotNull(database.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), database.getModifier().getId());
    }

    /**
     * @see DatabaseProvider#countBy
     */
    @Test
    public void case5() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var databaseEntity = new DatabaseEntity();
        databaseEntity.setApplicationId(applicationEntity.getId());
        databaseEntity.setCode("test");
        databaseEntity.setName("测试数据库");
        databaseEntity.setType("mysql");
        databaseEntity.setEnabled(Boolean.TRUE);
        databaseEntity.setRemark("测试");
        databaseEntity.setTenantCode("master");
        databaseEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(databaseEntity);

        // 查询数据
        var count = this.provider.countBy(Conditions.of(Database.class).eq("application.code", "central-security"));
        assertNotNull(count);
        assertEquals(1, count);
    }

    /**
     * @see DatabaseProvider#insert
     */
    @Test
    public void case6() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var input = DatabaseInput.builder()
                .applicationId(applicationEntity.getId())
                .code("test")
                .name("测试数据库")
                .type("mysql")
                .enabled(Boolean.TRUE)
                .remark("测试")
                .build();

        var database = this.provider.insert(input, properties.getSupervisor().getUsername());
        assertNotNull(database);
        assertNotNull(database.getId());

        assertTrue(this.mapper.existsBy(Conditions.of(DatabaseEntity.class).eq(DatabaseEntity::getCode, "test")));
    }

    /**
     * @see DatabaseProvider#insertBatch
     */
    @Test
    public void case7() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var input = DatabaseInput.builder()
                .applicationId(applicationEntity.getId())
                .code("test")
                .name("测试数据库")
                .type("mysql")
                .enabled(Boolean.TRUE)
                .remark("测试")
                .build();

        var databases = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(databases);
        assertEquals(1, databases.size());

        var database = Listx.getFirstOrNull(databases);
        assertNotNull(database);
        assertNotNull(database.getId());

        assertTrue(this.mapper.existsBy(Conditions.of(DatabaseEntity.class).eq(DatabaseEntity::getCode, "test")));
    }

    /**
     * @see DatabaseProvider#update
     */
    @Test
    public void case8() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var databaseEntity = new DatabaseEntity();
        databaseEntity.setApplicationId(applicationEntity.getId());
        databaseEntity.setCode("test");
        databaseEntity.setName("测试数据库");
        databaseEntity.setType("mysql");
        databaseEntity.setEnabled(Boolean.TRUE);
        databaseEntity.setRemark("测试");
        databaseEntity.setTenantCode("master");
        databaseEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(databaseEntity);

        var database = this.provider.findById(databaseEntity.getId());
        assertNotNull(database);

        var input = database.toInput().toBuilder()
                .code("example")
                .build();

        database = this.provider.update(input, properties.getSupervisor().getUsername());
        assertNotNull(database);

        assertTrue(this.mapper.existsBy(Conditions.of(DatabaseEntity.class).eq(DatabaseEntity::getCode, "example")));
    }

    /**
     * @see DatabaseProvider#updateBatch
     */
    @Test
    public void case9() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var databaseEntity = new DatabaseEntity();
        databaseEntity.setApplicationId(applicationEntity.getId());
        databaseEntity.setCode("test");
        databaseEntity.setName("测试数据库");
        databaseEntity.setType("mysql");
        databaseEntity.setEnabled(Boolean.TRUE);
        databaseEntity.setRemark("测试");
        databaseEntity.setTenantCode("master");
        databaseEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(databaseEntity);

        var database = this.provider.findById(databaseEntity.getId());
        assertNotNull(database);

        var input = database.toInput().toBuilder()
                .code("example")
                .build();

        var databases = this.provider.updateBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(databases);

        assertTrue(this.mapper.existsBy(Conditions.of(DatabaseEntity.class).eq(DatabaseEntity::getCode, "example")));
    }

    /**
     * @see DatabaseProvider#deleteByIds
     */
    @Test
    public void case10() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var databaseEntity = new DatabaseEntity();
        databaseEntity.setApplicationId(applicationEntity.getId());
        databaseEntity.setCode("test");
        databaseEntity.setName("测试数据库");
        databaseEntity.setType("mysql");
        databaseEntity.setEnabled(Boolean.TRUE);
        databaseEntity.setRemark("测试");
        databaseEntity.setTenantCode("master");
        databaseEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(databaseEntity);

        var deleted = this.provider.deleteByIds(List.of(databaseEntity.getId()));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(DatabaseEntity.class).eq(DatabaseEntity::getId, databaseEntity.getId())));
    }

    /**
     * @see DatabaseProvider#deleteBy(Conditions)
     */
    @Test
    public void case11() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var databaseEntity = new DatabaseEntity();
        databaseEntity.setApplicationId(applicationEntity.getId());
        databaseEntity.setCode("test");
        databaseEntity.setName("测试数据库");
        databaseEntity.setType("mysql");
        databaseEntity.setEnabled(Boolean.TRUE);
        databaseEntity.setRemark("测试");
        databaseEntity.setTenantCode("master");
        databaseEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(databaseEntity);

        var deleted = this.provider.deleteBy(Conditions.of(Database.class).eq(Database::getCode, "test"));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(DatabaseEntity.class).eq(DatabaseEntity::getId, databaseEntity.getId())));
    }
}
