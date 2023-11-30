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

package central.security.controller.sso.oauth;

import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.security.client.SessionVerifier;
import central.provider.graphql.organization.AccountProvider;
import central.provider.scheduled.ScheduledDataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.data.organization.Account;
import central.data.saas.Application;
import central.lang.Arrayx;
import central.lang.Stringx;
import central.security.Digestx;
import central.security.controller.sso.oauth.param.AccessTokenParams;
import central.security.controller.sso.oauth.param.AuthorizeParams;
import central.security.controller.sso.oauth.param.GrantParams;
import central.security.controller.sso.oauth.support.AuthorizationCode;
import central.security.controller.sso.oauth.support.AuthorizationTransaction;
import central.security.controller.sso.oauth.support.GrantScope;
import central.security.controller.sso.oauth.support.OAuthSession;
import central.security.core.attribute.OAuthAttributes;
import central.security.core.attribute.SessionAttributes;
import central.security.signer.KeyPair;
import central.starter.webmvc.render.TextRender;
import central.starter.webmvc.servlet.WebMvcRequest;
import central.starter.webmvc.servlet.WebMvcResponse;
import central.util.Guidx;
import central.util.Jsonx;
import central.util.Listx;
import central.util.Setx;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * OAuth2.0
 * <p>
 * 不支持 refresh_token，因为这个认证接口不是一直调用的，认证一次之后，应用系统与认证中心就基本没什么关系了，所以 refresh_token 没什么必要。
 *
 * @author Alan Yeh
 * @see <a href="https://oauth.net/2/">OAuth 2.0</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749">RFC6749</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1">Authorization Code Grant</a>
 * @since 2022/10/19
 */
@Controller
@RequestMapping("/sso/oauth2")
public class OAuthController {

    @Setter(onMethod_ = @Autowired)
    private SessionVerifier verifier;

    @Setter(onMethod_ = @Autowired)
    private ScheduledDataContext context;

    @Setter(onMethod_ = @Autowired)
    private OAuthSession session;

    @Setter(onMethod_ = @Autowired)
    private AccountProvider provider;

    @Setter(onMethod_ = @Autowired)
    private KeyPair keyPair;

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
     * OAuth 2.0 认证
     * <p>
     * 获取授权码（Authorization Code）
     * <p>
     * 完成认证之后，本接口会添加 code 参数和 state 参数重定向到 redirect_uri。
     * 业务系统在接收到这个 code 之后，需要在后台访问开放平台 /api/sso/oauth/token 获取会话凭证，通过会话凭证获取用户信息
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-10.5">Authorization Codes</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.1">Authorization Request</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2">Authorization Response</a>
     */
    @GetMapping("/authorize")
    public View authorize(@Validated AuthorizeParams params,
                          WebMvcRequest request, WebMvcResponse response) throws IOException {
        if (!request.getRequiredAttribute(OAuthAttributes.ENABLED)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "OAuth 2.0 认证服务已禁用");
        }


