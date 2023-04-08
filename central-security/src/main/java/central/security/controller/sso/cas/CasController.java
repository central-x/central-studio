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

package central.security.controller.sso.cas;

import central.api.client.security.SessionVerifier;
import central.api.scheduled.DataContext;
import central.api.scheduled.fetcher.DataFetcherType;
import central.lang.Stringx;
import central.security.controller.sso.cas.param.LoginParams;
import central.security.controller.sso.cas.request.LogoutRequest;
import central.security.controller.sso.cas.request.ValidateRequest;
import central.security.controller.sso.cas.support.CasSession;
import central.security.controller.sso.cas.support.ServiceTicket;
import central.security.core.SecurityDispatcher;
import central.security.core.SecurityExchange;
import central.security.core.SecurityResponse;
import central.security.core.attribute.CasAttributes;
import central.security.core.attribute.SessionAttributes;
import central.starter.webmvc.servlet.WebMvcRequest;
import central.starter.webmvc.servlet.WebMvcResponse;
import central.util.Guidx;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Central Authentication Service
 *
 * @author Alan Yeh
 * @see <a href="https://apereo.github.io/cas/6.6.x/protocol/CAS-Protocol.html">CAS Protocol</a>
 * @see <a href="https://apereo.github.io/cas/6.6.x/protocol/CAS-Protocol-Specification.html">CAS Protocol Specification</a>
 * @since 2022/10/19
 */
@Controller
@RequestMapping("/sso/cas")
public class CasController {
    @Setter(onMethod_ = @Autowired)
    private SecurityDispatcher dispatcher;

    @Setter(onMethod_ = @Autowired)
    private DataContext context;

    @Setter(onMethod_ = @Autowired)
    private CasSession tickets;

    @Setter(onMethod_ = @Autowired)
    private SessionVerifier verifier;

    private static final AtomicInteger serial = new AtomicInteger(1000);

    private static synchronized int getSerial() {
        return serial.updateAndGet(value -> {
            if (value > 9999) {
                return 1000;
            } else {
                return value + 1;
            }
        });
    }

    /**
     * 认证入口
     */
    @GetMapping("/login")
    public View login(@Validated LoginParams params,
                      WebMvcRequest request, WebMvcResponse response) throws IOException {
        if (!request.getRequiredAttribute(CasAttributes.ENABLED)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "中央认证服务（CAS）已禁用");
        }

        var loginUrl = UriComponentsBuilder.fromUri(request.getUri())
                .replacePath(request.getTenantPath())
                .path("/security")
                .path("/")
                .replaceQuery(null);

        var cookie = request.getRequiredAttribute(SessionAttributes.COOKIE);

        // 如果没有传递 service，则无法重定向到应用系统，直接跳转登录界面
        if (Stringx.isNullOrBlank(params.getService())) {
            if (Objects.equals(Boolean.TRUE, params.getRenew())) {
                // 如果要求重新登录
                cookie.remove(request, response);
            }

            return new RedirectView(loginUrl.build().toString());
        }

        // 验证 service 是否可信
        var application = this.context.getData(DataFetcherType.SAAS).getApplications().stream()
                .filter(it -> Stringx.addSuffix(params.getService(), "/").startsWith(Stringx.addSuffix(it.getUrl() + it.getContextPath(), "/")))
                .findFirst().orElse(null);
        if (application == null) {
            // 此应用不是已登记的应用，属于非法接入
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "服务[service]未登记: " + params.getService());
        }
        if (!application.getEnabled()) {
            // 此应用已禁用
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "服务[service]已禁用: " + params.getService());
        }

        // 验证会话
        var session = cookie.get(request, response);
        if (!verifier.verify(session)) {
            // 如果会话无效，则跳转到登录界面
            if (Objects.equals(Boolean.TRUE, params.getGateway())) {
                // 如果 gateway 不为 true，则要求直接重定向回 service 指定的地址
                return new RedirectView(params.getService());
            } else {
                // 跳转到登录界面
                loginUrl.queryParam("redirect_uri", Stringx.encodeUrl(request.getUri().toString()));
                return new RedirectView(loginUrl.build().toString());
            }
        } else {
            // 会话有效，则生成一次性的 ST（Service Ticket）返回给应用系统
            // 应用系统在接收到 ST 之后，需要通过 /security/sso/cas/p3/serviceValidate 验证并拿到用户信息

            var ticket = ServiceTicket.builder()
                    .expires(request.getRequiredAttribute(CasAttributes.TIMEOUT))
                    .code(application.getCode())
                    .ticket("ST-" + getSerial() + "-" + Guidx.nextID())
                    .session(session)
                    .build();

            this.tickets.save(request.getTenantCode(), ticket);

            // 协带 ST 重定向回应用系统
            var location = UriComponentsBuilder.fromUriString(params.getService())
                    .queryParam("ticket", ticket.getTicket())
                    .build().toString();

            return new RedirectView(location);
        }
    }

    /**
     * ST 认证
     */
    @PostMapping({"/serviceValidate", "/p3/serviceValidate"})
    public void validate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.dispatcher.dispatch(SecurityExchange.of(ValidateRequest.of(request), SecurityResponse.of(response)));
    }

    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.dispatcher.dispatch(SecurityExchange.of(LogoutRequest.of(request), SecurityResponse.of(response)));
    }
}
