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

package central.security.core;

import central.lang.Stringx;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;

/**
 * Cookie Manager
 *
 * @author Alan Yeh
 * @since 2022/10/19
 */
@RequiredArgsConstructor
public class CookieManager {
    /**
     * name
     */
    private final String name;

    private final String domain;

    private final boolean httpOnly;

    private final boolean secure;

    public CookieManager(String name) {
        this(name, null, true, false);
    }

    /**
     * 设置 Cookie
     */
    public void set(SecurityExchange exchange, String value) {
        var cookie = ResponseCookie.from(this.name, value)
                .path(exchange.getRequest().getTenantPath())
                .maxAge(-1)
                .httpOnly(this.httpOnly)
                .secure(this.secure);
        if (Stringx.isNotBlank(domain)) {
            cookie.domain(this.domain);
        }
        exchange.getResponse().getCookies().add(cookie.build());
    }

    /**
     * 获取 Cookie
     */
    public String get(SecurityExchange exchange) {
        return exchange.getRequest().getCookie(this.name);
    }

    /**
     * 清除 Cookie
     */
    public void remove(SecurityExchange exchange) {
        var cookie = ResponseCookie.from(this.name, "deleteMe")
                .path(exchange.getRequest().getTenantPath())
                .maxAge(0)
                .httpOnly(this.httpOnly)
                .secure(this.secure);
        if (Stringx.isNotBlank(domain)) {
            cookie.domain(domain);
        }
        exchange.getResponse().getCookies().add(cookie.build());
    }
}
