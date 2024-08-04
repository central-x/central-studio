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

package central.studio.identity.controller.sso.cas.exception;

import central.lang.Stringx;
import central.starter.webmvc.exception.ExceptionHandler;
import central.starter.webmvc.view.JsonView;
import central.starter.webmvc.view.TextView;
import central.starter.webmvc.view.XmlView;
import central.util.Mapx;
import central.util.Objectx;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import java.time.OffsetDateTime;

/**
 * CAS 异常补捕
 *
 * @author Alan Yeh
 * @since 2024/08/02
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CasExceptionHandler implements ExceptionHandler {

    @Override
    public boolean support(Throwable throwable) {
        return throwable instanceof CasException;
    }

    @Nullable
    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Throwable throwable) {
        var exception = (CasException) throwable;

        var mv = new ModelAndView();
        mv.setStatus(exception.getErrorCode().getValue());

        var accepts = MediaType.parseMediaTypes(Objectx.getOrDefault(request.getHeader(HttpHeaders.ACCEPT), MediaType.ALL_VALUE));
        if (accepts.stream().anyMatch(MediaType.APPLICATION_JSON::includes)) {
            // 客户端要求返回 JSON
            mv.setView(new JsonView(Mapx.of(
                    Mapx.entry("code", exception.getErrorCode().getName()),
                    Mapx.entry("description", exception.getLocalizedMessage())
            )));
        } else if (accepts.stream().anyMatch(MediaType.APPLICATION_XML::includes) || accepts.stream().anyMatch(MediaType.TEXT_XML::includes)) {
            // 客户端要求返回 XML
            var content = """
                    <cas:serviceResponse xmlns:cas="http://www.yale.edu/tp/cas">
                        <cas:authenticationFailure code="{}">{}</cas:authenticationFailure>
                    </cas:serviceResponse>
                    """;
            mv.setView(new XmlView(Stringx.format(content, exception.getErrorCode().getName(), exception.getLocalizedMessage())));
        } else {
            // 客户端没有指定，则返回 HTML
            var content = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Central Identity</title></head><body><h2>{} ({})</h2><p>{}</p><div id='created'>{}</div></body></html>";
            mv.setView(new TextView(Stringx.format(content, exception.getErrorCode().getName(), exception.getErrorCode().getValue(), exception.getLocalizedMessage(), OffsetDateTime.now().toString())));
        }

        return mv;
    }
}
