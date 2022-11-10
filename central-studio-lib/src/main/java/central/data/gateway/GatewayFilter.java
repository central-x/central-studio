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

import central.bean.*;
import central.data.organization.Account;
import central.sql.data.ModifiableEntity;
import central.util.Listx;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serial;
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
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GatewayFilter extends ModifiableEntity implements Remarkable, Available, Orderable<GatewayFilter> {
    @Serial
    private static final long serialVersionUID = 1192393390020070868L;

    /**
     * 类型
     */
    @Nonnull
    private String type;
    /**
     * 匹配路径
     */
    @Nonnull
    private String path;
    /**
     * 排序号
     */
    @Nonnull
    private Integer order;
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
     * 初始化参数(JSON)
     */
    @Nonnull
    private String params;
    /**
     * 断言
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<GatewayPredicate> predicates;
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

    public GatewayFilterInput toInput() {
        return GatewayFilterInput.builder()
                .id(this.getId())
                .type(this.getType())
                .path(this.getPath())
                .order(this.getOrder())
                .enabled(this.getEnabled())
                .remark(this.getRemark())
                .params(this.getParams())
                .predicates(Listx.asStream(this.predicates).map(GatewayPredicate::toInput).toList())
                .build();
    }
}