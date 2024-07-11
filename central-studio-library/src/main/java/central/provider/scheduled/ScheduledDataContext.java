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

package central.provider.scheduled;

import central.lang.Stringx;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.util.Observable;
import central.util.ObservableList;
import central.util.ObservableMap;
import central.util.ObserveEvent;
import central.util.concurrent.DelayedElement;
import central.util.concurrent.DelayedQueue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 定期刷新的数据
 *
 * @author Alan Yeh
 * @since 2022/10/13
 */
public class ScheduledDataContext extends Observable<ScheduledDataContext> implements DataContext {
    /**
     * 数据刷新事件
     */
    @Getter
    @RequiredArgsConstructor
    public static class DataRefreshedEvent implements ObserveEvent<ScheduledDataContext> {
        private final ScheduledDataContext observable;

        /**
         * 刷新数据的 Fetcher
         */
        private final DataFetcherType fetcher;

        /**
         * 刷新的数据
         */
        private final DataContainer container;

        public static DataRefreshedEvent of(ScheduledDataContext context, DataFetcherType fetcher, DataContainer container) {
            return new DataRefreshedEvent(context, fetcher, container);
        }
    }

    private ExecutorService service;

    /**
     * 数据获取器
     */
    private final ObservableList<DataFetcherType> fetchers = new ObservableList<>();

    /**
     * 数据容器
     */
    private final Map<String, DataContainer> data = new ObservableMap<>(new ConcurrentHashMap<>());

    /**
     * Bean 获取器
     */
    private final BeanSupplier supplier;

    /**
     * 创建定时刷新数据容器
     *
     * @param supplier 类型获取器，用于给数据获取器获取组件
     */
    public ScheduledDataContext(BeanSupplier supplier) {
        this.supplier = supplier;
        service = Executors.newFixedThreadPool(1, new CustomizableThreadFactory("central-data-fetcher"));
        service.submit(new ScheduledFetcher(this, this.fetchers));
    }

    @Override
    public void destroy() {
        // 销毁线程
        service.shutdownNow();
        service = null;
    }

    /**
     * 添加定期获取数据任务
     *
     * @param fetcher 数据获取器
     * @param <T>     数据类型
     */
    public <T extends DataContainer> void addFetcher(DataFetcherType fetcher) {
        if (this.fetchers.contains(fetcher)) {
            throw new IllegalStateException(Stringx.format("数据任务[{}]冲突", fetcher.getValue()));
        }
        this.fetchers.add(fetcher);
    }

    /**
     * 移除定期获取数据任务
     *
     * @param fetcher 数据获取器
     * @param <T>     数据类型
     */
    public <T extends DataContainer> void removeFetcher(DataFetcherType fetcher) {
        this.fetchers.remove(fetcher);
    }

    /**
     * 获取数据
     *
     * @param fetcher 数据获取器
     * @param <T>     数据类型
     * @return 数据
     */
    @SuppressWarnings("unchecked")
    public <T extends DataContainer> T getData(DataFetcherType fetcher) {
        return (T) this.data.get(fetcher.getValue());
    }

    /**
     * 定期刷新数据
     */
    @Slf4j
    private static class ScheduledFetcher implements Runnable {
        // 延迟队列，把 DataFetcher 放到这个队列里，就不需要通过循环来检测下一次更新数据的时间了
        private final DelayedQueue<DelayedElement<DataFetcherType>> queue = new DelayedQueue<>();
        private final ScheduledDataContext context;
        private final ObservableList<DataFetcherType> fetchers;

        public ScheduledFetcher(ScheduledDataContext context, ObservableList<DataFetcherType> fetchers) {
            this.context = context;
            this.fetchers = fetchers;

            fetchers.addObserver(event -> {
                if (event instanceof ObservableList.ElementAdded<DataFetcherType> added) {
                    for (var newFetcher : added.getElements()) {
                        this.queue.offer(new DelayedElement<>(newFetcher, Duration.ZERO));
                    }
                }
            });
        }

        @Override
        @SneakyThrows
        public void run() {
            try {
                // 等待应用启动之后再获取数据
                Thread.sleep(Duration.ofSeconds(10).toMillis());

                while (true) {
                    var element = queue.take();
                    var type = element.getElement();
                    // 当前数据获取器已经不再包含的话，就不再获取数据了
                    // 由于没有重新加入 queue 队列，因此相当于移除了
                    if (!this.fetchers.contains(type)) {
                        if (type != null) {
                            // 数据已过期，移除
                            this.context.data.remove(type.getValue());
                        }
                        continue;
                    }

                    try {
                        this.context.data.compute(type.getValue(), (key, container) -> {
                            var fetcher = type.getFetcher().get();

                            // 重新添加到队列里，这样就可以周期性执行获取数据的逻辑了
                            queue.add(new DelayedElement<>(type, fetcher.getTimeout()));

                            // 获取新数据
                            fetcher.setSupplier(this.context.supplier);
                            var data = (DataContainer) fetcher.get();

                            // 通知观查者数据已变更
                            this.context.notifyObservers(DataRefreshedEvent.of(context, type, data));
                            return data;
                        });
                    } catch (Throwable throwable) {
                        log.error("刷新数据出现异常: " + throwable.getLocalizedMessage(), throwable);
                    }
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
