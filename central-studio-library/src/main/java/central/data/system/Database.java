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

package central.data.system;

import central.bean.*;
import central.data.organization.Account;
import central.data.system.option.DatabaseType;
import central.data.saas.Application;
import central.sql.data.ModifiableEntity;
import central.util.Listx;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.List;

/**
 * 数据库
 *
 * @author Alan Yeh
 * @since 2022/09/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Database extends ModifiableEntity implements Codeable, Available, Remarkable {
    @Serial
    private static final long serialVersionUID = -6675888705633426897L;

    /**
     * 应用主键
     */
    @Nonnull
    private String applicationId;

    /**
     * 应用信息
     */
    @Nonnull
    private Application application;

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
     * 类型
     *
     * @see DatabaseType
     */
    @Nonnull
    private String type;

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
     * 主数据库属性
     */
    @Nonnull
    private DatabaseProperties master;

    /**
     * 从数据库属性
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<DatabaseProperties> slaves;

    /**
     * 初始化参数(JSON)
     * <p>
     * {@code {"master": "", "slaves": ""}}
     */
    @Nonnull
    private String params;

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

    public DatabaseInput.Builder toInput() {
        return DatabaseInput.builder()
                .id(this.getId())
                .applicationId(this.getApplicationId())
                .code(this.getCode())
                .name(this.getName())
                .type(this.getType())
                .enabled(this.getEnabled())
                .remark(this.getRemark())
                .master(this.getMaster().toInput().build())
                .slaves(Listx.asStream(this.getSlaves()).map(DatabaseProperties::toInput).map(DatabasePropertiesInput.Builder::build).toList())
                .params(this.getParams());
    }
}
