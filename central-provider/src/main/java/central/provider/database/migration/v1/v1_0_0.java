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

import central.data.sys.option.DatabaseType;
import central.io.IOStreamx;
import central.provider.graphql.sys.entity.DatabaseEntity;
import central.provider.graphql.sys.mapper.DatabaseMapper;
import central.provider.graphql.ten.entity.ApplicationEntity;
import central.provider.graphql.ten.entity.TenantApplicationEntity;
import central.provider.graphql.ten.entity.TenantEntity;
import central.provider.graphql.ten.mapper.ApplicationMapper;
import central.provider.graphql.ten.mapper.TenantApplicationMapper;
import central.provider.graphql.ten.mapper.TenantMapper;
import central.sql.SqlExecutor;
import central.sql.SqlType;
import central.sql.datasource.migration.*;
import central.util.Guidx;
import central.util.Version;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

/**
 * 初始化基础数据结构
 *
 * @author Alan Yeh
 * @since 2022/09/26
 */
public class v1_0_0 extends Migration {
    public v1_0_0() {
        super(Version.of("1.0.0"));
    }
}
