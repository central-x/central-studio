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

package central.provider.database.migration.v1;

import central.sql.SqlType;
import central.sql.datasource.migration.*;
import central.util.Version;

import java.sql.SQLException;
import java.util.List;

/**
 * 初始化版本
 *
 * @author Alan Yeh
 * @since 2022/09/26
 */
public class v1_0_0 extends Migration {
    public v1_0_0() {
        super(Version.of("1.0.0"));
    }

    @Override
    public void upgrade(Database database) throws SQLException {
        // sec
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
                    Index.of("X_SPAS_AI", false, "ACCOUNT_ID")
            );

            var table = Table.of("X_SEC_PASSWORD", "密码", columns, indies);
            database.addTable(table);
        }
        {
            // 菜单
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("PARENT_ID", SqlType.STRING, 32, "父菜单主键"),
                    Column.of("CODE", SqlType.STRING, 32, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("ICON", SqlType.STRING, 255, "图标"),
                    Column.of("TYPE", SqlType.STRING, 32, "类型"),
                    Column.of("ENABLED", SqlType.BOOLEAN, "是否启用"),
                    Column.of("ORDER", SqlType.INTEGER, "排序号"),
                    Column.of("REMARK", SqlType.STRING, 1024, "备注"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间"),
                    Column.of("TENANT_CODE", SqlType.STRING, 32, "租户编码")
            );

            var indies = List.of(
                    Index.of("X_SMEN_PI", false, "PARENT_ID"),
                    Index.of("X_SMEN_CODE", false, "CODE")
            );

            var table = Table.of("X_SEC_MENU", "菜单", columns, indies);

            database.addTable(table);
        }
        {
            // 权限
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("MENU_ID", SqlType.STRING, 32, "菜单主键"),
                    Column.of("CODE", SqlType.STRING, 32, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("ENABLED", SqlType.BOOLEAN, "是否启用"),
                    Column.of("REMARK", SqlType.STRING, 1024, "备注"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间"),
                    Column.of("TENANT_CODE", SqlType.STRING, 32, "租户编码")
            );

            var indies = List.of(
                    Index.of("X_SPER_MI", false, "MENU_ID"),
                    Index.of("X_SPER_CODE", false, "CODE")
            );

            var table = Table.of("X_SEC_PERMISSION", "功能", columns, indies);

            database.addTable(table);
        }
        {
            // 角色
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("CODE", SqlType.STRING, 32, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("UNIT_ID", SqlType.STRING, 32, "单位主键"),
                    Column.of("ENABLED", SqlType.BOOLEAN, "是否启用"),
                    Column.of("REMARK", SqlType.STRING, 1024, "备注"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间"),
                    Column.of("TENANT_CODE", SqlType.STRING, 32, "租户编码")
            );

            var indies = List.of(
                    Index.of("X_SROL_CODE", false, "CODE"),
                    Index.of("X_SROL_UI", false, "UNIT_ID")
            );

            var table = Table.of("X_SEC_ROLE", "角色", columns, indies);

            database.addTable(table);
        }

        // org
        {
            // 行政区划
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("PARENT_ID", SqlType.STRING, 32, "父区划主键"),
                    Column.of("CODE", SqlType.STRING, 32, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("TYPE", SqlType.STRING, 32, "类型"),
                    Column.of("ORDER", SqlType.INTEGER, "排序号"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间"),
                    Column.of("TENANT_CODE", SqlType.STRING, 32, "租户编码")
            );

            var indies = List.of(
                    Index.of("X_OARE_PI", false, "PARENT_ID"),
                    Index.of("X_OARE_CODE", false, "CODE")
            );

            var table = Table.of("X_ORG_AREA", "行政区划", columns, indies);

            database.addTable(table);
        }
        {
            // 单位
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("PARENT_ID", SqlType.STRING, 32, "父单位主键"),
                    Column.of("CODE", SqlType.STRING, 32, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("AREA_ID", SqlType.STRING, 32, "行政区划主键"),
                    Column.of("ORDER", SqlType.INTEGER, "排序号"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间"),
                    Column.of("TENANT_CODE", SqlType.STRING, 32, "租户编码")
            );

            var indies = List.of(
                    Index.of("X_OUNI_PI", false, "PARENT_ID"),
                    Index.of("X_OUNI_AI", false, "AREA_ID"),
                    Index.of("X_OUNI_CODE", false, "CODE")
            );

            var table = Table.of("X_ORG_UNIT", "单位", columns, indies);

            database.addTable(table);
        }
        {
            // 部门
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("UNIT_ID", SqlType.STRING, 32, "单位主键"),
                    Column.of("PARENT_ID", SqlType.STRING, 32, "父部门主键"),
                    Column.of("CODE", SqlType.STRING, 32, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("ORDER", SqlType.INTEGER, "排序号"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间"),
                    Column.of("TENANT_CODE", SqlType.STRING, 32, "租户编码")
            );

            var indies = List.of(
                    Index.of("X_ODEP_PI", false, "PARENT_ID"),
                    Index.of("X_ODEP_UI", false, "UNIT_ID"),
                    Index.of("X_ODEP_CODE", false, "CODE")
            );

            var table = Table.of("X_ORG_DEPARTMENT", "部门", columns, indies);

            database.addTable(table);
        }
        {
            // 职务
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("CODE", SqlType.STRING, 32, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("UNIT_ID", SqlType.STRING, 32, "单位主键"),
                    Column.of("ORDER", SqlType.INTEGER, "排序号"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间"),
                    Column.of("TENANT_CODE", SqlType.STRING, 32, "租户编码")
            );

            var indies = List.of(
                    Index.of("X_OPOS_CODE", false, "CODE"),
                    Index.of("X_OPOS_UI", false, "UNIT_ID")
            );

            var table = Table.of("X_ORG_POST", "职务", columns, indies);

            database.addTable(table);
        }
        {
            // 职级
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("CODE", SqlType.STRING, 32, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("UNIT_ID", SqlType.STRING, 32, "单位主键"),
                    Column.of("ORDER", SqlType.INTEGER, "排序号"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间"),
                    Column.of("TENANT_CODE", SqlType.STRING, 32, "租户编码")
            );

            var indies = List.of(
                    Index.of("X_ORAN_CODE", false, "CODE"),
                    Index.of("X_ORAN_UI", false, "UNIT_ID")
            );

            var table = Table.of("X_ORG_RANK", "职级", columns, indies);

            database.addTable(table);
        }
        {
            // 帐户
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("USERNAME", SqlType.STRING, 16, "用户名"),
                    Column.of("EMAIL", SqlType.STRING, 50, "邮箱"),
                    Column.of("MOBILE", SqlType.STRING, 16, "手机号"),
                    Column.of("NAME", SqlType.STRING, 50, "姓名"),
                    Column.of("AVATAR", SqlType.STRING, "头像"),
                    Column.of("ADMIN", SqlType.BOOLEAN, "是否管理员"),
                    Column.of("ENABLED", SqlType.BOOLEAN, "是否启用"),
                    Column.of("DELETED", SqlType.BOOLEAN, "是否己删除"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间"),
                    Column.of("TENANT_CODE", SqlType.STRING, 32, "租户编码")
            );

            var indies = List.of(
                    Index.of("X_OACC_USERNAME", false, "USERNAME"),
                    Index.of("X_OACC_EMAIL", false, "EMAIL"),
                    Index.of("X_OACC_MOBILE", false, "MOBILE")
            );

            var table = Table.of("X_ORG_ACCOUNT", "帐户", columns, indies);

            database.addTable(table);
        }
        {
            // 帐户单位关联关系
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("ACCOUNT_ID", SqlType.STRING, 32, "帐户主键"),
                    Column.of("UNIT_ID", SqlType.STRING, 32, "单位主键"),
                    Column.of("RANK_ID", SqlType.STRING, 32, "职级主键"),
                    Column.of("PRIMARY", SqlType.BOOLEAN, "是否主单位"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("TENANT_CODE", SqlType.STRING, 32, "租户编码")
            );

            var indies = List.of(
                    Index.of("X_OAU_AI", false, "ACCOUNT_ID"),
                    Index.of("X_OAU_UI", false, "UNIT_ID"),
                    Index.of("X_OAU_RI", false, "RANK_ID")
            );

            var table = Table.of("X_ORG_ACCOUNT_UNIT", "帐户单位关联关系", columns, indies);

            database.addTable(table);
        }
        {
            // 帐户单位关联关系
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("ACCOUNT_ID", SqlType.STRING, 32, "帐户主键"),
                    Column.of("UNIT_ID", SqlType.STRING, 32, "单位主键"),
                    Column.of("DEPARTMENT_ID", SqlType.STRING, 32, "部门主键"),
                    Column.of("POST_ID", SqlType.STRING, 32, "职务主键"),
                    Column.of("PRIMARY", SqlType.BOOLEAN, "是否主单位"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("TENANT_CODE", SqlType.STRING, 32, "租户编码")
            );

            var indies = List.of(
                    Index.of("X_OAD_AI", false, "ACCOUNT_ID"),
                    Index.of("X_OAD_UI", false, "UNIT_ID"),
                    Index.of("X_OAD_DI", false, "DEPARTMENT_ID"),
                    Index.of("X_OAD_PI", false, "POST_ID")
            );

            var table = Table.of("X_ORG_ACCOUNT_DEPARTMENT", "帐户部门关联关系", columns, indies);

            database.addTable(table);
        }

        // sys
        {
            // 数据库
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("CODE", SqlType.STRING, 32, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("TYPE", SqlType.STRING, 32, "类型"),
                    Column.of("ENABLED", SqlType.BOOLEAN, "是否启用"),
                    Column.of("REMARK", SqlType.STRING, 1024, "备注"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间"),
                    Column.of("TENANT_CODE", SqlType.STRING, 32, "租户编码")
            );

            var indies = List.of(
                    Index.of("X_SDAT_CODE", false, "CODE")
            );

            var table = Table.of("X_SYS_DATABASE", "数据库", columns, indies);

            database.addTable(table);
        }
        {
            // 字典
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("APPLICATION_ID", SqlType.STRING, 32, "所属应用主键"),
                    Column.of("CODE", SqlType.STRING, 32, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("ENABLED", SqlType.BOOLEAN, "是否启用"),
                    Column.of("REMARK", SqlType.STRING, 1024, "备注"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间"),
                    Column.of("TENANT_CODE", SqlType.STRING, 32, "租户编码")
            );

            var indies = List.of(
                    Index.of("X_SDIC_CODE", false, "CODE")
            );

            var table = Table.of("X_SYS_DICTIONARY", "字典", columns, indies);

            database.addTable(table);
        }
        {
            // 字典项
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("DICTIONARY_ID", SqlType.STRING, 32, "字典主键"),
                    Column.of("CODE", SqlType.STRING, 32, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("PRIMARY", SqlType.BOOLEAN, "是否主选项"),
                    Column.of("ORDER", SqlType.INTEGER, "排序号"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间"),
                    Column.of("TENANT_CODE", SqlType.STRING, 32, "租户编码")
            );

            var indies = List.of(
                    Index.of("X_SDI_CODE", false, "CODE")
            );

            var table = Table.of("X_SYS_DICTIONARY_ITEM", "字典项", columns, indies);

            database.addTable(table);
        }

        // ten
        {
            // 应用
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("CODE", SqlType.STRING, 32, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("LOGO_BYTES", SqlType.BLOB, "图标"),
                    Column.of("URL", SqlType.STRING, 1024, "服务地址"),
                    Column.of("CONTEXT_PATH", SqlType.STRING, 64, "上下文地址"),
                    Column.of("KEY", SqlType.STRING, 128, "密钥"),
                    Column.of("ENABLED", SqlType.BOOLEAN, "是否启用"),
                    Column.of("REMARK", SqlType.STRING, 1024, "备注"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间")
            );

            var indies = List.of(
                    Index.of("X_TAPP_CODE", false, "CODE")
            );

            var table = Table.of("X_TEN_APPLICATION", "应用", columns, indies);

            database.addTable(table);
        }
        {
            // 应用模块
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("APPLICATION_ID", SqlType.STRING, 32, "所属应用主键"),
                    Column.of("URL", SqlType.STRING, 1024, "服务地址"),
                    Column.of("CONTEXT_PATH", SqlType.STRING, 64, "上下文地址"),
                    Column.of("ENABLED", SqlType.BOOLEAN, "是否启用"),
                    Column.of("REMARK", SqlType.STRING, 1024, "备注"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间")
            );

            var indies = List.of(
                    Index.of("X_TAM_AI", false, "APPLICATION_ID")
            );

            var table = Table.of("X_TEN_APPLICATION_MODULE", "应用模块", columns, indies);

            database.addTable(table);
        }
        {
            // 租户
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("CODE", SqlType.STRING, 32, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("DATABASE_ID", SqlType.STRING, 32, "数据库主键"),
                    Column.of("ENABLED", SqlType.BOOLEAN, "是否启用"),
                    Column.of("REMARK", SqlType.STRING, 1024, "备注"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间")
            );

            var indies = List.of(
                    Index.of("X_TTEN_DI", false, "DATABASE_ID")
            );

            var table = Table.of("X_TEN_TENANT", "租户", columns, indies);

            database.addTable(table);
        }
        {
            // 租户租约
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("TENANT_ID", SqlType.STRING, 32, "租户主键"),
                    Column.of("APPLICATION_ID", SqlType.STRING, 32, "应用主键"),
                    Column.of("ENABLED", SqlType.BOOLEAN, "是否启用"),
                    Column.of("PRIMARY", SqlType.BOOLEAN, "是否主选项"),
                    Column.of("CREATOR_ID", SqlType.STRING, 36, "创建人主键"),
                    Column.of("CREATE_DATE", SqlType.DATETIME, "创建时间"),
                    Column.of("MODIFIER_ID", SqlType.STRING, 36, "更新人主键"),
                    Column.of("MODIFY_DATE", SqlType.DATETIME, "更新时间")
            );

            var indies = List.of(
                    Index.of("X_TTA_TI", false, "TENANT_ID"),
                    Index.of("X_TTA_AI", false, "APPLICATION_ID")
            );

            var table = Table.of("X_TEN_TENANT_APPLICATION", "租户与应用关联关系", columns, indies);

            database.addTable(table);
        }
    }

    @Override
    public void downgrade(Database database) throws SQLException {
        // auth
        {
            var menu = database.getTable("X_SEC_MENU");
            if (menu != null) {
                menu.drop();
            }

            var function = database.getTable("X_SEC_PERMISSION");
            if (function != null) {
                function.drop();
            }

            var role = database.getTable("X_SEC_ROLE");
            if (role != null) {
                role.drop();
            }
        }

        // org
        {
            var area = database.getTable("X_ORG_AREA");
            if (area != null) {
                area.drop();
            }

            var unit = database.getTable("X_ORG_UNIT");
            if (unit != null) {
                unit.drop();
            }

            var department = database.getTable("X_ORG_DEPARTMENT");
            if (department != null) {
                department.drop();
            }

            var post = database.getTable("X_ORG_POST");
            if (post != null) {
                post.drop();
            }

            var rank = database.getTable("X_ORG_RANK");
            if (rank != null) {
                rank.drop();
            }

            var accountUnit = database.getTable("X_ORG_ACCOUNT_UNIT");
            if (accountUnit != null) {
                accountUnit.drop();
            }

            var accountDepartment = database.getTable("X_ORG_ACCOUNT_DEPARTMENT");
            if (accountDepartment != null) {
                accountDepartment.drop();
            }
        }

        // sys
        {
            var db = database.getTable("X_SYS_DATABASE");
            if (db != null) {
                db.drop();
            }
            var dictionary = database.getTable("X_SYS_DICTIONARY");
            if (dictionary != null) {
                dictionary.drop();
            }

            var dictionaryItem = database.getTable("X_SYS_DICTIONARY_ITEM");
            if (dictionaryItem != null) {
                dictionaryItem.drop();
            }
        }

        // ten
        {
            var application = database.getTable("X_TEN_APPLICATION");
            if (application != null) {
                application.drop();
            }

            var applicationModule = database.getTable("X_TEN_APPLICATION_MODULE");
            if (applicationModule != null) {
                applicationModule.drop();
            }

            var tenant = database.getTable("X_TEN_TENANT");
            if (tenant != null) {
                tenant.drop();
            }

            var tenantApplication = database.getTable("X_TEN_TENANT_APPLICATION");
            if (tenantApplication != null) {
                tenantApplication.drop();
            }
        }
    }
}
