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

package central.studio.provider.graphql.multicast.mutation;

import central.provider.graphql.DTO;
import central.data.multicast.MulticastMessageInput;
import central.lang.Stringx;
import central.studio.provider.graphql.multicast.dto.MulticastMessageDTO;
import central.studio.provider.database.persistence.multicast.entity.MulticastMessageEntity;
import central.studio.provider.database.persistence.multicast.mapper.MulticastMessageMapper;
import central.sql.query.Conditions;
import central.starter.graphql.annotation.GraphQLFetcher;
import central.starter.graphql.annotation.GraphQLSchema;
import central.util.Listx;
import central.validation.group.Insert;
import central.validation.group.Update;
import central.web.XForwardedHeaders;
import jakarta.annotation.Nonnull;
import jakarta.validation.groups.Default;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Multicast Message
 * <p>
 * 广播消息
 *
 * @author Alan Yeh
 * @since 2022/11/04
 */
@Component
@GraphQLSchema(path = "multicast/mutation", types = MulticastMessageDTO.class)
public class MulticastMessageMutation {
    @Setter(onMethod_ = @Autowired)
    private MulticastMessageMapper mapper;

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull MulticastMessageDTO insert(@RequestParam @Validated({Insert.class, Default.class}) MulticastMessageInput input,
                                               @RequestParam String operator,
                                               @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var entity = new MulticastMessageEntity();
        entity.fromInput(input);
        entity.setTenantCode(tenant);
        entity.updateCreator(operator);
        this.mapper.insert(entity);

        return DTO.wrap(entity, MulticastMessageDTO.class);
    }

    /**
     * 批量保存数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<MulticastMessageDTO> insertBatch(@RequestParam @Validated({Insert.class, Default.class}) List<MulticastMessageInput> inputs,
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
    public @Nonnull MulticastMessageDTO update(@RequestParam @Validated({Update.class, Default.class}) MulticastMessageInput input,
                                                   @RequestParam String operator,
                                                   @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var entity = this.mapper.findFirstBy(Conditions.of(MulticastMessageEntity.class).eq(MulticastMessageEntity::getId, input.getId()).eq(MulticastMessageEntity::getTenantCode, tenant));
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("数据[id={}]不存在", input.getId()));
        }

        entity.fromInput(input);
        entity.setTenantCode(tenant);
        entity.updateModifier(operator);
        this.mapper.update(entity);

        return DTO.wrap(entity, MulticastMessageDTO.class);
    }

    /**
     * 批量更新数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<MulticastMessageDTO> updateBatch(@RequestParam @Validated({Update.class, Default.class}) List<MulticastMessageInput> inputs,
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

        return this.mapper.deleteBy(Conditions.of(MulticastMessageEntity.class).in(MulticastMessageEntity::getId, ids).eq(MulticastMessageEntity::getTenantCode, tenant));
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public long deleteBy(@RequestParam Conditions<MulticastMessageEntity> conditions,
                         @RequestHeader(XForwardedHeaders.TENANT) String tenant) {

        var entities = this.mapper.findBy(Conditions.group(conditions).eq(MulticastMessageEntity::getTenantCode, tenant));
        if (entities.isEmpty()) {
            return 0L;
        }

        return this.mapper.deleteBy(conditions);
    }
}
