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

package central.studio.gateway.core.filter;

import central.provider.scheduled.event.DataRefreshEvent;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.gateway.GatewayContainer;
import central.pluglet.PlugletFactory;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Setter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Gateway Container
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
@Component
public class Container implements DisposableBean, GenericApplicationListener {

    @Setter(onMethod_ = @Autowired)
    private FilterResolver resolver;

    @Setter(onMethod_ = @Autowired)
    private PlugletFactory factory;

    /**
     * 过滤器
     * <p>
     * tenant -> id -> filter
     */
    private final Map<String, Map<String, DynamicFilter>> filters = new HashMap<>();

    /**
     * 获取过滤器
     *
     * @param tenant 租户标识
     */
    public List<DynamicFilter> getFilters(String tenant) {
        return new ArrayList<>(this.filters.computeIfAbsent(tenant, key -> new HashMap<>()).values());
    }

    /**
     * 获取过滤器
     *
     * @param tenant 租户标识
     * @param id     主键
     */
    public DynamicFilter getFilter(String tenant, String id) {
        return this.filters.computeIfAbsent(tenant, key -> new HashMap<>()).get(id);
    }

    /**
     * 保存过滤器
     *
     * @param tenant 租户标识
     * @param filter 主键
     */
    private @Nullable DynamicFilter putFilter(String tenant, DynamicFilter filter) {
        return this.filters.computeIfAbsent(tenant, key -> new HashMap<>()).put(filter.getData().getId(), filter);
    }

    @Override
    public boolean supportsEventType(ResolvableType eventType) {
        return Objects.equals(eventType.getType(), DataRefreshEvent.class);
    }

    @Override
    public void onApplicationEvent(@Nonnull ApplicationEvent event) {
        if (event instanceof DataRefreshEvent<?> refreshEvent) {
            if (!Objects.equals(refreshEvent.getValue(), DataFetcherType.GATEWAY.getValue())) {
                return;
            }

            var container = (GatewayContainer) refreshEvent.getContainer();

            {
                // 初始化过滤器
                for (var tenant : container.getFilters().entrySet()) {
                    for (var data : tenant.getValue()) {
                        var current = this.getFilter(tenant.getKey(), data.getId());
                        if (current == null || !Objects.equals(data.getModifyDate(), current.getData().getModifyDate())) {
                            // 如果当前没有，或者已经过期了，就创建新的过滤器
                            var filter = new DynamicFilter(data, this.resolver, this.factory);
                            var old = this.putFilter(tenant.getKey(), filter);
                            this.factory.destroy(old);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        {
            // 销毁过滤器
            for (var tenant : this.filters.entrySet()) {
                var ids = new HashSet<>(tenant.getValue().keySet());
                for (var id : ids) {
                    var filter = tenant.getValue().remove(id);
                    this.factory.destroy(filter);
                }
            }
        }
    }
}
