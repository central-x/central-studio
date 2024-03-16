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

package central.data.log;

import central.data.log.option.LogLevel;
import central.data.log.option.LogType;
import central.sql.data.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.sql.Timestamp;

/**
 * 日志
 *
 * @author Alan Yeh
 * @since 2022/10/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Log extends Entity {
    @Serial
    private static final long serialVersionUID = -2890117921874089866L;
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 通用属性
    /**
     * 日志类型
     *
     * @see LogType
     */
    private String type;
    /**
     * 跟踪标识
     */
    private String traceId;
    /**
     * 等级
     *
     * @see LogLevel
     */
    private String level;
    /**
     * 记录时间
     */
    private Timestamp timestamp;
    /**
     * 内容
     */
    private String content;
    /**
     * 应用主键
     */
    private String applicationId;
    /**
     * 应用标识
     */
    private String applicationCode;
    /**
     * 租户标识
     */
    private String tenantCode;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 服务端属性
    /**
     * 微服务主机地址
     */
    private String serverHost;
    /**
     * 微服务端口
     */
    private String serverPort;
    /**
     * 微服务名({@code spring.application.name})
     */
    private String service;
    /**
     * 微服务版本号
     */
    private String version;
    /**
     * 线程名称
     */
    private String thread;
    /**
     * 进程标识
     */
    private String pid;
    /**
     * 日志灰
     */
    private String logger;
    /**
     * 日志产生位置
     */
    private String location;
    /**
     * 耗时
     */
    private Long duration;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 客户端属性
    /**
     * 请求来源
     */
    private String referer;
    /**
     * 浏览器信息
     */
    private String userAgent;
    /**
     * 客户端 IP
     */
    private String remoteHost;
    /**
     * 访问方法
     */
    private String method;
    /**
     * 访问地址
     */
    private String url;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 用户行为属性
    /**
     * 模块名
     */
    private String module;
    /**
     * 操作行为
     */
    private String action;
    /**
     * 用户主键
     */
    private String accountId;
    /**
     * 用户名（冗余保存）
     */
    private String username;
    /**
     * 用户姓名（冗余保存）
     */
    private String name;
}
