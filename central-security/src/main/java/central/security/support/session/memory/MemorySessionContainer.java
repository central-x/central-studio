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

package central.security.support.session.memory;

import central.api.client.security.SessionClaims;
import central.security.support.session.SessionContainer;
import central.util.Collectionx;
import central.util.ExpirableValue;
import central.util.concurrent.ConsumableQueue;
import central.util.concurrent.DelayedElement;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import org.springframework.util.function.ThrowingConsumer;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.DelayQueue;

/**
 * 内存会话
 *
 * @author Alan Yeh
 * @since 2023/05/13
 */
@Slf4j
@Component
public class MemorySessionContainer implements SessionContainer, DisposableBean {
    /**
     * 会话存储
     * <p>
     * tenant -> accountId -> key -> token
     */
    private final Map<String, Map<String, Map<String, ExpirableValue<DecodedJWT>>>> sessions = new HashMap<>();
    private final ConsumableQueue<DelayedElement<DecodedJWT>, DelayQueue<DelayedElement<DecodedJWT>>> timeoutQueue;

    public MemorySessionContainer() {
        // 使用延迟队列清除会话
        this.timeoutQueue = new ConsumableQueue<>(new DelayQueue<>(), "central-security.session.memory.cleaner");
        this.timeoutQueue.addConsumer((ThrowingConsumer<DelayQueue<DelayedElement<DecodedJWT>>>) queue -> {
            try {
                while (true) {
                    var element = queue.take();
                    var token = element.getElement();
                    var tenantCode = token.getClaim(SessionClaims.TENANT_CODE).asString();
                    var accountId = token.getSubject();

                    var session = sessions.getOrDefault(tenantCode, Collectionx.emptyMap())
                            .getOrDefault(accountId, Collectionx.emptyMap())
                            .get(token.getId());
                    if (session != null && session.isExpired()){
                        remove(tenantCode, token);
                    }
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        });
    }

    @Override
    public void destroy() throws Exception {
        this.timeoutQueue.close();
    }

    @Override
    public void save(String tenantCode, String accountId, DecodedJWT token, Duration expires) {
        this.sessions.computeIfAbsent(tenantCode, key -> new HashMap<>())
                .computeIfAbsent(accountId, key -> new HashMap<>())
                .put(token.getId(), new ExpirableValue<>(token, expires));
        this.timeoutQueue.add(new DelayedElement<>(token, expires));
    }

    @Override
    public List<DecodedJWT> get(String tenantCode, String accountId) {
        return this.sessions.getOrDefault(tenantCode, Collectionx.emptyMap())
                .getOrDefault(accountId, Collectionx.emptyMap())
                .values().stream()
                .map(it -> it.getValue().orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public boolean exists(String tenantCode, DecodedJWT token) {
        var accountId = token.getSubject();

        var session = this.sessions.getOrDefault(tenantCode, Collectionx.emptyMap())
                .getOrDefault(accountId, Collectionx.emptyMap())
                .get(token.getId());

        return session != null && !session.isExpired();
    }

    @Override
    public void expire(String tenantCode, DecodedJWT token, Duration expires) {
        var accountId = token.getSubject();

        var session = this.sessions.getOrDefault(tenantCode, Collectionx.emptyMap())
                .getOrDefault(accountId, Collectionx.emptyMap())
                .get(token.getId());

        session.expires(expires);
        // 添加新的会话倒计时
        this.timeoutQueue.add(new DelayedElement<>(token, expires));
    }

    @Override
    public void remove(String tenantCode, DecodedJWT token) {
        var accountId = token.getSubject();
        this.sessions.getOrDefault(tenantCode, Collectionx.emptyMap())
                .getOrDefault(accountId, Collectionx.emptyMap())
                .remove(token.getId());
    }

    @Override
    public void clear(String tenantCode, String accountId) {
        this.sessions.getOrDefault(tenantCode, Collectionx.emptyMap())
                .remove(accountId);
    }
}
