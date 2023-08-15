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

package central.api.client.security;

import central.lang.Assertx;
import central.lang.Stringx;
import central.security.Signerx;
import central.util.Guidx;
import central.util.Mapx;
import central.util.Objectx;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 会话
 *
 * @author Alan Yeh
 * @since 2023/08/05
 */
public class Session implements Serializable {
    @Serial
    private static final long serialVersionUID = -3965567322672268966L;

    /**
     * 会话凭证
     */
    @Getter
    private final String token;

    private final transient DecodedJWT decodedToken;

    public Session(@Nonnull String token) {
        this.token = token;
        try {
            this.decodedToken = JWT.decode(token);
        } catch (Exception ex) {
            throw new IllegalArgumentException("会话凭证[token]无效");
        }
    }

    @JsonCreator
    public static Session of(@Nonnull String token) {
        return new Session(token);
    }

    /**
     * 会话主键
     */
    public @Nonnull String getId() {
        return this.decodedToken.getId();
    }

    /**
     * 用户主键
     */
    public @Nonnull String getAccountId() {
        return this.decodedToken.getSubject();
    }

    /**
     * 用户名
     */
    public @Nonnull String getUsername() {
        return this.decodedToken.getClaim(SessionClaims.USERNAME).asString();
    }

    /**
     * 是否管理员（系统管理员，安全管理员、安全保密员）
     */
    public boolean isAdmin() {
        return this.decodedToken.getClaim(SessionClaims.ADMIN).asBoolean();
    }

    /**
     * 是否是超级管理员
     */
    public boolean isSupervisor() {
        return this.decodedToken.getClaim(SessionClaims.SUPERVISOR).asBoolean();
    }

    /**
     * 颁发当前会话的会话主键
     * 主会话失效时，从会话也会跟着失效
     */
    public @Nullable String getSource() {
        return this.decodedToken.getClaim(SessionClaims.SOURCE).asString();
    }

    /**
     * 申请会话时的客户端 IP
     */
    public @Nonnull String getIp() {
        return this.decodedToken.getClaim(SessionClaims.IP).asString();
    }

    /**
     * 凭证颁发机构，通常使用域名
     */
    public @Nonnull String issuer() {
        return this.decodedToken.getIssuer();
    }

    /**
     * 凭证颁发时间
     */
    public @Nonnull Date getIssueTime() {
        return this.decodedToken.getClaim(SessionClaims.ISSUE_TIME).asDate();
    }

    /**
     * 终端类型
     */
    public @Nonnull String getEndpoint() {
        return this.decodedToken.getClaim(SessionClaims.ENDPOINT).asString();
    }

    /**
     * 会话自动超时时间
     */
    public @Nonnull Duration getTimeout() {
        return Duration.ofMillis(this.decodedToken.getClaim(SessionClaims.TIMEOUT).asLong());
    }

    /**
     * 租户标识
     */
    public @Nonnull String getTenantCode() {
        return this.decodedToken.getClaim(SessionClaims.TENANT_CODE).asString();
    }

    /**
     * 获取 String 类型的 Claim
     *
     * @param name 名称
     */
    public @Nullable String getStringClaim(String name) {
        return this.decodedToken.getClaim(name).asString();
    }

    /**
     * 获取 String 类型的 Claim
     *
     * @param name     名称
     * @param fallback 默认值
     */
    public @Nonnull String getStringClaim(String name, @Nonnull String fallback) {
        return Objectx.getOrDefault(this.getStringClaim(name), fallback);
    }

    /**
     * 获取 Boolean 类型的 Claim
     *
     * @param name 名称
     */
    public @Nullable Boolean getBooleanClaim(String name) {
        return this.decodedToken.getClaim(name).asBoolean();
    }

    /**
     * 获取 Boolean 类型的 Claim
     *
     * @param name     名称
     * @param fallback 默认值
     */
    public @Nullable Boolean getBooleanClaim(String name, @Nonnull Boolean fallback) {
        return Objectx.getOrDefault(this.getBooleanClaim(name), fallback);
    }

