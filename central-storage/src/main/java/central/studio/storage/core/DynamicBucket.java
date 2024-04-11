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

package central.studio.storage.core;

import central.data.storage.StorageBucket;
import central.lang.Assertx;
import central.lang.Stringx;
import central.lang.reflect.TypeRef;
import central.pluglet.PlugletFactory;
import central.util.Jsonx;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.springframework.beans.factory.DisposableBean;

/**
 * 动态存储桶
 *
 * @author Alan Yeh
 * @since 2022/10/30
 */
public class DynamicBucket implements Bucket, DisposableBean {

    @Getter
    private final StorageBucket data;

    @Delegate(types = Bucket.class)
    private final Bucket delegate;

    private final BucketResolver resolver;

    private final PlugletFactory factory;

    public DynamicBucket(StorageBucket data, BucketResolver resolver, PlugletFactory factory) {
        this.data = data;
        this.resolver = resolver;
        this.factory = factory;

        var type = Assertx.requireNotNull(resolver.resolve(data.getType()), "找不到指定类型的存储桶类型: " + data.getType());

        try {
            var params = Jsonx.Default().deserialize(this.data.getParams(), TypeRef.ofMap(String.class, Object.class));
            this.delegate = this.factory.create(type, params);
        } catch (Exception ex) {
            throw new IllegalStateException(Stringx.format("初始化插件[id={}, type={}]出现异常: " + ex.getLocalizedMessage(), this.data.getId(), this.data.getType()), ex);
        }
    }

    @Override
    public void destroy() throws Exception {
        factory.destroy(this.delegate);
    }
}
