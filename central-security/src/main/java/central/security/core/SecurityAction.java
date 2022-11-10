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

package central.security.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Security Action
 * 安全行为
 *
 * @author Alan Yeh
 * @since 2022/10/19
 */
public abstract class SecurityAction implements ApplicationContextAware {

    @Getter
    @Setter
    private ApplicationContext applicationContext;

    @Setter
    private Map<String, Object> beanCache;

    public <T> T getBean(String name, Class<? extends T> type) {
        return (T) beanCache.computeIfAbsent(this.getClass().getName() + "." + name, key -> applicationContext.getBean(name, type));
    }

    public <T> T getBean(Class<? extends T> type) {
        return (T) beanCache.computeIfAbsent(this.getClass().getName() + "." + type.getName(), key -> applicationContext.getBean(type));
    }

    public <T> T getBean(Class<T> type, Supplier<T> supplier) {
        return (T) beanCache.computeIfAbsent(type.getName(), key -> supplier.get());
    }

    /**
     * 执行
     */
    public abstract void execute(SecurityExchange exchange);
}
