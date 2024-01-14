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

package central.studio.identity.controller.sso.oauth.support;

import central.identity.client.Session;
import central.util.concurrent.Expired;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 授权码
 *
 * @author Alan Yeh
 * @since 2022/11/07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationCode implements Expired, Serializable {
    @Serial
    private static final long serialVersionUID = -4562275982598329690L;
    private final long timestamp = System.currentTimeMillis();

    /**
     * 过期时间
     */
    private Duration expires;

    @Override
    public long getExpire(TimeUnit unit) {
        return unit.convert((this.timestamp + this.expires.toMillis()) - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 授权码
     */
    private String code;
    /**
     * 应用标识
     */
    private String clientId;
    /**
     * 重定向地址
     */
    private String redirectUri;
    /**
     * 会话凭证
     */
    private String token;
    /**
     * 授权范围
     */
    private Set<GrantScope> scope;

    private transient Session session;

    public Session getSession() {
        if (this.session == null) {
            this.session = Session.of(this.token);
        }
        return this.session;
    }
}
