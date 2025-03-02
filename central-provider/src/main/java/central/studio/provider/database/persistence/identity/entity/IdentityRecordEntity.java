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

package central.studio.provider.database.persistence.identity.entity;

import central.bean.Tenantable;
import central.data.identity.IdentityRecordInput;
import central.sql.data.ModifiableEntity;
import central.validation.Label;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;

/**
 * Identity Record
 * <p>
 * 认证记录
 *
 * @author Alan Yeh
 * @since 2025/03/02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "X_ID_RECORD")
@EqualsAndHashCode(callSuper = true)
@Builder(toBuilder = true, builderClassName = "Builder")
public class IdentityRecordEntity extends ModifiableEntity implements Tenantable {
    @Serial
    private static final long serialVersionUID = 7242633809429731226L;

    @Id
    @Label("主键")
    @Size(max = 32)
    private String id;

    @Label("主机归属地")
    @NotBlank
    @Size(min = 1, max = 32)
    private String address;

    @Label("主机地址")
    @NotBlank
    @Size(min = 1, max = 64)
    private String host;

    @Label("登录设备")
    @NotBlank
    @Size(min = 1, max = 128)
    private String device;

    @Label("租户标识")
    @NotBlank
    @Size(min = 1, max = 32)
    private String tenantCode;

    public void fromInput(@Nonnull IdentityRecordInput input) {
        this.setId(input.getId());
        this.setAddress(input.getAddress());
        this.setHost(input.getHost());
        this.setDevice(input.getDevice());
    }
}
