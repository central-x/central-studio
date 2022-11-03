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

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 存储桶（文件存储容器）
 *
 * @author Alan Yeh
 * @since 2022/10/30
 */
public interface Bucket {
    /**
     * 保存对象
     *
     * @param stream 对象流
     * @return 存储键
     */
    String store(ObjectStream stream) throws IOException;

    /**
     * 判断指定键的对象是否存在
     *
     * @param key 存储键
     */
    boolean exists(String key);

    /**
     * 删除指定键的对象
     *
     * @param key 存储键
     */
    void delete(String key) throws IOException;

    /**
     * 获取对象流
     *
     * @param key 存储键
     */
    ObjectStream get(String key) throws FileNotFoundException, IOException;
}