        SaasContainer container = this.context.getData(DataFetcherType.SAAS);
        var application = container.getApplicationByCode(params.getClientId());
        if (application == null) {
            // 此应用不是已登记的应用
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "应用标识[client_id]无效");
        }
        if (!application.getEnabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "应用标识[client_id]无效: 已禁用");
        }

        if (!params.getRedirectUri().toLowerCase().startsWith(application.getUrl() + application.getContextPath())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "回调地址[redirect_uri]与应用不符");
        }

        if (request.getRequiredAttribute(OAuthAttributes.AUTO_GRANTING)) {
            return this.autoGranting(params, request, response);
        } else {
            return this.granting(params, request, response);
        }
    }


    /**
     * 如果发现当前用户已登录，则直接重定向到业务系统，不需要用户手动确认
     */
    private View autoGranting(AuthorizeParams params, WebMvcRequest request, WebMvcResponse response) {
        // 获取会话信息
        var cookie = request.getRequiredAttribute(SessionAttributes.COOKIE);
        var session = cookie.get(request, response);

        if (!this.verifier.verify(session)) {
            // 如果找不到会话，则重定向到登录界面
            var loginUrl = UriComponentsBuilder.fromUri(request.getUri())
                    .replacePath(request.getTenantPath())
                    .path("/security/")
                    .replaceQuery(null)
                    .queryParam("redirect_uri", Stringx.encodeUrl(request.getUri().toString()))
                    .build().toString();

            return new RedirectView(loginUrl);
        } else {
            // 会话有效，则生成一次性授权码（Authorization Code）
            var code = AuthorizationCode.builder()
                    .expires(request.getRequiredAttribute(OAuthAttributes.AUTHORIZATION_CODE_TIMEOUT))
                    .code("OC-" + getSerial() + "-" + Guidx.nextID())
                    .clientId(params.getClientId())
                    .redirectUri(params.getRedirectUri())
                    .token(session)
                    .scope(params.getScope())
                    .build();

            // 保存一次性授权
            this.session.saveCode(request.getTenantCode(), code);

            var redirectUri = UriComponentsBuilder.fromUriString(params.getRedirectUri())
                    .queryParam("state", params.getState())
                    .queryParam("code", code.getCode())
                    .build().toString();

            return new RedirectView(redirectUri);
        }
    }

    /**
     * 用户手动授权
     * 无论用户是否已经登录了，都需要跳转到登录界面上，引导用户完成授权
     */
    private View granting(AuthorizeParams params, WebMvcRequest request, WebMvcResponse response) {
        var transCookie = request.getRequiredAttribute(OAuthAttributes.GRANTING_TRANS_COOKIE);
        var transId = transCookie.get(request, response);

        if (Stringx.isNullOrBlank(transId)) {
            // 如果没有授权事务，则开始新的授权事务
            var transaction = AuthorizationTransaction.builder()
                    .expires(request.getRequiredAttribute(OAuthAttributes.GRANTING_TRANS_TIMEOUT))
                    .id(Guidx.nextID())
                    .clientId(params.getClientId())
                    .scopes(params.getScope())
                    // 保存摘要，等完成授权后进行对比，保证没有人篡改过
                    .digest(Digestx.SHA256.digest(request.getUri().toString(), StandardCharsets.UTF_8))
                    // 标记为未授权
                    .granted(false)
                    .build();

            this.session.saveTransaction(request.getTenantCode(), transaction);
            // 通过 Cookie 跟踪事务
            transCookie.set(request, response, transaction.getId());

            // 用户完成授权之后，会重新重定向回本接口，就会走到 else 的逻辑了
            var loginUrl = UriComponentsBuilder.fromUri(request.getUri())
                    .replacePath(request.getTenantPath()).path("/security/")
                    .replaceQuery(null)
                    .queryParam("redirect_uri", Stringx.encodeUrl(request.getUri().toString()))
                    .build().toString();
            return new RedirectView(loginUrl);
        } else {
            // 取出事务，进行摘要对比，如果发现摘要不匹配，则说明有人篡改了参数
            var transaction = this.session.getAndRemoveTransaction(request.getTenantCode(), transId);
            // 事务是一次性的，因此删除 Cookie
            transCookie.remove(request, response);

            if (!Objects.equals(transaction.getDigest(), Digestx.SHA256.digest(request.getUri().toString(), StandardCharsets.UTF_8))) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "参数被篡改，请重新发起登录请求");
            }

            if (!transaction.isGranted()) {
                // 用户未授权，则直接跳回业务系统，不携带 code，代表用户拒绝授梳
                return new RedirectView(params.getRedirectUri());
            } else {
                // 用户已授权，则生成一次性授权码
                var code = AuthorizationCode.builder()
                        .expires(request.getRequiredAttribute(OAuthAttributes.AUTHORIZATION_CODE_TIMEOUT))
                        .code("OC-" + getSerial() + "-" + Guidx.nextID())
                        .clientId(params.getClientId())
                        .redirectUri(params.getRedirectUri())
                        .token(transaction.getSession())
                        .scope(Setx.asStream(transaction.getGrantedScope()).map(GrantScope::resolve).collect(Collectors.toSet()))
                        .build();

                this.session.saveCode(request.getTenantCode(), code);

                var redirectUri = UriComponentsBuilder.fromUriString(params.getRedirectUri())
                        .queryParam("state", params.getState())
                        .queryParam("code", code.getCode())
                        .build().toString();

                return new RedirectView(params.getRedirectUri());
            }
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetScopeVO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1099644431930255452L;

        @Builder.Default
        private Account account = new Account();

        @Builder.Default
        private Application application = new Application();

        @Builder.Default
        private List<Scope> scopes = new ArrayList<>();
    }

    /**
     * 待授权项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Scope implements Serializable {
        @Serial
        private static final long serialVersionUID = -2194870315185874029L;

        /**
         * 名称
         */
        private String name;
        /**
         * 值
         */
        private String value;
        /**
         * 是否默认选中
         */
        private boolean checked;
        /**
         * 是否必要授权（必要授权不可取消）
         */
        private boolean required;
    }

    /**
     * 获取待授权范围列表
     */
    @GetMapping("/scopes")
    @ResponseBody
    public GetScopeVO getScopes(WebMvcRequest request, WebMvcResponse response) throws IOException {
        var sessionCookie = request.getRequiredAttribute(SessionAttributes.COOKIE);
        var session = sessionCookie.get(request, response);
        if (!verifier.verify(session)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
        }

        var sessionJwt = JWT.decode(session);

        // 查找待授权事务
        var transCookie = request.getRequiredAttribute(OAuthAttributes.GRANTING_TRANS_COOKIE);
        var transId = transCookie.get(request, response);

        if (Stringx.isNullOrEmpty(transId)) {
            // 没有找到待授权事务
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "找不到待授权事务");
        }

        var transaction = this.session.getAndRemoveTransaction(request.getTenantCode(), transId);
        if (transaction == null) {
            // 事务过期了
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "找不到待授权事务");
        }

        // 组装授权信息
        var account = this.provider.findById(sessionJwt.getSubject());
        SaasContainer container = this.context.getData(DataFetcherType.SAAS);
        var application = container.getApplicationByCode(transaction.getClientId());
        var scopes = transaction.getScopes();

        var vo = new GetScopeVO();
        vo.getAccount().setId(account.getId());
        vo.getAccount().setUsername(account.getUsername());
        vo.getAccount().setName(account.getName());
        vo.getAccount().setAvatar(account.getAvatar());

        vo.getApplication().setCode(application.getCode());
        vo.getApplication().setName(application.getName());
        vo.getApplication().setLogo(application.getLogo());

        vo.getScopes().addAll(scopes.stream().map(it -> new Scope(it.getName(), it.getValue(), true, it == GrantScope.BASIC)).toList());

        return vo;
    }

    /**
     * 授权
     */
    @PostMapping("/scopes")
    @ResponseBody
    public Map<String, String> grant(@RequestBody @Validated GrantParams params,
                                     WebMvcRequest request, WebMvcResponse response) throws IOException {
        // 验证会话
        var sessionCookie = request.getRequiredAttribute(SessionAttributes.COOKIE);
        var session = sessionCookie.get(request, response);
        if (!verifier.verify(session)) {
            // 一般情况下不会出现这种情况，因为只有登录之后才有授权界面，除非是直接调接口
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
        }

        // 查找授权事务
        var transCookie = request.getRequiredAttribute(OAuthAttributes.GRANTING_TRANS_COOKIE);
        var transId = transCookie.get(request, response);

        if (Stringx.isNullOrBlank(transId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "授权错误: 找不到待授权请求");
        }

        var transaction = this.session.getAndRemoveTransaction(request.getTenantCode(), transId);
        if (transaction == null) {
            // 过期了
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "授权错误: 找不到待授权请求");
        }
        // 开发者申请的范围
        var requestedScopes = transaction.getScopes();
        // 用户授权的范围
        var grantedScopes = params.getScope().stream().map(GrantScope::resolve).collect(Collectors.toSet());

        if (grantedScopes.size() > requestedScopes.size() || !requestedScopes.containsAll(grantedScopes)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "授权错误: 用户授权超出范围");
        }

        // 更新事务
        transaction.setExpires(request.getRequiredAttribute(OAuthAttributes.GRANTING_TRANS_TIMEOUT));
        transaction.setGranted(true);
        transaction.setGrantedScope(params.getScope());
        transaction.setSession(session);

        this.session.saveTransaction(request.getTenantCode(), transaction);

        return Map.of(
                "message", "授权成功"
        );
    }

    /**
     * 获取访问凭证（Access Token）
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-10.3">Access Tokens</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.3">Access Token Request</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.4">Access Token Response</a>
     */
    @PostMapping("/access_token")
    public void getAccessToken(@RequestBody @Validated AccessTokenParams params,
                               WebMvcRequest request, WebMvcResponse response) throws IOException {
        if (!request.getRequiredAttribute(OAuthAttributes.ENABLED)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "OAuth 2.0 认证服务已禁用");
        }

        var code = session.getCode(request.getTenantCode(), params.getCode());
        if (code == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "授权码[code]已过期");
        }

        if (!Objects.equals(code.getRedirectUri(), params.getRedirectUri())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "授权码[code]与重定向地址[redirect_uri]不匹配，请确保重定向地址[redirect_uri]与申请授权码[code]时使用的是相同值");
        }

        if (!Objects.equals(code.getClientId(), params.getClientId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "授权码[code]与应用标识[client_id]不符");
        }

        SaasContainer container = this.context.getData(DataFetcherType.SAAS);
        var application = container.getApplicationByCode(code.getClientId());
        if (application == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "应用标识[client_id]无效");
        }
        if (!application.getEnabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "应用标识[client_id]已禁用");
        }
        if (!Objects.equals(application.getSecret(), params.getClientSecret())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "应用密钥[client_secret]错误");
        }

        var scopes = code.getScope();
        scopes.add(GrantScope.BASIC);

        // 颁发 access_token
        // 使用私钥签名，这样客户端那边就没办法伪造，我们也不需要保存这个凭证的信息，日期过了就失效了
        var token = JWT.create()
                // 指定本 JWT 为 access_token 类型
                .withHeader(Map.of("typ", "access_token"))
                .withJWTId(Guidx.nextID())
                .withSubject(code.getSession().getAccountId())
                .withIssuer(request.getRequiredAttribute(SessionAttributes.ISSUER))
                // 被授权的应用
                .withAudience(application.getCode())
                // 限定范围
                .withArrayClaim("scope", scopes.stream().map(GrantScope::getValue).toArray(String[]::new))
                // 指定过期时间
                .withExpiresAt(new Date(System.currentTimeMillis() + request.getRequiredAttribute(OAuthAttributes.ACCESS_TOKEN_TIMEOUT).toMillis()))
                .sign(Algorithm.RSA256((RSAPublicKey) keyPair.getVerifyKey(), (RSAPrivateKey) keyPair.getSignKey()));

        // 返回响应
        var body = Map.of(
                "access_token", token,
                "token_type", "bearer",
                "expires_in", request.getRequiredAttribute(OAuthAttributes.ACCESS_TOKEN_TIMEOUT).toSeconds(),
                "account_id", code.getSession().getAccountId(),
                "username", code.getSession().getUsername(),
                "scope", String.join(",", scopes.stream().map(GrantScope::getValue).toList())
        );

        if (request.isAcceptContentType(MediaType.APPLICATION_JSON)) {
            new TextRender(request, response)
                    .setStatus(HttpStatus.OK)
                    .setContentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                    .render(Jsonx.Default().serialize(body));
        } else if (request.isAcceptContentType(MediaType.APPLICATION_XML)) {
            var content = new StringBuilder("<OAuth>");
            for (var item : body.entrySet()) {
                content.append("<").append(item.getKey()).append("?").append(item.getValue()).append("</").append(item.getKey()).append(">");
            }
            content.append("</OAuth>");

            new TextRender(request, response)
                    .setStatus(HttpStatus.OK)
                    .setContentType(new MediaType(MediaType.APPLICATION_XML, StandardCharsets.UTF_8))
                    .render(content.toString());
        } else if (request.isAcceptContentType(MediaType.APPLICATION_FORM_URLENCODED)) {
            var content = body.entrySet().stream().map(it -> it.getKey() + "=" + it.getValue()).collect(Collectors.joining("&"));

            new TextRender(request, response)
                    .setStatus(HttpStatus.OK)
                    .setContentType(new MediaType(MediaType.APPLICATION_FORM_URLENCODED, StandardCharsets.UTF_8))
                    .render(content);
        } else {
            new TextRender(request, response)
                    .setStatus(HttpStatus.OK)
                    .setContentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                    .render(Jsonx.Default().serialize(body));
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/user")
    public Map<String, Object> getUser(WebMvcRequest request) {
        // 获取 access_token
        var token = this.validate(request);

        var scopes = Arrayx.asStream(token.getClaim("scope").asArray(String.class))
                .map(GrantScope::resolve)
                .toList();

        var account = this.provider.findById(token.getSubject());
        if (account == null) {
            // 一般情况下不会报这个异常，以防万一吧
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Stringx.format("User '{}' not found", token.getSubject()));
        }

        var result = new HashMap<String, Object>();
        for (var scope : scopes) {
            for (var fetcher : scope.getFetchers()) {
                result.put(fetcher.field(), fetcher.getter().apply(account));
            }
        }

        return result;
    }

    private DecodedJWT validate(WebMvcRequest request) {
        var token = request.getHeader("Authorization");
        if (Stringx.isNullOrBlank(token)) {
            // 没有找到请求头
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing required header 'Authorization'");
        }
        if (token.toLowerCase().startsWith("bearer ")) {
            token = token.substring("Bearer ".length());
        }
        DecodedJWT accessToken;
        try {
            accessToken = JWT.decode(token);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid OAuth access token: Bad token");
        }

        if (!Objects.equals("access_token", accessToken.getType())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid OAuth access token: Bad token");
        }

        if (accessToken.getExpiresAt() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid OAuth access token: Missing required claim 'exp'");
        }

        try {
            // 使用公钥验证
            JWT.require(Algorithm.RSA256((RSAPublicKey) keyPair.getVerifyKey(), (RSAPrivateKey) keyPair.getSignKey()))
                    .build()
                    .verify(accessToken);
        } catch (TokenExpiredException ex) {
            // 过期了
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid OAuth access token: Bad token");
        } catch (SignatureVerificationException ex) {
            // 签名无效
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid OAuth access token: Invalid signature");
        } catch (AlgorithmMismatchException ex) {
            // 签名算法不匹配
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid OAuth access token: Algorithm mismatch");
        } catch (Exception ex) {
            // 其它异常
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid OAuth access token: " + ex.getLocalizedMessage());
        }

        SaasContainer container = this.context.getData(DataFetcherType.SAAS);
        var application = container.getApplicationByCode(Listx.getFirstOrNull(accessToken.getAudience()));
        if (application == null || !application.getEnabled()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid OAuth access token: Invalid client '" + Listx.getFirstOrNull(accessToken.getAudience()) + "'");
        }

        return accessToken;
    }
}
