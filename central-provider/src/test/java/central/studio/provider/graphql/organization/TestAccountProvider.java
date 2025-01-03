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

package central.studio.provider.graphql.organization;

import central.data.organization.Account;
import central.data.organization.AccountInput;
import central.provider.graphql.organization.AccountProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.ProviderProperties;
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
 * Account Provider Test Cases
 *
 * @author Alan Yeh
 * @since 2022/09/26
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestAccountProvider extends TestProvider {

    @Setter(onMethod_ = @Autowired)
    private ProviderProperties properties;

    @Setter(onMethod_ = @Autowired)
    private AccountProvider provider;

    @Setter(onMethod_ = @Autowired)
    private AccountPersistence persistence;

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
    public void clear() {
        // 清空数据
        var tenant = this.context.getTenant();

        this.persistence.deleteBy(Conditions.of(AccountEntity.class).like(AccountEntity::getUsername, "test%"), tenant.getCode());
    }

    /**
     * @see AccountProvider#findById
     */
    @Test
    public void case1() {
        // 查询超级管理员数据
        var supervisor = this.provider.findById("syssa", "master");
        assertNotNull(supervisor);
        assertEquals(this.properties.getSupervisor().getUsername(), supervisor.getId());
        assertEquals(this.properties.getSupervisor().getUsername(), supervisor.getUsername());
        assertEquals(this.properties.getSupervisor().getEmail(), supervisor.getEmail());
        assertEquals(this.properties.getSupervisor().getName(), supervisor.getName());
        assertEquals(this.properties.getSupervisor().getAvatar(), supervisor.getAvatar());
        assertEquals(this.properties.getSupervisor().getEnabled(), supervisor.getEnabled());
        assertFalse(supervisor.getDeleted());
        assertNotNull(supervisor.getCreator());
        assertEquals(this.properties.getSupervisor().getUsername(), supervisor.getCreator().getId());
        assertEquals(this.properties.getSupervisor().getUsername(), supervisor.getCreator().getUsername());
        assertEquals(this.properties.getSupervisor().getName(), supervisor.getCreator().getName());
        assertNotNull(supervisor.getModifier());
        assertEquals(this.properties.getSupervisor().getUsername(), supervisor.getModifier().getId());
        assertEquals(this.properties.getSupervisor().getUsername(), supervisor.getModifier().getUsername());
        assertEquals(this.properties.getSupervisor().getName(), supervisor.getModifier().getName());
    }

    /**
     * @see AccountProvider#insert
     * @see AccountProvider#findById
     * @see AccountProvider#update
     * @see AccountProvider#findByIds
     * @see AccountProvider#countBy
     * @see AccountProvider#deleteByIds
     */
    @Test
    public void case2() {
        var tenant = this.context.getTenant();

        var input = AccountInput.builder()
                .username("test")
                .email("test@central-x.com")
                .mobile("18888888888")
                .name("测试帐号")
                .avatar("1234")
                .enabled(Boolean.TRUE)
                .deleted(Boolean.FALSE)
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", tenant.getCode());
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getUsername(), insert.getUsername());
        assertEquals(input.getEmail(), insert.getEmail());
        assertEquals(input.getMobile(), insert.getMobile());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getAvatar(), insert.getAvatar());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getDeleted(), insert.getDeleted());
        assertFalse(insert.getAdmin());
        assertFalse(insert.getSupervisor());

        var entity = this.persistence.findById(insert.getId(), Columns.all(), tenant.getCode());
        assertNotNull(entity);

        // test findById
        var findById = this.provider.findById(insert.getId(), tenant.getCode());
        assertNotNull(findById);
        assertNotNull(insert.getId(), findById.getId());
        assertEquals(insert.getUsername(), findById.getUsername());
        assertEquals(insert.getEmail(), findById.getEmail());
        assertEquals(insert.getMobile(), findById.getMobile());
        assertEquals(insert.getName(), findById.getName());
        assertEquals(insert.getAvatar(), findById.getAvatar());
        assertEquals(insert.getEnabled(), findById.getEnabled());
        assertEquals(insert.getDeleted(), findById.getDeleted());
        assertFalse(findById.getAdmin());
        assertFalse(findById.getSupervisor());

        // test countBy
        var count = this.provider.countBy(Conditions.of(Account.class).like(Account::getUsername, "test%"), tenant.getCode());
        assertEquals(1, count);

        // test update
        this.provider.update(insert.toInput().username("test2").build(), "syssa", tenant.getCode());

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), tenant.getCode());
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertNotNull(insert.getId(), fetched.getId());
        assertEquals("test2", fetched.getUsername());
        assertEquals(insert.getEmail(), fetched.getEmail());
        assertEquals(insert.getMobile(), fetched.getMobile());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getAvatar(), fetched.getAvatar());
        assertEquals(insert.getEnabled(), fetched.getEnabled());
        assertEquals(insert.getDeleted(), fetched.getDeleted());
        assertFalse(fetched.getAdmin());
        assertFalse(fetched.getSupervisor());

        // test deleteById
        count = this.provider.deleteByIds(List.of(insert.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(AccountEntity.class).like(AccountEntity::getUsername, "test%"), tenant.getCode());
        assertEquals(0, count);
    }

    /**
     * @see AccountProvider#insertBatch
     * @see AccountProvider#findBy
     * @see AccountProvider#updateBatch
     * @see AccountProvider#pageBy
     * @see AccountProvider#deleteBy
     */
    @Test
    public void case3() {
        var tenant = this.context.getTenant();

        var input = AccountInput.builder()
                .username("test")
                .email("test@central-x.com")
                .mobile("18888888888")
                .name("测试帐号")
                .avatar("1234")
                .enabled(Boolean.TRUE)
                .deleted(Boolean.FALSE)
                .build();

        // test insertBatch
        var insertBatch = this.provider.insertBatch(List.of(input), "syssa", tenant.getCode());
        assertNotNull(insertBatch);
        assertEquals(1, insertBatch.size());

        var insert = Listx.getFirstOrNull(insertBatch);
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getUsername(), insert.getUsername());
        assertEquals(input.getEmail(), insert.getEmail());
        assertEquals(input.getMobile(), insert.getMobile());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getAvatar(), insert.getAvatar());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getDeleted(), insert.getDeleted());
        assertFalse(insert.getAdmin());
        assertFalse(insert.getSupervisor());

        var entity = this.persistence.findById(insert.getId(), Columns.all(), tenant.getCode());
        assertNotNull(entity);

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(Account.class).like(Account::getUsername, "test%"), null, tenant.getCode());
        assertNotNull(findBy);
        assertEquals(1, findBy.size());

        var fetched = Listx.getFirstOrNull(findBy);
        assertNotNull(fetched);
        assertNotNull(insert.getId(), fetched.getId());
        assertEquals(insert.getUsername(), fetched.getUsername());
        assertEquals(insert.getEmail(), fetched.getEmail());
        assertEquals(insert.getMobile(), fetched.getMobile());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getAvatar(), fetched.getAvatar());
        assertEquals(insert.getEnabled(), fetched.getEnabled());
        assertEquals(insert.getDeleted(), fetched.getDeleted());
        assertFalse(fetched.getAdmin());
        assertFalse(fetched.getSupervisor());

        // test updateBatch
        this.provider.updateBatch(List.of(insert.toInput().username("test2").build()), "syssa", tenant.getCode());

        // test pageBy
        var pageBy = this.provider.pageBy(1, 10, Conditions.of(Account.class).like(Account::getUsername, "test%"), null, tenant.getCode());
        assertNotNull(pageBy);
        assertEquals(1, pageBy.getPager().getPageIndex());
        assertEquals(10, pageBy.getPager().getPageSize());
        assertEquals(1, pageBy.getPager().getPageCount());
        assertEquals(1, pageBy.getPager().getItemCount());
        assertEquals(1, pageBy.getData().size());

        fetched = Listx.getFirstOrNull(pageBy.getData());
        assertNotNull(fetched);
        assertNotNull(insert.getId(), fetched.getId());
        assertEquals("test2", fetched.getUsername());
        assertEquals(insert.getEmail(), fetched.getEmail());
        assertEquals(insert.getMobile(), fetched.getMobile());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getAvatar(), fetched.getAvatar());
        assertEquals(insert.getEnabled(), fetched.getEnabled());
        assertEquals(insert.getDeleted(), fetched.getDeleted());
        assertFalse(fetched.getAdmin());
        assertFalse(fetched.getSupervisor());

        // test deleteBy
        var count = this.provider.deleteBy(Conditions.of(Account.class).like(Account::getUsername, "test%"), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(AccountEntity.class).like(AccountEntity::getUsername, "test%"), tenant.getCode());
        assertEquals(0, count);
    }
}
