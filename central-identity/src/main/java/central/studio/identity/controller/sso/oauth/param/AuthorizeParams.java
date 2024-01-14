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

import central.studio.identity.controller.sso.oauth.support.GrantScope;
import central.validation.Fix;
import central.validation.Label;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * 认证入口参数
 *
 * @author Alan Yeh
 * @since 2023/04/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizeParams implements Serializable {
    @Serial
    private static final long serialVersionUID = -8503703869897835051L;

    /**
     * 授权类型
     * <p>
     * 此字段因定要求为 code
     */
    @Label("授权类型")
    @Fix("code")
    private String responseType;

    /**
     * 应用标识
     * <p>
     * 此值使用应用的标识[code]字段
     */
    @Label("应用标识")
    @NotBlank
    @Size(min = 1, max = 36)
    private String clientId;

    /**
     * 成功授权后的回调地址，必须是创建应用时的服务地址域名和端口下的地址。
     * <p>
     * 注意前端传递这个参数的时候，需要将 url 进行 URLEncode
     */
    @Label("回调地址")
    @NotBlank
    @Size(min = 1, max = 4096)
    private String redirectUri;

    /**
     * client 的状态值。用于第三方应用防止 CSRF 攻击，成功授权后回调时会原样带回
     * <p>
     * client 端需要检查用户与 state 参数状态的绑定
     */
    @Label("状态值")
    @NotBlank
    @Size(min = 1, max = 128)
    private String state;

    /**
     * 请求用户授权时，向用户显示的可进行授权的列表
     * <p>
     * 不传则默认只能获取用户主键信息
     */
    @Label("授权范围")
    private Set<GrantScope> scope;
}
