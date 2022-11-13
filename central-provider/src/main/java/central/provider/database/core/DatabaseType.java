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

package central.provider.database.core;

import central.bean.OptionalEnum;
import central.provider.database.core.impl.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 数据库类型
 *
 * @author Alan Yeh
 * @since 2022/11/12
 */
@Getter
@RequiredArgsConstructor
public enum DatabaseType implements OptionalEnum<String> {
    MYSQL("MySql", "mysql", MySqlDatabase.class),
    ORACLE("Oracle", "oracle", OracleDatabase.class),
    POSTGRESQL("PostgreSql", "postgresql", PostgreSqlDatabase.class),
    H2("H2", "h2", H2Database.class),
    DAMENG("达梦", "dameng", DamengDatabase.class),
    HIGHGO("瀚高", "highgo", HighGoDatabase.class),
    KINGBASE("人大金仓", "kingbase", KingbaseDatabase.class),
    OSCAR("神舟通用", "oscar", OscarDatabase.class),
    VASTBASE("海量数据", "vastbase", VastbaseDatabase.class);

    private final String name;
    private final String value;
    private final Class<? extends Database> type;

    public static DatabaseType resolve(String value) {
        return OptionalEnum.resolve(DatabaseType.class, value);
    }
}
