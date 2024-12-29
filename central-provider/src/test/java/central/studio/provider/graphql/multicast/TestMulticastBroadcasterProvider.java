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

package central.studio.provider.graphql.multicast;

import central.data.multicast.MulticastBroadcaster;
import central.data.multicast.MulticastBroadcasterInput;
import central.lang.reflect.TypeRef;
import central.provider.graphql.multicast.MulticastBroadcasterProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.ProviderProperties;
import central.studio.provider.database.persistence.multicast.entity.MulticastBroadcasterEntity;
import central.studio.provider.database.persistence.multicast.mapper.MulticastBroadcasterMapper;
import central.studio.provider.database.persistence.saas.entity.ApplicationEntity;
import central.studio.provider.database.persistence.saas.mapper.ApplicationMapper;
import central.studio.provider.database.persistence.saas.mapper.TenantMapper;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MulticastBroadcasterProvider Test Cases
 *
 * @author Alan Yeh
 * @since 2022/11/04
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestMulticastBroadcasterProvider {
    @Setter(onMethod_ = @Autowired)
    private ProviderProperties properties;

    @Setter(onMethod_ = @Autowired)
    private MulticastBroadcasterProvider provider;

    @Setter(onMethod_ = @Autowired)
    private MulticastBroadcasterMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private ApplicationMapper applicationMapper;

    @Setter(onMethod_ = @Autowired)
    private TenantMapper tenantMapper;

    private static ApplicationEntity applicationEntity;

    @Setter(onMethod_ = @Autowired)
    private DataContext context;

    @BeforeEach
    @AfterEach
    public void clear() throws Exception {
        // 清空测试数据
        this.mapper.deleteAll();

        if (applicationEntity == null) {
            this.applicationMapper.deleteAll();
            this.tenantMapper.deleteAll();
            applicationEntity = new ApplicationEntity();
            applicationEntity.setCode("central-security");
            applicationEntity.setName("统一认证中心");
            applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
            applicationEntity.setUrl("http://127.0.0.1:3100");
            applicationEntity.setContextPath("/security");
            applicationEntity.setSecret(Guidx.nextID());
            applicationEntity.setEnabled(Boolean.TRUE);
            applicationEntity.setRemark("用于所有应用的认证处理");
            applicationEntity.setRoutesJson("[]");
            applicationEntity.updateCreator(properties.getSupervisor().getUsername());
            this.applicationMapper.insert(applicationEntity);

            SaasContainer container = null;
            while (container == null || container.getApplications().isEmpty()) {
                Thread.sleep(100);
                container = context.getData(DataFetcherType.SAAS);
            }
        }
    }


    /**
     * @see MulticastBroadcasterProvider#findById
     */
    @Test
    public void case1() {
        var entity = new MulticastBroadcasterEntity();
        entity.setApplicationId(applicationEntity.getId());
        entity.setCode("security-broadcaster");
        entity.setName("腾讯企业邮");
        entity.setType("email_smtp");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("重置邮箱");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "host", "./storages",
                "ssl", "enabled",
                "port", "445",
                "username", "no-reply@central-x.com",
                "password", "x.123456",
                "name", "No Reply"
        )));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var broadcaster = this.provider.findById(entity.getId(), "master");
        assertNotNull(broadcaster);
        assertEquals(entity.getId(), broadcaster.getId());
        assertNotNull(broadcaster.getCode());
        assertNotNull(broadcaster.getName());
        assertNotNull(broadcaster.getType());
        assertNotNull(broadcaster.getEnabled());
        assertNotNull(broadcaster.getRemark());
        assertNotNull(broadcaster.getParams());
        var params = Jsonx.Default().deserialize(broadcaster.getParams(), TypeRef.ofMap(String.class, Object.class));
        assertNotNull(params);
        assertEquals(6, params.size());
        // 关联查询
        assertNotNull(broadcaster.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), broadcaster.getCreator().getId());
        assertNotNull(broadcaster.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), broadcaster.getModifier().getId());
    }

    /**
     * @see MulticastBroadcasterProvider#findByIds
     */
    @Test
    public void case2() {
        var entity = new MulticastBroadcasterEntity();
        entity.setApplicationId(applicationEntity.getId());
        entity.setCode("security-broadcaster");
        entity.setName("腾讯企业邮");
        entity.setType("email_smtp");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("重置邮箱");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "host", "./storages",
                "ssl", "enabled",
                "port", "445",
                "username", "no-reply@central-x.com",
                "password", "x.123456",
                "name", "No Reply"
        )));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var broadcasters = this.provider.findByIds(List.of(entity.getId()), "master");
        assertNotNull(broadcasters);
        assertEquals(1, broadcasters.size());

        var broadcaster = Listx.getFirstOrNull(broadcasters);
        assertNotNull(broadcaster);
        assertEquals(entity.getId(), broadcaster.getId());
        assertNotNull(broadcaster.getCode());
        assertNotNull(broadcaster.getName());
        assertNotNull(broadcaster.getType());
        assertNotNull(broadcaster.getEnabled());
        assertNotNull(broadcaster.getRemark());
        assertNotNull(broadcaster.getParams());
        // 关联查询
        assertNotNull(broadcaster.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), broadcaster.getCreator().getId());
        assertNotNull(broadcaster.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), broadcaster.getModifier().getId());
    }

    /**
     * @see MulticastBroadcasterProvider#findBy
     */
    @Test
    public void case3() {
        var entity = new MulticastBroadcasterEntity();
        entity.setApplicationId(applicationEntity.getId());
        entity.setCode("security-broadcaster");
        entity.setName("腾讯企业邮");
        entity.setType("email_smtp");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("重置邮箱");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "host", "./storages",
                "ssl", "enabled",
                "port", "445",
                "username", "no-reply@central-x.com",
                "password", "x.123456",
                "name", "No Reply"
        )));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var broadcasters = this.provider.findBy(null, null, Conditions.of(MulticastBroadcaster.class).eq(MulticastBroadcaster::getCode, "security-broadcaster"), null, "master");
        assertNotNull(broadcasters);
        assertEquals(1, broadcasters.size());

        var broadcaster = Listx.getFirstOrNull(broadcasters);
        assertNotNull(broadcaster);
        assertEquals(entity.getId(), broadcaster.getId());
        assertNotNull(broadcaster.getCode());
        assertNotNull(broadcaster.getName());
        assertNotNull(broadcaster.getType());
        assertNotNull(broadcaster.getEnabled());
        assertNotNull(broadcaster.getRemark());
        assertNotNull(broadcaster.getParams());
        // 关联查询
        assertNotNull(broadcaster.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), broadcaster.getCreator().getId());
        assertNotNull(broadcaster.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), broadcaster.getModifier().getId());
    }

    /**
     * @see MulticastBroadcasterProvider#pageBy
     */
    @Test
    public void case4() {
        var entity = new MulticastBroadcasterEntity();
        entity.setApplicationId(applicationEntity.getId());
        entity.setCode("security-broadcaster");
        entity.setName("腾讯企业邮");
        entity.setType("email_smtp");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("重置邮箱");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "host", "./storages",
                "ssl", "enabled",
                "port", "445",
                "username", "no-reply@central-x.com",
                "password", "x.123456",
                "name", "No Reply"
        )));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(MulticastBroadcaster.class).eq(MulticastBroadcaster::getCode, "security-broadcaster"), null, "master");
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1, page.getPager().getPageIndex());
        assertEquals(20, page.getPager().getPageSize());
        assertEquals(1, page.getPager().getPageCount());
        assertEquals(1, page.getPager().getItemCount());

        var broadcaster = Listx.getFirstOrNull(page.getData());
        assertNotNull(broadcaster);
        assertEquals(entity.getId(), broadcaster.getId());
        assertNotNull(broadcaster.getCode());
        assertNotNull(broadcaster.getName());
        assertNotNull(broadcaster.getType());
        assertNotNull(broadcaster.getEnabled());
        assertNotNull(broadcaster.getRemark());
        assertNotNull(broadcaster.getParams());
        // 关联查询
        assertNotNull(broadcaster.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), broadcaster.getCreator().getId());
        assertNotNull(broadcaster.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), broadcaster.getModifier().getId());
    }

    /**
     * @see MulticastBroadcasterProvider#countBy
     */
    @Test
    public void case5() {
        var entity = new MulticastBroadcasterEntity();
        entity.setApplicationId(applicationEntity.getId());
        entity.setCode("security-broadcaster");
        entity.setName("腾讯企业邮");
        entity.setType("email_smtp");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("重置邮箱");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "host", "./storages",
                "ssl", "enabled",
                "port", "445",
                "username", "no-reply@central-x.com",
                "password", "x.123456",
                "name", "No Reply"
        )));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var count = this.provider.countBy(Conditions.of(MulticastBroadcaster.class).eq(MulticastBroadcaster::getCode, "security-broadcaster"), "master");
        assertNotNull(count);
        assertEquals(1, count);
    }

    /**
     * @see MulticastBroadcasterProvider#insert
     */
    @Test
    public void case6() {
        var input = MulticastBroadcasterInput.builder()
                .applicationId(applicationEntity.getId())
                .code("security-broadcaster")
                .name("腾讯企业邮")
                .type("email_smtp")
                .enabled(Boolean.TRUE)
                .remark("重置邮箱")
                .params(Jsonx.Default().serialize(Map.of(
                        "host", "./storages",
                        "ssl", "enabled",
                        "port", "445",
                        "username", "no-reply@central-x.com",
                        "password", "x.123456",
                        "name", "No Reply"
                )))
                .build();

        // 查询数据
        var entity = this.provider.insert(input, properties.getSupervisor().getUsername(), "master");

        assertNotNull(entity);
        assertNotNull(entity.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(MulticastBroadcasterEntity.class).eq(MulticastBroadcasterEntity::getId, entity.getId())));
    }

    /**
     * @see MulticastBroadcasterProvider#insertBatch
     */
    @Test
    public void case7() {
        var input = MulticastBroadcasterInput.builder()
                .applicationId(applicationEntity.getId())
                .code("security-broadcaster")
                .name("腾讯企业邮")
                .type("email_smtp")
                .enabled(Boolean.TRUE)
                .remark("重置邮箱")
                .params(Jsonx.Default().serialize(Map.of(
                        "host", "./storages",
                        "ssl", "enabled",
                        "port", "445",
                        "username", "no-reply@central-x.com",
                        "password", "x.123456",
                        "name", "No Reply"
                )))
                .build();

        // 查询数据
        var entities = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername(), "master");

        assertNotNull(entities);
        assertEquals(1, entities.size());

        var entity = Listx.getFirstOrNull(entities);
        assertNotNull(entity);
        assertNotNull(entity.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(MulticastBroadcasterEntity.class).eq(MulticastBroadcasterEntity::getId, entity.getId())));
    }

    /**
     * @see MulticastBroadcasterProvider#update
     */
    @Test
    public void case8() {
        var entity = new MulticastBroadcasterEntity();
        entity.setApplicationId(applicationEntity.getId());
        entity.setCode("security-broadcaster");
        entity.setName("腾讯企业邮");
        entity.setType("email_smtp");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("重置邮箱");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "host", "./storages",
                "ssl", "enabled",
                "port", "445",
                "username", "no-reply@central-x.com",
                "password", "x.123456",
                "name", "No Reply"
        )));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var broadcaster = this.provider.findById(entity.getId(), "master");
        assertNotNull(broadcaster);
        assertEquals(entity.getId(), broadcaster.getId());

        var input = broadcaster.toInput()
                .code("test1")
                .build();

        // 更新数据
        broadcaster = this.provider.update(input, properties.getSupervisor().getUsername(), "master");
        assertNotNull(broadcaster);
        assertEquals(entity.getId(), broadcaster.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(MulticastBroadcasterEntity.class).eq(MulticastBroadcasterEntity::getCode, "test1")));
    }

    /**
     * @see MulticastBroadcasterProvider#updateBatch
     */
    @Test
    public void case9() {
        var entity = new MulticastBroadcasterEntity();
        entity.setApplicationId(applicationEntity.getId());
        entity.setCode("security-broadcaster");
        entity.setName("腾讯企业邮");
        entity.setType("email_smtp");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("重置邮箱");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "host", "./storages",
                "ssl", "enabled",
                "port", "445",
                "username", "no-reply@central-x.com",
                "password", "x.123456",
                "name", "No Reply"
        )));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var broadcaster = this.provider.findById(entity.getId(), "master");
        assertNotNull(broadcaster);
        assertEquals(entity.getId(), broadcaster.getId());

        var input = broadcaster.toInput()
                .code("test1")
                .build();

        // 更新数据
        var broadcasters = this.provider.updateBatch(List.of(input), properties.getSupervisor().getUsername(), "master");
        assertNotNull(broadcasters);
        assertEquals(1, broadcasters.size());

        broadcaster = Listx.getFirstOrNull(broadcasters);
        assertNotNull(broadcaster);
        assertEquals(entity.getId(), broadcaster.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(MulticastBroadcasterEntity.class).eq(MulticastBroadcasterEntity::getCode, "test1")));
    }

    /**
     * @see MulticastBroadcasterProvider#deleteByIds
     */
    @Test
    public void case10() {
        var entity = new MulticastBroadcasterEntity();
        entity.setApplicationId(applicationEntity.getId());
        entity.setCode("security-broadcaster");
        entity.setName("腾讯企业邮");
        entity.setType("email_smtp");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("重置邮箱");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "host", "./storages",
                "ssl", "enabled",
                "port", "445",
                "username", "no-reply@central-x.com",
                "password", "x.123456",
                "name", "No Reply"
        )));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var deleted = this.provider.deleteByIds(List.of(entity.getId()), "master");
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(MulticastBroadcasterEntity.class).eq(MulticastBroadcasterEntity::getId, entity.getId())));
    }

    /**
     * @see MulticastBroadcasterProvider#deleteBy(Conditions)
     */
    @Test
    public void case11() {

        var entity = new MulticastBroadcasterEntity();
        entity.setApplicationId(applicationEntity.getId());
        entity.setCode("security-broadcaster");
        entity.setName("腾讯企业邮");
        entity.setType("email_smtp");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("重置邮箱");
        entity.setParams(Jsonx.Default().serialize(Map.of(
                "host", "./storages",
                "ssl", "enabled",
                "port", "445",
                "username", "no-reply@central-x.com",
                "password", "x.123456",
                "name", "No Reply"
        )));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var deleted = this.provider.deleteBy(Conditions.of(MulticastBroadcaster.class).eq(MulticastBroadcaster::getCode, "security-broadcaster"), "master");
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(MulticastBroadcasterEntity.class).eq(MulticastBroadcasterEntity::getId, entity.getId())));
    }
}
