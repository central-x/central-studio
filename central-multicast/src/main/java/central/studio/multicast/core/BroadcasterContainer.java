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

package central.studio.multicast.core;

import central.provider.scheduled.event.DataRefreshEvent;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.multicast.MulticastContainer;
import central.lang.Assertx;
import central.lang.Stringx;
import central.pluglet.PlugletFactory;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Setter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * Multicast Container
 * <p>
 * 实例容器
 *
 * @author Alan Yeh
 * @since 2022/11/04
 */
@Component
public class BroadcasterContainer implements DisposableBean, GenericApplicationListener {

    @Setter(onMethod_ = @Autowired)
    private BroadcasterResolver resolver;

    @Setter(onMethod_ = @Autowired)
    private PlugletFactory factory;

    /**
     * 存储桶
     * <p>
     * tenant -> code -> broadcaster
     */
    private final Map<String, Map<String, DynamicBroadcaster>> broadcasters = new HashMap<>();

    /**
     * 根据标识获取存储桶
     *
     * @param tenant 租户标识
     * @param code   标识
     */
    public @Nullable DynamicBroadcaster getBroadcaster(String tenant, String code) {
        return this.broadcasters.computeIfAbsent(tenant, key -> new HashMap<>()).get(code);
    }

    /**
     * 获取存储桶
     *
     * @param tenant 租户标识
     */
    public @Nonnull List<DynamicBroadcaster> getBroadcasters(String tenant) {
        return new ArrayList<>(this.broadcasters.computeIfAbsent(tenant, key -> new HashMap<>()).values());
    }

    /**
     * 根据标识获取存储桶
     *
     * @param tenant 租户标识
     * @param code   标识
     */
    public @Nonnull DynamicBroadcaster requireBroadcaster(String tenant, String code) {
        return Assertx.requireNotNull(this.getBroadcaster(tenant, code), () -> new ResponseStatusException(HttpStatus.NOT_FOUND, Stringx.format("存储桶[code={}]不存在", code)));
    }

    private @Nullable DynamicBroadcaster putBroadcaster(String tenant, DynamicBroadcaster broadcaster) {
        return this.broadcasters.computeIfAbsent(tenant, key -> new HashMap<>()).put(broadcaster.getData().getCode(), broadcaster);
    }

    @Override
    public boolean supportsEventType(ResolvableType eventType) {
        return eventType.getType() == DataRefreshEvent.class;
    }

    @Override
    public void onApplicationEvent(@Nonnull ApplicationEvent event) {
        if (event instanceof DataRefreshEvent<?> refreshEvent) {
            if (!Objects.equals(refreshEvent.getValue(), DataFetcherType.MULTICAST.getValue())) {
                return;
            }

            var container = (MulticastContainer) refreshEvent.getContainer();

            {
                // 初始化存储器
                for (var tenant : container.getBroadcasters().entrySet()) {
                    for (var data : tenant.getValue().values()) {
                        var current = this.getBroadcaster(tenant.getKey(), data.getCode());
                        if (current == null || !Objects.equals(data.getModifyDate(), current.getData().getModifyDate())) {
                            // 如果当前没有，或者已经过期了，就创建新的存储桶
                            var broadcaster = new DynamicBroadcaster(data, this.resolver, this.factory);
                            var old = this.putBroadcaster(tenant.getKey(), broadcaster);
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
            // 销毁存储桶
            for (var tenant : this.broadcasters.entrySet()) {
                var codes = new HashSet<>(tenant.getValue().keySet());
                for (var code : codes) {
                    var broadcaster = tenant.getValue().remove(code);
                    this.factory.destroy(broadcaster);
                }
            }
        }
    }
}