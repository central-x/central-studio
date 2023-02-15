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

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 密码配置
 *
 * @author Alan Yeh
 * @since 2023/02/15
 */
public interface PasswordAttributes {
    /**
     * 最小长度
     */
    Attribute<Integer> MIN = Attribute.of("password.min", 6);
    /**
     * 最大长度
     */
    Attribute<Integer> MAX = Attribute.of("password.max", 16);
    /**
     * 大写字母数量
     */
    Attribute<Integer> UPPERCASE = Attribute.of("password.capital", 0);
    /**
     * 小写字母数量
     */
    Attribute<Integer> LOWERCASE = Attribute.of("password.lowercase", 0);
    /**
     * 数字数量
     */
    Attribute<Integer> NUMBER = Attribute.of("password.number", 0);
    /**
     * 字符数量
     */
    Attribute<Integer> SYMBOL = Attribute.of("password.symbol", 0);
    /**
     * 字符范围
     */
    Attribute<Set<Character>> SYMBOLS = Attribute.of("password.symbols", "\\!\"#$%&'()*+,-./:;<>=?@[]_^`{}~|".chars().mapToObj(it -> (char) it).collect(Collectors.toSet()));

}
