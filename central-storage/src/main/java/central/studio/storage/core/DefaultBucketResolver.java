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

package central.studio.storage.core;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Default Bucket Resolver
 * <p>
 * 默认存储桶解析器，解析内部支持的存储桶类型
 * <p>
 * 第三方扩展时，可以通过实现 BucketResolver 接口，来自定义存储桶类型
 *
 * @author Alan Yeh
 * @since 2024/04/11
 */
public class DefaultBucketResolver implements BucketResolver {
    @Override
    public @Nullable Class<? extends Bucket> resolve(@Nonnull String code) {
        BucketType type = BucketType.resolve(code);
        if (type == null) {
            return null;
        } else {
            return type.getType();
        }
    }
}
