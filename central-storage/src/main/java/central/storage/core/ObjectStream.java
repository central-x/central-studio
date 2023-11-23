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

package central.storage.core;

import central.io.IOStreamx;
import central.lang.Stringx;
import central.security.Digestx;
import jakarta.annotation.Nonnull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * 对象流
 *
 * @author Alan Yeh
 * @since 2022/10/30
 */
public abstract class ObjectStream {

    /**
     * 文件名
     */
    public abstract @Nonnull String getFilename();

    /**
     * 扩展名
     */
    public @Nonnull String getExtension() {
        return this.getFilename().substring(this.getFilename().lastIndexOf(".") + 1);
    }

    /**
     * 对象大小
     */
    public abstract long getSize();

    /**
     * 是否可以重用
     */
    public boolean isResumable() {
        return false;
    }

    protected volatile String digest;

    /**
     * 文件摘要(sha256)
     */
    public String getDigest() throws IOException {
        if (Stringx.isNotBlank(digest)) {
            return this.digest;
        }
        digest = Digestx.SHA256.digest(this.getInputStream());
        return digest;
    }

    /**
     * 获取流
     * 每次获取 InputStream 时，都是重新创建的
     */
    public abstract @Nonnull InputStream getInputStream() throws IOException;

    /**
     * 获取字节码
     */
    public byte[] getBytes() throws IOException {
        return IOStreamx.readBytes(this.getInputStream());
    }

    /**
     * 将对象数据写入指定的输出流（写完后没有关闭输出流）
     *
     * @param output 输出流
     */
    public void transferTo(OutputStream output) throws IOException {
        try (var input = this.getInputStream()) {
            IOStreamx.transfer(input, output);
        }
    }

    /**
     * 将对象数据写入到指定的文修护
     *
     * @param file 指定文件
     */
    public void transferTo(File file) throws IOException {
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new IOException("无法创建目录: " + file.getParentFile().getAbsolutePath());
            }
        }
        if (file.createNewFile()) {
            this.transferTo(Files.newOutputStream(file.toPath(), StandardOpenOption.WRITE));
        }
    }
}
