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

package central.security.client;

import central.lang.Stringx;
import central.security.Signerx;
import central.util.cache.CacheRepository;
import central.util.cache.memory.MemoryCacheRepository;
import lombok.Setter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.Closeable;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

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
     * 用于保存有效的会话
     */
    private final CacheRepository validRepository = new MemoryCacheRepository();
    /**
     * 用户保存无效的会话
     */
    private final CacheRepository invalidRepository = new MemoryCacheRepository();

    /**
     * 公钥
     * 通过 {@link SessionClient#getPublicKey} 方法获取，一般情况下不会更新
     */
    private RSAPublicKey publicKey;

    /**
     * 验证会话是否有效
     *
     * @param token 会话凭证
     */
    public boolean verify(String token) {
        if (Stringx.isNullOrBlank(token)) {
            return false;
        }

        Session session;

        try {
            session = Session.of(token);
        } catch (Exception ex) {
            // 不是有效会话
            return false;
        }

        var tenant = session.getTenantCode();
        if (Stringx.isNullOrBlank(tenant)) {
            // 如果有没有租户信息，那么这个会话一定是假的
            return false;
        }

        if (invalidRepository.hasKey(Stringx.format("{}:{}", session.getTenantCode(), session.getId()))) {
            // 该会话经校验过是无效的，因此不需要再处理下面的逻辑
            return false;
        }

        if (validRepository.hasKey(Stringx.format("{}:{}", session.getTenantCode(), session.getId()))) {
            // 本地校验即可
            return this.verify(session);
        }

        // 去服务端校验是否有效
        try {
            if (this.client.verify(session.getToken())) {
                // 校验成功，会话有效
                this.validRepository.opsValue(Stringx.format("{}:{}", session.getTenantCode(), session.getId()))
                        .set("true", Duration.ofSeconds(1));
                return true;
            } else {
                // 校验失败，会话无效
                this.invalidRepository.opsValue(Stringx.format("{}:{}", session.getTenantCode(), session.getId()))
                        .set("true", Duration.ofMillis(30));
                return false;
            }
        } catch (Throwable throwable) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "认证服务器不可用: " + throwable.getMessage(), throwable);
        }
    }

    /**
     * 保存无效会话
     *
     * @param token 会话凭证
     */
    public void invalid(String token) {
        Session session;
        try {
            session = Session.of(token);
        } catch (Exception ignored) {
            // 会话解析异常
            return;
        }

        if (this.verify(token)) {
            // 调用服务器，将会话置为无效
            this.client.logout(token);
            // 将该会话保存为无效会话
            this.invalidRepository.opsValue(Stringx.format("{}:{}", session.getTenantCode(), session.getId()))
                    .set("true", Duration.ofMinutes(30));
        }
    }

    /**
     * 通过公钥验证会话有效期
     * <p>
     * 这是因为可能会有人直接伪造相同 ID 的会话来攻击，因此通过公钥可以有效避免这个问题
     *
     * @param session 会话
     */
    private boolean verify(Session session) {
        if (this.publicKey == null) {
            this.publicKey = (RSAPublicKey) Signerx.RSA.getVerifyKey(this.client.getPublicKey());
        }

        try {
            session.verifier().verify(this.publicKey);
            return true;
        } catch (Exception ignored) {
            // 没有通过公钥验证
            return false;
        }
    }

    @Override
    public void destroy() throws Exception {
        if (this.validRepository instanceof Closeable closeable) {
            closeable.close();
        }
        if (this.invalidRepository instanceof Closeable closeable) {
            closeable.close();
        }
    }
}
