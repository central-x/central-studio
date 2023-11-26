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

package central.security.controller.sso.cas.support;

import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.List;

/**
 * 中央认证服务会话管理
 *
 * @author Alan Yeh
 * @since 2022/11/07
 */
public interface CasSession {
    /**
     * 保存服务凭证
     *
     * @param tenant  租户标识
     * @param ticket  服务凭证
     */
    void save(String tenant, ServiceTicket ticket);

    /**
     * 获取服务凭证
     *
     * @param tenant 租户标识
     * @param ticket 凭证标识
     */
    ServiceTicket remove(String tenant, String ticket);

    /**
     * 绑定会话与服务凭证的关系，用于单点退出
     *
     * @param tenant 租户标识
     * @param ticket 服务凭证
     */
    void bindTicket(String tenant, ServiceTicket ticket);

    /**
     * 根据会话获取已绑定的服务凭证
     *
     * @param tenant  租户标识
     * @param session 会话
     */
    List<ServiceTicket> getTicketBySession(String tenant, DecodedJWT session);

    /**
     * 移除会话与服务凭证的绑定关系
     *
     * @param tenant  租户标识
     * @param session 会话
     */
    void removeTicketBySession(String tenant, DecodedJWT session);
}