    /**
     * 获取 Integer 类型的 Claim
     *
     * @param name 名称
     */
    public @Nullable Integer getIntegerClaim(String name) {
        return this.decodedToken.getClaim(name).asInt();
    }

    /**
     * 获取 Integer 类型的 Claim
     *
     * @param name     名称
     * @param fallback 默认值
     */
    public @Nullable Integer getIntegerClaim(String name, Integer fallback) {
        return Objectx.getOrDefault(this.getIntegerClaim(name), fallback);
    }

    /**
     * 获取 Long 类型的 Claim
     *
     * @param name 名称
     */
    public @Nullable Long getLongClaim(String name) {
        return this.decodedToken.getClaim(name).asLong();
    }

    /**
     * 获取 Long 类型的 Claim
     *
     * @param name     名称
     * @param fallback 默认值
     */
    public @Nullable Long getLongClaim(String name, @Nonnull Long fallback) {
        return Objectx.getOrDefault(this.getLongClaim(name), fallback);
    }

    /**
     * 获取 Double 类型的 Claim
     *
     * @param name 名称
     */
    public @Nullable Double getDoubleClaim(String name) {
        return this.decodedToken.getClaim(name).asDouble();
    }

    /**
     * 获取 Double 类型的 Claim
     *
     * @param name     名称
     * @param fallback 默认值
     */
    public @Nullable Double getDoubleClaim(String name, @Nonnull Double fallback) {
        return Objectx.getOrDefault(this.getDoubleClaim(name), fallback);
    }

    /**
     * 获取 Date 类型的 Claim
     *
     * @param name 名称
     */
    public @Nullable Date getDateClaim(String name) {
        return this.decodedToken.getClaim(name).asDate();
    }

    /**
     * 获取 Date 类型的 Claim
     *
     * @param name     名称
     * @param fallback 默认值
     */
    public @Nullable Date getDateClaim(String name, @Nonnull Date fallback) {
        return Objectx.getOrDefault(this.getDateClaim(name), fallback);
    }

