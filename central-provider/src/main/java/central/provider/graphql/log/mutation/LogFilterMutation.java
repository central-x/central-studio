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

package central.provider.graphql.log.mutation;

import central.provider.DTO;
import central.data.log.LogFilterInput;
import central.lang.Assertx;
import central.lang.Stringx;
import central.provider.graphql.log.dto.LogFilterDTO;
import central.provider.graphql.log.entity.*;
import central.provider.graphql.log.mapper.LogCollectorFilterMapper;
import central.provider.graphql.log.mapper.LogFilterMapper;
import central.provider.graphql.log.mapper.LogStorageFilterMapper;
import central.provider.graphql.log.query.LogCollectorQuery;
import central.provider.graphql.log.query.LogStorageQuery;
import central.sql.query.Conditions;
import central.starter.graphql.annotation.GraphQLFetcher;
import central.starter.graphql.annotation.GraphQLSchema;
import central.util.Listx;
import central.validation.group.Insert;
import central.validation.group.Update;
import central.web.XForwardedHeaders;
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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Log Filter
 * <p>
 * 日志过滤器
 *
 * @author Alan Yeh
 * @since 2022/10/25
 */
@Component
@GraphQLSchema(path = "log/mutation", types = LogFilterDTO.class)
public class LogFilterMutation {
    @Setter(onMethod_ = @Autowired)
    private LogFilterMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private LogCollectorFilterMapper collectorRelMapper;

    @Setter(onMethod_ = @Autowired)
    private LogStorageFilterMapper storageRelMapper;

    /**
     * 保存数据
     *
     * @param input    数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull LogFilterDTO insert(@RequestParam @Validated({Insert.class, Default.class}) LogFilterInput input,
                                        @RequestParam String operator,
                                        @RequestHeader(XForwardedHeaders.TENANT) String tenant,
                                        @Autowired LogCollectorQuery collectorQuery,
                                        @Autowired LogStorageQuery storageQuery) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        // 标识唯一性校验
        if (this.mapper.existsBy(Conditions.of(LogFilterEntity.class).eq(LogFilterEntity::getCode, input.getCode()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在相同标识[code={}]的数据", input.getCode()));
        }

        var entity = new LogFilterEntity();
        entity.fromInput(input);
        entity.updateCreator(operator);
        this.mapper.insert(entity);

        this.saveRels(entity, collectorQuery, new HashSet<>(input.getCollectorIds()), storageQuery, new HashSet<>(input.getStorageIds()));

        return DTO.wrap(entity, LogFilterDTO.class);
    }

    /**
     * 批量保存数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<LogFilterDTO> insertBatch(@RequestParam @Validated({Insert.class, Default.class}) List<LogFilterInput> inputs,
                                                   @RequestParam String operator,
                                                   @RequestHeader(XForwardedHeaders.TENANT) String tenant,
                                                   @Autowired LogCollectorQuery collectorQuery,
                                                   @Autowired LogStorageQuery storageQuery) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        return Listx.asStream(inputs).map(it -> this.insert(it, operator, tenant, collectorQuery, storageQuery)).toList();
    }

    /**
     * 更新数据
     *
     * @param input    数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull LogFilterDTO update(@RequestParam @Validated({Update.class, Default.class}) LogFilterInput input,
                                        @RequestParam String operator,
                                        @RequestHeader(XForwardedHeaders.TENANT) String tenant,
                                        @Autowired LogCollectorQuery collectorQuery,
                                        @Autowired LogStorageQuery storageQuery) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        var entity = this.mapper.findFirstBy(Conditions.of(LogFilterEntity.class).eq(LogFilterEntity::getId, input.getId()));
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("数据[id={}]不存在", input.getId()));
        }

        // 标识唯一性校验
        if (!Objects.equals(entity.getCode(), input.getCode())) {
            if (this.mapper.existsBy(Conditions.of(LogFilterEntity.class).eq(LogFilterEntity::getCode, input.getCode()))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("已存在相同标识[code={}]的数据", input.getCode()));
            }
        }

        entity.fromInput(input);
        entity.updateModifier(operator);
        this.mapper.update(entity);

        saveRels(entity, collectorQuery, new HashSet<>(input.getCollectorIds()), storageQuery, new HashSet<>(input.getStorageIds()));

        return DTO.wrap(entity, LogFilterDTO.class);
    }

    /**
     * 批量更新数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     * @param tenant   租户标识
     */
    @GraphQLFetcher
    public @Nonnull List<LogFilterDTO> updateBatch(@RequestParam @Validated({Update.class, Default.class}) List<LogFilterInput> inputs,
                                                   @RequestParam String operator,
                                                   @RequestHeader(XForwardedHeaders.TENANT) String tenant,
                                                   @Autowired LogCollectorQuery collectorQuery,
                                                   @Autowired LogStorageQuery storageQuery) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        return Listx.asStream(inputs).map(it -> this.update(it, operator, tenant, collectorQuery, storageQuery)).toList();
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

