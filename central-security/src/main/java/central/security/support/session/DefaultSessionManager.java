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

package central.security.support.session;

import central.api.client.security.SessionClaims;
import central.data.organization.Account;
import central.security.controller.session.support.Endpoint;
import central.security.signer.KeyPair;
import central.util.Guidx;
import central.util.Mapx;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * 默认的会话
 *
 * @author Alan Yeh
 * @since 2023/05/13
 */
@Slf4j
@Component
public class DefaultSessionManager implements SessionManager {
    /**
     * 会话密钥
     */
    private final KeyPair sessionKey;

    /**
     * 会话容器
     */
    private final SessionContainer container;

    public DefaultSessionManager(KeyPair sessionKey, SessionContainer container) {
        this.sessionKey = sessionKey;
        this.container = container;
    }

    @Override
    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(this.sessionKey.getVerifyKey().getEncoded());
    }

    @Override
    public String issue(String tenantCode, String issuer, Long timeout, Account account, Endpoint endpoint, Integer limit, Map<String, Object> claims) {
        var jwt = JWT.create()
                // JWT 唯一标识
                .withJWTId(Guidx.nextID())
                // 用户主键
                .withSubject(account.getId())
                // 用户帐号
                .withClaim(SessionClaims.USERNAME, account.getUsername())
                // 是否管理员
                .withClaim(SessionClaims.ADMIN, account.getAdmin())
                // 是否超级管理员
                .withClaim(SessionClaims.SUPERVISOR, account.getSupervisor())
                // 颁发时间
                .withClaim(SessionClaims.ISSUE_TIME, new Date())
                // 终端类型
                .withClaim(SessionClaims.ENDPOINT, endpoint.getValue())
                // 颁发者
                .withIssuer(issuer)
                // 会话有效时间
                .withClaim(SessionClaims.TIMEOUT, timeout)
                // 租户标识
                .withClaim(SessionClaims.TENANT_CODE, tenantCode);

        // 附加用户指定的 Claims
        if (Mapx.isNotEmpty(claims)) {
            for (var entry : claims.entrySet()) {
                if (entry.getValue() instanceof Boolean b) {
                    jwt.withClaim(entry.getKey(), b);
                } else if (entry.getValue() instanceof Integer i) {
                    jwt.withClaim(entry.getKey(), i);
                } else if (entry.getValue() instanceof Long l) {
                    jwt.withClaim(entry.getKey(), l);
                } else if (entry.getValue() instanceof Double d) {
                    jwt.withClaim(entry.getKey(), d);
                } else if (entry.getValue() instanceof String s) {
                    jwt.withClaim(entry.getKey(), s);
                } else if (entry.getValue() instanceof Date d) {
                    jwt.withClaim(entry.getKey(), d);
                }
            }
        }
        return jwt.sign(Algorithm.RSA256((RSAPublicKey) sessionKey.getVerifyKey(), (RSAPrivateKey) sessionKey.getSignKey()));
    }

    @Override
    public boolean verify(String tenantCode, String token) {
        DecodedJWT session;
        try {
            session = JWT.decode(token);
        } catch (Exception ex) {
            log.info("不是有效的会话凭证");
            return false;
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不是有效的会话凭证");
        }

        if (!Objects.equals(tenantCode, session.getClaim(SessionClaims.TENANT_CODE).asString())) {
            log.info("租户不匹配");
            return false;
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "租户不匹配");
        }

        try {
            JWT.require(Algorithm.RSA256((RSAPublicKey) sessionKey.getVerifyKey()))
                    .build()
                    .verify(token);
        } catch (JWTVerificationException ex) {
            log.info("密钥不匹配");
            return false;
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "密钥不匹配");
        }

        // 验证会主知是否过期
        if (!this.container.exists(tenantCode, session)) {
            log.info("会话不存在");
            return false;
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "会话不存在");
        }

        var timeout = Duration.ofMillis(session.getClaim(SessionClaims.TIMEOUT).asLong());
        // 重置设置会话有效期
        this.container.expire(tenantCode, session, timeout);

        return true;
    }

    @Override
    public void invalid(String tenantCode, String token) {
        try {
            var session = JWT.require(Algorithm.RSA256((RSAPublicKey) sessionKey.getVerifyKey()))
                    .withClaim(SessionClaims.TENANT_CODE, tenantCode)
                    .build()
                    .verify(token);
            this.container.remove(tenantCode, session);
        } catch (JWTVerificationException ignored) {

        }
    }

    @Override
    public void clear(String tenantCode, String accountId) {
        this.container.clear(tenantCode, accountId);
    }
}
