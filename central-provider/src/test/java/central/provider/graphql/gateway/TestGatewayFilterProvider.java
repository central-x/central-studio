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

package central.provider.graphql.gateway;

import central.data.gateway.GatewayFilter;
import central.data.gateway.GatewayFilterInput;
import central.data.gateway.GatewayPredicate;
import central.data.gateway.GatewayPredicateInput;
import central.lang.reflect.TypeRef;
import central.studio.provider.ApplicationProperties;
import central.studio.provider.ProviderApplication;
import central.studio.provider.graphql.gateway.entity.GatewayFilterEntity;
import central.studio.provider.graphql.gateway.mapper.GatewayFilterMapper;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * GatewayFilterProvider Test Cases
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestGatewayFilterProvider {
    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    @Setter(onMethod_ = @Autowired)
    private GatewayFilterProvider provider;

    @Setter(onMethod_ = @Autowired)
    private GatewayFilterMapper mapper;

    @BeforeEach
    @AfterEach
    public void clear() throws Exception {
        // 清空测试数据
        this.mapper.deleteAll();
    }


    /**
     * @see GatewayFilterProvider#findById
     */
    @Test
    public void case1() {
        var entity = new GatewayFilterEntity();
        entity.setType("add_request_header");
        entity.setPath("/");
        entity.setOrder(0);
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("添加请求头");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "header", "test",
                "value", "test_value"
        )));
        entity.setPredicateJson(Jsonx.Default().serialize(List.of(
                new GatewayPredicate("host", Jsonx.Default().serialize(Map.of(
                        "regexp", "127.0.0.1"
                )))
        )));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var filter = this.provider.findById(entity.getId());
        assertNotNull(filter);
        assertEquals(entity.getId(), filter.getId());
        assertNotNull(filter.getType());
        assertNotNull(filter.getPath());
        assertNotNull(filter.getOrder());
        assertNotNull(filter.getEnabled());
        assertNotNull(filter.getRemark());
        assertNotNull(filter.getParams());
        var params = Jsonx.Default().deserialize(filter.getParams(), TypeRef.ofMap(String.class, Object.class));
        assertNotNull(params);
        assertEquals(2, params.size());

        // 断言
        assertNotNull(filter.getPredicates());
        assertEquals(1, filter.getPredicates().size());
        assertEquals("host", filter.getPredicates().get(0).getType());

        // 关联查询
        assertNotNull(filter.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), filter.getCreator().getId());
        assertNotNull(filter.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), filter.getModifier().getId());
    }

    /**
     * @see GatewayFilterProvider#findByIds
     */
    @Test
    public void case2() {
        var entity = new GatewayFilterEntity();
        entity.setType("add_request_header");
        entity.setPath("/");
        entity.setOrder(0);
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("添加请求头");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "header", "test",
                "value", "test_value"
        )));
        entity.setPredicateJson(Jsonx.Default().serialize(List.of(
                new GatewayPredicate("host", Jsonx.Default().serialize(Map.of(
                        "regexp", "127.0.0.1"
                )))
        )));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var filters = this.provider.findByIds(List.of(entity.getId()));
        assertNotNull(filters);
        assertEquals(1, filters.size());

        var filter = Listx.getFirstOrNull(filters);
        assertNotNull(filter);
        assertEquals(entity.getId(), filter.getId());
        assertNotNull(filter.getType());
        assertNotNull(filter.getPath());
        assertNotNull(filter.getOrder());
        assertNotNull(filter.getEnabled());
        assertNotNull(filter.getRemark());
        assertNotNull(filter.getParams());
        var params = Jsonx.Default().deserialize(filter.getParams(), TypeRef.ofMap(String.class, Object.class));
        assertNotNull(params);
        assertEquals(2, params.size());

        // 断言
        assertNotNull(filter.getPredicates());
        assertEquals(1, filter.getPredicates().size());
        assertEquals("host", filter.getPredicates().get(0).getType());

        // 关联查询
        assertNotNull(filter.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), filter.getCreator().getId());
        assertNotNull(filter.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), filter.getModifier().getId());
    }

    /**
     * @see GatewayFilterProvider#findBy
     */
    @Test
    public void case3() {
        var entity = new GatewayFilterEntity();
        entity.setType("add_request_header");
        entity.setPath("/");
        entity.setOrder(0);
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("添加请求头");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "header", "test",
                "value", "test_value"
        )));
        entity.setPredicateJson(Jsonx.Default().serialize(List.of(
                new GatewayPredicate("host", Jsonx.Default().serialize(Map.of(
                        "regexp", "127.0.0.1"
                )))
        )));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var filters = this.provider.findBy(null, null, Conditions.of(GatewayFilter.class).eq(GatewayFilter::getType, "add_request_header"), null);
        assertNotNull(filters);
        assertEquals(1, filters.size());

        var filter = Listx.getFirstOrNull(filters);
        assertNotNull(filter);
        assertEquals(entity.getId(), filter.getId());
        assertNotNull(filter.getType());
        assertNotNull(filter.getPath());
        assertNotNull(filter.getOrder());
        assertNotNull(filter.getEnabled());
        assertNotNull(filter.getRemark());
        assertNotNull(filter.getParams());
        var params = Jsonx.Default().deserialize(filter.getParams(), TypeRef.ofMap(String.class, Object.class));
        assertNotNull(params);
        assertEquals(2, params.size());

        // 断言
        assertNotNull(filter.getPredicates());
        assertEquals(1, filter.getPredicates().size());
        assertEquals("host", filter.getPredicates().get(0).getType());

        // 关联查询
        assertNotNull(filter.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), filter.getCreator().getId());
        assertNotNull(filter.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), filter.getModifier().getId());
    }

    /**
     * @see GatewayFilterProvider#pageBy
     */
    @Test
    public void case4() {
        var entity = new GatewayFilterEntity();
        entity.setType("add_request_header");
        entity.setPath("/");
        entity.setOrder(0);
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("添加请求头");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "header", "test",
                "value", "test_value"
        )));
        entity.setPredicateJson(Jsonx.Default().serialize(List.of(
                new GatewayPredicate("host", Jsonx.Default().serialize(Map.of(
                        "regexp", "127.0.0.1"
                )))
        )));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(GatewayFilter.class).eq(GatewayFilter::getType, "add_request_header"), null);
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1, page.getPager().getPageIndex());
        assertEquals(20, page.getPager().getPageSize());
        assertEquals(1, page.getPager().getPageCount());
        assertEquals(1, page.getPager().getItemCount());

        var filter = Listx.getFirstOrNull(page.getData());
        assertNotNull(filter);
        assertEquals(entity.getId(), filter.getId());
        assertNotNull(filter.getType());
        assertNotNull(filter.getPath());
        assertNotNull(filter.getOrder());
        assertNotNull(filter.getEnabled());
        assertNotNull(filter.getRemark());
        assertNotNull(filter.getParams());
        var params = Jsonx.Default().deserialize(filter.getParams(), TypeRef.ofMap(String.class, Object.class));
        assertNotNull(params);
        assertEquals(2, params.size());

        // 断言
        assertNotNull(filter.getPredicates());
        assertEquals(1, filter.getPredicates().size());
        assertEquals("host", filter.getPredicates().get(0).getType());

        // 关联查询
        assertNotNull(filter.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), filter.getCreator().getId());
        assertNotNull(filter.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), filter.getModifier().getId());
    }

    /**
     * @see GatewayFilterProvider#countBy
     */
    @Test
    public void case5() {
        var entity = new GatewayFilterEntity();
        entity.setType("add_request_header");
        entity.setPath("/");
        entity.setOrder(0);
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("添加请求头");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "header", "test",
                "value", "test_value"
        )));
        entity.setPredicateJson(Jsonx.Default().serialize(List.of(
                new GatewayPredicate("host", Jsonx.Default().serialize(Map.of(
                        "regexp", "127.0.0.1"
                )))
        )));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var count = this.provider.countBy(Conditions.of(GatewayFilter.class).eq(GatewayFilter::getType, "add_request_header"));
        assertNotNull(count);
        assertEquals(1, count);
    }

    /**
     * @see GatewayFilterProvider#insert
     */
    @Test
    public void case6() {
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
                .predicates(List.of(
                        new GatewayPredicateInput("host", Jsonx.Default().serialize(Map.of(
                                "regexp", "127.0.0.1"
                        )))
                ))
                .build();

        // 查询数据
        var entity = this.provider.insert(input, properties.getSupervisor().getUsername());

        assertNotNull(entity);
        assertNotNull(entity.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(GatewayFilterEntity.class).eq(GatewayFilterEntity::getId, entity.getId())));
    }

    /**
     * @see GatewayFilterProvider#insertBatch
     */
    @Test
    public void case7() {
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
                .predicates(List.of(
                        new GatewayPredicateInput("host", Jsonx.Default().serialize(Map.of(
                                "regexp", "127.0.0.1"
                        )))
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
        assertTrue(this.mapper.existsBy(Conditions.of(GatewayFilterEntity.class).eq(GatewayFilterEntity::getId, entity.getId())));
    }

    /**
     * @see GatewayFilterProvider#update
     */
    @Test
    public void case8() {
        var entity = new GatewayFilterEntity();
        entity.setType("add_request_header");
        entity.setPath("/");
        entity.setOrder(0);
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("添加请求头");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "header", "test",
                "value", "test_value"
        )));
        entity.setPredicateJson(Jsonx.Default().serialize(List.of(
                new GatewayPredicate("host", Jsonx.Default().serialize(Map.of(
                        "regexp", "127.0.0.1"
                )))
        )));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var filter = this.provider.findById(entity.getId());
        assertNotNull(filter);
        assertEquals(entity.getId(), filter.getId());

        var input = filter.toInput().toBuilder()
                .path("/test")
                .build();

        // 更新数据
        filter = this.provider.update(input, properties.getSupervisor().getUsername());
        assertNotNull(filter);
        assertEquals(entity.getId(), filter.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(GatewayFilterEntity.class).eq(GatewayFilterEntity::getPath, "/test")));
    }

    /**
     * @see GatewayFilterProvider#updateBatch
     */
    @Test
    public void case9() {
        var entity = new GatewayFilterEntity();
        entity.setType("add_request_header");
        entity.setPath("/");
        entity.setOrder(0);
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("添加请求头");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "header", "test",
                "value", "test_value"
        )));
        entity.setPredicateJson(Jsonx.Default().serialize(List.of(
                new GatewayPredicate("host", Jsonx.Default().serialize(Map.of(
                        "regexp", "127.0.0.1"
                )))
        )));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var filter = this.provider.findById(entity.getId());
        assertNotNull(filter);
        assertEquals(entity.getId(), filter.getId());

        var input = filter.toInput().toBuilder()
                .path("/test")
                .build();

        // 更新数据
        var filters = this.provider.updateBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(filters);
        assertEquals(1, filters.size());

        filter = Listx.getFirstOrNull(filters);
        assertNotNull(filter);
        assertEquals(entity.getId(), filter.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(GatewayFilterEntity.class).eq(GatewayFilterEntity::getPath, "/test")));
    }

    /**
     * @see GatewayFilterProvider#deleteByIds
     */
    @Test
    public void case10() {
        var entity = new GatewayFilterEntity();
        entity.setType("add_request_header");
        entity.setPath("/");
        entity.setOrder(0);
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("添加请求头");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "header", "test",
                "value", "test_value"
        )));
        entity.setPredicateJson(Jsonx.Default().serialize(List.of(
                new GatewayPredicate("host", Jsonx.Default().serialize(Map.of(
                        "regexp", "127.0.0.1"
                )))
        )));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var deleted = this.provider.deleteByIds(List.of(entity.getId()));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(GatewayFilterEntity.class).eq(GatewayFilterEntity::getId, entity.getId())));
    }

    /**
     * @see GatewayFilterProvider#deleteBy(Conditions)
     */
    @Test
    public void case11() {
        var entity = new GatewayFilterEntity();
        entity.setType("add_request_header");
        entity.setPath("/");
        entity.setOrder(0);
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("添加请求头");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "header", "test",
                "value", "test_value"
        )));
        entity.setPredicateJson(Jsonx.Default().serialize(List.of(
                new GatewayPredicate("host", Jsonx.Default().serialize(Map.of(
                        "regexp", "127.0.0.1"
                )))
        )));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var deleted = this.provider.deleteBy(Conditions.of(GatewayFilter.class).eq(GatewayFilter::getType, "add_request_header"));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(GatewayFilterEntity.class).eq(GatewayFilterEntity::getId, entity.getId())));
    }
}
