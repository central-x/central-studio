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

package central.provider.test;

import central.data.saas.TenantInput;
import central.util.Guidx;
import central.validation.Validatex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Code Validation Test Cases
 *
 * @author Alan Yeh
 * @since 2022/09/26
 */
public class TestCodeValidation {
    @Test
    public void case1() {
        assertThrows(IllegalArgumentException.class, () -> {
            var input = TenantInput.builder()
                    .code("test!")
                    .name("测试租户")
                    .databaseId(Guidx.nextID())
                    .enabled(Boolean.TRUE)
                    .remark("测试")
                    .build();
            Validatex.Default().validate(input);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            var input = TenantInput.builder()
                    .code("_test")
                    .name("测试租户")
                    .databaseId(Guidx.nextID())
                    .enabled(Boolean.TRUE)
                    .remark("测试")
                    .build();
            Validatex.Default().validate(input);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            var input = TenantInput.builder()
                    .code("-test")
                    .name("测试租户")
                    .databaseId(Guidx.nextID())
                    .enabled(Boolean.TRUE)
                    .remark("测试")
                    .build();
            Validatex.Default().validate(input);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            var input = TenantInput.builder()
                    .code("test_")
                    .name("测试租户")
                    .databaseId(Guidx.nextID())
                    .enabled(Boolean.TRUE)
                    .remark("测试")
                    .build();
            Validatex.Default().validate(input);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            var input = TenantInput.builder()
                    .code("test-")
                    .name("测试租户")
                    .databaseId(Guidx.nextID())
                    .enabled(Boolean.TRUE)
                    .remark("测试")
                    .build();
            Validatex.Default().validate(input);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            var input = TenantInput.builder()
                    .code("te--st")
                    .name("测试租户")
                    .databaseId(Guidx.nextID())
                    .enabled(Boolean.TRUE)
                    .remark("测试")
                    .build();
            Validatex.Default().validate(input);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            var input = TenantInput.builder()
                    .code("te__st")
                    .name("测试租户")
                    .databaseId(Guidx.nextID())
                    .enabled(Boolean.TRUE)
                    .remark("测试")
                    .build();
            Validatex.Default().validate(input);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            var input = TenantInput.builder()
                    .code("te-_st")
                    .name("测试租户")
                    .databaseId(Guidx.nextID())
                    .enabled(Boolean.TRUE)
                    .remark("测试")
                    .build();
            Validatex.Default().validate(input);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            var input = TenantInput.builder()
                    .code("te_-st")
                    .name("测试租户")
                    .databaseId(Guidx.nextID())
                    .enabled(Boolean.TRUE)
                    .remark("测试")
                    .build();
            Validatex.Default().validate(input);
        });
    }
}
