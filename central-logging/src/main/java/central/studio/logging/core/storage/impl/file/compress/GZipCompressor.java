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

package central.studio.logging.core.storage.impl.file.compress;

import central.io.Filex;
import central.io.IOStreamx;
import central.studio.logging.core.storage.impl.file.Compressor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPOutputStream;

/**
 * GZip 压缩
 *
 * @author Alan Yeh
 * @since 2022/10/24
 */
public class GZipCompressor implements Compressor {
    @Override
    public void compress(File file) throws IOException {
        // 如果文件已经压缩过了，就不重复压缩了
        if (file.getName().contains(".log.") && !file.getName().endsWith(".log")) {
            return;
        }

        // 如果文件不存在，不压缩
        if (!file.exists()) {
            return;
        }

        var gz = new File(file.getParentFile(), file.getName() + ".gz");
        if (gz.exists()) {
            // 如果文件正在压缩中，不压缩
            return;
        }

        if (!gz.createNewFile()) {
            // 创建文件失败，不压缩
            return;
        }

        try (var input = IOStreamx.buffered(Files.newInputStream(file.toPath(), StandardOpenOption.READ));
             var output = IOStreamx.buffered(new GZIPOutputStream(Files.newOutputStream(gz.toPath(), StandardOpenOption.WRITE)))) {
            IOStreamx.transfer(input, output);
            // 删除原来的文件
            Filex.delete(file);
        } catch (IOException ex) {
            // 压缩失败了
            Filex.delete(gz);
            throw ex;
        }
    }
}
