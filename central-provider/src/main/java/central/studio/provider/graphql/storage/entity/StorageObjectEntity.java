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

package central.studio.provider.graphql.storage.entity;

import central.bean.Tenantable;
import central.data.storage.StorageObjectInput;
import central.sql.data.ModifiableEntity;
import central.validation.Label;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * Storage Object
 * <p>
 * 存储对象
 *
 * @author Alan Yeh
 * @since 2022/10/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "X_STO_OBJECT")
@EqualsAndHashCode(callSuper = true)
public class StorageObjectEntity extends ModifiableEntity implements Tenantable {
    @Serial
    private static final long serialVersionUID = -6674344827015641784L;

    @Id
    @Label("主键")
    @Size(max = 32)
    private String id;

    @Label("存储桶主键")
    @NotBlank
    @Size(min = 1, max = 32)
    private String bucketId;

    @Label("文件名")
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @Label("文件大小")
    @NotNull
    private Long size;

    @Label("摘要")
    @NotBlank
    @Size(min = 1, max = 128)
    private String digest;

    @Label("存储键")
    @NotBlank
    @Size(min = 1, max = 2048)
    private String key;

    @Label("确认状态")
    @NotNull
    private Boolean confirmed;

    @Label("租户标识")
    @NotBlank
    @Size(min = 1, max = 32)
    private String tenantCode;

    public void fromInput(StorageObjectInput input) {
        this.setId(input.getId());
        this.setBucketId(input.getBucketId());
        this.setName(input.getName());
        this.setSize(input.getSize());
        this.setDigest(input.getDigest());
        this.setKey(input.getKey());
        this.setConfirmed(input.getConfirmed());
    }
}
