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

package central.logging.core.storage.impl.file;

import central.data.log.Log;
import central.io.Filex;
import central.lang.Stringx;
import central.util.concurrent.BlockedQueue;
import central.util.concurrent.ConsumableQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 日志文件写入
 *
 * @author Alan Yeh
 * @since 2022/10/24
 */
@Slf4j
@RequiredArgsConstructor
public class FileAppender implements InitializingBean, DisposableBean {

    private final RollingPolicy policy;

    private ConsumableQueue<Log, BlockedQueue<Log>> queue;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.queue = new ConsumableQueue<>(new BlockedQueue<>(new PriorityQueue<>(Comparator.comparing(Log::getTimestamp))));
        this.queue.addConsumer(new Writer(this.policy));
    }

    @Override
    public void destroy() throws Exception {
        if (this.queue != null) {
            this.queue.close();
            this.queue = null;
        }
    }

    public void append(List<Log> logs) {
        this.queue.addAll(logs);
    }

    @RequiredArgsConstructor
    private static class Writer implements Consumer<BlockedQueue<Log>> {

        private final RollingPolicy policy;

        private static final ThreadLocal<SimpleDateFormat> FORMATTER = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));

        @Override
        public void accept(BlockedQueue<Log> queue) {
            try {
                while (true) {
                    var logs = queue.poll(1000, 1, TimeUnit.SECONDS);
                    try {
                        // 根据策略获取待写入的文件
                        var logFile = this.policy.getFile();

                        // 组装日志
                        StringBuilder output = new StringBuilder();
                        for (Log l : logs) {
                            output.append("[").append(l.getService()).append("] ");
                            if (Stringx.isNotBlank(l.getTraceId())) {
                                output.append(l.getTraceId()).append(" ");
                            }
                            output.append(FORMATTER.get().format(l.getTimestamp())).append(" ");
                            output.append(l.getLevel().toUpperCase()).append(" ")
                                    .append(l.getPid()).append(" ")
                                    .append(l.getThread()).append(" ")
                                    .append(l.getLocation()).append("\r\n");
                            output.append(l.getContent()).append("\r\n\r\n");
                        }

                        // 写入文件
                        Filex.appendText(logFile, output.toString(), StandardCharsets.UTF_8);

                        // 写入文件之后，对文件进行滚动
                        policy.roll();
                    } catch (IOException ex) {
                        log.error("无法写入日志: " + ex.getLocalizedMessage(), ex);
                    }
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
