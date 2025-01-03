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

import central.data.identity.IdentityStrategy;
import central.data.identity.IdentityStrategyInput;
import central.provider.graphql.identity.IdentityStrategyProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.identity.IdentityStrategyPersistence;
import central.studio.provider.database.persistence.identity.entity.IdentityStrategyEntity;
import central.studio.provider.graphql.TestContext;
import central.util.Jsonx;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Security Strategy Test Cases
 *
 * @author Alan Yeh
 * @since 2022/11/05
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestIdentityStrategyProvider {

    @Setter(onMethod_ = @Autowired)
    private IdentityStrategyProvider provider;

    @Setter(onMethod_ = @Autowired)
    private IdentityStrategyPersistence persistence;

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
        this.persistence.deleteBy(Conditions.of(IdentityStrategyEntity.class), tenant.getCode());
    }


    /**
     * @see IdentityStrategyProvider#insert
     * @see IdentityStrategyProvider#findById
     * @see IdentityStrategyProvider#update
     * @see IdentityStrategyProvider#findByIds
     * @see IdentityStrategyProvider#countBy
     * @see IdentityStrategyProvider#deleteByIds
     */
    @Test
    public void case1() {
        var tenant = this.context.getTenant();

        var input = IdentityStrategyInput.builder()
                .code("captcha")
                .name("验证码策略")
                .type("captcha")
                .enabled(Boolean.TRUE)
                .remark("用于控制验证码行为")
                .params(Jsonx.Default().serialize(Map.of("enabled", "true")))
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", tenant.getCode());
        assertNotNull(insert);
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getType(), insert.getType());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getRemark(), insert.getRemark());
        assertEquals(input.getParams(), insert.getParams());
        var entity = this.persistence.findById(insert.getId(), Columns.all(), tenant.getCode());
        assertNotNull(entity);

        // test findById
        var findById = this.provider.findById(insert.getId(), tenant.getCode());
        assertNotNull(findById);
        assertEquals(input.getCode(), findById.getCode());
        assertEquals(input.getName(), findById.getName());
        assertEquals(input.getType(), findById.getType());
        assertEquals(input.getEnabled(), findById.getEnabled());
        assertEquals(input.getRemark(), findById.getRemark());
        assertEquals(input.getParams(), findById.getParams());

        // test countBy
        var count = this.provider.countBy(Conditions.of(IdentityStrategy.class), tenant.getCode());
        assertEquals(1, count);

        // test update
        this.provider.update(insert.toInput().code("captcha2").enabled(Boolean.FALSE).build(), "syssa", tenant.getCode());

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), tenant.getCode());
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertEquals("captcha2", fetched.getCode());
        assertEquals(input.getName(), fetched.getName());
        assertEquals(input.getType(), fetched.getType());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(input.getRemark(), fetched.getRemark());
        assertEquals(input.getParams(), fetched.getParams());

        // test deleteById
        count = this.provider.deleteByIds(List.of(insert.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(IdentityStrategyEntity.class), tenant.getCode());
        assertEquals(0, count);
    }

    /**
     * @see IdentityStrategyProvider#insertBatch
     * @see IdentityStrategyProvider#findBy
     * @see IdentityStrategyProvider#updateBatch
     * @see IdentityStrategyProvider#pageBy
     * @see IdentityStrategyProvider#deleteBy
     */
    @Test
    public void case2() {
        var tenant = this.context.getTenant();

        var input = IdentityStrategyInput.builder()
                .code("captcha")
                .name("验证码策略")
                .type("captcha")
                .enabled(Boolean.TRUE)
                .remark("用于控制验证码行为")
                .params(Jsonx.Default().serialize(Map.of("enabled", "true")))
                .build();

        // test insertBatch
        var insertBatch = this.provider.insertBatch(List.of(input), "syssa", tenant.getCode());
        assertNotNull(insertBatch);
        assertEquals(1, insertBatch.size());

        var insert = Listx.getFirstOrNull(insertBatch);
        assertNotNull(insert);
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getType(), insert.getType());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getRemark(), insert.getRemark());
        assertEquals(input.getParams(), insert.getParams());
        var entity = this.persistence.findById(insert.getId(), Columns.all(), tenant.getCode());
        assertNotNull(entity);

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(IdentityStrategy.class), null, tenant.getCode());
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
        this.provider.updateBatch(List.of(fetched.toInput().code("captcha2").enabled(Boolean.FALSE).build()), "syssa", tenant.getCode());

        // test pageBy
        var pageBy = this.provider.pageBy(1, 10, Conditions.of(IdentityStrategy.class), null, tenant.getCode());
        assertNotNull(pageBy);
        assertEquals(1, pageBy.getPager().getPageIndex());
        assertEquals(10, pageBy.getPager().getPageSize());
        assertEquals(1, pageBy.getPager().getPageCount());
        assertEquals(1, pageBy.getPager().getItemCount());
        assertEquals(1, pageBy.getData().size());

        fetched = Listx.getFirstOrNull(pageBy.getData());
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals("captcha2", fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getType(), fetched.getType());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(insert.getRemark(), fetched.getRemark());
        assertEquals(insert.getParams(), fetched.getParams());

        // test deleteBy
        var count = this.provider.deleteBy(Conditions.of(IdentityStrategy.class), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(IdentityStrategyEntity.class), tenant.getCode());
        assertEquals(0, count);
    }
}
