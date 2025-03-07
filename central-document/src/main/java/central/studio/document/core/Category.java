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

import central.lang.Stringx;
import central.util.Listx;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/// Document Category
///
/// 文档分类，用于控制标题栏
///
/// @author Alan Yeh
@Data
@Builder
public class Category implements Serializable {
    @Serial
    private static final long serialVersionUID = -1477056702001231966L;

    /// 标识
    private String code;

    /// 名称
    private String name;

    /// 文档版本
    private List<Version> versions;

    /// 获取指定版本的文档
    ///
    /// @param code 版本标识
    public @Nonnull Version getVersion(@Nullable String code) {
        if (Stringx.isNullOrBlank(code)) {
            return Listx.getLast(this.getVersions())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Stringx.format("文档版本[{}]不存在", code)));
        }

        return this.getVersions().stream()
                .filter(it -> it.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Stringx.format("文档版本[{}]不存在", code)));
    }
}
