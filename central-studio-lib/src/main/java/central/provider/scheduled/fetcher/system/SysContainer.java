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

package central.provider.scheduled.fetcher.system;

import central.provider.scheduled.DataContainer;
import central.data.system.Dictionary;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统数据容器
 *
 * @author Alan Yeh
 * @since 2022/10/14
 */
@RequiredArgsConstructor
public class SysContainer extends DataContainer {
    @Serial
    private static final long serialVersionUID = 7527120121005302705L;

    /**
     * 字典
     * tenantCode -> applicationCode -> dictionaryCode -> Dictionary
     */
    private final Map<String, Map<String, Map<String, Dictionary>>> dictionaries;

    public SysContainer() {
        this.dictionaries = new HashMap<>();
    }

    /**
     * 获取字典
     *
     * @param tenantCode      租户标识
     * @param applicationCode 应用标识
     * @param dictionaryCode  字典标识
     */
    public Dictionary getDictionary(String tenantCode, String applicationCode, String dictionaryCode) {
        return this.dictionaries.computeIfAbsent(tenantCode, key -> new HashMap<>())
                .computeIfAbsent(applicationCode, key -> new HashMap<>())
                .get(dictionaryCode);
    }
}
