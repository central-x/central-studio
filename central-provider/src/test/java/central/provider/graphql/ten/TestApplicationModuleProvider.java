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

package central.provider.graphql.ten;

import central.api.provider.ten.ApplicationModuleProvider;
import central.data.ten.ApplicationModule;
import central.data.ten.ApplicationModuleInput;
import central.provider.ApplicationProperties;
import central.provider.ProviderApplication;
import central.provider.graphql.TestProvider;
import central.provider.graphql.ten.entity.ApplicationEntity;
import central.provider.graphql.ten.entity.ApplicationModuleEntity;
import central.provider.graphql.ten.mapper.ApplicationMapper;
import central.provider.graphql.ten.mapper.ApplicationModuleMapper;
import central.sql.Conditions;
import central.util.Guidx;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Application Module Provider Test Cases
 * 应用模块
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestApplicationModuleProvider extends TestProvider {
    @Setter(onMethod_ = @Autowired)
    private ApplicationModuleProvider provider;

    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    @Setter(onMethod_ = @Autowired)
    private ApplicationMapper applicationMapper;

    @Setter(onMethod_ = @Autowired)
    private ApplicationModuleMapper mapper;

    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空数据
        applicationMapper.deleteAll();
        mapper.deleteAll();
    }

    /**
     * @see ApplicationModuleProvider#findById
     */
    @Test
    public void case1() {
        var entity = new ApplicationEntity();
        entity.setCode("central-security");
        entity.setName("统一认证中心");
        entity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        entity.setUrl("http://127.0.0.1:3100");
        entity.setContextPath("/security");
        entity.setKey(Guidx.nextID());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(entity);

        var modules = new ArrayList<ApplicationModuleEntity>();

        var module1 = new ApplicationModuleEntity();
        module1.setApplicationId(entity.getId());
        module1.setUrl("http://127.0.0.1:3110");
        module1.setContextPath("/security/test");
        module1.setEnabled(Boolean.TRUE);
        module1.setRemark("测试模块1");
        module1.updateCreator(properties.getSupervisor().getUsername());
        modules.add(module1);

        var module2 = new ApplicationModuleEntity();
        module2.setApplicationId(entity.getId());
        module2.setUrl("http://127.0.0.1:3120");
        module2.setContextPath("/security/example");
        module2.setEnabled(Boolean.TRUE);
        module2.setRemark("测试模块2");
        module2.updateCreator(properties.getSupervisor().getUsername());
        modules.add(module2);
        this.mapper.insertBatch(modules);

        // 查询数据
        var module = this.provider.findById(module1.getId());
        assertNotNull(module);
        assertEquals(module1.getId(), module.getId());
        assertEquals(module1.getApplicationId(), module.getApplicationId());
        assertNotNull(module.getApplication());
        // 关联查询
        assertEquals(entity.getCode(), module.getApplication().getCode());
        assertEquals(entity.getName(), module.getApplication().getName());
        assertEquals(entity.getEnabled(), module.getApplication().getEnabled());

        assertEquals(module1.getUrl(), module.getUrl());
        assertEquals(module1.getContextPath(), module.getContextPath());
        assertEquals(module1.getEnabled(), module.getEnabled());
        assertEquals(module1.getRemark(), module.getRemark());
    }

    /**
     * @see ApplicationModuleProvider#findByIds
     */
    @Test
    public void case2() {
        var entity = new ApplicationEntity();
        entity.setCode("central-security");
        entity.setName("统一认证中心");
        entity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        entity.setUrl("http://127.0.0.1:3100");
        entity.setContextPath("/security");
        entity.setKey(Guidx.nextID());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(entity);

        var modules = new ArrayList<ApplicationModuleEntity>();

        var module1 = new ApplicationModuleEntity();
        module1.setApplicationId(entity.getId());
        module1.setUrl("http://127.0.0.1:3110");
        module1.setContextPath("/security/test");
        module1.setEnabled(Boolean.TRUE);
        module1.setRemark("测试模块1");
        module1.updateCreator(properties.getSupervisor().getUsername());
        modules.add(module1);

        var module2 = new ApplicationModuleEntity();
        module2.setApplicationId(entity.getId());
        module2.setUrl("http://127.0.0.1:3120");
        module2.setContextPath("/security/example");
        module2.setEnabled(Boolean.TRUE);
        module2.setRemark("测试模块2");
        module2.updateCreator(properties.getSupervisor().getUsername());
        modules.add(module2);
        this.mapper.insertBatch(modules);

        // 查询数据
        var result = this.provider.findByIds(List.of(module1.getId()));
        var module = Listx.getFirstOrNull(result);
        assertNotNull(module);
        assertEquals(module1.getId(), module.getId());
        assertEquals(module1.getApplicationId(), module.getApplicationId());
        assertNotNull(module.getApplication());
        // 关联查询
        assertEquals(entity.getCode(), module.getApplication().getCode());
        assertEquals(entity.getName(), module.getApplication().getName());
        assertEquals(entity.getEnabled(), module.getApplication().getEnabled());

        assertEquals(module1.getUrl(), module.getUrl());
        assertEquals(module1.getContextPath(), module.getContextPath());
        assertEquals(module1.getEnabled(), module.getEnabled());
        assertEquals(module1.getRemark(), module.getRemark());
    }

    /**
     * @see ApplicationModuleProvider#findBy
     */
    @Test
    public void case3() {
        var entity = new ApplicationEntity();
        entity.setCode("central-security");
        entity.setName("统一认证中心");
        entity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        entity.setUrl("http://127.0.0.1:3100");
        entity.setContextPath("/security");
        entity.setKey(Guidx.nextID());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(entity);

        var modules = new ArrayList<ApplicationModuleEntity>();

        var module1 = new ApplicationModuleEntity();
        module1.setApplicationId(entity.getId());
        module1.setUrl("http://127.0.0.1:3110");
        module1.setContextPath("/security/test");
        module1.setEnabled(Boolean.TRUE);
        module1.setRemark("测试模块1");
        module1.updateCreator(properties.getSupervisor().getUsername());
        modules.add(module1);

        var module2 = new ApplicationModuleEntity();
        module2.setApplicationId(entity.getId());
        module2.setUrl("http://127.0.0.1:3120");
        module2.setContextPath("/security/example");
        module2.setEnabled(Boolean.TRUE);
        module2.setRemark("测试模块2");
        module2.updateCreator(properties.getSupervisor().getUsername());
        modules.add(module2);
        this.mapper.insertBatch(modules);

        // 查询数据
        var result = this.provider.findBy(1L, 0L, Conditions.of(ApplicationModule.class).eq("application.code", "central-security"), null);
        assertNotNull(result);
        assertEquals(1, result.size());

        var module = result.stream().filter(it -> Objects.equals(module1.getId(), it.getId())).findFirst().orElse(null);
        assertNotNull(module);
        assertEquals(module1.getId(), module.getId());
        assertEquals(module1.getApplicationId(), module.getApplicationId());
        assertNotNull(module.getApplication());
        // 关联查询
        assertEquals(entity.getCode(), module.getApplication().getCode());
        assertEquals(entity.getName(), module.getApplication().getName());
        assertEquals(entity.getEnabled(), module.getApplication().getEnabled());

        assertEquals(module1.getUrl(), module.getUrl());
        assertEquals(module1.getContextPath(), module.getContextPath());
        assertEquals(module1.getEnabled(), module.getEnabled());
        assertEquals(module1.getRemark(), module.getRemark());
    }

    /**
     * @see ApplicationModuleProvider#pageBy
     */
    @Test
    public void case4() {
        var entity = new ApplicationEntity();
        entity.setCode("central-security");
        entity.setName("统一认证中心");
        entity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        entity.setUrl("http://127.0.0.1:3100");
        entity.setContextPath("/security");
        entity.setKey(Guidx.nextID());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(entity);

        var modules = new ArrayList<ApplicationModuleEntity>();

        var module1 = new ApplicationModuleEntity();
        module1.setApplicationId(entity.getId());
        module1.setUrl("http://127.0.0.1:3110");
        module1.setContextPath("/security/test");
        module1.setEnabled(Boolean.TRUE);
        module1.setRemark("测试模块1");
        module1.updateCreator(properties.getSupervisor().getUsername());
        modules.add(module1);

        var module2 = new ApplicationModuleEntity();
        module2.setApplicationId(entity.getId());
        module2.setUrl("http://127.0.0.1:3120");
        module2.setContextPath("/security/example");
        module2.setEnabled(Boolean.TRUE);
        module2.setRemark("测试模块2");
        module2.updateCreator(properties.getSupervisor().getUsername());
        modules.add(module2);
        this.mapper.insertBatch(modules);

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(ApplicationModule.class).eq("application.code", "central-security"), null);
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1L, page.getPager().getPageIndex());
        assertEquals(20L, page.getPager().getPageSize());
        assertEquals(1L, page.getPager().getPageCount());
        assertEquals(2L, page.getPager().getItemCount());
        assertNotNull(page.getData());

        var module = page.getData().stream().filter(it -> Objects.equals(module1.getId(), it.getId())).findFirst().orElse(null);
        assertNotNull(module);
        assertEquals(module1.getId(), module.getId());
        assertEquals(module1.getApplicationId(), module.getApplicationId());
        assertNotNull(module.getApplication());
        // 关联查询
        assertEquals(entity.getCode(), module.getApplication().getCode());
        assertEquals(entity.getName(), module.getApplication().getName());
        assertEquals(entity.getEnabled(), module.getApplication().getEnabled());

        assertEquals(module1.getUrl(), module.getUrl());
        assertEquals(module1.getContextPath(), module.getContextPath());
        assertEquals(module1.getEnabled(), module.getEnabled());
        assertEquals(module1.getRemark(), module.getRemark());
    }

    /**
     * @see ApplicationModuleProvider#countBy
     */
    @Test
    public void case5() {
        var entity = new ApplicationEntity();
        entity.setCode("central-security");
        entity.setName("统一认证中心");
        entity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        entity.setUrl("http://127.0.0.1:3100");
        entity.setContextPath("/security");
        entity.setKey(Guidx.nextID());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(entity);

        var modules = new ArrayList<ApplicationModuleEntity>();

        var module1 = new ApplicationModuleEntity();
        module1.setApplicationId(entity.getId());
        module1.setUrl("http://127.0.0.1:3110");
        module1.setContextPath("/security/test");
        module1.setEnabled(Boolean.TRUE);
        module1.setRemark("测试模块1");
        module1.updateCreator(properties.getSupervisor().getUsername());
        modules.add(module1);

        var module2 = new ApplicationModuleEntity();
        module2.setApplicationId(entity.getId());
        module2.setUrl("http://127.0.0.1:3120");
        module2.setContextPath("/security/example");
        module2.setEnabled(Boolean.TRUE);
        module2.setRemark("测试模块2");
        module2.updateCreator(properties.getSupervisor().getUsername());
        modules.add(module2);
        this.mapper.insertBatch(modules);

        // 查询数据
        var count = this.provider.countBy(Conditions.of(ApplicationModule.class).eq("application.code", "central-security"));
        assertEquals(2L, count);
    }

    /**
     * @see ApplicationModuleProvider#insert
     */
    @Test
    public void case6() {
        var entity = new ApplicationEntity();
        entity.setCode("central-security");
        entity.setName("统一认证中心");
        entity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        entity.setUrl("http://127.0.0.1:3100");
        entity.setContextPath("/security");
        entity.setKey(Guidx.nextID());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(entity);

        var input = ApplicationModuleInput.builder()
                .applicationId(entity.getId())
                .url("http://127.0.0.1:3110")
                .contextPath("/security/test")
                .enabled(Boolean.TRUE)
                .remark("测试模块1")
                .build();

        var inserted = this.provider.insert(input, properties.getSupervisor().getUsername());
        assertNotNull(inserted);
        assertNotNull(inserted.getId());
        assertEquals(input.getApplicationId(), inserted.getApplicationId());
        assertNotNull(inserted.getApplication());
        // 关联查询
        assertEquals(entity.getCode(), inserted.getApplication().getCode());
        assertEquals(entity.getName(), inserted.getApplication().getName());
        assertEquals(entity.getEnabled(), inserted.getApplication().getEnabled());

        assertEquals(input.getUrl(), inserted.getUrl());
        assertEquals(input.getContextPath(), inserted.getContextPath());
        assertEquals(input.getEnabled(), inserted.getEnabled());
        assertEquals(input.getRemark(), inserted.getRemark());

        // 检查数据库
        assertTrue(this.mapper.existsBy(Conditions.of(ApplicationModuleEntity.class).eq(ApplicationModuleEntity::getId, inserted.getId())));
    }

    /**
     * @see ApplicationModuleProvider#insertBatch
     */
    @Test
    public void case7() {
        var entity = new ApplicationEntity();
        entity.setCode("central-security");
        entity.setName("统一认证中心");
        entity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        entity.setUrl("http://127.0.0.1:3100");
        entity.setContextPath("/security");
        entity.setKey(Guidx.nextID());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(entity);

        var input = ApplicationModuleInput.builder()
                .applicationId(entity.getId())
                .url("http://127.0.0.1:3110")
                .contextPath("/security/test")
                .enabled(Boolean.TRUE)
                .remark("测试模块1")
                .build();

        var inserted = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(inserted);
        assertEquals(1, inserted.size());

        var module = Listx.getFirstOrNull(inserted);
        assertNotNull(module);

        assertNotNull(module.getId());
        assertEquals(input.getApplicationId(), module.getApplicationId());
        assertNotNull(module.getApplication());
        // 关联查询
        assertEquals(entity.getCode(), module.getApplication().getCode());
        assertEquals(entity.getName(), module.getApplication().getName());
        assertEquals(entity.getEnabled(), module.getApplication().getEnabled());

        assertEquals(input.getUrl(), module.getUrl());
        assertEquals(input.getContextPath(), module.getContextPath());
        assertEquals(input.getEnabled(), module.getEnabled());
        assertEquals(input.getRemark(), module.getRemark());

        // 检查数据库
        assertTrue(this.mapper.existsBy(Conditions.of(ApplicationModuleEntity.class).eq(ApplicationModuleEntity::getId, module.getId())));
    }

    /**
     * @see ApplicationModuleProvider#update
     */
    @Test
    public void case8() {
        var entity = new ApplicationEntity();
        entity.setCode("central-security");
        entity.setName("统一认证中心");
        entity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        entity.setUrl("http://127.0.0.1:3100");
        entity.setContextPath("/security");
        entity.setKey(Guidx.nextID());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(entity);

        var modules = new ArrayList<ApplicationModuleEntity>();

        var module1 = new ApplicationModuleEntity();
        module1.setApplicationId(entity.getId());
        module1.setUrl("http://127.0.0.1:3110");
        module1.setContextPath("/security/test");
        module1.setEnabled(Boolean.TRUE);
        module1.setRemark("测试模块1");
        module1.updateCreator(properties.getSupervisor().getUsername());
        modules.add(module1);

        var module2 = new ApplicationModuleEntity();
        module2.setApplicationId(entity.getId());
        module2.setUrl("http://127.0.0.1:3120");
        module2.setContextPath("/security/example");
        module2.setEnabled(Boolean.TRUE);
        module2.setRemark("测试模块2");
        module2.updateCreator(properties.getSupervisor().getUsername());
        modules.add(module2);
        this.mapper.insertBatch(modules);

        var module = this.provider.findById(module1.getId());
        assertNotNull(module);

        var input = module.toInput().toBuilder()
                .url("http://127.0.0.1:4100")
                .build();

        module = this.provider.update(input, properties.getSupervisor().getUsername());
        assertEquals(input.getId(), module.getId());
        assertEquals(input.getApplicationId(), module.getApplicationId());
        assertEquals(input.getUrl(), module.getUrl());
        assertEquals(input.getContextPath(), module.getContextPath());
        assertEquals(input.getEnabled(), module.getEnabled());
        assertEquals(input.getRemark(), module.getRemark());
    }

    /**
     * @see ApplicationModuleProvider#updateBatch
     */
    @Test
    public void case9() {
        var entity = new ApplicationEntity();
        entity.setCode("central-security");
        entity.setName("统一认证中心");
        entity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        entity.setUrl("http://127.0.0.1:3100");
        entity.setContextPath("/security");
        entity.setKey(Guidx.nextID());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(entity);

        var modules = new ArrayList<ApplicationModuleEntity>();

        var module1 = new ApplicationModuleEntity();
        module1.setApplicationId(entity.getId());
        module1.setUrl("http://127.0.0.1:3110");
        module1.setContextPath("/security/test");
        module1.setEnabled(Boolean.TRUE);
        module1.setRemark("测试模块1");
        module1.updateCreator(properties.getSupervisor().getUsername());
        modules.add(module1);

        var module2 = new ApplicationModuleEntity();
        module2.setApplicationId(entity.getId());
        module2.setUrl("http://127.0.0.1:3120");
        module2.setContextPath("/security/example");
        module2.setEnabled(Boolean.TRUE);
        module2.setRemark("测试模块2");
        module2.updateCreator(properties.getSupervisor().getUsername());
        modules.add(module2);
        this.mapper.insertBatch(modules);

        var module = this.provider.findById(module1.getId());
        assertNotNull(module);

        var input = module.toInput().toBuilder()
                .url("http://127.0.0.1:4100")
                .build();

        var updated = this.provider.updateBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(updated);
        assertEquals(1, updated.size());

        module = Listx.getFirstOrNull(updated);
        assertNotNull(module);
        assertEquals(input.getId(), module.getId());
        assertEquals(input.getApplicationId(), module.getApplicationId());
        assertEquals(input.getUrl(), module.getUrl());
        assertEquals(input.getContextPath(), module.getContextPath());
        assertEquals(input.getEnabled(), module.getEnabled());
        assertEquals(input.getRemark(), module.getRemark());
    }

    /**
     * @see ApplicationModuleProvider#deleteByIds
     */
    @Test
    public void case10() {
        var entity = new ApplicationEntity();
        entity.setCode("central-security");
        entity.setName("统一认证中心");
        entity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        entity.setUrl("http://127.0.0.1:3100");
        entity.setContextPath("/security");
        entity.setKey(Guidx.nextID());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(entity);

        var modules = new ArrayList<ApplicationModuleEntity>();

        var module1 = new ApplicationModuleEntity();
        module1.setApplicationId(entity.getId());
        module1.setUrl("http://127.0.0.1:3110");
        module1.setContextPath("/security/test");
        module1.setEnabled(Boolean.TRUE);
        module1.setRemark("测试模块1");
        module1.updateCreator(properties.getSupervisor().getUsername());
        modules.add(module1);

        var module2 = new ApplicationModuleEntity();
        module2.setApplicationId(entity.getId());
        module2.setUrl("http://127.0.0.1:3120");
        module2.setContextPath("/security/example");
        module2.setEnabled(Boolean.TRUE);
        module2.setRemark("测试模块2");
        module2.updateCreator(properties.getSupervisor().getUsername());
        modules.add(module2);
        this.mapper.insertBatch(modules);

        var deleted = this.provider.deleteByIds(List.of(module1.getId()));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(ApplicationModuleEntity.class).eq(ApplicationModuleEntity::getId, entity.getId())));
    }

    /**
     * @see ApplicationModuleProvider#deleteBy
     */
    @Test
    public void case11() {
        var entity = new ApplicationEntity();
        entity.setCode("central-security");
        entity.setName("统一认证中心");
        entity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        entity.setUrl("http://127.0.0.1:3100");
        entity.setContextPath("/security");
        entity.setKey(Guidx.nextID());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(entity);

        var modules = new ArrayList<ApplicationModuleEntity>();

        var module1 = new ApplicationModuleEntity();
        module1.setApplicationId(entity.getId());
        module1.setUrl("http://127.0.0.1:3110");
        module1.setContextPath("/security/test");
        module1.setEnabled(Boolean.TRUE);
        module1.setRemark("测试模块1");
        module1.updateCreator(properties.getSupervisor().getUsername());
        modules.add(module1);

        var module2 = new ApplicationModuleEntity();
        module2.setApplicationId(entity.getId());
        module2.setUrl("http://127.0.0.1:3120");
        module2.setContextPath("/security/example");
        module2.setEnabled(Boolean.FALSE);
        module2.setRemark("测试模块2");
        module2.updateCreator(properties.getSupervisor().getUsername());
        modules.add(module2);
        this.mapper.insertBatch(modules);

        var deleted = this.provider.deleteBy(Conditions.of(ApplicationModule.class).eq(ApplicationModule::getEnabled, Boolean.FALSE));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(ApplicationModuleEntity.class).eq(ApplicationModuleEntity::getId, module2.getId())));
    }
}
