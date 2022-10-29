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

package central.data.log;

import central.data.org.Account;
import central.sql.data.ModifiableEntity;
import central.util.Listx;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serial;
import java.util.List;

/**
 * 日志过滤器
 *
 * @author Alan Yeh
 * @since 2022/10/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LogFilter extends ModifiableEntity {
    @Serial
    private static final long serialVersionUID = -8320855690325346616L;
    /**
     * 标识
     */
    @Nonnull
    private String code;
    /**
     * 名称
     */
    @Nonnull
    private String name;
    /**
     * 是否启用
     */
    @Nonnull
    private Boolean enabled;
    /**
     * 备注
     */
    @Nullable
    private String remark;
    /**
     * 采集器
     */
    @Nonnull
    private List<LogCollector> collectors;
    /**
     * 存储器
     */
    @Nonnull
    private List<LogStorage> storages;
    /**
     * 断言
     */
    private List<LogPredicate> predicates;
    /**
     * 创建人信息
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Account creator;
    /**
     * 修改人信息
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Account modifier;

    public LogFilterInput toInput() {
        return LogFilterInput.builder()
                .id(this.getId())
                .code(this.getCode())
                .name(this.getName())
                .enabled(this.getEnabled())
                .remark(this.getRemark())
                .collectorIds(Listx.asStream(this.getCollectors()).map(LogCollector::getId).toList())
                .storageIds(Listx.asStream(this.getStorages()).map(LogStorage::getId).toList())
                .predicates(Listx.asStream(this.getPredicates()).map(LogPredicate::toInput).toList())
                .build();
    }
}
