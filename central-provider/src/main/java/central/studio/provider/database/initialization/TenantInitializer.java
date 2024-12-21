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

package central.studio.provider.database.initialization;

import central.bean.InitializeException;
import central.lang.Stringx;
import central.sql.SqlExecutor;
import central.sql.query.Conditions;
import central.studio.provider.database.persistence.saas.entity.TenantApplicationEntity;
import central.studio.provider.database.persistence.saas.entity.TenantEntity;
import central.studio.provider.database.persistence.saas.mapper.ApplicationMapper;
import central.studio.provider.database.persistence.saas.mapper.TenantApplicationMapper;
import central.studio.provider.database.persistence.saas.mapper.TenantMapper;
import lombok.RequiredArgsConstructor;

/**
 * 租户数据初始化
 *
 * @author Alan Yeh
 * @since 2024/06/22
 */
@RequiredArgsConstructor
public class TenantInitializer {
    private final SqlExecutor executor;

    /**
     * 初始化
     *
     * @param tenant 租户标识
     */
    public void initialize(String tenant) {
        var tenantMapper = this.executor.getMapper(TenantMapper.class);
        var applicationMapper = this.executor.getMapper(ApplicationMapper.class);
        var relMapper = this.executor.getMapper(TenantApplicationMapper.class);

        var tenantEntity = tenantMapper.findFirstBy(Conditions.of(TenantEntity.class).eq(TenantEntity::getCode, tenant));
        if (tenantEntity == null) {
            throw new InitializeException(Stringx.format("找不到指定租户[code={}]", tenant));
        }

        // 查找租户租用的应用
        var relations = relMapper.findBy(Conditions.of(TenantApplicationEntity.class).eq(TenantApplicationEntity::getTenantId, tenantEntity.getId()));

        // 初始化每个应用的业务数据
        var applicationInitializer = new ApplicationInitializer(this.executor);
        for (var relation : relations) {
            var applicationEntity = applicationMapper.findById(relation.getApplicationId());
            applicationInitializer.initialize(tenant, applicationEntity);
        }
    }
}
