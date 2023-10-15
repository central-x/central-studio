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

package central.storage.core;

import central.provider.scheduled.event.DataRefreshEvent;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.storage.StorageContainer;
import central.lang.Assertx;
import central.lang.Stringx;
import central.pluglet.PlugletFactory;
import lombok.Setter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Bucket Container
 * <p>
 * 存储桶容器
 *
 * @author Alan Yeh
 * @since 2022/10/30
 */
@Component
public class Container implements DisposableBean, GenericApplicationListener {

    @Setter(onMethod_ = @Autowired)
    private PlugletFactory factory;

    /**
     * 存储桶
     * <p>
     * tenant -> code -> bucket
     */
    private final Map<String, Map<String, DynamicBucket>> buckets = new HashMap<>();

    /**
     * 根据标识获取存储桶
     *
     * @param tenant 租户标识
     * @param code   标识
     */
    public @Nullable DynamicBucket getBucket(String tenant, String code) {
        return this.buckets.computeIfAbsent(tenant, key -> new HashMap<>()).get(code);
    }

    /**
     * 获取存储桶
     *
     * @param tenant 租户标识
     */
    public @Nonnull List<DynamicBucket> getBuckets(String tenant) {
        return new ArrayList<>(this.buckets.computeIfAbsent(tenant, key -> new HashMap<>()).values());
    }

    /**
     * 根据标识获取存储桶
     *
     * @param tenant 租户标识
     * @param code   标识
     */
    public @Nonnull DynamicBucket requireBucket(String tenant, String code) {
        return Assertx.requireNotNull(this.getBucket(tenant, code), () -> new ResponseStatusException(HttpStatus.NOT_FOUND, Stringx.format("存储桶[code={}]不存在", code)));
    }

    private @Nullable DynamicBucket putBucket(String tenant, DynamicBucket bucket) {
        return this.buckets.computeIfAbsent(tenant, key -> new HashMap<>()).put(bucket.getData().getCode(), bucket);
    }

    @Override
    public boolean supportsEventType(ResolvableType eventType) {
        return eventType.getType() == DataRefreshEvent.class;
    }

    @Override
    public void onApplicationEvent(@Nonnull ApplicationEvent event) {
        if (event instanceof DataRefreshEvent<?> refreshEvent) {
            if (!Objects.equals(refreshEvent.getValue(), DataFetcherType.STORAGE.getValue())) {
                return;
            }

            var container = (StorageContainer) refreshEvent.getContainer();

            {
                // 初始化存储器
                for (var tenant : container.getBuckets().entrySet()) {
                    for (var data : tenant.getValue().values()) {
                        var current = this.getBucket(tenant.getKey(), data.getCode());
                        if (current == null || !Objects.equals(data.getModifyDate(), current.getData().getModifyDate())) {
                            // 如果当前没有，或者已经过期了，就创建新的存储桶
                            var bucket = new DynamicBucket(data, this.factory);
                            var old = this.putBucket(tenant.getKey(), bucket);
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
            for (var tenant : this.buckets.entrySet()) {
                var codes = new HashSet<>(tenant.getValue().keySet());
                for (var code : codes) {
                    var bucket = tenant.getValue().remove(code);
                    this.factory.destroy(bucket);
                }
            }
        }
    }
}
