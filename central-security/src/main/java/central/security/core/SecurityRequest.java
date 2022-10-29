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

package central.security.core;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.util.List;

/**
 * Security Request
 * 安全相关请求
 *
 * @author Alan Yeh
 * @since 2022/10/19
 */
public interface SecurityRequest {
    /**
     * Return servlet request
     * 获取 Servlet 请求
     */
    @Nonnull
    HttpServletRequest getRequest();

    /**
     * 获取当前请求地址
     */
    @Nonnull
    URI getUri();

    /**
     * 获取请求头
     */
    @Nullable
    String getHeader(String name);

    /**
     * 获取 Content-Type
     */
    @Nullable
    MediaType getContentType();

    /**
     * 是否支持指定响应体
     *
     * @param contentType 响应体类型
     */
    boolean isAcceptContentType(MediaType contentType);

    /**
     * 获取租户标识
     */
    String getTenantCode();

    /**
     * 获取租户路径
     */
    String getTenantPath();

    /**
     * 获取 Cookie 值
     */
    String getCookie(String name);

    /**
     * 获取参数
     *
     * @param name 参数名
     */
    String getParameter(String name);

    /**
     * 获取参数
     *
     * @param name         参数名
     * @param defaultValue 如果参数为空时的默认值
     */
    String getParameterOrDefault(String name, String defaultValue);

    /**
     * 获取参数数组
     *
     * @param name 参数名
     */
    List<String> getParameters(String name);

    /**
     * 解析参数
     * <p>
     * 此方法可以从 query，或者从 contentType 为 application/x-www-form-urlencoded 类型的请求体里面解析参数
     */
    <T> T getParameter(Class<T> type);

    /**
     * 获取请求体
     * <p>
     * 此方法主要用于从 contentType 为 application/json 类型的请求体里面解析参数
     */
    <T> T getBody(Class<T> type);

    /**
     * 获取安全执行行为
     */
    SecurityAction getAction();
}
