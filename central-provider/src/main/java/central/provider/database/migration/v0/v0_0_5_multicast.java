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

import central.provider.graphql.multicast.entity.MulticastBroadcasterEntity;
import central.provider.graphql.multicast.mapper.MulticastBroadcasterMapper;
import central.sql.SqlExecutor;
import central.sql.SqlType;
import central.sql.datasource.migration.*;
import central.util.Jsonx;
import central.util.Version;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 初始化广播中心
 *
 * @author Alan Yeh
 * @since 2022/11/03
 */
public class v0_0_5_multicast extends Migration {
    public v0_0_5_multicast() {
        super(Version.of("0.0.4"), Version.of("0.0.5"));
    }

    @Override
    public void upgrade(Database database) throws SQLException {
        {
            // 广播器
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("APPLICATION_ID", SqlType.STRING, 32, "应用主键"),
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
                    Index.of("X_MBRO_CODE", false, "CODE")
            );

            var table = Table.of("X_MCAST_BROADCASTER", "广播器", columns, indies);
            database.addTable(table);
        }
        {
            // 广播消息
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("BROADCASTER_ID", SqlType.STRING, 32, "广播器主键"),
                    Column.of("BODY", SqlType.CLOB, "消息体"),
                    Column.of("MODE", SqlType.STRING, 32, "推送模式"),
                    Column.of("STATUS", SqlType.STRING, 32, "消息状态"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间"),
                    Column.of("TENANT_CODE", SqlType.STRING, 32, "租户编码")
            );

            var indies = List.of(
                    Index.of("X_MMES_BI", false, "BROADCASTER_ID"),
                    Index.of("X_MMES_TC", false, "TENANT_CODE")
            );

            var table = Table.of("X_MCAST_MESSAGE", "广播消息", columns, indies);
            database.addTable(table);
        }
    }

    @Override
    public void upgrade(SqlExecutor executor) throws SQLException {

        var broadcasterMapper = executor.getMapper(MulticastBroadcasterMapper.class);
        // 初始化数据
        var broadcaster = new MulticastBroadcasterEntity();
        broadcaster.setId("qrCL6YDyaS0JrD8Urm8");
        broadcaster.setApplicationId("v788o67covIaDMn67mN");
        broadcaster.setCode("security");
        broadcaster.setName("腾讯企业邮");
        broadcaster.setType("email_smtp");
        broadcaster.setEnabled(Boolean.TRUE);
        broadcaster.setRemark("");
        broadcaster.setParams(Jsonx.Default().serialize(Map.of(
                "host", "smtp.exmail.qq.com",
                "ssl", "enabled",
                "port", "465",
                "username", "no-reply@central-x.com",
                "password", "x.123456",
                "name", "No Reply"
        )));
        broadcaster.setTenantCode("master");
        broadcaster.updateCreator("syssa");
        broadcasterMapper.insert(broadcaster);
    }

    @Override
    public void downgrade(Database database) throws SQLException {
        {
            // 广播器
            var table = database.getTable("X_MCAST_BROADCASTER");
            if (table != null) {
                table.drop();
            }
        }
        {
            // 广播消息
            var table = database.getTable("X_MCAST_MESSAGE");
            if (table != null) {
                table.drop();
            }
        }
    }
}