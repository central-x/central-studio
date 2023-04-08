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

package central.security.controller.sso.cas.param;

import central.validation.Label;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * CAS 登录参数
 *
 * @author Alan Yeh
 * @since 2023/04/08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginParams implements Serializable {
    @Serial
    private static final long serialVersionUID = -766346555738481377L;

    /**
     * 在大多数情况下，此参数是待接入的应用的 URL 地址。根据 HTTP 协议标准 RFC3986，这个 URL 须要使用 URLEncoded 进行编码。
     * 如果此参数为空，则直接跳转到登录界面
     */
    @Label("服务")
    @NotBlank
    @Size(min = 1, max = 4096)
    private String service;

    /**
     * 如果此参数被设置为 true，CAS 的 SSO 将会失效，每次进入登录界面都需重新登录。
     * 此参数与 gateway 参数冲突，因此两个参数不能共存。如果两个参数都存在值，gateway 参数将被忽略。
     */
    private Boolean renew;

    /**
     * 如果此参数被设置为 true，CAS 会直接检测当前是否已经存在有会话，或者是否能通过非交互式（non-interactive）的方式建立会话，如果满足
     * 以上条件，CAS 会携带有效的 ticket 并重定向到 service 指定的 URL 地址。
     * <p>
     * 如果此参数被设置为 true，CAS 发现当前没有存在会话，也不能通过非交互式的方式建立会话，那么 CAS 会直接重定向到 service 指定的 URL（未携带 ticket）。
     * <p>
     * 如果 gateway 参数被设置为 true，但是 service 参数为空的话，CAS 将直接转到登录界面，并忽略此参数。
     * 此参数与 renew 参数冲突
     */
    private Boolean gateway;
}
