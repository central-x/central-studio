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

package central.provider.database.core.impl;

import central.data.system.DatabaseProperties;
import central.lang.Stringx;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.provider.database.core.Database;
import central.validation.Label;
import jakarta.validation.constraints.*;
import lombok.Setter;

/**
 * Oracle 数据库
 *
 * @author Alan Yeh
 * @since 2022/11/12
 */
public class OracleDatabase extends Database {

    @Setter
    @Label("主机")
    @NotBlank
    @Size(min = 1, max = 50)
    @Control(label = "主机", defaultValue = "127.0.0.1")
    private String host;

    @Setter
    @Label("端口")
    @NotNull
    @Min(1)
    @Max(65535)
    @Control(label = "端口", type = ControlType.NUMBER, defaultValue = "1521")
    private Integer port;

    @Setter
    @Label("服务名")
    @NotBlank
    @Size(min = 1, max = 128)
    @Control(label = "服务名", defaultValue = "ORCL")
    private String service;

    @Setter
    @Label("用户名")
    @NotBlank
    @Size(min = 1, max = 128)
    @Control(label = "用户名", defaultValue = "SYSDBA")
    private String username;

    @Setter
    @Label("密码")
    @NotBlank
    @Size(min = 1, max = 128)
    @Control(label = "密码", type = ControlType.PASSWORD)
    private String password;

    @Override
    public DatabaseProperties getProperties() {
        var properties = new DatabaseProperties();
        properties.setDriver("oracle.jdbc.driver.OracleDriver");
        properties.setUrl(Stringx.format("jdbc:oracle:thin:@{}:{}:{}", this.host, this.port, this.service));
        properties.setUsername(this.username);
        properties.setPassword(this.password);
        return properties;
    }
}
