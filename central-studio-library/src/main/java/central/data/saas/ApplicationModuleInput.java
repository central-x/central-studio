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

package central.data.saas;

import central.validation.Label;
import central.validation.group.Insert;
import central.validation.group.Update;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * 应用模块输入
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationModuleInput implements Serializable {
    @Serial
    private static final long serialVersionUID = -5378855445379690625L;

    @Label("主键")
    @Null(groups = Insert.class)
    @NotBlank(groups = Update.class)
    @Size(min = 1, max = 32, groups = Insert.class)
    private String id;

    @Label("所属应用主键")
    @NotBlank
    @Size(min = 1, max = 32)
    private String applicationId;

    @Label("服务地址")
    @NotBlank
    @Size(min = 1, max = 1024)
    private String url;

    @Label("上下文路径")
    @NotBlank
    @Size(min = 1, max = 64)
    private String contextPath;

    @Label("是否启用")
    @NotNull
    private Boolean enabled;

    @Label("备注")
    @Size(max = 1024)
    private String remark;
}
