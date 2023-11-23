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

package central.data.system;

import central.bean.Codeable;
import central.bean.Nonnull;
import central.bean.Orderable;
import central.data.organization.Account;
import central.sql.data.ModifiableEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * 字典项
 *
 * @author Alan Yeh
 * @since 2022/09/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DictionaryItem extends ModifiableEntity implements Codeable, Orderable<DictionaryItem> {
    @Serial
    private static final long serialVersionUID = -819169978008402813L;

    /**
     * 所属字典主键
     */
    @Nonnull
    private String dictionaryId;

    /**
     * 所属字典信息
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Dictionary dictionary;

    /**
     * 标识
     */
    @Nonnull
    private String code;

    /**
     * 名称
     */
    @Nonnull
    private String name;

    /**
     * 是否主选项
     */
    @Nonnull
    private Boolean primary;

    /**
     * 排序号
     */
    @Nonnull
    private Integer order;

    /**
     * 创建人信息
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Account creator;

    /**
     * 修改人信息
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Account modifier;

    public DictionaryItemInput toInput() {
        return DictionaryItemInput.builder()
                .id(this.getId())
                .dictionaryId(this.getDictionaryId())
                .code(this.getCode())
                .name(this.getName())
                .primary(this.getPrimary())
                .order(this.getOrder())
                .build();
    }
}
