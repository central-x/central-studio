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

package central.studio.provider.database.core.impl;

import central.data.system.DatabaseProperties;
import central.lang.BooleanEnum;
import central.lang.Stringx;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.studio.provider.database.core.Database;
import central.validation.Label;
import jakarta.validation.constraints.*;
import lombok.Setter;

/**
 * MySql 数据库
 *
 * @author Alan Yeh
 * @since 2022/11/12
 */
public class MySqlDatabase extends Database {
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
    @Control(label = "端口", type = ControlType.NUMBER, defaultValue = "3306")
    private Integer port;

    @Setter
    @Label("数据库名")
    @NotBlank
    @Size(min = 1, max = 128)
    @Control(label = "数据库名")
    private String database;

    @Setter
    @Label("用户名")
    @NotBlank
    @Size(min = 1, max = 128)
    @Control(label = "用户名", defaultValue = "root")
    private String username;

    @Setter
    @Label("密码")
    @NotBlank
    @Size(min = 1, max = 128)
    @Control(label = "密码", type = ControlType.PASSWORD)
    private String password;

    @Setter
    @Label("useUnicode")
    @NotNull
    @Control(label = "Unicode", type = ControlType.RADIO, defaultValue = "1", comment = "是否使用 Unitcode 传输")
    private BooleanEnum useUnicode;

    @Setter
    @Label("编码")
    @NotBlank
    @Size(min = 1, max = 32)
    @Control(label = "编码", defaultValue = "utf8")
    private String characterEncoding;

    @Setter
    @Label("SSL")
    @NotNull
    @Control(label = "SSL", type = ControlType.RADIO, defaultValue = "0", comment = "是否使用 SSL 加密数据库链接")
    private BooleanEnum useSsl;

    @Override
    public DatabaseProperties getProperties() {
        var properties = new DatabaseProperties();
        properties.setDriver("com.mysql.jdbc.Driver");
        properties.setUrl(Stringx.format("jdbc:mysql://{}:{}/{}?useUnicode={}&characterEncoding={}&useSSL={}", this.host, this.port, this.database, this.useUnicode.getJValue(), this.characterEncoding, this.useSsl.getJValue()));
        properties.setUsername(this.username);
        properties.setPassword(this.password);
        return properties;
    }
}
