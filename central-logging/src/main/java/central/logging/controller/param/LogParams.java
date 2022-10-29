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

package central.logging.controller.param;

import central.data.log.Log;
import central.data.log.option.LogLevel;
import central.data.log.option.LogType;
import central.validation.Enums;
import central.validation.Label;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
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
public class LogParams implements Serializable {
    @Serial
    private static final long serialVersionUID = 1062452760964130234L;
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 基础属性
    @Label("类型")
    @NotNull
    @Enums(LogType.class)
    private String type;

    @Label("跟踪标识")
    private String traceId;

    @Label("等级")
    @NotNull
    @Enums(LogLevel.class)
    private String level;

    @Label("记录时间")
    @NotNull
    private Timestamp timestamp;

    @Label("内容")
    @Size(max = 5 * 1024 * 1024)
    private String content;

    @Label("应用主键")
    @Size(max = 32)
    private String applicationId;

    @Label("应用标识")
    @Size(max = 32)
    private String applicationCode;

    @Label("租户标识")
    @Size(max = 32)
    private String tenantCode;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 服务端属性
    /**
     * 微服务主机地址
     */
    @Label("租户标识")
    @Size(max = 32)
    private String serverHost;
    /**
     * 微服务端口
     */
    @Label("租户标识")
    @Size(max = 32)
    private String serverPort;
    /**
     * 微服务名({@code spring.application.name})
     */
    @Label("租户标识")
    @Size(max = 32)
    private String service;
    /**
     * 微服务版本号
     */
    @Label("租户标识")
    @Size(max = 32)
    private String version;
    /**
     * 线程名称
     */
    @Label("租户标识")
    @Size(max = 32)
    private String thread;
    /**
     * 进程标识
     */
    @Label("租户标识")
    @Size(max = 32)
    private String pid;
    /**
     * 日志灰
     */
    @Label("租户标识")
    @Size(max = 32)
    private String logger;
    /**
     * 日志产生位置
     */
    @Label("租户标识")
    @Size(max = 32)
    private String location;
    /**
     * 耗时
     */
    @Label("租户标识")
    @Size(max = 32)
    private Long duration;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 客户端属性

    @Label("请求来源")
    @Size(max = 32)
    private String referer;

    @Label("浏览器信息")
    @Size(max = 32)
    private String userAgent;

    @Label("客户端 IP")
    @Size(max = 32)
    private String remoteHost;

    @Label("访问方法")
    @Size(max = 32)
    private String method;

    @Label("访问地址")
    @Size(max = 32)
    private String url;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 用户行为属性

    @Label("模块名")
    @Size(max = 32)
    private String module;

    @Label("操作行为")
    @Size(max = 32)
    private String action;

    @Label("用户主键")
    @Size(max = 32)
    private String accountId;

    //（冗余保存）
    @Label("用户名")
    @Size(max = 32)
    private String username;

    //（冗余保存）
    @Label("用户姓名")
    @Size(max = 32)
    private String name;

    public Log toData() {
        var log = new Log();
        // 通用属性
        log.setType(this.getType());
        log.setTraceId(this.getTraceId());
        log.setLevel(this.getLevel());
        log.setTimestamp(this.getTimestamp());
        log.setContent(this.getContent());
        log.setApplicationId(this.getApplicationId());
        log.setApplicationCode(this.getApplicationCode());
        log.setTenantCode(this.getTenantCode());
        // 服务端属性
        log.setServerHost(this.getServerHost());
        log.setServerPort(this.getServerPort());
        log.setService(this.getService());
        log.setVersion(this.getVersion());
        log.setThread(this.getThread());
        log.setPid(this.getPid());
        log.setLogger(this.getLogger());
        log.setLocation(this.getLocation());
        log.setDuration(this.getDuration());
        // 客户端属性
        log.setReferer(this.getReferer());
        log.setUserAgent(this.getUserAgent());
        log.setRemoteHost(this.getRemoteHost());
        log.setMethod(this.getMethod());
        log.setUrl(this.getUrl());
        // 用户行为属性
        log.setModule(this.getModule());
        log.setAction(this.getAction());
        log.setAccountId(this.getAccountId());
        log.setUsername(this.getUsername());
        log.setName(this.getName());
        return log;
    }
}
