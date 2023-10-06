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

package central.dashboard;

import central.starter.security.SecurityProvider;
import lombok.Data;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.springframework.stereotype.Component;

/**
 * Application Security Provider
 *
 * 应用安全认证
 *
 * @author Alan Yeh
 * @since 2023/05/12
 */
@Data
@Component
public class ApplicationSecurityProvider implements SecurityProvider {
    @Override
    public void onReceiveAuthenticationToken(String token) {
        
    }

    @Override
    public void onReceiveAuthorizationInfo(String token, SimpleAuthorizationInfo authorizationInfo) {
    }

    @Override
    public void onLogout(String token) {
    }
}
