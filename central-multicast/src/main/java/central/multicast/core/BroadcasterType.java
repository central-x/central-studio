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

package central.multicast.core;

import central.bean.OptionalEnum;
import central.multicast.core.impl.baidu.AndroidBroadcaster;
import central.multicast.core.impl.baidu.AppleBroadcaster;
import central.multicast.core.impl.email.SmtpBroadcaster;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 广播类型
 *
 * @author Alan Yeh
 * @since 2022/11/03
 */
@Getter
@AllArgsConstructor
public enum BroadcasterType implements OptionalEnum<String> {
    EMAIL_SMTP("邮件（SMTP）", "email_smtp", SmtpBroadcaster.class),
    BAIDU_APPLE("百度云推送（Apple）", "baidu_apple", AppleBroadcaster.class),
    BAIDU_ANDROID("百度云推送（Android）", "baidu_android", AndroidBroadcaster.class);

    private final String name;
    private final String value;
    private final Class<? extends Broadcaster> type;

    public static BroadcasterType resolve(String value) {
        return OptionalEnum.resolve(BroadcasterType.class, value);
    }
}
