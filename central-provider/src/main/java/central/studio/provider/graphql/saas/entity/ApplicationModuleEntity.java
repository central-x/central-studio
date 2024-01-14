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

package central.studio.provider.graphql.saas.entity;

import central.data.saas.ApplicationModuleInput;
import central.sql.data.ModifiableEntity;
import central.sql.meta.annotation.Relation;
import central.validation.Label;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * 应用模块信息
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "X_SAAS_APPLICATION_MODULE")
@EqualsAndHashCode(callSuper = true)
@Relation(alias = "application", target = ApplicationEntity.class, property = "applicationId")
public class ApplicationModuleEntity extends ModifiableEntity {
    @Serial
    private static final long serialVersionUID = 7015722509704015462L;

    @Id
    @Label("主键")
    @Size(max = 32)
    private String id;

    @Label("所属应用主键")
    @NotBlank
    @Size(min = 1, max = 32)
    private String applicationId;

    @Label("服务地址")
    @NotBlank
    @Size(min = 1, max = 1024)
    private String url;

    @Label("上下文路径")
    @NotBlank
    @Size(min = 1, max = 64)
    private String contextPath;

    @Label("是否启用")
    @NotNull
    private Boolean enabled;

    @Label("备注")
    @Size(max = 1024)
    private String remark;

    public void fromInput(ApplicationModuleInput input) {
        this.setId(input.getId());
        this.setApplicationId(input.getApplicationId());
        this.setUrl(input.getUrl());
        this.setContextPath(input.getContextPath());
        this.setEnabled(input.getEnabled());
        this.setRemark(input.getRemark());
    }
}
