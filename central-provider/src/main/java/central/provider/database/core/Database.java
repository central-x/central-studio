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

package central.provider.database.core;

import central.data.system.DatabaseProperties;
import central.sql.datasource.factory.DataSourceFactory;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Closeable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 数据库插件
 *
 * @author Alan Yeh
 * @since 2022/11/12
 */
public abstract class Database implements InitializingBean {

    @Setter(onMethod_ = @Autowired)
    private DataSourceFactory factory;

    @Override
    public void afterPropertiesSet() throws Exception {
        var properties = this.getProperties();

        var executor = Executors.newFixedThreadPool(1);
        // 测试数据源是否可以连接
        try {
            Throwable exception;
            try {
                exception = executor.submit(() -> {
                    var datasource = factory.build(properties.getDriver(), properties.getUrl(), properties.getUsername(), properties.getPassword());
                    try {
                        try (var connection = datasource.getConnection()) {
                            if (!connection.isValid(100)) {
                                return new RuntimeException("无法连接数据库");
                            }
                            return null;
                        }
                    } catch (Exception ex) {
                        return ex;
                    } finally {
                        if (datasource instanceof Closeable closeable) {
                            closeable.close();
                        }
                    }
                }).get(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                exception = ex;
            }

            if (exception != null) {
                throw new IllegalArgumentException("数据库配置无效: " + exception.getLocalizedMessage());
            }
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * 构建数据库属性
     */
    public abstract DatabaseProperties getProperties();
}
