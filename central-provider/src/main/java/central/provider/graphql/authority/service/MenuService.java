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

package central.provider.graphql.authority.service;

import central.api.DTO;
import central.bean.Page;
import central.lang.Stringx;
import central.provider.graphql.authority.dto.MenuDTO;
import central.provider.graphql.authority.entity.MenuEntity;
import central.provider.graphql.authority.mapper.MenuMapper;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.util.Listx;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * 菜单服务
 *
 * @author Alan Yeh
 * @since 2023/02/07
 */
@Component
public class MenuService {
    @Setter(onMethod_ = @Autowired)
    private MenuMapper mapper;

    public @Nullable MenuDTO findById(@Nullable String id,
                                      @Nullable Columns<MenuDTO> columns,
                                      @Nonnull String tenant) {
        if (Stringx.isNullOrBlank(id)) {
            return null;
        }
        var entity = this.mapper.findFirstBy(columns, Conditions.of(MenuEntity.class).eq(MenuEntity::getId, id).eq(MenuEntity::getTenantCode, tenant));
        return DTO.wrap(entity, MenuDTO.class);
    }

    public @Nonnull List<MenuDTO> findByIds(@Nullable List<String> ids,
                                            @Nullable Columns<MenuDTO> columns,
                                            @Nonnull String tenant) {
        if (Listx.isNullOrEmpty(ids)) {
            return Collections.emptyList();
        }

        var entities = this.mapper.findBy(columns, Conditions.of(MenuEntity.class).in(MenuEntity::getId, ids).eq(MenuEntity::getTenantCode, tenant));

        return DTO.wrap(entities, MenuDTO.class);
    }

    public @Nonnull List<MenuDTO> findBy(@Nullable Long limit,
                                         @Nullable Long offset,
                                         @Nullable Columns<MenuDTO> columns,
                                         @Nullable Conditions<MenuDTO> conditions,
                                         @Nullable Orders<MenuDTO> orders,
                                         @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(MenuEntity::getTenantCode, tenant);
        var list = this.mapper.findBy(limit, offset, columns, conditions, orders);
        return DTO.wrap(list, MenuDTO.class);
    }

    public @Nonnull Page<MenuDTO> pageBy(@Nonnull Long pageIndex,
                                         @Nonnull Long pageSize,
                                         @Nullable Columns<MenuDTO> columns,
                                         @Nullable Conditions<MenuDTO> conditions,
                                         @Nullable Orders<MenuDTO> orders,
                                         @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(MenuEntity::getTenantCode, tenant);
        var page = this.mapper.findPageBy(pageIndex, pageSize, columns, conditions, orders);
        return DTO.wrap(page, MenuDTO.class);
    }

    public Long countBy(@Nullable Conditions<MenuDTO> conditions,
                        @Nonnull String tenant) {
        conditions = Conditions.group(conditions).eq(MenuEntity::getTenantCode, tenant);
        return this.mapper.countBy(conditions);
    }
}
