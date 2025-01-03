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
import central.provider.graphql.log.LogStorageProvider;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.log.LogStoragePersistence;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    private LogStorageProvider provider;

    @Setter(onMethod_ = @Autowired)
    private LogStoragePersistence persistence;

    @AfterEach
    public void clear() throws Exception {
        // 清空测试数据
        this.persistence.deleteBy(Conditions.of(LogStorageEntity.class).like(LogStorageEntity::getCode, "test%"));
    }

    /**
     * @see LogStorageProvider#insert
     * @see LogStorageProvider#findById
     * @see LogStorageProvider#update
     * @see LogStorageProvider#findByIds
     * @see LogStorageProvider#countBy
     * @see LogStorageProvider#deleteByIds
     */
    @Test
    public void case1() {
        var input = LogStorageInput.builder()
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
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", "master");
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getType(), insert.getType());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getRemark(), insert.getRemark());
        assertEquals(input.getParams(), insert.getParams());

        // test findById
        var findById = this.provider.findById(insert.getId(), "master");
        assertNotNull(findById);
        assertEquals(insert.getId(), findById.getId());
        assertEquals(insert.getCode(), findById.getCode());
        assertEquals(insert.getName(), findById.getName());
        assertEquals(insert.getType(), findById.getType());
        assertEquals(insert.getEnabled(), findById.getEnabled());
        assertEquals(insert.getRemark(), findById.getRemark());
        assertEquals(insert.getParams(), findById.getParams());

        // test countBy
        var count = this.provider.countBy(Conditions.of(LogStorage.class).eq(LogStorage::getCode, "test"), "master");
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
        assertEquals(insert.getType(), fetched.getType());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(insert.getRemark(), fetched.getRemark());
        assertEquals(insert.getParams(), fetched.getParams());

        // test deleteById
        count = this.provider.deleteByIds(List.of(insert.getId()), "master");
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(LogStorageEntity.class).eq(LogStorageEntity::getCode, "test2"));
        assertEquals(0, count);
    }

    /**
     * @see LogStorageProvider#insertBatch
     * @see LogStorageProvider#findBy
     * @see LogStorageProvider#updateBatch
     * @see LogStorageProvider#pageBy
     * @see LogStorageProvider#deleteBy
     */
    @Test
    public void case2() {
        var input = LogStorageInput.builder()
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
                .build();

        // test insertBatch
        var insertBatch = this.provider.insertBatch(List.of(input), "syssa", "master");
        assertNotNull(insertBatch);
        assertEquals(1, insertBatch.size());

        var insert = Listx.getFirstOrNull(insertBatch);
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getType(), insert.getType());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getRemark(), insert.getRemark());
        assertEquals(input.getParams(), insert.getParams());

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(LogStorage.class).eq(LogStorage::getCode, "test"), null, "master");
        assertNotNull(findBy);
        assertEquals(1, findBy.size());

        var fetched = Listx.getFirstOrNull(findBy);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getCode(), fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getType(), fetched.getType());
        assertEquals(insert.getEnabled(), fetched.getEnabled());
        assertEquals(insert.getRemark(), fetched.getRemark());
        assertEquals(insert.getParams(), fetched.getParams());

        // test updateBatch
        this.provider.updateBatch(List.of(insert.toInput().code("test2").enabled(Boolean.FALSE).build()), "syssa", "master");

        // test pageBy
        var pageBy = this.provider.pageBy(1, 10, Conditions.of(LogStorage.class).like(LogStorage::getCode, "test%"), null, "master");
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
        assertEquals(insert.getType(), fetched.getType());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(insert.getRemark(), fetched.getRemark());
        assertEquals(insert.getParams(), fetched.getParams());

        // test deleteBy
        var count = this.provider.deleteBy(Conditions.of(LogStorage.class).eq(LogStorage::getCode, "test2"), "master");
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(LogStorageEntity.class).eq(LogStorageEntity::getCode, "test2"));
        assertEquals(0, count);
    }
}
