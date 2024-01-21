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

package central.studio.logging.core.filter;

import central.data.log.Log;
import central.data.log.LogCollector;
import central.data.log.LogFilter;
import central.data.log.LogStorage;
import central.studio.logging.core.LoggingContainer;
import central.studio.logging.core.filter.predicate.DynamicPredicate;
import central.studio.logging.core.filter.predicate.Predicate;
import central.pluglet.PlugletFactory;
import central.util.Listx;
import lombok.Getter;
import org.springframework.beans.factory.DisposableBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 动态过滤器
 *
 * @author Alan Yeh
 * @since 2022/10/25
 */
public class DynamicFilter implements Filter, DisposableBean {
    @Getter
    private final LogFilter data;

    private final PlugletFactory factory;
    private final LoggingContainer container;

    /**
     * 采集器主键
     * <p>
     * 集合里的采集器会将日志传输到本过滤器进行过滤
     */
    private final Set<String> collectorIds = new HashSet<>();

    /**
     * 存储器主键
     * <p>
     * 过滤完的日志会交给这些存储器进行保存
     */
    private final Set<String> storageIds = new HashSet<>();

    private final List<Predicate> predicates = new ArrayList<>();

    public DynamicFilter(LogFilter data, LoggingContainer container, PlugletFactory factory) {
        this.data = data;
        this.container = container;
        this.factory = factory;

        this.collectorIds.addAll(data.getCollectors().stream().map(LogCollector::getId).toList());
        this.storageIds.addAll(data.getStorages().stream().map(LogStorage::getId).toList());

        for (var predicate : this.data.getPredicates()) {
            this.predicates.add(new DynamicPredicate(predicate, factory));
        }

        // 添加与采集器的关联
        for (var collectorId : this.collectorIds) {
            var collector = this.container.getCollector(collectorId);
            collector.register(this);
        }
    }

    @Override
    public void filter(List<Log> logs) {
        if (!this.data.getEnabled()) {
            // 当前过滤器已禁用
            return;
        }

        // 通过断言过滤日志
        logs = logs.stream().filter(log -> predicates.stream().allMatch(it -> it.predicate(log))).toList();

        if (Listx.isNotEmpty(logs)) {
            for (var storageId : this.storageIds) {
                var storage = this.container.getStorage(storageId);
                if (storage != null) {
                    storage.store(logs);
                }
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        // 取消与采集器的关联
        for (var collectorId : this.collectorIds) {
            var collector = this.container.getCollector(collectorId);
            collector.deregister(this);
        }

        for (var predicate : this.predicates) {
            this.factory.destroy(predicate);
        }
    }
}
