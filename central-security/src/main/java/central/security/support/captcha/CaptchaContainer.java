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

import java.time.Duration;

/**
 * 验证码容器
 *
 * @author Alan Yeh
 * @since 2022/11/06
 */
public interface CaptchaContainer {
    /**
     * 保存验证码
     *
     * @param tenantCode 租户
     * @param code       标识
     * @param value      值
     * @param expires    验证码过期时间
     * @return 验证码键
     */
    String put(String tenantCode, String code, String value, Duration expires);

    /**
     * 获取验证码
     * <p>
     * 获取后该验证码将失效
     *
     * @param tenantCode 租户
     * @param code       标识
     * @return 验证码
     */
    Captcha get(String tenantCode, String code);
}
