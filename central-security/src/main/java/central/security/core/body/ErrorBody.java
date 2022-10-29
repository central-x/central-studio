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

package central.security.core.body;

import central.io.IOStreamx;
import central.lang.Stringx;
import central.security.core.SecurityExchange;
import central.security.core.SecurityResponseBody;
import central.util.Jsonx;
import central.util.Mapx;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 异常响应体
 *
 * @author Alan Yeh
 * @since 2022/10/19
 */
public class ErrorBody implements SecurityResponseBody {

    @Getter
    private final HttpHeaders headers = new HttpHeaders();

    private String body;

    private ErrorBody() {
        headers.setPragma("no-cache");
        headers.setCacheControl("no-cache");
        headers.setExpires(0);
    }

    public ErrorBody(Throwable throwable) {
        this();
        if (throwable instanceof ResponseStatusException ex) {
            this.body = ex.getReason();
        } else {
            this.body = throwable.getMessage();
        }
    }

    public ErrorBody(String message) {
        this();
        this.body = message;
    }

    @Override
    public void write(SecurityExchange exchange) throws IOException {
        InputStream stream;
        if (exchange.getRequest().isAcceptContentType(MediaType.APPLICATION_JSON)) {
            exchange.getResponse().getResponse().addHeader(HttpHeaders.CONTENT_TYPE, new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8).toString());
            stream = new ByteArrayInputStream(Jsonx.Default().serialize(Mapx.newHashMap("message", this.body)).getBytes(StandardCharsets.UTF_8));
        } else {
            exchange.getResponse().getResponse().addHeader(HttpHeaders.CONTENT_TYPE, new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8).toString());

            String reason;
            int code = exchange.getResponse().getStatus().value();
            if (exchange.getResponse().getStatus() instanceof HttpStatus s) {
                reason = s.getReasonPhrase();
            } else {
                reason = "Unknown";
            }

            String body = Stringx.format("<html><body><h2>{} ({})</h2><p>{}</p><div id='created'>{}</div></body></html>", reason, code, this.body, OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));

            stream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
        }

        IOStreamx.transfer(stream, exchange.getResponse().getResponse().getOutputStream());
    }
}
