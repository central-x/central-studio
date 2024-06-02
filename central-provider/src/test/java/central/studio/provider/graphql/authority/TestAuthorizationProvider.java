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

package central.studio.provider.graphql.authority;

import central.data.saas.Application;
import central.provider.graphql.authority.AuthorizationProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.studio.provider.ProviderApplication;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * AuthorizationProvider Test Cases
 * <p>
 * 授权
 *
 * @author Alan Yeh
 * @since 2024/05/27
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestAuthorizationProvider {

    @Setter(onMethod_ = @Autowired)
    private AuthorizationProvider provider;

    @Setter(onMethod_ = @Autowired)
    private DataContext context;

    @BeforeEach
    @AfterEach
    public void clear() throws InterruptedException {
        // 清空数据
        SaasContainer container = null;
        while (container == null || container.getApplications().isEmpty()) {
            Thread.sleep(100);
            container = context.getData(DataFetcherType.SAAS);
        }
    }

    /**
     * @see AuthorizationProvider#findApplication
     */
    @Test
    public void case1() {
        // 测试错误的 secret
        {
            Application application = null;
            Exception exception = null;
            try {
                application = provider.findApplication("central-identity", "wrong secret");
            } catch (Exception ex) {
                exception = ex;
            }
            assertNotNull(exception);
            assertNull(application);
        }

        // 测试正确的的 secret
        {
            Application application = null;
            Exception exception = null;
            try {
                application = provider.findApplication("central-identity", "AkJSi2kmH7vSO5lJcvY");
            } catch (Exception ex) {
                exception = ex;
            }
            assertNull(exception);
            assertNotNull(application);
        }
    }
}
