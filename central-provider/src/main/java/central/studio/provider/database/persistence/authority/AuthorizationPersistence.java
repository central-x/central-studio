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

package central.studio.provider.database.persistence.authority;

import central.data.saas.Application;
import central.lang.Stringx;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Columns;
import central.sql.query.Conditions;
import central.studio.provider.ProviderProperties;
import central.studio.provider.database.persistence.authority.entity.MenuEntity;
import central.studio.provider.database.persistence.authority.entity.PermissionEntity;
import central.studio.provider.database.persistence.authority.entity.RoleEntity;
import central.studio.provider.database.persistence.organization.AccountPersistence;
import central.studio.provider.graphql.authority.dto.PermissionDTO;
import central.studio.provider.graphql.authority.dto.RoleDTO;
import central.util.Listx;
import central.util.Mapx;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Setter;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Authorization Persistence
 * <p>
 * 授权持久化
 *
 * @author Alan Yeh
 * @since 2024/06/19
 */
@Component
public class AuthorizationPersistence {

    @Setter(onMethod_ = @Autowired)
    private DataContext context;

    @Setter(onMethod_ = @Autowired)
    private AccountPersistence accountPersistence;

    @Setter(onMethod_ = @Autowired)
    private RolePersistence rolePersistence;

    @Setter(onMethod_ = @Autowired)
    private PermissionPersistence permissionPersistence;

    @Setter(onMethod_ = @Autowired)
    private ProviderProperties properties;

    @Setter(onMethod_ = @Autowired)
    private MenuPersistence menuPersistence;

    private SaasContainer getContainer() {
        return context.getData(DataFetcherType.SAAS);
    }

