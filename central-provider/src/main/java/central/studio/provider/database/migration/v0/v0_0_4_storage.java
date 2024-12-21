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

import central.studio.provider.database.persistence.storage.entity.StorageBucketEntity;
import central.studio.provider.database.persistence.storage.mapper.StorageBucketMapper;
import central.sql.SqlExecutor;
import central.sql.SqlType;
import central.sql.datasource.migration.*;
import central.util.Jsonx;
import central.util.Version;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 初始化存储中心
 *
 * @author Alan Yeh
 * @since 2022/10/30
 */
public class v0_0_4_storage extends Migration {
    public v0_0_4_storage() {
        super(Version.of("0.0.3"), Version.of("0.0.4"));
    }

    @Override
    public void upgrade(Database database) throws SQLException {
        {
            // 存储桶
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("APPLICATION_ID", SqlType.STRING, 32, "应用主键"),
                    Column.of("CODE", SqlType.STRING, 128, "标识"),
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
                    Index.of("X_SBUC_CODE", false, "CODE")
            );

            var table = Table.of("X_STO_BUCKET", "存储桶", columns, indies);
            database.addTable(table);
        }
        {
            // 存储对象
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("BUCKET_ID", SqlType.STRING, 32, "存储桶主键"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("SIZE", SqlType.INTEGER, "文件大小"),
                    Column.of("DIGEST", SqlType.STRING, 128, "摘要"),
                    Column.of("KEY", SqlType.STRING, 2048, "存储键"),
                    Column.of("CONFIRMED", SqlType.BOOLEAN, "确认状态"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间"),
                    Column.of("TENANT_CODE", SqlType.STRING, 32, "租户编码")
            );

//            var indies = List.of(
//                    Index.of("X_SOBJ_CODE", false, "CODE")
//            );

            var table = Table.of("X_STO_OBJECT", "存储桶", columns, List.of());
            database.addTable(table);
        }
    }

    @Override
    public void upgrade(SqlExecutor executor) throws SQLException {

        var bucketMapper = executor.getMapper(StorageBucketMapper.class);
        // 初始化数据
        var bucket = new StorageBucketEntity();
        bucket.setId("hy3tODglubTMlR9OcyO");
        bucket.setApplicationId("v788o67covIaDMn67mN");
        bucket.setCode("identity");
        bucket.setName("认证中心存储");
        bucket.setType("local");
        bucket.setEnabled(Boolean.TRUE);
        bucket.setRemark("");
        bucket.setParams(Jsonx.Default().serialize(Map.of("location", "./data/identity")));
        bucket.setTenantCode("master");
        bucket.updateCreator("syssa");
        bucketMapper.insert(bucket);
    }

    @Override
    public void downgrade(Database database) throws SQLException {
        {
            // 存储桶
            var table = database.getTable("X_STO_BUCKET");
            if (table != null) {
                table.drop();
            }
        }
        {
            // 存储对象
            var table = database.getTable("X_STO_OBJECT");
            if (table != null) {
                table.drop();
            }
        }
    }
}
