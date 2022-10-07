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

package central.data.ten;

import central.bean.Available;
import central.data.org.Account;
import central.sql.data.ModifiableEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.io.Serial;

/**
 * 租户与应用关联关系
 *
 * @author Alan Yeh
 * @since 2022/09/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TenantApplication extends ModifiableEntity implements Available {
    @Serial
    private static final long serialVersionUID = -5447798128289682120L;

    /**
     * 租户主键
     */
    @Nonnull
    private String tenantId;

    /**
     * 租户信息
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Tenant tenant;

    /**
     * 应用主键
     */
    @Nonnull
    private String applicationId;

    /**
     * 应用信息
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Application application;

    /**
     * 是否启用
     */
    @Nonnull
    private Boolean enabled;

    /**
     * 是否主要
     * 如果为主要，则访问租户时，如果不带路径，则会跳转到主要应用
     */
    @Nonnull
    private Boolean primary;

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

    public TenantApplicationInput toInput() {
        return TenantApplicationInput.builder()
                .id(this.getId())
                .tenantId(this.getTenantId())
                .applicationId(this.getApplicationId())
                .enabled(this.getEnabled())
                .primary(this.getPrimary())
                .build();
    }
}
