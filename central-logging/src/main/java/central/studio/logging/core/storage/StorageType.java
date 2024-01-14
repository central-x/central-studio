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

package central.studio.logging.core.storage;

import central.bean.OptionalEnum;
import central.studio.logging.core.storage.impl.console.ConsoleStorage;
import central.studio.logging.core.storage.impl.file.FileStorage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 存储器
 *
 * @author Alan Yeh
 * @since 2022/10/25
 */
@Getter
@RequiredArgsConstructor
public enum StorageType implements OptionalEnum<String> {
    CONSOLE("控制台（Console）", "console", ConsoleStorage.class),
    FILE("文件（File）", "file", FileStorage.class);

    private final String name;
    private final String value;
    private final Class<? extends Storage> type;

    public static StorageType resolve(String value) {
        return OptionalEnum.resolve(StorageType.class, value);
    }
}
