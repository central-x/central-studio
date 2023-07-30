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

package central.security.support.captcha.generator.random;

import central.security.support.captcha.Captcha;
import central.security.support.captcha.CaptchaGenerator;

import java.util.Random;

/**
 * 随机验证码生成器
 *
 * @author Alan Yeh
 * @since 2023/05/29
 */
public class RandomGenerator implements CaptchaGenerator {

    // 验证码随机字符数组
    protected static final char[] CHAR_RANGE = "3456789ABCDEFGHJKMNPQRSTUVWXYabcdefghjkmnpqrstuvwxy".toCharArray();
    protected static final Random RANDOM = new Random(System.nanoTime());

    @Override
    public Captcha generator(String code) {
        int num = 4;
        char[] randomChars = new char[num];
        for (int i = 0; i < randomChars.length; i++) {
            randomChars[i] = CHAR_RANGE[RANDOM.nextInt(CHAR_RANGE.length)];
        }

        return new RandomCaptcha(code, String.valueOf(randomChars));
    }
}
