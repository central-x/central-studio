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

package central.multicast;

import central.api.client.multicast.MessageClient;
import central.net.http.executor.okhttp.OkHttpExecutor;
import central.net.http.processor.impl.ThrowProcessor;
import central.net.http.proxy.HttpProxyFactory;
import central.net.http.proxy.contract.spring.SpringContract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 测试配置
 *
 * @author Alan Yeh
 * @since 2022/11/04
 */
@Configuration
public class TestConfiguration {
    @Bean
    public MessageClient messageClient(@Value("${server.port}") int port) {
        return HttpProxyFactory.builder(OkHttpExecutor.Default())
                .contact(new SpringContract())
                .baseUrl("http://127.0.0.1:" + port)
                .processor(new ThrowProcessor())
                .target(MessageClient.class);
    }
}
