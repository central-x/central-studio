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

package central.studio.provider.graphql.gateway;

import central.data.gateway.GatewayFilter;
import central.data.gateway.GatewayFilterInput;
import central.data.gateway.GatewayPredicateInput;
import central.provider.graphql.gateway.GatewayFilterProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.gateway.GatewayFilterPersistence;
import central.studio.provider.database.persistence.gateway.entity.GatewayFilterEntity;
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
 * GatewayFilterProvider Test Cases
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestGatewayFilterProvider {

    @Setter(onMethod_ = @Autowired)
    private GatewayFilterProvider provider;

    @Setter(onMethod_ = @Autowired)
    private GatewayFilterPersistence persistence;

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
        this.persistence.deleteBy(Conditions.of(GatewayFilterEntity.class), tenant.getCode());
    }

    /**
     * @see GatewayFilterProvider#insert
     * @see GatewayFilterProvider#findById
     * @see GatewayFilterProvider#update
     * @see GatewayFilterProvider#findByIds
     * @see GatewayFilterProvider#countBy
     * @see GatewayFilterProvider#deleteByIds
     */
    @Test
    public void case1() {
        var tenant = this.context.getTenant();

        var input = GatewayFilterInput.builder()
                .type("add_request_header")
                .path("/")
                .order(0)
                .enabled(Boolean.TRUE)
                .remark("添加请求头")
                .params(Jsonx.Default().serialize(Map.of(
                        "header", "test",
                        "value", "test_value"
                )))
                .predicates(List.of(GatewayPredicateInput.builder()
                        .type("host")
                        .params(Jsonx.Default().serialize(Map.of(
                                "regexp", "127.0.0.1"
                        ))).build()))
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", tenant.getCode());
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getType(), insert.getType());
        assertEquals(input.getPath(), insert.getPath());
        assertEquals(input.getOrder(), insert.getOrder());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getParams(), insert.getParams());
        assertEquals(1, insert.getPredicates().size());
        assertEquals("host", insert.getPredicates().get(0).getType());

        // test findById
        var findById = this.provider.findById(insert.getId(), tenant.getCode());
        assertNotNull(findById);
        assertEquals(insert.getId(), findById.getId());
        assertEquals(insert.getType(), findById.getType());
        assertEquals(insert.getPath(), findById.getPath());
        assertEquals(insert.getOrder(), findById.getOrder());
        assertEquals(insert.getEnabled(), findById.getEnabled());
        assertEquals(insert.getParams(), findById.getParams());
        assertEquals(insert.getPredicates(), findById.getPredicates());

        // test countBy
        var count = this.provider.countBy(Conditions.of(GatewayFilter.class), tenant.getCode());
        assertEquals(1, count);

        // test update
        this.provider.update(insert.toInput().path("/test").enabled(Boolean.FALSE).build(), "syssa", tenant.getCode());

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), tenant.getCode());
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getType(), fetched.getType());
        assertEquals("/test", fetched.getPath());
        assertEquals(insert.getOrder(), fetched.getOrder());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(insert.getParams(), fetched.getParams());
        assertEquals(insert.getPredicates(), fetched.getPredicates());

        // test deleteByIds
        count = this.provider.deleteByIds(List.of(insert.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(GatewayFilterEntity.class), tenant.getCode());
        assertEquals(0, count);
    }

    /**
     * @see GatewayFilterProvider#insertBatch
     * @see GatewayFilterProvider#findBy
     * @see GatewayFilterProvider#updateBatch
     * @see GatewayFilterProvider#pageBy
     * @see GatewayFilterProvider#deleteBy
     */
    @Test
    public void case2() {
        var tenant = this.context.getTenant();

        var input = GatewayFilterInput.builder()
                .type("add_request_header")
                .path("/")
                .order(0)
                .enabled(Boolean.TRUE)
                .remark("添加请求头")
                .params(Jsonx.Default().serialize(Map.of(
                        "header", "test",
                        "value", "test_value"
                )))
                .predicates(List.of(GatewayPredicateInput.builder()
                        .type("host")
                        .params(Jsonx.Default().serialize(Map.of(
                                "regexp", "127.0.0.1"
                        ))).build()))
                .build();

        // test insertBatch
        var insertBatch = this.provider.insertBatch(List.of(input), "syssa", tenant.getCode());
        assertNotNull(insertBatch);
        assertEquals(1, insertBatch.size());

        var insert = Listx.getFirstOrNull(insertBatch);
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getType(), insert.getType());
        assertEquals(input.getPath(), insert.getPath());
        assertEquals(input.getOrder(), insert.getOrder());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getParams(), insert.getParams());
        assertEquals(1, insert.getPredicates().size());
        assertEquals("host", insert.getPredicates().get(0).getType());

        // test findBy
        var findBy = this.provider.findBy(null, null, null, null, tenant.getCode());
        assertNotNull(findBy);
        assertEquals(1, findBy.size());

        var fetched = Listx.getFirstOrNull(findBy);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getType(), fetched.getType());
        assertEquals(insert.getPath(), fetched.getPath());
        assertEquals(insert.getOrder(), fetched.getOrder());
        assertEquals(insert.getEnabled(), fetched.getEnabled());
        assertEquals(insert.getParams(), fetched.getParams());
        assertEquals(insert.getPredicates().size(), fetched.getPredicates().size());
        assertEquals(insert.getPredicates().get(0).getType(), fetched.getPredicates().get(0).getType());

        // test updateBatch
        this.provider.updateBatch(List.of(insert.toInput().path("/test").enabled(Boolean.FALSE).build()), "syssa", tenant.getCode());

        // test pageBy
        var pageBy = this.provider.pageBy(1, 10, null, null, tenant.getCode());
        assertNotNull(pageBy);
        assertEquals(1, pageBy.getPager().getPageIndex());
        assertEquals(10, pageBy.getPager().getPageSize());
        assertEquals(1, pageBy.getPager().getPageCount());
        assertEquals(1, pageBy.getPager().getItemCount());
        assertEquals(1, pageBy.getData().size());

        fetched = Listx.getFirstOrNull(pageBy.getData());
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getType(), fetched.getType());
        assertEquals("/test", fetched.getPath());
        assertEquals(insert.getOrder(), fetched.getOrder());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(insert.getParams(), fetched.getParams());
        assertEquals(insert.getPredicates().size(), fetched.getPredicates().size());
        assertEquals(insert.getPredicates().get(0).getType(), fetched.getPredicates().get(0).getType());

        // test deleteBy
        var count = this.provider.deleteBy(Conditions.of(GatewayFilter.class).eq(GatewayFilter::getPath, "/test"), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(GatewayFilterEntity.class), tenant.getCode());
        assertEquals(0, count);
    }
}
