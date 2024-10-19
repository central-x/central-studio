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

package central.studio.dashboard.controller.system.param;

import central.data.system.DatabasePropertiesInput;
import central.validation.Label;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Database Properties Params
 * <p>
 * 数据库配置入参
 *
 * @author Alan Yeh
 * @since 2024/10/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatabasePropertiesParams {
    @Label("驱动")
    @NotBlank
    @Size(min = 1, max = 1024)
    private String driver;

    @Label("连接字符串")
    @NotBlank
    @Size(min = 1, max = 1024)
    private String url;

    @Label("用户名")
    @Size(max = 50)
    private String username;

    @Label("密码")
    @Size(max = 50)
    private String password;

    public DatabasePropertiesInput toInput() {
        return DatabasePropertiesInput.builder()
                .driver(this.getDriver())
                .url(this.getUrl())
                .username(this.getUsername())
                .password(this.getPassword())
                .build();
    }
}
