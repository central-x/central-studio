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

package central.gateway.core.attribute;

import central.data.ten.Application;
import central.data.ten.ApplicationModule;
import central.data.ten.Tenant;
import central.gateway.core.body.EmptyBody;
import central.gateway.core.body.HttpResponseBody;
import central.lang.Attribute;
import central.util.Guidx;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import reactor.netty.http.client.HttpClientResponse;

import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Duration;
import java.util.Date;

/**
 * 响应属性
 *
 * @author Alan Yeh
 * @since 2022/10/13
 */
public interface ExchangeAttributes {

    /**
     * 网关转发的目标服务器地址
     * 主要取 Schema、Host、Port，其余的由 exchange.getRequest().getURI() 来决定
     */
    Attribute<URI> TARGET_SERVER = Attribute.of(ExchangeAttributes.class.getName() + ".target_server");

    /**
     * 租户信息
     */
    Attribute<Tenant> TENANT = Attribute.of(ExchangeAttributes.class.getName() + ".tenant");

    /**
     * 网关转发的目标应用
     */
    Attribute<Application> TARGET_APPLICATION = Attribute.of(ExchangeAttributes.class.getName() + ".target_application");

    /**
     * 超时时间
     */
    Attribute<Integer> TIMEOUT = Attribute.of(ExchangeAttributes.class.getName() + ".timeout");

    /**
     * 响应
     */
    Attribute<HttpClientResponse> RESPONSE = Attribute.of(ExchangeAttributes.class.getName() + ".response");

    /**
     * 响应体
     */
    Attribute<HttpResponseBody> RESPONSE_BODY = Attribute.of(ExchangeAttributes.class.getName() + ".response_body", EmptyBody::new);

    /**
     * 网关转发的目标应用模块
     */
    Attribute<ApplicationModule> TARGET_APPLICATION_MODULE = Attribute.of(ExchangeAttributes.class.getName() + ".target_application_module");

    /**
     * 网关实际转发地址
     */
    Attribute<URI> FORWARDING_URI = Attribute.of(ExchangeAttributes.class.getName() + ".forwarding_uri");

    /**
     * 原始请求路径
     */
    Attribute<URI> ORIGIN_URI = Attribute.of(ExchangeAttributes.class.getName() + ".origin_uri");

    /**
     * 当前请求来源地址
     */
    Attribute<InetSocketAddress> REMOTE_ADDRESS = Attribute.of(ExchangeAttributes.class.getName() + ".remote_host");

    /**
     * JWT Token
     */
    Attribute<JWTCreator.Builder> TOKEN = Attribute.of(ExchangeAttributes.class.getName() + ".claims", () -> JWT.create().withJWTId(Guidx.nextID()).withExpiresAt(new Date(System.currentTimeMillis() + Duration.ofMinutes(3).toMillis())));
}
