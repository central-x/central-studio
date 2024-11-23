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

package central.studio.provider.database.initialization;

import central.data.system.option.DatabaseType;
import central.io.IOStreamx;
import central.sql.SqlExecutor;
import central.studio.provider.graphql.saas.entity.ApplicationEntity;
import central.studio.provider.graphql.saas.entity.TenantApplicationEntity;
import central.studio.provider.graphql.saas.entity.TenantEntity;
import central.studio.provider.graphql.saas.mapper.ApplicationMapper;
import central.studio.provider.graphql.saas.mapper.TenantApplicationMapper;
import central.studio.provider.graphql.saas.mapper.TenantMapper;
import central.studio.provider.graphql.system.entity.DatabaseEntity;
import central.studio.provider.graphql.system.mapper.DatabaseMapper;
import central.util.Jsonx;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 主租户数据初始化
 *
 * @author Alan Yeh
 * @since 2024/06/22
 */
@RequiredArgsConstructor
public class MasterTenantInitializer {
    private final SqlExecutor executor;

    public void initialize() throws Exception {
        // 初始化应用信息
        var applications = this.initApplications();
        // 初始化主数据库信息
        var masterDatabase = this.initDatabase(applications);
        // 初始化主租户信息
        var masterTenant = this.initTenant(masterDatabase);
        // 初始化主租户的租用关系
        this.initRelation(masterTenant, applications);
        // 初始化租户数据
        new TenantInitializer(this.executor).initialize(masterTenant.getCode());
    }

