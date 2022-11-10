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

package central.security.controller.sso.oauth.support;

import central.security.controller.sso.oauth.option.GrantScope;
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
 * 授权事务
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationTransaction implements Expired, Serializable {
    @Serial
    private static final long serialVersionUID = 1302762641139032299L;

    /**
     * 事务创建时间
     */
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
     * 事务标识
     */
    private String id;
    /**
     * 应用标识
     */
    private String clientId;
    /**
     * 开发者申请的权限
     */
    private Set<GrantScope> scopes;

    // 创建事务时用户 URL 的摘要（SHA256）
    private String digest;

    /**
     * 用户是否已授权
     */
    private boolean granted;
    /**
     * 用户已授梳的范围
     */
    private Set<String> grantedScope;
    /**
     * 用户会话
     */
    private String session;
}
