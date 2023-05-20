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

package central.security.core.strategy;

import central.security.core.SecurityExchange;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Standard Security Strategy Chain
 * 标准安全认证链
 *
 * @author Alan Yeh
 * @since 2022/10/19
 */
@RequiredArgsConstructor
public class StandardStrategyChain implements StrategyChain {

    /**
     * 待执行策略下标
     */
    private final int index;

    /**
     * 策略列表
     */
    private final List<? extends Strategy> strategies;

    /**
     * 下一调用链
     */
    private StandardStrategyChain next() {
        return new StandardStrategyChain(this.index + 1, this.strategies);
    }

    public StandardStrategyChain(List<? extends Strategy> strategies) {
        this.strategies = strategies;
        this.index = 0;
    }

    @Override
    public void execute(SecurityExchange exchange) {
        if (this.index < strategies.size()) {
            var strategy = this.strategies.get(index);
            if (strategy.predicate(exchange)) {
                strategy.execute(exchange, next());
            } else {
                this.next().execute(exchange);
            }
        }
    }
}
