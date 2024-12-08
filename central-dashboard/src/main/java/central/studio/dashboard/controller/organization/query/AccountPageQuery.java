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

package central.studio.dashboard.controller.organization.query;

import central.data.organization.Account;
import central.lang.Stringx;
import central.sql.query.Conditions;
import central.starter.web.query.PageQuery;
import central.validation.Label;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * Account Page Query
 * <p>
 * 帐号分页查询
 *
 * @author Alan Yeh
 * @since 2024/12/08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountPageQuery extends PageQuery<Account> {
    @Serial
    private static final long serialVersionUID = 8277108859843115333L;

    @Label("主键")
    private String id;

    @Label("用户名")
    private String username;

    @Label("邮箱")
    private String email;

    @Label("手机号")
    private String mobile;

    @Label("姓名")
    private String name;

    @Override
    public Conditions<Account> build() {
        var conditions = Conditions.of(Account.class);

        // 精确字段搜索
        if (Stringx.isNotEmpty(this.getId())) {
            conditions.eq(Account::getId, this.getId());
        }
        if (Stringx.isNotEmpty(this.getUsername())) {
            conditions.like(Account::getUsername, this.getUsername());
        }
        if (Stringx.isNotEmpty(this.getEmail())) {
            conditions.like(Account::getEmail, this.getEmail());
        }
        if (Stringx.isNotEmpty(this.getMobile())) {
            conditions.like(Account::getMobile, this.getMobile());
        }
        if (Stringx.isNotEmpty(this.getName())) {
            conditions.like(Account::getName, this.getName());
        }

        // 模糊字段搜索
        for (String keyword : this.getKeywords()) {
            conditions.and(filter -> filter.like(Account::getUsername, keyword).or().like(Account::getName, keyword));
        }

        return conditions;
    }
}
