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

import central.data.system.option.DatabaseType;
import central.io.IOStreamx;
import central.provider.graphql.system.entity.DatabaseEntity;
import central.provider.graphql.system.mapper.DatabaseMapper;
import central.provider.graphql.saas.entity.ApplicationEntity;
import central.provider.graphql.saas.entity.TenantApplicationEntity;
import central.provider.graphql.saas.entity.TenantEntity;
import central.provider.graphql.saas.mapper.ApplicationMapper;
import central.provider.graphql.saas.mapper.TenantApplicationMapper;
import central.provider.graphql.saas.mapper.TenantMapper;
import central.sql.SqlExecutor;
import central.sql.SqlType;
import central.sql.datasource.migration.*;
import central.util.Guidx;
import central.util.Jsonx;
import central.util.Version;
import lombok.SneakyThrows;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 初始化基础数据结构
 *
 * @author Alan Yeh
 * @since 2022/10/26
 */
public class v0_0_1_initial extends Migration {

    public v0_0_1_initial() {
        super(Version.of("0.0.1"));
    }

    @Override
    public void upgrade(Database database) throws SQLException {
        // authority
        {
            // 菜单
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("APPLICATION_ID", SqlType.STRING, 32, "应用主键"),
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
                    Index.of("X_AMEN_PI", false, "PARENT_ID"),
                    Index.of("X_AMEN_CODE", false, "CODE")
            );

            var table = Table.of("X_AUTH_MENU", "菜单", columns, indies);

            database.addTable(table);
        }
        {
            // 权限
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("APPLICATION_ID", SqlType.STRING, 32, "应用主键"),
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
                    Index.of("X_APER_MI", false, "MENU_ID"),
                    Index.of("X_APER_CODE", false, "CODE")
            );

            var table = Table.of("X_AUTH_PERMISSION", "功能", columns, indies);

