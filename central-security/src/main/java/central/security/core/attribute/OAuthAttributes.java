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

package central.security.core.attribute;

import central.lang.Attribute;
import central.security.controller.sso.oauth.option.GrantScope;
import central.security.core.CookieManager;

import java.time.Duration;
import java.util.Set;

/**
 * OAuth 2.0 配置
 *
 * @author Alan Yeh
 * @since 2023/02/15
 */
public interface OAuthAttributes {
    /**
     * 是否启用
     */
    Attribute<Boolean> ENABLED = Attribute.of("oauth.enabled", false);
    /**
     * 授权范围
     */
    Attribute<Set<GrantScope>> SCOPES = Attribute.of("oauth.scopes", Set.of(GrantScope.BASIC, GrantScope.CONTRACT));
    /**
     * 自动授权（不需要用户手动点击授权）
     */
    Attribute<Boolean> AUTO_GRANTING = Attribute.of("oauth.auto_granting", true);
    /**
     * 授权码（Authorization Code）过期时间
     */
    Attribute<Duration> AUTHORIZATION_CODE_TIMEOUT = Attribute.of("oauth.code_timeout", Duration.ofMinutes(1));
    /**
     * 授权事务 Cookie
     */
    Attribute<CookieManager> GRANTING_TRANS_COOKIE = Attribute.of("oauth.granting_trans_cookie", () -> new CookieManager("X-OAuth-Trans"));
    /**
     * 授权事务过期时间（也就是说用户必须在这个时间内完成授权过程，否则需要重新发起事务）
     */
    Attribute<Duration> GRANTING_TRANS_TIMEOUT = Attribute.of("oauth.granting_trans_timeout", Duration.ofMinutes(10));
    /**
     * 访问凭证（Access Token）过期时间，且不能续期
     */
    Attribute<Duration> ACCESS_TOKEN_TIMEOUT = Attribute.of("oauth.access_token_timeout", Duration.ofMinutes(30));
}
