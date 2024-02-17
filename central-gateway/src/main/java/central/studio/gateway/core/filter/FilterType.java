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

package central.studio.gateway.core.filter;

import central.bean.OptionalEnum;
import central.studio.gateway.core.filter.impl.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 过滤器类型
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
@Getter
@RequiredArgsConstructor
public enum FilterType implements OptionalEnum<String> {

    ADD_REQUEST_HEADER("添加请求头（Add Request Header）", "add_request_header", AddRequestHeaderFilter.class),
    SET_REQUEST_HEADER("设置请求头（Set Request Header）", "set_request_header", SetRequestHeaderFilter.class),
    REMOVE_REQUEST_HEADER("移除请求头（Remove Request Header）", "remove_request_header", RemoveRequestHeaderFilter.class),

    ADD_RESPONSE_HEADER("添加响应头（Add Response Header）", "add_response_header", AddResponseHeaderFilter.class),
    SET_RESPONSE_HEADER("设置响应头（Set Response Header）", "set_response_header", SetResponseHeaderFilter.class),
    REMOVE_RESPONSE_HEADER("移除响应头（Remove Response Header）", "remove_response_header", RemoveResponseHeaderFilter.class),

    ADD_REQUEST_PARAMETER("添回请求参数（Add Request Parameter）", "add_request_parameter", AddRequestParameterFilter.class),
    SET_REQUEST_PARAMETER("设置请求参数（Set Request Parameter）", "set_request_parameter", SetRequestParameterFilter.class),
    REMOVE_REQUEST_PARAMETER("移除请求参数（Remove Request Parameter）", "remove_request_parameter", RemoveRequestHeaderFilter.class),

    REWRITE_PATH("重写请求路径（Rewrite Path）", "rewrite_path", RewritePathFilter.class),
    PREFIX_PATH("添加请求路径前缀（Prefix Path）", "prefix_path", PrefixPathFilter.class),
    STRIP_PREFIX("移除请求路径前缀（Strip Prefix）", "strip_prefix", StripPrefixFilter.class),

    REQUEST_REFUSE("拒绝服务（Request Refuse）", "request_refuse", RequestRefuseFilter.class),
    REQUEST_DETAILS("请求详情（Request Details）", "request_details", RequestDetailsFilter.class),

    CROSS_ORIGIN("跨域（Cross Origin）", "cross_origin", CrossOriginFilter.class),
    TIMEOUT("设置超时时间（Timeout）", "timeout", TimeoutFilter.class);

    private final String name;
    private final String value;
    private final Class<? extends Filter> type;

    public static FilterType resolve(String value) {
        return OptionalEnum.resolve(FilterType.class, value);
    }
}
