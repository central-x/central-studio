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
 * H2 数据库
 *
 * @author Alan Yeh
 * @since 2022/11/12
 */
public class H2Database extends Database {

    @Setter
    @Label("数据库名")
    @NotBlank
    @Size(min = 1, max = 50)
    @Control(label = "数据库名")
    private String name;

    @Setter
    @Label("内存模式")
    @NotNull
    @Control(label = "内存模式", type = ControlType.RADIO, defaultValue = "0", comment = "使用内存模式时，服务重启后数据将丢失")
    private BooleanEnum memoryMode;

    @Setter
    @Label("用户名")
    @NotBlank
    @Size(min = 1, max = 128)
    @Control(label = "用户名")
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
        properties.setDriver("org.h2.Driver");
        if (memoryMode.getJValue()) {
            properties.setUrl(Stringx.format("jdbc:h2:mem:", this.name));
        } else {
            properties.setUrl(Stringx.format("jdbc:h2:./db/h2/{}", this.name));
        }
        properties.setUsername(this.username);
        properties.setPassword(this.password);
        return properties;
    }
}
