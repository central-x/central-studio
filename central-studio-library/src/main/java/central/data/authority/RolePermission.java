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

package central.data.authority;

import central.bean.Nonnull;
import central.data.organization.Account;
import central.data.saas.Application;
import central.sql.data.Entity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * Role Permission Relation
 * <p>
 * 角色与权限关联关系修改
 *
 * @author Alan Yeh
 * @since 2024/12/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RolePermission extends Entity {
    @Serial
    private static final long serialVersionUID = 1793160992455377967L;

    /**
     * 应用主键
     */
    @Nonnull
    private String applicationId;

    /**
     * 应用
     */
    @Nonnull
    private Application application;

    /**
     * 角色主键
     */
    @Nonnull
    private String roleId;

    /**
     * 角色
     */
    @Nonnull
    private Role role;

    /**
     * 权限主键
     */
    @Nonnull
    private String permissionId;

    /**
     * 权限
     */
    @Nonnull
    private Permission permission;

    /**
     * 创建人信息
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Account creator;
}
