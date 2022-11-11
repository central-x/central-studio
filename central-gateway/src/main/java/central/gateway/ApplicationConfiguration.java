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

package central.gateway;

import central.api.scheduled.DataContext;
import central.api.scheduled.event.DataRefreshEvent;
import central.api.scheduled.fetcher.DataFetcherType;
import central.api.scheduled.ScheduledDataContext;
import central.api.scheduled.SpringBeanSupplier;
import central.net.http.executor.okhttp.OkHttpExecutor;
import central.net.http.processor.impl.SetHeaderProcessor;
import central.net.http.processor.impl.TransmitForwardedProcessor;
import central.net.http.proxy.HttpProxyFactory;
import central.net.http.proxy.contract.spring.SpringContract;
import central.pluglet.PlugletFactory;
import central.pluglet.binder.SpringBeanFieldBinder;
import central.pluglet.lifecycle.SpringLifeCycleProcess;
import central.starter.graphql.stub.ProviderClient;
import central.web.XForwardedHeaders;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 应用配置
 *
 * @author Alan Yeh
 * @since 2022/10/09
 */
@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
public class ApplicationConfiguration {
    /**
     * 其它数据用的 HTTP 客户端
     */
    @Bean
    public ProviderClient providerClient() {
        return HttpProxyFactory.builder(OkHttpExecutor.Default())
                .contact(new SpringContract())
                .processor(new TransmitForwardedProcessor())
                .baseUrl("http://127.0.0.1:3200/provider")
                .target(ProviderClient.class);
    }

    /**
     * 租户用的 HTTP 客户端
     */
    @Bean
    public ProviderClient masterProviderClient() {
        return HttpProxyFactory.builder(OkHttpExecutor.Default())
                .contact(new SpringContract())
                .processor(new TransmitForwardedProcessor())
                .processor(new SetHeaderProcessor(XForwardedHeaders.TENANT, "master"))
                .baseUrl("http://127.0.0.1:3200/provider")
                .target(ProviderClient.class);
    }

    /**
     * 热数据容器
     */
    @Bean(initMethod = "initialized", destroyMethod = "destroy")
    public DataContext dataContext(ApplicationContext applicationContext) {
        var context = new ScheduledDataContext(new SpringBeanSupplier(applicationContext));
        context.addFetcher(DataFetcherType.SAAS);
        context.addFetcher(DataFetcherType.GATEWAY);
        context.addObserver(event -> {
            if (event instanceof ScheduledDataContext.DataRefreshedEvent refreshed) {
                applicationContext.publishEvent(new DataRefreshEvent<>(refreshed.getFetcher().getCode(), refreshed.getContainer()));
            }
        });
        return context;
    }

    /**
     * 插件工厂
     */
    @Bean
    public PlugletFactory plugletFactory(ApplicationContext applicationContext) {
        var factory = new PlugletFactory();
        factory.registerBinder(new SpringBeanFieldBinder(applicationContext));
        factory.registerLifeCycleProcessor(new SpringLifeCycleProcess(applicationContext));
        return factory;
    }
}
