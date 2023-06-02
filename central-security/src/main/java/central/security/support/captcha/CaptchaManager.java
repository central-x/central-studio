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

package central.security.support.captcha;

/**
 * 验证码管理器
 *
 * @author Alan Yeh
 * @since 2023/05/29
 */
public interface CaptchaManager {
    /**
     * 生成一个新的验证码
     *
     * @param tenantCode 租户
     * @param generator  生成器
     */
    Captcha generate(String tenantCode, CaptchaGenerator generator);

    /**
     * 验证验证码
     *
     * @param tenantCode 租户
     * @param key        二维码键
     * @param value      二维码值
     * @return 是否验证成功
     * @throws CaptchaException 验证失败时将抛出异常
     */
    void verify(String tenantCode, String key, String value) throws CaptchaException;
}
