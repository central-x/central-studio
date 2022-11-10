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

package central.api.scheduled.fetcher.security;

import central.api.scheduled.DataContainer;
import central.data.security.SecurityStrategy;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 认证中心数据容器
 *
 * @author Alan Yeh
 * @since 2022/11/05
 */
@NoArgsConstructor
public class SecurityContainer extends DataContainer {
    @Serial
    private static final long serialVersionUID = 6940307581203336301L;

    /**
     * 安全策略
     * <p>
     * tenant -> code -> strategy
     */
    @Getter
    private final Map<String, Map<String, SecurityStrategy>> strategies = new HashMap<>();

    public SecurityContainer(Map<String, List<SecurityStrategy>> strategies) {
        var idMap = strategies.entrySet().stream().map(it -> {
            var value = it.getValue().stream().collect(Collectors.toMap(SecurityStrategy::getCode, Function.identity()));
            return Map.entry(it.getKey(), value);
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        this.strategies.putAll(idMap);
    }

    /**
     * 获取安全策略
     *
     * @param tenant 租户标识
     */
    public List<SecurityStrategy> getStrategies(String tenant) {
        return new ArrayList<>(this.strategies.computeIfAbsent(tenant, key -> new HashMap<>()).values());
    }

    /**
     * 获取安全策略
     *
     * @param tenant 租户标识
     * @param code   标识
     */
    public SecurityStrategy getStrategy(String tenant, String code) {
        return this.strategies.computeIfAbsent(tenant, key -> new HashMap<>()).get(code);
    }

}
