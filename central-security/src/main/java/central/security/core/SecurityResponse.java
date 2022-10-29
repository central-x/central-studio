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

import central.security.core.body.StringBody;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Security Response
 * 安全相关响应
 *
 * @author Alan Yeh
 * @since 2022/10/19
 */
@RequiredArgsConstructor
public class SecurityResponse {
    /**
     * Return servlet response
     * <p>
     * 获取 Servlet 响应
     */
    @Getter
    private final HttpServletResponse response;

    public static SecurityResponse of(HttpServletResponse response) {
        return new SecurityResponse(response);
    }

    /**
     * 状态码
     */
    @Getter
    @Setter
    private @Nonnull HttpStatusCode status = HttpStatus.OK;

    /**
     * 响应头
     */
    @Getter
    private final HttpHeaders headers = new HttpHeaders();
    /**
     * Cookie
     */
    @Getter
    private final List<ResponseCookie> cookies = new ArrayList<>();

    /**
     * 响应体
     */
    @Getter
    @Setter
    private @Nonnull SecurityResponseBody body = new StringBody("");

    /**
     * 写响应
     */
    public void write(SecurityExchange exchange) throws IOException {
        // 写状态码
        this.getResponse().setStatus(this.getStatus().value());
        // 写响应头
        this.headers.addAll(this.body.getHeaders());
        for (var header : this.headers.entrySet()) {
            for (var value : header.getValue()) {
                this.getResponse().addHeader(header.getKey(), value);
            }
        }
        // 写 Cookie
        for (var cookie : this.getCookies()) {
            this.getResponse().addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
        // 写响应体
        try (var output = this.getResponse().getOutputStream()) {
            body.write(exchange);
            output.flush();
        }
    }
}
