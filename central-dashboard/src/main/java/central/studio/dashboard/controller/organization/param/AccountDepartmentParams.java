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

package central.studio.dashboard.controller.organization.param;

import central.data.organization.AccountDepartmentInput;
import central.validation.Label;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Account Department Params
 * <p>
 * 帐户与部门关系入参
 *
 * @author Alan Yeh
 * @since 2024/12/08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDepartmentParams {

    @Label("单位主键")
    @NotBlank
    @Size(min = 1, max = 32)
    private String unitId;

    @Label("部门主键")
    @NotBlank
    @Size(min = 1, max = 32)
    private String departmentId;

    @Label("职务主键")
    @NotBlank
    @Size(min = 1, max = 32)
    private String postId;

    @Label("是否主部门")
    @NotNull
    private Boolean primary;

    public AccountDepartmentInput toInput() {
        return AccountDepartmentInput.builder()
                .unitId(this.getUnitId())
                .departmentId(this.getDepartmentId())
                .postId(this.getPostId())
                .primary(this.getPrimary())
                .build();
    }
}
