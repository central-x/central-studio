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

package central.api.scheduled.fetcher.ten;

import central.api.provider.ten.ApplicationProvider;
import central.api.provider.ten.TenantProvider;
import central.api.scheduled.ProviderSupplier;
import central.api.scheduled.fetcher.DataFetcher;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

/**
 * 租户数据
 *
 * @author Alan Yeh
 * @since 2022/10/13
 */
public class TenantFetcher implements DataFetcher<TenantContainer> {

    @Setter
    private ProviderSupplier providerSupplier;

    @Getter
    private final long timeout = Duration.ofSeconds(5).toMillis();

    @Override
    public TenantContainer get() {
        if (providerSupplier == null){
            return new TenantContainer();
        }

        var tenantProvider = this.providerSupplier.get(TenantProvider.class);
        var tenants = tenantProvider.findBy(null, null, null, null);

        var applicationProvider = this.providerSupplier.get(ApplicationProvider.class);
        var applications = applicationProvider.findBy(null, null, null, null);

        return new TenantContainer(tenants, applications);
    }
}
