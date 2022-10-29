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

import central.security.controller.index.IndexController;
import central.security.core.SecurityAction;
import central.security.core.SecurityExchange;
import central.security.core.request.Request;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 获取验证码
 *
 * @author Alan Yeh
 * @see IndexController#getCaptcha
 * @since 2022/10/19
 */
public class GetCaptchaRequest extends Request {
    public GetCaptchaRequest(HttpServletRequest request) {
        super(request);
    }

    public static GetCaptchaRequest of(HttpServletRequest request) {
        return new GetCaptchaRequest(request);
    }

    @Override
    public SecurityAction getAction() {
        return new Action();
    }

    private static class Action extends SecurityAction {

        @Override
        public void execute(SecurityExchange exchange) {

        }
    }
}
