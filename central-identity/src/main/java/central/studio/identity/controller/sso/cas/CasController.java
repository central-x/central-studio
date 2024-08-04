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

package central.studio.identity.controller.sso.cas;

import central.data.organization.Account;
import central.identity.client.SessionVerifier;
import central.lang.Stringx;
import central.net.http.executor.apache.ApacheHttpClientExecutor;
import central.net.http.proxy.HttpProxyFactory;
import central.net.http.proxy.contract.spring.SpringContract;
import central.provider.graphql.organization.AccountProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.starter.webmvc.servlet.WebMvcRequest;
import central.starter.webmvc.servlet.WebMvcResponse;
import central.starter.webmvc.view.JsonView;
import central.starter.webmvc.view.XmlView;
import central.studio.identity.controller.sso.cas.exception.CasErrorCode;
import central.studio.identity.controller.sso.cas.exception.CasException;
import central.studio.identity.controller.sso.cas.param.LoginParams;
import central.studio.identity.controller.sso.cas.param.LogoutParams;
import central.studio.identity.controller.sso.cas.param.ValidateParams;
import central.studio.identity.controller.sso.cas.support.CasSession;
import central.studio.identity.controller.sso.cas.support.ServiceTicket;
import central.studio.identity.core.attribute.CasAttributes;
import central.studio.identity.core.attribute.SessionAttributes;
import central.util.Guidx;
import central.util.Mapx;
import central.util.Objectx;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
@RequestMapping("/identity/sso/cas")
public class CasController {

    @Setter(onMethod_ = @Autowired)
    private DataContext context;

    @Setter(onMethod_ = @Autowired)
    private CasSession tickets;

    @Setter(onMethod_ = @Autowired)
    private SessionVerifier verifier;

