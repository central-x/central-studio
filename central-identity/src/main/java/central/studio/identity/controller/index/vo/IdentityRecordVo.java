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

package central.studio.identity.controller.index.vo;

import central.bean.Nonnull;
import central.data.identity.IdentityRecord;
import central.data.organization.Account;
import central.sql.data.ModifiableEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/// Identity Record View Object
///
/// 认证记录
///
/// @author Alan Yeh
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IdentityRecordVo extends ModifiableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 2349640041960990893L;

    /// 主键
    @Nonnull
    private String id;

    /// 会话主键
    @Nonnull
    private String sessionId;

    /// 终端类型
    @Nonnull
    private String endpoint;

    /// 主机归属地
    @Nonnull
    private String address;

    /// 主机地址
    @Nonnull
    private String host;

    /// 设备
    @Nonnull
    private String device;

    /// 状态
    @Nonnull
    private String status;

    /// 创建人信息
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Account creator;
    
    /// 修改人信息
    @Nonnull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Account modifier;

    public void from(IdentityRecord record) {
        this.setId(record.getId());
        this.setAddress(record.getAddress());
        this.setHost(record.getHost());
        this.setDevice(record.getDevice());
        this.setCreatorId(record.getCreatorId());
        this.setCreateDate(record.getCreateDate());
        this.setCreator(record.getCreator());
        this.setModifierId(record.getModifierId());
        this.setModifyDate(record.getModifyDate());
        this.setModifier(record.getModifier());
    }
}
