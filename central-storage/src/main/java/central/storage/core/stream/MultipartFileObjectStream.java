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

package central.storage.core.stream;

import central.storage.core.ObjectStream;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * MultipartFile 对象流
 *
 * @author Alan Yeh
 * @since 2022/11/01
 */
public class MultipartFileObjectStream extends ObjectStream {
    @Getter
    private final String filename;

    @Getter
    private final long size;

    private final MultipartFile file;

    @NotNull
    @Override
    public InputStream getInputStream() throws IOException {
        return file.getInputStream();
    }

    public MultipartFileObjectStream(MultipartFile file) {
        this(file, file.getOriginalFilename());
    }

    public MultipartFileObjectStream(MultipartFile file, String filename) {
        this.file = file;
        this.filename = filename;
        this.size = file.getSize();
    }

    @Override
    public boolean isResumable() {
        return true;
    }
}
