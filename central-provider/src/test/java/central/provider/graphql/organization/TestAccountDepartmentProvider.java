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

package central.provider.graphql.organization;

import central.provider.organization.AccountDepartmentProvider;
import central.data.organization.AccountDepartment;
import central.data.organization.AccountDepartmentInput;
import central.data.organization.option.AreaType;
import central.provider.ApplicationProperties;
import central.provider.ProviderApplication;
import central.provider.graphql.TestProvider;
import central.provider.graphql.organization.entity.*;
import central.provider.graphql.organization.mapper.*;
import central.sql.query.Conditions;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Account Department Provider Test Cases
 * 帐户与部门关联关系
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestAccountDepartmentProvider extends TestProvider {
    @Setter(onMethod_ = @Autowired)
    private AccountDepartmentProvider provider;

    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    @Setter(onMethod_ = @Autowired)
    private AreaMapper areaMapper;

    @Setter(onMethod_ = @Autowired)
    private UnitMapper unitMapper;

    @Setter(onMethod_ = @Autowired)
    private DepartmentMapper departmentMapper;

    @Setter(onMethod_ = @Autowired)
    private PostMapper postMapper;

    @Setter(onMethod_ = @Autowired)
    private AccountMapper accountMapper;

    @Setter(onMethod_ = @Autowired)
    private AccountDepartmentMapper relMapper;

    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空数据
        areaMapper.deleteAll();
        unitMapper.deleteAll();
        departmentMapper.deleteAll();
        postMapper.deleteAll();
        accountMapper.deleteAll();
        relMapper.deleteAll();
    }


    /**
     * @see AccountDepartmentProvider#findById
     */
    @Test
    public void case1() {
        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("86");
        areaEntity.setName("中国");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        this.areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("1000101");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        var postEntity = new PostEntity();
        postEntity.setUnitId(unitEntity.getId());
        postEntity.setCode("10000");
        postEntity.setName("测试职务");
        postEntity.setOrder(0);
        postEntity.setTenantCode("master");
        postEntity.updateCreator(properties.getSupervisor().getUsername());
        this.postMapper.insert(postEntity);

        var accountEntity = new AccountEntity();
        accountEntity.setUsername("zhangs");
        accountEntity.setEmail("zhangs@central-x.com");
        accountEntity.setMobile("18888888888");
        accountEntity.setName("张三");
        accountEntity.setAvatar("1234");
        accountEntity.setAdmin(Boolean.FALSE);
        accountEntity.setEnabled(Boolean.TRUE);
        accountEntity.setDeleted(Boolean.FALSE);
        accountEntity.setTenantCode("master");
        accountEntity.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(accountEntity);

        var relEntity = new AccountDepartmentEntity();
        relEntity.setAccountId(accountEntity.getId());
        relEntity.setUnitId(unitEntity.getId());
        relEntity.setDepartmentId(departmentEntity.getId());
        relEntity.setPostId(postEntity.getId());
        relEntity.setPrimary(Boolean.TRUE);
        relEntity.setTenantCode("master");
        relEntity.updateCreator(properties.getSupervisor().getUsername());
        this.relMapper.insert(relEntity);

        // 查询数据
        var rel = this.provider.findById(relEntity.getId());
        assertNotNull(rel);
        assertEquals(relEntity.getId(), rel.getId());
        // 关联查询
        assertNotNull(rel.getAccount());
        assertEquals(accountEntity.getId(), rel.getAccount().getId());
        // 关联查询
        assertNotNull(rel.getUnit());
        assertEquals(unitEntity.getId(), rel.getUnit().getId());
        // 关联查询
        assertNotNull(rel.getDepartment());
        assertNotNull(departmentEntity.getId(), rel.getDepartment().getId());
        // 关联查询
        assertNotNull(rel.getPost());
        assertEquals(postEntity.getId(), rel.getPost().getId());
        // 关联查询
        assertNotNull(rel.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), rel.getCreator().getId());
    }

    /**
     * @see AccountDepartmentProvider#findByIds
     */
    @Test
    public void case2() {
        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("86");
        areaEntity.setName("中国");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        this.areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("1000101");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        var postEntity = new PostEntity();
        postEntity.setUnitId(unitEntity.getId());
        postEntity.setCode("10000");
        postEntity.setName("测试职务");
        postEntity.setOrder(0);
        postEntity.setTenantCode("master");
        postEntity.updateCreator(properties.getSupervisor().getUsername());
        this.postMapper.insert(postEntity);

        var accountEntity = new AccountEntity();
        accountEntity.setUsername("zhangs");
        accountEntity.setEmail("zhangs@central-x.com");
        accountEntity.setMobile("18888888888");
        accountEntity.setName("张三");
        accountEntity.setAvatar("1234");
        accountEntity.setAdmin(Boolean.FALSE);
        accountEntity.setEnabled(Boolean.TRUE);
        accountEntity.setDeleted(Boolean.FALSE);
        accountEntity.setTenantCode("master");
        accountEntity.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(accountEntity);

        var relEntity = new AccountDepartmentEntity();
        relEntity.setAccountId(accountEntity.getId());
        relEntity.setUnitId(unitEntity.getId());
        relEntity.setDepartmentId(departmentEntity.getId());
        relEntity.setPostId(postEntity.getId());
        relEntity.setPrimary(Boolean.TRUE);
        relEntity.setTenantCode("master");
        relEntity.updateCreator(properties.getSupervisor().getUsername());
        this.relMapper.insert(relEntity);

        // 查询数据
        var rels = this.provider.findByIds(List.of(relEntity.getId()));
        assertNotNull(rels);
        assertEquals(1, rels.size());

        var rel = Listx.getFirstOrNull(rels);
        assertNotNull(rel);
        assertEquals(relEntity.getId(), rel.getId());
        // 关联查询
        assertNotNull(rel.getAccount());
        assertEquals(accountEntity.getId(), rel.getAccount().getId());
        // 关联查询
        assertNotNull(rel.getUnit());
        assertEquals(unitEntity.getId(), rel.getUnit().getId());
        // 关联查询
        assertNotNull(rel.getDepartment());
        assertNotNull(departmentEntity.getId(), rel.getDepartment().getId());
        // 关联查询
        assertNotNull(rel.getPost());
        assertEquals(postEntity.getId(), rel.getPost().getId());
        // 关联查询
        assertNotNull(rel.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), rel.getCreator().getId());
    }

    /**
     * @see AccountDepartmentProvider#findBy
     */
    @Test
    public void case3() {
        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("86");
        areaEntity.setName("中国");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        this.areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("1000101");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        var postEntity = new PostEntity();
        postEntity.setUnitId(unitEntity.getId());
        postEntity.setCode("10000");
        postEntity.setName("测试职务");
        postEntity.setOrder(0);
        postEntity.setTenantCode("master");
        postEntity.updateCreator(properties.getSupervisor().getUsername());
        this.postMapper.insert(postEntity);

        var accountEntity = new AccountEntity();
        accountEntity.setUsername("zhangs");
        accountEntity.setEmail("zhangs@central-x.com");
        accountEntity.setMobile("18888888888");
        accountEntity.setName("张三");
        accountEntity.setAvatar("1234");
        accountEntity.setAdmin(Boolean.FALSE);
        accountEntity.setEnabled(Boolean.TRUE);
        accountEntity.setDeleted(Boolean.FALSE);
        accountEntity.setTenantCode("master");
        accountEntity.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(accountEntity);

        var relEntity = new AccountDepartmentEntity();
        relEntity.setAccountId(accountEntity.getId());
        relEntity.setUnitId(unitEntity.getId());
        relEntity.setDepartmentId(departmentEntity.getId());
        relEntity.setPostId(postEntity.getId());
        relEntity.setPrimary(Boolean.TRUE);
        relEntity.setTenantCode("master");
        relEntity.updateCreator(properties.getSupervisor().getUsername());
        this.relMapper.insert(relEntity);

        // 查询数据
        var rels = this.provider.findBy(null, null, Conditions.of(AccountDepartment.class).eq(AccountDepartment::getId, relEntity.getId()), null);
        assertNotNull(rels);
        assertEquals(1, rels.size());

        var rel = Listx.getFirstOrNull(rels);
        assertNotNull(rel);
        assertEquals(relEntity.getId(), rel.getId());
        // 关联查询
        assertNotNull(rel.getAccount());
        assertEquals(accountEntity.getId(), rel.getAccount().getId());
        // 关联查询
        assertNotNull(rel.getUnit());
        assertEquals(unitEntity.getId(), rel.getUnit().getId());
        // 关联查询
        assertNotNull(rel.getDepartment());
        assertNotNull(departmentEntity.getId(), rel.getDepartment().getId());
        // 关联查询
        assertNotNull(rel.getPost());
        assertEquals(postEntity.getId(), rel.getPost().getId());
        // 关联查询
        assertNotNull(rel.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), rel.getCreator().getId());
    }

    /**
     * @see AccountDepartmentProvider#pageBy
     */
    @Test
    public void case4() {
        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("86");
        areaEntity.setName("中国");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        this.areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("1000101");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        var postEntity = new PostEntity();
        postEntity.setUnitId(unitEntity.getId());
        postEntity.setCode("10000");
        postEntity.setName("测试职务");
        postEntity.setOrder(0);
        postEntity.setTenantCode("master");
        postEntity.updateCreator(properties.getSupervisor().getUsername());
        this.postMapper.insert(postEntity);

        var accountEntity = new AccountEntity();
        accountEntity.setUsername("zhangs");
        accountEntity.setEmail("zhangs@central-x.com");
        accountEntity.setMobile("18888888888");
        accountEntity.setName("张三");
        accountEntity.setAvatar("1234");
        accountEntity.setAdmin(Boolean.FALSE);
        accountEntity.setEnabled(Boolean.TRUE);
        accountEntity.setDeleted(Boolean.FALSE);
        accountEntity.setTenantCode("master");
        accountEntity.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(accountEntity);

        var relEntity = new AccountDepartmentEntity();
        relEntity.setAccountId(accountEntity.getId());
        relEntity.setUnitId(unitEntity.getId());
        relEntity.setDepartmentId(departmentEntity.getId());
        relEntity.setPostId(postEntity.getId());
        relEntity.setPrimary(Boolean.TRUE);
        relEntity.setTenantCode("master");
        relEntity.updateCreator(properties.getSupervisor().getUsername());
        this.relMapper.insert(relEntity);

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(AccountDepartment.class).eq(AccountDepartment::getId, relEntity.getId()), null);
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1L, page.getPager().getPageIndex());
        assertEquals(20L, page.getPager().getPageSize());
        assertEquals(1L, page.getPager().getPageCount());
        assertEquals(1L, page.getPager().getItemCount());
        assertNotNull(page.getData());

        var rel = Listx.getFirstOrNull(page.getData());
        assertNotNull(rel);
        assertEquals(relEntity.getId(), rel.getId());
        // 关联查询
        assertNotNull(rel.getAccount());
        assertEquals(accountEntity.getId(), rel.getAccount().getId());
        // 关联查询
        assertNotNull(rel.getUnit());
        assertEquals(unitEntity.getId(), rel.getUnit().getId());
        // 关联查询
        assertNotNull(rel.getDepartment());
        assertNotNull(departmentEntity.getId(), rel.getDepartment().getId());
        // 关联查询
        assertNotNull(rel.getPost());
        assertEquals(postEntity.getId(), rel.getPost().getId());
        // 关联查询
        assertNotNull(rel.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), rel.getCreator().getId());
    }

    /**
     * @see AccountDepartmentProvider#countBy
     */
    @Test
    public void case5() {
        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("86");
        areaEntity.setName("中国");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        this.areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("1000101");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        var postEntity = new PostEntity();
        postEntity.setUnitId(unitEntity.getId());
        postEntity.setCode("10000");
        postEntity.setName("测试职务");
        postEntity.setOrder(0);
        postEntity.setTenantCode("master");
        postEntity.updateCreator(properties.getSupervisor().getUsername());
        this.postMapper.insert(postEntity);

        var accountEntity = new AccountEntity();
        accountEntity.setUsername("zhangs");
        accountEntity.setEmail("zhangs@central-x.com");
        accountEntity.setMobile("18888888888");
        accountEntity.setName("张三");
        accountEntity.setAvatar("1234");
        accountEntity.setAdmin(Boolean.FALSE);
        accountEntity.setEnabled(Boolean.TRUE);
        accountEntity.setDeleted(Boolean.FALSE);
        accountEntity.setTenantCode("master");
        accountEntity.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(accountEntity);

        var relEntity = new AccountDepartmentEntity();
        relEntity.setAccountId(accountEntity.getId());
        relEntity.setUnitId(unitEntity.getId());
        relEntity.setDepartmentId(departmentEntity.getId());
        relEntity.setPostId(postEntity.getId());
        relEntity.setPrimary(Boolean.TRUE);
        relEntity.setTenantCode("master");
        relEntity.updateCreator(properties.getSupervisor().getUsername());
        this.relMapper.insert(relEntity);

        // 查询数据
        var count = this.provider.countBy(Conditions.of(AccountDepartment.class).eq(AccountDepartment::getId, relEntity.getId()));
        assertNotNull(count);
        assertEquals(1, count);
    }

    /**
     * @see AccountDepartmentProvider#insert
     */
    @Test
    public void case6() {
        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("86");
        areaEntity.setName("中国");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        this.areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("1000101");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        var postEntity = new PostEntity();
        postEntity.setUnitId(unitEntity.getId());
        postEntity.setCode("10000");
        postEntity.setName("测试职务");
        postEntity.setOrder(0);
        postEntity.setTenantCode("master");
        postEntity.updateCreator(properties.getSupervisor().getUsername());
        this.postMapper.insert(postEntity);

        var accountEntity = new AccountEntity();
        accountEntity.setUsername("zhangs");
        accountEntity.setEmail("zhangs@central-x.com");
        accountEntity.setMobile("18888888888");
        accountEntity.setName("张三");
        accountEntity.setAvatar("1234");
        accountEntity.setAdmin(Boolean.FALSE);
        accountEntity.setEnabled(Boolean.TRUE);
        accountEntity.setDeleted(Boolean.FALSE);
        accountEntity.setTenantCode("master");
        accountEntity.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(accountEntity);

        var input = AccountDepartmentInput.builder()
                .accountId(accountEntity.getId())
                .unitId(unitEntity.getId())
                .departmentId(departmentEntity.getId())
                .postId(postEntity.getId())
                .primary(Boolean.TRUE)
                .build();

        var rel = this.provider.insert(input, properties.getSupervisor().getUsername());
        assertNotNull(rel);
        assertNotNull(rel.getId());

        assertTrue(this.relMapper.existsBy(Conditions.of(AccountDepartmentEntity.class).eq(AccountDepartmentEntity::getId, rel.getId())));
    }

    /**
     * @see AccountDepartmentProvider#insertBatch
     */
    @Test
    public void case7() {
        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("86");
        areaEntity.setName("中国");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        this.areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("1000101");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        var postEntity = new PostEntity();
        postEntity.setUnitId(unitEntity.getId());
        postEntity.setCode("10000");
        postEntity.setName("测试职务");
        postEntity.setOrder(0);
        postEntity.setTenantCode("master");
        postEntity.updateCreator(properties.getSupervisor().getUsername());
        this.postMapper.insert(postEntity);

        var accountEntity = new AccountEntity();
        accountEntity.setUsername("zhangs");
        accountEntity.setEmail("zhangs@central-x.com");
        accountEntity.setMobile("18888888888");
        accountEntity.setName("张三");
        accountEntity.setAvatar("1234");
        accountEntity.setAdmin(Boolean.FALSE);
        accountEntity.setEnabled(Boolean.TRUE);
        accountEntity.setDeleted(Boolean.FALSE);
        accountEntity.setTenantCode("master");
        accountEntity.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(accountEntity);

        var input = AccountDepartmentInput.builder()
                .accountId(accountEntity.getId())
                .unitId(unitEntity.getId())
                .departmentId(departmentEntity.getId())
                .postId(postEntity.getId())
                .primary(Boolean.TRUE)
                .build();

        var rels = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(rels);
        assertEquals(1, rels.size());

        var rel = Listx.getFirstOrNull(rels);
        assertNotNull(rel);
        assertNotNull(rel.getId());

        assertTrue(this.relMapper.existsBy(Conditions.of(AccountDepartmentEntity.class).eq(AccountDepartmentEntity::getId, rel.getId())));
    }

    /**
     * @see AccountDepartmentProvider#deleteByIds
     */
    @Test
    public void case8() {
        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("86");
        areaEntity.setName("中国");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        this.areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("1000101");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        var postEntity = new PostEntity();
        postEntity.setUnitId(unitEntity.getId());
        postEntity.setCode("10000");
        postEntity.setName("测试职务");
        postEntity.setOrder(0);
        postEntity.setTenantCode("master");
        postEntity.updateCreator(properties.getSupervisor().getUsername());
        this.postMapper.insert(postEntity);

        var accountEntity = new AccountEntity();
        accountEntity.setUsername("zhangs");
        accountEntity.setEmail("zhangs@central-x.com");
        accountEntity.setMobile("18888888888");
        accountEntity.setName("张三");
        accountEntity.setAvatar("1234");
        accountEntity.setAdmin(Boolean.FALSE);
        accountEntity.setEnabled(Boolean.TRUE);
        accountEntity.setDeleted(Boolean.FALSE);
        accountEntity.setTenantCode("master");
        accountEntity.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(accountEntity);

        var relEntity = new AccountDepartmentEntity();
        relEntity.setAccountId(accountEntity.getId());
        relEntity.setUnitId(unitEntity.getId());
        relEntity.setDepartmentId(departmentEntity.getId());
        relEntity.setPostId(postEntity.getId());
        relEntity.setPrimary(Boolean.TRUE);
        relEntity.setTenantCode("master");
        relEntity.updateCreator(properties.getSupervisor().getUsername());
        this.relMapper.insert(relEntity);

        var deleted = this.provider.deleteByIds(List.of(relEntity.getId()));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.relMapper.existsBy(Conditions.of(AccountDepartmentEntity.class).eq(AccountDepartmentEntity::getId, relEntity.getId())));
    }

    /**
     * @see AccountDepartmentProvider#deleteBy
     */
    @Test
    public void case9() {
        var areaEntity = new AreaEntity();
        areaEntity.setParentId("");
        areaEntity.setCode("86");
        areaEntity.setName("中国");
        areaEntity.setType(AreaType.COUNTRY.getValue());
        areaEntity.setOrder(0);
        areaEntity.setTenantCode("master");
        areaEntity.updateCreator(properties.getSupervisor().getUsername());
        this.areaMapper.insert(areaEntity);

        var unitEntity = new UnitEntity();
        unitEntity.setParentId("");
        unitEntity.setAreaId(areaEntity.getId());
        unitEntity.setCode("10001");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("1000101");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        var postEntity = new PostEntity();
        postEntity.setUnitId(unitEntity.getId());
        postEntity.setCode("10000");
        postEntity.setName("测试职务");
        postEntity.setOrder(0);
        postEntity.setTenantCode("master");
        postEntity.updateCreator(properties.getSupervisor().getUsername());
        this.postMapper.insert(postEntity);

        var accountEntity = new AccountEntity();
        accountEntity.setUsername("zhangs");
        accountEntity.setEmail("zhangs@central-x.com");
        accountEntity.setMobile("18888888888");
        accountEntity.setName("张三");
        accountEntity.setAvatar("1234");
        accountEntity.setAdmin(Boolean.FALSE);
        accountEntity.setEnabled(Boolean.TRUE);
        accountEntity.setDeleted(Boolean.FALSE);
        accountEntity.setTenantCode("master");
        accountEntity.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(accountEntity);

        var relEntity = new AccountDepartmentEntity();
        relEntity.setAccountId(accountEntity.getId());
        relEntity.setUnitId(unitEntity.getId());
        relEntity.setDepartmentId(departmentEntity.getId());
        relEntity.setPostId(postEntity.getId());
        relEntity.setPrimary(Boolean.TRUE);
        relEntity.setTenantCode("master");
        relEntity.updateCreator(properties.getSupervisor().getUsername());
        this.relMapper.insert(relEntity);

        var deleted = this.provider.deleteBy(Conditions.of(AccountDepartment.class).eq(AccountDepartment::getId, relEntity.getId()));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.relMapper.existsBy(Conditions.of(AccountDepartmentEntity.class).eq(AccountDepartmentEntity::getId, relEntity.getId())));
    }
}
