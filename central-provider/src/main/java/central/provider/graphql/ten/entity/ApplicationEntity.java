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

package central.provider.graphql.ten.entity;

import central.data.ten.ApplicationInput;
import central.lang.Stringx;
import central.sql.data.ModifiableEntity;
import central.validation.Label;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serial;
import java.util.Base64;

/**
 * 应用信息
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "X_TEN_APPLICATION")
@EqualsAndHashCode(callSuper = true)
public class ApplicationEntity extends ModifiableEntity {
    @Serial
    private static final long serialVersionUID = 2540103974249029204L;

    @Id
    @Label("主键")
    @Size(max = 32)
    private String id;

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

    @Label("图标")
    @NotEmpty
    @Size(max = 2 * 1024 * 1024)
    private byte[] logoBytes;

    @Label("服务地址")
    @NotBlank
    @Size(min = 1, max = 1024)
    private String url;

    @Label("上下文路径")
    @NotBlank
    @Size(min = 1, max = 64)
    private String contextPath;

    @Label("密钥")
    @NotBlank
    @Size(min = 1, max = 32)
    private String key;

    @Label("是否启用")
    @NotNull
    private Boolean enabled;

    @Label("排序号")
    @NotNull
    private Integer order;

    @Label("备注")
    @Size(max = 1024)
    private String remark;

    public void fromInput(ApplicationInput input) {
        this.setId(input.getId());
        this.setCode(input.getCode());
        this.setName(input.getName());

        if (Stringx.isNotEmpty(input.getLogo())) {
            try {
                this.setLogoBytes(Base64.getDecoder().decode(input.getLogo()));
            } catch (Exception ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "图标[logo]不是有效的 Base64 字符串");
            }
        }
        this.setUrl(input.getUrl());
        this.setContextPath(input.getContextPath());
        this.setKey(input.getKey());
        this.setEnabled(input.getEnabled());
        this.setRemark(input.getRemark());
    }
}
