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

package central.studio.identity.controller.sso.cas.param;

import central.studio.identity.controller.sso.cas.support.Format;
import central.validation.Enums;
import central.validation.Label;
import central.validation.Prefix;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 校验参数
 *
 * @author Alan Yeh
 * @since 2023/04/09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateParams implements Serializable {
    @Serial
    private static final long serialVersionUID = -5381566453116378086L;

    @Label("服务地址")
    @NotBlank
    @Size(min = 1, max = 4096)
    private String service;

    @Label("服务凭证")
    @NotBlank
    @Prefix("ST-")
    @Size(min = 1, max = 256)
    private String ticket;

//    /**
//     * Proxy Granting Ticket Url。代理回调 URL。
//     * (暂不支持)
//     */
//    private String pgtUrl;
//
//    /**
//     * 如果这个值被设置为 true，则 ST（Service Ticket）必须是由用户授权颁发的，而不能由已存在的统一会话颁发的。
//     * （暂不支持）
//     */
//    private Boolean renew;

    @Label("格式")
    @Enums(Format.class)
    private String format = "json";
}
