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

import central.validation.Label;
import central.validation.group.Insert;
import central.validation.group.Update;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 数据库输入
 *
 * @author Alan Yeh
 * @since 2022/09/12
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true, builderClassName = "Builder")
public class DatabaseInput implements Serializable {
    @Serial
    private static final long serialVersionUID = -5975066044517701397L;

    @Label("主键")
    @Null(groups = Insert.class)
    @NotBlank(groups = Update.class)
    @Size(min = 1, max = 32, groups = Insert.class)
    private String id;

    @Label("应用主键")
    @NotNull
    @Size(min = 1, max = 32)
    private String applicationId;

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

    @Label("类型")
    @NotBlank
    @Size(min = 1, max = 32)
    private String type;

    @Label("是否启用")
    @NotNull
    private Boolean enabled;

    @Label("备注")
    @Size(max = 1024)
    private String remark;

    @Label("主数据库")
    @NotNull
    @Valid
    private DatabasePropertiesInput master;

    @Label("从数据库")
    @Valid
    private List<DatabasePropertiesInput> slaves;

    @Label("初始化参数")
    @NotBlank
    @Size(min = 1, max = 5 * 1024 * 1024)
    private String params;
}