            database.addTable(table);
        }
        {
            // 角色
            var columns = List.of(
                    Column.of("ID", true, SqlType.STRING, 32, "主键"),
                    Column.of("APPLICATION_ID", SqlType.STRING, 32, "应用主键"),
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
                    Index.of("X_AROL_CODE", false, "CODE"),
                    Index.of("X_AROL_UI", false, "UNIT_ID")
            );

            var table = Table.of("X_AUTH_ROLE", "角色", columns, indies);

            database.addTable(table);
        }

        // organization
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
                    Column.of("APPLICATION_ID", SqlType.STRING, 32, "应用主键"),
                    Column.of("CODE", SqlType.STRING, 32, "标识"),
                    Column.of("NAME", SqlType.STRING, 50, "名称"),
                    Column.of("TYPE", SqlType.STRING, 32, "类型"),
                    Column.of("ENABLED", SqlType.BOOLEAN, "是否启用"),
                    Column.of("REMARK", SqlType.STRING, 1024, "备注"),
                    Column.of("MASTER_JSON", SqlType.CLOB, "主数据库"),
                    Column.of("SLAVES_JSON", SqlType.CLOB, "从数据库"),
                    Column.of("PARAMS", SqlType.CLOB, "初始化参数"),
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
                    Column.of("SECRET", SqlType.STRING, 128, "密钥"),
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

            var table = Table.of("X_SAAS_APPLICATION", "应用", columns, indies);

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

            var table = Table.of("X_SAAS_APPLICATION_MODULE", "应用模块", columns, indies);

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

            var table = Table.of("X_SAAS_TENANT", "租户", columns, indies);

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

            var table = Table.of("X_SAAS_TENANT_APPLICATION", "租户与应用关联关系", columns, indies);

            database.addTable(table);
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(Guidx.nextID());
        }
    }

    @Override
    @SneakyThrows
    public void upgrade(SqlExecutor executor) throws SQLException {
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // 初始化应用信息
        var applicationMapper = executor.getMapper(ApplicationMapper.class);

        // 控制面板
        var dashboardApp = new ApplicationEntity();
        dashboardApp.setId("y9llwWofojZAtx71JP1");
        dashboardApp.setCode("central-dashboard");
        dashboardApp.setName("控制中心");
        dashboardApp.setLogoBytes(IOStreamx.readBytes(Thread.currentThread().getContextClassLoader().getResourceAsStream("central/logo/central-security.png")));
        dashboardApp.setUrl("http://central-dashboard");
        dashboardApp.setContextPath("/dashboard");
        dashboardApp.setSecret("glngxOUCye6fndsaiQI");
        dashboardApp.setEnabled(Boolean.TRUE);
        dashboardApp.setRemark("用于提供套件中所有应用的管理界面");
        dashboardApp.updateCreator("syssa");

        // Saas中心
        var saasApp = new ApplicationEntity();
        saasApp.setId("UFQG4uh6DtrT4AgLA4Z");
        saasApp.setCode("central-saas");
        saasApp.setName("租户中心");
        saasApp.setLogoBytes(IOStreamx.readBytes(Thread.currentThread().getContextClassLoader().getResourceAsStream("central/logo/central-tenant.png")));
        saasApp.setUrl("http://central-saas");
        saasApp.setContextPath("/saas");
        saasApp.setSecret("btdHv1HdTTWzCEbiRdg");
        saasApp.setEnabled(Boolean.TRUE);
        saasApp.setRemark("用于维护租户、应用系统以及其租用关系等");
        saasApp.updateCreator("syssa");

        // 认证中心
        var securityApp = new ApplicationEntity();
        securityApp.setId("v788o67covIaDMn67mN");
        securityApp.setCode("central-security");
        securityApp.setName("认证中心");
        securityApp.setLogoBytes(IOStreamx.readBytes(Thread.currentThread().getContextClassLoader().getResourceAsStream("central/logo/central-security.png")));
        securityApp.setUrl("http://central-security");
        securityApp.setContextPath("/security");
        securityApp.setSecret("AkJSi2kmH7vSO5lJcvY");
        securityApp.setEnabled(Boolean.TRUE);
        securityApp.setRemark("用于统一管理全局会话");
        securityApp.updateCreator("syssa");

        // 存储中心
        var storageApp = new ApplicationEntity();
        storageApp.setId("hrnxjZFkYK4P34EZBy2");
        storageApp.setCode("central-storage");
        storageApp.setName("存储中心");
        storageApp.setLogoBytes(IOStreamx.readBytes(Thread.currentThread().getContextClassLoader().getResourceAsStream("central/logo/central-security.png")));
        storageApp.setUrl("http://central-storage");
        storageApp.setContextPath("/storage");
        storageApp.setSecret("IKJuAKOiG7Vx9tMMqUf");
        storageApp.setEnabled(Boolean.TRUE);
        storageApp.setRemark("用于统一管理文件存储");
        storageApp.updateCreator("syssa");

        // 广播中心
        var multicastApp = new ApplicationEntity();
        multicastApp.setId("5HjlG6lusTufjoRapw");
        multicastApp.setCode("central-multicast");
        multicastApp.setName("广播中心");
        multicastApp.setLogoBytes(IOStreamx.readBytes(Thread.currentThread().getContextClassLoader().getResourceAsStream("central/logo/central-security.png")));
        multicastApp.setUrl("http://central-multicast");
        multicastApp.setContextPath("/multicast");
        multicastApp.setSecret("QHO85BzFQq8hqDzREJ");
        multicastApp.setEnabled(Boolean.TRUE);
        multicastApp.setRemark("用于统一消息推送");
        multicastApp.updateCreator("syssa");

        // 网关中心
        var gatewayApp = new ApplicationEntity();
        gatewayApp.setId("4yus43AGTA7kzyrDbbG");
        gatewayApp.setCode("central-gateway");
        gatewayApp.setName("网关中心");
        gatewayApp.setLogoBytes(IOStreamx.readBytes(Thread.currentThread().getContextClassLoader().getResourceAsStream("central/logo/central-security.png")));
        gatewayApp.setUrl("http://central-gateway");
        gatewayApp.setContextPath("/gateway");
        gatewayApp.setSecret("TT6cdfpHIPastoipuDg");
        gatewayApp.setEnabled(Boolean.TRUE);
        gatewayApp.setRemark("用于统一管理网关");
        gatewayApp.updateCreator("syssa");

        applicationMapper.insertBatch(List.of(dashboardApp, saasApp, securityApp, storageApp, multicastApp, gatewayApp));

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // 初始化主数据库信息
        var databaseMapper = executor.getMapper(DatabaseMapper.class);

        var masterDatabase = new DatabaseEntity();
        masterDatabase.setId("OIJN8cob9iZzJ1NigWE");
        masterDatabase.setApplicationId(saasApp.getId());
        masterDatabase.setCode("master");
        masterDatabase.setName("主数据源");
        masterDatabase.setType(DatabaseType.MYSQL.getValue());
        masterDatabase.setEnabled(Boolean.TRUE);
        masterDatabase.setRemark("本数据源是通过配置文件初始化，请勿修改");
        masterDatabase.setMasterJson("{}");
        masterDatabase.setSlavesJson("[]");
        masterDatabase.setParams(Jsonx.Default().serialize(Map.of(
                "master", "{}",
                "slaves", List.of()
        )));
        masterDatabase.setTenantCode("master");
        masterDatabase.updateCreator("syssa");
        databaseMapper.insert(masterDatabase);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // 初始化主租户
        var tenantMapper = executor.getMapper(TenantMapper.class);

        var masterTenant = new TenantEntity();
        masterTenant.setId("2Hp475EmG4I6sNN0dUJ");
        masterTenant.setCode("master");
        masterTenant.setName("主租户");
        masterTenant.setDatabaseId(masterDatabase.getId());
        masterTenant.setEnabled(Boolean.TRUE);
        masterTenant.setRemark("本租户主要用于管理基础软硬件及其他租户数据");
        masterTenant.updateCreator("syssa");
        tenantMapper.insert(masterTenant);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // 初始化主租户的租用关系
        var relMapper = executor.getMapper(TenantApplicationMapper.class);

        var dashboardAppRel = new TenantApplicationEntity();
        dashboardAppRel.setId("95AELSyhLQy5kDheAxQ");
        dashboardAppRel.setTenantId(masterTenant.getId());
        dashboardAppRel.setApplicationId(dashboardApp.getId());
        dashboardAppRel.setEnabled(Boolean.TRUE);
        dashboardAppRel.setPrimary(Boolean.FALSE);
        dashboardAppRel.updateCreator("syssa");

        var tenantAppRel = new TenantApplicationEntity();
        tenantAppRel.setId("2B4wrNPDL80tfvSE8ve");
        tenantAppRel.setTenantId(masterTenant.getId());
        tenantAppRel.setApplicationId(saasApp.getId());
        tenantAppRel.setEnabled(Boolean.TRUE);
        tenantAppRel.setPrimary(Boolean.FALSE);
        tenantAppRel.updateCreator("syssa");

        var securityAppRel = new TenantApplicationEntity();
        securityAppRel.setId("jlUpCMrWWhUNhWHiBh5");
        securityAppRel.setTenantId(masterTenant.getId());
        securityAppRel.setApplicationId(securityApp.getId());
        securityAppRel.setEnabled(Boolean.TRUE);
        securityAppRel.setPrimary(Boolean.TRUE);
        securityAppRel.updateCreator("syssa");

        var storageAppRel = new TenantApplicationEntity();
        storageAppRel.setId("JUWCjb5i032vOjbDYLs");
        storageAppRel.setTenantId(masterTenant.getId());
        storageAppRel.setApplicationId(storageApp.getId());
        storageAppRel.setEnabled(Boolean.TRUE);
        storageAppRel.setPrimary(Boolean.FALSE);
        storageAppRel.updateCreator("syssa");

        var multicastAppRel = new TenantApplicationEntity();
        multicastAppRel.setId("pDaMEd4CZPupGLSqvc");
        multicastAppRel.setTenantId(masterTenant.getId());
        multicastAppRel.setApplicationId(multicastApp.getId());
        multicastAppRel.setEnabled(Boolean.TRUE);
        multicastAppRel.setPrimary(Boolean.FALSE);
        multicastAppRel.updateCreator("syssa");

        var gatewayAppRel = new TenantApplicationEntity();
        gatewayAppRel.setId("L1kpkzehI9oofBw2EbO");
        gatewayAppRel.setTenantId(masterTenant.getId());
        gatewayAppRel.setApplicationId(gatewayApp.getId());
        gatewayAppRel.setEnabled(Boolean.TRUE);
        gatewayAppRel.setPrimary(Boolean.FALSE);
        gatewayAppRel.updateCreator("syssa");

        relMapper.insertBatch(List.of(dashboardAppRel, tenantAppRel, securityAppRel, storageAppRel, multicastAppRel, gatewayAppRel));
    }

    @Override
    public void downgrade(Database database) throws SQLException {
        // auth
        {
            var menu = database.getTable("X_AUTH_MENU");
            if (menu != null) {
                menu.drop();
            }

            var function = database.getTable("X_AUTH_PERMISSION");
            if (function != null) {
                function.drop();
            }

            var role = database.getTable("X_AUTH_ROLE");
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
            var application = database.getTable("X_SAAS_APPLICATION");
            if (application != null) {
                application.drop();
            }

            var applicationModule = database.getTable("X_SAAS_APPLICATION_MODULE");
            if (applicationModule != null) {
                applicationModule.drop();
            }

            var tenant = database.getTable("X_SAAS_TENANT");
            if (tenant != null) {
                tenant.drop();
            }

            var tenantApplication = database.getTable("X_SAAS_TENANT_APPLICATION");
            if (tenantApplication != null) {
                tenantApplication.drop();
            }
        }
    }
}
