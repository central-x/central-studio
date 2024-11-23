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

import central.data.multicast.MulticastMessage;
import central.data.multicast.MulticastMessageInput;
import central.data.multicast.option.MessageStatus;
import central.data.multicast.option.PublishMode;
import central.lang.reflect.TypeRef;
import central.multicast.client.body.Recipient;
import central.multicast.client.body.StandardBody;
import central.provider.graphql.multicast.MulticastMessageProvider;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.ProviderProperties;
import central.studio.provider.graphql.multicast.entity.MulticastBroadcasterEntity;
import central.studio.provider.graphql.multicast.entity.MulticastMessageEntity;
import central.studio.provider.graphql.multicast.mapper.MulticastBroadcasterMapper;
import central.studio.provider.graphql.multicast.mapper.MulticastMessageMapper;
import central.studio.provider.graphql.saas.entity.ApplicationEntity;
import central.studio.provider.graphql.saas.mapper.ApplicationMapper;
import central.studio.provider.graphql.saas.mapper.TenantMapper;
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
 * MulticastMessageProvider Test Cases
 *
 * @author Alan Yeh
 * @since 2022/11/04
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestMulticastMessageProvider {
    @Setter(onMethod_ = @Autowired)
    private ProviderProperties properties;

    @Setter(onMethod_ = @Autowired)
    private MulticastMessageProvider provider;

    @Setter(onMethod_ = @Autowired)
    private MulticastMessageMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private MulticastBroadcasterMapper broadcasterMapper;

    @Setter(onMethod_ = @Autowired)
    private ApplicationMapper applicationMapper;

    @Setter(onMethod_ = @Autowired)
    private TenantMapper tenantMapper;

    private static ApplicationEntity applicationEntity;
    private static MulticastBroadcasterEntity broadcasterEntity;

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

            broadcasterEntity = new MulticastBroadcasterEntity();
            broadcasterEntity.setApplicationId(applicationEntity.getId());
            broadcasterEntity.setCode("security-broadcaster");
            broadcasterEntity.setName("腾讯企业邮");
            broadcasterEntity.setType("email_smtp");
            broadcasterEntity.setEnabled(Boolean.TRUE);
            broadcasterEntity.setRemark("重置邮箱");
            broadcasterEntity.setParams(Jsonx.Default().serialize(Map.of(
                    "host", "./storages",
                    "ssl", "enabled",
                    "port", "445",
                    "username", "no-reply@central-x.com",
                    "password", "x.123456",
                    "name", "No Reply"
            )));
            broadcasterEntity.setTenantCode("master");
            broadcasterEntity.updateCreator(properties.getSupervisor().getUsername());
            this.broadcasterMapper.insert(broadcasterEntity);
        }
    }

    /**
     * @see MulticastMessageProvider#findById
     */
    @Test
    public void case1() {
        var entity = new MulticastMessageEntity();
        entity.setBroadcasterId(broadcasterEntity.getId());
        entity.setBody(Jsonx.Default().serialize(new StandardBody("系统通知", "测试消息", List.of(new Recipient("alan", "alan@central-x.com")))));
        entity.setMode(PublishMode.STANDARD.getValue());
        entity.setStatus(MessageStatus.STAGED.getValue());
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var message = this.provider.findById(entity.getId(), "master");
        assertNotNull(message);
        assertEquals(entity.getId(), message.getId());
        assertNotNull(message.getBroadcasterId());
        assertNotNull(message.getBroadcaster());
        assertEquals(entity.getBroadcasterId(), message.getBroadcaster().getId());
        assertNotNull(message.getMode());
        assertNotNull(message.getStatus());
        var body = Jsonx.Default().deserialize(message.getBody(), TypeRef.ofMap(String.class, Object.class));
        assertNotNull(body);
        assertEquals(3, body.size());
        // 关联查询
        assertNotNull(message.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), message.getCreator().getId());
        assertNotNull(message.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), message.getModifier().getId());
    }

    /**
     * @see MulticastMessageProvider#findByIds
     */
    @Test
    public void case2() {
        var entity = new MulticastMessageEntity();
        entity.setBroadcasterId(broadcasterEntity.getId());
        entity.setBody(Jsonx.Default().serialize(new StandardBody("系统通知", "测试消息", List.of(new Recipient("alan", "alan@central-x.com")))));
        entity.setMode(PublishMode.STANDARD.getValue());
        entity.setStatus(MessageStatus.STAGED.getValue());
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var messages = this.provider.findByIds(List.of(entity.getId()), "master");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        var message = Listx.getFirstOrNull(messages);
        assertNotNull(message);
        assertEquals(entity.getId(), message.getId());
        assertNotNull(message.getBroadcasterId());
        assertNotNull(message.getBroadcaster());
        assertEquals(entity.getBroadcasterId(), message.getBroadcaster().getId());
        assertNotNull(message.getMode());
        assertNotNull(message.getStatus());
        var body = Jsonx.Default().deserialize(message.getBody(), TypeRef.ofMap(String.class, Object.class));
        assertNotNull(body);
        assertEquals(3, body.size());
        // 关联查询
        assertNotNull(message.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), message.getCreator().getId());
        assertNotNull(message.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), message.getModifier().getId());
    }

    /**
     * @see MulticastMessageProvider#findBy
     */
    @Test
    public void case3() {
        var entity = new MulticastMessageEntity();
        entity.setBroadcasterId(broadcasterEntity.getId());
        entity.setBody(Jsonx.Default().serialize(new StandardBody("系统通知", "测试消息", List.of(new Recipient("alan", "alan@central-x.com")))));
        entity.setMode(PublishMode.STANDARD.getValue());
        entity.setStatus(MessageStatus.STAGED.getValue());
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var messages = this.provider.findBy(null, null, Conditions.of(MulticastMessage.class).eq("broadcaster.code", "security-broadcaster"), null, "master");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        var message = Listx.getFirstOrNull(messages);
        assertNotNull(message);
        assertEquals(entity.getId(), message.getId());
        assertNotNull(message.getBroadcasterId());
        assertNotNull(message.getBroadcaster());
        assertEquals(entity.getBroadcasterId(), message.getBroadcaster().getId());
        assertNotNull(message.getMode());
        assertNotNull(message.getStatus());
        var body = Jsonx.Default().deserialize(message.getBody(), TypeRef.ofMap(String.class, Object.class));
        assertNotNull(body);
        assertEquals(3, body.size());
        // 关联查询
        assertNotNull(message.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), message.getCreator().getId());
        assertNotNull(message.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), message.getModifier().getId());
    }

    /**
     * @see MulticastMessageProvider#pageBy
     */
    @Test
    public void case4() {
        var entity = new MulticastMessageEntity();
        entity.setBroadcasterId(broadcasterEntity.getId());
        entity.setBody(Jsonx.Default().serialize(new StandardBody("系统通知", "测试消息", List.of(new Recipient("alan", "alan@central-x.com")))));
        entity.setMode(PublishMode.STANDARD.getValue());
        entity.setStatus(MessageStatus.STAGED.getValue());
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(MulticastMessage.class).eq("broadcaster.code", "security-broadcaster"), null, "master");
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1, page.getPager().getPageIndex());
        assertEquals(20, page.getPager().getPageSize());
        assertEquals(1, page.getPager().getPageCount());
        assertEquals(1, page.getPager().getItemCount());

        var message = Listx.getFirstOrNull(page.getData());
        assertNotNull(message);
        assertEquals(entity.getId(), message.getId());
        assertNotNull(message.getBroadcasterId());
        assertNotNull(message.getBroadcaster());
        assertEquals(entity.getBroadcasterId(), message.getBroadcaster().getId());
        assertNotNull(message.getMode());
        assertNotNull(message.getStatus());
        var body = Jsonx.Default().deserialize(message.getBody(), TypeRef.ofMap(String.class, Object.class));
        assertNotNull(body);
        assertEquals(3, body.size());
        // 关联查询
        assertNotNull(message.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), message.getCreator().getId());
        assertNotNull(message.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), message.getModifier().getId());
    }

    /**
     * @see MulticastMessageProvider#countBy
     */
    @Test
    public void case5() {
        var entity = new MulticastMessageEntity();
        entity.setBroadcasterId(broadcasterEntity.getId());
        entity.setBody(Jsonx.Default().serialize(new StandardBody("系统通知", "测试消息", List.of(new Recipient("alan", "alan@central-x.com")))));
        entity.setMode(PublishMode.STANDARD.getValue());
        entity.setStatus(MessageStatus.STAGED.getValue());
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var count = this.provider.countBy(Conditions.of(MulticastMessage.class).eq(MulticastMessage::getId, entity.getId()), "master");
        assertNotNull(count);
        assertEquals(1, count);
    }

    /**
     * @see MulticastMessageProvider#insert
     */
    @Test
    public void case6() {
        var input = MulticastMessageInput.builder()
                .broadcasterId(broadcasterEntity.getId())
                .body(Jsonx.Default().serialize(new StandardBody("系统通知", "测试消息", List.of(new Recipient("alan", "alan@central-x.com")))))
                .mode(PublishMode.STANDARD.getValue())
                .status(MessageStatus.STAGED.getValue())
                .build();

        // 查询数据
        var entity = this.provider.insert(input, properties.getSupervisor().getUsername(), "master");

        assertNotNull(entity);
        assertNotNull(entity.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(MulticastMessageEntity.class).eq(MulticastMessageEntity::getId, entity.getId())));
    }

    /**
     * @see MulticastMessageProvider#insertBatch
     */
    @Test
    public void case7() {
        var input = MulticastMessageInput.builder()
                .broadcasterId(broadcasterEntity.getId())
                .body(Jsonx.Default().serialize(new StandardBody("系统通知", "测试消息", List.of(new Recipient("alan", "alan@central-x.com")))))
                .mode(PublishMode.STANDARD.getValue())
                .status(MessageStatus.STAGED.getValue())
                .build();

        // 查询数据
        var entities = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername(), "master");

        assertNotNull(entities);
        assertEquals(1, entities.size());

        var entity = Listx.getFirstOrNull(entities);
        assertNotNull(entity);
        assertNotNull(entity.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(MulticastMessageEntity.class).eq(MulticastMessageEntity::getId, entity.getId())));
    }

    /**
     * @see MulticastMessageProvider#update
     */
    @Test
    public void case8() {
        var entity = new MulticastMessageEntity();
        entity.setBroadcasterId(broadcasterEntity.getId());
        entity.setBody(Jsonx.Default().serialize(new StandardBody("系统通知", "测试消息", List.of(new Recipient("alan", "alan@central-x.com")))));
        entity.setMode(PublishMode.STANDARD.getValue());
        entity.setStatus(MessageStatus.STAGED.getValue());
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var message = this.provider.findById(entity.getId(), "master");
        assertNotNull(message);
        assertEquals(entity.getId(), message.getId());

        var input = message.toInput().toBuilder()
                .status(MessageStatus.SUCCEED.getValue())
                .build();

        // 更新数据
        message = this.provider.update(input, properties.getSupervisor().getUsername(), "master");
        assertNotNull(message);
        assertEquals(entity.getId(), message.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(MulticastMessageEntity.class).eq(MulticastMessageEntity::getStatus, MessageStatus.SUCCEED.getValue())));
    }

    /**
     * @see MulticastMessageProvider#updateBatch
     */
    @Test
    public void case9() {
        var entity = new MulticastMessageEntity();
        entity.setBroadcasterId(broadcasterEntity.getId());
        entity.setBody(Jsonx.Default().serialize(new StandardBody("系统通知", "测试消息", List.of(new Recipient("alan", "alan@central-x.com")))));
        entity.setMode(PublishMode.STANDARD.getValue());
        entity.setStatus(MessageStatus.STAGED.getValue());
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var message = this.provider.findById(entity.getId(), "master");
        assertNotNull(message);
        assertEquals(entity.getId(), message.getId());

        var input = message.toInput().toBuilder()
                .status(MessageStatus.SUCCEED.getValue())
                .build();

        // 更新数据
        var messages = this.provider.updateBatch(List.of(input), properties.getSupervisor().getUsername(), "master");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = Listx.getFirstOrNull(messages);
        assertNotNull(message);
        assertEquals(entity.getId(), message.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(MulticastMessageEntity.class).eq(MulticastMessageEntity::getStatus, MessageStatus.SUCCEED.getValue())));
    }

    /**
     * @see MulticastMessageProvider#deleteByIds
     */
    @Test
    public void case10() {
        var entity = new MulticastMessageEntity();
        entity.setBroadcasterId(broadcasterEntity.getId());
        entity.setBody(Jsonx.Default().serialize(new StandardBody("系统通知", "测试消息", List.of(new Recipient("alan", "alan@central-x.com")))));
        entity.setMode(PublishMode.STANDARD.getValue());
        entity.setStatus(MessageStatus.STAGED.getValue());
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var deleted = this.provider.deleteByIds(List.of(entity.getId()), "master");
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(MulticastMessageEntity.class).eq(MulticastMessageEntity::getId, entity.getId())));
    }

    /**
     * @see MulticastMessageProvider#deleteBy(Conditions)
     */
    @Test
    public void case11() {
        var entity = new MulticastMessageEntity();
        entity.setBroadcasterId(broadcasterEntity.getId());
        entity.setBody(Jsonx.Default().serialize(new StandardBody("系统通知", "测试消息", List.of(new Recipient("alan", "alan@central-x.com")))));
        entity.setMode(PublishMode.STANDARD.getValue());
        entity.setStatus(MessageStatus.STAGED.getValue());
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var deleted = this.provider.deleteBy(Conditions.of(MulticastMessage.class).eq(MulticastMessage::getBroadcasterId, broadcasterEntity.getId()), "master");
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(MulticastMessageEntity.class).eq(MulticastMessageEntity::getId, entity.getId())));
    }
}
