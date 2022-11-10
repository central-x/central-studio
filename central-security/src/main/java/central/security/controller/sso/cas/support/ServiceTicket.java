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

import central.util.concurrent.Expired;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 服凭凭证
 *
 * @author Alan Yeh
 * @since 2022/11/07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTicket implements Expired, Serializable {
    @Serial
    private static final long serialVersionUID = -921601202972478549L;
    private final long timestamp = System.currentTimeMillis();

    /**
     * 过期时间
     */
    private Duration expires;

    @Override
    public long getExpire(TimeUnit unit) {
        return unit.convert((this.timestamp + this.expires.toMillis()) - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 应用标识
     */
    private String code;
    /**
     * Service Ticket
     */
    private String ticket;
    /**
     * 会话
     */
    private String session;

    private transient DecodedJWT sessionJwt;

    public DecodedJWT getSessionJwt() {
        if (this.sessionJwt == null){
            this.sessionJwt = JWT.decode(this.session);
        }
        return sessionJwt;
    }
}
