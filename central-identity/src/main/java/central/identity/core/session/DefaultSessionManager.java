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

package central.identity.core.session;

import central.identity.client.Session;
import central.data.organization.Account;
import central.lang.Stringx;
import central.identity.controller.session.support.Endpoint;
import central.security.signer.KeyPair;
import central.util.cache.CacheRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Base64;
import java.util.Map;

/**
 * 默认的会话
 *
 * @author Alan Yeh
 * @since 2023/05/13
 */
@Slf4j
@Component
public class DefaultSessionManager implements SessionManager {
    /**
     * 会话密钥
     */
    private final KeyPair sessionKey;

    /**
     * 会话容器
     */
    private final SessionStorage storage;

    public DefaultSessionManager(KeyPair sessionKey, CacheRepository repository) {
        this.sessionKey = sessionKey;
        this.storage = new SessionStorage(repository);
    }

    @NotNull
    @Override
    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(this.sessionKey.getVerifyKey().getEncoded());
    }

    @NotNull
    @Override
    public Session issue(@NotNull String tenantCode, @NotNull String issuer, @NotNull Duration timeout, @NotNull Account account, @NotNull Endpoint endpoint, @NotNull Integer limit, @Nonnull String ip, @Nullable Map<String, Object> claims) {
        var session = Session.builder()
                // 用户主键
                .accountId(account.getId())
                // 用户帐号
                .username(account.getUsername())
                // 是否管理员
                .admin(account.getAdmin())
                // 是否超级管理员
                .supervisor(account.getSupervisor())
                // 终端类型
                .endpoint(endpoint.getValue())
                // 颁发者
                .issuer(issuer)
                // 申请会话的 IP
                .ip(ip)
                // 会话有效时间
                .timeout(timeout)
                // 租户标识
                .tenantCode(tenantCode)
                // 附加属性
                .claims(claims)
                // 签名
                .build(this.sessionKey.getSignKey());

        // 保存会话
        this.storage.save(session, limit);
        return session;
    }

    @NotNull
    @Override
    public Session issue(@NotNull String tenantCode, @NotNull Session source, @NotNull String issuer, @NotNull Duration timeout, @NotNull Endpoint endpoint, @NotNull Integer limit, @Nonnull String ip, @Nullable Map<String, Object> claims) {
        var session = Session.builder()
                .accountId(source.getAccountId())
                .username(source.getUsername())
                .admin(source.isAdmin())
                .supervisor(source.isSupervisor())
                .endpoint(source.getEndpoint())
                .issuer(issuer)
                .source(source.getId())
                .ip(ip)
                .timeout(timeout)
                .endpoint(endpoint.getValue())
                .tenantCode(tenantCode)
                .claims(claims)
                .build(this.sessionKey.getSignKey());

        // 保存会话
        this.storage.save(session, limit);
        return session;
    }

    @Override
    public boolean verify(@NotNull String tenantCode, @NotNull Session session) {
        try {
            session.verifier().verify(sessionKey.getVerifyKey());
        } catch (Exception ex) {
            log.info("无效会话: " + ex.getLocalizedMessage(), ex);
            return false;
        }

        // 验证会话是否过期
        if (!this.storage.verify(session)) {
            log.info("会话不存在");
            return false;
        }

        return true;
    }

    @Override
    public void invalid(@NotNull String tenantCode, @NotNull Session session) {
        this.storage.invalid(session);
    }

    @Override
    public void clear(@NotNull String tenantCode, @NotNull String accountId) {
        this.storage.clear(tenantCode, accountId);
    }

    @RequiredArgsConstructor
    private static class SessionStorage {
        private final CacheRepository repository;

        private String getTokenKey(String tenantCode, String accountId, String sessionId) {
            return Stringx.format("{}:security:session:{}:{}", tenantCode, accountId, sessionId);
        }

        private String getEndpointKey(String tenantCode, String accountId, String endpoint) {
            return Stringx.format("{}:security:session:endpoint:{}:{}", tenantCode, accountId, endpoint);
        }

        /**
         * 保存会话凭证
         *
         * @param session 会话
         * @param limit   会话限制
         */
        public void save(Session session, Integer limit) {
            // 获取当前用户所有终端会话
            var list = this.repository.opsList(this.getEndpointKey(session.getTenantCode(), session.getAccountId(), session.getEndpoint()));

            // 如果 limit > 0 则表示需要限制终端的数量
            if (limit > 0 && list.size() >= limit) {
                // 如果当前终端会话数量超过限制，则从后往前排查，排查到无效的就删除
                var ids = list.values();

                String expiredId = null;
                for (var it : ids) {
                    if (!this.repository.hasKey(getTokenKey(session.getTenantCode(), session.getAccountId(), it))) {
                        // 如果这个凭证过期了，那么就删除这个过期的凭证即可
                        expiredId = it;
                        break;
                    }
                }

                // 如果所有凭证都有效，就删除最早登录的那个会话
                if (expiredId == null) {
                    expiredId = list.removeFirst();
                    this.repository.delete(this.getTokenKey(session.getTenantCode(), session.getAccountId(), expiredId));
                } else {
                    // 如果存在着无效 jwt，那么删除该 jwt 即可
                    list.remove(expiredId);
                }
            }

            // 保存新的会话到用户的会话列表里
            list.add(session.getId());

            var value = this.repository.opsValue(this.getTokenKey(session.getTenantCode(), session.getAccountId(), session.getId()));
            value.set(session.getToken(), session.getTimeout());
        }

        /**
         * 验证会话是否有效
         * <p>
         * 如果会话有效，则延长会话有效期
         *
         * @param session 会话
         * @return 是否有效
         */
        public boolean verify(Session session) {
            // 去会话仓库中查看该会话是否已过期
            if (this.repository.hasKey(this.getTokenKey(session.getTenantCode(), session.getAccountId(), session.getId()))) {
                if (Stringx.isNotBlank(session.getSource())) {
                    // 如果会话是由别的会话创建的，则需要检测父会话是否还有效
                    if (!this.repository.hasKey(this.getTokenKey(session.getTenantCode(), session.getAccountId(), session.getSource()))) {
                        // 父会话过期了，则当前会话也过期
                        return false;
                    }
                }

                // 未过期，则延长会话有效期
                this.repository.expire(this.getTokenKey(session.getTenantCode(), session.getAccountId(), session.getId()), session.getTimeout());
                return true;
            } else {
                return false;
            }
        }

        /**
         * 清除会话
         *
         * @param session 会话
         */
        public void invalid(Session session) {
            // 删除会话
            this.repository.delete(this.getTokenKey(session.getTenantCode(), session.getAccountId(), session.getId()));

            // 除除用户在该终端的会话
            this.repository.opsList(this.getEndpointKey(session.getTenantCode(), session.getAccountId(), session.getEndpoint()))
                    .remove(session.getId());
        }

        /**
         * 清除该用户所有会话
         *
         * @param tenantCode 租户标识
         * @param accountId  帐户主键
         */
        public void clear(String tenantCode, String accountId) {
            // TODO MemoryCacheRepository 不支持遍历
            this.repository.delete(this.getTokenKey(tenantCode, accountId, "*"));
            for (var endpoint : Endpoint.values()) {
                this.repository.delete(this.getEndpointKey(tenantCode, accountId, endpoint.getValue()));
            }
        }
    }
}
