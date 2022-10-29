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

package central.provider.graphql.log.dto;

import central.data.log.LogPredicate;
import central.lang.Stringx;
import central.lang.reflect.TypeReference;
import central.provider.graphql.log.entity.LogCollectorEntity;
import central.provider.graphql.log.entity.LogFilterEntity;
import central.provider.graphql.log.entity.LogStorageEntity;
import central.provider.graphql.log.query.LogCollectorQuery;
import central.provider.graphql.log.query.LogStorageQuery;
import central.provider.graphql.org.dto.AccountDTO;
import central.sql.Conditions;
import central.sql.Orders;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLType;
import central.util.Jsonx;
import central.web.XForwardedHeaders;
import lombok.EqualsAndHashCode;
import org.dataloader.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.Serial;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Log Filter
 * <p>
 * 日志过滤器
 *
 * @author Alan Yeh
 * @since 2022/10/25
 */
@GraphQLType("LogFilter")
@EqualsAndHashCode(callSuper = true)
public class LogFilterDTO extends LogFilterEntity {
    @Serial
    private static final long serialVersionUID = 6277488185273187769L;

    /**
     * 采集器
     */
    @GraphQLGetter
    public List<LogCollectorDTO> getCollectors(@Autowired LogCollectorQuery query,
                                               @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return query.findBy(null, null,
                Conditions.of(LogCollectorEntity.class).eq("filter.id", this.getId()),
                Orders.of(LogCollectorEntity.class).asc(LogCollectorEntity::getCode),
                tenant);
    }

    /**
     * 存储器
     */
    @GraphQLGetter
    public List<LogStorageDTO> getStorages(@Autowired LogStorageQuery query,
                                           @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return query.findBy(null, null,
                Conditions.of(LogStorageEntity.class).eq("filter.id", this.getId()),
                Orders.of(LogStorageEntity.class).asc(LogStorageEntity::getCode),
                tenant);
    }

    /**
     * 断言
     */
    @GraphQLGetter
    public List<LogPredicate> getPredicates() {
        if (Stringx.isNullOrBlank(this.getPredicateJson())) {
            return Collections.emptyList();
        } else {
            return Jsonx.Default().deserialize(this.getPredicateJson(), TypeReference.ofList(LogPredicate.class));
        }
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
