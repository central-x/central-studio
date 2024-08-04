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

import central.bean.OptionalEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * CAS Error Code
 * <p>
 * Cas 错误码
 *
 * @author Alan Yeh
 * @since 2024/08/02
 */
@Getter
@RequiredArgsConstructor
public enum CasErrorCode implements OptionalEnum<HttpStatus> {

    INVALID_REQUEST("INVALID_REQUEST", HttpStatus.BAD_REQUEST),
    INVALID_TICKET_SPEC("INVALID_TICKET_SPEC", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_SERVICE_PROXY("UNAUTHORIZED_SERVICE_PROXY", HttpStatus.BAD_REQUEST),
    INVALID_PROXY_CALLBACK("INVALID_PROXY_CALLBACK", HttpStatus.BAD_REQUEST),
    INVALID_TICKET("INVALID_TICKET", HttpStatus.BAD_REQUEST),
    INVALID_SERVICE("INVALID_SERVICE", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE);

    private final String name;
    private final HttpStatus value;
}
