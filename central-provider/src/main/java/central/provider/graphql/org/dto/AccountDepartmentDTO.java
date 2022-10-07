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

package central.provider.graphql.org.dto;

import central.api.DTO;
import central.provider.graphql.org.entity.AccountDepartmentEntity;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLType;
import lombok.EqualsAndHashCode;
import org.dataloader.DataLoader;

import java.io.Serial;
import java.util.concurrent.CompletableFuture;

/**
 * 帐户部门关联关系
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
@GraphQLType("AccountDepartment")
@EqualsAndHashCode(callSuper = true)
public class AccountDepartmentDTO extends AccountDepartmentEntity implements DTO {
    @Serial
    private static final long serialVersionUID = 4975814618027780723L;

    /**
     * 帐户
     */
    @GraphQLGetter
    public CompletableFuture<AccountDTO> getAccount(DataLoader<String, AccountDTO> loader) {
        return loader.load(this.getAccountId());
    }

    /**
     * 单位
     */
    @GraphQLGetter
    public CompletableFuture<UnitDTO> getUnit(DataLoader<String, UnitDTO> loader) {
        return loader.load(this.getUnitId());
    }

    /**
     * 部门
     */
    @GraphQLGetter
    public CompletableFuture<DepartmentDTO> getDepartment(DataLoader<String, DepartmentDTO> loader) {
        return loader.load(this.getDepartmentId());
    }

    /**
     * 创建人信息
     */
    @GraphQLGetter
    public CompletableFuture<AccountDTO> getCreator(DataLoader<String, AccountDTO> loader) {
        return loader.load(this.getCreatorId());
    }
}
