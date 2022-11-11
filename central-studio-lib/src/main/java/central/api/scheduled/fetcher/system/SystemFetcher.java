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

package central.api.scheduled.fetcher.system;

import central.api.provider.system.DictionaryProvider;
import central.api.provider.saas.TenantProvider;
import central.api.scheduled.BeanSupplier;
import central.api.scheduled.fetcher.DataFetcher;
import central.data.system.Dictionary;
import central.sql.Conditions;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统数据
 *
 * @author Alan Yeh
 * @since 2022/10/14
 */
public class SystemFetcher implements DataFetcher<SysContainer> {

    @Setter
    private BeanSupplier supplier;

    @Getter
    private final Duration timeout = Duration.ofSeconds(30);

    @Override
    public SysContainer get() {
        if (supplier == null) {
            return new SysContainer();
        }

        var tenantProvider = supplier.get(TenantProvider.class);
        var dictionaryProvider = supplier.get(DictionaryProvider.class);

        var container = new HashMap<String, Map<String, Map<String, Dictionary>>>();

        // 获取所有租户，然后依次获取该租户下所有的字典
        var tenants = tenantProvider.findBy(null, null, null, null);

        for (var tenant : tenants) {
            var dictionaries = dictionaryProvider.findBy(null, null,
                    Conditions.of(Dictionary.class).eq(Dictionary::getEnabled, Boolean.TRUE),
                    null, tenant.getCode());
            // 把查出来的字典放到容器里
            dictionaries.forEach(dictionary -> container.computeIfAbsent(tenant.getCode(), key -> new HashMap<>())
                    .computeIfAbsent(dictionary.getApplication().getCode(), key -> new HashMap<>())
                    .put(dictionary.getCode(), dictionary));
        }

        return new SysContainer(container);
    }
}
