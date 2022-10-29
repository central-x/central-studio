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
import central.logging.core.storage.Storage;
import central.logging.core.storage.impl.file.polocy.SizeRollingPolicy;
import central.logging.core.storage.impl.file.polocy.TimeRollingPolicy;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.validation.Label;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * 文件存储
 *
 * @author Alan Yeh
 * @since 2022/10/24
 */
public class FileStorage implements Storage, InitializingBean, DisposableBean {

    @Control(label = "说明", type = ControlType.LABEl, defaultValue = """
            本存储器会将日志保存到指定的文件路径中。""")
    private String label;

    @Label("保存路径")
    @NotBlank
    @Control(label = "保存路径", defaultValue = "./logs", comment = "日志存储目录。请确保该目录有足够的磁盘空间")
    private String path;

    @Label("滚动策略")
    @NotNull
    @Control(label = "滚动策略", type = ControlType.RADIO, defaultValue = "daily", comment = "日志滚动策略。无论选择哪种日志策略，都会以日期为文件夹进行保存。")
    private RollingPolicyEnum rollingPolicy;

    @Label("压缩策略")
    @NotNull
    @Control(label = "压缩策略", type = ControlType.RADIO, defaultValue = "gzip", comment = "日志文件滚动时的压缩策略")
    private CompressPolicyEnum compressPolicy;

    @Label("文件大小")
    @NotNull
    @Min(10)
    @Max(2048)
    @Control(label = "文件大小", type = ControlType.NUMBER, defaultValue = "1024", comment = "单个日志文件最大大小（MB）")
    private Integer maxSize;

    @Label("保留历史")
    @Max(3650)
    @Min(1)
    @NotNull
    @Control(label = "保留历史", type = ControlType.NUMBER, defaultValue = "7", comment = "保留指定天数的日志")
    private Integer maxHistory;

    private FileAppender appender;

    @Override
    public void afterPropertiesSet() throws Exception {
        RollingPolicy policy;

        switch (this.rollingPolicy) {
            case HOURLY -> {
                policy = new TimeRollingPolicy(this.path, this.maxHistory, this.maxSize * 1024 * 1024, "yyyy-MM-dd_HH", this.compressPolicy.getCompressor());
            }
            case DAILY -> {
                policy = new TimeRollingPolicy(this.path, this.maxHistory, this.maxSize * 1024 * 1024, "yyyy-MM-dd", this.compressPolicy.getCompressor());
            }
            case SIZE -> {
                policy = new SizeRollingPolicy(this.path, this.maxHistory, this.maxSize * 1024 * 1024, this.compressPolicy.getCompressor());
            }
            default -> {
                throw new IllegalStateException("不支持的滚动类型: " + this.rollingPolicy.getName());
            }
        }

        this.appender = new FileAppender(policy);
        this.appender.afterPropertiesSet();
    }

    @Override
    public void destroy() throws Exception {
        if (this.appender != null) {
            this.appender.destroy();
            this.appender = null;
        }
    }

    @Override
    public void store(List<Log> logs) {
        this.appender.append(logs);
    }
}
