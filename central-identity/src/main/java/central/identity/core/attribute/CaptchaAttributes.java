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

package central.identity.core.attribute;

import central.lang.Attribute;
import central.identity.core.CookieManager;
import central.identity.core.captcha.CaptchaGenerator;
import central.identity.core.captcha.generator.random.RandomGenerator;

import java.time.Duration;

/**
 * 验证码配置
 *
 * @author Alan Yeh
 * @since 2023/02/15
 */
public interface CaptchaAttributes {
    /**
     * 是否禁用
     */
    Attribute<Boolean> ENABLED = Attribute.of("captcha.enabled", Boolean.FALSE);
    /**
     * 验证码是否大小写敏感
     */
    Attribute<Boolean> CASE_SENSITIVE = Attribute.of("captcha.case_sensitive", Boolean.FALSE);
    /**
     * 验证码 Cookie
     */
    Attribute<CookieManager> COOKIE = Attribute.of("captcha.cookie", () -> new CookieManager("X-Auth-Captcha"));
    /**
     * 验证码有效期
     */
    Attribute<Duration> TIMEOUT = Attribute.of("captcha.timeout", () -> Duration.ofMinutes(3));
    /**
     * 验证码生成器
     */
    Attribute<CaptchaGenerator> GENERATOR = Attribute.of("captcha.generator", RandomGenerator::new);
}
