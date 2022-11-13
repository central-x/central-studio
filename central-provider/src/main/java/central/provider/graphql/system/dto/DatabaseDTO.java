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

package central.provider.graphql.system.dto;

import central.api.DTO;
import central.data.system.DatabaseProperties;
import central.lang.Stringx;
import central.lang.reflect.TypeReference;
import central.provider.graphql.organization.dto.AccountDTO;
import central.provider.graphql.system.entity.DatabaseEntity;
import central.provider.graphql.saas.dto.ApplicationDTO;
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
 * 数据库信息
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
@GraphQLType("Database")
@EqualsAndHashCode(callSuper = true)
public class DatabaseDTO extends DatabaseEntity implements DTO {
    @Serial
    private static final long serialVersionUID = -9060113532481068358L;

    /**
     * 应用
     */
    @GraphQLGetter
    public CompletableFuture<ApplicationDTO> getApplication(DataLoader<String, ApplicationDTO> loader) {
        return loader.load(this.getApplicationId());
    }

    /**
     * 获取主数据库属性
     */
    @GraphQLGetter
    public DatabaseProperties getMaster() {
        return Jsonx.Default().deserialize(this.getMasterJson(), DatabaseProperties.class);
    }

    /**
     * 获取从数据库属性
     */
    @GraphQLGetter
    public List<DatabaseProperties> getSlaves() {
        if (Stringx.isNullOrBlank(this.getSlavesJson())) {
            return Collections.emptyList();
        } else {
            return Jsonx.Default().deserialize(this.getSlavesJson(), TypeReference.ofList(DatabaseProperties.class));
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
