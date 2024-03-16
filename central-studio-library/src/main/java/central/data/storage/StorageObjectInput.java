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

import central.validation.Label;
import central.validation.group.Insert;
import central.validation.group.Update;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * Storage Object
 * <p>
 * 存储对象
 *
 * @author Alan Yeh
 * @since 2022/10/30
 */
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class StorageObjectInput implements Serializable {
    @Serial
    private static final long serialVersionUID = -7717666978083018758L;

    @Label("主键")
    @Null(groups = Insert.class)
    @NotBlank(groups = Update.class)
    @Size(min = 1, max = 32, groups = Insert.class)
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
}
