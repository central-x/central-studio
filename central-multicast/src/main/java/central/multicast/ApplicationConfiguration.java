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

import central.api.scheduled.ScheduledDataContext;
import central.api.scheduled.SpringSupplier;
import central.api.scheduled.event.DataRefreshEvent;
import central.api.scheduled.fetcher.DataFetchers;
import central.net.http.executor.okhttp.OkHttpExecutor;
import central.net.http.processor.impl.SetHeaderProcessor;
import central.net.http.processor.impl.TransmitForwardedProcessor;
import central.net.http.proxy.HttpProxyFactory;
import central.net.http.proxy.contract.spring.SpringContract;
import central.pluglet.PlugletFactory;
import central.pluglet.binder.SpringBeanFieldBinder;
import central.pluglet.lifecycle.SpringLifeCycleProcess;
import central.starter.graphql.stub.EnableGraphQLStub;
import central.starter.graphql.stub.ProviderClient;
import central.web.XForwardedHeaders;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.Executors;

/**
 * 应用配置
 *
 * @author Alan Yeh
 * @since 2022/11/03
 */
@Configuration
@EnableGraphQLStub(packages = "central.api")
@EnableConfigurationProperties(ApplicationProperties.class)
public class ApplicationConfiguration {

    /**
     * 异步事件发布
     */
    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster() {
        var multicaster = new SimpleApplicationEventMulticaster();

        var service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new CustomizableThreadFactory("central-logging.multicaster"));
        multicaster.setTaskExecutor(service);
        return multicaster;
    }

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
    public ScheduledDataContext dataContext(ApplicationContext applicationContext) {
        var context = new ScheduledDataContext(5000, new SpringSupplier(applicationContext));
        context.addFetcher(DataFetchers.SAAS);
        context.addFetcher(DataFetchers.MULTICAST, (tenant, container) -> {
            applicationContext.publishEvent(new DataRefreshEvent<>(tenant, container));
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
