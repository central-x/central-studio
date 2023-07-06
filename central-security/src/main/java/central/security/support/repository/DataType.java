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

package central.security.support.repository;

import central.bean.OptionalEnum;
import central.lang.Arrayx;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 数据类型
 *
 * @author Alan Yeh
 * @since 2023/06/10
 */
@Getter
@AllArgsConstructor
public enum DataType implements OptionalEnum<Integer> {
    NONE("不存在", 0),
    STRING("字符串", 1),
    LIST("列表", 2),
    QUEUE("队表", 4),
    SET("无序集合", 8),
    ZSET("有序集合", 16),
    MAP("键值对", 32);

    private final String name;
    private final int value;

    public static boolean isString(int value) {
        return (STRING.getValue() & value) != 0;
    }

    public static boolean isList(int value) {
        return (LIST.getValue() & value) != 0;
    }

    public static boolean isQueue(int value) {
        return (QUEUE.getValue() & value) != 0;
    }

    public static boolean isSet(int value) {
        return (SET.getValue() & value) != 0;
    }

    public static boolean isZSet(int value) {
        return (ZSET.getValue() & value) != 0;
    }

    public static boolean isMap(int value) {
        return (MAP.getValue() & value) != 0;
    }

    @JsonCreator
    public static @Nullable DataType resolve(String value) {
        return Arrayx.asStream(DataType.values()).filter(it -> Objects.equals(it.getValue(), value)).findFirst().orElse(null);
    }
}
