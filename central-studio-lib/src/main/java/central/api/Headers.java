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

package central.api;

/**
 * 常用请求头
 *
 * @author Alan Yeh
 * @since 2022/09/25
 */
public interface Headers {

    /**
     * 原始请求路径
     * 主要后端微服务使用
     */
    String ORIGIN_URI = "X-Central-OriginUri";

    /**
     * 请求位置
     * 包含请求协议、域名、端口、租户路径
     * 主要前端使用
     */
    String LOCATION = "X-Central-Location";

    /**
     * 访问凭证
     * 主要后端微服务使用
     */
    String TOKEN = "X-Central-Token";

    /**
     * 版本号
     * 用户
     */
    String VERSION = "X-Central-Version";
}
