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

package central.studio.provider.graphql.multicast.entity;

import central.data.multicast.MulticastMessageInput;
import central.data.multicast.option.PublishMode;
import central.data.multicast.option.MessageStatus;
import central.sql.data.ModifiableEntity;
import central.sql.meta.annotation.Relation;
import central.validation.Enums;
import central.validation.Label;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * Multicast Message
 * <p>
 * 广播消息
 *
 * @author Alan Yeh
 * @since 2022/11/04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "X_MCAST_MESSAGE")
@EqualsAndHashCode(callSuper = true)
@Relation(alias = "broadcaster", target = MulticastBroadcasterEntity.class, property = "broadcasterId")
public class MulticastMessageEntity extends ModifiableEntity {
    @Serial
    private static final long serialVersionUID = -8778367778881935922L;

    @Id
    @Label("主键")
    @Size(max = 32)
    private String id;

    @Label("广播器主键")
    @NotBlank
    @Size(min = 1, max = 32)
    private String broadcasterId;

    @Label("消息体")
    @NotBlank
    @Size(min = 1, max = 5 * 1024 * 1024)
    private String body;

    @Label("推送模式")
    @NotBlank
    @Enums(PublishMode.class)
    private String mode;

    @Label("消息状态")
    @NotBlank
    @Enums(MessageStatus.class)
    private String status;

    @Label("租户标识")
    @NotBlank
    @Size(min = 1, max = 32)
    private String tenantCode;

    public void fromInput(MulticastMessageInput input) {
        this.setId(input.getId());
        this.setBroadcasterId(input.getBroadcasterId());
        this.setBody(input.getBody());
        this.setMode(input.getMode());
        this.setStatus(input.getStatus());
    }
}
