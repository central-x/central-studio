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

package central.provider.graphql.org;

import central.api.provider.org.AccountProvider;
import central.bean.Page;
import central.data.org.Account;
import central.data.org.AccountInput;
import central.lang.Stringx;
import central.provider.ApplicationProperties;
import central.provider.ProviderApplication;
import central.provider.graphql.TestProvider;
import central.provider.graphql.org.entity.AccountEntity;
import central.provider.graphql.org.mapper.AccountMapper;
import central.sql.Conditions;
import central.sql.Orders;
import central.sql.data.Entity;
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

    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空数据
        mapper.deleteAll();
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
        assertEquals(entity.getUsername(), account.getUsername());
        assertEquals(entity.getEmail(), account.getEmail());
        assertEquals(entity.getMobile(), account.getMobile());
        assertEquals(entity.getName(), account.getName());
        assertEquals(entity.getAvatar(), account.getAvatar());
        assertEquals(entity.getEnabled(), account.getEnabled());
        assertEquals(entity.getDeleted(), account.getDeleted());
        assertEquals(entity.getCreatorId(), account.getCreatorId());
        assertNotNull(account.getCreateDate());
        assertNotNull(account.getCreator());
        assertEquals(this.properties.getSupervisor().getUsername(), account.getCreator().getId());
        assertEquals(this.properties.getSupervisor().getUsername(), account.getCreator().getUsername());
        assertEquals(this.properties.getSupervisor().getName(), account.getCreator().getName());

        assertEquals(entity.getModifierId(), account.getModifierId());
        assertNotNull(account.getModifyDate());
        assertNotNull(account.getCreator());
        assertEquals(this.properties.getSupervisor().getUsername(), account.getModifier().getId());
        assertEquals(this.properties.getSupervisor().getUsername(), account.getModifier().getUsername());
        assertEquals(this.properties.getSupervisor().getName(), account.getModifier().getName());

        assertEquals(account.getCreateDate(), account.getModifyDate(), "创建数据后，创建日期和修改日期应该相同");
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
            entity.setMobile("18888888" + Stringx.padding(Integer.toString(i), 3, '0'));
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
            entity.setMobile("18888888" + Stringx.padding(Integer.toString(i), 3, '0'));
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
            entity.setMobile("18888888" + Stringx.padding(Integer.toString(i), 3, '0'));
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
        assertNotNull(inserted.getId());
        assertEquals(input.getUsername(), inserted.getUsername());
        assertEquals(input.getEmail(), inserted.getEmail());
        assertEquals(input.getMobile(), inserted.getMobile());
        assertEquals(input.getName(), inserted.getName());
        assertEquals(input.getAvatar(), inserted.getAvatar());
        assertEquals(input.getEnabled(), inserted.getEnabled());
        assertEquals(input.getDeleted(), inserted.getDeleted());
        assertEquals(this.properties.getSupervisor().getUsername(), inserted.getCreatorId());
        assertNotNull(inserted.getCreateDate());
        assertNotNull(inserted.getCreator());
        assertEquals(this.properties.getSupervisor().getUsername(), inserted.getCreator().getId());
        assertEquals(this.properties.getSupervisor().getUsername(), inserted.getCreator().getUsername());
        assertEquals(this.properties.getSupervisor().getName(), inserted.getCreator().getName());

        assertEquals(this.properties.getSupervisor().getUsername(), inserted.getModifierId());
        assertNotNull(inserted.getModifyDate());
        assertNotNull(inserted.getCreator());
        assertEquals(this.properties.getSupervisor().getUsername(), inserted.getModifier().getId());
        assertEquals(this.properties.getSupervisor().getUsername(), inserted.getModifier().getUsername());
        assertEquals(this.properties.getSupervisor().getName(), inserted.getModifier().getName());

        assertEquals(inserted.getCreateDate(), inserted.getModifyDate(), "创建数据后，创建日期和修改日期应该相同");

        // 查询数据库
        var entity = this.mapper.findById(inserted.getId());
        assertNotNull(entity);
        assertNotNull(entity.getTenantCode());
        assertEquals(inserted.getId(), entity.getId());
        assertEquals(inserted.getUsername(), entity.getUsername());
        assertEquals(inserted.getEmail(), entity.getEmail());
        assertEquals(inserted.getMobile(), entity.getMobile());
        assertEquals(inserted.getName(), entity.getName());
        assertEquals(inserted.getAvatar(), entity.getAvatar());
        assertEquals(inserted.getAdmin(), entity.getAdmin());
        assertEquals(inserted.getEnabled(), entity.getEnabled());
        assertEquals(inserted.getDeleted(), entity.getDeleted());
        assertEquals(inserted.getCreateDate(), entity.getCreateDate());
        assertEquals(inserted.getCreatorId(), entity.getCreatorId());
        assertEquals(inserted.getModifyDate(), entity.getModifyDate());
        assertEquals(inserted.getModifierId(), entity.getModifierId());
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

        // 张三
        var zhangsInserted = inserted.stream().filter(it -> Objects.equals(zhangsInput.getUsername(), it.getUsername())).findFirst().orElse(null);
        assertNotNull(zhangsInserted);
        assertNotNull(zhangsInserted.getId());
        assertEquals(zhangsInput.getUsername(), zhangsInserted.getUsername());
        assertEquals(zhangsInput.getEmail(), zhangsInserted.getEmail());
        assertEquals(zhangsInput.getMobile(), zhangsInserted.getMobile());
        assertEquals(zhangsInput.getName(), zhangsInserted.getName());
        assertEquals(zhangsInput.getAvatar(), zhangsInserted.getAvatar());
        assertEquals(zhangsInput.getEnabled(), zhangsInserted.getEnabled());
        assertEquals(zhangsInput.getDeleted(), zhangsInserted.getDeleted());
        assertEquals(this.properties.getSupervisor().getUsername(), zhangsInserted.getCreatorId());
        assertNotNull(zhangsInserted.getCreateDate());
        assertNotNull(zhangsInserted.getCreator());
        assertEquals(this.properties.getSupervisor().getUsername(), zhangsInserted.getCreator().getId());
        assertEquals(this.properties.getSupervisor().getUsername(), zhangsInserted.getCreator().getUsername());
        assertEquals(this.properties.getSupervisor().getName(), zhangsInserted.getCreator().getName());

        assertEquals(this.properties.getSupervisor().getUsername(), zhangsInserted.getModifierId());
        assertNotNull(zhangsInserted.getModifyDate());
        assertNotNull(zhangsInserted.getCreator());
        assertEquals(this.properties.getSupervisor().getUsername(), zhangsInserted.getModifier().getId());
        assertEquals(this.properties.getSupervisor().getUsername(), zhangsInserted.getModifier().getUsername());
        assertEquals(this.properties.getSupervisor().getName(), zhangsInserted.getModifier().getName());

        assertEquals(zhangsInserted.getCreateDate(), zhangsInserted.getModifyDate(), "创建数据后，创建日期和修改日期应该相同");

        // 查询数据库
        var zhangsEntity = this.mapper.findById(zhangsInserted.getId());
        assertNotNull(zhangsEntity);
        assertNotNull(zhangsEntity.getTenantCode());
        assertEquals(zhangsInserted.getId(), zhangsEntity.getId());
        assertEquals(zhangsInserted.getUsername(), zhangsEntity.getUsername());
        assertEquals(zhangsInserted.getEmail(), zhangsEntity.getEmail());
        assertEquals(zhangsInserted.getMobile(), zhangsEntity.getMobile());
        assertEquals(zhangsInserted.getName(), zhangsEntity.getName());
        assertEquals(zhangsInserted.getAvatar(), zhangsEntity.getAvatar());
        assertEquals(zhangsInserted.getAdmin(), zhangsEntity.getAdmin());
        assertEquals(zhangsInserted.getEnabled(), zhangsEntity.getEnabled());
        assertEquals(zhangsInserted.getDeleted(), zhangsEntity.getDeleted());
        assertEquals(zhangsInserted.getCreateDate(), zhangsEntity.getCreateDate());
        assertEquals(zhangsInserted.getCreatorId(), zhangsEntity.getCreatorId());
        assertEquals(zhangsInserted.getModifyDate(), zhangsEntity.getModifyDate());
        assertEquals(zhangsInserted.getModifierId(), zhangsEntity.getModifierId());

        // 李四
        var lisInserted = inserted.stream().filter(it -> Objects.equals(lisInput.getUsername(), it.getUsername())).findFirst().orElse(null);
        assertNotNull(lisInserted);
        assertNotNull(lisInserted.getId());

        var lisEntity = this.mapper.findById(lisInserted.getId());
        assertNotNull(lisEntity);
        assertNotNull(lisEntity.getTenantCode());
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
                .avatar("12345")
                .enabled(Boolean.FALSE)
                .deleted(Boolean.TRUE)
                .build();
        var updated = this.provider.update(input, this.properties.getSupervisor().getUsername());
        assertNotNull(updated);
        assertEquals(account.getId(), updated.getId());
        assertEquals(account.getUsername(), updated.getUsername(), "用户名[username]未修改");
        assertEquals(account.getEmail(), updated.getEmail(), "邮箱[email]未修改");
        assertEquals(account.getMobile(), updated.getMobile(), "手机号[mobile]未修改");
        assertNotEquals(account.getName(), updated.getName(), "姓名[name]已修改");
        assertEquals(input.getName(), updated.getName(), "姓名[name]需要修改为" + input.getName());
        assertNotEquals(account.getAvatar(), updated.getAvatar(), "头像[avatar]已修改");
        assertEquals(input.getAvatar(), updated.getAvatar(), "头像[avatar]需修改为" + input.getAvatar());
        assertNotEquals(account.getEnabled(), updated.getEnabled(), "启用状态[enabled]已修改");
        assertEquals(input.getEnabled(), updated.getEnabled(), "启用状态[enabled]需修改为" + input.getEnabled());
        assertNotEquals(account.getDeleted(), updated.getDeleted(), "删除状态[deleted]已修改");
        assertEquals(input.getDeleted(), updated.getDeleted(), "删除状态[deleted]需修改为" + input.getDeleted());
        assertEquals(account.getCreateDate(), updated.getCreateDate(), "创建日期[createDate]未修改");
        assertNotEquals(account.getModifyDate(), updated.getModifyDate(), "修改日期[modifyDate]需已修改");
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

        var lisEntity = new AccountEntity();
        lisEntity.setUsername("zhangs");
        lisEntity.setEmail("zhangs@central-x.com");
        lisEntity.setMobile("18888888888");
        lisEntity.setName("张三");
        lisEntity.setAvatar("1234");
        lisEntity.setAdmin(Boolean.FALSE);
        lisEntity.setEnabled(Boolean.TRUE);
        lisEntity.setDeleted(Boolean.FALSE);
        lisEntity.setTenantCode("master");
        lisEntity.updateCreator(this.properties.getSupervisor().getUsername());
        this.mapper.insert(lisEntity);

        // 获取数据
        var accounts = this.provider.findByIds(List.of(zhangsEntity.getId(), lisEntity.getId()));
        assertNotNull(accounts);
        assertEquals(2, accounts.size());

        // 修改数据
        var inputs = accounts.stream().map(it -> it.toInput()
                .toBuilder()
                .name("章三")
                .avatar("12345")
                .enabled(Boolean.FALSE)
                .deleted(Boolean.TRUE)
                .build()
        ).toList();

        var updated = this.provider.updateBatch(inputs, this.properties.getSupervisor().getUsername());
        assertNotNull(updated);
        var zhangsUpdated = updated.stream().filter(it -> Objects.equals(zhangsEntity.getId(), it.getId())).findFirst().orElse(null);
        assertNotNull(zhangsUpdated);

        assertEquals(zhangsEntity.getUsername(), zhangsUpdated.getUsername(), "用户名[username]未修改");
        assertEquals(zhangsEntity.getEmail(), zhangsUpdated.getEmail(), "邮箱[email]未修改");
        assertEquals(zhangsEntity.getMobile(), zhangsUpdated.getMobile(), "手机号[mobile]未修改");
        assertEquals("章三", zhangsUpdated.getName(), "姓名[name]已修改");
        assertEquals("12345", zhangsUpdated.getAvatar(), "头像[avatar]已修改");
        assertEquals(Boolean.FALSE, zhangsUpdated.getEnabled(), "启用状态[enabled]已修改");
        assertEquals(Boolean.TRUE, zhangsUpdated.getDeleted(), "删除状态[deleted]已修改");
        assertEquals(zhangsEntity.getCreateDate(), zhangsUpdated.getCreateDate(), "创建日期[createDate]未修改");
        assertEquals(zhangsUpdated.getModifyDate(), zhangsUpdated.getModifyDate(),  "修改日期[modifyDate]需已修改");


        var lisUpdated = updated.stream().filter(it -> Objects.equals(lisEntity.getId(), it.getId())).findFirst().orElse(null);
        assertNotNull(lisUpdated);

        assertEquals(lisEntity.getUsername(), lisUpdated.getUsername(), "用户名[username]未修改");
        assertEquals(lisEntity.getEmail(), lisUpdated.getEmail(), "邮箱[email]未修改");
        assertEquals(lisEntity.getMobile(), lisUpdated.getMobile(), "手机号[mobile]未修改");
        assertEquals("章三", lisUpdated.getName(), "姓名[name]已修改");
        assertEquals("12345", lisUpdated.getAvatar(), "头像[avatar]已修改");
        assertEquals(Boolean.FALSE, lisUpdated.getEnabled(), "启用状态[enabled]已修改");
        assertEquals(Boolean.TRUE, lisUpdated.getDeleted(), "删除状态[deleted]已修改");
        assertEquals(lisEntity.getCreateDate(), lisUpdated.getCreateDate(), "创建日期[createDate]未修改");
        assertEquals(lisUpdated.getModifyDate(), lisUpdated.getModifyDate(),  "修改日期[modifyDate]需已修改");

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
            entity.setMobile("18888888" + Stringx.padding(Integer.toString(i), 3, '0'));
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
            entity.setMobile("18888888" + Stringx.padding(Integer.toString(i), 3, '0'));
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
