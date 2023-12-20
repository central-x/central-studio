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

package central.identity.controller.sso.oauth.support;

/**
 * OAuth 会话管理
 *
 * @author Alan Yeh
 * @since 2022/11/07
 */
public interface OAuthSession {
    /**
     * 保存授权码
     *
     * @param tenant  租户标识
     * @param code    授权码
     */
    void saveCode(String tenant, AuthorizationCode code);

    /**
     * 获取授权码
     *
     * @param tenant 租户标识
     * @param code   授权码标识
     */
    AuthorizationCode getCode(String tenant, String code);

    /**
     * 保存授权事务
     *
     * @param tenant      租户标识
     * @param transaction 事务
     */
    void saveTransaction(String tenant, AuthorizationTransaction transaction);

    /**
     * 获取授权事务
     *
     * @param tenant 租户标识
     * @param id     事务主键
     */
    AuthorizationTransaction getAndRemoveTransaction(String tenant, String id);
}
