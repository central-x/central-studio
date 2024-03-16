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

package central.provider.scheduled.fetcher.log;

import central.provider.scheduled.DataContainer;
import central.data.log.LogCollector;
import central.data.log.LogFilter;
import central.data.log.LogStorage;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.*;

/**
 * 日志中心数据容器
 *
 * @author Alan Yeh
 * @since 2022/10/25
 */
@RequiredArgsConstructor
public class LogContainer extends DataContainer {
    @Serial
    private static final long serialVersionUID = 254177024383905591L;

    /**
     * 日志采集器数据
     * <p>
     * id -> collector
     */
    private final Map<String, LogCollector> collectors;

    /**
     * 日志过滤器数据
     * <p>
     * id -> collector
     */
    private final Map<String, LogFilter> filters;

    /**
     * 日志存储器数据
     * <p>
     * id -> collector
     */
    private final Map<String, LogStorage> storages;

    public LogContainer() {
        this.collectors = Map.of();
        this.filters = Map.of();
        this.storages = Map.of();
    }

    /**
     * 获取采集器数据
     */
    public List<LogCollector> getCollectors() {
        return new ArrayList<>(this.collectors.values());
    }

    /**
     * 获取采集器数据
     *
     * @param id 采集器主键
     */
    public LogCollector getCollector(String id) {
        return this.collectors.get(id);
    }

    /**
     * 获取过滤器数据
     */
    public List<LogFilter> getFilters() {
        return new ArrayList<>(this.filters.values());
    }

    /**
     * 获取过滤器数据
     *
     * @param id 过滤器主键
     */
    public LogFilter getFilter(String id) {
        return this.filters.get(id);
    }

    /**
     * 获取存储器数据
     */
    public List<LogStorage> getStorages() {
        return new ArrayList<>(this.storages.values());
    }

    /**
     * 获取存储器数据
     *
     * @param id 存储器主键
     */
    public LogStorage getStorage(String id) {
        return this.storages.get(id);
    }
}
