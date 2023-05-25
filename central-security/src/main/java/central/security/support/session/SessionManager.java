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

import central.data.organization.Account;
import central.security.controller.session.support.Endpoint;

import java.util.Map;

/**
 * Security Session
 * 安全会话管理
 *
 * @author Alan Yeh
 * @since 2023/05/13
 */
public interface SessionManager {
    /**
     * 获取公钥
     */
    String getPublicKey();

    /**
     * 签发会话
     *
     * @param tenantCode 租户
     * @param issuer     签发组织（一般是域名）
     * @param timeout    会话超时时间，毫秒。如果为空，则默认为 1800000ms（30分种）
     * @param account    会话所属帐户
     * @param endpoint   会话所属终端
     * @param limit      会话数量上限
     * @param claims     会话附加属性
     * @return 已签发的话会
     */
    String issue(String tenantCode, String issuer, Long timeout, Account account, Endpoint endpoint, Integer limit, Map<String, Object> claims);

    /**
     * 验证会话凭证是否有效
     *
     * @param tenantCode 租户
     * @param token      会话凭证证
     */
    boolean verify(String tenantCode, String token);

    /**
     * 将指定会话置为无效
     *
     * @param tenantCode 租户
     * @param token      会话凭证
     */
    void invalid(String tenantCode, String token);

    /**
     * 将指定的用户的所有会话都置为无效
     *
     * @param tenantCode 租户
     * @param accountId  用户
     */
    void clear(String tenantCode, String accountId);
}
