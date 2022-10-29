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

package central.security.core.strategy.global;

import central.lang.Stringx;
import central.security.core.SecurityExchange;
import central.security.core.strategy.GlobalStrategy;
import central.security.core.strategy.StrategyChain;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求执行策略
 *
 * @author Alan Yeh
 * @since 2022/10/19
 */
@Component
@Order
public class RequestExecutingStrategy implements GlobalStrategy, ApplicationContextAware {

    @Setter
    private ApplicationContext applicationContext;

    private final Map<String, Object> beanCache = new ConcurrentHashMap<>();

    @Override
    public void execute(SecurityExchange exchange, StrategyChain chain) {
        var action = exchange.getRequest().getAction();
        action.setBeanCache(this.beanCache);
        action.setApplicationContext(applicationContext);
        if (action instanceof InitializingBean bean) {
            try {
                bean.afterPropertiesSet();
            } catch (Exception ex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Stringx.format("初始化执行器[{}]出异常: " + ex.getLocalizedMessage(), action.getClass().getSimpleName()), ex);
            }
        }
        action.execute(exchange);
    }
}
