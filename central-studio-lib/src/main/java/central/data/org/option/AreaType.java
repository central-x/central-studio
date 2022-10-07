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

package central.data.org.option;

import central.bean.OptionalEnum;
import central.lang.Arrayx;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * 区划类型
 *
 * @author Alan Yeh
 * @since 2022/09/24
 */
@Getter
@AllArgsConstructor
public enum AreaType implements OptionalEnum<String> {

    COUNTRY("国家", "country", 0),
    PROVINCE("省/自治区/直辖市", "province", 1),
    CITY("市/州", "city", 2),
    DISTRICT("区/县/县级市", "district", 3),
    TOWN("街/镇/乡", "town", 4),
    VILLAGE("村/居", "village", 5);

    private final String name;
    private final String value;
    private final Integer priority;

    @Override
    public String toString() {
        return this.value;
    }

    @JsonCreator
    public static @Nullable AreaType resolve(String value) {
        return Arrayx.asStream(AreaType.values()).filter(it -> Objects.equals(it.getValue(), value)).findFirst().orElse(null);
    }
}
