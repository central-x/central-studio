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

package central.security.controller.index;

import central.api.client.security.SessionClient;
import central.api.client.security.SessionVerifier;
import central.api.provider.organization.AccountProvider;
import central.data.organization.Account;
import central.lang.Stringx;
import central.security.controller.index.param.IndexParams;
import central.security.controller.index.param.LoginParams;
import central.security.controller.index.support.LoginOptions;
import central.security.core.attribute.CaptchaAttributes;
import central.security.core.attribute.SessionAttributes;
import central.security.core.captcha.CaptchaManager;
import central.starter.webmvc.servlet.WebMvcRequest;
import central.starter.webmvc.servlet.WebMvcResponse;
import com.auth0.jwt.JWT;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页
 *
 * @author Alan Yeh
 * @since 2022/10/14
 */
@Controller
public class IndexController {

    @Setter(onMethod_ = @Autowired)
    private SessionVerifier verifier;

    @Setter(onMethod_ = @Autowired)
    private AccountProvider provider;

    @Setter(onMethod_ = @Autowired)
    private SessionClient client;

    @Setter(onMethod_ = @Autowired)
    private CaptchaManager captchaManager;

    /**
     * 获取首页
     */
    @GetMapping("/")
    public View index(@Validated IndexParams params,
                      WebMvcRequest request) {

        if (Stringx.isNotBlank(params.getRedirectUri())) {
            // 检测 redirectUrl 是否跨域
            // 统一认证不允许进行跨域验证，只能重定向到本域的地址
            if (!params.getRedirectUri().startsWith(request.getScheme() + "://" + request.getServerName())) {
                // 如果出现跨域，直接重定向到统一认证自己的界面
                var location = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString()).replaceQuery("").build().toString();

                return new RedirectView(location);
            }

            // 检测会话有效性
            // 如果当前会话有效，则直接重定向到指定的地址
            var cookie = request.getRequiredAttribute(SessionAttributes.COOKIE);
            var token = cookie.get(request);

            if (verifier.verify(token)) {
                return new RedirectView(params.getRedirectUri());
            }
        }

        // 其余情况，返回登录界面
        return new InternalResourceView("index.html");
    }

    /**
     * 获取当前已登录的用户信息
     */
    @GetMapping("/api/account")
    @ResponseBody
    public Account getAccount(WebMvcRequest request) {
        // 检测会话有效性
        var cookie = request.getRequiredAttribute(SessionAttributes.COOKIE);
        var token = cookie.get(request);

        if (Stringx.isNotBlank(token)) {
            if (!verifier.verify(token)) {
                token = null;
            }
        }

        if (token == null) {
            // 未登录或会话无效
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
        }

        var decodedJwt = JWT.decode(token);

        var account = provider.findById(decodedJwt.getSubject());
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Stringx.format("用户[id={}]不存在", decodedJwt.getSubject()));
        }

        return account;
    }

    /**
     * 获取登录选项
     */
    @GetMapping("/api/options")
    @ResponseBody
    public HashMap<String, Map<String, Object>> getOptions(WebMvcRequest request) {
        var result = new HashMap<String, Map<String, Object>>();

        for (var option : LoginOptions.values()) {
            var parts = option.getName().split("[.]");
            result.computeIfAbsent(parts[0], key -> new HashMap<>())
                    .put(parts[1], option.getValue().apply(request));
        }

        return result;
    }

    /**
     * 获取验证码
     */
    @GetMapping("/api/captcha")
    public View getCaptcha(WebMvcRequest request, WebMvcResponse response) {
        var generator = request.getRequiredAttribute(CaptchaAttributes.GENERATOR);
        var captcha = this.captchaManager.generate(request.getTenantCode(), generator);

        // 将验证码标识写入 Cookie
        var cookie = request.getRequiredAttribute(CaptchaAttributes.COOKIE);
        cookie.set(request, response, captcha.getCode());

        // 返回 Cookie 视图
        return captcha.getView();
    }

    /**
     * 登录
     */
    @PostMapping("/api/login")
    @ResponseBody
    public boolean login(@Validated @RequestBody LoginParams params,
                         WebMvcRequest request, WebMvcResponse response) {
        if (request.getRequiredAttribute(CaptchaAttributes.ENABLED)) {
            // 获取验证码标识
            var cookie = request.getRequiredAttribute(CaptchaAttributes.COOKIE);
            var code = cookie.get(request);

            // 验证
            this.captchaManager.verify(request.getTenantCode(), code, params.getCaptcha(), request.getRequiredAttribute(CaptchaAttributes.CASE_SENSITIVE));
        }

        try {
            var session = this.client.login(params.getAccount(), params.getPassword(), params.getSecret(), null);

            // 把会话放到 Cookie 里
            var cookie = request.getRequiredAttribute(SessionAttributes.COOKIE);
            cookie.set(request, response, session);

            return true;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "帐号或密码错误");
        }
    }

    /**
     * 退出登录
     */
    @GetMapping("/api/logout")
    @ResponseBody
    public boolean logout(WebMvcRequest request, WebMvcResponse response) {
        // 把会话放到 Cookie 里
        var cookie = request.getRequiredAttribute(SessionAttributes.COOKIE);
        var token = cookie.get(request, response);
        if (Stringx.isNotBlank(token)) {
            this.verifier.invalid(token);
        }

        // 移除 Cookie
        cookie.remove(request, response);

        return true;
    }
}
