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

package central.multicast.client.body;

import central.multicast.client.MessageBody;
import central.validation.Label;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.List;

/**
 * 邮件消息体
 *
 * @author Alan Yeh
 * @since 2022/11/04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailBody implements MessageBody {
    @Serial
    private static final long serialVersionUID = -8689953012437085820L;

    @Label("主题")
    private String subject;

    @Label("正文")
    private String content;

    @Label("发件人姓名")
    @Size(max = 30)
    private String from;

    @Label("收件人")
    @Valid
    @NotEmpty
    private List<Recipient> to;

    @Label("抄送人")
    @Valid
    private List<Recipient> cc;

    @Label("密送人")
    @Valid
    private List<Recipient> bcc;
}