    /**
     * 初始化应用程序
     */
    private Map<String, ApplicationEntity> initApplications() throws IOException {
        // 控制面板
        var dashboardApp = new ApplicationEntity();
        dashboardApp.setId("y9llwWofojZAtx71JP1");
        dashboardApp.setCode("central-dashboard");
        dashboardApp.setName("控制中心");
        dashboardApp.setLogoBytes(IOStreamx.readBytes(Thread.currentThread().getContextClassLoader().getResourceAsStream("central/logo/central-security.png")));
        dashboardApp.setUrl("http://central-dashboard");
        dashboardApp.setContextPath("/dashboard");
        dashboardApp.setSecret("glngxOUCye6fndsaiQI");
        dashboardApp.setEnabled(Boolean.TRUE);
        dashboardApp.setRemark("用于提供套件中所有应用的管理界面");
        dashboardApp.setRoutesJson("[]");
        dashboardApp.updateCreator("syssa");

        // Saas中心
        var saasApp = new ApplicationEntity();
        saasApp.setId("UFQG4uh6DtrT4AgLA4Z");
        saasApp.setCode("central-saas");
        saasApp.setName("租户中心");
        saasApp.setLogoBytes(IOStreamx.readBytes(Thread.currentThread().getContextClassLoader().getResourceAsStream("central/logo/central-tenant.png")));
        saasApp.setUrl("http://central-saas");
        saasApp.setContextPath("/saas");
        saasApp.setSecret("btdHv1HdTTWzCEbiRdg");
        saasApp.setEnabled(Boolean.TRUE);
        saasApp.setRemark("用于维护租户、应用系统以及其租用关系等");
        saasApp.setRoutesJson("[]");
        saasApp.updateCreator("syssa");

        // 认证中心
        var identityApp = new ApplicationEntity();
        identityApp.setId("v788o67covIaDMn67mN");
        identityApp.setCode("central-identity");
        identityApp.setName("认证中心");
        identityApp.setLogoBytes(IOStreamx.readBytes(Thread.currentThread().getContextClassLoader().getResourceAsStream("central/logo/central-identity.png")));
        identityApp.setUrl("http://central-identity");
        identityApp.setContextPath("/identity");
        identityApp.setSecret("AkJSi2kmH7vSO5lJcvY");
        identityApp.setEnabled(Boolean.TRUE);
        identityApp.setRemark("用于统一管理全局会话");
        identityApp.setRoutesJson("[]");
        identityApp.updateCreator("syssa");

        // 存储中心
        var storageApp = new ApplicationEntity();
        storageApp.setId("hrnxjZFkYK4P34EZBy2");
        storageApp.setCode("central-storage");
        storageApp.setName("存储中心");
        storageApp.setLogoBytes(IOStreamx.readBytes(Thread.currentThread().getContextClassLoader().getResourceAsStream("central/logo/central-security.png")));
        storageApp.setUrl("http://central-storage");
        storageApp.setContextPath("/storage");
        storageApp.setSecret("IKJuAKOiG7Vx9tMMqUf");
        storageApp.setEnabled(Boolean.TRUE);
        storageApp.setRemark("用于统一管理文件存储");
        storageApp.setRoutesJson("[]");
        storageApp.updateCreator("syssa");

        // 广播中心
        var multicastApp = new ApplicationEntity();
        multicastApp.setId("5HjlG6lusTufjoRapw");
        multicastApp.setCode("central-multicast");
        multicastApp.setName("广播中心");
        multicastApp.setLogoBytes(IOStreamx.readBytes(Thread.currentThread().getContextClassLoader().getResourceAsStream("central/logo/central-security.png")));
        multicastApp.setUrl("http://central-multicast");
        multicastApp.setContextPath("/multicast");
        multicastApp.setSecret("QHO85BzFQq8hqDzREJ");
        multicastApp.setEnabled(Boolean.TRUE);
        multicastApp.setRemark("用于统一消息推送");
        multicastApp.setRoutesJson("[]");
        multicastApp.updateCreator("syssa");

        // 网关中心
        var gatewayApp = new ApplicationEntity();
        gatewayApp.setId("4yus43AGTA7kzyrDbbG");
        gatewayApp.setCode("central-gateway");
        gatewayApp.setName("网关中心");
        gatewayApp.setLogoBytes(IOStreamx.readBytes(Thread.currentThread().getContextClassLoader().getResourceAsStream("central/logo/central-security.png")));
        gatewayApp.setUrl("http://central-gateway");
        gatewayApp.setContextPath("/gateway");
        gatewayApp.setSecret("TT6cdfpHIPastoipuDg");
        gatewayApp.setEnabled(Boolean.TRUE);
        gatewayApp.setRemark("用于统一管理网关");
        gatewayApp.setRoutesJson("[]");
        gatewayApp.updateCreator("syssa");

        var applications = List.of(dashboardApp, saasApp, identityApp, storageApp, multicastApp, gatewayApp);

        var applicationMapper = this.executor.getMapper(ApplicationMapper.class);
        applicationMapper.insertBatch(applications);

        return applications.stream().collect(Collectors.toMap(ApplicationEntity::getCode, Function.identity()));
    }

    /**
     * 初始化主数据库信息
     */
    private DatabaseEntity initDatabase(Map<String, ApplicationEntity> applications) {
        // 初始化主数据库信息
        var masterDatabase = new DatabaseEntity();
        masterDatabase.setId("OIJN8cob9iZzJ1NigWE");
        masterDatabase.setApplicationId(applications.get("central-saas").getId());
        masterDatabase.setCode("master");
        masterDatabase.setName("主数据源");
        masterDatabase.setType(DatabaseType.MYSQL.getValue());
        masterDatabase.setEnabled(Boolean.TRUE);
        masterDatabase.setRemark("本数据源是通过配置文件初始化，请勿修改");
        masterDatabase.setMasterJson("{}");
        masterDatabase.setSlavesJson("[]");
        masterDatabase.setParams(Jsonx.Default().serialize(Map.of(
                "master", "{}",
                "slaves", List.of()
        )));
        masterDatabase.setTenantCode("master");
        masterDatabase.updateCreator("syssa");

        var databaseMapper = executor.getMapper(DatabaseMapper.class);
        databaseMapper.insert(masterDatabase);

        return masterDatabase;
    }

