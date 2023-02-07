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

package central.logging.core.collector;

import central.data.log.Log;
import central.data.log.LogCollector;
import central.lang.Assertx;
import central.lang.Stringx;
import central.lang.reflect.TypeRef;
import central.logging.core.filter.Filter;
import central.pluglet.PlugletFactory;
import central.util.Jsonx;
import central.util.Listx;
import central.util.Objectx;
import lombok.Getter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态采集器
 *
 * @author Alan Yeh
 * @since 2022/10/25
 */
public class DynamicCollector extends Collector implements DisposableBean {

    @Getter
    private final LogCollector data;

    private final PlugletFactory factory;

    private final Collector collector;

    private final ApplicationContext applicationContext;

    public DynamicCollector(LogCollector data, PlugletFactory factory, ApplicationContext applicationContext) {
        this.data = data;
        this.factory = factory;
        this.applicationContext = applicationContext;

        var type = Assertx.requireNotNull(CollectorType.resolve(data.getType()), "找不到指定类型的采集器: " + data.getType());

        try {
            var params = Jsonx.Default().deserialize(this.data.getParams(), TypeRef.ofMap(String.class, Object.class));
            this.collector = this.factory.create(type.getType(), params);
            this.collector.setDelegate(this);
        } catch (Exception ex) {
            throw new IllegalStateException(Stringx.format("初始化插件[id={}, type={}]出现异常: " + ex.getLocalizedMessage(), this.data.getId(), this.data.getType()), ex);
        }
    }

    @Override
    public void destroy() throws Exception {
        factory.destroy(this.collector);
    }

    private final List<Filter> filters = new ArrayList<>();

    /**
     * 注册过滤器
     */
    public void register(Filter filter) {
        this.filters.add(filter);
    }

    /**
     * 撤销注册过滤器
     */
    public void deregister(Filter filter) {
        this.filters.remove(filter);
    }

    @Override
    public void collect(List<Log> logs) {
        if (!this.data.getEnabled()) {
            // 如果采集器被禁用了，则不再继续采集
            return;
        }

        // 没有日志，不处理后面的东西
        if (Listx.isNullOrEmpty(logs)){
            return;
        }

        // TODO 补始相关信息

        // 给所有日志补充租户信息
        logs.forEach(it -> it.setTenantCode(Objectx.getOrDefault(it.getTenantCode(), "master")));

        this.applicationContext.publishEvent(new CollectEvent(this.data.getId(), logs));

        // 传递到过滤器
        for (var filter : this.filters) {
            filter.filter(logs);
        }
    }
}
