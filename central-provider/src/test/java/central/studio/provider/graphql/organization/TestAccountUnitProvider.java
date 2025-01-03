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

import central.data.organization.*;
import central.data.organization.option.AreaType;
import central.provider.graphql.organization.AccountUnitProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.organization.*;
import central.studio.provider.database.persistence.organization.entity.*;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Account Unit Provider
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestAccountUnitProvider extends TestProvider {

    @Setter(onMethod_ = @Autowired)
    private AccountUnitProvider provider;

    @Setter(onMethod_ = @Autowired)
    private AccountUnitPersistence persistence;

    @Setter(onMethod_ = @Autowired)
    private AccountPersistence accountPersistence;

    @Setter(onMethod_ = @Autowired)
    private AreaPersistence areaPersistence;

    @Setter(onMethod_ = @Autowired)
    private UnitPersistence unitPersistence;

    @Setter(onMethod_ = @Autowired)
    private RankPersistence rankPersistence;

    @Setter(onMethod_ = @Autowired)
    private PostPersistence postPersistence;

    @Setter(onMethod_ = @Autowired)
    private DepartmentPersistence departmentPersistence;

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

        this.accountPersistence.deleteBy(Conditions.of(AccountEntity.class).like(AccountEntity::getUsername, "test%"), tenant.getCode());
        this.rankPersistence.deleteBy(Conditions.of(RankEntity.class).like(RankEntity::getCode, "test%"), tenant.getCode());
        this.postPersistence.deleteBy(Conditions.of(PostEntity.class).like(PostEntity::getCode, "test%"), tenant.getCode());
        this.departmentPersistence.deleteBy(Conditions.of(DepartmentEntity.class).like(DepartmentEntity::getCode, "test%"), tenant.getCode());
        this.unitPersistence.deleteBy(Conditions.of(UnitEntity.class).like(UnitEntity::getCode, "test%"), tenant.getCode());
        this.areaPersistence.deleteBy(Conditions.of(AreaEntity.class).like(AreaEntity::getCode, "test%"), tenant.getCode());
    }

    /**
     * @see AccountUnitProvider#insert
     * @see AccountUnitProvider#findById
     * @see AccountUnitProvider#findByIds
     * @see AccountUnitProvider#countBy
     * @see AccountUnitProvider#deleteByIds
     */
    @Test
    public void case1() {
        var tenant = this.context.getTenant();

        var area = this.areaPersistence.insert(AreaInput.builder()
                .parentId("")
                .code("test")
                .name("测试区划")
                .type(AreaType.COUNTRY.getValue())
                .order(0)
                .build(), "syssa", tenant.getCode());
        var unit = this.unitPersistence.insert(UnitInput.builder()
                .parentId("")
                .areaId(area.getId())
                .code("test")
                .name("测试单位")
                .order(0)
                .build(), "syssa", tenant.getCode());

        var rank = this.rankPersistence.insert(RankInput.builder()
                .unitId(unit.getId())
                .code("test")
                .name("测试职级")
                .order(0)
                .build(), "syssa", tenant.getCode());

        var account = this.accountPersistence.insert(AccountInput.builder()
                .username("test")
                .email("test@example.com")
                .mobile("13000000000")
                .name("测试账号")
                .avatar("123")
                .enabled(true)
                .deleted(false)
                .build(), "syssa", tenant.getCode());

        var input = AccountUnitInput.builder()
                .accountId(account.getId())
                .unitId(unit.getId())
                .rankId(rank.getId())
                .primary(true)
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", tenant.getCode());
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getAccountId(), insert.getAccountId());
        assertNotNull(insert.getAccount());
        assertEquals(input.getAccountId(), insert.getAccount().getId());
        assertEquals(input.getUnitId(), insert.getUnitId());
        assertNotNull(insert.getUnit());
        assertEquals(input.getUnitId(), insert.getUnit().getId());
        assertEquals(input.getRankId(), insert.getRankId());
        assertNotNull(insert.getRank());
        assertEquals(input.getRankId(), insert.getRank().getId());
        assertEquals(input.getPrimary(), insert.getPrimary());

        var entity = this.persistence.findById(insert.getId(), Columns.all(), tenant.getCode());
        assertNotNull(entity);

        // test findById
        var findById = this.provider.findById(insert.getId(), tenant.getCode());
        assertNotNull(findById);
        assertEquals(insert.getId(), findById.getId());
        assertEquals(insert.getAccountId(), findById.getAccountId());
        assertNotNull(findById.getAccount());
        assertEquals(insert.getAccountId(), findById.getAccount().getId());
        assertEquals(insert.getUnitId(), findById.getUnitId());
        assertNotNull(findById.getUnit());
        assertEquals(insert.getUnitId(), findById.getUnit().getId());
        assertEquals(insert.getRankId(), findById.getRankId());
        assertNotNull(findById.getRank());
        assertEquals(insert.getRankId(), findById.getRank().getId());
        assertEquals(input.getPrimary(), findById.getPrimary());

        // test countBy
        var count = this.provider.countBy(Conditions.of(AccountUnit.class).eq(AccountUnit::getAccountId, account.getId()), tenant.getCode());
        assertEquals(1, count);

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), tenant.getCode());
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getAccountId(), fetched.getAccountId());
        assertNotNull(fetched.getAccount());
        assertEquals(insert.getAccountId(), fetched.getAccount().getId());
        assertEquals(insert.getUnitId(), fetched.getUnitId());
        assertNotNull(fetched.getUnit());
        assertEquals(insert.getUnitId(), fetched.getUnit().getId());
        assertEquals(insert.getRankId(), fetched.getRankId());
        assertNotNull(fetched.getRank());
        assertEquals(insert.getRankId(), fetched.getRank().getId());
        assertEquals(input.getPrimary(), fetched.getPrimary());

        // test deleteById
        count = this.provider.deleteByIds(List.of(insert.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(AccountUnitEntity.class).eq(AccountUnitEntity::getAccountId, account.getId()), tenant.getCode());
        assertEquals(0, count);
    }

    /**
     * @see AccountUnitProvider#insertBatch
     * @see AccountUnitProvider#findBy
     * @see AccountUnitProvider#pageBy
     * @see AccountUnitProvider#deleteBy
     */
    @Test
    public void case2() {
        var tenant = this.context.getTenant();

        var area = this.areaPersistence.insert(AreaInput.builder()
                .parentId("")
                .code("test")
                .name("测试区划")
                .type(AreaType.COUNTRY.getValue())
                .order(0)
                .build(), "syssa", tenant.getCode());
        var unit = this.unitPersistence.insert(UnitInput.builder()
                .parentId("")
                .areaId(area.getId())
                .code("test")
                .name("测试单位")
                .order(0)
                .build(), "syssa", tenant.getCode());

        var rank = this.rankPersistence.insert(RankInput.builder()
                .unitId(unit.getId())
                .code("test")
                .name("测试职级")
                .order(0)
                .build(), "syssa", tenant.getCode());

        var account = this.accountPersistence.insert(AccountInput.builder()
                .username("test")
                .email("test@example.com")
                .mobile("13000000000")
                .name("测试账号")
                .avatar("123")
                .enabled(true)
                .deleted(false)
                .build(), "syssa", tenant.getCode());

        var input = AccountUnitInput.builder()
                .accountId(account.getId())
                .unitId(unit.getId())
                .rankId(rank.getId())
                .primary(true)
                .build();

        // test insertBatch
        var insertBatch = this.provider.insertBatch(List.of(input), "syssa", tenant.getCode());
        assertNotNull(insertBatch);
        assertEquals(1, insertBatch.size());

        var insert = Listx.getFirstOrNull(insertBatch);
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getAccountId(), insert.getAccountId());
        assertNotNull(insert.getAccount());
        assertEquals(input.getAccountId(), insert.getAccount().getId());
        assertEquals(input.getUnitId(), insert.getUnitId());
        assertNotNull(insert.getUnit());
        assertEquals(input.getUnitId(), insert.getUnit().getId());
        assertEquals(input.getRankId(), insert.getRankId());
        assertNotNull(insert.getRank());
        assertEquals(input.getRankId(), insert.getRank().getId());
        assertEquals(input.getPrimary(), insert.getPrimary());

        var entity = this.persistence.findById(insert.getId(), Columns.all(), tenant.getCode());
        assertNotNull(entity);

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(AccountUnit.class).eq(AccountUnit::getAccountId, account.getId()), null, tenant.getCode());
        assertNotNull(findBy);
        assertEquals(1, findBy.size());

        var fetched = Listx.getFirstOrNull(findBy);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getAccountId(), fetched.getAccountId());
        assertNotNull(fetched.getAccount());
        assertEquals(insert.getAccountId(), fetched.getAccount().getId());
        assertEquals(insert.getUnitId(), fetched.getUnitId());
        assertNotNull(fetched.getUnit());
        assertEquals(insert.getUnitId(), fetched.getUnit().getId());
        assertEquals(insert.getRankId(), fetched.getRankId());
        assertNotNull(fetched.getRank());
        assertEquals(insert.getRankId(), fetched.getRank().getId());
        assertEquals(input.getPrimary(), fetched.getPrimary());

        // test pageBy
        var pageBy = this.provider.pageBy(1, 10, Conditions.of(AccountUnit.class).eq(AccountUnit::getAccountId, account.getId()), null, tenant.getCode());
        assertNotNull(pageBy);
        assertEquals(1, pageBy.getPager().getPageIndex());
        assertEquals(10, pageBy.getPager().getPageSize());
        assertEquals(1, pageBy.getPager().getPageCount());
        assertEquals(1, pageBy.getPager().getItemCount());
        assertEquals(1, pageBy.getData().size());

        fetched = Listx.getFirstOrNull(pageBy.getData());
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getAccountId(), fetched.getAccountId());
        assertNotNull(fetched.getAccount());
        assertEquals(insert.getAccountId(), fetched.getAccount().getId());
        assertEquals(insert.getUnitId(), fetched.getUnitId());
        assertNotNull(fetched.getUnit());
        assertEquals(insert.getUnitId(), fetched.getUnit().getId());
        assertEquals(insert.getRankId(), fetched.getRankId());
        assertNotNull(fetched.getRank());
        assertEquals(insert.getRankId(), fetched.getRank().getId());
        assertEquals(input.getPrimary(), fetched.getPrimary());

        // test deleteBy
        var count = this.provider.deleteBy(Conditions.of(AccountUnit.class).eq(AccountUnit::getAccountId, account.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(AccountUnitEntity.class).eq(AccountUnitEntity::getAccountId, account.getId()), tenant.getCode());
        assertEquals(0, count);
    }
}
