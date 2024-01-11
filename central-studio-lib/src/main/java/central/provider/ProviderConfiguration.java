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

package central.provider;

import central.net.http.executor.apache.ApacheHttpClientExecutor;
import central.net.http.processor.impl.SetHeaderProcessor;
import central.net.http.processor.impl.TransmitForwardedProcessor;
import central.net.http.proxy.HttpProxyFactory;
import central.net.http.proxy.contract.spring.SpringContract;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.ScheduledDataContext;
import central.provider.scheduled.SpringBeanSupplier;
import central.provider.scheduled.event.DataRefreshEvent;
import central.starter.graphql.stub.EnableGraphQLStub;
import central.starter.graphql.stub.ProviderClient;
import central.web.XForwardedHeaders;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provider Configuration
 * <p>
 * 数据服务中心配置
 *
 * @author Alan Yeh
 * @since 2023/09/10
 */
@Configuration
@EnableGraphQLStub(packages = "central.provider.graphql")
@EnableConfigurationProperties(ProviderProperties.class)
public class ProviderConfiguration {
    /**
     * 其它数据用的 HTTP 客户端
     */
    @Bean
    public ProviderClient providerClient(ProviderProperties properties) {
        return HttpProxyFactory.builder(ApacheHttpClientExecutor.Default())
                .contact(new SpringContract())
                .processor(new TransmitForwardedProcessor())
                .baseUrl(properties.getUrl() + "/provider")
                .target(ProviderClient.class);
    }

    /**
     * 租户用的 HTTP 客户端
     */
    @Bean
    public ProviderClient masterProviderClient(ProviderProperties properties) {
        return HttpProxyFactory.builder(ApacheHttpClientExecutor.Default())
                .contact(new SpringContract())
                .processor(new TransmitForwardedProcessor())
                .processor(new SetHeaderProcessor(XForwardedHeaders.TENANT, "master"))
                .baseUrl(properties.getUrl() + "/provider")
                .target(ProviderClient.class);
    }

    /**
     * 热数据容器
     */
    @Bean(initMethod = "initialized", destroyMethod = "destroy")
    public DataContext dataContext(ApplicationContext applicationContext, ProviderProperties properties) {
        var context = new ScheduledDataContext(new SpringBeanSupplier(applicationContext));
        for (var type : properties.getFetchers()) {
            // 只获取业务系统需要的数据
            context.addFetcher(type);
        }
        context.addObserver(event -> {
            if (event instanceof ScheduledDataContext.DataRefreshedEvent refreshed) {
                applicationContext.publishEvent(new DataRefreshEvent<>(refreshed.getFetcher().getValue(), refreshed.getContainer()));
            }
        });
        return context;
    }
}
