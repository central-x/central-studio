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

package central.security.core.request;

import central.io.IOStreamx;
import central.lang.Arrayx;
import central.lang.Stringx;
import central.security.core.SecurityRequest;
import central.web.XForwardedHeaders;
import central.util.Convertx;
import central.util.Jsonx;
import central.util.Objectx;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

import java.beans.Introspector;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Request
 *
 * @author Alan Yeh
 * @since 2022/10/19
 */
@RequiredArgsConstructor
public abstract class Request implements SecurityRequest {

    @Getter
    private final HttpServletRequest request;

    @NotNull
    @Override
    public URI getUri() {
        var originUri = this.request.getHeader(XForwardedHeaders.ORIGIN_URI);
        if (originUri != null) {
            return URI.create(originUri);
        } else {
            return URI.create(this.request.getRequestURI());
        }
    }

    @Override
    public String getHeader(String name) {
        return this.request.getHeader(name);
    }

    @Override
    public MediaType getContentType() {
        var contentType = this.getHeader(HttpHeaders.CONTENT_TYPE);
        if (Stringx.isNullOrBlank(contentType)) {
            return null;
        }
        return MediaType.parseMediaType(contentType);
    }

    @Override
    public boolean isAcceptContentType(MediaType contentType) {
        var accepts = MediaType.parseMediaTypes(this.getHeader(HttpHeaders.ACCEPT));
        for (var accept : accepts) {
            if ("*".equals(accept.getType()) && "*".equals(accept.getSubtype())) {
                continue;
            }
            if (accept.isCompatibleWith(contentType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getTenantCode() {
        return this.getHeader(XForwardedHeaders.TENANT);
    }

    @Override
    public String getTenantPath() {
        return Objectx.getOrDefault(this.getHeader(XForwardedHeaders.PATH), "/");
    }

    @Override
    public String getCookie(String name) {
        return Arrayx.asStream(request.getCookies())
                .filter(it -> Objects.equals(name, it.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    @Override
    public String getParameter(String name) {
        return this.getRequest().getParameter(name);
    }

    @Override
    public String getParameterOrDefault(String name, String defaultValue) {
        return Objectx.getOrDefault(this.getParameter(name), defaultValue);
    }

    @Override
    public List<String> getParameters(String name) {
        var parameters = this.getRequest().getParameterValues(name);
        if (Arrayx.isNullOrEmpty(parameters)) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(parameters);
        }
    }

    @Override
    @SneakyThrows
    public <T> T getParameter(Class<T> type) {
        var info = Introspector.getBeanInfo(type);
        var properties = info.getPropertyDescriptors();

        var instance = type.getConstructor().newInstance();
        for (var property : properties) {
            var value = this.getParameter(property.getName());
            if (value != null) {
                if (!Convertx.Default().support(String.class, property.getPropertyType())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("参数[{}]需要 {} 类型", property.getName(), property.getPropertyType().getName()));
                }
                var castedValue = Convertx.Default().convert(value, property.getPropertyType());
                property.getWriteMethod().invoke(instance, castedValue);
            }
        }
        return instance;
    }

    @Override
    @SneakyThrows
    public <T> T getBody(Class<T> type) {
        String body = IOStreamx.readText(this.getRequest().getInputStream(), StandardCharsets.UTF_8);
        return Jsonx.Default().deserialize(body, type);
    }
}
