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

package central.identity.core;

import central.lang.Arrayx;
import central.lang.Stringx;
import central.starter.webmvc.servlet.WebMvcRequest;
import central.starter.webmvc.servlet.WebMvcResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.util.Objects;

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
    public void set(WebMvcRequest request, WebMvcResponse response, String value) {
        var builder = ResponseCookie.from(this.name, value)
                .path(request.getTenantPath())
                .maxAge(-1)
                .httpOnly(this.httpOnly)
                .secure(this.secure);
        if (Stringx.isNotBlank(this.domain)) {
            builder.domain(this.domain);
        }

        var cookie = builder.build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * 获取 Cookie
     */
    public String get(WebMvcRequest request, WebMvcResponse response) {
        return request.getCookie(this.name);
    }

    /**
     * 获取 Cookie
     */
    public String get(HttpServletRequest request) {
        return Arrayx.asStream(request.getCookies())
                .filter(it -> Objects.equals(this.name, it.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    /**
     * 删除客户端的 Cookie
     */
    public void remove(WebMvcRequest request, WebMvcResponse response) {
        var builder = ResponseCookie.from(this.name, "deleteMe")
                .path(request.getTenantPath())
                .maxAge(0)
                .httpOnly(this.httpOnly)
                .secure(this.secure);
        if (Stringx.isNotBlank(this.domain)) {
            builder.domain(this.domain);
        }

        var cookie = builder.build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
