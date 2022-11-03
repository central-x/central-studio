package central.provider;

import central.api.scheduled.ScheduledDataContext;
import central.api.scheduled.SpringSupplier;
import central.api.scheduled.event.DataRefreshEvent;
import central.api.scheduled.fetcher.DataFetchers;
import central.net.http.executor.okhttp.OkHttpExecutor;
import central.net.http.processor.impl.AddHeaderProcessor;
import central.net.http.proxy.HttpProxyFactory;
import central.net.http.proxy.contract.spring.SpringContract;
import central.sql.datasource.dynamic.lookup.LookupKeyFilter;
import central.starter.graphql.stub.EnableGraphQLStub;
import central.starter.graphql.stub.ProviderClient;
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
    public ScheduledDataContext dataContext(ApplicationContext applicationContext) {
        var context = new ScheduledDataContext(5000, new SpringSupplier(applicationContext));
        context.addFetcher(DataFetchers.SAAS);
        context.addFetcher(DataFetchers.LOG, (tenant, container) -> {
            applicationContext.publishEvent(new DataRefreshEvent<>(tenant, container));
        });
        return context;
    }
}
