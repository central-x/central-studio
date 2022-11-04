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

package central.api.client.multicast;

import central.data.multicast.MulticastMessage;
import central.data.multicast.option.PublishMode;
import central.util.Guidx;
import central.web.XForwardedHeaders;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 消息广播
 *
 * @author Alan Yeh
 * @since 2022/11/04
 */
public interface MessageClient {

    /**
     * 获取广播访问凭证
     *
     * @param secret  应用密钥
     * @param expires 凭证过期时间（毫秒）
     * @return 消息访问凭证
     */
    default String createToken(String secret, Long expires) {
        return JWT.create().withJWTId(Guidx.nextID())
                .withExpiresAt(new Date(System.currentTimeMillis() + expires))
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 广播消息
     *
     * @param code     广播器标识
     * @param token    访问凭证
     * @param mode     广播模式（{@link PublishMode}）
     * @param messages 消息
     * @return 消息记录（可根据 {@link #findByIds} 查询消息推送状态）
     */
    @PostMapping(value = "/multicast/api/broadcasters/{code}/messages", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<MulticastMessage> publish(@PathVariable String code, @RequestPart String token, @RequestPart PublishMode mode, @RequestPart List<MessageBody> messages);

    /**
     * 广播消息
     *
     * @param code     广播器标识
     * @param token    访问凭证
     * @param mode     广播模式（{@link PublishMode}）
     * @param messages 消息
     * @param tenant   租户标识
     * @return 消息记录（可根据 {@link #findByIds} 查询消息推送状态）
     */
    @PostMapping(value = "/multicast/api/broadcasters/{code}/messages", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<MulticastMessage> publish(@PathVariable String code, @RequestPart String token, @RequestPart PublishMode mode, @RequestPart List<MessageBody> messages, @RequestHeader(value = XForwardedHeaders.TENANT, required = false) String tenant);

    /**
     * 查询消息信息
     *
     * @param code  广播器标识
     * @param token 访问凭证
     * @param ids   消息主键
     * @return 消息记录
     */
    @GetMapping(value = "/multicast/api/broadcasters/{code}/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    List<MulticastMessage> findByIds(@PathVariable String code, @RequestParam String token, @RequestParam List<String> ids);

    /**
     * 查询消息信息
     *
     * @param code  广播器标识
     * @param token 访问凭证
     * @param ids   消息主键
     * @return 消息记录
     */
    @GetMapping(value = "/multicast/api/broadcasters/{code}/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    List<MulticastMessage> findByIds(@PathVariable String code, @RequestParam String token, @RequestParam List<String> ids, @RequestHeader(value = XForwardedHeaders.TENANT, required = false) String tenant);
}
