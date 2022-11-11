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

package central.logging.core.storage.impl.console;

import central.data.log.Log;
import central.data.log.option.LogLevel;
import central.lang.Stringx;
import central.logging.core.storage.Storage;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.util.concurrent.DelayedElement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 控制台
 *
 * @author Alan Yeh
 * @since 2022/10/24
 */
public class ConsoleStorage implements Storage, InitializingBean, DisposableBean {

    @Control(label = "说明", type = ControlType.LABEl, defaultValue = "　　本存储器不保存日志，主要用于在控制台实时监控日志。")
    private String label;

    private final ThreadLocal<SimpleDateFormat> formatter = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));

    private ExecutorService printerPool = null;
    private final DelayQueue<DelayedElement<String>> queue = new DelayQueue<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        this.printerPool = Executors.newFixedThreadPool(1, new CustomizableThreadFactory("central.logging.console.printer@" + this.hashCode()));
        this.printerPool.submit(new Printer(this.queue));
    }

    @Override
    public void destroy() throws Exception {
        if (printerPool != null) {
            printerPool.shutdownNow();
            printerPool = null;
        }
    }

    @Override
    public void store(List<Log> logs) {
        if (this.printerPool == null) {
            // 如果没有打印线程池，就直接丢弃，以免内存溢出
            return;
        }

        for (Log input : logs) {
            StringBuilder output = new StringBuilder();
            output.append("[\033[0;32m").append(input.getService()).append("\033[0m] ");
            if (Stringx.isNotBlank(input.getTraceId())) {
                output.append(input.getTraceId()).append(" ");
            }
            output.append(formatter.get().format(input.getTimestamp())).append(" ");
            switch (LogLevel.resolve(input.getLevel())) {
                case INFO: {
                    output.append("\033[1;32m").append(input.getLevel().toUpperCase()).append("\033[0m ");
                    break;
                }
                case WARN: {
                    output.append("\033[1;33m").append(input.getLevel().toUpperCase()).append("\033[0m ");
                    break;
                }
                case DEBUG: {
                    output.append("\033[1;36m").append(input.getLevel().toUpperCase()).append("\033[0m ");
                    break;
                }
                case ERROR: {
                    output.append("\033[1;31m").append(input.getLevel().toUpperCase()).append("\033[0m ");
                    break;
                }
                default:
            }
            output.append("\033[0;34m").append(input.getPid()).append("\033[0m ").append("[\033[0;35m").append(input.getThread()).append("\033[0m] ");
            output.append("\033[0;36m").append(input.getLocation()).append("\033[0m:\n");
            output.append(input.getContent()).append("\n");

            queue.add(new DelayedElement<>(output.toString(), Duration.ofMillis(Math.max(input.getTimestamp().getTime() + 3000 - System.currentTimeMillis(), 0))));
        }
    }

    @RequiredArgsConstructor
    private static class Printer implements Runnable {

        private final DelayQueue<DelayedElement<String>> queue;

        @Override
        public void run() {
            try {
                while (true) {
                    var output = this.queue.take();
                    System.out.println(output.getElement());
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
