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

package central.security.core.strategy.global;

import central.security.core.strategy.GlobalStrategy;
import central.security.core.strategy.StrategyChain;
import central.starter.webmvc.servlet.WebMvcRequest;
import central.starter.webmvc.servlet.WebMvcResponse;
import lombok.SneakyThrows;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Writing Response
 * 写响应
 *
 * @author Alan Yeh
 * @since 2022/10/19
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ResponseWritingStrategy implements GlobalStrategy {

    @Override
    @SneakyThrows
    public void execute(WebMvcRequest request, WebMvcResponse response, StrategyChain chain) {
        try {
            chain.execute(request, response);
        } catch (Throwable throwable) {
            if (throwable instanceof ResponseStatusException ex) {
                response.setStatus(ex.getStatusCode().value());
            } else {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
//            response.setBody(new ErrorBody(throwable));
        }
//        exchange.getResponse().write(exchange);
    }
}
