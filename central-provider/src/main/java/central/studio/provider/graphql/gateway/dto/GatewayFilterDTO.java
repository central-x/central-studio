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

package central.studio.provider.graphql.gateway.dto;

import central.data.gateway.GatewayPredicate;
import central.lang.Stringx;
import central.lang.reflect.TypeRef;
import central.studio.provider.graphql.gateway.entity.GatewayFilterEntity;
import central.studio.provider.graphql.organization.dto.AccountDTO;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLType;
import central.util.Jsonx;
import lombok.EqualsAndHashCode;
import org.dataloader.DataLoader;

import java.io.Serial;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Gateway Filter
 * <p>
 * 网关过滤器
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
@GraphQLType("GatewayFilter")
@EqualsAndHashCode(callSuper = true)
public class GatewayFilterDTO extends GatewayFilterEntity {
    @Serial
    private static final long serialVersionUID = 8209052600414051442L;

    /**
     * 断言
     */
    @GraphQLGetter
    public List<GatewayPredicate> getPredicates() {
        if (Stringx.isNullOrEmpty(this.getPredicateJson())) {
            return Collections.emptyList();
        }
        return Jsonx.Default().deserialize(this.getPredicateJson(), TypeRef.ofList(GatewayPredicate.class));
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
