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

package central.security.core;

import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * Security Session
 * 安全会话管理
 *
 * @author Alan Yeh
 * @since 2022/10/20
 */
public interface SecuritySession {

    /**
     * 保存会话
     *
     * @param session 会话
     * @param limit   会话上限
     */
    void save(DecodedJWT session, Integer limit);

    /**
     * 验证会话是否有效
     *
     * @param session 会话
     */
    boolean verify(DecodedJWT session);

    /**
     * 将指定会话置为无效
     *
     * @param session 会话
     */
    void invalid(DecodedJWT session);

    /**
     * 将指定的用户的所有会话都置为无效
     *
     * @param tenant    租户
     * @param accountId 用户
     */
    void clear(String tenant, String accountId);
}
