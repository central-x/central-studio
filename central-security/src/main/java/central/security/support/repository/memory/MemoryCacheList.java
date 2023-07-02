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

package central.security.support.repository.memory;

import central.security.support.repository.CacheList;

import java.util.Collection;
import java.util.List;

/**
 * 内存缓存列表
 *
 * @author Alan Yeh
 * @since 2023/07/02
 */
public class MemoryCacheList implements CacheList {
    @Override
    public List<String> values() {
        return null;
    }

    @Override
    public List<String> range(long start, long end) {
        return null;
    }

    @Override
    public void trim(long start, long end) {

    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public long add(String... values) {
        return 0;
    }

    @Override
    public long add(Collection<String> values) {
        return 0;
    }

    @Override
    public long addIfPresent(String... values) {
        return 0;
    }

    @Override
    public long insert(int index, String... values) {
        return 0;
    }

    @Override
    public void set(long index, String value) {

    }

    @Override
    public long remove(long count, String value) {
        return 0;
    }

    @Override
    public long remove(long index) {
        return 0;
    }

    @Override
    public long indexOf(String value) {
        return 0;
    }

    @Override
    public long lastIndexOf(String value) {
        return 0;
    }
}