        var effected = this.mapper.deleteBy(Conditions.of(LogFilterEntity.class).in(LogFilterEntity::getId, ids));

        // 级联删除
        this.collectorRelMapper.deleteBy(Conditions.of(LogCollectorFilterEntity.class).in(LogCollectorFilterEntity::getFilterId, ids));
        this.storageRelMapper.deleteBy(Conditions.of(LogStorageFilterEntity.class).in(LogStorageFilterEntity::getFilterId, ids));

        return effected;
    }

    /**
     * 根据条件删除数据
     *
     * @param conditions 条件
     * @param tenant     租户标识
     */
    @GraphQLFetcher
    public long deleteBy(@RequestParam Conditions<LogFilterEntity> conditions,
                         @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        Assertx.mustEquals("master", tenant, "只有主租户[master]才允许访问本接口");
        var entities = this.mapper.findBy(conditions);
        if (entities.isEmpty()) {
            return 0L;
        }

        var effected = this.mapper.deleteBy(conditions);

        // 级联删除
        var ids = entities.stream().map(LogFilterEntity::getId).toList();
        this.collectorRelMapper.deleteBy(Conditions.of(LogCollectorFilterEntity.class).in(LogCollectorFilterEntity::getFilterId, ids));
        this.storageRelMapper.deleteBy(Conditions.of(LogStorageFilterEntity.class).in(LogStorageFilterEntity::getFilterId, ids));

        return effected;
    }

    private void saveRels(LogFilterEntity entity, LogCollectorQuery collectorQuery, Set<String> collectorIds, LogStorageQuery storageQuery, Set<String> storageIds) {

        // 检查关联关系
        var collectors = collectorQuery.findBy(null, null, Conditions.of(LogCollectorEntity.class).in(LogCollectorEntity::getId, collectorIds), null, "master");
        if (collectors.size() != collectorIds.size()) {
            // 出现不存在的主键
            for (var collector : collectors) {
                collectorIds.remove(collector.getId());
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("找不到指定的采集器[id={}]", String.join(", ", collectorIds)));
        }

        var storages = storageQuery.findBy(null, null, Conditions.of(LogStorageEntity.class).in(LogStorageEntity::getId, storageIds), null, "master");
        if (storages.size() != storageIds.size()) {
            // 出现不存在的主键
            for (var storage : storages) {
                storageIds.remove(storage.getId());
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("找不到指定的存储器[id={}]", String.join(", ", storageIds)));
        }


        // 保存关联关系
        var collectorRels = collectorIds.stream().map(it -> {
            var rel = new LogCollectorFilterEntity();
            rel.setFilterId(entity.getId());
            rel.setCollectorId(it);
            rel.updateCreator(entity.getModifierId());
            return rel;
        }).toList();
        this.collectorRelMapper.deleteBy(Conditions.of(LogCollectorFilterEntity.class).eq(LogCollectorFilterEntity::getFilterId, entity.getId()));
        this.collectorRelMapper.insertBatch(collectorRels);

        var storageRels = storageIds.stream().map(it -> {
            var rel = new LogStorageFilterEntity();
            rel.setFilterId(entity.getId());
            rel.setStorageId(it);
            rel.updateCreator(entity.getModifierId());
            return rel;
        }).toList();
        this.storageRelMapper.deleteBy(Conditions.of(LogStorageFilterEntity.class).eq(LogStorageFilterEntity::getFilterId, entity.getId()));
        this.storageRelMapper.insertBatch(storageRels);
    }
}
