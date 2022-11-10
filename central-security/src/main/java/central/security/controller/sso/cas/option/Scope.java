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

package central.security.controller.sso.cas.option;

import central.bean.OptionalEnum;
import central.data.organization.Account;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Function;

/**
 * 用户属性
 *
 * @author Alan Yeh
 * @since 2022/11/07
 */
@Getter
@RequiredArgsConstructor
public enum Scope implements OptionalEnum<String> {
    BASIC("用户基本信息（主键、用户名、姓名、头像）", "user:basic", List.of(
            new DataFetcher("id", Account::getId),
            new DataFetcher("username", Account::getUsername),
            new DataFetcher("name", Account::getName),
            new DataFetcher("avatar", Account::getAvatar)
    )),
    CONTRACT("联系方式（邮箱、手机号）", "user:contract", List.of(
            new DataFetcher("email", Account::getEmail),
            new DataFetcher("mobile", Account::getMobile)
    ));

    private final String name;
    private final String value;
    private final List<DataFetcher> fetchers;

    public static Scope resolve(String value) {
        return OptionalEnum.resolve(Scope.class, value);
    }

    public record DataFetcher(String field, Function<Account, Object> getter) {
    }
}
