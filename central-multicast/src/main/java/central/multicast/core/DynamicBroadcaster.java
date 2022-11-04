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

package central.multicast.core;

import central.api.client.multicast.MessageBody;
import central.api.client.multicast.body.StandardBody;
import central.data.multicast.MulticastBroadcaster;
import central.lang.Assertx;
import central.lang.Stringx;
import central.lang.reflect.TypeReference;
import central.pluglet.PlugletFactory;
import central.util.Jsonx;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.springframework.beans.factory.DisposableBean;

import java.lang.reflect.ParameterizedType;

/**
 * 动态广播
 *
 * @author Alan Yeh
 * @since 2022/11/03
 */
public class DynamicBroadcaster implements Broadcaster<Object>, DisposableBean {

    @Getter
    private final MulticastBroadcaster data;

    private final PlugletFactory factory;

    private final Broadcaster<Object> delegate;

    @Getter
    private final TypeReference<Object> bodyType;

    public DynamicBroadcaster(MulticastBroadcaster data, PlugletFactory factory) {
        this.data = data;
        this.factory = factory;

        var type = Assertx.requireNotNull(BroadcasterType.resolve(data.getType()), "找不到指定类型的广播器类型: " + data.getType());

        try {
            var params = Jsonx.Default().deserialize(data.getParams(), TypeReference.ofMap(String.class, Object.class));
            this.delegate = this.factory.create(type.getType(), params);
        } catch (Exception ex) {
            throw new IllegalStateException(Stringx.format("初始化插件[id={}, type={}]出现异常: " + ex.getLocalizedMessage(), this.data.getId(), this.data.getType()), ex);
        }

        var parameterizedType = (ParameterizedType) type.getType().getGenericInterfaces()[0];
        this.bodyType = TypeReference.of(parameterizedType.getActualTypeArguments()[0]);
    }

    @Override
    public void destroy() throws Exception {
        factory.destroy(this.delegate);
    }

    @Override
    public void standardPublish(StandardBody body) {
        this.delegate.standardPublish(body);
    }

    @Override
    public void customPublish(Object body) {
        var params = Jsonx.Default().deserialize(Jsonx.Default().serialize(body), this.bodyType);
        this.delegate.customPublish(params);
    }
}
