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

package central.studio.dashboard.controller.multicast.query;

import central.data.multicast.MulticastMessage;
import central.lang.Stringx;
import central.sql.query.Conditions;
import central.starter.web.query.PageQuery;
import central.validation.Label;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * Multicast Message Page Query
 * <p>
 * 消息分页查询
 *
 * @author Alan Yeh
 * @since 2024/11/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MessagePageQuery extends PageQuery<MulticastMessage> {
    @Serial
    private static final long serialVersionUID = 3554744502321204801L;

    @Label("广播器主键")
    @NotBlank
    @Size(min = 1, max = 32)
    private String broadcasterId;

    @Label("消息体")
    private String body;

    @Label("消息状态")
    private String status;

    @Override
    public Conditions<MulticastMessage> build() {
        var conditions = Conditions.of(MulticastMessage.class).eq(MulticastMessage::getBroadcasterId, this.getBroadcasterId());

        // 精确字段搜索
        if (Stringx.isNotEmpty(this.getBody())) {
            conditions.like(MulticastMessage::getBody, this.getBody());
        }
        if (this.getStatus() != null) {
            conditions.eq(MulticastMessage::getStatus, this.getStatus());
        }

        // 模糊搜索
        for (var keyword : this.getKeywords()) {
            conditions.and(filter -> filter.like(MulticastMessage::getBody, keyword));
        }

        return conditions;
    }
}
