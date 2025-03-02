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

package central.studio.provider.graphql.identity;

import central.data.identity.IdentityRecord;
import central.data.identity.IdentityRecordInput;
import central.provider.graphql.identity.IdentityRecordProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.identity.IdentityRecordPersistence;
import central.studio.provider.database.persistence.identity.entity.IdentityRecordEntity;
import central.studio.provider.graphql.TestContext;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Identity Record Test Cases
 *
 * @author Alan Yeh
 * @since 2025/03/02
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestIdentityRecordProvider {

    @Setter(onMethod_ = @Autowired)
    private IdentityRecordProvider provider;

    @Setter(onMethod_ = @Autowired)
    private IdentityRecordPersistence persistence;

    @Setter(onMethod_ = @Autowired)
    private TestContext context;

    @BeforeAll
    public static void setup(@Autowired DataContext context) throws Exception {
        SaasContainer container = null;
        while (container == null || container.getApplications().isEmpty()) {
            Thread.sleep(100);
            container = context.getData(DataFetcherType.SAAS);
        }
    }

    @AfterEach
    public void clear() throws Exception {
        var tenant = this.context.getTenant();
        // 清空测试数据
        this.persistence.deleteBy(Conditions.of(IdentityRecordEntity.class), tenant.getCode());
    }

    /**
     * @see IdentityRecordProvider#insert
     * @see IdentityRecordProvider#findById
     * @see IdentityRecordProvider#update
     * @see IdentityRecordProvider#findByIds
     * @see IdentityRecordProvider#countBy
     * @see IdentityRecordProvider#deleteByIds
     */
    @Test
    public void case1() {
        var tenant = this.context.getTenant();

        var input = IdentityRecordInput.builder()
                .address("Unknow")
                .host("127.0.0.1")
                .device("Safari on macOS")
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", tenant.getCode());
        assertNotNull(insert);
        assertEquals(input.getAddress(), insert.getAddress());
        assertEquals(input.getHost(), insert.getHost());
        assertEquals(input.getDevice(), insert.getDevice());
        assertNotNull(insert.getCreateDate());
        assertNotNull(insert.getCreatorId());
        assertNotNull(insert.getCreator());
        assertEquals("syssa", insert.getCreator().getId());
        assertNotNull(insert.getModifyDate());
        assertNotNull(insert.getModifierId());
        assertNotNull(insert.getModifier());
        assertEquals("syssa", insert.getModifier().getId());

        var entity = this.persistence.findById(insert.getId(), Columns.all(), tenant.getCode());
        assertNotNull(entity);

        // test findById
        var findById = this.provider.findById(insert.getId(), tenant.getCode());
        assertNotNull(findById);
        assertEquals(input.getAddress(), findById.getAddress());
        assertEquals(input.getHost(), findById.getHost());
        assertEquals(input.getDevice(), findById.getDevice());

        // test countBy
        var count = this.provider.countBy(Conditions.of(IdentityRecord.class), tenant.getCode());
        assertEquals(1, count);

        // test update
        this.provider.update(insert.toInput().address("localhost").build(), "syssa", tenant.getCode());

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), tenant.getCode());
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertEquals("localhost", fetched.getAddress());
        assertEquals(input.getHost(), fetched.getHost());
        assertEquals(input.getDevice(), fetched.getDevice());

        // test deleteById
        count = this.provider.deleteByIds(List.of(insert.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(IdentityRecordEntity.class), tenant.getCode());
        assertEquals(0, count);
    }

    /**
     * @see IdentityRecordProvider#insertBatch
     * @see IdentityRecordProvider#findBy
     * @see IdentityRecordProvider#updateBatch
     * @see IdentityRecordProvider#pageBy
     * @see IdentityRecordProvider#deleteBy
     */
    @Test
    public void case2() {
        var tenant = this.context.getTenant();

        var input = IdentityRecordInput.builder()
                .address("Unknow")
                .host("127.0.0.1")
                .device("Safari on macOS")
                .build();

        // test insertBatch
        var insertBatch = this.provider.insertBatch(List.of(input), "syssa", tenant.getCode());
        assertNotNull(insertBatch);
        assertEquals(1, insertBatch.size());

        var insert = Listx.getFirstOrNull(insertBatch);
        assertNotNull(insert);
        assertEquals(input.getAddress(), insert.getAddress());
        assertEquals(input.getHost(), insert.getHost());
        assertEquals(input.getDevice(), insert.getDevice());
        assertNotNull(insert.getCreateDate());
        assertNotNull(insert.getCreatorId());
        assertNotNull(insert.getCreator());
        assertEquals("syssa", insert.getCreator().getId());
        assertNotNull(insert.getModifyDate());
        assertNotNull(insert.getModifierId());
        assertNotNull(insert.getModifier());
        var entity = this.persistence.findById(insert.getId(), Columns.all(), tenant.getCode());
        assertNotNull(entity);

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(IdentityRecord.class), null, tenant.getCode());
        assertNotNull(findBy);
        assertEquals(1, findBy.size());

        var fetched = Listx.getFirstOrNull(findBy);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(input.getAddress(), fetched.getAddress());
        assertEquals(input.getHost(), fetched.getHost());
        assertEquals(input.getDevice(), fetched.getDevice());

        // test updateBatch
        this.provider.updateBatch(List.of(fetched.toInput().address("localhost").build()), "syssa", tenant.getCode());

        // test pageBy
        var pageBy = this.provider.pageBy(1, 10, Conditions.of(IdentityRecord.class), null, tenant.getCode());
        assertNotNull(pageBy);
        assertEquals(1, pageBy.getPager().getPageIndex());
        assertEquals(10, pageBy.getPager().getPageSize());
        assertEquals(1, pageBy.getPager().getPageCount());
        assertEquals(1, pageBy.getPager().getItemCount());
        assertEquals(1, pageBy.getData().size());

        fetched = Listx.getFirstOrNull(pageBy.getData());
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals("localhost", fetched.getAddress());
        assertEquals(insert.getHost(), fetched.getHost());
        assertEquals(insert.getDevice(), fetched.getDevice());

        // test deleteBy
        var count = this.provider.deleteBy(Conditions.of(IdentityRecord.class), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(IdentityRecordEntity.class), tenant.getCode());
        assertEquals(0, count);
    }
}
