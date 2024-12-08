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

import central.data.organization.AccountInput;
import central.util.Listx;
import central.validation.Label;
import central.validation.group.Insert;
import central.validation.group.Update;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Account Params
 * <p>
 * 帐号入参
 *
 * @author Alan Yeh
 * @since 2024/12/08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountParams {

    @Label("主键")
    @Null(groups = Insert.class)
    @NotBlank(groups = Update.class)
    @Size(min = 1, max = 32, groups = Insert.class)
    private String id;

    @Label("用户名")
    @NotBlank
    @Size(min = 1, max = 16)
    @Pattern(regexp = "^[0-9a-zA-Z_-]+$", message = "${label}[${property}]只能由数字、英文字母、中划线、下划线组成")
    @Pattern(regexp = "^(?!-)(?!_).*$", message = "${label}[${property}]不能由中划线或下划线开头")
    @Pattern(regexp = "^(?!.*?[-_]$).*$", message = "${label}[${property}]不能由中划线或下划线结尾")
    @Pattern(regexp = "^(?!.*(--).*$).*$", message = "${label}[${property}]不能出现连续中划线")
    @Pattern(regexp = "^(?!.*(__).*$).*$", message = "${label}[${property}]不能出现连续下划线")
    @Pattern(regexp = "^(?!.*(-_|_-).*$).*$", message = "${label}[${property}]不能出现连续中划线、下划线")
    private String username;

    @Label("邮箱")
    @Email
    @Size(max = 50)
    private String email;

    @Label("手机号")
    @Size(max = 16)
    private String mobile;

    @Label("姓名")
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @Label("头像")
    @Size(max = 2 * 1024 * 1024)
    private String avatar;

    @Label("是否启用")
    @NotNull
    private Boolean enabled;

    @Label("是否已删除")
    @NotNull
    private Boolean deleted;

    @Valid
    @Label("单位")
    private List<AccountUnitParams> units;

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
                .units(Listx.asStream(this.getUnits()).map(AccountUnitParams::toInput).toList())
                .build();
    }
}
