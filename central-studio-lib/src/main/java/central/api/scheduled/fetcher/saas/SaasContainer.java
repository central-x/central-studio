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

package central.api.scheduled.fetcher.saas;

import central.api.scheduled.DataContainer;
import central.data.saas.Application;
import central.data.saas.Tenant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.*;

/**
 * 租户类数据容器
 *
 * @author Alan Yeh
 * @since 2022/10/13
 */
@RequiredArgsConstructor
public class SaasContainer extends DataContainer {
    @Serial
    private static final long serialVersionUID = -1728549119085105086L;

    @Getter
    private final List<Tenant> tenants;

    @Getter
    private final List<Application> applications;

    public SaasContainer() {
        this.tenants = Collections.emptyList();
        this.applications = Collections.emptyList();
    }

    /**
     * 根据标识查询租户数据
     *
     * @param code 租户标识
     * @return 租户数据
     */
    public Tenant getTenantByCode(String code) {
        return this.tenants.stream().filter(it -> Objects.equals(code, it.getCode())).findFirst().orElse(null);
    }

    /**
     * 根据标识查询应用系统数据
     *
     * @param code 应用标识
     * @return 应用数据
     */
    public Application getApplicationByCode(String code) {
        return this.applications.stream().filter(it -> Objects.equals(code, it.getCode())).findFirst().orElse(null);
    }

    /**
     * 根据主键查询应用系统
     *
     * @param id 主键
     * @return 应用
     */
    public Application getApplicationById(String id) {
        return this.applications.stream().filter(it -> Objects.equals(id, it.getId())).findFirst().orElse(null);
    }
}
