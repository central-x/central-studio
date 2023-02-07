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

import central.api.DTO;
import central.data.organization.AccountInput;
import central.lang.Stringx;
import central.provider.graphql.organization.dto.AccountDTO;
import central.provider.graphql.organization.entity.AccountDepartmentEntity;
import central.provider.graphql.organization.entity.AccountEntity;
import central.provider.graphql.organization.entity.AccountUnitEntity;
import central.provider.graphql.organization.mapper.AccountMapper;
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
 * Account Mutation
 * 帐户修改
 *
 * @author Alan Yeh
 * @since 2022/10/03
 */
@Component
@GraphQLSchema(path = "organization/mutation", types = {AccountDTO.class, AccountUnitMutation.class})
public class AccountMutation {
    @Setter(onMethod_ = @Autowired)
    private AccountMapper mapper;

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull AccountDTO insert(@RequestParam @Validated({Insert.class, Default.class}) AccountInput input,
                                      @RequestParam String operator,
                                      @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        // 帐号唯一性校验
        if (this.mapper.existsBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getUsername, input.getUsername()).eq(AccountEntity::getTenantCode, tenant))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在帐号[username={}]", input.getUsername()));
        }

        // 邮箱唯一性校验
        if (this.mapper.existsBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getEmail, input.getEmail()).eq(AccountEntity::getTenantCode, tenant))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在帐号[email={}]", input.getEmail()));
        }

        // 手机号唯一性校验
        if (this.mapper.existsBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getMobile, input.getMobile()).eq(AccountEntity::getTenantCode, tenant))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在帐号[mobile={}]", input.getMobile()));
        }

        var entity = new AccountEntity();
        entity.fromInput(input);
        entity.setAdmin(Boolean.FALSE);
        entity.setTenantCode(tenant);
        entity.updateCreator(operator);
        this.mapper.insert(entity);

        return DTO.wrap(entity, AccountDTO.class);
    }

    /**
     * 批量保存数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<AccountDTO> insertBatch(@RequestParam @Validated({Insert.class, Default.class}) List<AccountInput> inputs,
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
    public @Nonnull AccountDTO update(@RequestParam @Validated({Update.class, Default.class}) AccountInput input,
                                      @RequestParam String operator,
                                      @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var entity = this.mapper.findFirstBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getId, input.getId()).eq(AccountEntity::getTenantCode, tenant));
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("数据[id={}]不存在", input.getId()));
        }

        // 帐号唯一性校验
        if (!Objects.equals(entity.getUsername(), input.getUsername())) {
            if (this.mapper.existsBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getUsername, input.getUsername()).eq(AccountEntity::getTenantCode, tenant))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在帐号[username={}]", input.getUsername()));
            }
        }

        // 邮箱唯一性校验
        if (Stringx.isNotBlank(input.getEmail()) && !Objects.equals(entity.getEmail(), input.getEmail())) {
            if (this.mapper.existsBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getEmail, input.getEmail()).eq(AccountEntity::getTenantCode, tenant))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在帐号[email={}]", input.getEmail()));
            }
        }

        // 手机号唯一性校验
        if (Stringx.isNotBlank(input.getMobile()) && !Objects.equals(entity.getMobile(), input.getMobile())) {
            if (this.mapper.existsBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getMobile, input.getMobile()).eq(AccountEntity::getTenantCode, tenant))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在帐号[mobile={}]", input.getMobile()));
            }
        }

        entity.fromInput(input);
        entity.updateModifier(operator);
        this.mapper.update(entity);

        return DTO.wrap(entity, AccountDTO.class);
    }

    /**
     * 批量更新数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<AccountDTO> updateBatch(@RequestParam @Validated({Update.class, Default.class}) List<AccountInput> inputs,
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
                            @RequestHeader(XForwardedHeaders.TENANT) String tenant,
                            @Autowired AccountUnitMutation unitMutation,
                            @Autowired AccountDepartmentMutation departmentMutation) {
        if (Listx.isNullOrEmpty(ids)) {
            return 0;
        }

        var effected = this.mapper.deleteBy(Conditions.of(AccountEntity.class).in(AccountEntity::getId, ids).eq(AccountEntity::getTenantCode, tenant));
        if (effected > 0L) {
            // 级联删除
            unitMutation.deleteBy(Conditions.of(AccountUnitEntity.class).in(AccountUnitEntity::getAccountId, ids), tenant);
            departmentMutation.deleteBy(Conditions.of(AccountDepartmentEntity.class).in(AccountDepartmentEntity::getAccountId, ids), tenant);
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
    public long deleteBy(@RequestParam Conditions<AccountEntity> conditions,
                         @RequestHeader(XForwardedHeaders.TENANT) String tenant,
                         @Autowired AccountUnitMutation unitMutation,
                         @Autowired AccountDepartmentMutation departmentMutation) {
        conditions = Conditions.group(conditions).eq(AccountEntity::getTenantCode, tenant);

        var entities = this.mapper.findBy(conditions);
        if (entities.isEmpty()) {
            return 0L;
        }

        var effected = this.mapper.deleteBy(conditions);

        // 级联删除
        var ids = entities.stream().map(AccountEntity::getId).toList();
        unitMutation.deleteBy(Conditions.of(AccountUnitEntity.class).in(AccountUnitEntity::getAccountId, ids), tenant);
        departmentMutation.deleteBy(Conditions.of(AccountDepartmentEntity.class).in(AccountDepartmentEntity::getAccountId, ids), tenant);

        return effected;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 关联查询
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Account Unit Mutation
     * 帐户与单位关联关系修改
     */
    @GraphQLGetter
    public AccountUnitMutation getUnits(@Autowired AccountUnitMutation mutation) {
        return mutation;
    }
}
