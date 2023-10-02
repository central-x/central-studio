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

package central.provider.graphql.security;

import central.data.security.SecurityStrategy;
import central.data.security.SecurityStrategyInput;
import central.lang.reflect.TypeRef;
import central.provider.ApplicationProperties;
import central.provider.ProviderApplication;
import central.provider.graphql.security.entity.SecurityStrategyEntity;
import central.provider.graphql.security.mapper.SecurityStrategyMapper;
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
 * Security Strategy Test Cases
 *
 * @author Alan Yeh
 * @since 2022/11/05
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestSecurityStrategyProvider {

    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    @Setter(onMethod_ = @Autowired)
    private SecurityStrategyProvider provider;

    @Setter(onMethod_ = @Autowired)
    private SecurityStrategyMapper mapper;

    @BeforeEach
    @AfterEach
    public void clear() throws Exception {
        // 清空测试数据
        this.mapper.deleteAll();
    }


    /**
     * @see SecurityStrategyProvider#findById
     */
    @Test
    public void case1() {
        var entity = new SecurityStrategyEntity();
        entity.setCode("captcha");
        entity.setName("验证码策略");
        entity.setType("captcha");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于控制验证码行为");
        entity.setParams(Jsonx.Default().serialize(Map.of("enabled", "true")));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var strategy = this.provider.findById(entity.getId());
        assertNotNull(strategy);
        assertEquals(entity.getId(), strategy.getId());
        assertNotNull(strategy.getCode());
        assertNotNull(strategy.getName());
        assertNotNull(strategy.getType());
        assertNotNull(strategy.getEnabled());
        assertNotNull(strategy.getRemark());
        assertNotNull(strategy.getParams());
        var params = Jsonx.Default().deserialize(strategy.getParams(), TypeRef.ofMap(String.class, Object.class));
        assertNotNull(params);
        assertEquals(1, params.size());
        // 关联查询
        assertNotNull(strategy.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), strategy.getCreator().getId());
        assertNotNull(strategy.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), strategy.getModifier().getId());
    }

    /**
     * @see SecurityStrategyProvider#findByIds
     */
    @Test
    public void case2() {
        var entity = new SecurityStrategyEntity();
        entity.setCode("captcha");
        entity.setName("验证码策略");
        entity.setType("captcha");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于控制验证码行为");
        entity.setParams(Jsonx.Default().serialize(Map.of("enabled", "true")));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var strategies = this.provider.findByIds(List.of(entity.getId()));
        assertNotNull(strategies);
        assertEquals(1, strategies.size());

        var strategy = Listx.getFirstOrNull(strategies);
        assertNotNull(strategy);
        assertEquals(entity.getId(), strategy.getId());
        assertNotNull(strategy.getCode());
        assertNotNull(strategy.getName());
        assertNotNull(strategy.getType());
        assertNotNull(strategy.getEnabled());
        assertNotNull(strategy.getRemark());
        assertNotNull(strategy.getParams());
        // 关联查询
        assertNotNull(strategy.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), strategy.getCreator().getId());
        assertNotNull(strategy.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), strategy.getModifier().getId());
    }

    /**
     * @see SecurityStrategyProvider#findBy
     */
    @Test
    public void case3() {
        var entity = new SecurityStrategyEntity();
        entity.setCode("captcha");
        entity.setName("验证码策略");
        entity.setType("captcha");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于控制验证码行为");
        entity.setParams(Jsonx.Default().serialize(Map.of("enabled", "true")));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var strategies = this.provider.findBy(null, null, Conditions.of(SecurityStrategy.class).eq(SecurityStrategy::getCode, "captcha"), null);
        assertNotNull(strategies);
        assertEquals(1, strategies.size());

        var strategy = Listx.getFirstOrNull(strategies);
        assertNotNull(strategy);
        assertEquals(entity.getId(), strategy.getId());
        assertNotNull(strategy.getCode());
        assertNotNull(strategy.getName());
        assertNotNull(strategy.getType());
        assertNotNull(strategy.getEnabled());
        assertNotNull(strategy.getRemark());
        assertNotNull(strategy.getParams());
        // 关联查询
        assertNotNull(strategy.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), strategy.getCreator().getId());
        assertNotNull(strategy.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), strategy.getModifier().getId());
    }

    /**
     * @see SecurityStrategyProvider#pageBy
     */
    @Test
    public void case4() {
        var entity = new SecurityStrategyEntity();
        entity.setCode("captcha");
        entity.setName("验证码策略");
        entity.setType("captcha");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于控制验证码行为");
        entity.setParams(Jsonx.Default().serialize(Map.of("enabled", "true")));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(SecurityStrategy.class).eq(SecurityStrategy::getCode, "captcha"), null);
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1, page.getPager().getPageIndex());
        assertEquals(20, page.getPager().getPageSize());
        assertEquals(1, page.getPager().getPageCount());
        assertEquals(1, page.getPager().getItemCount());

        var strategy = Listx.getFirstOrNull(page.getData());
        assertNotNull(strategy);
        assertEquals(entity.getId(), strategy.getId());
        assertNotNull(strategy.getCode());
        assertNotNull(strategy.getName());
        assertNotNull(strategy.getType());
        assertNotNull(strategy.getEnabled());
        assertNotNull(strategy.getRemark());
        assertNotNull(strategy.getParams());
        // 关联查询
        assertNotNull(strategy.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), strategy.getCreator().getId());
        assertNotNull(strategy.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), strategy.getModifier().getId());
    }

    /**
     * @see SecurityStrategyProvider#countBy
     */
    @Test
    public void case5() {
        var entity = new SecurityStrategyEntity();
        entity.setCode("captcha");
        entity.setName("验证码策略");
        entity.setType("captcha");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于控制验证码行为");
        entity.setParams(Jsonx.Default().serialize(Map.of("enabled", "true")));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var count = this.provider.countBy(Conditions.of(SecurityStrategy.class).eq(SecurityStrategy::getCode, "captcha"));
        assertNotNull(count);
        assertEquals(1, count);
    }

    /**
     * @see SecurityStrategyProvider#insert
     */
    @Test
    public void case6() {
        var input = SecurityStrategyInput.builder()
                .code("captcha")
                .name("验证码策略")
                .type("captcha")
                .enabled(Boolean.TRUE)
                .remark("用于控制验证码行为")
                .params(Jsonx.Default().serialize(Map.of("enabled", "true")))
                .build();

        // 查询数据
        var entity = this.provider.insert(input, properties.getSupervisor().getUsername());

        assertNotNull(entity);
        assertNotNull(entity.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(SecurityStrategyEntity.class).eq(SecurityStrategyEntity::getId, entity.getId())));
    }

    /**
     * @see SecurityStrategyProvider#insertBatch
     */
    @Test
    public void case7() {
        var input = SecurityStrategyInput.builder()
                .code("captcha")
                .name("验证码策略")
                .type("captcha")
                .enabled(Boolean.TRUE)
                .remark("用于控制验证码行为")
                .params(Jsonx.Default().serialize(Map.of("enabled", "true")))
                .build();

        // 查询数据
        var entities = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername());

        assertNotNull(entities);
        assertEquals(1, entities.size());

        var entity = Listx.getFirstOrNull(entities);
        assertNotNull(entity);
        assertNotNull(entity.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(SecurityStrategyEntity.class).eq(SecurityStrategyEntity::getId, entity.getId())));
    }

    /**
     * @see SecurityStrategyProvider#update
     */
    @Test
    public void case8() {
        var entity = new SecurityStrategyEntity();
        entity.setCode("captcha");
        entity.setName("验证码策略");
        entity.setType("captcha");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于控制验证码行为");
        entity.setParams(Jsonx.Default().serialize(Map.of("enabled", "true")));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var strategy = this.provider.findById(entity.getId());
        assertNotNull(strategy);
        assertEquals(entity.getId(), strategy.getId());

        var input = strategy.toInput().toBuilder()
                .code("test1")
                .build();

        // 更新数据
        strategy = this.provider.update(input, properties.getSupervisor().getUsername());
        assertNotNull(strategy);
        assertEquals(entity.getId(), strategy.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(SecurityStrategyEntity.class).eq(SecurityStrategyEntity::getCode, "test1")));
    }

    /**
     * @see SecurityStrategyProvider#updateBatch
     */
    @Test
    public void case9() {
        var entity = new SecurityStrategyEntity();
        entity.setCode("captcha");
        entity.setName("验证码策略");
        entity.setType("captcha");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于控制验证码行为");
        entity.setParams(Jsonx.Default().serialize(Map.of("enabled", "true")));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var strategy = this.provider.findById(entity.getId());
        assertNotNull(strategy);
        assertEquals(entity.getId(), strategy.getId());

        var input = strategy.toInput().toBuilder()
                .code("test1")
                .build();

        // 更新数据
        var strategies = this.provider.updateBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(strategies);
        assertEquals(1, strategies.size());

        strategy = Listx.getFirstOrNull(strategies);
        assertNotNull(strategy);
        assertEquals(entity.getId(), strategy.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(SecurityStrategyEntity.class).eq(SecurityStrategyEntity::getCode, "test1")));
    }

    /**
     * @see SecurityStrategyProvider#deleteByIds
     */
    @Test
    public void case10() {
        var entity = new SecurityStrategyEntity();
        entity.setCode("captcha");
        entity.setName("验证码策略");
        entity.setType("captcha");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于控制验证码行为");
        entity.setParams(Jsonx.Default().serialize(Map.of("enabled", "true")));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var deleted = this.provider.deleteByIds(List.of(entity.getId()));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(SecurityStrategyEntity.class).eq(SecurityStrategyEntity::getId, entity.getId())));
    }

    /**
     * @see SecurityStrategyProvider#deleteBy(Conditions)
     */
    @Test
    public void case11() {

        var entity = new SecurityStrategyEntity();
        entity.setCode("captcha");
        entity.setName("验证码策略");
        entity.setType("captcha");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于控制验证码行为");
        entity.setParams(Jsonx.Default().serialize(Map.of("enabled", "true")));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var deleted = this.provider.deleteBy(Conditions.of(SecurityStrategy.class).eq(SecurityStrategy::getCode, "captcha"));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(SecurityStrategyEntity.class).eq(SecurityStrategyEntity::getId, entity.getId())));
    }
}
