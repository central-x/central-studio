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

package central.data.system.option;

import central.bean.OptionalEnum;
import central.lang.Arrayx;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * 数据库类型
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
@Getter
@AllArgsConstructor
public enum DatabaseType implements OptionalEnum<String> {
    MYSQL("MySql", "mysql"),
    ORACLE("Oracle", "oracle"),
    DAMENG("达梦", "dameng"),
    KINGBASE("人大金仓", "kingbase"),
    OSCAR("神舟通用", "oscar"),
    HIGHGO("翰高", "highgo"),
    H2("H2", "h2"),
    POSTGRESQL("PostgreSql", "postgresql"),
    VASTBASE("海量数据", "vasebase");

    private final String name;
    private final String value;

    public @Nullable DatabaseType resolve(String value) {
        return Arrayx.asStream(DatabaseType.values()).filter(it -> Objects.equals(it.getValue(), value)).findFirst().orElse(null);
    }
}
