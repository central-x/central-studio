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

package central.studio.provider.graphql.saas.mutation;

import central.data.saas.TenantInput;
import central.lang.Assertx;
import central.provider.graphql.DTO;
import central.sql.query.Conditions;
import central.starter.graphql.annotation.GraphQLFetcher;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLSchema;
import central.studio.provider.database.persistence.saas.TenantPersistence;
import central.studio.provider.database.persistence.saas.entity.TenantEntity;
import central.studio.provider.graphql.saas.dto.TenantDTO;
import central.validation.group.Insert;
import central.validation.group.Update;
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
 * Tenant Mutation
 * <p>
 * 租户修改
 *
 * @author Alan Yeh
 * @since 2022/10/04
 */
@Component
@GraphQLSchema(path = "saas/mutation", types = {TenantDTO.class, TenantApplicationMutation.class})
public class TenantMutation {

    @Setter(onMethod_ = @Autowired)
    private TenantPersistence persistence;

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull TenantDTO insert(@RequestParam @Validated({Insert.class, Default.class}) TenantInput input,
                                     @RequestParam String operator,
                                     @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");

        var data = this.persistence.insert(input, operator);
        return DTO.wrap(data, TenantDTO.class);
    }

    /**
     * 批量保存数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<TenantDTO> insertBatch(@RequestParam @Validated({Insert.class, Default.class}) List<TenantInput> inputs,
                                                @RequestParam String operator,
                                                @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");

        var data = this.persistence.insertBatch(inputs, operator);
        return DTO.wrap(data, TenantDTO.class);
    }

    /**
     * 更新数据
     *
     * @param input    数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull TenantDTO update(@RequestParam @Validated({Update.class, Default.class}) TenantInput input,
                                     @RequestParam String operator,
                                     @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");

        var data = this.persistence.update(input, operator);
        return DTO.wrap(data, TenantDTO.class);
    }

    /**
     * 批量更新数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<TenantDTO> updateBatch(@RequestParam @Validated({Update.class, Default.class}) List<TenantInput> inputs,
                                                @RequestParam String operator,
                                                @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");

        var data = this.persistence.updateBatch(inputs, operator);
        return DTO.wrap(data, TenantDTO.class);
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
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");

        return this.persistence.deleteByIds(ids);
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public long deleteBy(@RequestParam Conditions<TenantEntity> conditions,
                         @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");

        return this.persistence.deleteBy(conditions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 关联查询
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Tenant Application Mutation
     * 租户与应用关联关系修改
     */
    @GraphQLGetter
    public TenantApplicationMutation getApplications(@Autowired TenantApplicationMutation mutation) {
        return mutation;
    }
}
