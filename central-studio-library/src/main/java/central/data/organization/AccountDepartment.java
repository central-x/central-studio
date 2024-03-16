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

package central.data.organization;

import central.bean.*;
import central.sql.data.Entity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * 帐户与部门关系
 *
 * @author Alan Yeh
 * @since 2022/09/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccountDepartment extends Entity {
    @Serial
    private static final long serialVersionUID = -8026990877129031832L;

    /**
     * 帐户主键
     */
    @Nonnull
    private String accountId;

    /**
     * 帐户信息
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Account account;

    /**
     * 单位主键
     */
    @Nonnull
    private String unitId;

    /**
     * 单位信息
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Unit unit;

    /**
     * 部门主键
     */
    @Nonnull
    private String departmentId;

    /**
     * 部门信息
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Department department;

    /**
     * 职务主键
     */
    @Nullable
    private String postId;

    /**
     * 职务信息
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Post post;

    /**
     * 是否主部门
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

    public AccountDepartmentInput toInput() {
        return AccountDepartmentInput.builder()
                .accountId(this.getAccountId())
                .unitId(this.getUnitId())
                .departmentId(this.getDepartmentId())
                .postId(this.getPostId())
                .primary(this.getPrimary())
                .build();
    }
}
