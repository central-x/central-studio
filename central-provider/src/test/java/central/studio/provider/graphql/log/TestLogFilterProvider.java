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

import central.data.log.*;
import central.provider.graphql.log.LogFilterProvider;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.log.LogCollectorPersistence;
import central.studio.provider.database.persistence.log.LogFilterPersistence;
import central.studio.provider.database.persistence.log.LogStoragePersistence;
import central.studio.provider.database.persistence.log.entity.LogCollectorEntity;
import central.studio.provider.database.persistence.log.entity.LogFilterEntity;
import central.studio.provider.database.persistence.log.entity.LogStorageEntity;
import central.util.Jsonx;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
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
    private LogFilterProvider provider;

    @Setter(onMethod_ = @Autowired)
    private LogFilterPersistence persistence;

    @Setter(onMethod_ = @Autowired)
    private LogCollectorPersistence collectorPersistence;

    @Setter(onMethod_ = @Autowired)
    private LogStoragePersistence storagePersistence;

    @AfterEach
    public void clear() {
        // 清空测试数据
        this.persistence.deleteBy(Conditions.of(LogFilterEntity.class).like(LogFilterEntity::getCode, "test%"));
        this.collectorPersistence.deleteBy(Conditions.of(LogCollectorEntity.class).like(LogCollectorEntity::getCode, "test%"));
        this.storagePersistence.deleteBy(Conditions.of(LogStorageEntity.class).like(LogStorageEntity::getCode, "test%"));
    }

    /**
     * @see LogFilterProvider#insert
     * @see LogFilterProvider#findById
     * @see LogFilterProvider#update
     * @see LogFilterProvider#findByIds
     * @see LogFilterProvider#countBy
     * @see LogFilterProvider#deleteByIds
     */
    @Test
    public void case1() {
        // 采集器
        var collector = this.collectorPersistence.insert(LogCollectorInput.builder()
                .code("test")
                .name("测试采集器")
                .type("http")
                .enabled(Boolean.TRUE)
                .remark("测试采集器")
                .params(Jsonx.Default().serialize(Map.of("path", "central")))
                .build(), "syssa");

        // 存储器
        var storage = this.storagePersistence.insert(LogStorageInput.builder()
                .code("test")
                .name("测试存储器")
                .type("file")
                .enabled(Boolean.TRUE)
                .remark("测试存储器")
                .params(Jsonx.Default().serialize(Map.of(
                        "path", "./path",
                        "rollingPolicy", "daily",
                        "compressPolicy", "gzip",
                        "maxSize", "1024",
                        "maxHistory", "7")))
                .build(), "syssa");

        // 过滤器
        var input = LogFilterInput.builder()
                .code("test")
                .name("测试日志过滤器")
                .enabled(Boolean.TRUE)
                .remark("测试日志过滤器")
                .collectorIds(List.of(collector.getId()))
                .storageIds(List.of(storage.getId()))
                .predicates(List.of(
                        new LogPredicateInput("tenant", Jsonx.Default().serialize(Map.of("tenantCode", "master"))),
                        new LogPredicateInput("level", Jsonx.Default().serialize(Map.of("levels", List.of("info", "error"))))
                ))
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", "master");
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getRemark(), insert.getRemark());
        assertEquals(1, insert.getCollectors().size());
        assertTrue(insert.getCollectors().stream().anyMatch(it -> it.getId().equals(collector.getId())));
        assertTrue(insert.getStorages().stream().anyMatch(it -> it.getId().equals(storage.getId())));
        assertEquals(2, insert.getPredicates().size());
        assertTrue(insert.getPredicates().stream().anyMatch(it -> Objects.equals("tenant", it.getType())));
        assertTrue(insert.getPredicates().stream().anyMatch(it -> Objects.equals("level", it.getType())));

        // test findById
        var findById = this.provider.findById(insert.getId(), "master");
        assertNotNull(findById);
        assertEquals(insert.getId(), findById.getId());
        assertEquals(insert.getCode(), findById.getCode());
        assertEquals(insert.getName(), findById.getName());
        assertEquals(insert.getEnabled(), findById.getEnabled());
        assertEquals(insert.getRemark(), findById.getRemark());
        assertEquals(1, findById.getCollectors().size());
        assertTrue(findById.getCollectors().stream().anyMatch(it -> it.getId().equals(collector.getId())));
        assertTrue(findById.getStorages().stream().anyMatch(it -> it.getId().equals(storage.getId())));
        assertEquals(2, findById.getPredicates().size());
        assertTrue(findById.getPredicates().stream().anyMatch(it -> Objects.equals("tenant", it.getType())));
        assertTrue(findById.getPredicates().stream().anyMatch(it -> Objects.equals("level", it.getType())));

        // test countBy
        var count = this.provider.countBy(Conditions.of(LogFilter.class).eq(LogFilter::getCode, "test"), "master");
        assertEquals(1, count);

        // test update
        this.provider.update(insert.toInput().code("test2").enabled(Boolean.FALSE).build(), "syssa", "master");

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), "master");
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals("test2", fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(insert.getRemark(), fetched.getRemark());
        assertEquals(1, fetched.getCollectors().size());
        assertTrue(fetched.getCollectors().stream().anyMatch(it -> it.getId().equals(collector.getId())));
        assertTrue(fetched.getStorages().stream().anyMatch(it -> it.getId().equals(storage.getId())));
        assertEquals(2, fetched.getPredicates().size());
        assertTrue(fetched.getPredicates().stream().anyMatch(it -> Objects.equals("tenant", it.getType())));
        assertTrue(fetched.getPredicates().stream().anyMatch(it -> Objects.equals("level", it.getType())));

        // test deleteByIds
        count = this.provider.deleteByIds(List.of(insert.getId()), "master");
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(LogFilterEntity.class).like(LogFilterEntity::getCode, "test%"));
        assertEquals(0, count);
    }

    /**
     * @see LogFilterProvider#insertBatch
     * @see LogFilterProvider#findBy
     * @see LogFilterProvider#updateBatch
     * @see LogFilterProvider#pageBy
     * @see LogFilterProvider#deleteBy
     */
    @Test
    public void case2() {
        // 采集器
        var collector = this.collectorPersistence.insert(LogCollectorInput.builder()
                .code("test")
                .name("测试采集器")
                .type("http")
                .enabled(Boolean.TRUE)
                .remark("测试采集器")
                .params(Jsonx.Default().serialize(Map.of("path", "central")))
                .build(), "syssa");

        // 存储器
        var storage = this.storagePersistence.insert(LogStorageInput.builder()
                .code("test")
                .name("测试存储器")
                .type("file")
                .enabled(Boolean.TRUE)
                .remark("测试存储器")
                .params(Jsonx.Default().serialize(Map.of(
                        "path", "./path",
                        "rollingPolicy", "daily",
                        "compressPolicy", "gzip",
                        "maxSize", "1024",
                        "maxHistory", "7")))
                .build(), "syssa");

        // 过滤器
        var input = LogFilterInput.builder()
                .code("test")
                .name("测试日志过滤器")
                .enabled(Boolean.TRUE)
                .remark("测试日志过滤器")
                .collectorIds(List.of(collector.getId()))
                .storageIds(List.of(storage.getId()))
                .predicates(List.of(
                        new LogPredicateInput("tenant", Jsonx.Default().serialize(Map.of("tenantCode", "master"))),
                        new LogPredicateInput("level", Jsonx.Default().serialize(Map.of("levels", List.of("info", "error"))))
                ))
                .build();

        // test insert
        var insertBatch = this.provider.insertBatch(List.of(input), "syssa", "master");
        assertNotNull(insertBatch);
        assertEquals(1, insertBatch.size());

        var insert = Listx.getFirstOrNull(insertBatch);
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getRemark(), insert.getRemark());
        assertEquals(1, insert.getCollectors().size());
        assertTrue(insert.getCollectors().stream().anyMatch(it -> it.getId().equals(collector.getId())));
        assertTrue(insert.getStorages().stream().anyMatch(it -> it.getId().equals(storage.getId())));
        assertEquals(2, insert.getPredicates().size());
        assertTrue(insert.getPredicates().stream().anyMatch(it -> Objects.equals("tenant", it.getType())));
        assertTrue(insert.getPredicates().stream().anyMatch(it -> Objects.equals("level", it.getType())));

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(LogFilter.class).like(LogFilter::getCode, "test%"), null, "master");
        assertNotNull(findBy);
        assertEquals(1, findBy.size());

        var fetched = Listx.getFirstOrNull(findBy);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getCode(), fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getEnabled(), fetched.getEnabled());
        assertEquals(insert.getRemark(), fetched.getRemark());
        assertEquals(1, fetched.getCollectors().size());
        assertTrue(fetched.getCollectors().stream().anyMatch(it -> it.getId().equals(collector.getId())));
        assertTrue(fetched.getStorages().stream().anyMatch(it -> it.getId().equals(storage.getId())));
        assertEquals(2, fetched.getPredicates().size());
        assertTrue(fetched.getPredicates().stream().anyMatch(it -> Objects.equals("tenant", it.getType())));
        assertTrue(fetched.getPredicates().stream().anyMatch(it -> Objects.equals("level", it.getType())));

        // test updateBatch
        this.provider.updateBatch(List.of(insert.toInput().code("test2").enabled(Boolean.FALSE).build()), "syssa", "master");

        // test pageBy
        var pageBy = this.provider.pageBy(1, 10, Conditions.of(LogFilter.class).like(LogFilter::getCode, "test%"), null, "master");
        assertNotNull(pageBy);
        assertEquals(1, pageBy.getPager().getPageIndex());
        assertEquals(10, pageBy.getPager().getPageSize());
        assertEquals(1, pageBy.getPager().getPageCount());
        assertEquals(1, pageBy.getPager().getItemCount());
        assertEquals(1, pageBy.getData().size());

        fetched = Listx.getFirstOrNull(pageBy.getData());
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals("test2", fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(insert.getRemark(), fetched.getRemark());
        assertEquals(1, fetched.getCollectors().size());
        assertTrue(fetched.getCollectors().stream().anyMatch(it -> it.getId().equals(collector.getId())));
        assertTrue(fetched.getStorages().stream().anyMatch(it -> it.getId().equals(storage.getId())));
        assertEquals(2, fetched.getPredicates().size());
        assertTrue(fetched.getPredicates().stream().anyMatch(it -> Objects.equals("tenant", it.getType())));
        assertTrue(fetched.getPredicates().stream().anyMatch(it -> Objects.equals("level", it.getType())));

        // test deleteByIds
        var count = this.provider.deleteBy(Conditions.of(LogFilter.class).like(LogFilter::getCode, "test%"), "master");
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(LogFilterEntity.class).like(LogFilterEntity::getCode, "test%"));
        assertEquals(0, count);
    }
}
