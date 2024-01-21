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

package central.studio.logging.core.collector.impl.local;

import central.io.Filex;
import central.io.IOStreamx;
import central.lang.Arrayx;
import central.lang.Stringx;
import central.lang.reflect.TypeRef;
import central.pluglet.annotation.Control;
import central.studio.logging.controller.param.LogParams;
import central.studio.logging.core.collector.Collector;
import central.util.Jsonx;
import central.validation.Label;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Setter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 本地日志采集器
 *
 * @author Alan Yeh
 * @since 2024/01/21
 */
public class LocalCollector extends Collector implements InitializingBean, DisposableBean, Runnable {

    @Setter
    @Label("采集路径")
    @Size(min = 1, max = 36)
    @NotBlank
    @Control(label = "采集路径", comment = "待采集的日志存放的路径")
    private String path;

    private ExecutorService executor;

    private File dir;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.dir = new File(this.path);
        this.executor = Executors.newCachedThreadPool(new CustomizableThreadFactory("central.logging.collector.local"));
        this.executor.submit(this);
    }

    @Override
    public void destroy() throws Exception {
        if (!this.executor.isShutdown()) {
            this.executor.shutdown();
            if (!this.executor.awaitTermination(30, java.util.concurrent.TimeUnit.SECONDS)) {
                // 30 秒后线程池还没能结束，则强制结束
                this.executor.shutdownNow();
            }
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                // 每隔 1 秒查询是否有新的日志
                Thread.sleep(Duration.ofSeconds(1).toMillis());

                try {
                    // 查询指定目录是否存在
                    if (!this.dir.exists()) {
                        continue;
                    }

                    // 有可能多个线程去处理遗留的日志时，可能会导致冲突
                    // 这里使用文件作为锁
                    var lock = new File(this.dir, ".lock");

                    if (lock.exists()) {
                        // 锁已经存在，则检查锁的时间
                        var dateStr = IOStreamx.readText(Files.newInputStream(lock.toPath(), StandardOpenOption.READ), StandardCharsets.UTF_8);
                        if (Stringx.isNullOrBlank(dateStr)) {
                            // 锁里面没有内容
                            Filex.delete(lock);
                        } else {
                            try {
                                // 如果文件锁存在，检查一下锁的创建时间
                                var date = OffsetDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
                                var now = OffsetDateTime.now();

                                if (date.plusMinutes(10).isBefore(now)) {
                                    // 锁创建时间超过 10 分钟了，删除该锁
                                    Filex.delete(lock);
                                }
                                if (date.isAfter(now)) {
                                    // 这个创建时间比当前时间还晚，说明锁无效
                                    Filex.delete(lock);
                                }
                            } catch (IOException ignored) {
                                // 解析日志异常
                                Filex.delete(lock);
                            }
                        }
                    }

                    if (lock.exists()) {
                        // 锁已经存在了，则其它线程正在处理，因此跳过
                        continue;
                    }

                    // 判断是否持有文件锁
                    boolean obtained = false;

                    try {
                        obtained = lock.createNewFile();
                        if (obtained) {
                            Filex.writeText(lock, OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

                            var tmps = Arrayx.asStream(this.dir.listFiles()).filter(it -> it.getName().endsWith(".logtmp")).toList();

                            for (var tmp : tmps) {
                                if (tmp.length() > 0) {
                                    // 文件有内容才发送
                                    try {
                                        var body = IOStreamx.readText(Files.newInputStream(tmp.toPath(), StandardOpenOption.READ), StandardCharsets.UTF_8);
                                        var params = Jsonx.Default().deserialize(body, TypeRef.ofList(LogParams.class));
                                        if (!params.isEmpty()) {
                                            this.collect(params.stream().map(LogParams::toData).toList());
                                        }
                                    } catch (Exception ignored) {
                                    }
                                }
                                Filex.delete(tmp);
                                // 稍微卡一下再继续读取
                                Thread.sleep(100);
                            }
                        }
                    } finally {
                        if (obtained) {
                            Filex.delete(lock);
                        }
                    }
                } catch (Throwable ignored) {
                    // 忽略异常
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
