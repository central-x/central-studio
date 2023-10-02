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

package central.provider.graphql.organization.mutation;

import central.provider.graphql.DTO;
import central.data.organization.RankInput;
import central.lang.Stringx;
import central.provider.graphql.organization.dto.RankDTO;
import central.provider.graphql.organization.entity.RankEntity;
import central.provider.graphql.organization.mapper.RankMapper;
import central.sql.query.Conditions;
import central.starter.graphql.annotation.GraphQLFetcher;
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
 * Rank Mutation
 * 职级修改
 *
 * @author Alan Yeh
 * @since 2022/10/04
 */
@Component
@GraphQLSchema(path = "organization/mutation", types = RankDTO.class)
public class RankMutation {
    @Setter(onMethod_ = @Autowired)
    private RankMapper mapper;

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull RankDTO insert(@RequestParam @Validated({Insert.class, Default.class}) RankInput input,
                                   @RequestParam String operator,
                                   @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        // 标识唯一性校验
        if (this.mapper.existsBy(Conditions.of(RankEntity.class).eq(RankEntity::getCode, input.getCode()).eq(RankEntity::getTenantCode, tenant))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在相同标识[code={}]的数据", input.getCode()));
        }

        var entity = new RankEntity();
        entity.fromInput(input);
        entity.setTenantCode(tenant);
        entity.updateCreator(operator);
        this.mapper.insert(entity);

        return DTO.wrap(entity, RankDTO.class);
    }

    /**
     * 批量保存数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<RankDTO> insertBatch(@RequestParam @Validated({Insert.class, Default.class}) List<RankInput> inputs,
                                              @RequestParam String operator,
                                              @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
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
    public @Nonnull RankDTO update(@RequestParam @Validated({Update.class, Default.class}) RankInput input,
                                   @RequestParam String operator,
                                   @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var entity = this.mapper.findFirstBy(Conditions.of(RankEntity.class).eq(RankEntity::getId, input.getId()).eq(RankEntity::getTenantCode, tenant));
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("数据[id={}]不存在", input.getId()));
        }

        // 标识唯一性校验
        if (!Objects.equals(entity.getCode(), input.getCode())) {
            if (this.mapper.existsBy(Conditions.of(RankEntity.class).eq(RankEntity::getCode, input.getCode()).eq(RankEntity::getTenantCode, tenant))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在相同标识[code={}]的数据", input.getCode()));
            }
        }

        entity.fromInput(input);
        entity.updateModifier(operator);
        this.mapper.update(entity);

        return DTO.wrap(entity, RankDTO.class);
    }

    /**
     * 批量更新数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<RankDTO> updateBatch(@RequestParam @Validated({Update.class, Default.class}) List<RankInput> inputs,
                                              @RequestParam String operator,
                                              @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
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
                            @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        if (Listx.isNullOrEmpty(ids)) {
            return 0;
        }

        // TODO 级联删除？
        return this.mapper.deleteBy(Conditions.of(RankEntity.class).in(RankEntity::getId, ids).eq(RankEntity::getTenantCode, tenant));
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public long deleteBy(@RequestParam Conditions<RankEntity> conditions,
                         @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        conditions = Conditions.group(conditions).eq(RankEntity::getTenantCode, tenant);

        // TODO 级联删除？
        return this.mapper.deleteBy(conditions);
    }
}
