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

import central.bean.InitializeException;
import central.data.authority.MenuInput;
import central.data.authority.PermissionInput;
import central.io.IOStreamx;
import central.lang.reflect.TypeRef;
import central.sql.SqlExecutor;
import central.studio.provider.graphql.authority.entity.MenuEntity;
import central.studio.provider.graphql.authority.entity.PermissionEntity;
import central.studio.provider.graphql.authority.mapper.MenuMapper;
import central.studio.provider.graphql.authority.mapper.PermissionMapper;
import central.util.Guidx;
import central.util.Jsonx;
import central.util.Listx;
import central.util.Mapx;
import central.validation.Label;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 应用数据初始化器
 *
 * @author Alan Yeh
 * @since 2024/06/22
 */
@RequiredArgsConstructor
public class ApplicationInitializer {
    private final SqlExecutor executor;

    /**
     * 初始化
     *
     * @param tenant        租户标识
     * @param applicationId 应用主键
     */
    public void initialize(String tenant, String applicationId) {
        // 初始化菜单数据
        this.initMenus(tenant, applicationId);
    }

    /**
     * 初始化菜单
     */
    private void initMenus(String tenant, String applicationId) {
        var menuMapper = executor.getMapper(MenuMapper.class);
        var permissionMapper = executor.getMapper(PermissionMapper.class);

        // 使用 JSON 来初始化通用菜单
        var resources = List.of(
                new ClassPathResource("/central/database/initialization/menus.authority.json"),
                new ClassPathResource("/central/database/initialization/menus.system.json")
        );

        for (var resource : resources) {
            List<MenuConfig> configs;
            try (var stream = resource.getInputStream()) {
                configs = Jsonx.Default().deserialize(IOStreamx.readText(stream, StandardCharsets.UTF_8), TypeRef.ofList(MenuConfig.class));
            } catch (IOException ex) {
                throw new InitializeException("读取资源异常: " + ex.getLocalizedMessage(), ex);
            }

            // 将菜单配置变成菜单输入，并保存到数据
            var inputs = buildInput(applicationId, "", configs);

            var menus = inputs.stream().map(input -> {
                var entity = new MenuEntity();
                entity.fromInput(input);
                entity.setTenantCode(tenant);
                entity.updateCreator("syssa");
                return entity;
            }).toList();
            menuMapper.insertBatch(menus);

            // 保存权限
            var permissions = inputs.stream().flatMap(input -> {
                var result = new ArrayList<PermissionEntity>();
                if (Listx.isNotEmpty(input.getPermissions())) {
                    for (var permission : input.getPermissions()) {
                        var entity = new PermissionEntity();
                        entity.fromInput(permission);
                        entity.setTenantCode(tenant);
                        entity.updateCreator("syssa");
                        result.add(entity);
                    }
                }
                return result.stream();
            }).toList();
            permissionMapper.insertBatch(permissions);
        }
    }

    /**
     * 构建菜单输入
     *
     * @param applicationId 应用
     * @param parentId      父菜单
     * @param configs       菜单配置
     */
    private List<MenuInput> buildInput(@Nonnull String applicationId, @Nullable String parentId, @Nonnull List<MenuConfig> configs) {
        var result = new ArrayList<MenuInput>();

        for (var config : configs) {
            // 解析菜单配置
            var menu = MenuInput.builder()
                    .id(Guidx.nextID())
                    .applicationId(applicationId)
                    .parentId(parentId)
                    .code(config.getCode())
                    .name(config.getName())
                    .icon(config.getIcon())
                    .url(config.getUrl())
                    .type(config.getType())
                    .enabled(config.getEnabled())
                    .order(config.getOrder())
                    .remark("")
                    .permissions(new ArrayList<>())
                    .build();
            // 解析权限配置
            if (Mapx.isNotEmpty(config.getPermissions())) {
                for (var entry : config.getPermissions().entrySet()) {
                    var permission = PermissionInput.builder()
                            .id(Guidx.nextID())
                            .applicationId(applicationId)
                            .menuId(menu.getId())
                            .code(entry.getKey())
                            .name(entry.getValue())
                            .build();
                    menu.getPermissions().add(permission);
                }
            }

            if (Listx.isNotEmpty(config.getChildren())) {
                var children = this.buildInput(applicationId, menu.getId(), config.getChildren());
                result.addAll(children);
            }

            result.add(menu);
        }

        return result;
    }

    /**
     * 菜单配置实体
     */
    @Data
    private static class MenuConfig implements Serializable {
        @Serial
        private static final long serialVersionUID = 1591392167398097579L;

        @Label("标识")
        private String code;

        @Label("名称")
        private String name;

        @Label("图标")
        private String icon;

        @Label("地址")
        private String url;

        @Label("类型")
        private String type;

        @Label("是否启用")
        private Boolean enabled;

        @Label("排序号")
        private Integer order;

        @Label("权限")
        private Map<String, String> permissions;

        @Label("子菜单")
        private List<MenuConfig> children;
    }
}
