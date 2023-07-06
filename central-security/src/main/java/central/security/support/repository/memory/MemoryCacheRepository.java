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

import central.security.support.repository.*;
import central.util.concurrent.ConsumableQueue;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * 内存缓存仓库
 *
 * @author Alan Yeh
 * @since 2023/06/10
 */
public class MemoryCacheRepository implements CacheRepository, AutoCloseable {

    private final Map<String, Cache> caches = new HashMap<>();
    private final ConsumableQueue<Cache, DelayQueue<Cache>> timeoutQueue;

    public MemoryCacheRepository() {
        this.timeoutQueue = new ConsumableQueue<>(new DelayQueue<>(), "central.cache-repository.memory.cleaner");
        this.timeoutQueue.addConsumer(queue -> {
            try {
                while (true) {
                    var cache = queue.poll(5, TimeUnit.SECONDS);
                    if (cache != null) {
                        if (cache.isExpired()) {
                            // 缓存已过期
                            this.caches.remove(cache.getKey());
                        } else if (!cache.isPermanent() && !cache.isInvalid()) {
                            // 如果缓存是临时并且有效的，那么需要重新加入队列进行倒计时
                            queue.offer(cache);
                        }
                    }
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        });
    }

    @Override
    public void close() throws IOException {
        this.timeoutQueue.close();
    }

    @Override
    public boolean hasKey(String key) {
        return this.caches.containsKey(key);
    }

    @Override
    public boolean delete(String key) {
        Cache cache = this.caches.remove(key);
        if (cache == null) {
            return false;
        }
        // 将缓存置为无效
        cache.invalid();
        return true;
    }

    @Override
    public long delete(Collection<String> keys) {
        long count = 0;
        for (var key : keys) {
            count += (this.delete(key) ? 1 : 0);
        }
        return count;
    }

    @Override
    public DataType type(String key) {
        var cache = this.caches.get(key);
        if (cache == null) {
            return DataType.NONE;
        } else {
            return cache.getType();
        }
    }

    @Override
    public Set<String> keys() {
        return this.caches.keySet();
    }

    @Override
    public boolean expire(String key, Duration timeout) {
        var cache = this.caches.get(key);
        if (cache == null) {
            return false;
        }
        cache.setExpire(new Date(System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(timeout)));
        this.timeoutQueue.offer(cache);
        return true;
    }

    @Override
    public boolean expireAt(String key, Date date) {
        var cache = this.caches.get(key);
        if (cache == null) {
            return false;
        }

        cache.setExpire(date);
        this.timeoutQueue.offer(cache);
        return true;
    }

    @Override
    public boolean persist(String key) {
        var cache = this.caches.get(key);
        if (cache == null) {
            return false;
        }
        cache.persist();
        return true;
    }

    @Override
    public Duration getExpire(String key) {
        var cache = this.caches.get(key);
        if (cache == null) {
            return null;
        } else {
            return Duration.ofMillis(cache.getDelay(TimeUnit.MILLISECONDS));
        }
    }

    @Override
    public CacheValue opsValue(String key) {
        return null;
    }

    @Override
    public CacheList opsList(String key) {
        return null;
    }

    @Override
    public CacheQueue opsQueue(String key) {
        return null;
    }

    @Override
    public CacheSet opsSet(String key) {
        return null;
    }

    @Override
    public CacheSet opsZSet(String key) {
        return null;
    }

    @Override
    public CacheMap opsMap(String key) {
        return null;
    }
}
