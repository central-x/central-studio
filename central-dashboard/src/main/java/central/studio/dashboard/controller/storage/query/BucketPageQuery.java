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

package central.studio.dashboard.controller.storage.query;

import central.data.organization.option.AreaType;
import central.data.storage.StorageBucket;
import central.lang.Stringx;
import central.sql.query.Conditions;
import central.starter.web.query.PageQuery;
import central.validation.Enums;
import central.validation.Label;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @author Alan Yeh
 * @since 2024/10/29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BucketPageQuery extends PageQuery<StorageBucket> {
    @Serial
    private static final long serialVersionUID = -5018132706306974456L;

    @Label("主键")
    private String id;

    @Label("应用主键")
    private String applicationId;

    @Label("名称")
    private String name;

    @Label("区划代码")
    private String code;

    @Label("类型")
    @Enums(value = AreaType.class)
    private String type;

    @Override
    public Conditions<StorageBucket> build() {
        var conditions = Conditions.of(StorageBucket.class);

        // 精确字段搜索
        if (Stringx.isNotEmpty(this.getId())) {
            conditions.eq(StorageBucket::getId, this.getId());
        }
        if (Stringx.isNotEmpty(this.getApplicationId())) {
            conditions.eq(StorageBucket::getApplicationId, this.getApplicationId());
        }
        if (Stringx.isNotEmpty(this.getName())) {
            conditions.like(StorageBucket::getName, this.getName());
        }
        if (Stringx.isNotEmpty(this.getCode())) {
            conditions.like(StorageBucket::getCode, this.getCode());
        }

        // 模糊搜索
        for (String keyword : this.getKeywords()) {
            conditions.and(filter -> filter.like(StorageBucket::getCode, keyword).or().like(StorageBucket::getName, keyword));
        }
        return conditions;
    }
}
