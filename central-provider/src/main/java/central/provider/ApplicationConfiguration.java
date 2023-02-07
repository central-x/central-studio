package central.provider;

import central.api.scheduled.DataContext;
import central.api.scheduled.ScheduledDataContext;
import central.api.scheduled.SpringBeanSupplier;
import central.api.scheduled.event.DataRefreshEvent;
import central.api.scheduled.fetcher.DataFetcherType;
import central.net.http.executor.okhttp.OkHttpExecutor;
import central.net.http.processor.impl.AddHeaderProcessor;
import central.net.http.proxy.HttpProxyFactory;
import central.net.http.proxy.contract.spring.SpringContract;
import central.sql.datasource.dynamic.lookup.LookupKeyFilter;
import central.starter.graphql.stub.EnableGraphQLStub;
import central.starter.graphql.stub.ProviderClient;
import central.starter.logging.EnableLogPoint;
import central.web.XForwardedHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * 应用配置
 *
 * @author Alan Yeh
 * @since 2022/07/07
 */
@Configuration
@EnableLogPoint
@EnableGraphQLStub(packages = "central.api")
@EnableConfigurationProperties(ApplicationProperties.class)
public class ApplicationConfiguration {

    @Bean
    public FilterRegistrationBean<LookupKeyFilter> getLookupKeyFilter() {
        var bean = new FilterRegistrationBean<LookupKeyFilter>();
        bean.setFilter(new LookupKeyFilter());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    public ProviderClient masterProviderClient(@Value("${server.port}") int port) {
        return HttpProxyFactory.builder(OkHttpExecutor.Default())
                .contact(new SpringContract())
                .processor(new AddHeaderProcessor(XForwardedHeaders.TENANT, "master"))
                .baseUrl("http://127.0.0.1:" + port + "/provider")
                .target(ProviderClient.class);
    }

    @Bean
    public ProviderClient providerClient(@Value("${server.port}") int port) {
        return HttpProxyFactory.builder(OkHttpExecutor.Default())
                .contact(new SpringContract())
                .processor(new AddHeaderProcessor(XForwardedHeaders.TENANT, "master"))
                .baseUrl("http://127.0.0.1:" + port + "/provider")
                .target(ProviderClient.class);
    }

    /**
     * 热数据容器
     */
    @Bean(initMethod = "initialized", destroyMethod = "destroy")
    public DataContext dataContext(ApplicationContext applicationContext) {
        var context = new ScheduledDataContext(new SpringBeanSupplier(applicationContext));
        context.addFetcher(DataFetcherType.SAAS);
        context.addFetcher(DataFetcherType.LOG);
        context.addObserver(event -> {
            if (event instanceof ScheduledDataContext.DataRefreshedEvent refreshed) {
                applicationContext.publishEvent(new DataRefreshEvent<>(refreshed.getFetcher().getCode(), refreshed.getContainer()));
            }
        });
        return context;
    }
}
