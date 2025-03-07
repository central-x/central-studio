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

import central.lang.Assertx;
import central.lang.Stringx;
import central.util.Listx;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

/// Document Library
///
/// 文档库
///
/// @author Alan Yeh
@Slf4j
public abstract class Library {
    private Metadata metadata;

    /// 获取文档元信息
    ///
    /// 主要用于控制文档库的图标、标题、版本号、角标、分类
    public Metadata getMetadata() {
        if (this.metadata == null) {
            this.metadata = this.buildMetadata();
        }
        return this.metadata;
    }

    /// 获取文章内容
    ///
    /// @param categoryCode 文档分类
    /// @param versionCode  版本号
    /// @param articleCode  文章
    /// @return 文章内容
    public String getContent(String categoryCode, String versionCode, String articleCode) throws IOException {
        var category = this.getMetadata().getCategory(categoryCode);
        var version = category.getVersion(versionCode);

        var path = Path.of(category.getName()).resolve(version.getCode()).resolve(version.getArticle(articleCode));
        if ("default".equals(version.getCode())) {
            path = Path.of(category.getName()).resolve(version.getArticle(articleCode));
            ;
        }

        return this.getContent(path);
    }

    /// 获取文档库资源
    ///
    /// @param categoryCode 分类标识
    /// @param versionCode  版本标识
    /// @param articleCode  文档标识
    /// @param name         资源名称
    public InputStream getAsset(String categoryCode, String versionCode, String articleCode, String name) throws IOException {
        var path = Path.of("assets").resolve(categoryCode).resolve(articleCode);
        if (Stringx.isNotBlank(versionCode) && !"default".equals(versionCode)) {
            path = Path.of("assets").resolve(categoryCode).resolve(versionCode).resolve(articleCode);
        }

        return this.getAsset(path.resolve(name));
    }

    /// 获取文档内容
    protected abstract String getContent(Path path);

    /// 获取文档资源
    public abstract InputStream getAsset(Path path);

    /// 获取元数据
    protected abstract Map<String, Object> fetchMetadata(Path path);

    /// 获取文档库布局文件
    protected abstract Map<String, Object> fetchLayout(Path path);

    /// 处理元数据
    @SuppressWarnings("unchecked")
    protected Metadata buildMetadata() {
        var metadataInfo = this.fetchMetadata(Path.of("metadata.json"));
        return Metadata.builder()
                .title(Optional.ofNullable(metadataInfo.get("title")).orElse("Central Studio").toString())
                .vendor(Optional.ofNullable(metadataInfo.get("vendor")).orElse("CentralX").toString())
                .version(Optional.ofNullable(metadataInfo.get("version")).orElse("SNAPSHOT").toString())
                .categories(this.buildCategories((Map<String, Object>) metadataInfo.get("categories")))
                .build();
    }

    protected List<Category> buildCategories(Map<String, Object> categoryInfo) {
        if (categoryInfo == null) {
            throw new IllegalArgumentException("文档库元信息 categories 不能为空");
        }

        return categoryInfo.entrySet().stream()
                .map(entry -> {
                    // 分类名称
                    var name = entry.getKey();
                    // 分类访问路径
                    var path = Path.of(name);
                    // 分类标识
                    String code;
                    // 分类版本号
                    var versions = List.of("default");

                    // 为了方便编写 metadata.json，支持更多种灵活的写法
                    // 如果 value 是字符串，则直接将 value 当文档标识
                    // 如果 value 是对象，则需要提供 code 作为文档标识
                    if (entry.getValue() instanceof String value) {
                        code = value;
                    } else if (entry.getValue() instanceof Map<?, ?> value) {
                        code = Optional.ofNullable((String) value.get("code")).orElseThrow(() -> new IllegalArgumentException("文档库元信息 categories 配置错误：code 必须不为空"));
                        versions = Optional.ofNullable((List<String>) value.get("versions")).orElse(versions);
                    } else {
                        throw new IllegalArgumentException("文档库元信息 categories 配置错误：无法解析文档标识 code");
                    }

                    return Category.builder()
                            .code(code)
                            .name(name)
                            .versions(versions.stream().map(version -> {
                                // 解析分类的每个版本的布局信息
                                var layoutFile = path.resolve(version).resolve("layout.json");
                                if ("default".equals(version)) {
                                    layoutFile = path.resolve("layout.json");
                                }
                                var layouts = this.buildLayout(layoutFile);
                                var mapping = new HashMap<String, Path>();
                                this.buildMapping(Path.of(""), layouts, mapping);

                                return Version.builder().code(version).layout(layouts).mapping(mapping).build();
                            }).toList())
                            .build();
                }).toList();
    }

    protected List<Layout> buildLayout(Path path) {
        var layout = this.fetchLayout(path);
        return this.buildLayout(layout);
    }

    private List<Layout> buildLayout(Map<String, Object> layout) {
        if (layout == null) {
            return Collections.emptyList();
        } else {
            return layout.entrySet().stream()
                    .map(entry -> {
                        var type = entry.getValue() instanceof String ? LayoutType.resolve(entry.getValue().toString()) : LayoutType.DIRECTORY;
                        var name = entry.getKey();
                        var displayName = type == LayoutType.DIRECTORY ? name : name.substring(0, name.lastIndexOf('.'));
                        var code = entry.getValue() instanceof String value ? value : null;
                        List<Layout> children = entry.getValue() instanceof Map<?, ?> value ? this.buildLayout((Map<String, Object>) value) : Collections.emptyList();
                        return Layout.builder()
                                .code(code)
                                .name(name)
                                .displayName(displayName)
                                .type(type)
                                .children(children)
                                .build();
                    }).toList();
        }
    }

    private void buildMapping(Path parent, List<Layout> layouts, Map<String, Path> mapping) {
        if (Listx.isNullOrEmpty(layouts)) {
            return;
        }

        for (var layout : layouts) {
            var path = parent.resolve(layout.getName());

            if (LayoutType.DIRECTORY == layout.getType()) {
                buildMapping(path, layout.getChildren(), mapping);
            } else {
                var exists = mapping.put(layout.getCode(), path);
                Assertx.mustNull(exists, "文档库元信息 categories 配置错误，重复的文档标识 code：{}", layout.getCode());
            }
        }
    }
}
