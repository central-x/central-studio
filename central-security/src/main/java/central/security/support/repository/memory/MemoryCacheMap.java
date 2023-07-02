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

import central.security.support.repository.CacheMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 内存缓存键值对
 *
 * @author Alan Yeh
 * @since 2023/07/02
 */
public class MemoryCacheMap implements CacheMap {
    @Override
    public long delete(String... keys) {
        return 0;
    }

    @Override
    public boolean hasKey(String key) {
        return false;
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public List<String> get(String... keys) {
        return null;
    }

    @Override
    public List<String> get(Collection<String> keys) {
        return null;
    }

    @Override
    public Set<String> keys() {
        return null;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public void put(String key, String value) {

    }

    @Override
    public void putAll(Map<String, String> values) {

    }

    @Override
    public boolean putIfAbsent(Map<String, String> map) {
        return false;
    }

    @Override
    public Set<String> values() {
        return null;
    }

    @Override
    public Map<String, String> entries() {
        return null;
    }
}
