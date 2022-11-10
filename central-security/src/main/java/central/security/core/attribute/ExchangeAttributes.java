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
import central.security.controller.session.support.EndpointConfig;
import central.security.controller.sso.cas.option.Scope;
import central.security.controller.sso.oauth.option.GrantScope;
import central.security.core.CookieManager;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 交换属性
 *
 * @author Alan Yeh
 * @since 2022/10/19
 */
public interface ExchangeAttributes {
    /**
     * 会话配置
     */
    interface Session {
        /**
         * 会话超时时间
         */
        Attribute<Long> TIMEOUT = Attribute.of("session.timeout", () -> Duration.ofMinutes(30).toMillis());
        /**
         * 会话颁发者
         */
        Attribute<String> ISSUER = Attribute.of("session.issuer", "com.central-x.security");
        /**
         * 会话 Cookie
         */
        Attribute<CookieManager> COOKIE = Attribute.of("session.cookie", () -> new CookieManager("Authorization", null, true, false));
    }

    /**
     * 终端配置
     */
    interface Endpoint {
        /**
         * Web 端配置
         */
        Attribute<EndpointConfig> WEB = Attribute.of("endpoint.web", () -> new EndpointConfig("lLS4p6skBbBVZX30zR5", -1));
        /**
         * PC 客户端配置
         */
        Attribute<EndpointConfig> PC = Attribute.of("endpoint.pc", () -> new EndpointConfig("GGp5Zc4NwUkdPvgka6M", -1));
        /**
         * 手机客户端配置
         */
        Attribute<EndpointConfig> PHONE = Attribute.of("endpoint.phone", () -> new EndpointConfig("Dul8CRGeVLcmi0yM8f7", -1));
        /**
         * 平板客户端配置
         */
        Attribute<EndpointConfig> PAD = Attribute.of("endpoint.pad", () -> new EndpointConfig("Jrsy8odZ0orSXkKXR2U", -1));

    }

    /**
     * 验证码配置
     */
    interface Captcha {
        /**
         * 是否禁用
         */
        Attribute<Boolean> ENABLED = Attribute.of("captcha.options.enabled", Boolean.FALSE);
        /**
         * 验证码是否大小写敏感
         */
        Attribute<Boolean> CASE_SENSITIVE = Attribute.of("captcha.case_sensitive", Boolean.FALSE);
        /**
         * 验证码 Cookie
         */
        Attribute<CookieManager> COOKIE = Attribute.of("captcha.cookie", () -> new CookieManager("X-Auth-Captcha"));
        /**
         * 验证码有效期
         */
        Attribute<Duration> TIMEOUT = Attribute.of("captcha.timeout", () -> Duration.ofMinutes(3));
    }

    /**
     * 密码配置
     */
    interface Password {
        /**
         * 最小长度
         */
        Attribute<Integer> MIN = Attribute.of("password.min", 6);
        /**
         * 最大长度
         */
        Attribute<Integer> MAX = Attribute.of("password.max", 16);
        /**
         * 大写字母数量
         */
        Attribute<Integer> UPPERCASE = Attribute.of("password.capital", 0);
        /**
         * 小写字母数量
         */
        Attribute<Integer> LOWERCASE = Attribute.of("password.lowercase", 0);
        /**
         * 数字数量
         */
        Attribute<Integer> NUMBER = Attribute.of("password.number", 0);
        /**
         * 字符数量
         */
        Attribute<Integer> SYMBOL = Attribute.of("password.symbol", 0);
        /**
         * 字符范围
         */
        Attribute<Set<Character>> SYMBOLS = Attribute.of("password.symbols", "\\!\"#$%&'()*+,-./:;<>=?@[]_^`{}~|".chars().mapToObj(it -> (char) it).collect(Collectors.toSet()));
    }

    /**
     * 中央认证服务（CAS）配置
     */
    interface Cas {
        /**
         * 是否启用
         */
        Attribute<Boolean> ENABLED = Attribute.of("cas.enabled", false);
        /**
         * 授权范围
         */
        Attribute<Set<Scope>> SCOPES = Attribute.of("cas.scopes", Set.of(Scope.BASIC));
        /**
         * 服务凭证（Service Ticket）有效期
         */
        Attribute<Duration> TIMEOUT = Attribute.of("cas.timeout", Duration.ofMinutes(10));
        /**
         * 是否启用单点退出
         */
        Attribute<Boolean> SINGLE_LOGOUT_ENABLED = Attribute.of("cas.single_logout", true);
    }

    /**
     * OAuth 2.0 配置
     */
    interface OAuth {
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
}
