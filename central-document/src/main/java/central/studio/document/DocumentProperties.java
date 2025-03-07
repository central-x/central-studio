package central.studio.document;/*
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

import central.studio.document.core.LibraryType;
import central.studio.document.properties.GitLibraryProperties;
import central.studio.document.properties.InternalLibraryProperties;
import central.studio.document.properties.LocalLibraryProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/// 应用配置
///
/// @author Alan Yeh
@Data
@ConfigurationProperties("studio.document")
public class DocumentProperties {
    /// 是否启用文档库功能
    private boolean enabled = true;

    /// 文档库类型
    private LibraryType type = LibraryType.INTERNAL;

    /// Git 文档库配置
    private GitLibraryProperties git = new GitLibraryProperties();

    /// 内置文档库配置
    private InternalLibraryProperties internal = new InternalLibraryProperties();

    /// 本地文档库配置
    private LocalLibraryProperties local = new LocalLibraryProperties();
}
