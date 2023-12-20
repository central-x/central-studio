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

package central.identity.core.attribute;

import central.lang.Attribute;
import central.identity.controller.sso.cas.support.Scope;

import java.time.Duration;
import java.util.Set;

/**
 * 中央认证服务（CAS）配置
 *
 * @author Alan Yeh
 * @since 2023/02/15
 */
public interface CasAttributes {
    /**
     * 是否启用
     */
    Attribute<Boolean> ENABLED = Attribute.of("cas.enabled", false);
    /**
     * 授权范围
     */
    Attribute<Set<Scope>> SCOPES = Attribute.of("cas.scopes", Set.of(Scope.BASIC));
    /**
     * 服务凭证（Service Ticket）有效期
     */
    Attribute<Duration> TIMEOUT = Attribute.of("cas.timeout", Duration.ofMinutes(10));
    /**
     * 是否启用单点退出
     */
    Attribute<Boolean> SINGLE_LOGOUT_ENABLED = Attribute.of("cas.single_logout", true);
}
