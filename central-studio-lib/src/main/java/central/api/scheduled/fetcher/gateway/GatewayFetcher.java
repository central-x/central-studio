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

package central.api.scheduled.fetcher.gateway;

import central.api.provider.gateway.GatewayFilterProvider;
import central.api.provider.saas.TenantProvider;
import central.api.scheduled.BeanSupplier;
import central.api.scheduled.fetcher.DataFetcher;
import central.data.gateway.GatewayFilter;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 网关中心数据获取
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
public class GatewayFetcher implements DataFetcher<GatewayContainer> {
    @Setter
    private BeanSupplier supplier;

    @Getter
    private final Duration timeout = Duration.ofSeconds(5);

    @Override
    public GatewayContainer get() {
        if (supplier == null) {
            return new GatewayContainer();
        }

        var tenantProvider = supplier.get(TenantProvider.class);
        var filterProvider = supplier.get(GatewayFilterProvider.class);

        // 获取所有租户，然后依次获取该租户下的所有过滤器
        var tenants = tenantProvider.findBy(null, null, null, null);

        var filters = new HashMap<String, List<GatewayFilter>>();

        for (var tenant : tenants) {
            if (tenant.getApplications().stream().noneMatch(it -> Objects.equals("central-gateway", it.getApplication().getCode()))) {
                // 该租户没有分配网关中心
                continue;
            }
            var data = filterProvider.findBy(null, null, null, null, tenant.getCode());
            filters.put(tenant.getCode(), data);
        }

        return new GatewayContainer(filters);

    }
}
