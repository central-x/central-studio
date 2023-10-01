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

package central.provider.scheduled.fetcher.saas;

import central.provider.saas.ApplicationProvider;
import central.provider.saas.TenantProvider;
import central.provider.scheduled.BeanSupplier;
import central.provider.scheduled.fetcher.DataFetcher;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

/**
 * 租户中心数据获取
 *
 * @author Alan Yeh
 * @since 2022/10/13
 */
public class SaasFetcher implements DataFetcher<SaasContainer> {

    @Setter
    private BeanSupplier supplier;

    @Getter
    private final Duration timeout = Duration.ofSeconds(5);

    @Override
    public SaasContainer get() {
        if (supplier == null){
            return new SaasContainer();
        }

        var tenantProvider = this.supplier.get(TenantProvider.class);
        var tenants = tenantProvider.findBy(null, null, null, null);

        var applicationProvider = this.supplier.get(ApplicationProvider.class);
        var applications = applicationProvider.findBy(null, null, null, null);

        return new SaasContainer(tenants, applications);
    }
}
