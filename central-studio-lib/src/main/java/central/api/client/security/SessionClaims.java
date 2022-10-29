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

/**
 * Session JWT Claims
 * 会话常用附加信息
 *
 * @author Alan Yeh
 * @since 2022/10/20
 */
public interface SessionClaims {
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
