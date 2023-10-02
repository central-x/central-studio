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

package central.provider.graphql.saas.dto;

import central.provider.graphql.DTO;
import central.provider.graphql.organization.dto.AccountDTO;
import central.provider.graphql.system.dto.DatabaseDTO;
import central.provider.graphql.saas.entity.TenantApplicationEntity;
import central.provider.graphql.saas.entity.TenantEntity;
import central.provider.graphql.saas.query.TenantApplicationQuery;
import central.sql.query.Conditions;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLType;
import central.web.XForwardedHeaders;
import lombok.EqualsAndHashCode;
import org.dataloader.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.Serial;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 租户信息
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
@GraphQLType("Tenant")
@EqualsAndHashCode(callSuper = true)
public class TenantDTO extends TenantEntity implements DTO {
    @Serial
    private static final long serialVersionUID = 1074908736228849278L;

    /**
     * 数据库信息
     */
    @GraphQLGetter
    public CompletableFuture<DatabaseDTO> getDatabase(DataLoader<String, DatabaseDTO> loader) {
        return loader.load(this.getDatabaseId());
    }

    /**
     * 获取应用信息
     */
    @GraphQLGetter
    public List<TenantApplicationDTO> getApplications(@Autowired TenantApplicationQuery query,
                                                      @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return query.findBy(null, null, Conditions.of(TenantApplicationEntity.class).eq(TenantApplicationEntity::getTenantId, this.getId()), null, tenant);
    }

    /**
     * 创建人信息
     */
    @GraphQLGetter
    public CompletableFuture<AccountDTO> getCreator(DataLoader<String, AccountDTO> loader) {
        return loader.load(this.getCreatorId());
    }

    /**
     * 修改人信息
     */
    @GraphQLGetter
    public CompletableFuture<AccountDTO> getModifier(DataLoader<String, AccountDTO> loader) {
        return loader.load(this.getModifierId());
    }
}
