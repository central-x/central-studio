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

package central.logging.core.collector.impl.http;

import central.logging.core.collector.Collector;
import central.pluglet.annotation.Control;
import central.validation.Label;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Setter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Http 日志收集器
 *
 * @author Alan Yeh
 * @since 2022/10/24
 */
public class HttpCollector extends Collector implements ApplicationContextAware, InitializingBean, DisposableBean, ApplicationListener<HttpEvent> {

    @Setter
    @Label("接口路径")
    @Size(min = 1, max = 36)
    @NotBlank
    @Control(label = "接口路径", comment = "上传日志时使用的接口路径")
    private String path;

    @Setter
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 动态添加监听
        if (applicationContext instanceof AbstractApplicationContext context) {
            context.addApplicationListener(this);
        }
    }

    @Override
    public void destroy() throws Exception {
        if (applicationContext instanceof AbstractApplicationContext context) {
            context.getApplicationListeners().remove(this);
        }
        var multicaster = this.applicationContext.getBean(AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME, ApplicationEventMulticaster.class);
        multicaster.removeApplicationListener(this);
    }

    @Override
    public void onApplicationEvent(@Nonnull HttpEvent event) {
        if (Objects.equals(this.path, event.getPath())) {
            this.collect(event.getLogs());
        }
    }
}
