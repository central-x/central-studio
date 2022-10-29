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
import central.security.core.CookieManager;

import java.time.Duration;

/**
 * 交换属性
 *
 * @author Alan Yeh
 * @since 2022/10/19
 */
public interface ExchangeAttributes {
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 会话配置
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 会话超时时间
     */
    Attribute<Long> SESSION_TIMEOUT = Attribute.of(ExchangeAttributes.class.getSimpleName() + ".session.timeout", () -> Duration.ofMinutes(30).toMillis());
    /**
     * 会话颁发者
     */
    Attribute<String> SESSION_ISSUER = Attribute.of(ExchangeAttributes.class.getSimpleName() + ".session.issuer", "com.central-x.security");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 终端配置
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Web 端配置
     */
    Attribute<EndpointConfig> ENDPOINT_WEB = Attribute.of(ExchangeAttributes.class.getSimpleName() + ".endpoint.web", () -> new EndpointConfig("lLS4p6skBbBVZX30zR5", -1));
    /**
     * PC 客户端配置
     */
    Attribute<EndpointConfig> ENDPOINT_PC = Attribute.of(ExchangeAttributes.class.getSimpleName() + ".endpoint.pc", () -> new EndpointConfig("GGp5Zc4NwUkdPvgka6M", -1));
    /**
     * 手机客户端配置
     */
    Attribute<EndpointConfig> ENDPOINT_PHONE = Attribute.of(ExchangeAttributes.class.getSimpleName() + ".endpoint.phone", () -> new EndpointConfig("Dul8CRGeVLcmi0yM8f7", -1));
    /**
     * 平板客户端配置
     */
    Attribute<EndpointConfig> ENDPOINT_PAD = Attribute.of(ExchangeAttributes.class.getSimpleName() + ".endpoint.pad", () -> new EndpointConfig("Jrsy8odZ0orSXkKXR2U", -1));

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 登录选项配置
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    interface Captcha {
        interface Options {
            /**
             * 是否禁用
             */
            Attribute<Boolean> ENABLED = Attribute.of(ExchangeAttributes.class.getSimpleName() + ".captcha.options.enabled", Boolean.FALSE);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Cookie 配置
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    Attribute<CookieManager> COOKIE = Attribute.of(ExchangeAttributes.class.getSimpleName() + ".cookie", () -> new CookieManager("Authentication"));
}
