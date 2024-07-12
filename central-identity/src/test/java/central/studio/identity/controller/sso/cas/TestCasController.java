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

import central.data.identity.IdentityStrategyInput;
import central.lang.Stringx;
import central.provider.graphql.identity.IdentityStrategyProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.identity.IdentityContainer;
import central.studio.identity.IdentityApplication;
import central.studio.identity.controller.TestController;
import central.studio.identity.controller.index.IdentityIndexController;
import central.studio.identity.core.strategy.StrategyType;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Cas Controller Test Cases
 * <p>
 * CAS 协议测试
 *
 * @author Alan Yeh
 * @since 2024/07/12
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = IdentityApplication.class)
public class TestCasController extends TestController {

    /**
     * 新增 CAS 策略
     */
    @BeforeAll
    public static void beforeAll(@Autowired DataContext context, @Autowired IdentityStrategyProvider provider) throws InterruptedException {
        var strategy = IdentityStrategyInput.builder()
                .code("cas")
                .name("启用 CAS 协议")
                .type(StrategyType.CAS.getValue())
                .enabled(Boolean.TRUE)
                .remark("测试 CAS 协议")
                .params(Jsonx.Default().serialize(Mapx.of(
                        Mapx.entry("enabled", "1"),
                        Mapx.entry("scopes", "user:basic"),
                        Mapx.entry("singleLogout", "1")
                ))).build();

        provider.insert(strategy, "syssa", "master");

        var container = (IdentityContainer) context.getData(DataFetcherType.IDENTITY);
        while (container == null || Listx.isNullOrEmpty(container.getStrategies("master"))) {
            Thread.sleep(100);
            container = context.getData(DataFetcherType.IDENTITY);
        }
    }


    /**
     * 未登录时，重定向到 /identity/ 进行认证
     *
     * @see CasController#login
     * @see IdentityIndexController#login
     */
    @Test
    public void case0(@Autowired MockMvc mvc) throws Exception {
        var request = MockMvcRequestBuilders.get("/identity/sso/cas/login")
                .queryParam("service", "http://central-identity/identity/sso/cas/login-result")
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
                                                Stringx.encodeUrl(UriComponentsBuilder.fromUriString("https://identity.central-x.com/identity/sso/cas/login")
                                                        .queryParam("service", Stringx.encodeUrl("http://central-identity/identity/sso/cas/login-result"))
                                                        .build().toString())
                                        )
                                        .build().toString()
                        )
                );
    }
}
