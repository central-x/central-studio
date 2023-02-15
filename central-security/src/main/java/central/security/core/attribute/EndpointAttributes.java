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

package central.security.core.attribute;

import central.lang.Attribute;
import central.security.controller.session.support.EndpointConfig;

/**
 * 终端配置
 *
 * @author Alan Yeh
 * @since 2023/02/15
 */
public interface EndpointAttributes {
    /**
     * Web 端配置
     */
    Attribute<EndpointConfig> WEB = Attribute.of("endpoint.web", () -> new EndpointConfig("lLS4p6skBbBVZX30zR5", -1));
    /**
     * PC 客户端配置
     */
    Attribute<EndpointConfig> PC = Attribute.of("endpoint.pc", () -> new EndpointConfig("GGp5Zc4NwUkdPvgka6M", -1));
    /**
     * 手机客户端配置
     */
    Attribute<EndpointConfig> PHONE = Attribute.of("endpoint.phone", () -> new EndpointConfig("Dul8CRGeVLcmi0yM8f7", -1));
    /**
     * 平板客户端配置
     */
    Attribute<EndpointConfig> PAD = Attribute.of("endpoint.pad", () -> new EndpointConfig("Jrsy8odZ0orSXkKXR2U", -1));

}
