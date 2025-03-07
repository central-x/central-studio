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

package central.studio.dashboard.logic.identity;

import central.data.identity.IdentityPassword;
import central.data.identity.IdentityPasswordInput;
import central.lang.Stringx;
import central.provider.graphql.identity.IdentityPasswordProvider;
import central.provider.graphql.organization.AccountProvider;
import central.security.Passwordx;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.util.Listx;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/// Identity Password Logic
///
/// 密码逻辑
///
/// @author Alan Yeh
@Service
public class PasswordLogic {

    @Setter(onMethod_ = @Autowired)
    private IdentityPasswordProvider provider;

    @Setter(onMethod_ = @Autowired)
    private AccountProvider accountProvider;

    /// 检测密码
    ///
    /// @param accountId 帐户主键
    /// @param password  密码
    /// @return 校验是否通过
    public boolean checkPassword(String accountId, String password) {
        var account = this.accountProvider.findById(accountId);
        if (account == null) {
            return false;
        }

        return true;
    }

    /// 更新密码
    ///
    /// @param accountId   帐户主键
    /// @param oldPassword 原密码
    /// @param newPassword 新密码
    /// @param retention   保留最多密码数量
    public boolean updatePassword(String accountId, String oldPassword, String newPassword, Long retention) {
        var account = this.accountProvider.findById(accountId);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("帐户[id={}]不存在", accountId));
        }
        if (Boolean.TRUE.equals(account.getSupervisor())) {
            // 超级管理员不允许修改密码
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "超级管理员禁止修改密码");
        }

        // 校验密码
        var passwords = this.provider.findBy(1L, 0L, Conditions.of(IdentityPassword.class).eq(IdentityPassword::getAccountId, accountId).eq(IdentityPassword::getEnabled, Boolean.TRUE), null);
        var password = Listx.getFirstOrNull(passwords);

        if (password == null) {
            // 当前用户没有设置初始密码，直接设置新密码
            var input = IdentityPasswordInput.builder()
                    .accountId(accountId)
                    .value(newPassword)
                    .build();
            this.provider.insert(input, accountId);
        } else {
            // 校验密码
            if (!Passwordx.verify(oldPassword, password.getValue())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("用户[id={}]密码错误", accountId));
            }

            if (retention != null && retention > 0) {
                // 校验新密码不能与旧密码重复
                passwords = this.provider.findBy(retention, 0L, Conditions.of(IdentityPassword.class).eq(IdentityPassword::getAccountId, accountId).eq(IdentityPassword::getEnabled, Boolean.FALSE), Orders.of(IdentityPassword.class).desc(IdentityPassword::getCreateDate));
                for (var it : passwords) {
                    if (Passwordx.verify(newPassword, it.getValue())) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("新密码不能与旧密码重复"));
                    }
                }
            }

            // 校验通过，插入新密码
            var input = IdentityPasswordInput.builder()
                    .accountId(accountId)
                    .value(newPassword)
                    .build();
            this.provider.insert(input, accountId);

            // 删除最近 n 条密码
            this.provider.findBy(null, retention, Conditions.of(IdentityPassword.class).eq(IdentityPassword::getAccountId, accountId), Orders.of(IdentityPassword.class).desc(IdentityPassword::getCreateDate));

        }

        return true;
    }
}
