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

package central.studio.gateway.core.filter.predicate.impl;

import central.studio.gateway.core.filter.predicate.Predicate;
import central.lang.Arrayx;
import central.lang.Stringx;
import central.pluglet.annotation.Control;
import central.util.Listx;
import central.validation.Label;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.server.ServerWebExchange;

import java.net.InetAddress;
import java.util.Collections;
import java.util.List;

/**
 * 远程主机断言
 *
 * @author Alan Yeh
 * @since 2022/11/08
 */
public class RemoteAddrPredicate implements Predicate, InitializingBean {

    @Label("白名单")
    @NotBlank
    @Control(label = "白名单", required = false, comment = "RemoteAddr 在指定的 IP 范围内，匹配成功。支持网段，如 192.168.1.1/24 表示允许 192.168.*.*。多个 IP 使用 ',' 分隔")
    private String whiteIps;

    @Label("黑名单")
    @NotBlank
    @Control(label = "黑名单", required = false, comment = "RemoteAddr 不在指定的 IP 范围内，匹配成功。支持网段，如 192.168.1.1/24 表示允许 192.168.*.*。多个 IP 使用 ',' 分隔")
    private String blackIps;

    private List<IpSubnetFilterRule> whiteRules = Collections.emptyList();

    private List<IpSubnetFilterRule> blackRules = Collections.emptyList();

    @Override
    public void afterPropertiesSet() throws Exception {
        if (Stringx.isNullOrBlank(whiteIps) && Stringx.isNullOrBlank(blackIps)) {
            throw new IllegalArgumentException("白名单与黑名单不能同时为空");
        }
        if (Stringx.isNotBlank(whiteIps) && Stringx.isNotBlank(blackIps)) {
            throw new IllegalArgumentException("白名单与黑名单不能同时不为空");
        }

        if (Stringx.isNotBlank(this.whiteIps)) {
            this.whiteRules = parseRules(this.whiteIps.split("[,]"));
        }
        if (Stringx.isNotBlank(this.blackIps)) {
            this.blackRules = parseRules(this.blackIps.split("[,]"));
        }
    }

    @Override
    public boolean predicate(ServerWebExchange exchange) {
        // 获取调用方主机
        var address = exchange.getRequest().getRemoteAddress();
        if (address == null) {
            // 检测不到 IP
            return true;
        }

        // 在白名单内
        if (!Listx.isNotEmpty(this.whiteRules)) {
            return this.whiteRules.stream().anyMatch(it -> it.matches(address));
        }

        // 在黑名单内
        if (!Listx.isNotEmpty(this.blackRules)) {
            return this.blackRules.stream().noneMatch(it -> it.matches(address));
        }

        return false;
    }

    private List<IpSubnetFilterRule> parseRules(String[] ips) {
        return Arrayx.asStream(ips).filter(Stringx::isNotBlank).map(it -> {
            // IP
            String address;
            // 网段
            int cidr;
            if (it.contains("/")) {
                // 解析 IP 和网段
                String[] parts = it.split("/", 2);

                address = parts[0];
                cidr = Integer.parseInt(parts[1]);
            } else {
                address = it;
                cidr = 32;
            }

            try {
                InetAddress inetAddress = InetAddress.getByName(address);
                return new IpSubnetFilterRule(inetAddress, cidr, IpFilterRuleType.ACCEPT);
            } catch (Exception ex) {
                throw new IllegalArgumentException(Stringx.format("'{}' 不是有效的 IP 格式", it));
            }
        }).toList();
    }
}
