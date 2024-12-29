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

package central.data.organization;

import central.validation.Label;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 帐户与单位关系输入
 *
 * @author Alan Yeh
 * @since 2022/09/12
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true, builderClassName = "Builder")
public class AccountUnitInput implements Serializable {
    @Serial
    private static final long serialVersionUID = -7840283230047486126L;

    @Label("帐户主键")
    @NotBlank
    @Size(min = 1, max = 32)
    private String accountId;

    @Label("单位主键")
    @NotBlank
    @Size(min = 1, max = 32)
    private String unitId;

    @Label("职级主键")
    @NotBlank
    @Size(min = 1, max = 32)
    private String rankId;

    @Label("是否主单位")
    @NotNull
    private Boolean primary;

    @Valid
    @Label("部门列表")
    private List<AccountDepartmentInput> departments;
}
