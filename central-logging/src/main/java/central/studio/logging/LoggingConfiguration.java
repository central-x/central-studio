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

package central.studio.logging;

import central.pluglet.PlugletFactory;
import central.provider.EnableCentralProvider;
import central.starter.ability.EnablePluglet;
import central.starter.probe.EnableProbe;
import central.studio.logging.core.collector.CollectorResolver;
import central.studio.logging.core.collector.DefaultCollectorResolver;
import central.studio.logging.core.filter.predicate.DefaultPredicateResolver;
import central.studio.logging.core.filter.predicate.PredicateResolver;
import central.studio.logging.core.storage.DefaultStorageResolver;
import central.studio.logging.core.storage.StorageResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 日志中心配置
 *
 * @author Alan Yeh
 * @since 2022/10/24
 */
@EnableProbe // 启用探针
@Configuration
@EnablePluglet
@EnableCentralProvider
@ComponentScan("central.studio.logging")
@EnableConfigurationProperties(LoggingProperties.class)
public class LoggingConfiguration {

    /**
     * 采集器解析
     */
    @Bean
    @ConditionalOnMissingBean(CollectorResolver.class)
    public CollectorResolver collectorResolver(PlugletFactory factory) {
        return new DefaultCollectorResolver(factory);
    }

    /**
     * 断言解析
     */
    @Bean
    @ConditionalOnMissingBean(PredicateResolver.class)
    public PredicateResolver predicateResolver(PlugletFactory factory) {
        return new DefaultPredicateResolver(factory);
    }

    /**
     * 存储器解析
     */
    @Bean
    @ConditionalOnMissingBean(StorageResolver.class)
    public StorageResolver storageResolver(PlugletFactory factory) {
        return new DefaultStorageResolver(factory);
    }
}
