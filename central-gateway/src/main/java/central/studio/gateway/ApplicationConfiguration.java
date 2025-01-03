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

package central.studio.gateway;

import central.pluglet.PlugletFactory;
import central.pluglet.binder.SpringBeanFieldBinder;
import central.pluglet.lifecycle.SpringLifeCycleProcess;
import central.provider.EnableCentralProvider;
import central.starter.probe.EnableProbe;
import central.studio.gateway.core.filter.DefaultFilterResolver;
import central.studio.gateway.core.filter.FilterResolver;
import central.studio.gateway.core.filter.predicate.DefaultPredicateResolver;
import central.studio.gateway.core.filter.predicate.PredicateResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
@EnableProbe
@Configuration
@EnableCentralProvider
@EnableConfigurationProperties(ApplicationProperties.class)
public class ApplicationConfiguration {

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

    /**
     * 过滤器类型解析器
     */
    @Bean
    @ConditionalOnMissingBean(FilterResolver.class)
    public FilterResolver filterResolver(PlugletFactory factory) {
        return new DefaultFilterResolver(factory);
    }

    /**
     * 路由断言类型解析器
     */
    @Bean
    @ConditionalOnMissingBean(PredicateResolver.class)
    public PredicateResolver predicateResolver(PlugletFactory factory) {
        return new DefaultPredicateResolver(factory);
    }
}
