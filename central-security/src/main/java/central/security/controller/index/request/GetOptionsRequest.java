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

package central.security.controller.index.request;

import central.bean.OptionalEnum;
import central.security.controller.index.IndexController;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.core.attribute.CaptchaAttributes;
import central.security.core.attribute.EndpointAttributes;
import central.security.core.attribute.PasswordAttributes;
import central.security.core.body.JsonBody;
import central.security.core.request.Request;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 获取登录选项
 *
 * @author Alan Yeh
 * @see IndexController#getOptions
 * @since 2022/10/19
 */
public class GetOptionsRequest extends Request {
    public GetOptionsRequest(HttpServletRequest request) {
        super(request);
    }

    public static GetOptionsRequest of(HttpServletRequest request) {
        return new GetOptionsRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new Action();
    }

    private static class Action extends SecurityAction {

        @Override
        public void execute(SecurityExchange exchange) {
            var result = new HashMap<String, Map<String, Object>>();

            for (var option : Options.values()) {
                var parts = option.getName().split("[.]");
                result.computeIfAbsent(parts[0], key -> new HashMap<>())
                        .put(parts[1], option.getValue().apply(exchange));
            }

            exchange.getResponse().setBody(new JsonBody(result));
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum Options implements OptionalEnum<Function<?, ?>> {
        // 终端密钥配置
        ENDPOINT_WEB("endpoint.web", exchange -> exchange.getRequiredAttribute(EndpointAttributes.WEB).getSecret()),
        ENDPOINT_PC("endpoint.pc", exchange -> exchange.getRequiredAttribute(EndpointAttributes.PC).getSecret()),
        ENDPOINT_PHONE("endpoint.phone", exchange -> exchange.getRequiredAttribute(EndpointAttributes.PHONE).getSecret()),
        ENDPOINT_PAD("endpoint.pad", exchange -> exchange.getRequiredAttribute(EndpointAttributes.PAD).getSecret()),

        // 验证码配置
        CAPTCHA("captcha.enabled", exchange -> exchange.getRequiredAttribute(CaptchaAttributes.ENABLED)),
        CAPTCHA_TIMEOUT("captcha.timeout", exchange -> exchange.getRequiredAttribute(CaptchaAttributes.TIMEOUT).toMillis()),

        // 密码配置
        PASSWORD_MIN("password.min", exchange -> exchange.getRequiredAttribute(PasswordAttributes.MIN)),
        PASSWORD_MAX("password.max", exchange -> exchange.getRequiredAttribute(PasswordAttributes.MAX)),
        PASSWORD_UPPERCASE("password.uppercase", exchange -> exchange.getRequiredAttribute(PasswordAttributes.UPPERCASE)),
        PASSWORD_LOWERCASE("password.lowercase", exchange -> exchange.getRequiredAttribute(PasswordAttributes.LOWERCASE)),
        PASSWORD_NUMBER("password.number", exchange -> exchange.getRequiredAttribute(PasswordAttributes.NUMBER)),
        PASSWORD_SYMBOL("password.symbol", exchange -> exchange.getRequiredAttribute(PasswordAttributes.SYMBOL)),
        PASSWORD_SYMBOLS("password.symbols", exchange -> exchange.getRequiredAttribute(PasswordAttributes.SYMBOLS).stream().map(Object::toString).collect(Collectors.joining()));

        private final String name;
        private final Function<SecurityExchange, Object> value;
    }
}
