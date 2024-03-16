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

package central.identity.client;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 会话管理
 *
 * @author Alan Yeh
 * @since 2022/10/21
 */
public interface SessionClient {
    /**
     * 获取公钥
     * <p>
     * 客户端可以通过公钥自行验证会话的真实性。同时客户端需要定期通过 {@link #verify} 方法来验证会话是否过期
     */
    @GetMapping("/identity/api/sessions/pubkey")
    String getPublicKey();

    /**
     * 使用帐户密码登录
     * <p>
     * 登录成功后，将返回 JWT 格式的会话凭证，该会话凭证可以通过公钥 {@link #getPublicKey} 来验证真实性。认证信息只能通过 {@link  #verify}
     * 方法来验证会话是否过期
     *
     * @param account  帐户
     * @param password 密码
     * @param secret   终端密钥
     * @param claims   会话附加声明
     * @return 会话（JWT）
     */
    @PostMapping(value = "/identity/api/sessions/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    String login(@RequestPart String account, @RequestPart String password, @RequestPart String secret, @RequestPart(required = false) Map<String, Object> claims);


    /**
     * 使用已有的会话登录新的会话
     * <p>
     * 此方法主要用于扫一扫登录之类的场景，通过已登录的会话去颁发一个新一会话。如果原来的会话被注释了，那么本会话也会跟着一起补注销。
     *
     * @param token  已登录的会话凭证
     * @param secret 终端密钥
     * @param claims 会话附加声明
     * @return 会话（JWT）
     */
    @PostMapping(value = "/identity/api/sessions/login/token", consumes = MediaType.APPLICATION_JSON_VALUE)
    String loginByToken(@RequestPart String token, @RequestPart String secret, @RequestPart(required = false) Map<String, Object> claims);

    /**
     * 验证会话是否过期
     * <p>
     * 由于会话可能因为超时、被踢、主动退出等原因变为无效，因此客户端必须通过服务端的接口进行验证。调用本接口后，会延长该会话的超时时间。
     *
     * @param token 会话凭证
     * @return 是否有效
     */
    @PostMapping(value = "/identity/api/sessions/verify", consumes = MediaType.APPLICATION_JSON_VALUE)
    boolean verify(@RequestPart String token);

    /**
     * 退出登录，将指定会话置为无效
     *
     * @param token 会话凭证
     */
    @GetMapping("/identity/api/sessions/logout")
    void logout(@RequestParam String token);

    /**
     * 将某个用户的所有会话都置为无效
     *
     * @param accountId 用户主键
     */
    @PostMapping(value = "/identity/api/sessions/invalid", consumes = MediaType.APPLICATION_JSON_VALUE)
    void invalid(@RequestPart String accountId);
}
