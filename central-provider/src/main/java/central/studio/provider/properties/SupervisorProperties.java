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

package central.studio.provider.properties;

import central.validation.Label;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 超级管理员配置
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
@Data
public class SupervisorProperties {
    @Label("用户名")
    @NotBlank
    @Size(min = 1, max = 16)
    private String username = "syssa";

    @Label("邮箱")
    @Email
    @Size(max = 50)
    private String email = "syssa@central-x.com";

    @Label("名称")
    @NotBlank
    @Size(min = 1, max = 50)
    private String name = "超级管理员";

    @Label("密码")
    @NotBlank
    @Size(min = 1, max = 128)
    private String password = "x.123456";

    @Label("头像")
    private String avatar = "";

    @Label("是否启用")
    private Boolean enabled = Boolean.TRUE;
}
