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

package central.provider.scheduled.fetcher.multicast;

import central.provider.graphql.multicast.MulticastBroadcasterProvider;
import central.provider.graphql.saas.TenantProvider;
import central.provider.scheduled.BeanSupplier;
import central.provider.scheduled.fetcher.DataFetcher;
import central.data.multicast.MulticastBroadcaster;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 广播中心数据获取
 *
 * @author Alan Yeh
 * @since 2022/11/04
 */
public class MulticastFetcher implements DataFetcher<MulticastContainer> {

    @Setter
    private BeanSupplier supplier;

    @Getter
    private final Duration timeout = Duration.ofSeconds(30);

    @Override
    public MulticastContainer get() {
        if (supplier == null) {
            return new MulticastContainer();
        }

        var tenantProvider = supplier.get(TenantProvider.class);
        var multicastProvider = supplier.get(MulticastBroadcasterProvider.class);

        // 获取所有租户，然后依次获取该租户下的所有广播器
        var tenants = tenantProvider.findBy(null, null, null, null);

        var broadcasters = new HashMap<String, List<MulticastBroadcaster>>();

        for (var tenant : tenants) {
            if (tenant.getApplications().stream().noneMatch(it -> Objects.equals("central-storage", it.getApplication().getCode()))) {
                // 该租户没有分配存储中心
                continue;
            }
            var data = multicastProvider.findBy(null, null, null, null, tenant.getCode());
            broadcasters.put(tenant.getCode(), data);
        }

        return new MulticastContainer(broadcasters);
    }
}