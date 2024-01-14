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

package central.studio.gateway.core.body.serializer;

import central.io.IOStreamx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;

/**
 * Deflate 压缩
 *
 * @author Alan Yeh
 * @since 2022/10/13
 */
public class DeflateSerializer implements BodySerializer {
    @Override
    public String read(InputStream body, Charset charset) throws IOException {
        return IOStreamx.readText(new DeflaterInputStream(body), charset);
    }

    @Override
    public byte[] write(String body, Charset charset) throws IOException {
        var stream = new ByteArrayOutputStream();
        var output = new DeflaterOutputStream(stream);
        output.write(body.getBytes(charset));
        output.flush();
        output.close();

        return stream.toByteArray();
    }
}
