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

package central.studio.provider.graphql;

import central.data.saas.ApplicationInput;
import central.data.saas.TenantApplicationInput;
import central.data.saas.TenantInput;
import central.data.system.DatabaseInput;
import central.data.system.DatabasePropertiesInput;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.studio.provider.database.core.DatabaseType;
import central.studio.provider.database.persistence.saas.ApplicationPersistence;
import central.studio.provider.database.persistence.saas.TenantApplicationPersistence;
import central.studio.provider.database.persistence.saas.TenantPersistence;
import central.studio.provider.database.persistence.saas.entity.ApplicationEntity;
import central.studio.provider.database.persistence.saas.entity.TenantEntity;
import central.studio.provider.database.persistence.system.DatabasePersistence;
import central.studio.provider.database.persistence.system.entity.DatabaseEntity;
import central.util.Guidx;
import lombok.Setter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Test Context
 * <p>
 * 测试上下文
 *
 * @author Alan Yeh
 * @since 2024/12/29
 */
@Component
public class TestContext implements InitializingBean, DisposableBean {

    @Setter(onMethod_ = @Autowired)
    private TenantPersistence tenantPersistence;

    @Setter(onMethod_ = @Autowired)
    private ApplicationPersistence applicationPersistence;

    @Setter(onMethod_ = @Autowired)
    private TenantApplicationPersistence relationPersistence;

    @Setter(onMethod_ = @Autowired)
    private DatabasePersistence databasePersistence;

    @Override
    public void afterPropertiesSet() throws Exception {
        // SaaS 应用
        var saas = applicationPersistence.findFirstBy(Columns.all(), Conditions.of(ApplicationEntity.class).eq(ApplicationEntity::getCode, "central-saas"), null);

        // 为 SaaS 应用新增测试数据库
        var database = databasePersistence.insert(DatabaseInput.builder()
                .applicationId(saas.getId())
                .code("test")
                .name("测试数据源")
                .type(DatabaseType.H2.getValue())
                .enabled(Boolean.TRUE)
                .remark("测试数据源，用后即删")
                .master(DatabasePropertiesInput.builder()
                        .driver("")
                        .url("jdbc:h2:mem:test")
                        .username("test")
                        .password("test")
                        .build())
                .slaves(Collections.emptyList())
                .params("{}")
                .build(), "syssa", "master");

        var tenant = tenantPersistence.insert(TenantInput.builder()
                .code("test")
                .name("测试租户")
                .databaseId(database.getId())
                .enabled(Boolean.TRUE)
                .remark("测试租户")
                .build(), "syssa");

        var application = applicationPersistence.insert(ApplicationInput.builder()
                .code("test")
                .name("测试应用")
                .logo("1234")
                .url("http://127.0.0.1:3100")
                .contextPath("/test")
                .secret(Guidx.nextID())
                .enabled(Boolean.TRUE)
                .remark("测试应用")
                .routes(Collections.emptyList())
                .build(), "syssa");

        relationPersistence.insert(TenantApplicationInput.builder()
                .tenantId(tenant.getId())
                .applicationId(application.getId())
                .enabled(Boolean.TRUE)
                .primary(Boolean.TRUE)
                .build(), "syssa");
    }

    @Override
    public void destroy() throws Exception {
        this.applicationPersistence.deleteBy(Conditions.of(ApplicationEntity.class).eq(ApplicationEntity::getCode, "test"));
        this.tenantPersistence.deleteBy(Conditions.of(TenantEntity.class).eq(TenantEntity::getCode, "test"));
        this.databasePersistence.deleteBy(Conditions.of(DatabaseEntity.class).eq(DatabaseEntity::getCode, "test"), "master");
    }

    /**
     * 获取测试租户
     */
    public TenantEntity getTenant() {
        return this.tenantPersistence.findFirstBy(Columns.all(), Conditions.of(TenantEntity.class).eq(TenantEntity::getCode, "master"), null);
    }

    /**
     * 测试应用
     */
    public ApplicationEntity getApplication() {
        return this.applicationPersistence.findFirstBy(Columns.all(), Conditions.of(ApplicationEntity.class).eq(ApplicationEntity::getCode, "central-identity"), null);
    }
}
