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

package central.studio.logging.core.storage;

import central.data.log.Log;
import central.data.log.LogStorage;
import central.lang.Assertx;
import central.lang.Stringx;
import central.lang.reflect.TypeRef;
import central.util.Jsonx;
import lombok.Getter;
import org.springframework.beans.factory.DisposableBean;

import java.util.List;

/**
 * 动态存储器
 *
 * @author Alan Yeh
 * @since 2022/10/25
 */
public class DynamicStorage implements Storage, DisposableBean {

    @Getter
    private final LogStorage data;

    private final Storage delegate;

    private final StorageResolver resolver;

    public DynamicStorage(LogStorage data, StorageResolver resolver) {
        this.data = data;
        this.resolver = resolver;

        try {
            var params = Jsonx.Default().deserialize(this.data.getParams(), TypeRef.ofMap(String.class, Object.class));
            this.delegate = Assertx.requireNotNull(resolver.resolve(data.getType(), params), "找不到指定类型的存储器类型: " + data.getType());
        } catch (Exception ex) {
            throw new IllegalStateException(Stringx.format("初始化插件[id={}, type={}]出现异常: " + ex.getLocalizedMessage(), this.data.getId(), this.data.getType()), ex);
        }
    }

    @Override
    public void destroy() throws Exception {
        this.resolver.destroy(this.delegate);
    }

    @Override
    public void store(List<Log> logs) {
        this.delegate.store(logs);
    }
}
