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

package central.security.controller.sso.cas.option;

import central.bean.OptionalEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 响应格式
 *
 * @author Alan Yeh
 * @since 2022/11/07
 */
@Getter
@RequiredArgsConstructor
public enum Format implements OptionalEnum<String> {

    JSON("JSON", "json"),
    XML("XML", "xml");

    private final String name;
    private final String value;

    @Override
    public boolean isCompatibleWith(Object value) {
        if (value instanceof String string) {
            return this.getValue().equalsIgnoreCase(string);
        } else {
            return OptionalEnum.super.isCompatibleWith(value);
        }
    }

    public static Format resolve(String value) {
        return OptionalEnum.resolve(Format.class, value);
    }
}
