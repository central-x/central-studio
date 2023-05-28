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

package central.security.support.captcha.storage.memory;

import central.security.support.captcha.Captcha;
import central.security.support.captcha.CaptchaContainer;
import central.util.Collectionx;
import central.util.Guidx;
import central.util.concurrent.ConsumableQueue;
import central.util.concurrent.DelayedElement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.function.ThrowingConsumer;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * Memory Captcha Container
 * <p>
 * 使用内存保存验证码
 *
 * @author Alan Yeh
 * @since 2023/05/26
 */
@Slf4j
@Component
public class MemoryContainer implements CaptchaContainer, Closeable {

    /**
     * 验证码存储
     * <p>
     * tenant -> key -> value
     */
    private final Map<String, Map<String, Captcha>> storage = new HashMap<>();

    private final ConsumableQueue<DelayedElement<String>, DelayQueue<DelayedElement<String>>> timeoutQueue;

    public MemoryContainer() {
        // 使用延迟队列清除验证码
        this.timeoutQueue = new ConsumableQueue<>(new DelayQueue<>(), "central-security.captcha.memory.cleaner");
        this.timeoutQueue.addConsumer((ThrowingConsumer<DelayQueue<DelayedElement<String>>>) queue -> {
            try {
                while (true) {
                    var element = queue.take();
                    storage.remove(element.getElement());
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
    public String put(String tenantCode, String value, Duration expires) {
        var key = Guidx.nextID();
        this.storage.computeIfAbsent(tenantCode, it -> new HashMap<>())
                .put(key, new CaptchaImpl(value, expires));
        return key;
    }


    @Override
    public Captcha get(String tenantCode, String key) {
        return this.storage.getOrDefault(tenantCode, Collectionx.emptyMap())
                .remove(key);
    }

    @Getter
    @RequiredArgsConstructor
    private static class CaptchaImpl implements Captcha {
        private final long timestamp = System.currentTimeMillis();
        private final String value;
        private final Duration expires;

        @Override
        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > TimeUnit.MILLISECONDS.convert(expires);
        }

        @Override
        public boolean verify(String value, boolean caseSensitive) {
            if (this.isExpired()) {
                return false;
            }
            if (caseSensitive) {
                return this.value.equals(value);
            } else {
                return this.value.equalsIgnoreCase(value);
            }
        }
    }
}
