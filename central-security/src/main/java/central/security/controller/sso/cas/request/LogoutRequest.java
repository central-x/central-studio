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

package central.security.controller.sso.cas.request;

import central.api.client.security.SessionVerifier;
import central.api.scheduled.DataContext;
import central.api.scheduled.fetcher.DataFetchers;
import central.lang.Stringx;
import central.net.http.executor.okhttp.OkHttpExecutor;
import central.net.http.proxy.HttpProxyFactory;
import central.net.http.proxy.contract.spring.SpringContract;
import central.security.controller.sso.cas.CasController;
import central.security.controller.sso.cas.support.CasSession;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.core.attribute.ExchangeAttributes;
import central.security.core.body.RedirectBody;
import central.security.core.request.Request;
import central.util.Guidx;
import central.validation.Label;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Cas Logout Request
 *
 * @author Alan Yeh
 * @see CasController#logout
 * @since 2022/11/07
 */
public class LogoutRequest extends Request {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Params {

        @Label("服务地址")
        @NotBlank
        @Size(min = 1, max = 4096)
        private String service;
    }

    @Getter
    private final Params params;

    public LogoutRequest(HttpServletRequest request) {
        super(request);
        this.params = this.bindParameter(Params.class);
    }

    public static LogoutRequest of(HttpServletRequest request) {
        return new LogoutRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new LogoutAction();
    }

    private static class LogoutAction extends SecurityAction implements InitializingBean {
        private DataContext context;

        private CasSession tickets;

        private SessionVerifier verifier;

        private ExecutorService executor;

        @Override
        public void afterPropertiesSet() throws Exception {
            this.context = this.getBean(DataContext.class);
            this.tickets = this.getBean(CasSession.class);
            this.verifier = this.getBean(SessionVerifier.class);
            this.executor = this.getBean(ExecutorService.class, () -> {
                return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new CustomizableThreadFactory("Logout.Executor"));
            });
        }

        @Override
        @SneakyThrows
        public void execute(SecurityExchange exchange) {
            if (!exchange.getRequiredAttribute(ExchangeAttributes.Cas.ENABLED)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "中央认证服务（CAS）已禁用");
            }

            var request = (LogoutRequest) exchange.getRequest();

            // 验证 service 是否可信
            var application = this.context.get(DataFetchers.SAAS).getApplications().stream()
                    .filter(it -> Stringx.addSuffix(request.getParams().getService(), "/").startsWith(Stringx.addSuffix(it.getUrl() + it.getContextPath(), "/")))
                    .findFirst().orElse(null);
            if (application == null) {
                // 此应用不是已登记的应用，属于非法接入
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "服务[service]未登记: " + request.getParams().getService());
            }
            if (!application.getEnabled()) {
                // 此应用已禁用
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "服务[service]已禁用: " + request.getParams().getService());
            }

            var cookie = exchange.getRequiredAttribute(ExchangeAttributes.Session.COOKIE);
            var session = cookie.get(exchange);

            if (Stringx.isNullOrBlank(session)) {
                // 找不到会话，因此可以直接跳转到应用系统指定的地址
                exchange.getResponse().setStatus(HttpStatus.TEMPORARY_REDIRECT);
                exchange.getResponse().setBody(new RedirectBody(URI.create(request.getParams().getService())));
                return;
            }

            DecodedJWT sessionJwt;

            try {
                sessionJwt = JWT.decode(session);
            } catch (Exception ex) {
                // 解析会话异常
                cookie.remove(exchange);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "会话异常");
            }

            if (exchange.getRequiredAttribute(ExchangeAttributes.Cas.SINGLE_LOGOUT_ENABLED)) {
                // 如果启用了单点退出功能，则执行单点退出逻辑
                // 1. 注销当前会话
                this.verifier.invalid(session);

                // 2. 销毁 Cookie
                cookie.remove(exchange);

                // 3. 通知所有应用系统注销会话，完成单点退出
                // 根据 CAS 协议的建议，服务器端在调用客户端的退出接口时，需要忽略所有客户端返回的错误信息，
                // 保证这些错误信息不会影响到 CAS 服务器的性能和其它业务系统的会话注消行为（Fire and forget）
                // 因此这里使用了线程池去发送注销会话的请求
                var tickets = this.tickets.getTicketBySession(request.getTenantCode(), sessionJwt);
                for (var ticket : tickets) {
                    var app = this.context.get(DataFetchers.SAAS).getApplicationByCode(ticket.getCode());
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
                var ticket = tickets.stream().filter(it -> Objects.equals(it.getCode(), application.getCode())).findFirst().orElse(null);
                if (ticket != null) {
                    this.executor.submit(new LogoutRunner(application.getUrl() + application.getContextPath(), ticket.getTicket()));
                }
            }

            // 由于是异步去调用第三方的退出登录，因此最好等待 1 秒之后，再重定到 service 指定的 URL 地址
            // 根据 CAS 协议的建议，这里应该是先返回一个界面，用于提示用户会话已经注销了，然后等待两三秒之后，再重定向到 service 指定的 URL 地址
            // 但是这样干有点有麻烦，偷个懒
            Thread.sleep(1000);
            exchange.getResponse().setStatus(HttpStatus.TEMPORARY_REDIRECT);
            exchange.getResponse().setBody(new RedirectBody(URI.create(request.getParams().getService())));
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
                var client = HttpProxyFactory.builder(OkHttpExecutor.Default())
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
}
