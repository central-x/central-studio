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

import central.data.system.Database;
import central.data.system.DatabaseInput;
import central.data.system.DatabaseProperties;
import central.data.system.DatabasePropertiesInput;
import central.provider.ApplicationProperties;
import central.provider.ProviderApplication;
import central.provider.graphql.TestProvider;
import central.provider.graphql.saas.entity.ApplicationEntity;
import central.provider.graphql.saas.mapper.ApplicationMapper;
import central.provider.graphql.system.entity.DatabaseEntity;
import central.provider.graphql.system.mapper.DatabaseMapper;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Conditions;
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

    private static ApplicationEntity applicationEntity;

    @Setter(onMethod_ = @Autowired)
    private DataContext context;

    @BeforeEach
    @AfterEach
    public void clear() throws Exception {
        // 清空数据
        mapper.deleteAll();
        if (applicationEntity == null){
            applicationEntity = new ApplicationEntity();
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

            SaasContainer container = null;
            while (container == null || container.getApplications().isEmpty()) {
                Thread.sleep(100);
                container = context.getData(DataFetcherType.SAAS);
            }
        }
    }

    /**
     * @see DatabaseProvider#findById
     */
    @Test
    public void case1() {
        var databaseEntity = new DatabaseEntity();
        databaseEntity.setApplicationId(applicationEntity.getId());
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
        this.mapper.insert(databaseEntity);

        // 查询数据
        var database = this.provider.findById(databaseEntity.getId());
        assertNotNull(database);
        assertNotNull(database.getId());
        assertNotNull(database.getParams());

        // 关联查询
        assertNotNull(database.getApplication());
        assertEquals(applicationEntity.getId(), database.getApplication().getId());

        assertNotNull(database.getMaster());
        assertEquals("org.h2.Driver", database.getMaster().getDriver());
        assertEquals("jdbc:h2:mem:central-provider", database.getMaster().getUrl());
        assertEquals("centralx", database.getMaster().getUsername());
        assertEquals("central.x", database.getMaster().getPassword());

        assertNotNull(database.getSlaves());
        assertEquals(2, database.getSlaves().size());

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
        var databaseEntity = new DatabaseEntity();
        databaseEntity.setApplicationId(applicationEntity.getId());
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
        this.mapper.insert(databaseEntity);

        // 查询数据
        var databases = this.provider.findByIds(List.of(databaseEntity.getId()));
        assertNotNull(databases);
        assertEquals(1, databases.size());

        var database = Listx.getFirstOrNull(databases);

        assertNotNull(database);
        assertNotNull(database.getId());
        assertNotNull(database.getParams());

        // 关联查询
        assertNotNull(database.getApplication());
        assertEquals(applicationEntity.getId(), database.getApplication().getId());

        assertNotNull(database.getMaster());
        assertEquals("org.h2.Driver", database.getMaster().getDriver());
        assertEquals("jdbc:h2:mem:central-provider", database.getMaster().getUrl());
        assertEquals("centralx", database.getMaster().getUsername());
        assertEquals("central.x", database.getMaster().getPassword());

        assertNotNull(database.getSlaves());
        assertEquals(2, database.getSlaves().size());

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
        var databaseEntity = new DatabaseEntity();
        databaseEntity.setApplicationId(applicationEntity.getId());
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
        this.mapper.insert(databaseEntity);

        // 查询数据
        var databases = this.provider.findBy(null, null, Conditions.of(Database.class).eq("application.code", "central-security"), null);
        assertNotNull(databases);
        assertEquals(1, databases.size());

        var database = Listx.getFirstOrNull(databases);

        assertNotNull(database);
        assertNotNull(database.getId());
        assertNotNull(database.getParams());

        // 关联查询
        assertNotNull(database.getApplication());
        assertEquals(applicationEntity.getId(), database.getApplication().getId());

        assertNotNull(database.getMaster());
        assertEquals("org.h2.Driver", database.getMaster().getDriver());
        assertEquals("jdbc:h2:mem:central-provider", database.getMaster().getUrl());
        assertEquals("centralx", database.getMaster().getUsername());
        assertEquals("central.x", database.getMaster().getPassword());

        assertNotNull(database.getSlaves());
        assertEquals(2, database.getSlaves().size());

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
        var databaseEntity = new DatabaseEntity();
        databaseEntity.setApplicationId(applicationEntity.getId());
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
        assertNotNull(database.getParams());

        // 关联查询
        assertNotNull(database.getApplication());
        assertEquals(applicationEntity.getId(), database.getApplication().getId());

        assertNotNull(database.getMaster());
        assertEquals("org.h2.Driver", database.getMaster().getDriver());
        assertEquals("jdbc:h2:mem:central-provider", database.getMaster().getUrl());
        assertEquals("centralx", database.getMaster().getUsername());
        assertEquals("central.x", database.getMaster().getPassword());

        assertNotNull(database.getSlaves());
        assertEquals(2, database.getSlaves().size());

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
        var databaseEntity = new DatabaseEntity();
        databaseEntity.setApplicationId(applicationEntity.getId());
        databaseEntity.setCode("test");
        databaseEntity.setName("测试数据库");
        databaseEntity.setType("mysql");
        databaseEntity.setEnabled(Boolean.TRUE);
        databaseEntity.setRemark("测试");
        databaseEntity.setMasterJson(Jsonx.Default().serialize(new DatabaseProperties("org.h2.Driver", "jdbc:h2:mem:central-provider", "centralx", "central.x")));
        databaseEntity.setSlavesJson(Jsonx.Default().serialize(List.of(
                new DatabasePropertiesInput("org.h2.Driver", "jdbc:h2:mem:central-provider-slave1", "centralx", "central.x"),
                new DatabasePropertiesInput("org.h2.Driver", "jdbc:h2:mem:central-provider-slave2", "centralx", "central.x")
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
        var input = DatabaseInput.builder()
                .applicationId(applicationEntity.getId())
                .code("test")
                .name("测试数据库")
                .type("mysql")
                .enabled(Boolean.TRUE)
                .remark("测试")
                .master(new DatabasePropertiesInput("org.h2.Driver", "jdbc:h2:mem:central-provider", "centralx", "central.x"))
                .slaves(List.of(
                        new DatabasePropertiesInput("org.h2.Driver", "jdbc:h2:mem:central-provider-slave1", "centralx", "central.x"),
                        new DatabasePropertiesInput("org.h2.Driver", "jdbc:h2:mem:central-provider-slave2", "centralx", "central.x")
                ))
                .params(Jsonx.Default().serialize(Map.of(
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
                )))
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
        var input = DatabaseInput.builder()
                .applicationId(applicationEntity.getId())
                .code("test")
                .name("测试数据库")
                .type("mysql")
                .enabled(Boolean.TRUE)
                .remark("测试")
                .master(new DatabasePropertiesInput("org.h2.Driver", "jdbc:h2:mem:central-provider", "centralx", "central.x"))
                .slaves(List.of(
                        new DatabasePropertiesInput("org.h2.Driver", "jdbc:h2:mem:central-provider-slave1", "centralx", "central.x"),
                        new DatabasePropertiesInput("org.h2.Driver", "jdbc:h2:mem:central-provider-slave2", "centralx", "central.x")
                ))
                .params(Jsonx.Default().serialize(Map.of(
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
                )))
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
        var databaseEntity = new DatabaseEntity();
        databaseEntity.setApplicationId(applicationEntity.getId());
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
        var databaseEntity = new DatabaseEntity();
        databaseEntity.setApplicationId(applicationEntity.getId());
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
        var databaseEntity = new DatabaseEntity();
        databaseEntity.setApplicationId(applicationEntity.getId());
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
        var databaseEntity = new DatabaseEntity();
        databaseEntity.setApplicationId(applicationEntity.getId());
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
        this.mapper.insert(databaseEntity);

        var deleted = this.provider.deleteBy(Conditions.of(Database.class).eq(Database::getCode, "test"));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(DatabaseEntity.class).eq(DatabaseEntity::getId, databaseEntity.getId())));
    }
}
