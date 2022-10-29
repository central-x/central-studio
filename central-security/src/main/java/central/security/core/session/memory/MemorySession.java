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

package central.security.core.session.memory;

import central.api.client.security.SessionClaims;
import central.security.controller.session.support.Endpoint;
import central.security.core.SecuritySession;
import central.util.Listx;
import central.util.Setx;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

/**
 * Memory Session
 * 内存会话
 *
 * @author Alan Yeh
 * @since 2022/10/20
 */
@Component
public class MemorySession implements SecuritySession, InitializingBean {
    /**
     * 会话
     * tenant -> accountId
     */
    private final Map<String, Map<String, List<Session>>> sessions = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new CustomizableThreadFactory("memory-session-cleaner"));

    @Override
    public void afterPropertiesSet() throws Exception {
        executor.scheduleAtFixedRate(new Cleaner(this.sessions), Duration.ofSeconds(5).toMillis(), Duration.ofSeconds(5).toMillis(), TimeUnit.MILLISECONDS);
    }

    @RequiredArgsConstructor
    private static class Cleaner implements Runnable {
        private final Map<String, Map<String, List<Session>>> sessions;

        @Override
        public void run() {
            for (var tenantIt : sessions.entrySet()) {
                for (var accountIt : tenantIt.getValue().entrySet()) {
                    var timeout = new ArrayList<Session>();
                    for (var session : accountIt.getValue()) {
                        if (session.isTimeout()) {
                            timeout.add(session);
                        }
                    }
                    // 清除过期会话
                    accountIt.getValue().removeAll(timeout);
                }
            }
        }
    }

    @Override
    public synchronized void save(DecodedJWT session, Integer limit) {
        var endpoint = Endpoint.resolve(session.getClaim(SessionClaims.ENDPOINT).asString());
        var tenant = session.getClaim(SessionClaims.TENANT_CODE).asString();
        var accountId = session.getSubject();

        // 获取当前端终所有会话
        var list = this.sessions.computeIfAbsent(tenant, key -> new ConcurrentHashMap<>())
                .computeIfAbsent(accountId, key -> new ArrayList<>());
        if (limit > 0) {
            // limit > 0，说明需要限定指定数量的会话
            var sessions = list.stream()
                    .filter(it -> Objects.equals(it.getSession().getClaim(SessionClaims.ENDPOINT).asString(), endpoint.getValue()))
                    .sorted(Comparator.comparing(Session::getTimestamp))
                    .toList();
            if (sessions.size() >= limit) {
                // 删除最早登录的那个会话
                list.remove(sessions.get(0));
            }
        }

        // 保存新会话
        list.add(new Session(session));
    }

    @Override
    public boolean verify(DecodedJWT session) {
        var tenant = session.getClaim(SessionClaims.TENANT_CODE).asString();
        var accountId = session.getSubject();
        var s = this.sessions.computeIfAbsent(tenant, key -> new ConcurrentHashMap<>())
                .computeIfAbsent(accountId, key -> new ArrayList<>())
                .stream().filter(it -> Objects.equals(it.getId(), session.getId()))
                .findFirst().orElse(null);
        if (s == null || s.isTimeout()) {
            // 没有登记该会话，则该会话已过期了
            return false;
        }
        s.setTimestamp(System.currentTimeMillis());
        return true;
    }

    @Override
    public void invalid(DecodedJWT session) {
        var tenant = session.getClaim(SessionClaims.TENANT_CODE).asString();
        var accountId = session.getSubject();
        // 当前用户的所有会话
        var sessions = this.sessions.computeIfAbsent(tenant, key -> new ConcurrentHashMap<>())
                .computeIfAbsent(accountId, key -> new ArrayList<>());
        // 找到当前会话
        var s = sessions.stream()
                .filter(it -> Objects.equals(it.getId(), session.getId()))
                .findFirst().orElse(null);
        if (s != null && !s.isTimeout()) {
            sessions.remove(s);
        }

        // 找到由当前会话颁发的其它会话，一同注销
        var ids = Setx.newHashSet(session.getId());
        while (Setx.isNotEmpty(ids)) {
            var granted = sessions.stream()
                    .filter(it -> ids.contains(it.getSession().getClaim(SessionClaims.SOURCE).asString()))
                    .toList();
            if (Listx.isNullOrEmpty(granted)){
                break;
            }
            sessions.removeAll(granted);
            ids.clear();
            ids.addAll(granted.stream().map(Session::getId).toList());
        }
    }

    @Override
    public void clear(String tenant, String accountId) {
        var sessions = this.sessions.computeIfAbsent(tenant, key -> new ConcurrentHashMap<>())
                .computeIfAbsent(accountId, key -> new ArrayList<>());
        sessions.clear();
    }

    @Getter
    private static class Session {
        @Setter
        private long timestamp = System.currentTimeMillis();

        private final String id;

        private final DecodedJWT session;

        public Session(DecodedJWT session) {
            this.session = session;
            this.id = session.getId();
        }

        public boolean isTimeout() {
            var timeout = session.getClaim(SessionClaims.TIMEOUT).asLong();
            return System.currentTimeMillis() - timestamp >= timeout;
        }
    }
}
