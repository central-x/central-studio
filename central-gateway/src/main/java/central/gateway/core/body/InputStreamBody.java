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

package central.gateway.core.body;

import central.io.IOStreamx;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

import java.io.InputStream;

/**
 * InputStream Body
 *
 * @author Alan Yeh
 * @since 2022/10/18
 */
public class InputStreamBody implements HttpResponseBody {

    private final InputStream body;

    @Getter
    private final HttpHeaders headers = new HttpHeaders();

    public InputStreamBody(InputStream body, HttpHeaders headers) {
        this.body = body;
        this.headers.addAll(headers);
    }

    @SneakyThrows
    public InputStreamBody(InputStream body, MediaType contentType) {
        this.body = body;
        this.headers.setContentType(contentType);
        this.headers.setContentLength(body.available());
    }

    @NotNull
    @Override
    public Flux<DataBuffer> get(DataBufferFactory bufferFactory) {
        return DataBufferUtils.readInputStream(() -> body, bufferFactory, IOStreamx.BUFFER_SIZE);
    }

    @Override
    @SneakyThrows
    public void dispose() {
        this.body.close();
    }
}
