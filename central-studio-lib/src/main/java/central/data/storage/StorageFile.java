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

package central.data.storage;

import central.bean.*;
import central.data.organization.Account;
import central.sql.data.ModifiableEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.List;

/**
 * Storage File
 * <p>
 * 文件
 *
 * @author Alan Yeh
 * @since 2022/10/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StorageFile extends ModifiableEntity implements Codeable, Treeable<StorageFile> {
    @Serial
    private static final long serialVersionUID = 960422241807610750L;

    /**
     * 存储桶主键
     */
    @Nonnull
    private String bucketId;
    /**
     * 存储桶
     */
    @Nonnull
    private StorageBucket bucket;
    /**
     * 父文件夹主键
     */
    @Nullable
    private String parentId;
    /**
     * 父文件夹
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private StorageFile parent;
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
     * 扩展名
     */
    @Nonnull
    private String extension;
    /**
     * 是否文件夹
     */
    @Nonnull
    private Boolean directory;
    /**
     * 确认状态
     * 如果没有确认，存储中心会在一段时间之后清除该对象
     */
    @Nonnull
    private Boolean confirmed;
    /**
     * 子文件
     */
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<StorageFile> children;

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

    public StorageFileInput toInput() {
        return StorageFileInput.builder()
                .id(this.getId())
                .bucketId(this.getBucketId())
                .parentId(this.getParentId())
                .code(this.getCode())
                .name(this.getName())
                .directory(this.getDirectory())
                .confirmed(this.getConfirmed())
                .build();
    }
}
