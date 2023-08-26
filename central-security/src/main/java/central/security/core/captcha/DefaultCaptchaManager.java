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

package central.security.core.captcha;

import central.lang.Assertx;
import central.lang.Stringx;
import central.util.Guidx;
import central.util.cache.CacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 验证码管理器
 *
 * @author Alan Yeh
 * @since 2023/05/29
 */
@Component
@RequiredArgsConstructor
public class DefaultCaptchaManager implements CaptchaManager {

    private final CacheRepository repository;

    @Override
    public Captcha generate(String tenantCode, CaptchaGenerator generator) {
        var captcha = generator.generator(Guidx.nextID());

        // 保存到缓存中
        var cache = this.repository.opsValue(Stringx.format("{}:{}", tenantCode, captcha.getCode()));
        cache.set(captcha.getValue(), Duration.ofMinutes(3));

        return captcha;
    }

    @Override
    public void verify(String tenantCode, String code, String value, boolean caseSensitive) throws CaptchaException {
        Assertx.mustNotBlank(value, "验证码不能必为");

        var cache = this.repository.opsValue(Stringx.format("{}:{}", tenantCode, code));
        var captcha = cache.getValue();

        Assertx.mustNotNull(captcha, "验证码已过期");
        if (caseSensitive) {
            Assertx.mustTrue(value.equals(captcha), "验证码错误");
        } else {
            Assertx.mustTrue(value.equalsIgnoreCase(captcha), "验证码错误");
        }
    }
}
