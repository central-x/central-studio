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

package central.studio.logging.core;

import central.provider.scheduled.event.DataRefreshEvent;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.log.LogContainer;
import central.studio.logging.core.collector.CollectorResolver;
import central.studio.logging.core.collector.DynamicCollector;
import central.studio.logging.core.filter.DynamicFilter;
import central.studio.logging.core.storage.DynamicStorage;
import central.pluglet.PlugletFactory;
import central.studio.logging.core.storage.StorageResolver;
import jakarta.annotation.Nonnull;
import lombok.Setter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

/**
 * 日志插件容器
 *
 * @author Alan Yeh
 * @since 2022/10/25
 */
@Component
public class LoggingContainer implements ApplicationContextAware, DisposableBean, InitializingBean, GenericApplicationListener {

    @Setter(onMethod_ = @Autowired)
    private CollectorResolver collectorResolver;

    @Setter(onMethod_ = @Autowired)
    private StorageResolver storageResolver;

    @Setter(onMethod_ = @Autowired)
    private PlugletFactory factory;

    @Setter
    private ApplicationContext applicationContext;

    /**
     * 根据主键获取采集器
     */
    public DynamicCollector getCollector(String id) {
        return this.collectors.get(id);
    }

    private DynamicCollector putCollector(String id, DynamicCollector collector) {
        return this.collectors.put(id, collector);
    }

    /**
     * 采集器
     * id -> Collector
     */
    private final Map<String, DynamicCollector> collectors = new HashMap<>();

    /**
     * 根据主键获取过滤器
     */
    public DynamicFilter getFilter(String id) {
        return this.filters.get(id);
    }

    private DynamicFilter putFilter(String id, DynamicFilter filter) {
        return this.filters.put(id, filter);
    }

    /**
     * 过滤器
     * id -> Filter
     */
    private final Map<String, DynamicFilter> filters = new HashMap<>();

    /**
     * 根据主键获取存储器
     */
    public DynamicStorage getStorage(String id) {
        return this.storages.get(id);
    }

    private DynamicStorage putStorage(String id, DynamicStorage storage) {
        return this.storages.put(id, storage);
    }

    /**
     * 存储器
     * id -> Storage
     */
    private final Map<String, DynamicStorage> storages = new HashMap<>();


    @Override
    public boolean supportsEventType(@Nonnull ResolvableType eventType) {
        return eventType.getType() == DataRefreshEvent.class;
    }

    @Override
    public void onApplicationEvent(@Nonnull ApplicationEvent event) {
        if (event instanceof DataRefreshEvent<?> refreshEvent) {
            if (!Objects.equals(refreshEvent.getValue(), DataFetcherType.LOG.getValue())) {
                return;
            }
            var container = (LogContainer) refreshEvent.getContainer();

            {
                // 初始化采集器
                for (var data : container.getCollectors()) {
                    var current = this.getCollector(data.getId());
                    if (current == null || !Objects.equals(data.getModifyDate(), current.getData().getModifyDate())) {
                        // 如果当前没有，或者已经过期了，就创建新的采集器
                        var collector = new DynamicCollector(data, collectorResolver, factory, this.applicationContext);
                        var old = this.collectors.put(data.getId(), collector);
                        factory.destroy(old);
                    }
                }
            }

            {
                // 初始化存储器
                for (var data : container.getStorages()) {
                    var current = this.getStorage(data.getId());
                    if (current == null || !Objects.equals(data.getModifyDate(), current.getData().getModifyDate())) {
                        // 如果当前没有，或者已经过期了，就创建新的存储器
                        var storage = new DynamicStorage(data, storageResolver, factory);
                        var old = this.storages.put(data.getId(), storage);
                        factory.destroy(old);
                    }
                }
            }

            {
                // 初始化过滤器
                for (var data : container.getFilters()) {
                    var current = this.getFilter(data.getId());
                    if (current == null || !Objects.equals(data.getModifyDate(), current.getData().getModifyDate())) {
                        // 如果当前没有，或者已经过期了，就创建新的过滤器
                        var filter = new DynamicFilter(data, this, factory);
                        var old = this.filters.put(data.getId(), filter);
                        factory.destroy(old);
                    }
                }
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Initializing LoggingContainer");
    }

    @Override
    public void destroy() throws Exception {
        {
            // 销毁过滤器
            var ids = new HashSet<>(this.filters.keySet());
            for (var id : ids) {
                var filter = this.filters.remove(id);
                this.factory.destroy(filter);
            }
        }

        {
            // 销毁存储器
            var ids = new HashSet<>(this.storages.keySet());
            for (var id : ids) {
                var storage = this.storages.remove(id);
                this.factory.destroy(storage);
            }
        }

        {
            // 销毁采集器
            var ids = new HashSet<>(this.collectors.keySet());
            for (var id : ids) {
                var collector = this.collectors.remove(id);
                this.factory.destroy(collector);
            }
        }
    }
}
