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

package central.studio.storage.core.cache;

import central.io.Filex;
import central.io.IOStreamx;
import central.lang.Assertx;
import central.studio.storage.core.BucketCache;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 本地缓存
 *
 * @author Alan Yeh
 * @since 2022/11/02
 */
public class LocalCache implements BucketCache {
    private final Path path;

    public LocalCache(Path path) throws IOException{
        this.path = path;
        if (!Files.notExists(path)) {
            Files.createDirectories(this.path);
        }
    }

    @Override
    public void put(String key, InputStream data) throws IOException {
        var cache = new File(this.path.toFile(), key);
        Assertx.mustTrue(cache.createNewFile(), IOException::new, "缓存[{}]已存在", key);
        try (data; var output = Files.newOutputStream(cache.toPath(), StandardOpenOption.WRITE)) {
            IOStreamx.transfer(data, output);
        }
    }

    @Override
    public void set(String key, InputStream data) throws IOException {
        var cache = new File(this.path.toFile(), key);
        Filex.delete(cache);
        Assertx.mustTrue(cache.createNewFile(), IOException::new, "缓存[{}]创建失败", key);
        try (data; var output = Files.newOutputStream(cache.toPath(), StandardOpenOption.WRITE)) {
            IOStreamx.transfer(data, output);
        }
    }

    @Override
    public InputStream cache(String key, InputStream data) throws IOException {
        var cache = new File(this.path.toFile(), key);
        Assertx.mustTrue(cache.createNewFile(), IOException::new, "缓存[{}]已存在", key);

        var bufferedOutput = new BufferedOutputStream(Files.newOutputStream(cache.toPath(), StandardOpenOption.WRITE));
        var bufferedInput = new BufferedInputStream(data);
        return new InputStream() {
            @Override
            public int read() throws IOException {
                int b = bufferedInput.read();
                if (b >= 0) {
                    bufferedOutput.write(b);
                }
                return b;
            }

            @Override
            public void close() throws IOException {
                bufferedInput.close();
                bufferedOutput.flush();
                bufferedOutput.close();
                super.close();
            }
        };
    }

    @Override
    public boolean exists(String key) {
        return new File(this.path.toFile(), key).exists();
    }

    @Override
    public InputStream get(String key) throws IOException {
        var cache = new File(this.path.toFile(), key);
        if (!cache.exists()) {
            return null;
        } else {
            return Files.newInputStream(cache.toPath(), StandardOpenOption.READ);
        }
    }

    @Override
    public InputStream get(List<String> keys) throws IOException {
        var streams = new ArrayList<InputStream>(keys.size());
        for (var key : keys) {
            var stream = this.get(key);
            Assertx.mustNotNull(stream, FileNotFoundException::new, "缓存[{}]不存在", key);
            streams.add(stream);
        }
        return new SequenceInputStream(Collections.enumeration(streams));
    }

    @Override
    public void delete(String key) throws IOException {
        var cache = new File(this.path.toFile(), key);
        Filex.delete(cache);
    }

    @Override
    public void delete(List<String> keys) throws IOException {
        for (var key : keys) {
            this.delete(key);
        }
    }
}