    /**
     * 初始化主租户数据
     */
    private TenantEntity initTenant(DatabaseEntity database) {

        var masterTenant = new TenantEntity();
        masterTenant.setId("2Hp475EmG4I6sNN0dUJ");
        masterTenant.setCode("master");
        masterTenant.setName("主租户");
        masterTenant.setDatabaseId(database.getId());
        masterTenant.setEnabled(Boolean.TRUE);
        masterTenant.setRemark("本租户主要用于管理基础软硬件及其他租户数据");
        masterTenant.updateCreator("syssa");

        var tenantMapper = this.executor.getMapper(TenantMapper.class);
        tenantMapper.insert(masterTenant);

        return masterTenant;
    }

    /**
     * 初始化租户与应用的租用关系
     */
    private void initRelation(TenantEntity masterTenant, Map<String, ApplicationEntity> applications) {
        var dashboardAppRel = new TenantApplicationEntity();
        dashboardAppRel.setId("95AELSyhLQy5kDheAxQ");
        dashboardAppRel.setTenantId(masterTenant.getId());
        dashboardAppRel.setApplicationId(applications.get("central-dashboard").getId());
        dashboardAppRel.setEnabled(Boolean.TRUE);
        dashboardAppRel.setPrimary(Boolean.FALSE);
        dashboardAppRel.updateCreator("syssa");

        var tenantAppRel = new TenantApplicationEntity();
        tenantAppRel.setId("2B4wrNPDL80tfvSE8ve");
        tenantAppRel.setTenantId(masterTenant.getId());
        tenantAppRel.setApplicationId(applications.get("central-saas").getId());
        tenantAppRel.setEnabled(Boolean.TRUE);
        tenantAppRel.setPrimary(Boolean.FALSE);
        tenantAppRel.updateCreator("syssa");

        var identityAppRel = new TenantApplicationEntity();
        identityAppRel.setId("jlUpCMrWWhUNhWHiBh5");
        identityAppRel.setTenantId(masterTenant.getId());
        identityAppRel.setApplicationId(applications.get("central-identity").getId());
        identityAppRel.setEnabled(Boolean.TRUE);
        identityAppRel.setPrimary(Boolean.TRUE);
        identityAppRel.updateCreator("syssa");

        var storageAppRel = new TenantApplicationEntity();
        storageAppRel.setId("JUWCjb5i032vOjbDYLs");
        storageAppRel.setTenantId(masterTenant.getId());
        storageAppRel.setApplicationId(applications.get("central-storage").getId());
        storageAppRel.setEnabled(Boolean.TRUE);
        storageAppRel.setPrimary(Boolean.FALSE);
        storageAppRel.updateCreator("syssa");

        var multicastAppRel = new TenantApplicationEntity();
        multicastAppRel.setId("pDaMEd4CZPupGLSqvc");
        multicastAppRel.setTenantId(masterTenant.getId());
        multicastAppRel.setApplicationId(applications.get("central-multicast").getId());
        multicastAppRel.setEnabled(Boolean.TRUE);
        multicastAppRel.setPrimary(Boolean.FALSE);
        multicastAppRel.updateCreator("syssa");

        var gatewayAppRel = new TenantApplicationEntity();
        gatewayAppRel.setId("L1kpkzehI9oofBw2EbO");
        gatewayAppRel.setTenantId(masterTenant.getId());
        gatewayAppRel.setApplicationId(applications.get("central-gateway").getId());
        gatewayAppRel.setEnabled(Boolean.TRUE);
        gatewayAppRel.setPrimary(Boolean.FALSE);
        gatewayAppRel.updateCreator("syssa");

        var relMapper = this.executor.getMapper(TenantApplicationMapper.class);
        relMapper.insertBatch(List.of(dashboardAppRel, tenantAppRel, identityAppRel, storageAppRel, multicastAppRel, gatewayAppRel));
    }
}
