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

package central.studio.identity.core.strategy;

import central.lang.Assertx;
import central.lang.Stringx;
import central.provider.scheduled.event.DataRefreshEvent;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.identity.IdentityContainer;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
 * 插件容器
 *
 * @author Alan Yeh
 * @since 2022/11/05
 */
@Slf4j
@Component
public class StrategyContainer implements DisposableBean, GenericApplicationListener {

    @Setter(onMethod_ = @Autowired)
    private StrategyResolver resolver;

    /**
     * 安全策略
     * <p>
     * tenant -> code -> strategy
     */
    private final Map<String, Map<String, DynamicStrategyFilter>> strategies = new HashMap<>();

    /**
     * 根据标识获取安全策略
     *
     * @param tenant 租户标识
     * @param code   标识
     */
    public @Nullable DynamicStrategyFilter getStrategy(String tenant, String code) {
        return this.strategies.computeIfAbsent(tenant, key -> new HashMap<>()).get(code);
    }

    /**
     * 获取安全策略
     *
     * @param tenant 租户标识
     */
    public @Nonnull List<DynamicStrategyFilter> getStrategies(String tenant) {
        return new ArrayList<>(this.strategies.computeIfAbsent(tenant, key -> new HashMap<>()).values());
    }

    /**
     * 根据标识获取安全策略
     *
     * @param tenant 租户标识
     * @param code   标识
     */
    public @Nonnull DynamicStrategyFilter requireStrategy(String tenant, String code) {
        return Assertx.requireNotNull(this.getStrategy(tenant, code), () -> new ResponseStatusException(HttpStatus.NOT_FOUND, Stringx.format("安全策略[code={}]不存在", code)));
    }

    public @Nullable DynamicStrategyFilter putStrategy(String tenant, DynamicStrategyFilter strategy) {
        return this.strategies.computeIfAbsent(tenant, key -> new HashMap<>()).put(strategy.getData().getCode(), strategy);
    }

    @Override
    public boolean supportsEventType(ResolvableType eventType) {
        return eventType.getType() == DataRefreshEvent.class;
    }

    @Override
    public void onApplicationEvent(@Nonnull ApplicationEvent event) {
        if (event instanceof DataRefreshEvent<?> refreshEvent) {
            if (!Objects.equals(refreshEvent.getValue(), DataFetcherType.IDENTITY.getValue())) {
                return;
            }

            var container = (IdentityContainer) refreshEvent.getContainer();

            {
                // 初始化存储器
                for (var tenant : container.getStrategies().entrySet()) {
                    for (var data : tenant.getValue().values()) {
                        var current = this.getStrategy(tenant.getKey(), data.getCode());
                        if (current == null || !Objects.equals(data.getModifyDate(), current.getData().getModifyDate())) {
                            // 如果当前没有，或者已经过期了，就创建新的安全策略
                            var strategy = new DynamicStrategyFilter(data, this.resolver);
                            var old = this.putStrategy(tenant.getKey(), strategy);
                            if (old != null) {
                                try {
                                    old.destroy();
                                } catch (Exception ex) {
                                    log.error("实例销毁失败: " + ex.getLocalizedMessage(), ex);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        {
            // 销毁安全策略
            for (var tenant : this.strategies.entrySet()) {
                var codes = new HashSet<>(tenant.getValue().keySet());
                for (var code : codes) {
                    var strategy = tenant.getValue().remove(code);
                    if (strategy != null) {
                        try {
                            strategy.destroy();
                        } catch (Exception ex) {
                            log.error("实例销毁失败: " + ex.getLocalizedMessage(), ex);
                        }
                    }
                }
            }
        }
    }
}
