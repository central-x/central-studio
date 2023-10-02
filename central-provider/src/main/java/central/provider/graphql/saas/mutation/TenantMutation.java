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

package central.provider.graphql.saas.mutation;

import central.provider.graphql.DTO;
import central.data.saas.TenantInput;
import central.lang.Assertx;
import central.lang.Stringx;
import central.provider.graphql.saas.dto.TenantDTO;
import central.provider.graphql.saas.entity.TenantApplicationEntity;
import central.provider.graphql.saas.entity.TenantEntity;
import central.provider.graphql.saas.mapper.TenantMapper;
import central.sql.query.Conditions;
import central.starter.graphql.annotation.GraphQLFetcher;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLSchema;
import central.web.XForwardedHeaders;
import central.util.Listx;
import central.validation.group.Insert;
import central.validation.group.Update;
import jakarta.validation.groups.Default;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

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
    private TenantMapper mapper;

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
        // 标识唯一性校验
        if (this.mapper.existsBy(Conditions.of(TenantEntity.class).eq(TenantEntity::getCode, input.getCode()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在相同标识[code={}]的数据", input.getCode()));
        }

        var entity = new TenantEntity();
        entity.fromInput(input);
        entity.updateCreator(operator);
        this.mapper.insert(entity);

        return DTO.wrap(entity, TenantDTO.class);
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
        return Listx.asStream(inputs).map(it -> this.insert(it, operator, tenant)).toList();
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
        var entity = this.mapper.findFirstBy(Conditions.of(TenantEntity.class).eq(TenantEntity::getId, input.getId()));
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("数据[id={}]不存在", input.getId()));
        }

        // 标识唯一性校验
        if (!Objects.equals(entity.getCode(), input.getCode())) {
            if (this.mapper.existsBy(Conditions.of(TenantEntity.class).eq(TenantEntity::getCode, input.getCode()))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在相同标识[code={}]的数据", input.getCode()));
            }
        }

        entity.fromInput(input);
        entity.updateModifier(operator);
        this.mapper.update(entity);

        return DTO.wrap(entity, TenantDTO.class);
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
        return Listx.asStream(inputs).map(it -> this.update(it, operator, tenant)).toList();
    }

    /**
     * 根据主键删除数据
     *
     * @param ids    主键
     * @param tenant 租户标识
     */
    @GraphQLFetcher
    public long deleteByIds(@RequestParam List<String> ids,
                            @RequestHeader(XForwardedHeaders.TENANT) String tenant,
                            @Autowired TenantApplicationMutation mutation) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        if (Listx.isNullOrEmpty(ids)) {
            return 0L;
        }

        var effected = this.mapper.deleteByIds(ids);
        if (effected > 0L) {
            // 级联删除
            mutation.deleteBy(Conditions.of(TenantApplicationEntity.class).in(TenantApplicationEntity::getTenantId, ids), tenant);
        }
        return effected;
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public long deleteBy(@RequestParam Conditions<TenantEntity> conditions,
                         @RequestHeader(XForwardedHeaders.TENANT) String tenant,
                         @Autowired TenantApplicationMutation mutation) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        var entities = this.mapper.findBy(conditions);
        if (entities.isEmpty()) {
            return 0L;
        }
        var effected = this.mapper.deleteBy(conditions);
        // 级联删除
        mutation.deleteBy(Conditions.of(TenantApplicationEntity.class).in(TenantApplicationEntity::getTenantId, entities.stream().map(TenantEntity::getId).toList()), tenant);
        return effected;
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
