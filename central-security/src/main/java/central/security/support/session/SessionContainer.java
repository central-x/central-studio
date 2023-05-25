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

import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.Duration;
import java.util.List;

/**
 * 会话容器
 *
 * @author Alan Yeh
 * @since 2023/05/13
 */
public interface SessionContainer {
    /**
     * 保存会话凭证
     *
     * @param tenantCode 租户
     * @param accountId  会话所属帐户
     * @param token      会话凭证
     * @param expires    过期时间
     */
    void save(String tenantCode, String accountId, DecodedJWT token, Duration expires);

    /**
     * 获取指定用户所有会话
     *
     * @param tenantCode 租户
     * @param accountId  帐户主键
     */
    List<DecodedJWT> get(String tenantCode, String accountId);

    /**
     * 判断会话凭证是否存在
     *
     * @param tenantCode 租户
     * @param token      会话凭证
     */
    boolean exists(String tenantCode, DecodedJWT token);

    /**
     * 重新设置会话的过期时间
     *
     * @param tenantCode 租户
     * @param token      会话凭证
     * @param expires    新的过期时间
     */
    void expire(String tenantCode, DecodedJWT token, Duration expires);

    /**
     * 删除会话
     *
     * @param tenantCode 租户
     * @param token      会话凭证
     */
    void remove(String tenantCode, DecodedJWT token);

    /**
     * 清除指定用户所有登录凭证
     *
     * @param tenantCode 租户
     * @param accountId  帐户主键
     */
    void clear(String tenantCode, String accountId);
}
