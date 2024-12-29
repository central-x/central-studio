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

package central.studio.provider.graphql.saas;

import central.data.saas.Application;
import central.data.saas.ApplicationInput;
import central.data.saas.ApplicationRouteInput;
import central.provider.graphql.saas.ApplicationProvider;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.ProviderProperties;
import central.studio.provider.graphql.TestProvider;
import central.studio.provider.database.persistence.saas.entity.ApplicationEntity;
import central.studio.provider.database.persistence.saas.mapper.ApplicationMapper;
import central.util.Guidx;
import central.util.Jsonx;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Application Provider Test Cases
 * 应用
 *
 * @author Alan Yeh
 * @since 2022/09/28
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestApplicationProvider extends TestProvider {

    @Setter(onMethod_ = @Autowired)
    private ApplicationProvider provider;

    @Setter(onMethod_ = @Autowired)
    private ProviderProperties properties;

    @Setter(onMethod_ = @Autowired)
    private ApplicationMapper mapper;

    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空数据
        mapper.deleteAll();
    }

    /**
     * @see ApplicationProvider#findById
     */
    @Test
    public void case1() {
        var entity = new ApplicationEntity();
        entity.setCode("central-identity");
        entity.setName("统一认证中心");
        entity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        entity.setUrl("http://127.0.0.1:3100");
        entity.setContextPath("/identity");
        entity.setSecret(Guidx.nextID());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.setRoutesJson(Jsonx.Default().serialize(List.of(
                ApplicationRouteInput.builder().contextPath("/identity/test").url("http://127.0.0.1:3110").enabled(Boolean.TRUE).remark("测试路由1").build(),
                ApplicationRouteInput.builder().contextPath("/identity/example").url("http://127.0.0.1:3120").enabled(Boolean.TRUE).remark("测试路由2").build()
        )));

        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var application = this.provider.findById(entity.getId(), "master");
        assertNotNull(application);
        assertEquals(entity.getId(), application.getId());
        assertEquals(entity.getCode(), application.getCode());
        assertEquals(Base64.getEncoder().encodeToString(entity.getLogoBytes()), application.getLogo());
        assertEquals(entity.getUrl(), application.getUrl());
        assertEquals(entity.getContextPath(), application.getContextPath());
        assertEquals(entity.getSecret(), application.getSecret());
        assertEquals(entity.getEnabled(), application.getEnabled());
        assertEquals(entity.getRemark(), application.getRemark());
        assertNotNull(application.getRoutes());
        assertEquals(2, application.getRoutes().size());
        assertTrue(application.getRoutes().stream().anyMatch(it -> Objects.equals("/identity/test", it.getContextPath())));
        assertTrue(application.getRoutes().stream().anyMatch(it -> Objects.equals("/identity/example", it.getContextPath())));
    }

    /**
     * @see ApplicationProvider#findByIds
     */
    @Test
    public void case2() {
        var entity = new ApplicationEntity();
        entity.setCode("central-identity");
        entity.setName("统一认证中心");
        entity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        entity.setUrl("http://127.0.0.1:3100");
        entity.setContextPath("/identity");
        entity.setSecret(Guidx.nextID());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.setRoutesJson(Jsonx.Default().serialize(List.of(
                ApplicationRouteInput.builder().contextPath("/identity/test").url("http://127.0.0.1:3110").enabled(Boolean.TRUE).remark("测试路由1").build(),
                ApplicationRouteInput.builder().contextPath("/identity/example").url("http://127.0.0.1:3120").enabled(Boolean.TRUE).remark("测试路由2").build()
        )));

        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var applications = this.provider.findByIds(List.of(entity.getId()), "master");
        var application = Listx.getFirstOrNull(applications);
        assertNotNull(application);
        assertEquals(entity.getId(), application.getId());
        assertEquals(entity.getCode(), application.getCode());
        assertEquals(Base64.getEncoder().encodeToString(entity.getLogoBytes()), application.getLogo());
        assertEquals(entity.getUrl(), application.getUrl());
        assertEquals(entity.getContextPath(), application.getContextPath());
        assertEquals(entity.getSecret(), application.getSecret());
        assertEquals(entity.getEnabled(), application.getEnabled());
        assertEquals(entity.getRemark(), application.getRemark());
        assertNotNull(application.getRoutes());
        assertEquals(2, application.getRoutes().size());
        assertTrue(application.getRoutes().stream().anyMatch(it -> Objects.equals("/identity/test", it.getContextPath())));
        assertTrue(application.getRoutes().stream().anyMatch(it -> Objects.equals("/identity/example", it.getContextPath())));
    }

    /**
     * @see ApplicationProvider#findBy
     */
    @Test
    public void case3() {
        var entity = new ApplicationEntity();
        entity.setCode("central-identity");
        entity.setName("统一认证中心");
        entity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        entity.setUrl("http://127.0.0.1:3100");
        entity.setContextPath("/identity");
        entity.setSecret(Guidx.nextID());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.setRoutesJson(Jsonx.Default().serialize(List.of(
                ApplicationRouteInput.builder().contextPath("/identity/test").url("http://127.0.0.1:3110").enabled(Boolean.TRUE).remark("测试路由1").build(),
                ApplicationRouteInput.builder().contextPath("/identity/example").url("http://127.0.0.1:3120").enabled(Boolean.TRUE).remark("测试路由2").build()
        )));

        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var applications = this.provider.findBy(1L, 0L, Conditions.of(Application.class).eq(Application::getCode, "central-identity"), null, "master");
        var application = Listx.getFirstOrNull(applications);
        assertNotNull(application);
        assertEquals(entity.getId(), application.getId());
        assertEquals(entity.getCode(), application.getCode());
        assertEquals(Base64.getEncoder().encodeToString(entity.getLogoBytes()), application.getLogo());
        assertEquals(entity.getUrl(), application.getUrl());
        assertEquals(entity.getContextPath(), application.getContextPath());
        assertEquals(entity.getSecret(), application.getSecret());
        assertEquals(entity.getEnabled(), application.getEnabled());
        assertEquals(entity.getRemark(), application.getRemark());
        assertNotNull(application.getRoutes());
        assertEquals(2, application.getRoutes().size());
        assertTrue(application.getRoutes().stream().anyMatch(it -> Objects.equals("/identity/test", it.getContextPath())));
        assertTrue(application.getRoutes().stream().anyMatch(it -> Objects.equals("/identity/example", it.getContextPath())));
    }

    /**
     * @see ApplicationProvider#pageBy
     */
    @Test
    public void case4() {
        var entity = new ApplicationEntity();
        entity.setCode("central-identity");
        entity.setName("统一认证中心");
        entity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        entity.setUrl("http://127.0.0.1:3100");
        entity.setContextPath("/identity");
        entity.setSecret(Guidx.nextID());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.setRoutesJson(Jsonx.Default().serialize(List.of(
                ApplicationRouteInput.builder().contextPath("/identity/test").url("http://127.0.0.1:3110").enabled(Boolean.TRUE).remark("测试路由1").build(),
                ApplicationRouteInput.builder().contextPath("/identity/example").url("http://127.0.0.1:3120").enabled(Boolean.TRUE).remark("测试路由2").build()
        )));

        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(Application.class).eq(Application::getCode, "central-identity"), null, "master");
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1L, page.getPager().getPageIndex());
        assertEquals(20L, page.getPager().getPageSize());
        assertEquals(1L, page.getPager().getPageCount());
        assertEquals(1L, page.getPager().getItemCount());
        assertNotNull(page.getData());
        var application = Listx.getFirstOrNull(page.getData());
        assertNotNull(application);
        assertEquals(entity.getId(), application.getId());
        assertEquals(entity.getCode(), application.getCode());
        assertEquals(Base64.getEncoder().encodeToString(entity.getLogoBytes()), application.getLogo());
        assertEquals(entity.getUrl(), application.getUrl());
        assertEquals(entity.getContextPath(), application.getContextPath());
        assertEquals(entity.getSecret(), application.getSecret());
        assertEquals(entity.getEnabled(), application.getEnabled());
        assertEquals(entity.getRemark(), application.getRemark());
        assertNotNull(application.getRoutes());
        assertEquals(2, application.getRoutes().size());
        assertTrue(application.getRoutes().stream().anyMatch(it -> Objects.equals("/identity/test", it.getContextPath())));
        assertTrue(application.getRoutes().stream().anyMatch(it -> Objects.equals("/identity/example", it.getContextPath())));
    }

    /**
     * @see ApplicationProvider#countBy
     */
    @Test
    public void case5() {
        var entity = new ApplicationEntity();
        entity.setCode("central-identity");
        entity.setName("统一认证中心");
        entity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        entity.setUrl("http://127.0.0.1:3100");
        entity.setContextPath("/identity");
        entity.setSecret(Guidx.nextID());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.setRoutesJson(Jsonx.Default().serialize(List.of(
                ApplicationRouteInput.builder().contextPath("/identity/test").url("http://127.0.0.1:3110").enabled(Boolean.TRUE).remark("测试路由1").build(),
                ApplicationRouteInput.builder().contextPath("/identity/example").url("http://127.0.0.1:3120").enabled(Boolean.TRUE).remark("测试路由2").build()
        )));

        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var count = this.provider.countBy(Conditions.of(Application.class).eq(Application::getCode, "central-identity"), "master");
        assertEquals(1L, count);
    }

    /**
     * @see ApplicationProvider#insert
     */
    @Test
    public void case6() {
        var input = ApplicationInput.builder()
                .code("central-security")
                .name("统一认证中心")
                .logo(Base64.getEncoder().encodeToString("1234".getBytes(StandardCharsets.UTF_8)))
                .url("http://127.0.0.1:3100")
                .contextPath("/security")
                .secret(Guidx.nextID())
                .enabled(Boolean.TRUE)
                .remark("用于所有应用的认证处理")
                .routes(List.of(
                        ApplicationRouteInput.builder().contextPath("/identity/test").url("http://127.0.0.1:3110").enabled(Boolean.TRUE).remark("测试路由1").build(),
                        ApplicationRouteInput.builder().contextPath("/identity/example").url("http://127.0.0.1:3120").enabled(Boolean.TRUE).remark("测试路由2").build()
                ))
                .build();
        var application = this.provider.insert(input, properties.getSupervisor().getUsername(), "master");
        assertNotNull(application);
        assertNotNull(application.getId());
        assertEquals(input.getCode(), application.getCode());
        assertEquals(input.getLogo(), application.getLogo());
        assertEquals(input.getUrl(), application.getUrl());
        assertEquals(input.getContextPath(), application.getContextPath());
        assertEquals(input.getSecret(), application.getSecret());
        assertEquals(input.getEnabled(), application.getEnabled());
        assertEquals(input.getRemark(), application.getRemark());

        application = this.provider.findById(application.getId(), "master");
        assertNotNull(application);
        assertNotNull(application.getId());
        assertEquals(input.getCode(), application.getCode());
        assertEquals(input.getLogo(), application.getLogo());
        assertEquals(input.getUrl(), application.getUrl());
        assertEquals(input.getContextPath(), application.getContextPath());
        assertEquals(input.getSecret(), application.getSecret());
        assertEquals(input.getEnabled(), application.getEnabled());
        assertEquals(input.getRemark(), application.getRemark());
    }

    /**
     * @see ApplicationProvider#insertBatch
     */
    @Test
    public void case7() {
        var input = ApplicationInput.builder()
                .code("central-security")
                .name("统一认证中心")
                .logo(Base64.getEncoder().encodeToString("1234".getBytes(StandardCharsets.UTF_8)))
                .url("http://127.0.0.1:3100")
                .contextPath("/security")
                .secret(Guidx.nextID())
                .enabled(Boolean.TRUE)
                .remark("用于所有应用的认证处理")
                .routes(List.of(
                        ApplicationRouteInput.builder().contextPath("/identity/test").url("http://127.0.0.1:3110").enabled(Boolean.TRUE).remark("测试路由1").build(),
                        ApplicationRouteInput.builder().contextPath("/identity/example").url("http://127.0.0.1:3120").enabled(Boolean.TRUE).remark("测试路由2").build()
                ))
                .build();
        var applications = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername(), "master");
        assertNotNull(applications);
        assertEquals(1, applications.size());

        var application = Listx.getFirstOrNull(applications);

        assertNotNull(application);
        assertNotNull(application.getId());
        assertEquals(input.getCode(), application.getCode());
        assertEquals(input.getLogo(), application.getLogo());
        assertEquals(input.getUrl(), application.getUrl());
        assertEquals(input.getContextPath(), application.getContextPath());
        assertEquals(input.getSecret(), application.getSecret());
        assertEquals(input.getEnabled(), application.getEnabled());
        assertEquals(input.getRemark(), application.getRemark());
        assertEquals(2, application.getRoutes().size());

        application = this.provider.findById(application.getId(), "master");
        assertNotNull(application);
        assertNotNull(application.getId());
        assertEquals(input.getCode(), application.getCode());
        assertEquals(input.getLogo(), application.getLogo());
        assertEquals(input.getUrl(), application.getUrl());
        assertEquals(input.getContextPath(), application.getContextPath());
        assertEquals(input.getSecret(), application.getSecret());
        assertEquals(input.getEnabled(), application.getEnabled());
        assertEquals(input.getRemark(), application.getRemark());
        assertEquals(2, application.getRoutes().size());
    }

    /**
     * @see ApplicationProvider#update
     */
    @Test
    public void case8() {
        var entity = new ApplicationEntity();
        entity.setCode("central-identity");
        entity.setName("统一认证中心");
        entity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        entity.setUrl("http://127.0.0.1:3100");
        entity.setContextPath("/identity");
        entity.setSecret(Guidx.nextID());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.setRoutesJson(Jsonx.Default().serialize(List.of(
                ApplicationRouteInput.builder().contextPath("/identity/test").url("http://127.0.0.1:3110").enabled(Boolean.TRUE).remark("测试路由1").build(),
                ApplicationRouteInput.builder().contextPath("/identity/example").url("http://127.0.0.1:3120").enabled(Boolean.TRUE).remark("测试路由2").build()
        )));

        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var application = this.provider.findById(entity.getId(), "master");
        assertNotNull(application);

        var input = application.toInput()
                .name("统一认证")
                .url("http://127.0.0.1:4100")
                .build();

        application = this.provider.update(input, properties.getSupervisor().getUsername(), "master");
        assertEquals(input.getId(), application.getId());
        assertEquals(input.getCode(), application.getCode());
        assertEquals(input.getLogo(), application.getLogo());
        assertEquals(input.getUrl(), application.getUrl());
        assertEquals(input.getContextPath(), application.getContextPath());
        assertEquals(input.getSecret(), application.getSecret());
        assertEquals(input.getEnabled(), application.getEnabled());
        assertEquals(input.getRemark(), application.getRemark());
    }

    /**
     * @see ApplicationProvider#updateBatch
     */
    @Test
    public void case9() {
        var entity = new ApplicationEntity();
        entity.setCode("central-identity");
        entity.setName("统一认证中心");
        entity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        entity.setUrl("http://127.0.0.1:3100");
        entity.setContextPath("/identity");
        entity.setSecret(Guidx.nextID());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.setRoutesJson(Jsonx.Default().serialize(List.of(
                ApplicationRouteInput.builder().contextPath("/identity/test").url("http://127.0.0.1:3110").enabled(Boolean.TRUE).remark("测试路由1").build(),
                ApplicationRouteInput.builder().contextPath("/identity/example").url("http://127.0.0.1:3120").enabled(Boolean.TRUE).remark("测试路由2").build()
        )));

        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var application = this.provider.findById(entity.getId(), "master");
        assertNotNull(application);

        var input = application.toInput()
                .name("统一认证")
                .url("http://127.0.0.1:4100")
                .build();

        var updated = this.provider.updateBatch(List.of(input), properties.getSupervisor().getUsername(), "master");
        application = Listx.getFirstOrNull(updated);
        assertNotNull(application);
        assertEquals(input.getId(), application.getId());
        assertEquals(input.getCode(), application.getCode());
        assertEquals(input.getLogo(), application.getLogo());
        assertEquals(input.getUrl(), application.getUrl());
        assertEquals(input.getContextPath(), application.getContextPath());
        assertEquals(input.getSecret(), application.getSecret());
        assertEquals(input.getEnabled(), application.getEnabled());
        assertEquals(input.getRemark(), application.getRemark());
    }

    /**
     * @see ApplicationProvider#deleteByIds
     */
    @Test
    public void case10() {
        var entity = new ApplicationEntity();
        entity.setCode("central-identity");
        entity.setName("统一认证中心");
        entity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        entity.setUrl("http://127.0.0.1:3100");
        entity.setContextPath("/identity");
        entity.setSecret(Guidx.nextID());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.setRoutesJson(Jsonx.Default().serialize(List.of(
                ApplicationRouteInput.builder().contextPath("/identity/test").url("http://127.0.0.1:3110").enabled(Boolean.TRUE).remark("测试路由1").build(),
                ApplicationRouteInput.builder().contextPath("/identity/example").url("http://127.0.0.1:3120").enabled(Boolean.TRUE).remark("测试路由2").build()
        )));

        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var deleted = this.provider.deleteByIds(List.of(entity.getId()), "master");
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(ApplicationEntity.class).eq(ApplicationEntity::getId, entity.getId())));
    }

    /**
     * @see ApplicationProvider#deleteBy
     */
    @Test
    public void case11() {
        var entity = new ApplicationEntity();
        entity.setCode("central-identity");
        entity.setName("统一认证中心");
        entity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        entity.setUrl("http://127.0.0.1:3100");
        entity.setContextPath("/identity");
        entity.setSecret(Guidx.nextID());
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("用于所有应用的认证处理");
        entity.setRoutesJson(Jsonx.Default().serialize(List.of(
                ApplicationRouteInput.builder().contextPath("/identity/test").url("http://127.0.0.1:3110").enabled(Boolean.TRUE).remark("测试路由1").build(),
                ApplicationRouteInput.builder().contextPath("/identity/example").url("http://127.0.0.1:3120").enabled(Boolean.TRUE).remark("测试路由2").build()
        )));

        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var deleted = this.provider.deleteBy(Conditions.of(Application.class).eq(Application::getCode, "central-identity"), "master");
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(ApplicationEntity.class).eq(ApplicationEntity::getId, entity.getId())));
    }
}
