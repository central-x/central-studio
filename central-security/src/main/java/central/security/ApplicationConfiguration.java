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

package central.security;

import central.pluglet.PlugletFactory;
import central.pluglet.binder.SpringBeanFieldBinder;
import central.pluglet.lifecycle.SpringLifeCycleProcess;
import central.provider.EnableCentralProvider;
import central.security.client.SessionVerifier;
import central.security.signer.KeyPair;
import central.util.cache.CacheRepository;
import central.util.cache.memory.MemoryCacheRepository;
import central.util.cache.redis.RedisCacheRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 应用配置
 *
 * @author Alan Yeh
 * @since 2022/10/19
 */
@Configuration
@EnableCentralProvider
@EnableCentralSecurity
@EnableConfigurationProperties(ApplicationProperties.class)
public class ApplicationConfiguration {

    /**
     * 签发会话时使用的安全密钥
     */
    @Bean
    public KeyPair keyPair() {
        return Signerx.RSA.generateKeyPair();
    }

    @Bean
    public SessionVerifier sessionVerifier() {
        return new SessionVerifier();
    }

    /**
     * 缓存仓库
     */
    @Bean
    @ConditionalOnMissingBean(CacheRepository.class)
    public CacheRepository memoryCacheRepository() {
        return new MemoryCacheRepository();
    }

    /**
     * 缓存仓库
     */
    @Bean
    @ConditionalOnProperty(name = "central.security.cache.type", havingValue = "redis")
    public CacheRepository redisCacheRepository() {
        return new RedisCacheRepository();
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
