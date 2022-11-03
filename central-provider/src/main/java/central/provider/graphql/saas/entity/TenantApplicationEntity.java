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

package central.provider.graphql.saas.entity;

import central.data.saas.TenantApplicationInput;
import central.sql.data.ModifiableEntity;
import central.validation.Label;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * 租户与应用关联关系
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "X_SAAS_TENANT_APPLICATION")
@EqualsAndHashCode(callSuper = true)
public class TenantApplicationEntity extends ModifiableEntity {
    @Serial
    private static final long serialVersionUID = 6785254341241070298L;

    @Id
    @Label("主键")
    @Size(max = 32)
    private String id;

    @Label("租户主键")
    @NotBlank
    @Size(min = 1, max = 32)
    private String tenantId;

    @Label("应用主键")
    @NotBlank
    @Size(min = 1, max = 32)
    private String applicationId;

    @Label("是否启用")
    @NotNull
    private Boolean enabled;

    @Label("是否主选项")
    @NotNull
    private Boolean primary;

    public void fromInput(TenantApplicationInput input) {
        this.setId(input.getId());
        this.setTenantId(input.getTenantId());
        this.setApplicationId(input.getApplicationId());
        this.setEnabled(input.getEnabled());
        this.setPrimary(input.getPrimary());
    }
}
