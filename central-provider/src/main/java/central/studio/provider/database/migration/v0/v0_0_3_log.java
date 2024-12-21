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

import central.data.log.LogPredicate;
import central.sql.SqlExecutor;
import central.sql.SqlType;
import central.sql.datasource.migration.*;
import central.studio.provider.database.persistence.log.entity.*;
import central.studio.provider.database.persistence.log.mapper.*;
import central.util.Jsonx;
import central.util.Version;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 初始化日志中心
 *
 * @author Alan Yeh
 * @since 2022/10/26
 */
public class v0_0_3_log extends Migration {

    public v0_0_3_log() {
        super(Version.of("0.0.2"), Version.of("0.0.3"));
    }

    @Override
    public void upgrade(Database database) throws SQLException {
        {
            // 采集器
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("CODE", SqlType.STRING, 128, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("TYPE", SqlType.STRING, 32, "类型"),
                    Column.of("ENABLED", SqlType.BOOLEAN, "是否启用"),
                    Column.of("REMARK", SqlType.STRING, 1024, "备注"),
                    Column.of("PARAMS", SqlType.CLOB, "初始化参数"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间")
            );

            var indies = List.of(
                    Index.of("X_LCOL_CODE", false, "CODE")
            );

            var table = Table.of("X_LOG_COLLECTOR", "采集器", columns, indies);
            database.addTable(table);
        }

        {
            // 过滤器
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("CODE", SqlType.STRING, 128, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("ENABLED", SqlType.BOOLEAN, "是否启用"),
                    Column.of("REMARK", SqlType.STRING, 1024, "备注"),
                    Column.of("PREDICATE_JSON", SqlType.CLOB, "初始化参数"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间")
            );

            var indies = List.of(
                    Index.of("X_LFIL_CODE", false, "CODE")
            );

            var table = Table.of("X_LOG_FILTER", "过滤器", columns, indies);
            database.addTable(table);
        }

        {
            // 存储器
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("CODE", SqlType.STRING, 128, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("TYPE", SqlType.STRING, 32, "类型"),
                    Column.of("ENABLED", SqlType.BOOLEAN, "是否启用"),
                    Column.of("REMARK", SqlType.STRING, 1024, "备注"),
                    Column.of("PARAMS", SqlType.CLOB, "初始化参数"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间")
            );

            var indies = List.of(
                    Index.of("X_LSTO_CODE", false, "CODE")
            );

            var table = Table.of("X_LOG_STORAGE", "存储器", columns, indies);
            database.addTable(table);
        }

        {
            // 采集器与过滤器关联关系
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("COLLECTOR_ID", SqlType.STRING, 32, "采集器主键"),
                    Column.of("FILTER_ID", SqlType.STRING, 32, "过滤器主键"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间")
            );

            var indies = List.of(
                    Index.of("X_LCF_CI", false, "COLLECTOR_ID"),
                    Index.of("X_LCF_FI", false, "FILTER_ID")
            );

            var table = Table.of("X_LOG_COLLECTOR_FILTER", "采集器与过滤器关联关系", columns, indies);
            database.addTable(table);
        }

        {
            // 存储器与过滤器关联关系
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("STORAGE_ID", SqlType.STRING, 32, "存储器主键"),
                    Column.of("FILTER_ID", SqlType.STRING, 32, "过滤器主键"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间")
            );

            var indies = List.of(
                    Index.of("X_LSF_SI", false, "STORAGE_ID"),
                    Index.of("X_LSF_FI", false, "FILTER_ID")
            );

            var table = Table.of("X_LOG_STORAGE_FILTER", "存储器与过滤器关联关系", columns, indies);
            database.addTable(table);
        }
    }

    @Override
    public void upgrade(SqlExecutor executor) throws SQLException {
        // 初始化数据
        var collectorMapper = executor.getMapper(LogCollectorMapper.class);
        var filterMapper = executor.getMapper(LogFilterMapper.class);
        var storageMapper = executor.getMapper(LogStorageMapper.class);
        var collectorRelMapper = executor.getMapper(LogCollectorFilterMapper.class);
        var storageRelMapper = executor.getMapper(LogStorageFilterMapper.class);

        // Http 采集器
        var httpCollector = new LogCollectorEntity();
        httpCollector.setCode("http");
        httpCollector.setName("Http 采集器");
        httpCollector.setType("http");
        httpCollector.setEnabled(Boolean.TRUE);
        httpCollector.setRemark("Http 采集器");
        httpCollector.setParams(Jsonx.Default().serialize(Map.of("path", "central")));
        httpCollector.updateCreator("syssa");
        collectorMapper.insert(httpCollector);

        // 本地采集器
        var localCollector = new LogCollectorEntity();
        localCollector.setCode("local");
        localCollector.setName("本地采集器");
        localCollector.setType("local");
        localCollector.setEnabled(Boolean.TRUE);
        localCollector.setRemark("本地采集器");
        localCollector.setParams(Jsonx.Default().serialize(Map.of("path", "./logs/tmp")));
        localCollector.updateCreator("syssa");
        collectorMapper.insert(localCollector);

        // 存储器
        var storageEntity = new LogStorageEntity();
        storageEntity.setCode("file");
        storageEntity.setName("文件存储器");
        storageEntity.setType("file");
        storageEntity.setEnabled(Boolean.TRUE);
        storageEntity.setRemark("文件存储器");
        storageEntity.setParams(Jsonx.Default().serialize(Map.of(
                "path", "./path",
                "rollingPolicy", "daily",
                "compressPolicy", "gzip",
                "maxSize", "1024",
                "maxHistory", "7")));
        storageEntity.updateCreator("syssa");
        storageMapper.insert(storageEntity);

        // 过滤器
        var entity = new LogFilterEntity();
        entity.setCode("central.x");
        entity.setName("CentralX");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("CentralX");
        entity.setPredicateJson(Jsonx.Default().serialize(List.of(
                new LogPredicate("tenant", Jsonx.Default().serialize(Map.of("tenantCode", "master"))),
                new LogPredicate("level", Jsonx.Default().serialize(Map.of("levels", List.of("info", "error"))))
        )));
        entity.updateCreator("syssa");
        filterMapper.insert(entity);

        // 采集器与过滤器的关联关系
        var collectorRel = new LogCollectorFilterEntity();
        collectorRel.setCollectorId(httpCollector.getId());
        collectorRel.setFilterId(entity.getId());
        collectorRel.updateCreator("syssa");
        collectorRelMapper.insert(collectorRel);

        // 存储器与过滤器的关联关系
        var storageRel = new LogStorageFilterEntity();
        storageRel.setStorageId(storageEntity.getId());
        storageRel.setFilterId(entity.getId());
        storageRel.updateCreator("syssa");
        storageRelMapper.insert(storageRel);
    }

    @Override
    public void downgrade(Database database) throws SQLException {
        {
            // 采集器
            var collector = database.getTable("X_LOG_COLLECTOR");
            if (collector != null) {
                collector.drop();
            }
        }
        {
            // 过滤器
            var filter = database.getTable("X_LOG_FILTER");
            if (filter != null) {
                filter.drop();
            }
        }
        {
            // 存储器
            var storage = database.getTable("X_LOG_STORAGE");
            if (storage != null) {
                storage.drop();
            }
        }
        {
            // 采集器与过滤器关联关系
            var rel = database.getTable("X_LOG_COLLECTOR_FILTER");
            if (rel != null) {
                rel.drop();
            }
        }
        {
            // 存储器与过滤器关联关系
            var rel = database.getTable("X_LOG_STORAGE_FILTER");
            if (rel != null) {
                rel.drop();
            }
        }
    }
}
