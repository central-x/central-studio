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

package central.studio.provider.database.migration.v0;

import central.sql.SqlType;
import central.sql.datasource.migration.*;
import central.util.Version;

import java.sql.SQLException;
import java.util.List;

/**
 * 初始化认证中心
 *
 * @author Alan Yeh
 * @since 2022/10/26
 */
public class v0_0_2_identity extends Migration {
    public v0_0_2_identity() {
        super(Version.of("0.0.1"), Version.of("0.0.2"));
    }

    @Override
    public void upgrade(Database database) throws SQLException {
        {
            // 密码
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("ACCOUNT_ID", SqlType.STRING, 36, "帐户主键"),
                    Column.of("VALUE", SqlType.STRING, 256, "密码"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("TENANT_CODE", SqlType.STRING, 32, "租户编码")
            );

            var indies = List.of(
                    Index.of("X_IPAS_AI", false, "ACCOUNT_ID")
            );

            var table = Table.of("X_ID_PASSWORD", "密码", columns, indies);
            database.addTable(table);
        }
        {
            // 策略
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("CODE", SqlType.STRING, 32, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("TYPE", SqlType.STRING, 32, "类型"),
                    Column.of("ENABLED", SqlType.BOOLEAN, "是否启用"),
                    Column.of("REMARK", SqlType.STRING, 1024, "备注"),
                    Column.of("PARAMS", SqlType.CLOB, "初始化参数"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间"),
                    Column.of("TENANT_CODE", SqlType.STRING, 32, "租户编码")
            );

            var indies = List.of(
                    Index.of("X_ISTR_CODE", false, "CODE")
            );

            var table = Table.of("X_ID_STRATEGY", "安全策略", columns, indies);
            database.addTable(table);
        }
    }

    @Override
    public void downgrade(Database database) throws SQLException {
        {
            // 密码
            var table = database.getTable("X_ID_PASSWORD");
            if (table != null) {
                table.drop();
            }
        }
        {
            // 策略
            var table = database.getTable("X_ID_STRATEGY");
            if (table != null) {
                table.drop();
            }
        }
    }
}
