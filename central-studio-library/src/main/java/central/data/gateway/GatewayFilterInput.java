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

package central.data.gateway;

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
 * Gateway Filter
 * <p>
 * 网关过滤器
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true, builderClassName = "Builder")
public class GatewayFilterInput implements Serializable {
    @Serial
    private static final long serialVersionUID = 3224470673107616978L;

    @Label("主键")
    @Null(groups = Insert.class)
    @NotBlank(groups = Update.class)
    @Size(min = 1, max = 32, groups = Insert.class)
    private String id;

    @Label("类型")
    @NotBlank
    @Size(min = 1, max = 32)
    private String type;

    @Label("匹配路径")
    @NotBlank
    @Size(min = 1, max = 255)
    private String path;

    @Label("排序号")
    @NotNull
    @Min(Integer.MIN_VALUE)
    @Max(Integer.MAX_VALUE)
    private Integer order;

    @Label("是否启用")
    @NotNull
    private Boolean enabled;

    @Label("备注")
    @Size(max = 1024)
    private String remark;

    @Label("初始化参数")
    @NotNull
    @Size(min = 1, max = 5 * 1024 * 1024)
    private String params;

    @Label("断言")
    @Valid
    private List<GatewayPredicateInput> predicates;
}
