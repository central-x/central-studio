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

import central.provider.graphql.DTO;
import central.provider.graphql.organization.entity.DepartmentEntity;
import central.provider.graphql.organization.entity.UnitEntity;
import central.provider.graphql.organization.query.DepartmentQuery;
import central.provider.graphql.organization.query.UnitQuery;
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
 * 单位信息
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
@GraphQLType("Unit")
@EqualsAndHashCode(callSuper = true)
public class UnitDTO extends UnitEntity implements DTO {
    @Serial
    private static final long serialVersionUID = -7924414310018767718L;

    /**
     * 行政区划信息
     */
    @GraphQLGetter
    public CompletableFuture<AreaDTO> getArea(DataLoader<String, AreaDTO> loader) {
        return loader.load(this.getAreaId());
    }

    /**
     * 父单位信息
     */
    @GraphQLGetter
    public CompletableFuture<UnitDTO> getParent(DataLoader<String, UnitDTO> loader) {
        return loader.load(this.getParentId());
    }

    /**
     * 子单位信息
     */
    @GraphQLGetter
    public List<UnitDTO> getChildren(@Autowired UnitQuery query,
                                     @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return query.findBy(null, null, Conditions.of(UnitEntity.class).eq(UnitEntity::getParentId, this.getId()), Orders.of(UnitEntity.class).asc(UnitEntity::getOrder).asc(UnitEntity::getCode), tenant);
    }

    /**
     * 部门信息
     */
    @GraphQLGetter
    public List<DepartmentDTO> getDepartments(@Autowired DepartmentQuery query,
                                              @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return query.findBy(null, null, Conditions.of(DepartmentEntity.class).eq(DepartmentEntity::getUnitId, this.getId()), Orders.of(DepartmentEntity.class).asc(DepartmentEntity::getOrder).asc(DepartmentEntity::getCode), tenant);
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
