package central.provider;

import central.sql.datasource.dynamic.lookup.LookupKeyFilter;
import central.starter.graphql.EnableGraphQL;
import central.starter.logging.EnableLogPoint;
import central.starter.orm.EnableOrm;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * 应用配置
 *
 * @author Alan Yeh
 * @since 2022/07/07
 */
@EnableOrm
@EnableGraphQL
@EnableLogPoint
@EnableCentralProvider
@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
public class ApplicationConfiguration {

    @Bean
    public FilterRegistrationBean<LookupKeyFilter> getLookupKeyFilter() {
        var bean = new FilterRegistrationBean<LookupKeyFilter>();
        bean.setFilter(new LookupKeyFilter());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
