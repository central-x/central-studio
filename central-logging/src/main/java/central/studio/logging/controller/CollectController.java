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

package central.studio.logging.controller;

import central.io.IOStreamx;
import central.lang.Stringx;
import central.lang.reflect.TypeRef;
import central.studio.logging.controller.param.LogParams;
import central.studio.logging.core.collector.impl.http.HttpEvent;
import central.util.Jsonx;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

/**
 * HTTP 日志收集入口
 *
 * @author Alan Yeh
 * @since 2022/10/24
 */
@RestController
@RequestMapping("/api/collect")
public class CollectController {
    @Setter(onMethod_ = @Autowired)
    private ApplicationEventPublisher publisher;

    /**
     * 收集日志
     *
     * @param path Http 日志入口
     */
    @PostMapping("/{path}")
    public Map<String, String> collect(@PathVariable String path,
                                       HttpServletRequest request) throws IOException {
        String body;

        var contentEncoding = request.getHeader(HttpHeaders.CONTENT_ENCODING);
        if (Stringx.isNullOrBlank(contentEncoding)) {
            // 没有压缩
            try (var stream = request.getInputStream()) {
                body = IOStreamx.readText(stream, StandardCharsets.UTF_8);
            }
        } else if ("gzip".equalsIgnoreCase(contentEncoding)) {
            // Gzip 压缩
            try (var stream = new GZIPInputStream(request.getInputStream())) {
                body = IOStreamx.readText(stream, StandardCharsets.UTF_8);
            }
        } else if ("deflate".equalsIgnoreCase(contentEncoding)) {
            try (var stream = new DeflaterInputStream(request.getInputStream())) {
                body = IOStreamx.readText(stream, StandardCharsets.UTF_8);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("Unsupported content-encoding '{}'", contentEncoding));
        }

        if (Stringx.isNullOrEmpty(body)) {
            // 没有请求体
            return Map.of("message", "success");
        }

        var params = Jsonx.Default().deserialize(body, TypeRef.ofList(LogParams.class));
        if (params.isEmpty()) {
            return Map.of("message", "success");
        }

        // 异步处理日志
        this.publisher.publishEvent(new HttpEvent(path, params.stream().map(LogParams::toData).toList()));

        return Map.of("message", "success");
    }
}
