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

import central.api.client.security.SessionClient;
import central.security.controller.index.IndexController;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.core.attribute.ExchangeAttributes;
import central.security.core.body.StringBody;
import central.security.core.request.Request;
import central.validation.Label;
import central.validation.Validatex;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

/**
 * 登录接口
 *
 * @author Alan Yeh
 * @see IndexController#login
 * @since 2022/10/19
 */
public class LoginRequest extends Request {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Params {
        @Label("帐户")
        @NotBlank
        private String account;

        @Label("密码")
        @NotBlank
        private String password;

        @Label("验证码")
        private String captcha;

        @Label("终端密钥")
        private String secret;
    }

    @Getter
    private final Params params;

    public LoginRequest(HttpServletRequest request) {
        super(request);
        if (MediaType.APPLICATION_JSON.isCompatibleWith(this.getContentType())) {
            this.params = this.getBody(Params.class);
        } else {
            this.params = this.getParameter(Params.class);
        }
        Validatex.Default().validate(params, new Class[0], (message) -> new ResponseStatusException(HttpStatus.BAD_REQUEST, message));
    }

    public static LoginRequest of(HttpServletRequest request) {
        return new LoginRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new LoginAction();
    }

    private static class LoginAction extends SecurityAction implements InitializingBean {

        private SessionClient client;

        @Override
        public void afterPropertiesSet() throws Exception {
            this.client = this.getBean(SessionClient.class);
        }

        @Override
        public void execute(SecurityExchange exchange) {
            var params = ((LoginRequest) exchange.getRequest()).getParams();
            try {
                var session = this.client.login(params.getAccount(), params.getPassword(), params.getSecret(), null);

                // 把会话放到 Cookie 里
                exchange.getRequiredAttribute(ExchangeAttributes.COOKIE).set(exchange, session);
                exchange.getResponse().setBody(new StringBody("登录成功"));
            } catch (Exception ex){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "帐号或密码错误");
            }
        }
    }
}
