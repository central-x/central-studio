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

import central.data.saas.*;
import central.data.system.DatabaseInput;
import central.data.system.DatabasePropertiesInput;
import central.provider.graphql.saas.TenantApplicationProvider;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.core.DatabaseType;
import central.studio.provider.database.persistence.saas.ApplicationPersistence;
import central.studio.provider.database.persistence.saas.TenantApplicationPersistence;
import central.studio.provider.database.persistence.saas.TenantPersistence;
import central.studio.provider.database.persistence.saas.entity.ApplicationEntity;
import central.studio.provider.database.persistence.saas.entity.TenantApplicationEntity;
import central.studio.provider.database.persistence.saas.entity.TenantEntity;
import central.studio.provider.database.persistence.system.DatabasePersistence;
import central.studio.provider.database.persistence.system.entity.DatabaseEntity;
import central.studio.provider.graphql.TestProvider;
import central.util.Guidx;
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
 * Tenant Application Provider Test Cases
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestTenantApplicationProvider extends TestProvider {

    @Setter(onMethod_ = @Autowired)
    private TenantApplicationProvider provider;

    @Setter(onMethod_ = @Autowired)
    private TenantApplicationPersistence persistence;

    @Setter(onMethod_ = @Autowired)
    private TenantPersistence tenantPersistence;

    @Setter(onMethod_ = @Autowired)
    private ApplicationPersistence applicationPersistence;

    @Setter(onMethod_ = @Autowired)
    private DatabasePersistence databasePersistence;

    @AfterEach
    public void clear() {
        // 清空数据
        this.applicationPersistence.deleteBy(Conditions.of(ApplicationEntity.class).like(ApplicationEntity::getCode, "test%"));
        this.tenantPersistence.deleteBy(Conditions.of(TenantEntity.class).like(TenantEntity::getCode, "test%"));
        this.databasePersistence.deleteBy(Conditions.of(DatabaseEntity.class).like(DatabaseEntity::getCode, "test%"), "master");
    }

    /**
     * @see TenantApplicationProvider#insert
     * @see TenantApplicationProvider#findById
     * @see TenantApplicationProvider#update
     * @see TenantApplicationProvider#findByIds
     * @see TenantApplicationProvider#countBy
     * @see TenantApplicationProvider#deleteByIds
     */
    @Test
    public void case1() {
        var saasApplication = applicationPersistence.findFirstBy(Columns.all(), Conditions.of(ApplicationEntity.class).eq(ApplicationEntity::getCode, "central-saas"), null);
        assertNotNull(saasApplication);

        var database = this.databasePersistence.insert(DatabaseInput.builder()
                .applicationId(saasApplication.getId())
                .code("test")
                .name("测试数据库")
                .type(DatabaseType.H2.getValue())
                .enabled(Boolean.TRUE)
                .remark("测试")
                .master(DatabasePropertiesInput.builder().driver("org.h2.Driver").url("jdbc:h2:mem:central-provider").username("root").password("root").build())
                .params(Jsonx.Default().serialize(Map.of()))
                .build(), "syssa", "master");

        var tenant = this.tenantPersistence.insert(TenantInput.builder()
                .code("test")
                .name("测试租户")
                .databaseId(database.getId())
                .enabled(Boolean.TRUE)
                .remark("测试租户")
                .build(), "syssa");

        var application = this.applicationPersistence.insert(ApplicationInput.builder()
                .code("test")
                .name("测试应用")
                .logo("1234")
                .url("http://127.0.0.1:3100")
                .contextPath("/test")
                .secret(Guidx.nextID())
                .enabled(Boolean.TRUE)
                .remark("测试应用")
                .routes(List.of(
                        ApplicationRouteInput.builder().contextPath("/test/test").url("http://127.0.0.1:3110").enabled(Boolean.TRUE).remark("测试路由1").build()
                ))
                .build(), "syssa");

        var input = TenantApplicationInput.builder()
                .tenantId(tenant.getId())
                .applicationId(application.getId())
                .enabled(Boolean.TRUE)
                .primary(Boolean.TRUE)
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", "master");
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getTenantId(), insert.getTenantId());
        assertEquals(input.getTenantId(), insert.getTenant().getId());
        assertEquals(input.getApplicationId(), insert.getApplicationId());
        assertEquals(input.getApplicationId(), insert.getApplication().getId());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getPrimary(), insert.getPrimary());

        // test findById
        var findById = this.provider.findById(insert.getId(), "master");
        assertNotNull(findById);
        assertEquals(insert.getId(), findById.getId());
        assertEquals(insert.getTenantId(), findById.getTenantId());
        assertEquals(insert.getTenantId(), findById.getTenant().getId());
        assertEquals(insert.getApplicationId(), findById.getApplicationId());
        assertEquals(insert.getApplicationId(), findById.getApplication().getId());
        assertEquals(insert.getEnabled(), findById.getEnabled());
        assertEquals(insert.getPrimary(), findById.getPrimary());

        // test countBy
        var count = this.provider.countBy(Conditions.of(TenantApplication.class).eq(TenantApplication::getTenantId, tenant.getId()), "master");
        assertEquals(1, count);

        // test update
        this.provider.update(insert.toInput().enabled(Boolean.FALSE).primary(Boolean.FALSE).build(), "syssa", "master");

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), "master");
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getTenantId(), fetched.getTenantId());
        assertEquals(insert.getTenantId(), fetched.getTenant().getId());
        assertEquals(insert.getApplicationId(), fetched.getApplicationId());
        assertEquals(insert.getApplicationId(), fetched.getApplication().getId());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(Boolean.FALSE, fetched.getPrimary());

        // test deleteById
        count = this.provider.deleteByIds(List.of(insert.getId()), "master");
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(TenantApplicationEntity.class).eq(TenantApplicationEntity::getTenantId, tenant.getId()));
        assertEquals(0, count);
    }

    /**
     * @see TenantApplicationProvider#insertBatch
     * @see TenantApplicationProvider#findBy
     * @see TenantApplicationProvider#updateBatch
     * @see TenantApplicationProvider#pageBy
     * @see TenantApplicationProvider#deleteBy
     */
    @Test
    public void case2() {
        var saasApplication = applicationPersistence.findFirstBy(Columns.all(), Conditions.of(ApplicationEntity.class).eq(ApplicationEntity::getCode, "central-saas"), null);
        assertNotNull(saasApplication);

        var database = this.databasePersistence.insert(DatabaseInput.builder()
                .applicationId(saasApplication.getId())
                .code("test")
                .name("测试数据库")
                .type(DatabaseType.H2.getValue())
                .enabled(Boolean.TRUE)
                .remark("测试")
                .master(DatabasePropertiesInput.builder().driver("org.h2.Driver").url("jdbc:h2:mem:central-provider").username("root").password("root").build())
                .params(Jsonx.Default().serialize(Map.of()))
                .build(), "syssa", "master");

        var tenant = this.tenantPersistence.insert(TenantInput.builder()
                .code("test")
                .name("测试租户")
                .databaseId(database.getId())
                .enabled(Boolean.TRUE)
                .remark("测试租户")
                .build(), "syssa");

        var application = this.applicationPersistence.insert(ApplicationInput.builder()
                .code("test")
                .name("测试应用")
                .logo("1234")
                .url("http://127.0.0.1:3100")
                .contextPath("/test")
                .secret(Guidx.nextID())
                .enabled(Boolean.TRUE)
                .remark("测试应用")
                .routes(List.of(
                        ApplicationRouteInput.builder().contextPath("/test/test").url("http://127.0.0.1:3110").enabled(Boolean.TRUE).remark("测试路由1").build()
                ))
                .build(), "syssa");

        var input = TenantApplicationInput.builder()
                .tenantId(tenant.getId())
                .applicationId(application.getId())
                .enabled(Boolean.TRUE)
                .primary(Boolean.TRUE)
                .build();

        // test insertBatch
        var insertBatch = this.provider.insertBatch(List.of(input), "syssa", "master");
        assertNotNull(insertBatch);
        assertEquals(1, insertBatch.size());

        var insert = Listx.getFirstOrNull(insertBatch);
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getTenantId(), insert.getTenantId());
        assertEquals(input.getTenantId(), insert.getTenant().getId());
        assertEquals(input.getApplicationId(), insert.getApplicationId());
        assertEquals(input.getApplicationId(), insert.getApplication().getId());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getPrimary(), insert.getPrimary());

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(TenantApplication.class).eq(TenantApplication::getTenantId, tenant.getId()), null, "master");
        assertNotNull(findBy);
        assertEquals(1, findBy.size());

        var fetched = Listx.getFirstOrNull(findBy);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getTenantId(), fetched.getTenantId());
        assertEquals(insert.getTenantId(), fetched.getTenant().getId());
        assertEquals(insert.getApplicationId(), fetched.getApplicationId());
        assertEquals(insert.getApplicationId(), fetched.getApplication().getId());
        assertEquals(insert.getEnabled(), fetched.getEnabled());
        assertEquals(insert.getPrimary(), fetched.getPrimary());

        // test update
        this.provider.updateBatch(List.of(insert.toInput().enabled(Boolean.FALSE).primary(Boolean.FALSE).build()), "syssa", "master");

        // test pageBy
        var pageBy = this.provider.pageBy(1, 10, Conditions.of(TenantApplication.class).eq(TenantApplication::getTenantId, tenant.getId()), null, "master");
        assertNotNull(pageBy);
        assertEquals(1, pageBy.getPager().getPageIndex());
        assertEquals(10, pageBy.getPager().getPageSize());
        assertEquals(1, pageBy.getPager().getPageCount());
        assertEquals(1, pageBy.getPager().getItemCount());
        assertEquals(1, pageBy.getData().size());

        fetched = Listx.getFirstOrNull(pageBy.getData());
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getTenantId(), fetched.getTenantId());
        assertEquals(insert.getTenantId(), fetched.getTenant().getId());
        assertEquals(insert.getApplicationId(), fetched.getApplicationId());
        assertEquals(insert.getApplicationId(), fetched.getApplication().getId());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(Boolean.FALSE, fetched.getPrimary());

        // test deleteById
        var count = this.provider.deleteByIds(List.of(insert.getId()), "master");
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(TenantApplicationEntity.class).eq(TenantApplicationEntity::getTenantId, tenant.getId()));
        assertEquals(0, count);
    }
}
