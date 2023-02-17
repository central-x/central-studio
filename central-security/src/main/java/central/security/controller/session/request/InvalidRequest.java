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

package central.security.controller.session.request;

import central.security.controller.session.SessionController;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.support.session.SessionContainer;
import central.security.core.request.Request;
import central.validation.Label;
import central.validation.Validatex;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

/**
 * 清除指定帐户的所有会话
 *
 * @author Alan Yeh
 * @see SessionController#invalid
 * @since 2022/10/19
 */
public class InvalidRequest extends Request {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Params {
        @Label("帐户主键")
        @NotBlank
        @Size(min = 1, max = 32)
        private String accountId;
    }

    @Getter
    private final Params params;

    public InvalidRequest(HttpServletRequest request) {
        super(request);
        if (MediaType.APPLICATION_JSON.isCompatibleWith(this.getContentType())) {
            this.params = this.bindBody(Params.class);
        } else {
            this.params = this.bindParameter(Params.class);
        }

        Validatex.Default().validate(params, new Class[0], (message) -> new ResponseStatusException(HttpStatus.BAD_REQUEST, message));
    }

    public static InvalidRequest of(HttpServletRequest request) {
        return new InvalidRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new InvalidAction();
    }

    private static class InvalidAction extends SecurityAction implements InitializingBean {

        private SessionContainer session;

        @Override
        public void afterPropertiesSet() throws Exception {
            this.session = this.getBean(SessionContainer.class);
        }

        @Override
        public void execute(SecurityExchange exchange) {
            var request = (InvalidRequest) exchange.getRequest();

            this.session.clear(request.getTenantCode(), request.getParams().getAccountId());
        }
    }
}
