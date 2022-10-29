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

package central.security.test;

import central.api.client.security.SessionClaims;
import central.api.client.security.SessionClient;
import central.security.Digestx;
import central.security.SecurityApplication;
import central.security.controller.session.SessionController;
import central.security.signer.KeyPair;
import central.util.Mapx;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Session Controller Test Cases
 * 会话测试
 *
 * @author Alan Yeh
 * @see SessionController
 * @since 2022/10/21
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = SecurityApplication.class)
public class TestSessionController {

    @Setter(onMethod_ = @Autowired)
    private SessionClient client;

    @Setter(onMethod_ = @Autowired)
    private KeyPair keyPair;

    /**
     * @see SessionClient#getPublicKey
     */
    @Test
    public void case1() {
        var pubkey = client.getPublicKey();
        var source = Base64.getEncoder().encodeToString(keyPair.getVerifyKey().getEncoded());
        assertEquals(source, pubkey);
    }

    /**
     * @see SessionClient#login
     * @see SessionClient#verify
     * @see SessionClient#logout
     */
    @Test
    public void case2() {
        // 登录获取会话
        var session = client.login("syssa", Digestx.SHA256.digest("x.123456", StandardCharsets.UTF_8), "lLS4p6skBbBVZX30zR5", Mapx.newHashMap("test", "123"));
        assertNotNull(session);

        // 本地验证
        JWT.require(Algorithm.RSA256((RSAPublicKey) keyPair.getVerifyKey()))
                .withClaim(SessionClaims.TENANT_CODE, "master")
                .withClaim(SessionClaims.USERNAME, "syssa")
                .withClaim(SessionClaims.ADMIN, true)
                .withClaim(SessionClaims.SUPERVISOR, true)
                .withClaim("test", "123")
                .build()
                .verify(session);

        // 验证有效性
        boolean verified = client.verify(session);
        assertTrue(verified);

        // 登录登录
        client.logout(session);

        // 验证有效性
        verified = client.verify(session);
        assertFalse(verified);
    }

    /**
     * @see SessionClient#login
     * @see SessionClient#verify
     * @see SessionClient#invalid
     */
    @Test
    public void case3() {
        // 登录获取会话
        var session = client.login("syssa", Digestx.SHA256.digest("x.123456", StandardCharsets.UTF_8), "lLS4p6skBbBVZX30zR5", Mapx.newHashMap("test", "123"));
        assertNotNull(session);

        // 验证有效性
        boolean verified = client.verify(session);
        assertTrue(verified);

        // 所有会话都置为无效
        client.invalid("syssa");

        // 验证有效性
        verified = client.verify(session);
        assertFalse(verified);
    }

    /**
     * @see SessionClient#login
     * @see SessionClient#loginByToken
     * @see SessionClient#logout
     */
    @Test
    public void case4() {
        // 登录获取会话
        var session = client.login("syssa", Digestx.SHA256.digest("x.123456", StandardCharsets.UTF_8), "lLS4p6skBbBVZX30zR5", Mapx.newHashMap("test", "123"));
        assertNotNull(session);

        // 验证有效性
        boolean verified = client.verify(session);
        assertTrue(verified);

        // 再次颁发
        var token = client.loginByToken(session, "lLS4p6skBbBVZX30zR5", Mapx.newHashMap("test", "123"));

        // 验证有效性
        verified = client.verify(token);
        assertTrue(verified);

        // 退出登录
        client.logout(session);

        // 验证有效性
        verified = client.verify(session);
        assertFalse(verified);

        // 验证有效性
        verified = client.verify(token);
        assertFalse(verified);
    }

    /**
     * @see SessionClient#login
     * @see SessionClient#loginByToken
     * @see SessionClient#invalid
     */
    @Test
    public void case5() {
        // 登录获取会话
        var session = client.login("syssa", Digestx.SHA256.digest("x.123456", StandardCharsets.UTF_8), "lLS4p6skBbBVZX30zR5", Mapx.newHashMap("test", "123"));
        assertNotNull(session);

        // 验证有效性
        boolean verified = client.verify(session);
        assertTrue(verified);

        // 再次颁发
        var token = client.loginByToken(session, "lLS4p6skBbBVZX30zR5", Mapx.newHashMap("test", "123"));

        // 验证有效性
        verified = client.verify(token);
        assertTrue(verified);

        // 退出登录
        client.invalid("syssa");

        // 验证有效性
        verified = client.verify(session);
        assertFalse(verified);

        // 验证有效性
        verified = client.verify(token);
        assertFalse(verified);
    }
}
