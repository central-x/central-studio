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

package central.data.authority.option;

import central.bean.OptionalEnum;
import central.lang.Arrayx;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 授权范围类型
 *
 * @author Alan Yeh
 * @since 2022/09/28
 */
@Getter
@AllArgsConstructor
public enum RangeType implements OptionalEnum<String> {
    ACCOUNT("本人", "account"),
    ACCOUNT_DEPARTMENT("本部门", "account_department"),
    ACCOUNT_DEPARTMENT_RECURSION("本部门及子部门", "account_department_recursion"),
    ACCOUNT_UNIT("本单位", "account_unit"),
    ACCOUNT_UNIT_RECURSION("本单位及下级单位", "account_unit_recursion"),

    DEPARTMENT("部门", "department"),
    DEPARTMENT_RECURSION("部门（递归）", "department_recursion"),
    UNIT("单位", "unit"),
    UNIT_RECURSION("单位（递归）", "unit_recursion"),
    AREA("行政区划", "area"),
    AREA_RECURSION("行政区划（递归）", "area_recursion"),;

    private final String name;
    private final String value;

    @Override
    public String toString() {
        return this.value;
    }

    public static @Nullable RangeType resolve(String value) {
        return Arrayx.asStream(RangeType.values()).filter(it -> Objects.equals(it.getValue(), value)).findFirst().orElse(null);
    }
}
