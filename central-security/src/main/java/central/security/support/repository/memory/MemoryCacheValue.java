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

import central.security.support.repository.CacheValue;

import java.time.Duration;

/**
 * 内存缓存值
 *
 * @author Alan Yeh
 * @since 2023/07/02
 */
public class MemoryCacheValue implements CacheValue {
    @Override
    public String set(Object value) {
        return null;
    }

    @Override
    public String set(Object value, Duration timeout) {
        return null;
    }

    @Override
    public String setIfAbsent(Object value) {
        return null;
    }

    @Override
    public boolean setIfAbsent(Object value, Duration timeout) {
        return false;
    }

    @Override
    public boolean setIfPresent(Object value) {
        return false;
    }

    @Override
    public boolean setIfPresent(Object value, Duration timeout) {
        return false;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public String getAndDelete() {
        return null;
    }

    @Override
    public Long increment() {
        return null;
    }

    @Override
    public Long increment(long delta) {
        return null;
    }

    @Override
    public Long decrement() {
        return null;
    }

    @Override
    public Long decrement(long delta) {
        return null;
    }
}
