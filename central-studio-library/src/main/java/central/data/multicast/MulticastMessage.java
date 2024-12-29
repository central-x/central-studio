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

package central.data.multicast;

import central.bean.Nonnull;
import central.data.multicast.option.PublishMode;
import central.data.multicast.option.MessageStatus;
import central.data.organization.Account;
import central.sql.data.ModifiableEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * 广播消息
 *
 * @author Alan Yeh
 * @since 2022/11/04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MulticastMessage extends ModifiableEntity {
    @Serial
    private static final long serialVersionUID = 8486004779238241933L;

    /**
     * 广播器主键
     *
     * @see MulticastBroadcaster
     */
    @Nonnull
    private String broadcasterId;
    /**
     * 广播器
     */
    @Nonnull
    private MulticastBroadcaster broadcaster;
    /**
     * 消息体
     */
    @Nonnull
    private String body;
    /**
     * 推送模式
     *
     * @see PublishMode
     */
    private String mode;
    /**
     * 消息状态
     *
     * @see MessageStatus
     */
    @Nonnull
    private String status;
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

    public MulticastMessageInput.Builder toInput() {
        return MulticastMessageInput.builder()
                .id(this.getId())
                .broadcasterId(this.getBroadcasterId())
                .body(this.getBody())
                .mode(this.getMode())
                .status(this.getStatus());
    }
}
