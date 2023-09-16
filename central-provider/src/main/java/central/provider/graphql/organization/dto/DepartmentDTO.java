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

package central.provider.graphql.organization.dto;

import central.provider.DTO;
import central.provider.graphql.organization.entity.DepartmentEntity;
import central.provider.graphql.organization.query.DepartmentQuery;
import central.sql.query.Conditions;
import central.sql.query.Orders;
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
 * 部门信息
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
@GraphQLType("Department")
@EqualsAndHashCode(callSuper = true)
public class DepartmentDTO extends DepartmentEntity implements DTO {
    @Serial
    private static final long serialVersionUID = -5935775736872065997L;

    /**
     * 单位
     */
    @GraphQLGetter
    public CompletableFuture<UnitDTO> getUnit(DataLoader<String, UnitDTO> loader) {
        return loader.load(this.getUnitId());
    }

    /**
     * 父部门信息
     */
    @GraphQLGetter
    public CompletableFuture<DepartmentDTO> getParent(DataLoader<String, DepartmentDTO> loader) {
        return loader.load(this.getParentId());
    }

    /**
     * 子部门
     */
    @GraphQLGetter
    public List<DepartmentDTO> getChildren(@Autowired DepartmentQuery query,
                                           @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return query.findBy(null, null, Conditions.of(DepartmentEntity.class).eq(DepartmentEntity::getParentId, this.getId()), Orders.of(DepartmentEntity.class).asc(DepartmentEntity::getOrder).asc(DepartmentEntity::getCode), tenant);
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
