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

package central.identity;

import central.data.authority.Permission;
import central.lang.Stringx;
import central.identity.client.Session;
import central.identity.client.SessionClient;
import central.provider.graphql.authority.AuthorizationProvider;
import central.starter.identity.IdentityProvider;
import central.util.CachedSupplier;
import central.util.Objectx;
import central.util.cache.CacheRepository;
import central.util.cache.memory.MemoryCacheRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;

/**
 * Application Identity Provider
 * <p>
 * 应用安全认证
 *
 * @author Alan Yeh
 * @since 2023/05/12
 */
@Slf4j
public class DefaultIdentityProvider implements IdentityProvider {

    @Setter(onMethod_ = @Autowired)
    private SessionClient client;

    /**
     * 缓存有效的 Cookie，有效的 Cookie 只在本地校验
     * 这样可以降低访问服务器的频率
     */
    private final CacheRepository valid = new MemoryCacheRepository();
    /**
     * 保存无效的 Cookie，避免无效的 Cookie 频繁访问服务器
     * 防止内存过大，5 分钟销毁
     */
    private final CacheRepository invalid = new MemoryCacheRepository();

    /**
     * 授权缓存
     */
    private final CacheRepository authorization = new MemoryCacheRepository();

    /**
     * 用于本地校验会话凭证的公钥
     */
    private final CachedSupplier<RSAPublicKey> pubkey = new CachedSupplier<>(Duration.ofSeconds(5), () -> {
        try {
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(client.getPublicKey())));
        } catch (Throwable ex) {
            throw new RuntimeException("获取公钥失败: " + ex.getLocalizedMessage(), ex);
        }
    });

    /**
     * 校验会话凭证
     * <p>
     * 这里做了双重缓存机制，一个缓存已确认的凭证，一个是缓存错误的凭证，用于尽可能减少到服务器商校验的过程
     *
     * @param token 会话凭证
     */
    @Override
    public void onReceiveAuthenticationToken(String token) {
        if (Stringx.isNullOrEmpty(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户凭证无效");
        }

        var session = Session.of(token);

        // 看看无效的 JWT 中是否已经包含了该会话
        // 如果包含了，则直接返回错误信息
        if (this.invalid.hasKey(session.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户凭证无效");
        }

        if (this.valid.hasKey(session.getId())) {
            // 本地校验
            try {
                session.verifier()
                        .verify(this.pubkey.get());
            } catch (Exception ex) {
                log.error("本地校验会话失败: " + ex.getLocalizedMessage(), ex);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户凭证无效");
            }
        } else {
            // 服务端校验
            try {
                boolean isValid = this.client.verify(token);
                if (isValid) {
                    // 校验成功，保存
                    this.valid.opsValue(session.getId()).set("true", Duration.ofSeconds(5));
                } else {
                    // 校验失败
                    this.invalid.opsValue(session.getId()).set("true", Duration.ofMinutes(30));
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户凭证无效");
                }
            } catch (Throwable throwable) {
                if (throwable instanceof ResponseStatusException ex) {
                    throw ex;
                } else {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "认证服务不可用，请联系管理员处理", throwable);
                }
            }
        }
    }

    @Setter
    private AuthorizationProvider provider;

    /**
     * 获取授权信息
     *
     * @param token             JWT
     * @param authorizationInfo 授权信息
     */
    @Override
    public void onReceiveAuthorizationInfo(String token, SimpleAuthorizationInfo authorizationInfo) {
        var session = Session.of(token);

        var permissions = provider.findPermissions(session.getAccountId(), null, session.getTenantCode());
        // 添加权限
        authorizationInfo.addStringPermissions(permissions.stream().map(Permission::getCode).toList());
    }

    /**
     * 注销会话
     *
     * @param token 会话凭证
     */
    @Override
    public void onLogout(String token) {
        Session session = Objectx.getSilently(() -> Session.of(token));
        if (session == null) {
            // 解析会话异常
            return;
        }

        if (!this.invalid.hasKey(session.getId())) {
            // 该会话已经无效了
            return;
        }

        try {
            this.client.logout(token);
            this.invalid.opsValue(session.getId()).set("true", Duration.ofMinutes(30));
        } catch (Throwable throwable) {
            if (throwable instanceof ResponseStatusException ex) {
                throw ex;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "认证服务不可用，请联系管理员处理", throwable);
            }
        }


    }
}
