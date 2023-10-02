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

package central.provider.graphql.log;

import central.data.log.LogFilter;
import central.data.log.LogFilterInput;
import central.data.log.LogPredicate;
import central.data.log.LogPredicateInput;
import central.provider.ApplicationProperties;
import central.provider.ProviderApplication;
import central.provider.graphql.log.entity.*;
import central.provider.graphql.log.mapper.*;
import central.sql.query.Conditions;
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
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LogFilterProvider Test Cases
 * <p>
 * 日志过滤器
 *
 * @author Alan Yeh
 * @since 2022/10/26
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestLogFilterProvider {

    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    @Setter(onMethod_ = @Autowired)
    private LogFilterProvider provider;

    @Setter(onMethod_ = @Autowired)
    private LogFilterMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private LogCollectorMapper collectorMapper;

    @Setter(onMethod_ = @Autowired)
    private LogCollectorFilterMapper collectorRelMapper;

    @Setter(onMethod_ = @Autowired)
    private LogStorageMapper storageMapper;

    @Setter(onMethod_ = @Autowired)
    private LogStorageFilterMapper storageRelMapper;

    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空测试数据
        this.mapper.deleteAll();
        this.collectorMapper.deleteAll();
        this.collectorRelMapper.deleteAll();
        this.storageMapper.deleteAll();
        this.storageRelMapper.deleteAll();
    }

    private String initData() {
        // 采集器
        var collectorEntity = new LogCollectorEntity();
        collectorEntity.setCode("http");
        collectorEntity.setName("Http 采集器");
        collectorEntity.setType("http");
        collectorEntity.setEnabled(Boolean.TRUE);
        collectorEntity.setRemark("Http 采集器");
        collectorEntity.setParams(Jsonx.Default().serialize(Map.of("path", "central")));
        collectorEntity.updateCreator(properties.getSupervisor().getUsername());
        this.collectorMapper.insert(collectorEntity);

        // 存储器
        var storageEntity = new LogStorageEntity();
        storageEntity.setCode("file");
        storageEntity.setName("文件存储器");
        storageEntity.setType("file");
        storageEntity.setEnabled(Boolean.TRUE);
        storageEntity.setRemark("文件存储器");
        storageEntity.setParams(Jsonx.Default().serialize(Map.of(
                "path", "./path",
                "rollingPolicy", "daily",
                "compressPolicy", "gzip",
                "maxSize", "1024",
                "maxHistory", "7")));
        storageEntity.updateCreator(properties.getSupervisor().getUsername());
        this.storageMapper.insert(storageEntity);

        // 过滤器
        var entity = new LogFilterEntity();
        entity.setCode("central.x");
        entity.setName("CentralX");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("CentralX");
        entity.setPredicateJson(Jsonx.Default().serialize(List.of(
                new LogPredicate("tenant", Jsonx.Default().serialize(Map.of("tenantCode", "master"))),
                new LogPredicate("level", Jsonx.Default().serialize(Map.of("levels", List.of("info", "error"))))
        )));
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 采集器与过滤器的关联关系
        var collectorRel = new LogCollectorFilterEntity();
        collectorRel.setCollectorId(collectorEntity.getId());
        collectorRel.setFilterId(entity.getId());
        collectorRel.updateCreator(properties.getSupervisor().getUsername());
        this.collectorRelMapper.insert(collectorRel);

        // 存储器与过滤器的关联关系
        var storageRel = new LogStorageFilterEntity();
        storageRel.setStorageId(storageEntity.getId());
        storageRel.setFilterId(entity.getId());
        storageRel.updateCreator(properties.getSupervisor().getUsername());
        this.storageRelMapper.insert(storageRel);

        return entity.getId();
    }

    /**
     * @see LogFilterProvider#findById
     */
    @Test
    public void case1() {
        String id = this.initData();

        // 查询数据
        var filter = this.provider.findById(id);
        assertNotNull(filter);
        assertEquals(id, filter.getId());
        assertNotNull(filter.getCode());
        assertNotNull(filter.getName());
        assertNotNull(filter.getEnabled());
        assertNotNull(filter.getRemark());
        assertNotNull(filter.getPredicates());

        // 断言关联查询
        assertEquals(2, filter.getPredicates().size());
        assertTrue(filter.getPredicates().stream().anyMatch(it -> Objects.equals("tenant", it.getType())));
        assertTrue(filter.getPredicates().stream().anyMatch(it -> Objects.equals("level", it.getType())));

        // 采集器关联查询
        assertNotNull(filter.getCollectors());
        assertEquals(1, filter.getCollectors().size());
        assertTrue(filter.getCollectors().stream().anyMatch(it -> Objects.equals("http", it.getCode())));

        // 存储器关联查询
        assertNotNull(filter.getStorages());
        assertEquals(1, filter.getStorages().size());
        assertTrue(filter.getStorages().stream().anyMatch(it -> Objects.equals("file", it.getCode())));

        // 关联查询
        assertNotNull(filter.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), filter.getCreator().getId());
        assertNotNull(filter.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), filter.getModifier().getId());
    }

    /**
     * @see LogFilterProvider#findByIds
     */
    @Test
    public void case2() {
        String id = this.initData();

        // 查询数据
        var filters = this.provider.findByIds(List.of(id));
        assertNotNull(filters);

        var filter = Listx.getFirstOrNull(filters);

        assertNotNull(filter);
        assertEquals(id, filter.getId());
        assertNotNull(filter.getCode());
        assertNotNull(filter.getName());
        assertNotNull(filter.getEnabled());
        assertNotNull(filter.getRemark());
        assertNotNull(filter.getPredicates());

        // 断言关联查询
        assertEquals(2, filter.getPredicates().size());
        assertTrue(filter.getPredicates().stream().anyMatch(it -> Objects.equals("tenant", it.getType())));
        assertTrue(filter.getPredicates().stream().anyMatch(it -> Objects.equals("level", it.getType())));

        // 采集器关联查询
        assertNotNull(filter.getCollectors());
        assertEquals(1, filter.getCollectors().size());
        assertTrue(filter.getCollectors().stream().anyMatch(it -> Objects.equals("http", it.getCode())));

        // 存储器关联查询
        assertNotNull(filter.getStorages());
        assertEquals(1, filter.getStorages().size());
        assertTrue(filter.getStorages().stream().anyMatch(it -> Objects.equals("file", it.getCode())));

        // 关联查询
        assertNotNull(filter.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), filter.getCreator().getId());
        assertNotNull(filter.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), filter.getModifier().getId());
    }

    /**
     * @see LogFilterProvider#findBy
     */
    @Test
    public void case3() {
        String id = this.initData();

        // 查询数据
        var filters = this.provider.findBy(null, null, Conditions.of(LogFilter.class).eq(LogFilter::getCode, "central.x"), null);
        assertNotNull(filters);
        assertEquals(1, filters.size());

        var filter = Listx.getFirstOrNull(filters);
        assertNotNull(filter);
        assertEquals(id, filter.getId());
        assertNotNull(filter.getCode());
        assertNotNull(filter.getName());
        assertNotNull(filter.getEnabled());
        assertNotNull(filter.getRemark());
        assertNotNull(filter.getPredicates());

        // 断言关联查询
        assertEquals(2, filter.getPredicates().size());
        assertTrue(filter.getPredicates().stream().anyMatch(it -> Objects.equals("tenant", it.getType())));
        assertTrue(filter.getPredicates().stream().anyMatch(it -> Objects.equals("level", it.getType())));

        // 采集器关联查询
        assertNotNull(filter.getCollectors());
        assertEquals(1, filter.getCollectors().size());
        assertTrue(filter.getCollectors().stream().anyMatch(it -> Objects.equals("http", it.getCode())));

        // 存储器关联查询
        assertNotNull(filter.getStorages());
        assertEquals(1, filter.getStorages().size());
        assertTrue(filter.getStorages().stream().anyMatch(it -> Objects.equals("file", it.getCode())));

        // 关联查询
        assertNotNull(filter.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), filter.getCreator().getId());
        assertNotNull(filter.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), filter.getModifier().getId());
    }

    /**
     * @see LogFilterProvider#pageBy
     */
    @Test
    public void case4() {
        String id = this.initData();

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(LogFilter.class).eq(LogFilter::getCode, "central.x"), null);
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1, page.getPager().getPageIndex());
        assertEquals(20, page.getPager().getPageSize());
        assertEquals(1, page.getPager().getPageCount());
        assertEquals(1, page.getPager().getItemCount());

        var filter = Listx.getFirstOrNull(page.getData());
        assertNotNull(filter);
        assertEquals(id, filter.getId());
        assertNotNull(filter.getCode());
        assertNotNull(filter.getName());
        assertNotNull(filter.getEnabled());
        assertNotNull(filter.getRemark());
        assertNotNull(filter.getPredicates());

        // 断言关联查询
        assertEquals(2, filter.getPredicates().size());
        assertTrue(filter.getPredicates().stream().anyMatch(it -> Objects.equals("tenant", it.getType())));
        assertTrue(filter.getPredicates().stream().anyMatch(it -> Objects.equals("level", it.getType())));

        // 采集器关联查询
        assertNotNull(filter.getCollectors());
        assertEquals(1, filter.getCollectors().size());
        assertTrue(filter.getCollectors().stream().anyMatch(it -> Objects.equals("http", it.getCode())));

        // 存储器关联查询
        assertNotNull(filter.getStorages());
        assertEquals(1, filter.getStorages().size());
        assertTrue(filter.getStorages().stream().anyMatch(it -> Objects.equals("file", it.getCode())));

        // 关联查询
        assertNotNull(filter.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), filter.getCreator().getId());
        assertNotNull(filter.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), filter.getModifier().getId());
    }

    /**
     * @see LogFilterProvider#countBy
     */
    @Test
    public void case5() {
        this.initData();

        // 查询数据
        var count = this.provider.countBy(Conditions.of(LogFilter.class).eq(LogFilter::getCode, "central.x"));
        assertNotNull(count);
        assertEquals(1, count);
    }

    /**
     * @see LogFilterProvider#insert
     */
    @Test
    public void case6() {
        // 采集器
        var collectorEntity = new LogCollectorEntity();
        collectorEntity.setCode("http");
        collectorEntity.setName("Http 采集器");
        collectorEntity.setType("http");
        collectorEntity.setEnabled(Boolean.TRUE);
        collectorEntity.setRemark("Http 采集器");
        collectorEntity.setParams(Jsonx.Default().serialize(Map.of("path", "central")));
        collectorEntity.updateCreator(properties.getSupervisor().getUsername());
        this.collectorMapper.insert(collectorEntity);

        // 存储器
        var storageEntity = new LogStorageEntity();
        storageEntity.setCode("file");
        storageEntity.setName("文件存储器");
        storageEntity.setType("file");
        storageEntity.setEnabled(Boolean.TRUE);
        storageEntity.setRemark("文件存储器");
        storageEntity.setParams(Jsonx.Default().serialize(Map.of(
                "path", "./path",
                "rollingPolicy", "daily",
                "compressPolicy", "gzip",
                "maxSize", "1024",
                "maxHistory", "7")));
        storageEntity.updateCreator(properties.getSupervisor().getUsername());
        this.storageMapper.insert(storageEntity);

        // 过滤器
        var input = LogFilterInput.builder()
                .code("centralx")
                .name("CentralX")
                .enabled(Boolean.TRUE)
                .remark("CentralX")
                .collectorIds(List.of(collectorEntity.getId()))
                .storageIds(List.of(storageEntity.getId()))
                .predicates(List.of(
                        new LogPredicateInput("tenant", Jsonx.Default().serialize(Map.of("tenantCode", "master"))),
                        new LogPredicateInput("level", Jsonx.Default().serialize(Map.of("levels", List.of("info", "error"))))
                ))
                .build();

        // 查询数据
        var entity = this.provider.insert(input, properties.getSupervisor().getUsername());

        assertNotNull(entity);
        assertNotNull(entity.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(LogFilterEntity.class).eq(LogFilterEntity::getId, entity.getId())));
    }

    /**
     * @see LogFilterProvider#insertBatch
     */
    @Test
    public void case7() {
        // 采集器
        var collectorEntity = new LogCollectorEntity();
        collectorEntity.setCode("http");
        collectorEntity.setName("Http 采集器");
        collectorEntity.setType("http");
        collectorEntity.setEnabled(Boolean.TRUE);
        collectorEntity.setRemark("Http 采集器");
        collectorEntity.setParams(Jsonx.Default().serialize(Map.of("path", "central")));
        collectorEntity.updateCreator(properties.getSupervisor().getUsername());
        this.collectorMapper.insert(collectorEntity);

        // 存储器
        var storageEntity = new LogStorageEntity();
        storageEntity.setCode("file");
        storageEntity.setName("文件存储器");
        storageEntity.setType("file");
        storageEntity.setEnabled(Boolean.TRUE);
        storageEntity.setRemark("文件存储器");
        storageEntity.setParams(Jsonx.Default().serialize(Map.of(
                "path", "./path",
                "rollingPolicy", "daily",
                "compressPolicy", "gzip",
                "maxSize", "1024",
                "maxHistory", "7")));
        storageEntity.updateCreator(properties.getSupervisor().getUsername());
        this.storageMapper.insert(storageEntity);

        // 过滤器
        var input = LogFilterInput.builder()
                .code("centralx")
                .name("CentralX")
                .enabled(Boolean.TRUE)
                .remark("CentralX")
                .collectorIds(List.of(collectorEntity.getId()))
                .storageIds(List.of(storageEntity.getId()))
                .predicates(List.of(
                        new LogPredicateInput("tenant", Jsonx.Default().serialize(Map.of("tenantCode", "master"))),
                        new LogPredicateInput("level", Jsonx.Default().serialize(Map.of("levels", List.of("info", "error"))))
                ))
                .build();

        // 查询数据
        var entities = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(entities);
        assertEquals(1, entities.size());

        var entity = Listx.getFirstOrNull(entities);
        assertNotNull(entity);
        assertNotNull(entity.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(LogFilterEntity.class).eq(LogFilterEntity::getId, entity.getId())));
    }

    /**
     * @see LogFilterProvider#update
     */
    @Test
    public void case8() {
        var id = this.initData();

        // 查询数据
        var filter = this.provider.findById(id);
        assertNotNull(filter);
        assertEquals(id, filter.getId());

        var input = filter.toInput().toBuilder()
                .code("test1")
                .build();

        // 更新数据
        filter = this.provider.update(input, properties.getSupervisor().getUsername());
        assertNotNull(filter);
        assertEquals(id, filter.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(LogFilterEntity.class).eq(LogFilterEntity::getCode, "test1")));
    }

    /**
     * @see LogFilterProvider#updateBatch
     */
    @Test
    public void case9() {

        var id = this.initData();

        // 查询数据
        var filter = this.provider.findById(id);
        assertNotNull(filter);
        assertEquals(id, filter.getId());

        var input = filter.toInput().toBuilder()
                .code("test1")
                .build();

        // 更新数据
        var filters = this.provider.updateBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(filters);
        assertEquals(1, filters.size());

        filter = Listx.getFirstOrNull(filters);
        assertNotNull(filter);
        assertEquals(id, filter.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(LogFilterEntity.class).eq(LogFilterEntity::getCode, "test1")));
    }

    /**
     * @see LogFilterProvider#deleteByIds
     */
    @Test
    public void case10() {
        var id = this.initData();

        var deleted = this.provider.deleteByIds(List.of(id));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(LogFilterEntity.class).eq(LogFilterEntity::getId, id)));
        assertFalse(this.collectorRelMapper.existsBy(Conditions.of(LogCollectorFilterEntity.class).eq(LogCollectorFilterEntity::getFilterId, id)));
        assertFalse(this.storageRelMapper.existsBy(Conditions.of(LogStorageFilterEntity.class).eq(LogStorageFilterEntity::getFilterId, id)));
    }

    /**
     * @see LogFilterProvider#deleteBy(Conditions)
     */
    @Test
    public void case11() {
        var id = this.initData();

        var deleted = this.provider.deleteBy(Conditions.of(LogFilter.class).eq(LogFilter::getCode, "central.x"));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(LogFilterEntity.class).eq(LogFilterEntity::getId, id)));
        assertFalse(this.collectorRelMapper.existsBy(Conditions.of(LogCollectorFilterEntity.class).eq(LogCollectorFilterEntity::getFilterId, id)));
        assertFalse(this.storageRelMapper.existsBy(Conditions.of(LogStorageFilterEntity.class).eq(LogStorageFilterEntity::getFilterId, id)));
    }
}
