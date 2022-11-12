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

package central.security.core.strategy.dynamic;

import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.security.core.SecurityExchange;
import central.security.core.attribute.ExchangeAttributes;
import central.security.core.strategy.Strategy;
import central.security.core.strategy.StrategyChain;
import central.validation.Label;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 密码策略
 *
 * @author Alan Yeh
 * @since 2022/11/06
 */
public class PasswordStrategy implements Strategy, InitializingBean {

    @Control(label = "说明", type = ControlType.LABEL, defaultValue = "　　本策略用于控制用户的密码规则。")
    private String label;

    @Label("最小长度")
    @Min(6)
    @Max(32)
    @Control(label = "最小长度", type = ControlType.NUMBER, defaultValue = "8", comment = "用于控制密码的最短位数")
    @Setter
    private Integer min;

    @Label("最大长度")
    @Min(6)
    @Max(32)
    @Control(label = "最大长度", type = ControlType.NUMBER, defaultValue = "16", comment = "用于控制密码的最大位数")
    @Setter
    private Integer max;

    @Label("大写字母")
    @Min(0)
    @Control(label = "大写字母", type = ControlType.NUMBER, defaultValue = "0", comment = "用于控制密码中大写字母最低出现的个数")
    @Setter
    private Integer uppercase;

    @Label("小写字母")
    @Min(0)
    @Control(label = "小写字母", type = ControlType.NUMBER, defaultValue = "0", comment = "用于控制密码中小写字母最低出现的个数")
    @Setter
    private Integer lowercase;

    @Label("数字")
    @Min(0)
    @Control(label = "数字", type = ControlType.NUMBER, defaultValue = "0", comment = "用于控制密码中数字最低出现的个数")
    @Setter
    private Integer number;

    @Label("字符")
    @Min(0)
    @Control(label = "字符", type = ControlType.NUMBER, defaultValue = "0", comment = "用于控制密码中字符最低出现的个数")
    @Setter
    private Integer symbol;

    @Label("字符范围")
    @Size(min = 1, max = 128)
    @NotBlank
    @Control(label = "字符范围", type = ControlType.NUMBER, defaultValue = "\\!\"#$%&'()*+,-./:;<>=?@[]_^`{}~|", comment = "用于控制密码中字符允许范围")
    @Setter
    private String symbols;

    private Set<Character> characters;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.characters = symbols.chars().mapToObj(it -> (char) it).collect(Collectors.toSet());
    }

    @Override
    public void execute(SecurityExchange exchange, StrategyChain chain) {
        exchange.setAttribute(ExchangeAttributes.Password.MIN, this.min);
        exchange.setAttribute(ExchangeAttributes.Password.MAX, this.max);
        exchange.setAttribute(ExchangeAttributes.Password.UPPERCASE, this.uppercase);
        exchange.setAttribute(ExchangeAttributes.Password.LOWERCASE, this.lowercase);
        exchange.setAttribute(ExchangeAttributes.Password.NUMBER, this.number);
        exchange.setAttribute(ExchangeAttributes.Password.SYMBOL, this.symbol);
        exchange.setAttribute(ExchangeAttributes.Password.SYMBOLS, this.characters);
        chain.execute(exchange);
    }
}
