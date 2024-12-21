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

package central.studio.provider.database.persistence.authority.entity;

import central.bean.Tenantable;
import central.data.authority.RoleRangeInput;
import central.data.authority.option.RangeCategory;
import central.data.authority.option.RangeType;
import central.data.organization.Account;
import central.data.organization.Area;
import central.data.organization.Department;
import central.data.organization.Unit;
import central.sql.data.Entity;
import central.validation.Enums;
import central.validation.Label;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * Role Range Relation
 * <p>
 * 角色与范围关联关系
 *
 * @author Alan Yeh
 * @since 2022/09/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "X_AUTH_ROLE_RANGE")
@EqualsAndHashCode(callSuper = true)
public class RoleRangeEntity extends Entity implements Tenantable {
    @Serial
    private static final long serialVersionUID = 2400532317294500859L;

    @Id
    @Label("主键")
    @Size(max = 32)
    private String id;

    @Label("应用主键")
    @NotBlank
    @Size(min = 1, max = 32)
    private String applicationId;

    @Label("角色")
    @NotBlank
    @Size(min = 1, max = 32)
    private String roleId;

    /**
     * 如果数据与组织架构相关，则使用 {@link RangeCategory#ORGANIZATION }，通过授权组织架构来授权数据范围
     * <p>
     * 如果想直接授权数据，则使用 {@link RangeCategory#DATA}
     */
    @Label("范围分类")
    @NotBlank
    @Enums(RangeCategory.class)
    private String category;

    /**
     * 如果授权分类是 {@link RangeCategory#ORGANIZATION} 时，type 必须是 {@link RangeType} 的枚举值
     * <p>
     * 如果授权分类是 {@link RangeCategory#DATA} 时，则需要开发者自行定义数据类型标识
     */
    @Label("授权数据类型")
    @NotBlank
    private String type;

    /**
     * 如果授权分类是 {@link RangeCategory#ORGANIZATION} 时，则需要根据授权数据类型决定
     *
     * <ul>
     *    <li>{@link Account#getId()}
     *       <ul>
     *          <li>{@link RangeType#ACCOUNT}</li>
     *          <li>{@link RangeType#ACCOUNT_DEPARTMENT}</li>
     *          <li>{@link RangeType#ACCOUNT_DEPARTMENT_RECURSION}</li>
     *          <li>{@link RangeType#ACCOUNT_UNIT}</li>
     *          <li>{@link RangeType#ACCOUNT_UNIT_RECURSION}</li>
     *       </ul>
     *    </li>
     *    <li>{@link Department#getId()}
     *       <ul>
     *          <li>{@link RangeType#DEPARTMENT}</li>
     *          <li>{@link RangeType#DEPARTMENT_RECURSION}</li>
     *       </ul>
     *    </li>
     *    <li>{@link Unit#getId()}
     *       <ul>
     *          <li>{@link RangeType#UNIT}</li>
     *          <li>{@link RangeType#UNIT_RECURSION}</li>
     *       </ul>
     *    </li>
     *    <li>{@link Area#getId()}
     *       <ul>
     *          <li>{@link RangeType#AREA}</li>
     *          <li>{@link RangeType#AREA_RECURSION}</li>
     *       </ul>
     *    </li>
     * </ul>
     * <p>
     * 如果授权分类是 {@link RangeCategory#DATA} 时，则为待授权的数据的主键
     */
    @Label("数据主键")
    @NotBlank
    private String dataId;

    @Label("租户标识")
    @NotBlank
    @Size(min = 1, max = 32)
    private String tenantCode;

    public void fromInput(RoleRangeInput input) {
        this.setApplicationId(input.getApplicationId());
        this.setRoleId(input.getRoleId());
        this.setCategory(input.getCategory());
        this.setType(input.getType());
        this.setDataId(input.getDataId());
    }
}
