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

package central.provider.database.migration.v0;

import central.sql.SqlType;
import central.sql.datasource.migration.*;
import central.util.Version;

import java.sql.SQLException;
import java.util.List;

/**
 * 初始化网关中心
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
public class v0_0_6_gateway extends Migration {
    public v0_0_6_gateway() {
        super(Version.of("0.0.5"), Version.of("0.0.6"));
    }

    @Override
    public void upgrade(Database database) throws SQLException {
        {
            // 过滤器
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("TYPE", SqlType.STRING, 32, "类型"),
                    Column.of("PATH", SqlType.STRING, 255, "匹配路径"),
                    Column.of("ORDER", SqlType.INTEGER, "排序号"),
                    Column.of("ENABLED", SqlType.BOOLEAN, "是否启用"),
                    Column.of("REMARK", SqlType.STRING, 1024, "备注"),
                    Column.of("PARAMS", SqlType.CLOB, "初始化参数"),
                    Column.of("PREDICATE_JSON", SqlType.CLOB, "断言"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间"),
                    Column.of("TENANT_CODE", SqlType.STRING, 32, "租户编码")
            );

            var indies = List.of(
                    Index.of("X_GW_F_TC", false, "TENANT_CODE")
            );

            var table = Table.of("X_GW_FILTER", "过滤器", columns, indies);
            database.addTable(table);
        }
    }

    @Override
    public void downgrade(Database database) throws SQLException {
        {
            // 过滤器
            var table = database.getTable("X_GW_FILTER");
            if (table != null) {
                table.drop();
            }
        }
    }
}
