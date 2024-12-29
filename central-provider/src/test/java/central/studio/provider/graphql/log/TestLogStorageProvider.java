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

package central.studio.provider.graphql.log;

import central.data.log.LogStorage;
import central.data.log.LogStorageInput;
import central.lang.reflect.TypeRef;
import central.provider.graphql.log.LogStorageProvider;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.ProviderProperties;
import central.studio.provider.database.persistence.log.entity.LogStorageEntity;
import central.studio.provider.database.persistence.log.mapper.LogStorageMapper;
import central.util.Jsonx;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LogStorageProvider Test Cases
 * <p>
 * 日志存储器
 *
 * @author Alan Yeh
 * @since 2022/10/26
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestLogStorageProvider {

    @Setter(onMethod_ = @Autowired)
    private ProviderProperties properties;

    @Setter(onMethod_ = @Autowired)
    private LogStorageProvider provider;

    @Setter(onMethod_ = @Autowired)
    private LogStorageMapper mapper;

    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空测试数据
        this.mapper.deleteAll();
    }


    /**
     * @see LogStorageProvider#findById
     */
    @Test
    public void case1() {
        var entity = new LogStorageEntity();
        entity.setCode("file");
        entity.setName("文件存储器");
        entity.setType("file");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("文件存储器");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "path", "./path",
                "rollingPolicy", "daily",
                "compressPolicy", "gzip",
                "maxSize", "1024",
                "maxHistory", "7")));
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var storage = this.provider.findById(entity.getId(), "master");
        assertNotNull(storage);
        assertEquals(entity.getId(), storage.getId());
        assertNotNull(storage.getCode());
        assertNotNull(storage.getName());
        assertNotNull(storage.getType());
        assertNotNull(storage.getEnabled());
        assertNotNull(storage.getRemark());
        assertNotNull(storage.getParams());
        var params = Jsonx.Default().deserialize(storage.getParams(), TypeRef.ofMap(String.class, Object.class));
        assertNotNull(params);
        assertEquals(5, params.size());
        // 关联查询
        assertNotNull(storage.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), storage.getCreator().getId());
        assertNotNull(storage.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), storage.getModifier().getId());
    }

    /**
     * @see LogStorageProvider#findByIds
     */
    @Test
    public void case2() {
        var entity = new LogStorageEntity();
        entity.setCode("file");
        entity.setName("文件存储器");
        entity.setType("file");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("文件存储器");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "path", "./path",
                "rollingPolicy", "daily",
                "compressPolicy", "gzip",
                "maxSize", "1024",
                "maxHistory", "7")));
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var storages = this.provider.findByIds(List.of(entity.getId()), "master");
        assertNotNull(storages);
        assertEquals(1, storages.size());

        var storage = Listx.getFirstOrNull(storages);
        assertNotNull(storage);
        assertEquals(entity.getId(), storage.getId());
        assertNotNull(storage.getCode());
        assertNotNull(storage.getName());
        assertNotNull(storage.getType());
        assertNotNull(storage.getEnabled());
        assertNotNull(storage.getRemark());
        assertNotNull(storage.getParams());
        // 关联查询
        assertNotNull(storage.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), storage.getCreator().getId());
        assertNotNull(storage.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), storage.getModifier().getId());
    }

    /**
     * @see LogStorageProvider#findBy
     */
    @Test
    public void case3() {
        var entity = new LogStorageEntity();
        entity.setCode("file");
        entity.setName("文件存储器");
        entity.setType("file");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("文件存储器");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "path", "./path",
                "rollingPolicy", "daily",
                "compressPolicy", "gzip",
                "maxSize", "1024",
                "maxHistory", "7")));
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var storages = this.provider.findBy(null, null, Conditions.of(LogStorage.class).eq(LogStorage::getCode, "file"), null, "master");
        assertNotNull(storages);
        assertEquals(1, storages.size());

        var storage = Listx.getFirstOrNull(storages);
        assertNotNull(storage);
        assertEquals(entity.getId(), storage.getId());
        assertNotNull(storage.getCode());
        assertNotNull(storage.getName());
        assertNotNull(storage.getType());
        assertNotNull(storage.getEnabled());
        assertNotNull(storage.getRemark());
        assertNotNull(storage.getParams());
        // 关联查询
        assertNotNull(storage.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), storage.getCreator().getId());
        assertNotNull(storage.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), storage.getModifier().getId());
    }

    /**
     * @see LogStorageProvider#pageBy
     */
    @Test
    public void case4() {
        var entity = new LogStorageEntity();
        entity.setCode("file");
        entity.setName("文件存储器");
        entity.setType("file");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("文件存储器");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "path", "./path",
                "rollingPolicy", "daily",
                "compressPolicy", "gzip",
                "maxSize", "1024",
                "maxHistory", "7")));
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(LogStorage.class).eq(LogStorage::getCode, "file"), null, "master");
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1, page.getPager().getPageIndex());
        assertEquals(20, page.getPager().getPageSize());
        assertEquals(1, page.getPager().getPageCount());
        assertEquals(1, page.getPager().getItemCount());

        var storage = Listx.getFirstOrNull(page.getData());
        assertNotNull(storage);
        assertEquals(entity.getId(), storage.getId());
        assertNotNull(storage.getCode());
        assertNotNull(storage.getName());
        assertNotNull(storage.getType());
        assertNotNull(storage.getEnabled());
        assertNotNull(storage.getRemark());
        assertNotNull(storage.getParams());
        // 关联查询
        assertNotNull(storage.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), storage.getCreator().getId());
        assertNotNull(storage.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), storage.getModifier().getId());
    }

    /**
     * @see LogStorageProvider#countBy
     */
    @Test
    public void case5() {
        var entity = new LogStorageEntity();
        entity.setCode("file");
        entity.setName("文件存储器");
        entity.setType("file");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("文件存储器");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "path", "./path",
                "rollingPolicy", "daily",
                "compressPolicy", "gzip",
                "maxSize", "1024",
                "maxHistory", "7")));
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var count = this.provider.countBy(Conditions.of(LogStorage.class).eq(LogStorage::getCode, "file"), "master");
        assertNotNull(count);
        assertEquals(1, count);
    }

    /**
     * @see LogStorageProvider#insert
     */
    @Test
    public void case6() {
        var input = LogStorageInput.builder()
                .code("file")
                .name("文件存储器")
                .type("file")
                .enabled(Boolean.TRUE)
                .remark("文件存储器")
                .params(Jsonx.Default().serialize(Map.of(
                        "path", "./path",
                        "rollingPolicy", "daily",
                        "compressPolicy", "gzip",
                        "maxSize", "1024",
                        "maxHistory", "7")))
                .build();

        // 查询数据
        var entity = this.provider.insert(input, properties.getSupervisor().getUsername(), "master");

        assertNotNull(entity);
        assertNotNull(entity.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(LogStorageEntity.class).eq(LogStorageEntity::getId, entity.getId())));
    }

    /**
     * @see LogStorageProvider#insertBatch
     */
    @Test
    public void case7() {
        var input = LogStorageInput.builder()
                .code("file")
                .name("文件存储器")
                .type("file")
                .enabled(Boolean.TRUE)
                .remark("文件存储器")
                .params(Jsonx.Default().serialize(Map.of(
                        "path", "./path",
                        "rollingPolicy", "daily",
                        "compressPolicy", "gzip",
                        "maxSize", "1024",
                        "maxHistory", "7")))
                .build();

        // 查询数据
        var entities = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername(), "master");

        assertNotNull(entities);
        assertEquals(1, entities.size());

        var entity = Listx.getFirstOrNull(entities);
        assertNotNull(entity);
        assertNotNull(entity.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(LogStorageEntity.class).eq(LogStorageEntity::getId, entity.getId())));
    }

    /**
     * @see LogStorageProvider#update
     */
    @Test
    public void case8() {
        var entity = new LogStorageEntity();
        entity.setCode("file");
        entity.setName("文件存储器");
        entity.setType("file");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("文件存储器");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "path", "./path",
                "rollingPolicy", "daily",
                "compressPolicy", "gzip",
                "maxSize", "1024",
                "maxHistory", "7")));
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var storage = this.provider.findById(entity.getId(), "master");
        assertNotNull(storage);
        assertEquals(entity.getId(), storage.getId());

        var input = storage.toInput()
                .code("test1")
                .build();

        // 更新数据
        storage = this.provider.update(input, properties.getSupervisor().getUsername(), "master");
        assertNotNull(storage);
        assertEquals(entity.getId(), storage.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(LogStorageEntity.class).eq(LogStorageEntity::getCode, "test1")));
    }

    /**
     * @see LogStorageProvider#updateBatch
     */
    @Test
    public void case9() {
        var entity = new LogStorageEntity();
        entity.setCode("file");
        entity.setName("文件存储器");
        entity.setType("file");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("文件存储器");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "path", "./path",
                "rollingPolicy", "daily",
                "compressPolicy", "gzip",
                "maxSize", "1024",
                "maxHistory", "7")));
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var storage = this.provider.findById(entity.getId(), "master");
        assertNotNull(storage);
        assertEquals(entity.getId(), storage.getId());

        var input = storage.toInput()
                .code("test1")
                .build();

        // 更新数据
        var storages = this.provider.updateBatch(List.of(input), properties.getSupervisor().getUsername(), "master");
        assertNotNull(storages);
        assertEquals(1, storages.size());

        storage = Listx.getFirstOrNull(storages);
        assertNotNull(storage);
        assertEquals(entity.getId(), storage.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(LogStorageEntity.class).eq(LogStorageEntity::getCode, "test1")));
    }

    /**
     * @see LogStorageProvider#deleteByIds
     */
    @Test
    public void case10() {
        var entity = new LogStorageEntity();
        entity.setCode("file");
        entity.setName("文件存储器");
        entity.setType("file");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("文件存储器");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "path", "./path",
                "rollingPolicy", "daily",
                "compressPolicy", "gzip",
                "maxSize", "1024",
                "maxHistory", "7")));
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var deleted = this.provider.deleteByIds(List.of(entity.getId()), "master");
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(LogStorageEntity.class).eq(LogStorageEntity::getId, entity.getId())));
    }

    /**
     * @see LogStorageProvider#deleteBy(Conditions)
     */
    @Test
    public void case11() {

        var entity = new LogStorageEntity();
        entity.setCode("file");
        entity.setName("文件存储器");
        entity.setType("file");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("文件存储器");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "path", "./path",
                "rollingPolicy", "daily",
                "compressPolicy", "gzip",
                "maxSize", "1024",
                "maxHistory", "7")));
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var deleted = this.provider.deleteBy(Conditions.of(LogStorage.class).eq(LogStorage::getCode, "file"), "master");
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(LogStorageEntity.class).eq(LogStorageEntity::getId, entity.getId())));
    }
}
