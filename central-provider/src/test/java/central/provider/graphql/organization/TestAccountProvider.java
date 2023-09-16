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

import central.provider.organization.AccountProvider;
import central.bean.Page;
import central.data.organization.Account;
import central.data.organization.AccountInput;
import central.data.organization.option.AreaType;
import central.lang.Stringx;
import central.provider.ApplicationProperties;
import central.provider.ProviderApplication;
import central.provider.graphql.TestProvider;
import central.provider.graphql.organization.entity.*;
import central.provider.graphql.organization.mapper.*;
import central.sql.query.Conditions;
import central.sql.query.Orders;
import central.sql.data.Entity;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Account Provider Test Cases
 * 帐户
 *
 * @author Alan Yeh
 * @since 2022/09/26
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestAccountProvider extends TestProvider {

    @Setter(onMethod_ = @Autowired)
    private AccountProvider provider;

    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    @Setter(onMethod_ = @Autowired)
    private AccountMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private AreaMapper areaMapper;

    @Setter(onMethod_ = @Autowired)
    private UnitMapper unitMapper;

    @Setter(onMethod_ = @Autowired)
    private RankMapper rankMapper;

    @Setter(onMethod_ = @Autowired)
    private PostMapper postMapper;

    @Setter(onMethod_ = @Autowired)
    private DepartmentMapper departmentMapper;

    @Setter(onMethod_ = @Autowired)
    private AccountUnitMapper unitRelMapper;

    @Setter(onMethod_ = @Autowired)
    private AccountDepartmentMapper departmentRelMapper;

    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空数据
        mapper.deleteAll();
        areaMapper.deleteAll();
        unitMapper.deleteAll();
        rankMapper.deleteAll();
        postMapper.deleteAll();
        departmentRelMapper.deleteAll();
        unitRelMapper.deleteAll();
        departmentRelMapper.deleteAll();
    }

    /**
     * @see AccountProvider#findById
     */
    @Test
    public void case1() {
        // 查询超级管理员数据
        var supervisor = this.provider.findById("syssa");
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
     * @see AccountProvider#findByIds
     */
    @Test
    public void case2() {
        var entity = new AccountEntity();
        entity.setUsername("zhangs");
        entity.setEmail("zhangs@central-x.com");
        entity.setMobile("18888888888");
        entity.setName("张三");
        entity.setAvatar("1234");
        entity.setAdmin(Boolean.FALSE);
        entity.setEnabled(Boolean.TRUE);
        entity.setDeleted(Boolean.FALSE);
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var list = this.provider.findByIds(List.of(this.properties.getSupervisor().getUsername(), entity.getId()));
        assertNotNull(list);
        assertEquals(2, list.size());

        // 查询刚才保存的䉤据
        var account = list.stream().filter(it -> Objects.equals(it.getId(), entity.getId())).findFirst().orElse(null);
        assertNotNull(account);
        assertNotNull(account.getId());

        // 关联查询
        assertNotNull(account.getCreator());
        assertEquals(this.properties.getSupervisor().getUsername(), account.getCreator().getId());
        assertNotNull(account.getModifier());
        assertEquals(this.properties.getSupervisor().getUsername(), account.getModifier().getId());
    }

    /**
     * @see AccountProvider#findBy
     */
    @Test
    public void case3() {
        var random = 150;
        var entities = new ArrayList<AccountEntity>(random);
        for (int i = 0; i < random; i++) {
            var entity = new AccountEntity();
            entity.setUsername("zhang" + i);
            entity.setEmail("zhang" + i + "@central-x.com");
            entity.setMobile("18888888" + Stringx.paddingRight(Integer.toString(i), 3, '0'));
            entity.setName("张" + i);
            entity.setAvatar("avatar" + i);
            entity.setAdmin(Boolean.FALSE);
            entity.setEnabled(Boolean.TRUE);
            entity.setDeleted(Boolean.FALSE);
            entity.setTenantCode("master");
            entity.updateCreator(this.properties.getSupervisor().getUsername());
            entities.add(entity);
        }
        this.mapper.insertBatch(entities);

        List<Account> list = this.provider.findBy(2L, 4L, Conditions.of(Account.class).like(Account::getName, "%3"), Orders.of(Account.class).asc(Account::getUsername));
        assertNotNull(list);
        assertEquals(2, list.size());
        assertTrue(list.stream().allMatch(it -> it.getName().endsWith("3")));
    }

    /**
     * @see AccountProvider#findBy
     */
    @Test
    public void case3_1() {
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

        var rankEntity = new RankEntity();
        rankEntity.setUnitId(unitEntity.getId());
        rankEntity.setCode("10000");
        rankEntity.setName("测试职级");
        rankEntity.setOrder(0);
        rankEntity.setTenantCode("master");
        rankEntity.updateCreator(properties.getSupervisor().getUsername());
        this.rankMapper.insert(rankEntity);

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
        this.mapper.insert(accountEntity);

        var unitRelEntity = new AccountUnitEntity();
        unitRelEntity.setAccountId(accountEntity.getId());
        unitRelEntity.setUnitId(unitEntity.getId());
        unitRelEntity.setRankId(rankEntity.getId());
        unitRelEntity.setPrimary(Boolean.TRUE);
        unitRelEntity.setTenantCode("master");
        unitRelEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitRelMapper.insert(unitRelEntity);

        var departmentEntity = new DepartmentEntity();
        departmentEntity.setUnitId(unitEntity.getId());
        departmentEntity.setParentId("");
        departmentEntity.setCode("10086");
        departmentEntity.setName("测试部门");
        departmentEntity.setOrder(0);
        departmentEntity.setTenantCode("master");
        departmentEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentMapper.insert(departmentEntity);

        var postEntity = new PostEntity();
        postEntity.setUnitId(unitEntity.getId());
        postEntity.setCode("10002");
        postEntity.setName("测试职务");
        postEntity.setOrder(0);
        postEntity.setTenantCode("master");
        postEntity.updateCreator(properties.getSupervisor().getUsername());
        this.postMapper.insert(postEntity);

        var departmentRelEntity = new AccountDepartmentEntity();
        departmentRelEntity.setAccountId(accountEntity.getId());
        departmentRelEntity.setDepartmentId(departmentEntity.getId());
        departmentRelEntity.setUnitId(unitEntity.getId());
        departmentRelEntity.setPostId(postEntity.getId());
        departmentRelEntity.setPrimary(true);
        departmentRelEntity.setTenantCode("master");
        departmentRelEntity.updateCreator(properties.getSupervisor().getUsername());
        this.departmentRelMapper.insert(departmentRelEntity);

        List<Account> accounts = this.provider.findBy(null, null, Conditions.of(Account.class).eq("unit.code", "10001"), null);
        assertNotNull(accounts);
        assertEquals(1, accounts.size());
        assertTrue(accounts.stream().anyMatch(it -> Objects.equals(accountEntity.getId(), it.getId())));

        accounts = this.provider.findBy(null, null, Conditions.of(Account.class).eq("rank.code", "10000"), null);
        assertNotNull(accounts);
        assertEquals(1, accounts.size());
        assertTrue(accounts.stream().anyMatch(it -> Objects.equals(accountEntity.getId(), it.getId())));

        accounts = this.provider.findBy(null, null, Conditions.of(Account.class).eq("department.code", "10086"), null);
        assertNotNull(accounts);
        assertEquals(1, accounts.size());
        assertTrue(accounts.stream().anyMatch(it -> Objects.equals(accountEntity.getId(), it.getId())));

        accounts = this.provider.findBy(null, null, Conditions.of(Account.class).eq("post.code", "10002"), null);
        assertNotNull(accounts);
        assertEquals(1, accounts.size());
        assertTrue(accounts.stream().anyMatch(it -> Objects.equals(accountEntity.getId(), it.getId())));
    }

    /**
     * @see AccountProvider#pageBy
     */
    @Test
    public void case4() {
        var random = 150;
        var entities = new ArrayList<AccountEntity>(random);
        for (int i = 0; i < random; i++) {
            var entity = new AccountEntity();
            entity.setUsername("zhang" + i);
            entity.setEmail("zhang" + i + "@central-x.com");
            entity.setMobile("18888888" + Stringx.paddingRight(Integer.toString(i), 3, '0'));
            entity.setName("张" + i);
            entity.setAvatar("avatar" + i);
            entity.setAdmin(Boolean.FALSE);
            entity.setEnabled(Boolean.TRUE);
            entity.setDeleted(Boolean.FALSE);
            entity.setTenantCode("master");
            entity.updateCreator(properties.getSupervisor().getUsername());
            entities.add(entity);
        }
        this.mapper.insertBatch(entities);

        Page<Account> page = this.provider.pageBy(2L, 4L, Conditions.of(Account.class).like(Account::getName, "%3"), Orders.of(Account.class).asc(Account::getUsername));
        assertNotNull(page);
        assertEquals(2, page.getPager().getPageIndex());
        assertEquals(4, page.getPager().getPageSize());
        assertEquals(4, page.getPager().getPageCount());
        assertEquals(15, page.getPager().getItemCount());
        assertEquals(4, page.getData().size());
        assertTrue(page.getData().stream().allMatch(it -> it.getName().endsWith("3")));
    }

    /**
     * @see AccountProvider#countBy
     */
    @Test
    public void case5() {
        var random = 150;
        var entities = new ArrayList<AccountEntity>(random);
        for (int i = 0; i < random; i++) {
            var entity = new AccountEntity();
            entity.setUsername("zhang" + i);
            entity.setEmail("zhang" + i + "@central-x.com");
            entity.setMobile("18888888" + Stringx.paddingRight(Integer.toString(i), 3, '0'));
            entity.setName("张" + i);
            entity.setAvatar("avatar" + i);
            entity.setAdmin(Boolean.FALSE);
            entity.setEnabled(Boolean.TRUE);
            entity.setDeleted(Boolean.FALSE);
            entity.setTenantCode("master");
            entity.updateCreator(this.properties.getSupervisor().getUsername());
            entities.add(entity);
        }
        this.mapper.insertBatch(entities);

        var count = this.provider.countBy(Conditions.of(Account.class).like(Account::getName, "%3"));
        assertNotNull(count);
        assertEquals(15L, count);
    }

    /**
     * @see AccountProvider#insert
     */
    @Test
    public void case6() {
        var input = AccountInput.builder()
                .username("zhangs")
                .email("zhangs@central-x.com")
                .mobile("18888888888")
                .name("张三")
                .avatar("1234")
                .enabled(Boolean.TRUE)
                .deleted(Boolean.FALSE)
                .build();

        // 保存数据
        var inserted = this.provider.insert(input, this.properties.getSupervisor().getUsername());
        assertNotNull(inserted);

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getId, inserted.getId())));
    }

    /**
     * @see AccountProvider#insertBatch
     */
    @Test
    public void case7() {
        var zhangsInput = AccountInput.builder()
                .username("zhangs")
                .email("zhangs@central-x.com")
                .mobile("18888888888")
                .name("张三")
                .avatar("1234")
                .enabled(Boolean.TRUE)
                .deleted(Boolean.FALSE)
                .build();

        var lisInput = AccountInput.builder()
                .username("lis")
                .email("lis@central-x.com")
                .mobile("17777777777")
                .name("李四")
                .avatar("4321")
                .enabled(Boolean.TRUE)
                .deleted(Boolean.FALSE)
                .build();


        var inputs = List.of(zhangsInput, lisInput);

        // 保存数据
        var inserted = this.provider.insertBatch(inputs, this.properties.getSupervisor().getUsername());
        assertNotNull(inserted);
        assertEquals(2, inserted.size());

        assertTrue(inserted.stream().anyMatch(it -> Objects.equals(zhangsInput.getUsername(), it.getUsername())));
        assertTrue(inserted.stream().anyMatch(it -> Objects.equals(lisInput.getUsername(), it.getUsername())));

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getUsername, zhangsInput.getUsername())));
        assertTrue(this.mapper.existsBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getUsername, lisInput.getUsername())));
    }

    /**
     * @see AccountProvider#update
     */
    @Test
    public void case8() {
        var entity = new AccountEntity();
        entity.setUsername("zhangs");
        entity.setEmail("zhangs@central-x.com");
        entity.setMobile("18888888888");
        entity.setName("张三");
        entity.setAvatar("1234");
        entity.setAdmin(Boolean.FALSE);
        entity.setEnabled(Boolean.TRUE);
        entity.setDeleted(Boolean.FALSE);
        entity.setTenantCode("master");
        entity.updateCreator(this.properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 获取数据
        var account = this.provider.findById(entity.getId());
        assertNotNull(account);

        // 修改数据
        var input = account.toInput().toBuilder()
                .name("章三")
                .build();
        var updated = this.provider.update(input, this.properties.getSupervisor().getUsername());
        assertNotNull(updated);
        assertEquals(account.getId(), updated.getId());

        assertTrue(this.mapper.existsBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getName, input.getName())));
    }

    /**
     * @see AccountProvider#updateBatch
     */
    @Test
    public void case9() {
        var zhangsEntity = new AccountEntity();
        zhangsEntity.setUsername("zhangs");
        zhangsEntity.setEmail("zhangs@central-x.com");
        zhangsEntity.setMobile("18888888888");
        zhangsEntity.setName("张三");
        zhangsEntity.setAvatar("1234");
        zhangsEntity.setAdmin(Boolean.FALSE);
        zhangsEntity.setEnabled(Boolean.TRUE);
        zhangsEntity.setDeleted(Boolean.FALSE);
        zhangsEntity.setTenantCode("master");
        zhangsEntity.updateCreator(this.properties.getSupervisor().getUsername());
        this.mapper.insert(zhangsEntity);

        // 获取数据
        var account = this.provider.findById(zhangsEntity.getId());
        assertNotNull(account);

        // 修改数据
        var input = account.toInput().toBuilder()
                .name("章三")
                .build();

        var accounts = this.provider.updateBatch(List.of(input), this.properties.getSupervisor().getUsername());
        assertNotNull(accounts);
        assertEquals(1, accounts.size());

        account = Listx.getFirstOrNull(accounts);
        assertNotNull(account);
        assertEquals(zhangsEntity.getId(), account.getId());

        assertTrue(this.mapper.existsBy(Conditions.of(AccountEntity.class).eq(AccountEntity::getName, input.getName())));

    }

    /**
     * @see AccountProvider#deleteByIds
     */
    @Test
    public void case10() {
        // 插入数据
        var random = 30;
        var entities = new ArrayList<AccountEntity>(random);
        for (int i = 0; i < random; i++) {
            var entity = new AccountEntity();
            entity.setUsername("zhang" + i);
            entity.setEmail("zhang" + i + "@central-x.com");
            entity.setMobile("18888888" + Stringx.paddingRight(Integer.toString(i), 3, '0'));
            entity.setName("张" + i);
            entity.setAvatar("avatar" + i);
            entity.setAdmin(Boolean.FALSE);
            entity.setEnabled(Boolean.TRUE);
            entity.setDeleted(Boolean.FALSE);
            entity.setTenantCode("master");
            entity.updateCreator(this.properties.getSupervisor().getUsername());
            entities.add(entity);
        }
        this.mapper.insertBatch(entities);

        // 删除 10 个主键的数据
        var ids = entities.stream().skip(10).limit(10).map(Entity::getId).toList();

        // 删除数据
        var deleted = this.provider.deleteByIds(ids);
        assertEquals(10L, deleted);

        // 查询删除数据之后数据库的数量
        var count = this.mapper.count();
        assertEquals(20L, count);

        // 查询是否还存在指定主键的数据
        var accounts = this.mapper.findByIds(ids);
        assertEquals(0, accounts.size());
    }

    /**
     * @see AccountProvider#deleteBy
     */
    @Test
    public void case11() {
        var random = 30;
        var entities = new ArrayList<AccountEntity>(random);
        for (int i = 0; i < random; i++) {
            var entity = new AccountEntity();
            entity.setUsername("zhang" + i);
            entity.setEmail("zhang" + i + "@central-x.com");
            entity.setMobile("18888888" + Stringx.paddingRight(Integer.toString(i), 3, '0'));
            entity.setName("张" + i);
            entity.setAvatar("avatar" + i);
            entity.setAdmin(Boolean.FALSE);
            entity.setEnabled(Boolean.TRUE);
            entity.setDeleted(Boolean.FALSE);
            entity.setTenantCode("master");
            entity.updateCreator(this.properties.getSupervisor().getUsername());
            entities.add(entity);
        }
        this.mapper.insertBatch(entities);

        var deleted = this.provider.deleteBy(Conditions.of(Account.class).like(Account::getName, "%3"));
        assertEquals(3, deleted);

        // 检查是否真的被删除了
        var count = this.mapper.countBy(Conditions.of(AccountEntity.class).like(AccountEntity::getName, "%3"));
        assertEquals(0, count);
    }
}
