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

package central.studio.identity.controller.sso.oauth.support.impl;

import central.studio.identity.controller.sso.oauth.support.AuthorizationCode;
import central.studio.identity.controller.sso.oauth.support.AuthorizationTransaction;
import central.studio.identity.controller.sso.oauth.support.OAuthSession;
import central.util.concurrent.ExpiredMap;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存会话管理
 *
 * @author Alan Yeh
 * @since 2022/11/09
 */
@Component
//@ConditionalOnMissingBean(OAuthSession.class)
public class OAuthMemorySession implements OAuthSession, DisposableBean {
    private final Map<String, Map<String, AuthorizationCode>> codes = new ConcurrentHashMap<>();

    private final Map<String, Map<String, AuthorizationTransaction>> transactions = new ConcurrentHashMap<>();

    @Override
    public void destroy() throws Exception {
        var keys = new HashSet<>(codes.keySet());
        for (var key : keys) {
            var map = codes.remove(key);
            if (map instanceof Closeable closeable) {
                closeable.close();
            }
        }

        keys = new HashSet<>(transactions.keySet());
        for (var key : keys) {
            var map = transactions.remove(key);
            if (map instanceof Closeable closeable) {
                closeable.close();
            }
        }
    }


    @Override
    public void saveCode(String tenant, AuthorizationCode code) {
        this.codes.computeIfAbsent(tenant, key -> new ExpiredMap<>())
                .put(code.getCode(), code);
    }

    @Override
    public AuthorizationCode getCode(String tenant, String code) {
        return this.codes.computeIfAbsent(tenant, key -> new ExpiredMap<>())
                .remove(code);
    }

    @Override
    public void saveTransaction(String tenant, AuthorizationTransaction transaction) {
        this.transactions.computeIfAbsent(tenant, key -> new ExpiredMap<>())
                .put(transaction.getId(), transaction);
    }

    @Override
    public AuthorizationTransaction getAndRemoveTransaction(String tenant, String id) {
        return this.transactions.computeIfAbsent(tenant, key -> new ExpiredMap<>())
                .remove(id);
    }
}
