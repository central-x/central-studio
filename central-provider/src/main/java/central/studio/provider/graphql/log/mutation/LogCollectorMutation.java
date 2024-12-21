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

package central.studio.provider.graphql.log.mutation;

import central.data.log.LogCollectorInput;
import central.lang.Assertx;
import central.lang.Stringx;
import central.provider.graphql.DTO;
import central.studio.provider.graphql.log.dto.LogCollectorDTO;
import central.studio.provider.database.persistence.log.entity.LogCollectorEntity;
import central.studio.provider.database.persistence.log.entity.LogCollectorFilterEntity;
import central.studio.provider.database.persistence.log.mapper.LogCollectorFilterMapper;
import central.studio.provider.database.persistence.log.mapper.LogCollectorMapper;
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
import java.util.Objects;

/**
 * Log Collector
 * <p>
 * 日志采集器
 *
 * @author Alan Yeh
 * @since 2022/10/25
 */
@Component
@GraphQLSchema(path = "log/mutation", types = LogCollectorDTO.class)
public class LogCollectorMutation {
    @Setter(onMethod_ = @Autowired)
    private LogCollectorMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private LogCollectorFilterMapper relMapper;

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull LogCollectorDTO insert(@RequestParam @Validated({Insert.class, Default.class}) LogCollectorInput input,
                                           @RequestParam String operator,
                                           @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        // 标识唯一性校验
        if (this.mapper.existsBy(Conditions.of(LogCollectorEntity.class).eq(LogCollectorEntity::getCode, input.getCode()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在相同标识[code={}]的数据", input.getCode()));
        }

        var entity = new LogCollectorEntity();
        entity.fromInput(input);
        entity.updateCreator(operator);
        this.mapper.insert(entity);

        return DTO.wrap(entity, LogCollectorDTO.class);
    }

    /**
     * 批量保存数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<LogCollectorDTO> insertBatch(@RequestParam @Validated({Insert.class, Default.class}) List<LogCollectorInput> inputs,
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
    public @Nonnull LogCollectorDTO update(@RequestParam @Validated({Update.class, Default.class}) LogCollectorInput input,
                                           @RequestParam String operator,
                                           @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        var entity = this.mapper.findFirstBy(Conditions.of(LogCollectorEntity.class).eq(LogCollectorEntity::getId, input.getId()));
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("数据[id={}]不存在", input.getId()));
        }

        // 标识唯一性校验
        if (!Objects.equals(entity.getCode(), input.getCode())) {
            if (this.mapper.existsBy(Conditions.of(LogCollectorEntity.class).eq(LogCollectorEntity::getCode, input.getCode()))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在相同标识[code={}]的数据", input.getCode()));
            }
        }

        entity.fromInput(input);
        entity.updateModifier(operator);
        this.mapper.update(entity);

        return DTO.wrap(entity, LogCollectorDTO.class);
    }

    /**
     * 批量更新数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<LogCollectorDTO> updateBatch(@RequestParam @Validated({Update.class, Default.class}) List<LogCollectorInput> inputs,
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
                            @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        if (Listx.isNullOrEmpty(ids)) {
            return 0;
        }

        var effected = this.mapper.deleteBy(Conditions.of(LogCollectorEntity.class).in(LogCollectorEntity::getId, ids));

        if (effected > 0L) {
            // 级联删除
            this.relMapper.deleteBy(Conditions.of(LogCollectorFilterEntity.class).in(LogCollectorFilterEntity::getCollectorId, ids));
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
    public long deleteBy(@RequestParam Conditions<LogCollectorEntity> conditions,
                         @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");

        var entities = this.mapper.findBy(conditions);
        if (entities.isEmpty()) {
            return 0L;
        }

        var effected = this.mapper.deleteBy(conditions);

        // 级联删除
        var ids = entities.stream().map(LogCollectorEntity::getId).toList();
        this.relMapper.deleteBy(Conditions.of(LogCollectorFilterEntity.class).in(LogCollectorFilterEntity::getCollectorId, ids));

        return effected;
    }
}
