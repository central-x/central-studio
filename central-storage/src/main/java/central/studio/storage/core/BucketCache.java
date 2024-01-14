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

package central.studio.storage.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 缓存
 *
 * @author Alan Yeh
 * @since 2022/11/02
 */
public interface BucketCache {
    /**
     * 保存数据
     * <p>
     * 如果键已存在，则抛异常
     *
     * @param key  缓存键
     * @param data 待保存数据
     */
    void put(String key, InputStream data) throws IOException;

    /**
     * 保存数据
     * <p>
     * 如果键已存在，则覆盖
     *
     * @param key  缓存键
     * @param data 待保存数据
     */
    void set(String key, InputStream data) throws IOException;

    /**
     * 缓存数据流，并返回已保存的数据流
     *
     * @param key  缓存键
     * @param data 待缓存的数据
     * @return 已缓存的数据
     */
    InputStream cache(String key, InputStream data) throws IOException;

    /**
     * 判断缓存是否已存在
     *
     * @param key 缓存键
     */
    boolean exists(String key);

    /**
     * 获取数据流
     *
     * @param key 缓存键
     */
    InputStream get(String key) throws IOException;

    /**
     * 获取数据流
     * <p>
     * 多个键的缓存会合并成一个数据流
     *
     * @param keys 缓存键集合
     */
    InputStream get(List<String> keys) throws IOException;

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    void delete(String key) throws IOException;

    /**
     * 删除缓存
     *
     * @param keys 缓存键集合
     */
    void delete(List<String> keys) throws IOException;
}
