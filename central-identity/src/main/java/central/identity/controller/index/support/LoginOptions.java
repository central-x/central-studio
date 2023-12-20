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

package central.identity.controller.index.support;

import central.bean.OptionalEnum;
import central.identity.core.attribute.CaptchaAttributes;
import central.identity.core.attribute.EndpointAttributes;
import central.identity.core.attribute.PasswordAttributes;
import central.starter.webmvc.servlet.WebMvcRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 登录选项
 *
 * @author Alan Yeh
 * @since 2023/03/11
 */
@Getter
@RequiredArgsConstructor
public enum LoginOptions implements OptionalEnum<Function<?, ?>> {
    // 终端密钥配置
    ENDPOINT_WEB("endpoint.web", request -> request.getRequiredAttribute(EndpointAttributes.WEB).getSecret()),
    ENDPOINT_PC("endpoint.pc", request -> request.getRequiredAttribute(EndpointAttributes.PC).getSecret()),
    ENDPOINT_PHONE("endpoint.phone", request -> request.getRequiredAttribute(EndpointAttributes.PHONE).getSecret()),
    ENDPOINT_PAD("endpoint.pad", request -> request.getRequiredAttribute(EndpointAttributes.PAD).getSecret()),

    // 验证码配置
    CAPTCHA("captcha.enabled", request -> request.getRequiredAttribute(CaptchaAttributes.ENABLED)),
    CAPTCHA_TIMEOUT("captcha.timeout", request -> request.getRequiredAttribute(CaptchaAttributes.TIMEOUT).toMillis()),

    // 密码配置
    PASSWORD_MIN("password.min", request -> request.getRequiredAttribute(PasswordAttributes.MIN)),
    PASSWORD_MAX("password.max", request -> request.getRequiredAttribute(PasswordAttributes.MAX)),
    PASSWORD_UPPERCASE("password.uppercase", request -> request.getRequiredAttribute(PasswordAttributes.UPPERCASE)),
    PASSWORD_LOWERCASE("password.lowercase", request -> request.getRequiredAttribute(PasswordAttributes.LOWERCASE)),
    PASSWORD_NUMBER("password.number", request -> request.getRequiredAttribute(PasswordAttributes.NUMBER)),
    PASSWORD_SYMBOL("password.symbol", request -> request.getRequiredAttribute(PasswordAttributes.SYMBOL)),
    PASSWORD_SYMBOLS("password.symbols", request -> request.getRequiredAttribute(PasswordAttributes.SYMBOLS).stream().map(Object::toString).collect(Collectors.joining()));

    private final String name;
    private final Function<WebMvcRequest, Object> value;
}