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

package central.api.client.security;

import central.lang.Stringx;
import central.security.Signerx;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Setter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.web.server.ResponseStatusException;

import java.io.Closeable;
import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Session Verifier
 * 会话校验
 * <p>
 * 由于会话需要通过 {@link SessionClient#verify} 才能真校验有期，但是这会产生一次 http 远程调用。
 * 为了提高验证效率，每 5 秒才会去服务器检查有效期，期间使用 {@link SessionClient#getPublicKey} 提供的公钥进行验证
 *
 * @author Alan Yeh
 * @since 2022/10/24
 */
public class SessionVerifier implements DisposableBean {

    @Setter(onMethod_ = @Autowired)
    private SessionClient client;

    /**
     * 公钥
     * 通过 {@link SessionClient#getPublicKey} 方法获取，一般情况下不会更新
     */
    private RSAPublicKey publicKey;

    /**
     * 缓存
     * tenant -> cache
     */
    private final Map<String, Cache> caches = new ConcurrentHashMap<>();

    /**
     * 验证会话是否有效
     *
     * @param session 会话
     */
    public boolean verify(String session) {
        if (Stringx.isNullOrBlank(session)) {
            return false;
        }

        DecodedJWT sessionJwt;

        try {
            sessionJwt = JWT.decode(session);
        } catch (Exception ex) {
            // 不是有效会话
            return false;
        }

        var tenant = sessionJwt.getClaim(SessionClaims.TENANT_CODE).asString();
        if (Stringx.isNullOrBlank(tenant)) {
            // 如果有没有租户信息，那么这个会话一定是假的
            return false;
        }

        var cache = this.caches.computeIfAbsent(tenant, key -> new Cache());
        if (cache.containsInvalid(sessionJwt.getId())) {
            // 该会话经校验过是无效的，因此不需要再处理下面的逻辑
            return false;
        }

        if (cache.contains(sessionJwt.getId())) {
            return this.verify(sessionJwt);
        }

        // 去服务端校验是否有效
        try {
            if (this.client.verify(session)) {
                // 校验成功，会话有效
                cache.put(sessionJwt.getId(), 100);
                return true;
            } else {
                // 校验失败，会话无效
                cache.putInvalid(sessionJwt.getId(), Duration.ofMinutes(30).toMillis());
                return false;
            }
        } catch (Throwable throwable) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "认证服务器不可用: " + throwable.getMessage(), throwable);
        }
    }

    /**
     * 保存无效会话
     *
     * @param session 会话
     */
    public void invalid(String session) {
        if (Stringx.isNullOrBlank(session)) {
            return;
        }
        DecodedJWT sessionJwt;

        try {
            sessionJwt = JWT.decode(session);
        } catch (Exception ex) {
            // 不是有效会话
            return;
        }

        var tenant = sessionJwt.getClaim(SessionClaims.TENANT_CODE).asString();
        if (Stringx.isNullOrBlank(tenant)) {
            // 如果有没有租户信息，那么这个会话一定是假的
            return;
        }

        var cache = this.caches.computeIfAbsent(tenant, key -> new Cache());
        if (cache.contains(sessionJwt.getId())) {
            if (this.verify(sessionJwt)) {
                // 调用服务器，将会话置为无效
                this.client.logout(session);
                // 将该会话保存为无效会话
                cache.putInvalid(sessionJwt.getId(), Duration.ofMinutes(30).toMillis());
            }
        }
    }

    /**
     * 通过公钥验证会话有效期
     * <p>
     * 这是因为可能会有人直接伪造相同 ID 的会话来攻击，因此通过公钥可以有效避免这个问题
     *
     * @param session 会话
     */
    private boolean verify(DecodedJWT session) {
        if (this.publicKey == null) {
            this.publicKey = (RSAPublicKey) Signerx.RSA.getVerifyKey(this.client.getPublicKey());
        }

        try {
            JWT.require(Algorithm.RSA256(this.publicKey)).build()
                    .verify(session);
            return true;
        } catch (Exception ignored) {
            // 没有通过公钥验证
            return false;
        }
    }

    @Override
    public void destroy() throws Exception {
        var keys = new HashSet<>(this.caches.keySet());
        for (var key : keys) {
            var cache = this.caches.remove(key);
            try {
                cache.close();
            } catch (Exception ignored) {
            }
        }
    }

    private static class Cache implements Closeable {

        private final ScheduledExecutorService cleaner;

        @Override
        public void close() throws IOException {
            this.cleaner.shutdownNow();
        }

        public Cache() {
            // 使用定时器主动清除缓存
            this.cleaner = Executors.newSingleThreadScheduledExecutor(new CustomizableThreadFactory("central-security.session.validator"));
            this.cleaner.scheduleWithFixedDelay(() -> {
                var invalidKeys = new ArrayList<String>();
                var now = System.currentTimeMillis();
                this.cache.forEach((key, value) -> {
                    if (value >= now) {
                        invalidKeys.add(key);
                    }
                });
                invalidKeys.forEach(this.cache::remove);

                invalidKeys.clear();
                this.invalid.forEach((key, value) -> {
                    if (value >= now) {
                        invalidKeys.add(key);
                    }
                });
                invalidKeys.forEach(this.invalid::remove);
            }, Duration.ofSeconds(5).toMillis(), Duration.ofSeconds(5).toMillis(), TimeUnit.MILLISECONDS);
        }

        // 保存有效的会话主键
        private final Map<String, Long> cache = new ConcurrentHashMap<>();

        // 保存已验证的无效会话主键
        private final Map<String, Long> invalid = new ConcurrentHashMap<>();

        /**
         * 是否包含指定有效的会话主键
         *
         * @param id 会话主键
         */
        public boolean contains(String id) {
            var expires = this.cache.get(id);
            if (expires == null) {
                return false;
            }

            if (expires >= System.currentTimeMillis()) {
                this.cache.remove(id);
                return false;
            }
            return true;
        }

        /**
         * 保存会话，在指定时间前都有效
         *
         * @param id      会话主键
         * @param expires 过期时间
         */
        public void put(String id, long expires) {
            this.cache.put(id, expires);
        }

        /**
         * 无效会话是否包含指定会话主键
         *
         * @param id 会话主键
         */
        public boolean containsInvalid(String id) {
            var expires = this.invalid.get(id);
            if (expires == null) {
                return false;
            }

            if (expires >= System.currentTimeMillis()) {
                this.invalid.remove(id);
                return false;
            }
            return true;
        }

        /**
         * 保存无效会话，在指定时间内不需要再次验证
         *
         * @param id      会话主键
         * @param expires 过期时间
         */
        public void putInvalid(String id, long expires) {
            this.invalid.put(id, expires);
        }
    }
}
