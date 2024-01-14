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

import central.bean.Codeable;
import central.bean.Tenantable;
import central.data.storage.StorageFileInput;
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
@Table(name = "X_STO_FILE")
@EqualsAndHashCode(callSuper = true)
public class StorageFileEntity extends ModifiableEntity implements Codeable, Tenantable {
    @Serial
    private static final long serialVersionUID = -1835394098565520541L;


    @Id
    @Label("主键")
    @Size(max = 32)
    private String id;

    @Label("存储桶主键")
    @NotBlank
    @Size(min = 1, max = 32)
    private String bucketId;

    @Label("父主键")
    @Size(max = 32)
    private String parentId;

    @Label("标识")
    @NotBlank
    @Size(min = 1, max = 32)
    @Pattern(regexp = "^[0-9a-zA-Z_-]+$", message = "${label}[${property}]只能由数字、英文字母、中划线、下划线组成")
    @Pattern(regexp = "^(?!-)(?!_).*$", message = "${label}[${property}]不能由中划线或下划线开头")
    @Pattern(regexp = "^(?!.*?[-_]$).*$", message = "${label}[${property}]不能由中划线或下划线结尾")
    @Pattern(regexp = "^(?!.*(--).*$).*$", message = "${label}[${property}]不能出现连续中划线")
    @Pattern(regexp = "^(?!.*(__).*$).*$", message = "${label}[${property}]不能出现连续下划线")
    @Pattern(regexp = "^(?!.*(-_|_-).*$).*$", message = "${label}[${property}]不能出现连续中划线、下划线")
    private String code;

    @Label("名称")
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @Label("是否文件夹")
    @NotNull
    private Boolean directory;

    @Label("是否已确认")
    @NotNull
    private Boolean confirmed;

    @Label("租户标识")
    @NotBlank
    @Size(min = 1, max = 32)
    private String tenantCode;

    public void fromInput(StorageFileInput input) {
        this.setId(input.getId());
        this.setBucketId(input.getBucketId());
        this.setParentId(input.getParentId());
        this.setCode(input.getCode());
        this.setName(input.getName());
        this.setDirectory(input.getDirectory());
        this.setConfirmed(input.getConfirmed());
    }
}
