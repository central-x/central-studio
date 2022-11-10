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

package central.provider.graphql.gateway.entity;

import central.bean.*;
import central.data.gateway.GatewayFilterInput;
import central.sql.data.ModifiableEntity;
import central.util.Jsonx;
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
 * Gateway Filter
 * <p>
 * 网关过滤器
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "X_GW_FILTER")
@EqualsAndHashCode(callSuper = true)
public class GatewayFilterEntity extends ModifiableEntity implements Available, Remarkable, Orderable<GatewayFilterEntity>, Tenantable {
    @Serial
    private static final long serialVersionUID = -1675449460258179338L;

    @Id
    @Label("主键")
    @Size(max = 32)
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
    @NotBlank
    @Size(min = 1, max = 5 * 1024 * 1024)
    private String params;

    @Label("断言")
    @Size(max = 5 * 1025 * 1024)
    private String predicateJson;

    @Label("租户标识")
    @NotBlank
    @Size(min = 1, max = 32)
    private String tenantCode;

    public void fromInput(GatewayFilterInput input) {
        this.setId(input.getId());
        this.setType(input.getType());
        this.setPath(input.getPath());
        this.setOrder(input.getOrder());
        this.setEnabled(input.getEnabled());
        this.setRemark(input.getRemark());
        this.setParams(input.getParams());
        this.setPredicateJson(Jsonx.Default().serialize(input.getPredicates()));
    }
}
