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

package central.studio.provider.graphql.authority.service;

import central.provider.graphql.DTO;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.studio.provider.graphql.saas.dto.ApplicationDTO;
import jakarta.annotation.Nullable;
import lombok.Setter;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

/**
 * Authorization Service
 * <p>
 * 授权服务
 *
 * @author Alan Yeh
 * @since 2024/06/19
 */
@Component
public class AuthorizationService {

    @Setter(onMethod_ = @Autowired)
    private DataContext context;

    /**
     * 根据应用标识和应用密钥获取应用信息
     *
     * @param code   应用标识
     * @param secret 应用密钥
     */
    public @Nullable ApplicationDTO findApplication(String code, String secret) {
        SaasContainer container = context.getData(DataFetcherType.SAAS);

        var application = container.getApplicationByCode(code);
        if (application == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MessageFormatter.format("应用[code={}]不存在", code).getMessage());
        }
        if (!Objects.equals(secret, application.getSecret())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MessageFormatter.format("应用[code={}]密钥错误", code).getMessage());
        }
        return DTO.wrap(application, ApplicationDTO.class);
    }
}
