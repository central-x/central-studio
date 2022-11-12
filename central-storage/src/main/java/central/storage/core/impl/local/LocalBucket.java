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

package central.storage.core.impl.local;

import central.io.Filex;
import central.lang.Stringx;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.storage.core.ObjectStream;
import central.storage.core.Bucket;
import central.storage.core.stream.FileObjectStream;
import central.util.Guidx;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 本地磁盘存储
 *
 * @author Alan Yeh
 * @since 2022/10/30
 */
public class LocalBucket implements Bucket, InitializingBean {

    @Control(label = "说明", type = ControlType.LABEL, required = false, defaultValue = "将对象保存到本地磁盘中。")
    private String label;

    @Control(label = "保存位置", comment = "文件保存路径，可以使用相对路径")
    private String location;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 创建目录
        var dir = new File(location);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IllegalStateException("无法创建目录: " + dir.getAbsolutePath());
            }
        }

        // 写文件
        var file = new File(dir, Guidx.nextID() + ".tmp");
        try {
            if (!file.createNewFile()) {
                throw new IllegalStateException("无法写入数据: " + dir.getAbsolutePath());
            }
        } finally {
            Filex.delete(file);
        }
    }

    @Override
    public String store(ObjectStream stream) throws IOException {
        // 取摘要的前三个字符作为文件夹名
        var dir = new File(this.location, stream.getDigest().substring(0, 3));
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException(Stringx.format("无法写入数据: " + this.location));
        }

        // 使用摘要作为文件名
        var file = new File(dir, stream.getDigest());

        stream.transferTo(file);

        return stream.getDigest();
    }

    @Override
    public boolean exists(String key) {
        var file = new File(new File(this.location, key.substring(0, 3)), key);
        return file.exists();
    }

    @Override
    public void delete(String key) throws IOException {
        var file = new File(new File(this.location, key.substring(0, 3)), key);
        Filex.delete(file);
    }

    @Override
    public ObjectStream get(String key) throws FileNotFoundException, IOException {
        var file = new File(new File(this.location, key.substring(0, 3)), key);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + key);
        }
        return new FileObjectStream(file);
    }
}
