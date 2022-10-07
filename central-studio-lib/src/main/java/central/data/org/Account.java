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

package central.data.org;

import central.data.sec.Role;
import central.bean.Available;
import central.bean.Deletable;
import central.sql.data.ModifiableEntity;
import central.util.Listx;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serial;
import java.util.List;

/**
 * 帐户
 *
 * @author Alan Yeh
 * @since 2022/09/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Account extends ModifiableEntity implements Available, Deletable {
    @Serial
    private static final long serialVersionUID = -9196700805612029968L;

    /**
     * 用户名
     */
    @Nonnull
    private String username;

    /**
     * 邮箱
     */
    @Nullable
    private String email;

    /**
     * 手机号
     */
    @Nullable
    private String mobile;

    /**
     * 姓名
     */
    @Nonnull
    private String name;

    /**
     * 头像
     */
    @Nullable
    private String avatar;

    /**
     * 是否管理员
     * 管理员是指三员（系统管理员，安全管理员、安全保密员）
     */
    @Nonnull
    private Boolean admin;

    /**
     * 是否超级管理员
     * 超级管理员是虚拟帐户，不存在数据库表中
     */
    @Nonnull
    private Boolean supervisor;

    /**
     * 是否启用
     */
    @Nonnull
    private Boolean enabled;

    /**
     * 是否已删除
     */
    @Nonnull
    private Boolean deleted;

    /**
     * 单列列表
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AccountUnit> units;

    /**
     * 角色表表
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Role> roles;

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

    public AccountInput toInput() {
        return AccountInput.builder()
                .id(this.getId())
                .username(this.getUsername())
                .email(this.getEmail())
                .mobile(this.getMobile())
                .name(this.getName())
                .avatar(this.getAvatar())
                .enabled(this.getEnabled())
                .deleted(this.getDeleted())
                .units(Listx.asStream(this.getUnits()).map(AccountUnit::toInput).toList())
                .build();
    }
}