    @Override
    public String toString() {
        return this.token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(token, session.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

    public static SessionBuilder builder() {
        return new SessionBuilder();
    }

    public static class SessionBuilder {
        private String id;
        private String accountId;
        private String username;
        private Boolean admin;
        private Boolean supervisor;
        private String source;
        private String ip;
        private String issuer;
        private Date issueTime;
        private String endpoint;
        private Duration timeout;
        private String tenantCode;
        private final Map<String, Object> claims = new HashMap<>();

        /**
         * 会话主键
         */
        public SessionBuilder id(@Nullable String id) {
            this.id = id;
            return this;
        }

        /**
         * 用户主键
         */
        public SessionBuilder accountId(@Nonnull String accountId) {
            this.accountId = accountId;
            return this;
        }

        /**
         * 用户名
         */
        public SessionBuilder username(@Nonnull String username) {
            this.username = username;
            return this;
        }

        /**
         * 是否管理员（系统管理员，安全管理员、安全保密员）
         */
        public SessionBuilder admin(boolean admin) {
            this.admin = admin;
            return this;
        }

        /**
         * 是否是超级管理员
         */
        public SessionBuilder supervisor(boolean supervisor) {
            this.supervisor = supervisor;
            return this;
        }

        /**
         * 颁发当前会话的会话主键
         */
        public SessionBuilder source(@Nullable String source) {
            this.source = source;
            return this;
        }

        /**
         * 申请会话时的客户端 IP
         */
        public SessionBuilder ip(@Nonnull String ip) {
            this.ip = ip;
            return this;
        }

        /**
         * 凭证颁发机构，通常使用域名
         */
        public SessionBuilder issuer(@Nonnull String issuer) {
            this.issuer = issuer;
            return this;
        }

        /**
         * 凭证颁发时间
         */
        public SessionBuilder issueTime(@Nullable Date issueTime) {
            this.issueTime = issueTime;
            return this;
        }

        /**
         * 终端类型
         */
        public SessionBuilder endpoint(@Nonnull String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        /**
         * 会话自动超时时间
         */
        public SessionBuilder timeout(@Nonnull Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * 租户标识
         */
        public SessionBuilder tenantCode(@Nonnull String tenantCode) {
            this.tenantCode = tenantCode;
            return this;
        }

        /**
         * 附加声明
         *
         * @param name  名称
         * @param value 必须包含的声明值
         */
        public SessionBuilder claim(@Nonnull String name, @Nonnull String value) {
            this.claims.put(name, value);
            return this;
        }

        /**
         * 附加声明
         *
         * @param name  名称
         * @param value 必须包含的声明值
         */
        public SessionBuilder claim(@Nonnull String name, @Nonnull Boolean value) {
            this.claims.put(name, value);
            return this;
        }

        /**
         * 附加声明
         *
         * @param name  名称
         * @param value 必须包含的声明值
         */
        public SessionBuilder claim(@Nonnull String name, @Nonnull Integer value) {
            this.claims.put(name, value);
            return this;
        }

        /**
         * 附加声明
         *
         * @param name  名称
         * @param value 必须包含的声明值
         */
        public SessionBuilder claim(@Nonnull String name, @Nonnull Long value) {
            this.claims.put(name, value);
            return this;
        }

        /**
         * 附加声明
         *
         * @param name  名称
         * @param value 必须包含的声明值
         */
        public SessionBuilder claim(@Nonnull String name, @Nonnull Double value) {
            this.claims.put(name, value);
            return this;
        }

        /**
         * 附加声明
         *
         * @param name  名称
         * @param value 必须包含的声明值
         */
        public SessionBuilder claim(@Nonnull String name, @Nonnull Date value) {
            this.claims.put(name, value);
            return this;
        }

        /**
         * 附加声明
         */
        public SessionBuilder claims(@Nullable Map<String, Object> claims) {
            if (Mapx.isNotEmpty(claims)) {
                this.claims.putAll(claims);
            }
            return this;
        }

        public Session build(String signKey) {
            return this.build((RSAPrivateKey) Signerx.RSA.getSignKey(signKey));
        }

        public Session build(Key signKey) {
            return this.build((RSAPrivateKey) signKey);
        }

        private Session build(RSAPrivateKey signKey) {
            this.id = Objectx.getOrDefault(this.id, Guidx.nextID());
            Assertx.mustNotBlank(this.accountId, "用户主键[accountId]必须不为空");
            Assertx.mustNotBlank(this.username, "用户名[username]必须不为空");
            this.admin = Objectx.getOrDefault(this.admin, Boolean.FALSE);
            this.supervisor = Objectx.getOrDefault(this.supervisor, Boolean.FALSE);
            Assertx.mustNotBlank(this.ip, "客户端IP[ip]必须不为空");
            Assertx.mustNotBlank(this.issuer, "凭证颁发机构[issuer]必须不为空");
            this.issueTime = Objectx.getOrDefault(this.issueTime, new Date());
            Assertx.mustNotBlank(this.endpoint, "终端类型必须不为空");
            // 默认 30 分钟有效期
            this.timeout = Objectx.getOrDefault(this.timeout, Duration.ofMinutes(30));
            Assertx.mustNotBlank(this.tenantCode, "租户标识[tenantCode]必须不为空");

            var jwt = JWT.create()
                    // JWT 唯一标识
                    .withJWTId(this.id)
                    // 用户主键
                    .withSubject(this.accountId)
                    // 用户帐号
                    .withClaim(SessionClaims.USERNAME, this.username)
                    // 是否管理员
                    .withClaim(SessionClaims.ADMIN, this.admin)
                    // 是否超级管理员
                    .withClaim(SessionClaims.SUPERVISOR, this.supervisor)
                    // 申请会话时的客户端 IP
                    .withClaim(SessionClaims.IP, this.ip)
                    // 终端类型
                    .withClaim(SessionClaims.ENDPOINT, this.endpoint)
                    // 颁发机构
                    .withIssuer(this.issuer)
                    // 颁发时间
                    .withClaim(SessionClaims.ISSUE_TIME, this.issueTime)
                    // 会话有效时间
                    .withClaim(SessionClaims.TIMEOUT, TimeUnit.MILLISECONDS.convert(this.timeout))
                    // 租户标识
                    .withClaim(SessionClaims.TENANT_CODE, tenantCode);

            // 颁发当前会话的会话主键
            if (Stringx.isNotBlank(this.source)) {
                jwt.withClaim(SessionClaims.SOURCE, this.source);
            }

            // 附加用户指定的 Claims
            if (Mapx.isNotEmpty(this.claims)) {
                for (var entry : this.claims.entrySet()) {
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
            var token = jwt.sign(Algorithm.RSA256(null, signKey));
            return Session.of(token);
        }
    }

    /**
     * 校验会话
     */
    public SessionVerifier verifier() {
        return new SessionVerifier(this.token);
    }

    /**
     * 会话校验器
     */
    public static class SessionVerifier {
        private final String token;

        protected SessionVerifier(@Nonnull String token) {
            this.token = token;
        }

        private String accountId;
        private String username;
        private Boolean admin;
        private Boolean supervisor;
        private String ip;
        private String issuer;
        private String endpoint;
        private String tenantCode;
        private Map<String, Object> claims = new HashMap<>();

        /**
         * 校验用户主键
         */
        public SessionVerifier accountId(String accountId) {
            this.accountId = accountId;
            return this;
        }

        /**
         * 校验用户名
         */
        public SessionVerifier username(String username) {
            this.username = username;
            return this;
        }

        /**
         * 校验是否管理员（系统管理员，安全管理员、安全保密员）
         */
        public SessionVerifier admin(boolean admin) {
            this.admin = admin;
            return this;
        }

        /**
         * 校验是否是超级管理员
         */
        public SessionVerifier supervisor(boolean supervisor) {
            this.supervisor = supervisor;
            return this;
        }

        /**
         * 校验申请会话时的客户端 IP
         */
        public SessionVerifier ip(String ip) {
            this.ip = ip;
            return this;
        }

        /**
         * 校验凭证颁发机构
         */
        public SessionVerifier issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        /**
         * 校验凭证颁发时间
         */
        public SessionVerifier endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        /**
         * 校验租户标识
         */
        public SessionVerifier tenantCode(@Nonnull String tenantCode) {
            this.tenantCode = tenantCode;
            return this;
        }

        /**
         * 校验声明
         *
         * @param name  名称
         * @param value 必须包含的声明值
         */
        public SessionVerifier claim(@Nonnull String name, @Nonnull String value) {
            this.claims.put(name, value);
            return this;
        }

        /**
         * 校验声明
         *
         * @param name  名称
         * @param value 必须包含的声明值
         */
        public SessionVerifier claim(@Nonnull String name, @Nonnull Boolean value) {
            this.claims.put(name, value);
            return this;
        }

        /**
         * 校验声明
         *
         * @param name  名称
         * @param value 必须包含的声明值
         */
        public SessionVerifier claim(@Nonnull String name, @Nonnull Integer value) {
            this.claims.put(name, value);
            return this;
        }

        /**
         * 校验声明
         *
         * @param name  名称
         * @param value 必须包含的声明值
         */
        public SessionVerifier claim(@Nonnull String name, @Nonnull Long value) {
            this.claims.put(name, value);
            return this;
        }

        /**
         * 校验声明
         *
         * @param name  名称
         * @param value 必须包含的声明值
         */
        public SessionVerifier claim(@Nonnull String name, @Nonnull Double value) {
            this.claims.put(name, value);
            return this;
        }

        /**
         * 校验声明
         *
         * @param name  名称
         * @param value 必须包含的声明值
         */
        public SessionVerifier claim(@Nonnull String name, @Nonnull Date value) {
            this.claims.put(name, value);
            return this;
        }

        /**
         * 校验声明
         *
         * @param claims 声明
         */
        public SessionVerifier claims(@Nullable Map<String, Object> claims) {
            if (Mapx.isNotEmpty(claims)) {
                this.claims.putAll(claims);
            }
            return this;
        }

        /**
         * 执行校验
         *
         * @param verifyKey 校验密钥
         */
        public void verify(String verifyKey) throws InvalidKeyException, InvalidSessionException {
            this.verify((RSAPublicKey) Signerx.RSA.getVerifyKey(verifyKey));
        }

        /**
         * 执行校验
         *
         * @param verifyKey 校验密钥
         */
        public void verify(Key verifyKey) throws InvalidKeyException, InvalidSessionException {
            this.verify((RSAPublicKey) verifyKey);
        }

        /**
         * 执行校验
         *
         * @param verifyKey 校验密钥
         */
        private void verify(RSAPublicKey verifyKey) {
            var verifier = JWT.require(Algorithm.RSA256(verifyKey, null));

            // 租户标识
            if (Stringx.isNotBlank(this.tenantCode)) {
                verifier.withClaim(SessionClaims.TENANT_CODE, this.tenantCode);
            }
            // 用户主键
            if (Stringx.isNotBlank(this.accountId)) {
                verifier.withSubject(this.accountId);
            }

            // 用户帐号
            if (Stringx.isNotBlank(this.username)) {
                verifier.withClaim(SessionClaims.USERNAME, this.username);
            }

            // 是否管理员
            if (this.admin != null) {
                verifier.withClaim(SessionClaims.ADMIN, this.admin);
            }

            // 是否超级管理员
            if (this.supervisor != null) {
                verifier.withClaim(SessionClaims.SUPERVISOR, this.supervisor);
            }

            // 终端类型
            if (Stringx.isNotBlank(this.endpoint)) {
                verifier.withClaim(SessionClaims.ENDPOINT, this.endpoint);
            }

            // 颁发者
            if (Stringx.isNotBlank(this.issuer)) {
                verifier.withIssuer(this.issuer);
            }

            // 申请会话时的客户端 IP
            if (Stringx.isNotBlank(this.ip)) {
                verifier.withClaim(SessionClaims.IP, this.ip);
            }

            // 凭证颁发时间
            if (Stringx.isNotBlank(this.endpoint)) {
                verifier.withClaim(SessionClaims.ENDPOINT, this.endpoint);
            }

            // 附加用户指定的 Claims
            if (Mapx.isNotEmpty(this.claims)) {
                for (var entry : this.claims.entrySet()) {
                    if (entry.getValue() instanceof Boolean b) {
                        verifier.withClaim(entry.getKey(), b);
                    } else if (entry.getValue() instanceof Integer i) {
                        verifier.withClaim(entry.getKey(), i);
                    } else if (entry.getValue() instanceof Long l) {
                        verifier.withClaim(entry.getKey(), l);
                    } else if (entry.getValue() instanceof Double d) {
                        verifier.withClaim(entry.getKey(), d);
                    } else if (entry.getValue() instanceof String s) {
                        verifier.withClaim(entry.getKey(), s);
                    } else if (entry.getValue() instanceof Date d) {
                        verifier.withClaim(entry.getKey(), d);
                    }
                }
            }

            try {
                verifier.build().verify(this.token);
            } catch (Exception exception) {
                throw new InvalidSessionException(exception.getLocalizedMessage(), exception);
            }
        }
    }

    /**
     * Session JWT Claims
     * 会话常用附加信息
     *
     * @author Alan Yeh
     * @since 2022/10/20
     */
    private interface SessionClaims {
        /**
         * 会话自动超时时间
         *
         * @see Long
         */
        String TIMEOUT = "t";
        /**
         * 租户标识
         *
         * @see String
         */
        String TENANT_CODE = "tc";
        /**
         * 用户名
         *
         * @see String
         */
        String USERNAME = "u";
        /**
         * 是否管理员（系统管理员，安全管理员、安全保密员）
         *
         * @see Boolean
         */
        String ADMIN = "a";
        /**
         * 是否超级管理员
         *
         * @see Boolean
         */
        String SUPERVISOR = "sv";
        /**
         * 颁发当前会话的会话主键
         */
        String SOURCE = "s";
        /**
         * 申请会话的客户端 IP
         *
         * @see String
         */
        String IP = "ip";

        /**
         * 颁发时间
         *
         * @see java.util.Date
         */
        String ISSUE_TIME = "it";
        /**
         * 终端类型
         *
         * @see String
         */
        String ENDPOINT = "ep";
    }
}
