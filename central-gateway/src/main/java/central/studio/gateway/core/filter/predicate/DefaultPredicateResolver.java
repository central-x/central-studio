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

package central.studio.gateway.core.filter.predicate;

import central.pluglet.PlugletFactory;
import central.studio.gateway.core.filter.predicate.impl.PathPredicate;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Default Predicate Resolver
 * <p>
 * 默认路由断言类型解析器
 *
 * @author Alan Yeh
 * @since 2024/05/24
 */
@RequiredArgsConstructor
public class DefaultPredicateResolver implements PredicateResolver {

    public final PlugletFactory factory;

    @Nullable
    @Override
    public Predicate resolve(@NotNull String code, @NotNull Map<String, Object> params) {
        Class<? extends Predicate> type;
        if ("path".equalsIgnoreCase(code)) {
            type = PathPredicate.class;
        } else {
            var predicateType = PredicateType.resolve(code);
            if (predicateType == null) {
                return null;
            }
            type = predicateType.getType();
        }

        return this.instance(type, params);
    }

    protected Predicate instance(@Nonnull Class<? extends Predicate> type, @Nonnull Map<String, Object> params) {
        return this.factory.create(type, params);
    }

    @Override
    public void destroy(@NotNull Predicate predicate) {
        this.factory.destroy(predicate);
    }
}
