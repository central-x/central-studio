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

package central.studio.provider.graphql.identity;

import central.data.identity.IdentityPassword;
import central.data.identity.IdentityPasswordInput;
import central.data.organization.AccountInput;
import central.provider.graphql.identity.IdentityPasswordProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.security.Passwordx;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.identity.IdentityPasswordPersistence;
import central.studio.provider.database.persistence.identity.entity.IdentityPasswordEntity;
import central.studio.provider.database.persistence.organization.AccountPersistence;
import central.studio.provider.database.persistence.organization.entity.AccountEntity;
import central.studio.provider.graphql.TestContext;
import central.studio.provider.graphql.TestProvider;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Password Provider Test Cases
 * 密码
 *
 * @author Alan Yeh
 * @since 2022/10/07
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestIdentityPasswordProvider extends TestProvider {

    @Setter(onMethod_ = @Autowired)
    private IdentityPasswordProvider provider;

    @Setter(onMethod_ = @Autowired)
    private IdentityPasswordPersistence persistence;

    @Setter(onMethod_ = @Autowired)
    private AccountPersistence accountPersistence;

    @Setter(onMethod_ = @Autowired)
    private TestContext context;

    @BeforeAll
    public static void setup(@Autowired DataContext context) throws Exception {
        SaasContainer container = null;
        while (container == null || container.getApplications().isEmpty()) {
            Thread.sleep(100);
            container = context.getData(DataFetcherType.SAAS);
        }
    }

    @AfterEach
    public void clear() throws Exception {
        var tenant = this.context.getTenant();
        // 清空测试数据
        this.persistence.deleteBy(Conditions.of(IdentityPasswordEntity.class), tenant.getCode());
        this.accountPersistence.deleteBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getUsername, "test"), tenant.getCode());
    }

    /**
     * @see IdentityPasswordProvider#insert
     * @see IdentityPasswordProvider#findById
     * @see IdentityPasswordProvider#findByIds
     * @see IdentityPasswordProvider#countBy
     * @see IdentityPasswordProvider#deleteByIds
     */
    @Test
    public void case1() {
        var tenant = this.context.getTenant();
        var account = this.accountPersistence.insert(AccountInput.builder()
                .username("test")
                .email("test@central-x.com")
                .mobile("18888888888")
                .name("测试帐号")
                .avatar("1234")
                .enabled(Boolean.TRUE)
                .deleted(Boolean.FALSE)
                .build(), "syssa", tenant.getCode());

        var input = IdentityPasswordInput.builder()
                .accountId(account.getId())
                .value("x.123456")
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", tenant.getCode());
        assertNotNull(insert);
        assertEquals(input.getAccountId(), insert.getAccountId());
        assertEquals(input.getAccountId(), insert.getAccount().getId());
        assertTrue(Passwordx.verify(input.getValue(), insert.getValue()));
        var entity = this.persistence.findById(insert.getId(), Columns.all(), tenant.getCode());
        assertNotNull(entity);

        // test findById
        var findById = this.provider.findById(insert.getId(), tenant.getCode());
        assertNotNull(findById);
        assertEquals(input.getAccountId(), findById.getAccountId());
        assertEquals(input.getAccountId(), findById.getAccount().getId());
        assertTrue(Passwordx.verify(input.getValue(), findById.getValue()));

        // test countBy
        var count = this.provider.countBy(Conditions.of(IdentityPassword.class).eq(IdentityPassword::getAccountId, input.getAccountId()), tenant.getCode());
        assertEquals(1, count);

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), tenant.getCode());
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertEquals(input.getAccountId(), fetched.getAccountId());
        assertEquals(input.getAccountId(), fetched.getAccount().getId());
        assertTrue(Passwordx.verify(input.getValue(), insert.getValue()));

        // test deleteById
        count = this.provider.deleteByIds(List.of(insert.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(IdentityPasswordEntity.class).eq(IdentityPasswordEntity::getAccountId, input.getAccountId()), tenant.getCode());
        assertEquals(0, count);
    }

    /**
     * @see IdentityPasswordProvider#insertBatch
     * @see IdentityPasswordProvider#findBy
     * @see IdentityPasswordProvider#pageBy
     * @see IdentityPasswordProvider#deleteBy
     */
    @Test
    public void case2() {
        var tenant = this.context.getTenant();
        var account = this.accountPersistence.insert(AccountInput.builder()
                .username("test")
                .email("test@central-x.com")
                .mobile("18888888888")
                .name("测试帐号")
                .avatar("1234")
                .enabled(Boolean.TRUE)
                .deleted(Boolean.FALSE)
                .build(), "syssa", tenant.getCode());

        var input = IdentityPasswordInput.builder()
                .accountId(account.getId())
                .value("x.123456")
                .build();

        // test insertBatch
        var insertBatch = this.provider.insertBatch(List.of(input), "syssa", tenant.getCode());
        assertNotNull(insertBatch);
        assertEquals(1, insertBatch.size());

        var insert = Listx.getFirstOrNull(insertBatch);
        assertNotNull(insert);
        assertEquals(input.getAccountId(), insert.getAccountId());
        assertEquals(input.getAccountId(), insert.getAccount().getId());
        assertTrue(Passwordx.verify(input.getValue(), insert.getValue()));
        var entity = this.persistence.findById(insert.getId(), Columns.all(), tenant.getCode());
        assertNotNull(entity);

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(IdentityPassword.class).eq(IdentityPassword::getAccountId, insert.getAccountId()), null, tenant.getCode());
        assertNotNull(findBy);
        assertEquals(1, findBy.size());

        var fetched = Listx.getFirstOrNull(findBy);
        assertNotNull(fetched);
        assertEquals(input.getAccountId(), fetched.getAccountId());
        assertEquals(input.getAccountId(), fetched.getAccount().getId());
        assertTrue(Passwordx.verify(input.getValue(), fetched.getValue()));

        // test pageBy
        var pageBy = this.provider.pageBy(1, 10, Conditions.of(IdentityPassword.class).eq(IdentityPassword::getAccountId, insert.getAccountId()), null, tenant.getCode());
        assertNotNull(pageBy);
        assertEquals(1, pageBy.getPager().getPageIndex());
        assertEquals(10, pageBy.getPager().getPageSize());
        assertEquals(1, pageBy.getPager().getPageCount());
        assertEquals(1, pageBy.getPager().getItemCount());
        assertEquals(1, pageBy.getData().size());

        fetched = Listx.getFirstOrNull(pageBy.getData());
        assertNotNull(fetched);
        assertEquals(input.getAccountId(), fetched.getAccountId());
        assertEquals(input.getAccountId(), fetched.getAccount().getId());
        assertTrue(Passwordx.verify(input.getValue(), insert.getValue()));

        // test deleteBy
        var count = this.provider.deleteBy(Conditions.of(IdentityPassword.class).eq(IdentityPassword::getAccountId, insert.getAccountId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(IdentityPasswordEntity.class).eq(IdentityPasswordEntity::getAccountId, input.getAccountId()), tenant.getCode());
        assertEquals(0, count);
    }
}
