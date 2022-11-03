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

import central.api.provider.log.LogCollectorProvider;
import central.data.log.LogCollector;
import central.data.log.LogCollectorInput;
import central.lang.reflect.TypeReference;
import central.provider.ApplicationProperties;
import central.provider.ProviderApplication;
import central.provider.graphql.log.entity.LogCollectorEntity;
import central.provider.graphql.log.mapper.LogCollectorMapper;
import central.sql.Conditions;
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
 * LogCollectorProvider Test Cases
 * <p>
 * 日志采集器
 *
 * @author Alan Yeh
 * @since 2022/10/26
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestLogCollectorProvider {

    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    @Setter(onMethod_ = @Autowired)
    private LogCollectorProvider provider;

    @Setter(onMethod_ = @Autowired)
    private LogCollectorMapper mapper;

    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空测试数据
        this.mapper.deleteAll();
    }


    /**
     * @see LogCollectorProvider#findById
     */
    @Test
    public void case1() {
        var entity = new LogCollectorEntity();
        entity.setCode("http");
        entity.setName("Http 采集器");
        entity.setType("http");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("Http 采集器");
        entity.setParams(Jsonx.Default().serialize(Map.of("path", "central")));
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var collector = this.provider.findById(entity.getId());
        assertNotNull(collector);
        assertEquals(entity.getId(), collector.getId());
        assertNotNull(collector.getCode());
        assertNotNull(collector.getName());
        assertNotNull(collector.getType());
        assertNotNull(collector.getEnabled());
        assertNotNull(collector.getRemark());
        assertNotNull(collector.getParams());
        var params = Jsonx.Default().deserialize(collector.getParams(), TypeReference.ofMap(String.class, Object.class));
        assertNotNull(params);
        assertEquals(1, params.size());
        // 关联查询
        assertNotNull(collector.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), collector.getCreator().getId());
        assertNotNull(collector.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), collector.getModifier().getId());
    }

    /**
     * @see LogCollectorProvider#findByIds
     */
    @Test
    public void case2() {
        var entity = new LogCollectorEntity();
        entity.setCode("http");
        entity.setName("Http 采集器");
        entity.setType("http");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("Http 采集器");
        entity.setParams(Jsonx.Default().serialize(Map.of("path", "central")));
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var collectors = this.provider.findByIds(List.of(entity.getId()));
        assertNotNull(collectors);
        assertEquals(1, collectors.size());

        var collector = Listx.getFirstOrNull(collectors);
        assertNotNull(collector);
        assertEquals(entity.getId(), collector.getId());
        assertNotNull(collector.getCode());
        assertNotNull(collector.getName());
        assertNotNull(collector.getType());
        assertNotNull(collector.getEnabled());
        assertNotNull(collector.getRemark());
        assertNotNull(collector.getParams());
        // 关联查询
        assertNotNull(collector.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), collector.getCreator().getId());
        assertNotNull(collector.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), collector.getModifier().getId());
    }

    /**
     * @see LogCollectorProvider#findBy
     */
    @Test
    public void case3() {
        var entity = new LogCollectorEntity();
        entity.setCode("http");
        entity.setName("Http 采集器");
        entity.setType("http");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("Http 采集器");
        entity.setParams(Jsonx.Default().serialize(Map.of("path", "central")));
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var collectors = this.provider.findBy(null, null, Conditions.of(LogCollector.class).eq(LogCollector::getCode, "http"), null);
        assertNotNull(collectors);
        assertEquals(1, collectors.size());

        var collector = Listx.getFirstOrNull(collectors);
        assertNotNull(collector);
        assertEquals(entity.getId(), collector.getId());
        assertNotNull(collector.getCode());
        assertNotNull(collector.getName());
        assertNotNull(collector.getType());
        assertNotNull(collector.getEnabled());
        assertNotNull(collector.getRemark());
        assertNotNull(collector.getParams());
        // 关联查询
        assertNotNull(collector.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), collector.getCreator().getId());
        assertNotNull(collector.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), collector.getModifier().getId());
    }

    /**
     * @see LogCollectorProvider#pageBy
     */
    @Test
    public void case4() {
        var entity = new LogCollectorEntity();
        entity.setCode("http");
        entity.setName("Http 采集器");
        entity.setType("http");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("Http 采集器");
        entity.setParams(Jsonx.Default().serialize(Map.of("path", "central")));
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(LogCollector.class).eq(LogCollector::getCode, "http"), null);
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1, page.getPager().getPageIndex());
        assertEquals(20, page.getPager().getPageSize());
        assertEquals(1, page.getPager().getPageCount());
        assertEquals(1, page.getPager().getItemCount());

        var collector = Listx.getFirstOrNull(page.getData());
        assertNotNull(collector);
        assertEquals(entity.getId(), collector.getId());
        assertNotNull(collector.getCode());
        assertNotNull(collector.getName());
        assertNotNull(collector.getType());
        assertNotNull(collector.getEnabled());
        assertNotNull(collector.getRemark());
        assertNotNull(collector.getParams());
        // 关联查询
        assertNotNull(collector.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), collector.getCreator().getId());
        assertNotNull(collector.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), collector.getModifier().getId());
    }

    /**
     * @see LogCollectorProvider#countBy
     */
    @Test
    public void case5() {
        var entity = new LogCollectorEntity();
        entity.setCode("http");
        entity.setName("Http 采集器");
        entity.setType("http");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("Http 采集器");
        entity.setParams(Jsonx.Default().serialize(Map.of("path", "central")));
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var count = this.provider.countBy(Conditions.of(LogCollector.class).eq(LogCollector::getCode, "http"));
        assertNotNull(count);
        assertEquals(1, count);
    }

    /**
     * @see LogCollectorProvider#insert
     */
    @Test
    public void case6() {
        var input = LogCollectorInput.builder()
                .code("http")
                .name("Http 采集器")
                .type("http")
                .enabled(Boolean.TRUE)
                .remark("Http 采集器")
                .params(Jsonx.Default().serialize(Map.of("path", "central")))
                .build();

        // 查询数据
        var entity = this.provider.insert(input, properties.getSupervisor().getUsername());

        assertNotNull(entity);
        assertNotNull(entity.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(LogCollectorEntity.class).eq(LogCollectorEntity::getId, entity.getId())));
    }

    /**
     * @see LogCollectorProvider#insertBatch
     */
    @Test
    public void case7() {
        var input = LogCollectorInput.builder()
                .code("http")
                .name("Http 采集器")
                .type("http")
                .enabled(Boolean.TRUE)
                .remark("Http 采集器")
                .params(Jsonx.Default().serialize(Map.of("path", "central")))
                .build();

        // 查询数据
        var entities = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername());

        assertNotNull(entities);
        assertEquals(1, entities.size());

        var entity = Listx.getFirstOrNull(entities);
        assertNotNull(entity);
        assertNotNull(entity.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(LogCollectorEntity.class).eq(LogCollectorEntity::getId, entity.getId())));
    }

    /**
     * @see LogCollectorProvider#update
     */
    @Test
    public void case8() {
        var entity = new LogCollectorEntity();
        entity.setCode("http");
        entity.setName("Http 采集器");
        entity.setType("http");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("Http 采集器");
        entity.setParams(Jsonx.Default().serialize(Map.of("path", "central")));
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var collector = this.provider.findById(entity.getId());
        assertNotNull(collector);
        assertEquals(entity.getId(), collector.getId());

        var input = collector.toInput().toBuilder()
                .code("test1")
                .build();

        // 更新数据
        collector = this.provider.update(input, properties.getSupervisor().getUsername());
        assertNotNull(collector);
        assertEquals(entity.getId(), collector.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(LogCollectorEntity.class).eq(LogCollectorEntity::getCode, "test1")));
    }

    /**
     * @see LogCollectorProvider#updateBatch
     */
    @Test
    public void case9() {
        var entity = new LogCollectorEntity();
        entity.setCode("http");
        entity.setName("Http 采集器");
        entity.setType("http");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("Http 采集器");
        entity.setParams(Jsonx.Default().serialize(Map.of("path", "central")));
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var collector = this.provider.findById(entity.getId());
        assertNotNull(collector);
        assertEquals(entity.getId(), collector.getId());

        var input = collector.toInput().toBuilder()
                .code("test1")
                .build();

        // 更新数据
        var collectors = this.provider.updateBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(collectors);
        assertEquals(1, collectors.size());

        collector = Listx.getFirstOrNull(collectors);
        assertNotNull(collector);
        assertEquals(entity.getId(), collector.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(LogCollectorEntity.class).eq(LogCollectorEntity::getCode, "test1")));
    }

    /**
     * @see LogCollectorProvider#deleteByIds
     */
    @Test
    public void case10() {
        var entity = new LogCollectorEntity();
        entity.setCode("http");
        entity.setName("Http 采集器");
        entity.setType("http");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("Http 采集器");
        entity.setParams(Jsonx.Default().serialize(Map.of("path", "central")));
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var deleted = this.provider.deleteByIds(List.of(entity.getId()));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(LogCollectorEntity.class).eq(LogCollectorEntity::getId, entity.getId())));
    }

    /**
     * @see LogCollectorProvider#deleteBy(Conditions)
     */
    @Test
    public void case11() {

        var entity = new LogCollectorEntity();
        entity.setCode("http");
        entity.setName("Http 采集器");
        entity.setType("http");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("Http 采集器");
        entity.setParams(Jsonx.Default().serialize(Map.of("path", "central")));
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var deleted = this.provider.deleteBy(Conditions.of(LogCollector.class).eq(LogCollector::getCode, "http"));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(LogCollectorEntity.class).eq(LogCollectorEntity::getId, entity.getId())));
    }
}
