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

package central.api.scheduled.fetcher;

import central.api.scheduled.fetcher.gateway.GatewayContainer;
import central.api.scheduled.fetcher.gateway.GatewayFetcher;
import central.api.scheduled.fetcher.log.LogContainer;
import central.api.scheduled.fetcher.log.LogFetcher;
import central.api.scheduled.fetcher.multicast.MulticastContainer;
import central.api.scheduled.fetcher.multicast.MulticastFetcher;
import central.api.scheduled.fetcher.security.SecurityContainer;
import central.api.scheduled.fetcher.security.SecurityFetcher;
import central.api.scheduled.fetcher.storage.StorageContainer;
import central.api.scheduled.fetcher.storage.StorageFetcher;
import central.api.scheduled.fetcher.system.SysContainer;
import central.api.scheduled.fetcher.system.SystemFetcher;
import central.api.scheduled.fetcher.saas.SaasContainer;
import central.api.scheduled.fetcher.saas.SaasFetcher;
import central.lang.Attribute;

/**
 * 数据获取器
 *
 * @author Alan Yeh
 * @since 2022/10/13
 */
public interface DataFetcherType {
    /**
     * 租户类数据
     */
    Attribute<DataFetcher<SaasContainer>> SAAS = Attribute.of("fetcher.saas", SaasFetcher::new);
    /**
     * 系统类数据
     */
    Attribute<DataFetcher<SysContainer>> SYSTEM = Attribute.of("fetcher.system", SystemFetcher::new);
    /**
     * 日志类数据
     */
    Attribute<DataFetcher<LogContainer>> LOG = Attribute.of("fetcher.log", LogFetcher::new);
    /**
     * 存储中心数据
     */
    Attribute<DataFetcher<StorageContainer>> STORAGE = Attribute.of("fetcher.storage", StorageFetcher::new);
    /**
     * 广播中心数据
     */
    Attribute<DataFetcher<MulticastContainer>> MULTICAST = Attribute.of("fetcher.multicast", MulticastFetcher::new);
    /**
     * 认证中心数据
     */
    Attribute<DataFetcher<SecurityContainer>> SECURITY = Attribute.of("fetcher.security", SecurityFetcher::new);
    /**
     * 网关中心数据
     */
    Attribute<DataFetcher<GatewayContainer>> GATEWAY = Attribute.of("fetcher.gateway", GatewayFetcher::new);
}
