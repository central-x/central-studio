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

package central.api.scheduled.fetcher.storage;

import central.api.provider.storage.StorageBucketProvider;
import central.api.provider.saas.TenantProvider;
import central.api.scheduled.ProviderSupplier;
import central.api.scheduled.fetcher.DataFetcher;
import central.data.storage.StorageBucket;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 存储中心数据
 *
 * @author Alan Yeh
 * @since 2022/10/30
 */
public class StorageFetcher implements DataFetcher<StorageContainer> {

    @Setter
    private ProviderSupplier providerSupplier;

    @Getter
    private final long timeout = Duration.ofSeconds(30).toMillis();

    @Override
    public StorageContainer get() {
        if (providerSupplier == null) {
            return new StorageContainer();
        }

        var tenantProvider = providerSupplier.get(TenantProvider.class);
        var bucketProvider = providerSupplier.get(StorageBucketProvider.class);

        // 获取所有租户，然后依次获取该租户下的所有存储桶
        var tenants = tenantProvider.findBy(null, null, null, null);

        var buckets = new HashMap<String, List<StorageBucket>>();

        for (var tenant : tenants) {
            if (tenant.getApplications().stream().noneMatch(it -> Objects.equals("central-storage", it.getApplication().getCode()))) {
                // 该租户没有分配存储中心
                continue;
            }
            var data = bucketProvider.findBy(null, null, null, null, tenant.getCode());
            buckets.put(tenant.getCode(), data);
        }

        return new StorageContainer(buckets);
    }
}