    @Setter(onMethod_ = @Autowired)
    private AccountProvider accountProvider;

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
     * <p>
     * 此接口是接入方重定向进来的入口，使用标准的异常处理即可，不需要使用 CasException
     */
    @GetMapping("/login")
    public View login(@Validated LoginParams params,
                      WebMvcRequest request, WebMvcResponse response) throws IOException {
        if (!request.getRequiredAttribute(CasAttributes.ENABLED)) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "中央认证服务（CAS）已禁用");
        }

        var loginUrl = UriComponentsBuilder.fromUri(request.getUri())
                .replacePath(request.getTenantPath())
                .path("/identity")
                .path("/")
                .replaceQuery(null);

        var cookie = request.getRequiredAttribute(SessionAttributes.COOKIE);

        // 如果没有传递 service，则无法重定向到应用系统，直接跳转登录界面
        if (Stringx.isNullOrBlank(params.getService())) {
            return new RedirectView(loginUrl.build().toString());
        }

        // 验证 service 是否可信
        SaasContainer container = this.context.getData(DataFetcherType.SAAS);
        var application = container.getApplications().stream()
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

        // 如果传递了 renew 参数，则移除当前会话，重新跳转到登录界面
        if (Objects.equals(Boolean.TRUE, params.getRenew())) {
            // 如果要求重新登录
            cookie.remove(request, response);

            // 移除请求时带的 renew 参数，否则认证完再次回来时又被移除会话了
            var requestUri = UriComponentsBuilder.fromUri(request.getUri())
                    .replaceQueryParam("renew")
                    .build().toUriString();

            // 跳转到登录界面
            loginUrl.queryParam("redirect_uri", Stringx.encodeUrl(requestUri));
            return new RedirectView(loginUrl.build().toString());
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
            // 应用系统在接收到 ST 之后，需要通过 /identity/sso/cas/p3/serviceValidate 验证并拿到用户信息

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
     * <p>
     * 此接口是接入方通过调用接口的方式调用，因此需要使用 CasException 来处理异常，以保证遵循 CAS 协议
     */
    @PostMapping({"/serviceValidate", "/p3/serviceValidate"})
    public ModelAndView validate(@Validated ValidateParams params,
                                 WebMvcRequest request, WebMvcResponse response) throws CasException {
        if (!request.getRequiredAttribute(CasAttributes.ENABLED)) {
            throw new CasException(CasErrorCode.SERVICE_UNAVAILABLE, "中央认证服务（CAS）已禁用");
        }

        // 验证 service 不为空
        if (Stringx.isNullOrBlank(params.getService())) {
            throw new CasException(CasErrorCode.INVALID_SERVICE, "服务地址[service]不能为空");
        }
        if (params.getService().length() > 4096) {
            throw new CasException(CasErrorCode.INVALID_SERVICE, "服务地址[service]的长度必须小于 4096");
        }

        // 验证 service 是否可信
        SaasContainer container = this.context.getData(DataFetcherType.SAAS);
        var application = container.getApplications().stream()
                .filter(it -> Stringx.addSuffix(params.getService(), "/").startsWith(Stringx.addSuffix(it.getUrl() + it.getContextPath(), "/")))
                .findFirst();
        if (application.isEmpty()) {
            // 此应用不是已登记的应用，属于非法接入
            throw new CasException(CasErrorCode.INVALID_SERVICE, "服务地址[service]未登记: " + params.getService());
        }
        if (!application.get().getEnabled()) {
            // 此应用已禁用
            throw new CasException(CasErrorCode.INVALID_SERVICE, "服务地址[service]已禁用: " + params.getService());
        }

        // 验证服务凭证格式
        if (Stringx.isNullOrBlank(params.getTicket())) {
            throw new CasException(CasErrorCode.INVALID_TICKET_SPEC, "票据[ticket]必须不为空");
        }
        if (params.getTicket().length() > 256) {
            throw new CasException(CasErrorCode.INVALID_TICKET_SPEC, "票据[ticket]格式无效: " + params.getTicket());
        }
        if (!params.getTicket().startsWith("ST-")) {
            // 服务凭证必须以 ST- 开头
            throw new CasException(CasErrorCode.INVALID_TICKET_SPEC, "票据[ticket]格式无效: " + params.getTicket());
        }

        // 获取会话
        var ticket = this.tickets.remove(request.getTenantCode(), params.getTicket());
        if (ticket == null) {
            // 找不找指定服务凭证
            throw new CasException(CasErrorCode.INVALID_TICKET, "票据[ticket]无效: " + params.getTicket());
        }

        if (!application.get().getCode().equals(ticket.getCode())) {
            // 应用与票据不匹配
            throw new CasException(CasErrorCode.INVALID_TICKET, "票据[ticket]与服务地址[service]不符: " + params.getTicket());
        }

        // 验证会话有效性
        var session = ticket.getSession();
        if (!this.verifier.verify(session)) {
            throw new CasException(CasErrorCode.INVALID_TICKET, "票据[ticket]所在会话已过期: " + params.getTicket());
        }

        // 解析会话
        var account = this.accountProvider.findById(ticket.getSessionJwt().getSubject(), request.getTenantCode());

        this.tickets.bindTicket(request.getTenantCode(), ticket);
        return getSuccessView(request, response, account, null);
    }

    private ModelAndView getSuccessView(WebMvcRequest request, WebMvcResponse response, Account account, String pgt) {
        ModelAndView mv = new ModelAndView();
        mv.setStatus(HttpStatus.OK);

        var attrs = new HashMap<String, Object>();
        for (var attr : request.getRequiredAttribute(CasAttributes.SCOPES)) {
            for (var fetcher : attr.getFetchers()) {
                attrs.put(fetcher.field(), fetcher.getter().apply(account));
            }
        }

        var accepts = MediaType.parseMediaTypes(Objectx.getOrDefault(request.getHeader(HttpHeaders.ACCEPT), MediaType.ALL_VALUE));

        if (accepts.stream().anyMatch(MediaType.APPLICATION_XML::includes) || accepts.stream().anyMatch(MediaType.TEXT_XML::includes)) {
            var content = """
                    <cas:serviceResponse xmlns:cas="http://www.yale.edu/tp/cas">
                        <cas:authenticationSuccess>
                            <cas:user>{}</cas:user>
                            {}
                        </cas:authenticationSuccess>
                    </cas:serviceResponse>
                    """;

            var attrsContent = new StringBuilder("<cas:attributes>\n");
            for (var attr : attrs.entrySet()) {
                attrsContent.append("            <cas:").append(attr.getKey()).append(">").append(attr.getValue()).append("</cas:").append(attr.getKey()).append(">\n");
            }
            attrsContent.append("        </cas:attributes>");

            mv.setView(new XmlView(Stringx.format(content, account.getUsername(), attrsContent)));
        } else {
            mv.setView(new JsonView(Mapx.of(
                    Mapx.entry("user", account.getUsername()),
                    Mapx.entry("attributes", attrs)
            )));
        }

        return mv;
    }


    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new CustomizableThreadFactory("central-security.cas.logout"));

    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public View logout(@Validated LogoutParams params,
                       WebMvcRequest request, WebMvcResponse response) throws IOException, InterruptedException {
        if (!request.getRequiredAttribute(CasAttributes.ENABLED)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "中央认证服务（CAS）已禁用");
        }

        // 验证 service 是否可信
        SaasContainer container = this.context.getData(DataFetcherType.SAAS);
        var application = container.getApplications().stream()
                .filter(it -> Stringx.addSuffix(params.getService(), "/").startsWith(Stringx.addSuffix(it.getUrl() + it.getContextPath(), "/")))
                .findFirst();
        if (application.isEmpty()) {
            // 此应用不是已登记的应用，属于非法接入
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "服务[service]未登记: " + params.getService());
        }
        if (!application.get().getEnabled()) {
            // 此应用已禁用
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "服务[service]已禁用: " + params.getService());
        }

        var cookie = request.getRequiredAttribute(SessionAttributes.COOKIE);
        var session = cookie.get(request, response);

        if (Stringx.isNullOrBlank(session)) {
            // 找不到会话，因此可以直接跳转到应用系统指定的地址
            return new RedirectView(params.getService());
        }

        DecodedJWT sessionJwt;

        try {
            sessionJwt = JWT.decode(session);
        } catch (Exception ex) {
            // 解析会话异常
            cookie.remove(request, response);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "会话异常");
        }

        if (request.getRequiredAttribute(CasAttributes.SINGLE_LOGOUT_ENABLED)) {
            // 如果启用了单点退出功能，则执行单点退出逻辑
            // 1. 注销当前会话
            this.verifier.invalid(session);

            // 2. 销毁 Cookie
            cookie.remove(request, response);

            // 3. 通知所有应用系统注销会话，完成单点退出
            // 根据 CAS 协议的建议，服务器端在调用客户端的退出接口时，需要忽略所有客户端返回的错误信息，
            // 保证这些错误信息不会影响到 CAS 服务器的性能和其它业务系统的会话注消行为（Fire and forget）
            // 因此这里使用了线程池去发送注销会话的请求
            var tickets = this.tickets.getTicketBySession(request.getTenantCode(), sessionJwt);
            for (var ticket : tickets) {
                var app = container.getApplicationByCode(ticket.getCode());
                if (app == null) {
                    continue;
                }
                this.executor.submit(new LogoutRunner(app.getUrl() + app.getContextPath(), ticket.getTicket()));
            }

            // 4. 移除会话
            this.tickets.removeTicketBySession(request.getTenantCode(), sessionJwt);
        } else {
            // 如果不启用单点退出功能，则只通知调用本身口的应用系统注销会话
            var tickets = this.tickets.getTicketBySession(request.getTenantCode(), sessionJwt);
            var ticket = tickets.stream().filter(it -> Objects.equals(it.getCode(), application.get().getCode())).findFirst();
            ticket.ifPresent(serviceTicket -> this.executor.submit(new LogoutRunner(application.get().getUrl() + application.get().getContextPath(), serviceTicket.getTicket())));
        }

        // 由于是异步去调用第三方的退出登录，因此最好等待 1 秒之后，再重定到 service 指定的 URL 地址
        // 根据 CAS 协议的建议，这里应该是先返回一个界面，用于提示用户会话已经注销了，然后等待两三秒之后，再重定向到 service 指定的 URL 地址
        // 但是这样干有点有麻烦，偷个懒
        Thread.sleep(1000);
        return new RedirectView(params.getService());
    }

    @RequiredArgsConstructor
    private static class LogoutRunner implements Runnable {

        private final String url;
        private final String ticket;

        @Override
        public void run() {
            String content = Stringx.format("""
                     <samlp:LogoutRequest xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol" ID="{}" Version="2.0" IssueInstant="{}">
                         <saml:NameID xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion">@NOT_USED@</saml:NameID>
                         <samlp:SessionIndex>{}</samlp:SessionIndex>
                    </samlp:LogoutRequest>
                    """, Guidx.nextID(), OffsetDateTime.now().toString(), this.ticket);
            var client = HttpProxyFactory.builder(ApacheHttpClientExecutor.Default())
                    .contact(new SpringContract())
                    .baseUrl(this.url)
                    .target(LogoutClient.class);
            client.logout(content);
        }

        private interface LogoutClient {
            // CAS 协议这个退出登录也是个骚操作
            // 发送的时候，这个 XML 竟然不是请求体，而是作为请求参数 logoutRequest 的参数值
            // 去查看 org.jasig.cas:cas-client-core 的源代码的时候，发现这傻逼竟然是通过一个 Filter 全局拦截所有 GET 和 POST 请求，然后看这个
            // 请求是否有携带 logoutRequest 这个参数，如果有带这个参数，则认为这个请求就是个退出登录的请求，真™️骚到家了
            @PostMapping(value = "/", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
            void logout(@RequestPart String logoutRequest);
        }
    }
}
