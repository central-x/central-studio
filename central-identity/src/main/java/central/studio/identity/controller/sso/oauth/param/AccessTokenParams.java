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

package central.studio.identity.controller.sso.oauth.param;

import central.data.saas.Application;
import central.validation.Fix;
import central.validation.Label;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 获取访问凭证参数
 *
 * @author Alan Yeh
 * @since 2023/04/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenParams implements Serializable {
    @Serial
    private static final long serialVersionUID = 662071433992783738L;

    /**
     * 授权类型
     * <p>
     * 此值固定为 authorization_code
     */
    @Label("授权类型")
    @NotBlank
    @Fix("authorization_code")
    private String grantType;
    /**
     * @see Application#getCode
     */
    @Label("应用标识")
    @NotBlank
    @Size(min = 1, max = 50)
    private String clientId;
    /**
     * @see Application#getSecret
     */
    @Label("应用密钥")
    @NotBlank
    @Size(min = 1, max = 50)
    private String clientSecret;

    @Label("授权码")
    @NotBlank
    @Size(min = 1, max = 128)
    private String code;

    @Label("重定向地址")
    @NotBlank
    @Size(min = 1, max = 4096)
    private String redirectUri;
}
