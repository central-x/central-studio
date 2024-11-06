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

package central.studio.dashboard.controller.multicast.query;

import central.data.multicast.MulticastBroadcaster;
import central.data.organization.option.AreaType;
import central.lang.Stringx;
import central.sql.query.Conditions;
import central.starter.web.query.PageQuery;
import central.validation.Enums;
import central.validation.Label;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * Broadcaster Page Query
 * <p>
 * 广播器分页查询
 *
 * @author Alan Yeh
 * @since 2024/11/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BroadcasterPageQuery extends PageQuery<MulticastBroadcaster> {
    @Serial
    private static final long serialVersionUID = 4377760817013978860L;

    @Label("主键")
    private String id;

    @Label("应用主键")
    private String applicationId;

    @Label("名称")
    private String name;

    @Label("标识")
    private String code;

    @Label("类型")
    @Enums(value = AreaType.class)
    private String type;

    @Override
    public Conditions<MulticastBroadcaster> build() {
        var conditions = Conditions.of(MulticastBroadcaster.class);

        // 精确字段搜索
        if (Stringx.isNotEmpty(this.getId())) {
            conditions.eq(MulticastBroadcaster::getId, this.getId());
        }
        if (Stringx.isNotEmpty(this.getApplicationId())) {
            conditions.eq(MulticastBroadcaster::getApplicationId, this.getApplicationId());
        }
        if (Stringx.isNotEmpty(this.getName())) {
            conditions.like(MulticastBroadcaster::getName, this.getName());
        }
        if (Stringx.isNotEmpty(this.getCode())) {
            conditions.like(MulticastBroadcaster::getCode, this.getCode());
        }

        // 模糊搜索
        for (String keyword : this.getKeywords()) {
            conditions.and(filter -> filter.like(MulticastBroadcaster::getCode, keyword).or().like(MulticastBroadcaster::getName, keyword));
        }
        return conditions;
    }
}
