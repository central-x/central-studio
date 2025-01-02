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

import central.data.log.LogCollector;
import central.data.log.LogCollectorInput;
import central.provider.graphql.log.LogCollectorProvider;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.log.LogCollectorPersistence;
import central.studio.provider.database.persistence.log.entity.LogCollectorEntity;
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
    private LogCollectorProvider provider;

    @Setter(onMethod_ = @Autowired)
    private LogCollectorPersistence persistence;

    @AfterEach
    public void clear() throws Exception {
        // 清空测试数据
        this.persistence.deleteBy(Conditions.of(LogCollectorEntity.class).like(LogCollectorEntity::getCode, "test%"));
    }

    /**
     * @see LogCollectorProvider#insert
     * @see LogCollectorProvider#findById
     * @see LogCollectorProvider#update
     * @see LogCollectorProvider#findByIds
     * @see LogCollectorProvider#countBy
     * @see LogCollectorProvider#deleteByIds
     */
    @Test
    public void case1() {
        var input = LogCollectorInput.builder()
                .code("test")
                .name("测试采集器")
                .type("http")
                .enabled(Boolean.TRUE)
                .remark("测试采集器")
                .params(Jsonx.Default().serialize(Map.of("path", "central")))
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
        assertEquals(insert.getCode(), findById.getCode());
        assertEquals(insert.getName(), findById.getName());
        assertEquals(insert.getType(), findById.getType());
        assertEquals(insert.getEnabled(), findById.getEnabled());
        assertEquals(insert.getRemark(), findById.getRemark());
        assertEquals(insert.getParams(), findById.getParams());

        // test countBy
        var count = this.provider.countBy(Conditions.of(LogCollector.class).like(LogCollector::getCode, "test%"), "master");
        assertEquals(1, count);

        // test update
        this.provider.update(insert.toInput().code("test2").enabled(Boolean.FALSE).build(), "syssa", "master");

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), "master");
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertEquals("test2", fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getType(), fetched.getType());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(insert.getRemark(), fetched.getRemark());
        assertEquals(insert.getParams(), fetched.getParams());

        // test deleteById
        count = this.provider.deleteByIds(List.of(insert.getId()), "master");
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(LogCollectorEntity.class).eq(LogCollectorEntity::getCode, "test2"));
        assertEquals(0, count);
    }

    /**
     * @see LogCollectorProvider#insertBatch
     * @see LogCollectorProvider#findBy
     * @see LogCollectorProvider#updateBatch
     * @see LogCollectorProvider#pageBy
     * @see LogCollectorProvider#deleteBy
     */
    @Test
    public void case2() {
        var input = LogCollectorInput.builder()
                .code("test")
                .name("测试采集器")
                .type("http")
                .enabled(Boolean.TRUE)
                .remark("测试采集器")
                .params(Jsonx.Default().serialize(Map.of("path", "central")))
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
        var findBy = this.provider.findBy(null, null, Conditions.of(LogCollector.class).eq(LogCollector::getCode, "test"), null, "master");
        assertNotNull(findBy);
        assertEquals(1, findBy.size());

        var fetched = Listx.getFirstOrNull(findBy);
        assertNotNull(fetched);
        assertEquals(insert.getCode(), fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getType(), fetched.getType());
        assertEquals(insert.getEnabled(), fetched.getEnabled());
        assertEquals(insert.getRemark(), fetched.getRemark());
        assertEquals(insert.getParams(), fetched.getParams());

        // test countBy
        var count = this.provider.countBy(Conditions.of(LogCollector.class).eq(LogCollector::getCode, "test"), "master");
        assertEquals(1, count);

        // test updateBatch
        this.provider.updateBatch(List.of(insert.toInput().code("test2").enabled(Boolean.FALSE).build()), "syssa", "master");

        // test pageBy
        var pageBy = this.provider.pageBy(1, 10, null, null, "master");
        assertNotNull(pageBy);
        assertEquals(1, pageBy.getPager().getPageIndex());
        assertEquals(10, pageBy.getPager().getPageSize());
        assertEquals(1, pageBy.getPager().getPageCount());
        assertEquals(1, pageBy.getPager().getItemCount());
        assertEquals(1, pageBy.getData().size());

        fetched = Listx.getFirstOrNull(pageBy.getData());
        assertNotNull(fetched);
        assertEquals("test2", fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getType(), fetched.getType());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(insert.getRemark(), fetched.getRemark());
        assertEquals(insert.getParams(), fetched.getParams());

        // test deleteBy
        count = this.provider.deleteBy(Conditions.of(LogCollector.class).eq(LogCollector::getCode, "test2"), "master");
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(LogCollectorEntity.class).eq(LogCollectorEntity::getCode, "test2"));
        assertEquals(0, count);
    }
}
