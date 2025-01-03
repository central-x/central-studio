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

import central.data.multicast.MulticastBroadcasterInput;
import central.data.multicast.MulticastMessage;
import central.data.multicast.MulticastMessageInput;
import central.data.multicast.option.MessageStatus;
import central.data.multicast.option.PublishMode;
import central.multicast.client.body.Recipient;
import central.multicast.client.body.StandardBody;
import central.provider.graphql.multicast.MulticastMessageProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.multicast.MulticastBroadcasterPersistence;
import central.studio.provider.database.persistence.multicast.MulticastMessagePersistence;
import central.studio.provider.database.persistence.multicast.entity.MulticastBroadcasterEntity;
import central.studio.provider.database.persistence.multicast.entity.MulticastMessageEntity;
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
 * MulticastMessageProvider Test Cases
 *
 * @author Alan Yeh
 * @since 2022/11/04
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestMulticastMessageProvider {

    @Setter(onMethod_ = @Autowired)
    private MulticastMessageProvider provider;

    @Setter(onMethod_ = @Autowired)
    private MulticastMessagePersistence persistence;

    @Setter(onMethod_ = @Autowired)
    private MulticastBroadcasterPersistence broadcasterPersistence;

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
        this.persistence.deleteBy(Conditions.of(MulticastMessageEntity.class), tenant.getCode());
        this.broadcasterPersistence.deleteBy(Conditions.of(MulticastBroadcasterEntity.class).like(MulticastBroadcasterEntity::getCode, "test%"), tenant.getCode());
    }

    /**
     * @see MulticastMessageProvider#insert
     * @see MulticastMessageProvider#findById
     * @see MulticastMessageProvider#update
     * @see MulticastMessageProvider#findByIds
     * @see MulticastMessageProvider#countBy
     * @see MulticastMessageProvider#deleteByIds
     */
    @Test
    public void case1() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();
        var broadcaster = this.broadcasterPersistence.insert(MulticastBroadcasterInput.builder()
                .applicationId(application.getId())
                .code("test")
                .name("测试推送器")
                .type("email_smtp")
                .enabled(Boolean.TRUE)
                .remark("测试推送器")
                .params(Jsonx.Default().serialize(Map.of(
                        "host", "smtp.exmail.qq.com",
                        "ssl", "enabled",
                        "port", "445",
                        "username", "no-reply@central-x.com",
                        "password", "x.123456",
                        "name", "No Reply"
                )))
                .build(), "syssa", tenant.getCode());

        var input = MulticastMessageInput.builder()
                .broadcasterId(broadcaster.getId())
                .body(Jsonx.Default().serialize(new StandardBody("系统通知", "测试消息", List.of(new Recipient("alan", "alan@central-x.com")))))
                .mode(PublishMode.STANDARD.getValue())
                .status(MessageStatus.STAGED.getValue())
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", tenant.getCode());
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getBroadcasterId(), insert.getBroadcasterId());
        assertEquals(input.getBroadcasterId(), insert.getBroadcaster().getId());
        assertEquals(input.getBody(), insert.getBody());
        assertEquals(input.getMode(), insert.getMode());
        assertEquals(input.getStatus(), insert.getStatus());

        // test findById
        var findById = this.provider.findById(insert.getId(), tenant.getCode());
        assertNotNull(findById);
        assertEquals(insert.getId(), findById.getId());
        assertEquals(insert.getBroadcasterId(), findById.getBroadcasterId());
        assertEquals(insert.getBroadcasterId(), findById.getBroadcaster().getId());
        assertEquals(insert.getBody(), findById.getBody());
        assertEquals(insert.getMode(), findById.getMode());
        assertEquals(insert.getStatus(), findById.getStatus());

        // test countBy
        var count = this.provider.countBy(Conditions.of(MulticastMessage.class).eq(MulticastMessage::getBroadcasterId, broadcaster.getId()), tenant.getCode());
        assertEquals(1, count);

        // test update
        this.provider.update(insert.toInput().status(MessageStatus.SUCCEED.getValue()).build(), "syssa", tenant.getCode());

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), tenant.getCode());
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getBroadcasterId(), fetched.getBroadcasterId());
        assertEquals(insert.getBroadcasterId(), fetched.getBroadcaster().getId());
        assertEquals(insert.getBody(), fetched.getBody());
        assertEquals(insert.getMode(), fetched.getMode());
        assertEquals(MessageStatus.SUCCEED.getValue(), fetched.getStatus());

        // test deleteByIds
        count = this.provider.deleteByIds(List.of(insert.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(MulticastMessageEntity.class).eq(MulticastMessageEntity::getBroadcasterId, broadcaster.getId()), tenant.getCode());
        assertEquals(0, count);
    }

    /**
     * @see MulticastMessageProvider#insertBatch
     * @see MulticastMessageProvider#findBy
     * @see MulticastMessageProvider#updateBatch
     * @see MulticastMessageProvider#pageBy
     * @see MulticastMessageProvider#deleteBy
     */
    @Test
    public void case2() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();
        var broadcaster = this.broadcasterPersistence.insert(MulticastBroadcasterInput.builder()
                .applicationId(application.getId())
                .code("test")
                .name("测试推送器")
                .type("email_smtp")
                .enabled(Boolean.TRUE)
                .remark("测试推送器")
                .params(Jsonx.Default().serialize(Map.of(
                        "host", "smtp.exmail.qq.com",
                        "ssl", "enabled",
                        "port", "445",
                        "username", "no-reply@central-x.com",
                        "password", "x.123456",
                        "name", "No Reply"
                )))
                .build(), "syssa", tenant.getCode());

        var input = MulticastMessageInput.builder()
                .broadcasterId(broadcaster.getId())
                .body(Jsonx.Default().serialize(new StandardBody("系统通知", "测试消息", List.of(new Recipient("alan", "alan@central-x.com")))))
                .mode(PublishMode.STANDARD.getValue())
                .status(MessageStatus.STAGED.getValue())
                .build();

        // test insertBatch
        var insertBatch = this.provider.insertBatch(List.of(input), "syssa", tenant.getCode());
        assertNotNull(insertBatch);
        assertEquals(1, insertBatch.size());

        var insert = Listx.getFirstOrNull(insertBatch);
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getBroadcasterId(), insert.getBroadcasterId());
        assertEquals(input.getBroadcasterId(), insert.getBroadcaster().getId());
        assertEquals(input.getBody(), insert.getBody());
        assertEquals(input.getMode(), insert.getMode());
        assertEquals(input.getStatus(), insert.getStatus());

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(MulticastMessage.class).eq(MulticastMessage::getBroadcasterId, broadcaster.getId()), null, tenant.getCode());
        assertNotNull(findBy);
        assertEquals(1, findBy.size());

        var fetched = Listx.getFirstOrNull(findBy);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getBroadcasterId(), fetched.getBroadcasterId());
        assertEquals(insert.getBroadcasterId(), fetched.getBroadcaster().getId());
        assertEquals(insert.getBody(), fetched.getBody());
        assertEquals(insert.getMode(), fetched.getMode());
        assertEquals(insert.getStatus(), fetched.getStatus());

        // test updateBatch
        this.provider.updateBatch(List.of(insert.toInput().status(MessageStatus.SUCCEED.getValue()).build()), "syssa", tenant.getCode());

        // test pageBy
        var pageBy = this.provider.pageBy(1, 10, Conditions.of(MulticastMessage.class).eq(MulticastMessage::getBroadcasterId, broadcaster.getId()), null, tenant.getCode());
        assertNotNull(pageBy);
        assertEquals(1, pageBy.getPager().getPageIndex());
        assertEquals(10, pageBy.getPager().getPageSize());
        assertEquals(1, pageBy.getPager().getPageCount());
        assertEquals(1, pageBy.getPager().getItemCount());
        assertEquals(1, pageBy.getData().size());

        fetched = Listx.getFirstOrNull(pageBy.getData());
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getBroadcasterId(), fetched.getBroadcasterId());
        assertEquals(insert.getBroadcasterId(), fetched.getBroadcaster().getId());
        assertEquals(insert.getBody(), fetched.getBody());
        assertEquals(insert.getMode(), fetched.getMode());
        assertEquals(MessageStatus.SUCCEED.getValue(), fetched.getStatus());

        // test deleteByIds
        var count = this.provider.deleteByIds(List.of(insert.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(MulticastMessageEntity.class).eq(MulticastMessageEntity::getBroadcasterId, broadcaster.getId()), tenant.getCode());
        assertEquals(0, count);
    }
}
