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

package central.studio.identity.core.strategy;

import central.bean.OptionalEnum;
import central.identity.core.strategy.dynamic.*;
import central.studio.identity.core.strategy.dynamic.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 策略类型
 *
 * @author Alan Yeh
 * @since 2022/11/05
 */
@Getter
@AllArgsConstructor
public enum StrategyType implements OptionalEnum<String> {
    CAPTCHA("验证码策略（Captcha）", "captcha", CaptchaStrategyFilter.class),
    PASSWORD("密码策略（Password）", "password", PasswordStrategyFilter.class),
    SESSION("会话策略（Session）", "session", SessionStrategyFilter.class),
    SESSION_COOKIE("会话 Cookie（Session Cookie）", "session_cookie", SessionCookieStrategyFilter.class),
    CAS("中央认证服务策略（CAS）", "cas", CasStrategyFilter.class),
    OAUTH("OAuth 2.0（OAuth）", "oauth", OAuthStrategyFilter.class);

    private final String name;
    private final String value;
    private final Class<? extends StrategyFilter> type;

    public static StrategyType resolve(String value) {
        return OptionalEnum.resolve(StrategyType.class, value);
    }
}
