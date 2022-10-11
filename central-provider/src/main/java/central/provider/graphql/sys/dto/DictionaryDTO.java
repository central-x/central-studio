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

package central.provider.graphql.sys.dto;

import central.api.DTO;
import central.provider.graphql.org.dto.AccountDTO;
import central.provider.graphql.sys.entity.DictionaryEntity;
import central.provider.graphql.sys.entity.DictionaryItemEntity;
import central.provider.graphql.sys.query.DictionaryItemQuery;
import central.provider.graphql.ten.dto.ApplicationDTO;
import central.sql.Conditions;
import central.sql.Orders;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLType;
import central.starter.web.http.XForwardedHeaders;
import lombok.EqualsAndHashCode;
import org.dataloader.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.Serial;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 字典信息
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
@GraphQLType("Dictionary")
@EqualsAndHashCode(callSuper = true)
public class DictionaryDTO extends DictionaryEntity implements DTO {
    @Serial
    private static final long serialVersionUID = 7042129479380372585L;

    /**
     * 应用
     */
    @GraphQLGetter
    public CompletableFuture<ApplicationDTO> getApplication(DataLoader<String, ApplicationDTO> loader) {
        return loader.load(this.getApplicationId());
    }

    /**
     * 字典项
     */
    @GraphQLGetter
    public List<DictionaryItemDTO> getItems(@Autowired DictionaryItemQuery query,
                                            @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return query.findBy(null, null,
                Conditions.of(DictionaryItemEntity.class).eq(DictionaryItemEntity::getDictionaryId, this.getId()),
                Orders.of(DictionaryItemEntity.class).asc(DictionaryItemEntity::getOrder).asc(DictionaryItemEntity::getCode),
                tenant);
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
