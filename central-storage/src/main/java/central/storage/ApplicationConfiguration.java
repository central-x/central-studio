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

package central.storage;

import central.pluglet.PlugletFactory;
import central.pluglet.binder.SpringBeanFieldBinder;
import central.pluglet.lifecycle.SpringLifeCycleProcess;
import central.provider.EnableCentralProvider;
import central.starter.probe.EnableProbe;
import central.storage.core.BucketCache;
import central.storage.core.cache.LocalCache;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Executors;

/**
 * 应用配置
 *
 * @author Alan Yeh
 * @since 2022/10/30
 */
@EnableProbe // 启用探针
@Configuration
@EnableCentralProvider
@EnableConfigurationProperties(ApplicationProperties.class)
public class ApplicationConfiguration {

    @Bean
    public BucketCache bucketCache() throws IOException {
        return new LocalCache(Path.of("cache", "storage"));
    }


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
