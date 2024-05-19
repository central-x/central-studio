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

package central.studio.logging.core.collector;

import central.pluglet.PlugletFactory;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Default Collector Resolver
 * <p>
 * 默认采集器类型解析器
 *
 * @author Alan Yeh
 * @since 2024/04/19
 */
@RequiredArgsConstructor
public class DefaultCollectorResolver implements CollectorResolver {

    public final PlugletFactory factory;

    @Override
    public @Nullable Collector resolve(@Nonnull String code, @Nonnull Map<String, Object> params) {
        var type = CollectorType.resolve(code);
        if (type == null) {
            return null;
        }

        return this.instance(type.getType(), params);
    }

    protected Collector instance(@Nonnull Class<? extends Collector> type, @Nonnull Map<String, Object> params) {
        return this.factory.create(type, params);
    }

    @Override
    public void destroy(@Nonnull Collector collector) {
        this.factory.destroy(collector);
    }
}
