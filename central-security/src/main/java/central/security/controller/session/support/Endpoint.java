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

package central.security.controller.session.support;

import central.bean.OptionalEnum;
import central.lang.Arrayx;
import central.lang.Attribute;
import central.security.core.SecurityExchange;
import central.security.core.attribute.ExchangeAttributes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * 终端类型
 *
 * @author Alan Yeh
 * @since 2022/10/20
 */
@Getter
@RequiredArgsConstructor
public enum Endpoint implements OptionalEnum<String> {

    WEB("网页端（Web）", "web", ExchangeAttributes.Endpoint.WEB),
    PC("电脑客户端（PC）", "pc", ExchangeAttributes.Endpoint.PC),
    PHONE("手机客户端（Phone）", "phone", ExchangeAttributes.Endpoint.PHONE),
    PAD("平板客户端（Pad）", "pad", ExchangeAttributes.Endpoint.PAD);

    private final String name;
    private final String value;
    private final Attribute<EndpointConfig> attribute;

    public static Endpoint resolve(String value) {
        return Arrayx.asStream(Endpoint.values()).filter(it -> Objects.equals(it.getValue(), value)).findFirst().orElse(null);
    }

    public static Endpoint resolve(SecurityExchange exchange, String secret) {
        return Arrayx.asStream(Endpoint.values()).filter(it -> Objects.equals(exchange.getRequiredAttribute(it.getAttribute()).getSecret(), secret)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
