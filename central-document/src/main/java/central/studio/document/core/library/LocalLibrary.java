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

package central.studio.document.core.library;

import central.io.IOStreamx;
import central.lang.Stringx;
import central.lang.reflect.TypeRef;
import central.studio.document.DocumentProperties;
import central.studio.document.core.Library;
import central.util.Jsonx;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/// Local Library
///
/// 本地文档库
///
/// @author Alan Yeh
@Component
@ConditionalOnProperty(name = "studio.document.type", havingValue = "local")
public class LocalLibrary extends Library {

    @Setter(onMethod_ = @Autowired)
    private DocumentProperties properties;

    private Path getRealPath(Path relativePath) {
        return Path.of(properties.getLocal().getPath()).resolve(relativePath);
    }

    @Override
    @SneakyThrows(IOException.class)
    protected String getContent(Path path) {
        path = getRealPath(path);

        if (!Files.exists(path)) {
            throw new FileNotFoundException("文档元数据文件 metadata.json 不存在");
        }

        try (var inputStream = Files.newInputStream(path, StandardOpenOption.READ)) {
            return IOStreamx.readText(inputStream, StandardCharsets.UTF_8);
        }
    }

    @Override
    @SneakyThrows(IOException.class)
    public InputStream getAsset(Path path) {
        path = getRealPath(path);

        if (!Files.exists(path)) {
            throw new FileNotFoundException(Stringx.format("文件 {} 不存在", path.getFileName()));
        }

        return Files.newInputStream(path, StandardOpenOption.READ);
    }

    @Override
    @SneakyThrows(IOException.class)
    protected Map<String, Object> fetchMetadata(Path path) {
        path = getRealPath(path);

        if (!Files.exists(path)) {
            throw new FileNotFoundException("文档元数据文件 metadata.json 不存在");
        }
        try (var inputStream = Files.newInputStream(path, StandardOpenOption.READ)) {
            return Jsonx.Default().deserialize(inputStream, UTF_8, TypeRef.ofMap(String.class, Object.class));
        }
    }

    @Override
    @SneakyThrows(IOException.class)
    protected Map<String, Object> fetchLayout(Path path) {
        path = getRealPath(path);

        if (!Files.exists(path)) {
            throw new FileNotFoundException("文档布局文件 layout.json 不存在");
        }
        try (var inputStream = Files.newInputStream(path, StandardOpenOption.READ)) {
            return Jsonx.Default().deserialize(inputStream, UTF_8, TypeRef.ofMap(String.class, Object.class));
        }
    }
}
