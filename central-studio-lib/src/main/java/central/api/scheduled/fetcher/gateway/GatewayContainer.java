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

import central.api.scheduled.DataContainer;
import central.data.gateway.GatewayFilter;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网关中心数据容器
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
@NoArgsConstructor
public class GatewayContainer extends DataContainer {
    @Serial
    private static final long serialVersionUID = -6116018017073023899L;

    /**
     * 过滤器
     * <p>
     * tenant -> filters
     */
    @Getter
    private final Map<String, List<GatewayFilter>> filters = new HashMap<>();

    public GatewayContainer(Map<String, List<GatewayFilter>> filters) {
        this.filters.putAll(filters);
    }

    /**
     * 获取过滤器
     *
     * @param tenant 租户标识
     */
    public List<GatewayFilter> getFilters(String tenant) {
        return new ArrayList<>(this.filters.computeIfAbsent(tenant, key -> new ArrayList<>()));
    }
}