    /**
     * 根据应用标识和应用密钥获取应用信息
     *
     * @param code   应用标识
     * @param secret 应用密钥
     */
    public @Nullable Application findApplication(@Nonnull String code,
                                                 @Nonnull String secret) {
        SaasContainer container = context.getData(DataFetcherType.SAAS);

        var application = container.getApplicationByCode(code);
        if (application == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, MessageFormatter.format("应用[code={}]不存在", code).getMessage());
        }
        if (!Objects.equals(secret, application.getSecret())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MessageFormatter.format("应用[code={}]密钥错误", code).getMessage());
        }
        return application;
    }


    /**
     * 获取指定帐户允许访问的应用列表
     *
     * @param accountId 帐户主键
     * @param type      菜单类型
     * @param tenant    租户标识
     * @return 应用列表
     */
    public @Nonnull List<Application> findApplications(@Nonnull String accountId,
                                                       @Nonnull String type,
                                                       @Nonnull String tenant) {
        var menus = this.findMenus(accountId, type, null, tenant);

        var applicationIds = Listx.asStream(menus).map(MenuEntity::getApplicationId).collect(Collectors.toSet());
        if (applicationIds.isEmpty()) {
            return List.of();
        }

        var container = this.getContainer();
        return applicationIds.stream().map(container::getApplicationById).toList();
    }

    /**
     * 验证应用是否有效
     *
     * @param applicationId 应用主键
     * @param tenantCode    租户
     */
    private void checkApplication(String applicationId, String tenantCode) {
        SaasContainer container = context.getData(DataFetcherType.SAAS);
        var tenant = container.getTenantByCode(tenantCode);
        // 找不到租户
        if (tenant == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("没有找到指定租户[code={}]", tenantCode));
        }
        // 检查应用租用关系
        var relation = tenant.getApplications().stream().filter(it -> Objects.equals(applicationId, it.getApplicationId())).findFirst();
        if (relation.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("在租户[code={}]下没有找到指定应用", tenantCode));
        }
        if (!relation.get().getEnabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("应用[id={}]已禁用", applicationId));
        }

        // 检查应用
        var application = container.getApplicationById(relation.get().getApplicationId());
        if (application == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("应用[id={}]不存在", applicationId));
        }

        if (!application.getEnabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Stringx.format("应用[code={}]已禁用", application.getCode()));
        }
    }

    /**
     * 获取指定用户允许访问的菜单列表
     *
     * @param accountId     帐户主键
     * @param type          菜单类型
     * @param applicationId 应用主键
     * @param tenant        租户标识
     * @return 菜单列表
     */
    public @Nonnull List<MenuEntity> findMenus(String accountId, String type, String applicationId, String tenant) {
        // 先查询用户被授权了哪些权限，再根据权限反推菜单
        var permissions = this.findPermissions(accountId, applicationId, tenant);

        var menus = new ArrayList<MenuEntity>();

        // 保存那些待查询的菜单主键
        var pendingIds = new HashSet<>(permissions.stream().map(PermissionEntity::getMenuId).toList());

        // 循环向上获取菜单，直到没有父级菜单为止
        while (!pendingIds.isEmpty()) {
            var conditions = Conditions.of(MenuEntity.class).in(MenuEntity::getId, pendingIds);
            if (Stringx.isNotBlank(type)) {
                conditions.eq(MenuEntity::getType, type);
            }
            var fetched = menuPersistence.findBy(null, null, Columns.all(), conditions, null, tenant);
            menus.addAll(fetched);

            pendingIds.clear();
            pendingIds.addAll(fetched.stream().map(MenuEntity::getParentId).filter(Stringx::isNotBlank).toList());
        }

        return menus;
    }


    /**
     * 获取指定用户的所有角色
     *
     * @param accountId     帐户主键
     * @param applicationId 应用主键。如果不指定，则查询所有应用下的所有角色
     * @param tenant        租户标识
     * @return 已分配的角色清单
     */
    public @Nonnull List<RoleEntity> findRoles(@Nonnull String accountId,
                                               @Nullable String applicationId,
                                               @Nonnull String tenant) {
        {
            // 检查帐号是否是超级管理员、三员
            // 超级管理员、三员都没有角色
            var account = accountPersistence.findById(accountId, Columns.all(), tenant);
            if (account == null) {
                return Collections.emptyList();
            }
            if (account.getAdmin()) {
                return Collections.emptyList();
            }
        }

        var conditions = Conditions.of(RoleDTO.class).eq(RoleDTO::getEnabled, Boolean.TRUE);

        if (Stringx.isNotBlank(applicationId)) {
            conditions.eq(RoleDTO::getApplicationId, applicationId);
        }

        return rolePersistence.findBy(null, null, Columns.all(), conditions, null, tenant);
    }

    /**
     * 获取指定用户的所有权限
     *
     * @param accountId     帐户主键
     * @param applicationId 应用主键。如果不指定，则查询所有应用下的所有权限
     * @param tenant        租户标识
     * @return 已分配的权限清单
     */
    public @Nonnull List<PermissionEntity> findPermissions(@Nonnull String accountId,
                                                           @Nullable String applicationId,
                                                           @Nonnull String tenant) {
        var conditions = Conditions.of(PermissionDTO.class);

        if (Stringx.isNotBlank(applicationId)) {
            conditions.eq(PermissionDTO::getApplicationId, applicationId);
        }

        {
            // 检查帐号是否是超级管理员、三员
            var account = accountPersistence.findById(accountId, Columns.all(), tenant);
            if (account == null) {
                return Collections.emptyList();
            }
            if (account.getAdmin()) {
                var permissions = permissionPersistence.findBy(null, null, Columns.all(), null, null, tenant);
                // 超级管理员有所有权限
                if (accountPersistence.isSupervisor(account.getId())) {
                    return permissions;
                }

                // 三员根据配置文件返回权限
                var adminProperties = Mapx.asStream(properties.getAdmins()).map(Map.Entry::getValue).filter(it -> it.getUsername().equals(account.getUsername())).findFirst();
                if (adminProperties.isEmpty()) {
                    // 没有匹配上三员
                    // 这个应该是配置错误了
                    return List.of();
                }

                var range = adminProperties.get().getPermissions();
                // TODO 不是使用 contains 判断，应该使用匹配算法
                if (Listx.isNotEmpty(range.getIncludes())) {
                    return permissions.stream().filter(it -> range.getIncludes().contains(it.getCode())).toList();
                } else {
                    return permissions.stream().filter(it -> !range.getExcludes().contains(it.getCode())).toList();
                }
            }
        }

        // 其它帐号根据角色分配权限
        var roles = this.findRoles(accountId, applicationId, tenant);
        if (Listx.isNullOrEmpty(roles)) {
            return new ArrayList<>();
        }
        return this.permissionPersistence.findBy(null, null, Columns.all(), conditions.in("role.id", roles.stream().map(RoleEntity::getId).toList()), null, tenant);
    }
}
