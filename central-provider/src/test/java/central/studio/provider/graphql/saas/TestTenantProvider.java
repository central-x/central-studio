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

package central.studio.provider.graphql.saas;

import central.data.saas.Tenant;
import central.data.saas.TenantInput;
import central.data.system.DatabaseInput;
import central.data.system.DatabasePropertiesInput;
import central.provider.graphql.saas.TenantProvider;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.core.DatabaseType;
import central.studio.provider.database.persistence.saas.ApplicationPersistence;
import central.studio.provider.database.persistence.saas.TenantPersistence;
import central.studio.provider.database.persistence.saas.entity.ApplicationEntity;
import central.studio.provider.database.persistence.saas.entity.TenantEntity;
import central.studio.provider.database.persistence.system.DatabasePersistence;
import central.studio.provider.database.persistence.system.entity.DatabaseEntity;
import central.studio.provider.graphql.TestProvider;
import central.util.Jsonx;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tenant Provider Test Cases
 * 租户
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestTenantProvider extends TestProvider {

    @Setter(onMethod_ = @Autowired)
    private TenantProvider provider;

    @Setter(onMethod_ = @Autowired)
    private TenantPersistence persistence;

    @Setter(onMethod_ = @Autowired)
    private DatabasePersistence databasePersistence;

    @Setter(onMethod_ = @Autowired)
    private ApplicationPersistence applicationPersistence;

    @AfterEach
    public void clear() {
        // 清空数据
        this.persistence.deleteBy(Conditions.of(TenantEntity.class).like(TenantEntity::getCode, "test%"));
        this.databasePersistence.deleteBy(Conditions.of(DatabaseEntity.class).like(DatabaseEntity::getCode, "test%"), "master");
    }

    private ApplicationEntity getApplication() {
        return applicationPersistence.findFirstBy(Columns.all(), Conditions.of(ApplicationEntity.class).eq(ApplicationEntity::getCode, "central-saas"), null);
    }

    /**
     * @see TenantProvider#insert
     * @see TenantProvider#findById
     * @see TenantProvider#update
     * @see TenantProvider#findByIds
     * @see TenantProvider#countBy
     * @see TenantProvider#deleteByIds
     */
    @Test
    public void case1() {
        var application = this.getApplication();
        var database = this.databasePersistence.insert(DatabaseInput.builder()
                .applicationId(application.getId())
                .code("test")
                .name("测试数据库")
                .type(DatabaseType.H2.getValue())
                .enabled(Boolean.TRUE)
                .remark("测试")
                .master(DatabasePropertiesInput.builder().driver("org.h2.Driver").url("jdbc:h2:mem:central-provider").username("root").password("root").build())
                .params(Jsonx.Default().serialize(Map.of()))
                .build(), "syssa", "master");

        var input = TenantInput.builder()
                .code("test")
                .name("测试租户")
                .databaseId(database.getId())
                .enabled(Boolean.TRUE)
                .remark("测试租户")
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", "master");
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getDatabaseId(), insert.getDatabaseId());
        assertEquals(input.getDatabaseId(), insert.getDatabase().getId());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getRemark(), insert.getRemark());

        // test findById
        var findById = this.provider.findById(insert.getId(), "master");
        assertNotNull(findById);
        assertEquals(insert.getId(), findById.getId());
        assertEquals(insert.getCode(), findById.getCode());
        assertEquals(insert.getName(), findById.getName());
        assertEquals(insert.getDatabaseId(), findById.getDatabaseId());
        assertEquals(insert.getDatabaseId(), findById.getDatabase().getId());
        assertEquals(insert.getEnabled(), findById.getEnabled());
        assertEquals(insert.getRemark(), findById.getRemark());

        // test countBy
        var count = this.provider.countBy(Conditions.of(Tenant.class).like(Tenant::getCode, "test%"), "master");
        assertEquals(1, count);

        // test update
        this.provider.update(insert.toInput().code("test2").enabled(Boolean.FALSE).build(), "syssa", "master");

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), "master");
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals("test2", fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getDatabaseId(), fetched.getDatabaseId());
        assertEquals(insert.getDatabaseId(), fetched.getDatabase().getId());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(insert.getRemark(), fetched.getRemark());

        // test deleteById
        count = this.provider.deleteByIds(List.of(insert.getId()), "master");
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(TenantEntity.class).like(TenantEntity::getCode, "test%"));
        assertEquals(0, count);
    }

    /**
     * @see TenantProvider#insertBatch
     * @see TenantProvider#findBy
     * @see TenantProvider#updateBatch
     * @see TenantProvider#pageBy
     * @see TenantProvider#deleteBy
     */
    @Test
    public void case2() {
        var application = this.getApplication();
        var database = this.databasePersistence.insert(DatabaseInput.builder()
                .applicationId(application.getId())
                .code("test")
                .name("测试数据库")
                .type(DatabaseType.H2.getValue())
                .enabled(Boolean.TRUE)
                .remark("测试")
                .master(DatabasePropertiesInput.builder().driver("org.h2.Driver").url("jdbc:h2:mem:central-provider").username("root").password("root").build())
                .params(Jsonx.Default().serialize(Map.of()))
                .build(), "syssa", "master");

        var input = TenantInput.builder()
                .code("test")
                .name("测试租户")
                .databaseId(database.getId())
                .enabled(Boolean.TRUE)
                .remark("测试租户")
                .build();

        // test insertBatch
        var insertBatch = this.provider.insertBatch(List.of(input), "syssa", "master");
        assertNotNull(insertBatch);
        assertEquals(1, insertBatch.size());

        var insert = Listx.getFirstOrNull(insertBatch);
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getDatabaseId(), insert.getDatabaseId());
        assertEquals(input.getDatabaseId(), insert.getDatabase().getId());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getRemark(), insert.getRemark());

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(Tenant.class).like(Tenant::getCode, "test%"), null, "master");
        assertNotNull(findBy);
        assertEquals(1, findBy.size());

        var fetched = Listx.getFirstOrNull(findBy);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getCode(), fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getDatabaseId(), fetched.getDatabaseId());
        assertEquals(insert.getDatabaseId(), fetched.getDatabase().getId());
        assertEquals(insert.getEnabled(), fetched.getEnabled());
        assertEquals(insert.getRemark(), fetched.getRemark());

        // test updateBatch
        this.provider.updateBatch(List.of(insert.toInput().code("test2").enabled(Boolean.FALSE).build()), "syssa", "master");

        // test pageBy
        var pageBy = this.provider.pageBy(1, 10, Conditions.of(Tenant.class).like(Tenant::getCode, "test%"), null, "master");
        assertNotNull(pageBy);
        assertEquals(1, pageBy.getPager().getPageIndex());
        assertEquals(10, pageBy.getPager().getPageSize());
        assertEquals(1, pageBy.getPager().getPageCount());
        assertEquals(1, pageBy.getPager().getItemCount());
        assertEquals(1, pageBy.getData().size());

        fetched = Listx.getFirstOrNull(pageBy.getData());
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals("test2", fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getDatabaseId(), fetched.getDatabaseId());
        assertEquals(insert.getDatabaseId(), fetched.getDatabase().getId());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(insert.getRemark(), fetched.getRemark());

        // test deleteBy
        var count = this.provider.deleteBy(Conditions.of(Tenant.class).eq(Tenant::getCode, "test2"), "master");
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(TenantEntity.class).like(TenantEntity::getCode, "test%"));
        assertEquals(0, count);
    }
}
