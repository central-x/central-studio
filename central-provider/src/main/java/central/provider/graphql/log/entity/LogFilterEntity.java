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

package central.provider.graphql.log.entity;

import central.bean.Available;
import central.bean.Codeable;
import central.bean.Remarkable;
import central.data.log.LogFilterInput;
import central.sql.data.ModifiableEntity;
import central.util.Jsonx;
import central.util.Listx;
import central.validation.Label;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * 日志过滤器
 *
 * @author Alan Yeh
 * @since 2022/10/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "X_LOG_FILTER")
@EqualsAndHashCode(callSuper = true)
public class LogFilterEntity extends ModifiableEntity implements Codeable, Available, Remarkable {
    @Serial
    private static final long serialVersionUID = 7322866847530780209L;

    @Id
    @Label("主键")
    @Size(max = 32)
    private String id;

    @Label("标识")
    @NotBlank
    @Size(min = 1, max = 32)
    @Pattern(regexp = "^[0-9a-zA-Z_-]+$", message = "${label}[${property}]只能由数字、英文字母、中划线、下划线组成")
    @Pattern(regexp = "^(?!-)(?!_).*$", message = "${label}[${property}]不能由中划线或下划线开头")
    @Pattern(regexp = "^(?!.*?[-_]$).*$", message = "${label}[${property}]不能由中划线或下划线结尾")
    @Pattern(regexp = "^(?!.*(--).*$).*$", message = "${label}[${property}]不能出现连续中划线")
    @Pattern(regexp = "^(?!.*(__).*$).*$", message = "${label}[${property}]不能出现连续下划线")
    @Pattern(regexp = "^(?!.*(-_|_-).*$).*$", message = "${label}[${property}]不能出现连续中划线、下划线")
    private String code;

    @Label("名称")
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @Label("是否启用")
    @NotNull
    private Boolean enabled;

    @Label("备注")
    @Size(max = 1024)
    private String remark;

    @Label("断言")
    @Size(max = 5 * 1025 * 1024)
    private String predicateJson;

    public void fromInput(LogFilterInput input) {
        this.setId(input.getId());
        this.setCode(input.getCode());
        this.setName(input.getName());
        this.setEnabled(input.getEnabled());
        this.setRemark(input.getRemark());
        if (Listx.isNotEmpty(input.getPredicates())) {
            this.setPredicateJson(Jsonx.Default().serialize(input.getPredicates()));
        } else {
            this.setPredicateJson("");
        }
    }
}
