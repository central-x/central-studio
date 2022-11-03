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

package central.api.scheduled;

import central.api.scheduled.fetcher.DataFetcher;
import central.api.scheduled.fetcher.saas.SaasContainer;
import central.bean.LifeCycle;
import central.lang.Attribute;
import central.lang.Stringx;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定期刷新的数据
 *
 * @author Alan Yeh
 * @since 2022/10/13
 */
public class ScheduledDataContext implements LifeCycle {
    private ScheduledExecutorService scheduled;

    private final Map<String, DataFetcher<? extends DataContainer>> dataFetcher = new ConcurrentHashMap<>();
    private final Map<String, DataListener> dataListener = new ConcurrentHashMap<>();
    private final Map<String, DataContainer> data = new ConcurrentHashMap<>();

    private final int delay;

    private final ProviderSupplier supplier;

    /**
     * 创建定时刷新数据容器
     *
     * @param delay    刷新检查频率
     * @param supplier 类型获取器
     */
    public ScheduledDataContext(int delay, ProviderSupplier supplier) {
        this.delay = delay;
        this.supplier = supplier;
    }

    @Override
    public void initialized() {
        scheduled = Executors.newSingleThreadScheduledExecutor(new CustomizableThreadFactory("central-data-fetcher"));
        scheduled.scheduleWithFixedDelay(new ScheduledFetcher(this.dataFetcher, this.dataListener, this.data, this.supplier), this.delay, this.delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void destroy() {
        scheduled.shutdownNow();
        scheduled = null;
    }

    /**
     * 添加定期获取数据任务
     *
     * @param fetcher 属性
     * @param <T>     数据类型
     */
    public <T extends DataContainer> void addFetcher(Attribute<? extends DataFetcher<T>> fetcher) {
        if (this.dataFetcher.containsKey(fetcher.getCode())) {
            throw new IllegalStateException(Stringx.format("数据任务[{}]冲突", fetcher.getCode()));
        }
        this.dataFetcher.put(fetcher.getCode(), fetcher.requireValue());
    }

    public <T extends DataContainer> void addFetcher(Attribute<? extends DataFetcher<T>> fetcher, DataListener<T> listener) {
        if (this.dataFetcher.containsKey(fetcher.getCode())) {
            throw new IllegalStateException(Stringx.format("数据任务[{}]冲突", fetcher.getCode()));
        }
        this.dataFetcher.put(fetcher.getCode(), fetcher.requireValue());
        this.dataListener.put(fetcher.getCode(), listener);
    }

    /**
     * 移除定期获取数据任务
     *
     * @param fetcher 属性
     * @param <T>     数据类型
     */
    public <T extends DataContainer> void removeFetcher(Attribute<? extends DataFetcher<T>> fetcher) {
        this.dataFetcher.remove(fetcher.getCode());
        this.dataListener.remove(fetcher.getCode());
    }

    /**
     * 获取数据
     *
     * @param fetcher 属性
     * @param <T>     数据类型
     * @return 数据
     */
    public <T extends DataContainer> T get(Attribute<? extends DataFetcher<T>> fetcher) {
        return (T) this.data.get(fetcher.getCode());
    }

    /**
     * 定期刷新数据
     */
    @Slf4j
    @RequiredArgsConstructor
    private static class ScheduledFetcher implements Runnable {
        private final Map<String, DataFetcher<? extends DataContainer>> fetcher;
        private final Map<String, DataListener> listener;
        private final Map<String, DataContainer> data;
        private final ProviderSupplier supplier;

        @Override
        public void run() {
            var keys = fetcher.keySet();
            for (var it : keys) {
                try {
                    data.compute(it, (key, container) -> {
                        var fetcher = this.fetcher.get(key);
                        if (fetcher == null) {
                            return new SaasContainer();
                        } else if (container == null || container.isTimeout(fetcher.getTimeout())) {
                            fetcher.setProviderSupplier(supplier);
                            var data = fetcher.get();
                            var listener = this.listener.get(key);
                            if (listener != null){
                                listener.refresh(key, data);
                            }
                            return data;
                        } else {
                            return container;
                        }
                    });
                } catch (Throwable throwable) {
                    log.error("刷新数据出现异常: " + throwable.getLocalizedMessage(), throwable);
                }
            }
        }
    }
}
