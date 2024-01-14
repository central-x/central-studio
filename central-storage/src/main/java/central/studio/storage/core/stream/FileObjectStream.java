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

package central.studio.storage.core.stream;

import central.lang.Assertx;
import central.studio.storage.core.ObjectStream;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * 文件对象数据流
 *
 * @author Alan Yeh
 * @since 2022/10/30
 */
public class FileObjectStream extends ObjectStream {

    private final File file;

    public FileObjectStream(File file) {
        Assertx.mustTrue(file.exists(), "文件不存在: " + file.getAbsolutePath());
        this.file = file;
    }

    @NotNull
    @Override
    public String getFilename() {
        return file.getName();
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @NotNull
    @Override
    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(file.toPath(), StandardOpenOption.READ);
    }

    @Override
    public boolean isResumable() {
        return true;
    }
}
