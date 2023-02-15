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
import central.api.provider.organization.AccountProvider;
import central.api.scheduled.DataContext;
import central.api.scheduled.fetcher.DataFetcherType;
import central.bean.OptionalEnum;
import central.data.organization.Account;
import central.lang.Stringx;
import central.security.controller.sso.cas.CasController;
import central.security.controller.sso.cas.option.Format;
import central.security.controller.sso.cas.support.CasSession;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.core.attribute.CasAttributes;
import central.security.core.body.JsonBody;
import central.security.core.body.XmlBody;
import central.security.core.request.Request;
import central.validation.Enums;
import central.validation.Label;
import central.validation.Prefix;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * 校验
 *
 * @author Alan Yeh
 * @see CasController#validate
 * @since 2022/11/07
 */
public class ValidateRequest extends Request {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Params {

        @Label("服务地址")
        @NotBlank
        @Size(min = 1, max = 4096)
        private String service;

        @Label("服务凭证")
        @NotBlank
        @Prefix("ST-")
        @Size(min = 1, max = 256)
        private String ticket;

//        /**
//         * Proxy Granting Ticket Url。代理回调 URL。
//         * (暂不支持)
//         */
//        private String pgtUrl;

//        /**
//         * 如果这个值被设置为 true，则 ST（Service Ticket）必须是由用户授权颁发的，而不能由已存在的统一会话颁发的。
//         * （暂不支持）
//         */
//        private Boolean renew;

        @Label("格式")
        @Enums(Format.class)
        private String format = "json";
    }

    @Getter
    private final Params params;

    public ValidateRequest(HttpServletRequest request) {
        super(request);
        if (MediaType.APPLICATION_JSON.isCompatibleWith(this.getContentType())) {
            this.params = this.bindBody(Params.class);
        } else {
            this.params = this.bindParameter(Params.class);
        }
    }

    public static ValidateRequest of(HttpServletRequest request) {
        return new ValidateRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new ValidateAction();
    }

    private static class ValidateAction extends SecurityAction implements InitializingBean {

        private DataContext context;

        private CasSession tickets;

        private SessionVerifier verifier;

        private AccountProvider provider;

        @Override
        public void afterPropertiesSet() throws Exception {
            this.context = this.getBean(DataContext.class);
            this.tickets = this.getBean(CasSession.class);
            this.verifier = this.getBean(SessionVerifier.class);
            this.provider = this.getBean(AccountProvider.class);
        }

        @Override
        public void execute(SecurityExchange exchange) {
            if (!exchange.getRequiredAttribute(CasAttributes.ENABLED)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "中央认证服务（CAS）已禁用");
            }

            var request = (ValidateRequest) exchange.getRequest();

            // 验证 service 是否可信
            var application = this.context.getData(DataFetcherType.SAAS).getApplications().stream()
                    .filter(it -> Stringx.addSuffix(request.getParams().getService(), "/").startsWith(Stringx.addSuffix(it.getUrl() + it.getContextPath(), "/")))
                    .findFirst().orElse(null);
            if (application == null) {
                // 此应用不是已登记的应用，属于非法接入
                sendError(exchange, ErrorCode.INVALID_SERVICE, "服务[service]未登记: " + request.getParams().getService(), request.getParams().getFormat());
                return;
            }
            if (!application.getEnabled()) {
                // 此应用已禁用
                sendError(exchange, ErrorCode.INVALID_SERVICE, "服务[service]已禁用: " + request.getParams().getService(), request.getParams().getFormat());
                return;
            }

            // 获取会话
            var ticket = this.tickets.remove(request.getTenantCode(), request.getParams().getTicket());
            if (ticket == null) {
                // 找不找指定服务凭证
                sendError(exchange, ErrorCode.INVALID_TICKET, "票据[ticket]无效: " + request.getParams().getTicket(), request.getParams().getFormat());
                return;
            }

            if (!application.getCode().equals(ticket.getCode())) {
                // 应用与票据不匹配
                sendError(exchange, ErrorCode.INVALID_TICKET, "票据[ticket]与服务[service]不符: " + request.getParams().getTicket(), request.getParams().getFormat());
                return;
            }

            // 验证会话有效性
            var session = ticket.getSession();
            if (!this.verifier.verify(session)) {
                sendError(exchange, ErrorCode.INVALID_TICKET_SPEC, "票据[ticket]所在会话已过期: " + request.getParams().getTicket(), request.getParams().getFormat());
                return;
            }

            // 解析会话
            var account = this.provider.findById(ticket.getSessionJwt().getSubject(), request.getTenantCode());
            sendSuccess(exchange, account, null, request.getParams().getFormat());

            this.tickets.bindTicket(request.getTenantCode(), ticket);
        }

        private void sendSuccess(SecurityExchange exchange, Account account, String pgt, String format) {
            var attrs = new HashMap<String, Object>();
            for (var attr : exchange.getRequiredAttribute(CasAttributes.SCOPES)) {
                for (var fetcher : attr.getFetchers()) {
                    attrs.put(fetcher.field(), fetcher.getter().apply(account));
                }
            }

            exchange.getResponse().setStatus(HttpStatus.OK);
            if (Format.JSON.isCompatibleWith(format)) {
                exchange.getResponse().setBody(new JsonBody(Map.of(
                        "user", account.getUsername(),
                        "attributes", attrs
                )));
            } else {
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

                exchange.getResponse().setBody(new XmlBody(Stringx.format(content, account.getUsername(), attrsContent)));
            }
        }

        private void sendError(SecurityExchange exchange, ErrorCode code, String message, String format) {
            if (Format.XML.isCompatibleWith(format)) {
                var content = """
                        <cas:serviceResponse xmlns:cas="http://www.yale.edu/tp/cas">
                            <cas:authenticationFailure code="{}">{}</cas:authenticationFailure>
                        </cas:serviceResponse>
                        """;
                exchange.getResponse().setStatus(code.getValue());
                exchange.getResponse().setBody(new XmlBody(Stringx.format(content, code.getName(), message)));
            } else {
                exchange.getResponse().setStatus(code.getValue());
                exchange.getResponse().setBody(new JsonBody(Map.of(
                        "code", code.getName(),
                        "description", message
                )));
            }
        }

        @Getter
        @RequiredArgsConstructor
        private enum ErrorCode implements OptionalEnum<HttpStatus> {
            INVALID_REQUEST("INVALID_REQUEST", HttpStatus.BAD_REQUEST),
            INVALID_TICKET_SPEC("INVALID_TICKET_SPEC", HttpStatus.BAD_REQUEST),
            UNAUTHORIZED_SERVICE_PROXY("UNAUTHORIZED_SERVICE_PROXY", HttpStatus.BAD_REQUEST),
            INVALID_PROXY_CALLBACK("INVALID_PROXY_CALLBACK", HttpStatus.BAD_REQUEST),
            INVALID_TICKET("INVALID_TICKET", HttpStatus.BAD_REQUEST),
            INVALID_SERVICE("INVALID_SERVICE", HttpStatus.BAD_REQUEST),
            INTERNAL_ERROR("INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);

            private final String name;
            private final HttpStatus value;
        }
    }
}
