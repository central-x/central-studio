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

package central.logging.core.storage.impl.file.polocy;

import central.io.Filex;
import central.lang.Arrayx;
import central.lang.Stringx;
import central.logging.core.storage.impl.file.Compressor;
import central.logging.core.storage.impl.file.RollingPolicy;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalQueries;
import java.util.Comparator;
import java.util.Objects;

/**
 * 基于时间的滚动策略
 *
 * @author Alan Yeh
 * @since 2022/10/24
 */
@RequiredArgsConstructor
public class TimeRollingPolicy implements RollingPolicy {

    /**
     * 日志保存路径
     */
    private final String path;
    /**
     * 最大日志保存文件数量
     */
    private final Integer maxHistory;
    /**
     * 最大日志文件大小
     */
    private final Integer maxSize;
    /**
     * 文件命名规则
     */
    private final String fileNamePattern;
    /**
     * 文件压缩
     */
    private final Compressor compressor;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public File getFile() throws IOException {
        // 创建文件夹
        var parent = new File(this.path, LocalDateTime.now().format(formatter));
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                throw new IOException("无法在指定目录下写入文件: " + parent.getAbsolutePath());
            }
        }

        // 创建新的日志文件
        var filename = LocalDateTime.now().format(DateTimeFormatter.ofPattern(this.fileNamePattern));

        var lastname = Arrayx.asStream(parent.listFiles())
                .map(File::getName)
                .filter(it -> it.startsWith(filename + "_"))
                .max(Comparator.naturalOrder())
                .orElse(filename + "_0001.log");

        var file = new File(parent, lastname);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("无法在指定目标下创建日志文件: " + file.getAbsolutePath());
            }
        }

        return file;
    }

    @Override
    public void roll() throws IOException {
        // 文件滚动
        var currentFile = this.getFile();
        if (currentFile.length() > this.maxSize) {
            // 如果文件超出指定大小，则创建下一个文件
            String index = currentFile.getName().substring(currentFile.getName().lastIndexOf("_") + 1, currentFile.getName().lastIndexOf("."));
            var nextIndex = currentFile.getName().substring(0, currentFile.getName().lastIndexOf("_") + 1) + Stringx.paddingLeft(String.valueOf(Integer.parseInt(index) + 1), 4, '0');
            var nextFile = new File(nextIndex + ".log");

            if (!nextFile.createNewFile()) {
                throw new IOException("无法在指定目录下创建日志文件: " + nextFile.getAbsolutePath());
            }
        }

        // 清除过期日志
        var keepDate = LocalDate.now().minusDays(this.maxHistory);

        var folders = Arrayx.asStream(new File(this.path).listFiles())
                .filter(it -> {
                    try {
                        var date = formatter.parse(it.getName()).query(TemporalQueries.localDate());
                        return date.isBefore(keepDate);
                    } catch (DateTimeParseException ex) {
                        // 如果文件夹名字无法转成日期格式，说明该文件夹也需要被删除
                        return true;
                    }
                })
                .toList();

        for (var folder : folders) {
            Filex.delete(folder);
        }

        // 压缩文件
        var files = Arrayx.asStream(new File(this.path).listFiles())
                .flatMap(it -> Arrayx.asStream(it.listFiles()))
                .filter(it -> it.getName().endsWith(".log"))
                .toList();
        // 与当前正在写的文件不同名的全部都要执行压缩
        var current = this.getFile().getAbsolutePath();
        for (var file : files) {
            if (!Objects.equals(file.getAbsolutePath(), current)) {
                this.compressor.compress(file);
            }
        }
    }
}
