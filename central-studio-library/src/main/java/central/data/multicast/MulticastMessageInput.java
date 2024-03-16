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

import central.data.multicast.option.PublishMode;
import central.data.multicast.option.MessageStatus;
import central.validation.Enums;
import central.validation.Label;
import central.validation.group.Insert;
import central.validation.group.Update;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * 广播消息
 *
 * @author Alan Yeh
 * @since 2022/11/04
 */
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class MulticastMessageInput implements Serializable {
    @Serial
    private static final long serialVersionUID = -6457292307996258219L;

    @Label("主键")
    @Null(groups = Insert.class)
    @NotBlank(groups = Update.class)
    @Size(min = 1, max = 32, groups = Insert.class)
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
}
