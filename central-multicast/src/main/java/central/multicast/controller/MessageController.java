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

package central.multicast.controller;

import central.api.client.multicast.body.StandardBody;
import central.api.provider.multicast.MulticastMessageProvider;
import central.data.multicast.MulticastMessage;
import central.data.multicast.MulticastMessageInput;
import central.data.multicast.option.MessageStatus;
import central.data.multicast.option.PublishMode;
import central.lang.reflect.TypeReference;
import central.multicast.controller.param.PublishParams;
import central.multicast.core.Container;
import central.multicast.core.DynamicBroadcaster;
import central.util.Jsonx;
import central.util.Listx;
import central.validation.Validatex;
import central.web.XForwardedHeaders;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 消息
 *
 * @author Alan Yeh
 * @since 2022/11/04
 */
@RestController
@RequestMapping("/api/broadcasters/{code}/messages")
public class MessageController {

    @Setter(onMethod_ = @Autowired)
    private Container container;

    @Setter(onMethod_ = @Autowired)
    private MulticastMessageProvider provider;

    private void validate(String token, DynamicBroadcaster broadcaster) {
        try {
            JWT.require(Algorithm.HMAC256(broadcaster.getData().getApplication().getSecret()))
                    .build().verify(token);
        } catch (AlgorithmMismatchException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "访问凭证[token]无效: 签名算法不匹配");
        } catch (SignatureVerificationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "访问凭证[token]无效: 签名不匹配");
        } catch (TokenExpiredException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "访问凭证[token]无效: 已过期");
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "访问凭证[token]无效: " + ex.getLocalizedMessage());
        }
    }

    /**
     * 广播消息
     *
     * @param code   广播器标识
     * @param params 广播参数
     * @param tenant 租户标识
     */
    @PostMapping
    public List<MulticastMessage> publish(@PathVariable String code,
                                          @Validated @RequestBody PublishParams params,
                                          @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var broadcaster = this.container.requireBroadcaster(tenant, code);
        this.validate(params.getToken(), broadcaster);

        if (Listx.isNullOrEmpty(params.getMessages())) {
            return Collections.emptyList();
        }

        var inputs = new ArrayList<MulticastMessageInput>();

        if (PublishMode.STANDARD.isCompatibleWith(params.getMode())) {
            var bodies = Jsonx.Default().deserialize(Jsonx.Default().serialize(params.getMessages()), TypeReference.ofList(StandardBody.class));
            for (var body : bodies) {
                Validatex.Default().validate(body);
                broadcaster.standardPublish(body);
                inputs.add(MulticastMessageInput.builder()
                        .broadcasterId(broadcaster.getData().getId())
                        .body(Jsonx.Default().serialize(body))
                        .mode(params.getMode())
                        .status(MessageStatus.SUCCEED.getValue())
                        .build());
            }
        } else {
            var bodies = Jsonx.Default().deserialize(Jsonx.Default().serialize(params.getMessages()), TypeReference.ofList(broadcaster.getBodyType()));
            for (var body : bodies) {
                Validatex.Default().validate(body);
                broadcaster.customPublish(body);
                inputs.add(MulticastMessageInput.builder()
                        .broadcasterId(broadcaster.getData().getId())
                        .body(Jsonx.Default().serialize(body))
                        .mode(params.getMode())
                        .status(MessageStatus.SUCCEED.getValue())
                        .build());
            }
        }

        return this.provider.insertBatch(inputs, "syssa", tenant);
    }
}
