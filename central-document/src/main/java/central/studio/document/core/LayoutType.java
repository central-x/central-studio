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

package central.studio.document.core;

import central.bean.OptionalEnum;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/// Document Layout Type
///
/// 布局类型
///
/// @author Alan Yeh
@Getter
@RequiredArgsConstructor
public enum LayoutType implements OptionalEnum<List<String>> {
    /// 目录类型
    DIRECTORY("directory", Collections.emptyList()),
    /// Markdown 文件类型
    MARKDOWN("markdown", List.of("md", "markdown")),
    /// HTML 文件类型
    HTML("html", List.of("html", "htm"));

    private final String name;
    private final List<String> value;

    public static @Nullable LayoutType resolve(String value) {
        return Arrays.stream(LayoutType.values()).filter(it -> it.getValue().contains(value.toLowerCase())).findFirst().orElse(null);
    }
}
