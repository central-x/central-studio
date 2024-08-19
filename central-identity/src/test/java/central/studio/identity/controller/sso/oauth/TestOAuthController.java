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

package central.studio.identity.controller.sso.oauth;

import central.data.identity.IdentityStrategy;
import central.lang.BooleanEnum;
import central.lang.Stringx;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.studio.identity.IdentityApplication;
import central.studio.identity.controller.TestController;
import central.studio.identity.controller.index.IdentityIndexController;
import central.studio.identity.core.strategy.DynamicStrategyFilter;
import central.studio.identity.core.strategy.StrategyContainer;
import central.studio.identity.core.strategy.StrategyResolver;
import central.studio.identity.core.strategy.StrategyType;
import central.studio.identity.core.strategy.dynamic.OAuthStrategyFilter;
import central.util.Guidx;
import central.util.Jsonx;
import central.util.Listx;
import central.util.Mapx;
import central.web.XForwardedHeaders;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * OAuth Controller Test Cases
 * <p>
 * OAuth 协议测试
 *
 * @author Alan Yeh
 * @since 2024/08/06
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = IdentityApplication.class)
public class TestOAuthController extends TestController {

    /**
     * 设置 OAuth 2.0 策略
     */
    @BeforeAll
    public static void setup(@Autowired StrategyContainer container, @Autowired StrategyResolver resolver, @Autowired DataContext context) throws Exception {
        {
            SaasContainer dataContainer = null;
            while (dataContainer == null || Listx.isNullOrEmpty(dataContainer.getApplications())) {
                Thread.sleep(100);
                dataContainer = context.getData(DataFetcherType.SAAS);
            }
        }

        IdentityStrategy strategy = new IdentityStrategy();
        strategy.setId(Guidx.nextID());
        strategy.setCode("oauth");
        strategy.setName("启用 OAuth 协议");
        strategy.setType(StrategyType.OAUTH.getValue());
        strategy.setEnabled(true);
        strategy.setRemark("测试 OAuth 协议");
        strategy.setParams(Jsonx.Default().serialize(Mapx.of(
                Mapx.entry("enabled", "1"),
                Mapx.entry("scopes", "user:basic"),
                Mapx.entry("authGranting", "1"),
                Mapx.entry("timeout", "180000")
        )));
        strategy.updateCreator("syssa");

        container.putStrategy("master", new DynamicStrategyFilter(strategy, resolver));
    }

    /**
     * 获取 OAuth 策略
     */
    private OAuthStrategyFilter getStrategyFilter(StrategyContainer container) {
        var dynamic = container.getStrategy("master", "oauth");
        assertNotNull(dynamic);

        assertInstanceOf(OAuthStrategyFilter.class, dynamic.getDelegate());
        return (OAuthStrategyFilter) dynamic.getDelegate();
    }

    /**
     * 测试禁用 OAuth 协议
     */
    @Test
    public void case0(@Autowired MockMvc mvc, @Autowired StrategyContainer container) throws Exception {
        var strategy = this.getStrategyFilter(container);

        // 禁用策略
        strategy.setEnabled(BooleanEnum.FALSE);

        var request = MockMvcRequestBuilders.get("/identity/sso/oauth2/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", "example")
                .queryParam("redirect_uri", "https://example.com/identity/sso/oauth2/login-result")
                .queryParam("state", UUID.randomUUID().toString())
                .header(XForwardedHeaders.TENANT, "master")
                .with(req -> {
                    req.setScheme("https");
                    req.setServerName("identity.central-x.com");
                    req.setServerPort(443);
                    return req;
                });

        mvc.perform(request)
                .andExpect(status().is(HttpStatus.SERVICE_UNAVAILABLE.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));

        // 恢复策略
        strategy.setEnabled(BooleanEnum.TRUE);
    }

    /**
     * 未登记的应用不允许接入 OAuth 功能
     *
     * @see OAuthController#authorize 
     */
    @Test
    public void case1(@Autowired MockMvc mvc) throws Exception {
        var request = MockMvcRequestBuilders.get("/identity/sso/oauth2/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", "example")
                .queryParam("redirect_uri", "https://example.com/identity/sso/oauth2/login-result")
                .queryParam("state", UUID.randomUUID().toString())
                .header(XForwardedHeaders.TENANT, "master")
                .with(req -> {
                    req.setScheme("https");
                    req.setServerName("identity.central-x.com");
                    req.setServerPort(443);
                    return req;
                });

        mvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }

    /**
     * 未登录时，重定向到 /identity/ 进行认证
     *
     * @see OAuthController#authorize
     * @see IdentityIndexController#login
     */
    @Test
    public void case2(@Autowired MockMvc mvc) throws Exception {
        var state = UUID.randomUUID().toString();
        var request = MockMvcRequestBuilders.get("/identity/sso/oauth2/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", "central-identity")
                .queryParam("redirect_uri", "http://central-identity/identity/sso/oauth2/login-result")
                .queryParam("state", state)
                .header(XForwardedHeaders.TENANT, "master")
                .with(req -> {
                    req.setScheme("https");
                    req.setServerName("identity.central-x.com");
                    req.setServerPort(443);
                    return req;
                });
        mvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.LOCATION,
                                UriComponentsBuilder.fromUriString("/identity/")
                                        .scheme("https")
                                        .host("identity.central-x.com")
                                        .queryParam("redirect_uri",
                                                Stringx.encodeUrl(UriComponentsBuilder.fromUriString("https://identity.central-x.com/identity/sso/oauth2/authorize")
                                                        .queryParam("response_type", "code")
                                                        .queryParam("client_id", "central-identity")
                                                        .queryParam("redirect_uri", Stringx.encodeUrl("http://central-identity/identity/sso/oauth2/login-result"))
                                                        .queryParam("state", state)
                                                        .build().toString())
                                        )
                                        .build().toString()
                        )
                );
    }
}
