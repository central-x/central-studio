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

package central.api.scheduled.fetcher.multicast;

import central.api.scheduled.DataContainer;
import central.data.multicast.MulticastBroadcaster;
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
 * 广播中心数据容器
 *
 * @author Alan Yeh
 * @since 2022/11/04
 */
@NoArgsConstructor
public class MulticastContainer extends DataContainer {
    @Serial
    private static final long serialVersionUID = 3072339257568188029L;


    /**
     * 广播器
     * <p>
     * tenant -> id -> broadcaster
     */
    @Getter
    private final Map<String, Map<String, MulticastBroadcaster>> broadcasters = new HashMap<>();

    public MulticastContainer(Map<String, List<MulticastBroadcaster>> buckets) {
        var idMap = buckets.entrySet().stream().map(it -> {
            var value = it.getValue().stream().collect(Collectors.toMap(MulticastBroadcaster::getCode, Function.identity()));
            return Map.entry(it.getKey(), value);
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        this.broadcasters.putAll(idMap);
    }

    /**
     * 获取广播器
     *
     * @param tenant 租户标识
     */
    public List<MulticastBroadcaster> getBroadcasters(String tenant) {
        return new ArrayList<>(this.broadcasters.computeIfAbsent(tenant, key -> new HashMap<>()).values());
    }

    /**
     * 获取广播器
     *
     * @param tenant 租户标识
     * @param id     主键
     */
    public MulticastBroadcaster getBroadcaster(String tenant, String id) {
        return this.broadcasters.computeIfAbsent(tenant, key -> new HashMap<>()).get(id);
    }
}
