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

package central.identity.controller.sso.cas.support.impl;

import central.identity.controller.sso.cas.support.CasSession;
import central.identity.controller.sso.cas.support.ServiceTicket;
import central.util.concurrent.ExpiredMap;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存会话
 *
 * @author Alan Yeh
 * @since 2022/11/09
 */
@Component
//@ConditionalOnMissingBean(CasSession.class)
public class CasMemorySession implements CasSession, DisposableBean {
    /**
     * ticket 缓存
     * <p>
     * tenant -> ticketId -> ticket
     */
    private final Map<String, Map<String, ServiceTicket>> tickets = new ConcurrentHashMap<>();

    /**
     * ticket 与会话的绑定关系
     * <p>
     * tenant -> sessionId -> ticket
     */
    private final Map<String, Map<String, List<ServiceTicket>>> bindings = new ConcurrentHashMap<>();

    @Override
    public void destroy() throws Exception {
        var keys = new HashSet<>(tickets.keySet());
        for (var key : keys) {
            var map = tickets.remove(key);
            if (map instanceof Closeable closeable) {
                closeable.close();
            }
        }
    }

    @Override
    public void save(String tenant, ServiceTicket ticket) {
        this.tickets.computeIfAbsent(tenant, key -> new ExpiredMap<>())
                .put(ticket.getTicket(), ticket);
    }

    @Override
    public ServiceTicket remove(String tenant, String ticket) {
        return this.tickets.computeIfAbsent(tenant, key -> new ExpiredMap<>())
                .remove(ticket);
    }

    @Override
    public void bindTicket(String tenant, ServiceTicket ticket) {
        this.bindings.computeIfAbsent(tenant, key -> new ConcurrentHashMap<>())
                .computeIfAbsent(ticket.getSessionJwt().getId(), key -> new ArrayList<>())
                .add(ticket);
    }

    @Override
    public List<ServiceTicket> getTicketBySession(String tenant, DecodedJWT session) {
        return this.bindings.computeIfAbsent(tenant, key -> new ConcurrentHashMap<>())
                .computeIfAbsent(session.getId(), key -> new ArrayList<>());
    }

    @Override
    public void removeTicketBySession(String tenant, DecodedJWT session) {
        this.bindings.computeIfAbsent(tenant, key -> new ConcurrentHashMap<>())
                .remove(session.getId());
    }
}
