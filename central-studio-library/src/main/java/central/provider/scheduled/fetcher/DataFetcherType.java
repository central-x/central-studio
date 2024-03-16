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

package central.provider.scheduled.fetcher;

import central.bean.OptionalEnum;
import central.lang.Arrayx;
import central.provider.scheduled.DataContainer;
import central.provider.scheduled.fetcher.gateway.GatewayFetcher;
import central.provider.scheduled.fetcher.log.LogFetcher;
import central.provider.scheduled.fetcher.multicast.MulticastFetcher;
import central.provider.scheduled.fetcher.saas.SaasFetcher;
import central.provider.scheduled.fetcher.identity.IdentityFetcher;
import central.provider.scheduled.fetcher.storage.StorageFetcher;
import central.provider.scheduled.fetcher.system.SystemFetcher;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 数据获取器
 *
 * @author Alan Yeh
 * @since 2022/10/13
 */
@Getter
@RequiredArgsConstructor
public enum DataFetcherType implements OptionalEnum<String> {
    SAAS("租户类热数据容器", "fetcher.saas", SaasFetcher::new),
    SYSTEM("系统类热数据容器", "fetcher.system", SystemFetcher::new),
    LOG("日志中心热数据容器", "fetcher.log", LogFetcher::new),
    STORAGE("存储中心热数据容器", "fetcher.storage", StorageFetcher::new),
    MULTICAST("广播中心热数据容器", "fetcher.multicast", MulticastFetcher::new),
    IDENTITY("认证中心热数据容器", "fetcher.identity", IdentityFetcher::new),
    GATEWAY("网关中心热数据容器", "fetcher.gateway", GatewayFetcher::new);

    private final String name;
    private final String value;
    private final Supplier<DataFetcher<? extends DataContainer>> fetcher;

    @JsonCreator
    public static DataFetcherType resolve(String value) {
        return Arrayx.asStream(DataFetcherType.values()).filter(it -> Objects.equals(it.getValue(), value)).findFirst().orElse(null);
    }
}
