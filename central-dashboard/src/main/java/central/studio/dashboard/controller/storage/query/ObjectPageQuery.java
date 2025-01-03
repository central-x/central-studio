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

import central.data.storage.StorageObject;
import central.lang.Stringx;
import central.sql.query.Conditions;
import central.starter.web.query.PageQuery;
import central.validation.Label;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * Storage Object Page Query
 * <p>
 * 存储对象分页查询
 *
 * @author Alan Yeh
 * @since 2024/11/01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ObjectPageQuery extends PageQuery<StorageObject> {
    @Serial
    private static final long serialVersionUID = 7917881061907979327L;

    @Label("存储桶主键")
    @NotBlank
    @Size(min = 1, max = 32)
    private String bucketId;

    @Label("文件名称")
    private String name;

    @Label("确认状态")
    private Boolean confirmed;

    @Override
    public Conditions<StorageObject> build() {
        var conditions = Conditions.of(StorageObject.class).eq(StorageObject::getBucketId, this.getBucketId());

        // 精确字段搜索
        if (Stringx.isNotEmpty(this.getName())) {
            conditions.like(StorageObject::getName, this.getName());
        }
        if (this.getConfirmed() != null) {
            conditions.eq(StorageObject::getConfirmed, this.getConfirmed());
        }

        // 模糊搜索
        for (var keyword : this.getKeywords()) {
            conditions.and(filter -> filter.like(StorageObject::getName, keyword));
        }

        return conditions;
    }
}
