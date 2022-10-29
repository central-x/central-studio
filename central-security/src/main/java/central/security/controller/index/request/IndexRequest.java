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

package central.security.controller.index.request;

import central.api.client.security.SessionVerifier;
import central.lang.Assertx;
import central.lang.Stringx;
import central.security.controller.index.IndexController;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.core.attribute.ExchangeAttributes;
import central.security.core.body.InputStreamBody;
import central.security.core.body.RedirectBody;
import central.security.core.request.Request;
import central.util.Objectx;
import central.validation.Label;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * 首页
 *
 * @author Alan Yeh
 * @see IndexController#index
 * @since 2022/10/19
 */
public class IndexRequest extends Request {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Params {
        @Label("重定向地址")
        private String redirectUri;
    }

    @Getter
    private final Params params;

    public IndexRequest(HttpServletRequest request) {
        super(request);
        this.params = new Params();
        // 兼容两种传参方式
        this.params.setRedirectUri(Objectx.getOrDefault(request.getParameter("redirect_uri"), request.getParameter("redirectUri")));
    }

    public static IndexRequest of(HttpServletRequest request) {
        return new IndexRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new Action();
    }

    private static class Action extends SecurityAction implements InitializingBean {

        private SessionVerifier verifier;

        @Override
        public void afterPropertiesSet() throws Exception {
            this.verifier = this.getBean(SessionVerifier.class);
        }

        @Override
        public void execute(SecurityExchange exchange) {
            var request = (IndexRequest) exchange.getRequest();

            if (Stringx.isNotBlank(request.getParams().getRedirectUri())) {
                // 检测 redirectUrl 是否跨域
                // 统一认证不允许进行跨域验证，只能重定向到本域的地址
                if (!request.getParams().getRedirectUri().startsWith(request.getRequest().getScheme() + "://" + request.getRequest().getServerName())) {
                    // 如果出现跨域，直接重定向到统一认证自己的界面
                    var location = URI.create(UriComponentsBuilder.fromUri(request.getUri()).replaceQuery("").build().toString());
                    exchange.getResponse().setStatus(HttpStatus.TEMPORARY_REDIRECT);
                    exchange.getResponse().setBody(new RedirectBody(location));
                    return;
                }

                // 如果当前会话有效，则直接重定向到指定的地址
                var cookie = exchange.getRequiredAttribute(ExchangeAttributes.COOKIE);
                var token = cookie.get(exchange);

                if (this.verifier.verify(token)) {
                    var location = URI.create(request.getParams().getRedirectUri());
                    exchange.getResponse().setStatus(HttpStatus.TEMPORARY_REDIRECT);
                    exchange.getResponse().setBody(new RedirectBody(location));
                    return;
                }
            }

            // 其余情况，返回登录界面
            var index = Assertx.requireNotNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("public/index.html"), () -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "找不到首页文件"));
            exchange.getResponse().setBody(new InputStreamBody(index, new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8)));
        }
    }
}
