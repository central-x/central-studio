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

package central.identity;

import central.identity.client.SessionClient;
import central.net.http.executor.apache.ApacheHttpClientExecutor;
import central.net.http.processor.impl.TransmitForwardedProcessor;
import central.net.http.proxy.HttpProxyFactory;
import central.net.http.proxy.contract.spring.SpringContract;
import central.starter.identity.IdentityProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Identity Configuration
 * <p>
 * 认证中心配置
 *
 * @author Alan Yeh
 * @since 2023/09/17
 */
@Configuration
@EnableConfigurationProperties(IdentityProperties.class)
public class IdentityConfiguration {

    /**
     * 会话控制客户端
     */
    @Bean
    public SessionClient sessionClient(IdentityProperties properties) {
        return HttpProxyFactory.builder(ApacheHttpClientExecutor.Default())
                .contact(new SpringContract())
                .processor(new TransmitForwardedProcessor())
                .baseUrl(properties.getUrl())
                .target(SessionClient.class);
    }

    @Configuration
    @ConditionalOnClass(IdentityProvider.class)
    public static class IdentityStarterConfiguration {
        /**
         * 应用校验器
         */
        @Bean
        public IdentityProvider identityProvider(){
            return new DefaultIdentityProvider();
        }
    }
}
