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

package central.studio.provider.graphql.authority.mutation;

import central.data.authority.RoleRangeInput;
import central.provider.graphql.DTO;
import central.sql.query.Conditions;
import central.starter.graphql.annotation.GraphQLFetcher;
import central.starter.graphql.annotation.GraphQLSchema;
import central.studio.provider.database.persistence.authority.RoleRangePersistence;
import central.studio.provider.database.persistence.authority.entity.RoleRangeEntity;
import central.studio.provider.graphql.authority.dto.RoleRangeDTO;
import central.validation.group.Insert;
import central.web.XForwardedHeaders;
import jakarta.annotation.Nonnull;
import jakarta.validation.groups.Default;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Role Range Relation Mutation
 * <p>
 * 角色与范围关联关系修改
 *
 * @author Alan Yeh
 * @since 2024/12/14
 */
@Component
@GraphQLSchema(path = "authority/mutation", types = RoleRangeDTO.class)
public class RoleRangeMutation {

    @Setter(onMethod_ = @Autowired)
    private RoleRangePersistence persistence;

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull RoleRangeDTO insert(@RequestParam @Validated({Insert.class, Default.class}) RoleRangeInput input,
                                        @RequestParam String operator,
                                        @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var data = this.persistence.insert(input, operator, tenant);
        return DTO.wrap(data, RoleRangeDTO.class);
    }

    /**
     * 批量保存数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<RoleRangeDTO> insertBatch(@RequestParam @Validated({Insert.class, Default.class}) List<RoleRangeInput> inputs,
                                                   @RequestParam String operator,
                                                   @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var data = this.persistence.insertBatch(inputs, operator, tenant);
        return DTO.wrap(data, RoleRangeDTO.class);
    }

    /**
     * 根据主键删除数据
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    @GraphQLFetcher
    public long deleteByIds(@RequestParam List<String> ids,
                            @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.persistence.deleteByIds(ids, tenant);
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public long deleteBy(@RequestParam Conditions<RoleRangeEntity> conditions,
                         @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        return this.persistence.deleteBy(conditions, tenant);
    }
}
