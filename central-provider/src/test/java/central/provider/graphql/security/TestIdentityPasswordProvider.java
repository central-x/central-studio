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

package central.provider.graphql.security;

import central.bean.Page;
import central.data.identity.IdentityPassword;
import central.data.identity.IdentityPasswordInput;
import central.provider.ApplicationProperties;
import central.provider.ProviderApplication;
import central.provider.graphql.TestProvider;
import central.provider.graphql.identity.IdentityPasswordProvider;
import central.provider.graphql.identity.entity.IdentityPasswordEntity;
import central.provider.graphql.organization.entity.AccountEntity;
import central.provider.graphql.organization.mapper.AccountMapper;
import central.provider.graphql.identity.mapper.IdentityPasswordMapper;
import central.security.Passwordx;
import central.sql.query.Conditions;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Password Provider Test Cases
 * 密码
 *
 * @author Alan Yeh
 * @since 2022/10/07
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestIdentityPasswordProvider extends TestProvider {

    @Setter(onMethod_ = @Autowired)
    private IdentityPasswordProvider provider;

    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    @Setter(onMethod_ = @Autowired)
    private IdentityPasswordMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private AccountMapper accountMapper;

    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空数据
        mapper.deleteAll();
    }


    /**
     * @see IdentityPasswordProvider#findById
     */
    @Test
    public void case1() {
        var account = new AccountEntity();
        account.setUsername("zhangs");
        account.setEmail("zhangs@central-x.com");
        account.setMobile("18888888888");
        account.setName("张三");
        account.setAvatar("1234");
        account.setAdmin(Boolean.FALSE);
        account.setEnabled(Boolean.TRUE);
        account.setDeleted(Boolean.FALSE);
        account.setTenantCode("master");
        account.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(account);

        var entity = new IdentityPasswordEntity();
        entity.setAccountId(account.getId());
        entity.setValue(Passwordx.encrypt("123456"));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var password = this.provider.findById(entity.getId());
        assertNotNull(password);
        assertEquals(entity.getId(), password.getId());
        assertEquals(entity.getAccountId(), password.getAccountId());
        assertNotNull(password.getAccount());
        assertEquals(account.getId(), password.getAccount().getId());
        assertEquals(account.getUsername(), password.getAccount().getUsername());
        assertEquals(account.getName(), password.getAccount().getName());
        assertTrue(Passwordx.verify("123456", password.getValue()));

        assertNotNull(password.getCreatorId());
        assertEquals(this.properties.getSupervisor().getUsername(), password.getCreator().getId());
        assertEquals(this.properties.getSupervisor().getUsername(), password.getCreator().getUsername());
        assertEquals(this.properties.getSupervisor().getName(), password.getCreator().getName());
    }

    /**
     * @see IdentityPasswordProvider#findByIds
     */
    @Test
    public void case2() {
        var account = new AccountEntity();
        account.setUsername("zhangs");
        account.setEmail("zhangs@central-x.com");
        account.setMobile("18888888888");
        account.setName("张三");
        account.setAvatar("1234");
        account.setAdmin(Boolean.FALSE);
        account.setEnabled(Boolean.TRUE);
        account.setDeleted(Boolean.FALSE);
        account.setTenantCode("master");
        account.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(account);

        var entity = new IdentityPasswordEntity();
        entity.setAccountId(account.getId());
        entity.setValue(Passwordx.encrypt("123456"));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var passwords = this.provider.findByIds(List.of(entity.getId()));
        assertNotNull(passwords);
        assertEquals(1, passwords.size());
        assertTrue(passwords.stream().anyMatch(it -> Objects.equals(entity.getId(), it.getId())));
    }

    /**
     * @see IdentityPasswordProvider#findBy
     */
    @Test
    public void case3() {
        var account = new AccountEntity();
        account.setUsername("zhangs");
        account.setEmail("zhangs@central-x.com");
        account.setMobile("18888888888");
        account.setName("张三");
        account.setAvatar("1234");
        account.setAdmin(Boolean.FALSE);
        account.setEnabled(Boolean.TRUE);
        account.setDeleted(Boolean.FALSE);
        account.setTenantCode("master");
        account.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(account);

        var entity = new IdentityPasswordEntity();
        entity.setAccountId(account.getId());
        entity.setValue(Passwordx.encrypt("123456"));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        List<IdentityPassword> passwords = this.provider.findBy(null, null, Conditions.of(IdentityPassword.class).eq("account.username", account.getUsername()), null);
        assertNotNull(passwords);
        assertEquals(1, passwords.size());
        assertTrue(passwords.stream().allMatch(it -> Objects.equals(entity.getId(), it.getId())));
    }

    /**
     * @see IdentityPasswordProvider#pageBy
     */
    @Test
    public void case4() {
        var account = new AccountEntity();
        account.setUsername("zhangs");
        account.setEmail("zhangs@central-x.com");
        account.setMobile("18888888888");
        account.setName("张三");
        account.setAvatar("1234");
        account.setAdmin(Boolean.FALSE);
        account.setEnabled(Boolean.TRUE);
        account.setDeleted(Boolean.FALSE);
        account.setTenantCode("master");
        account.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(account);

        var entity = new IdentityPasswordEntity();
        entity.setAccountId(account.getId());
        entity.setValue(Passwordx.encrypt("123456"));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        Page<IdentityPassword> page = this.provider.pageBy(1L, 4L, Conditions.of(IdentityPassword.class).like("account.name", "张%"), null);
        assertNotNull(page);
        assertEquals(1, page.getPager().getPageIndex());
        assertEquals(4, page.getPager().getPageSize());
        assertEquals(1, page.getPager().getPageCount());
        assertEquals(1, page.getPager().getItemCount());
        assertEquals(1, page.getData().size());
        assertTrue(page.getData().stream().allMatch(it -> Objects.equals(entity.getId(), it.getId())));
    }

    /**
     * @see IdentityPasswordProvider#countBy
     */
    @Test
    public void case5() {
        var account = new AccountEntity();
        account.setUsername("zhangs");
        account.setEmail("zhangs@central-x.com");
        account.setMobile("18888888888");
        account.setName("张三");
        account.setAvatar("1234");
        account.setAdmin(Boolean.FALSE);
        account.setEnabled(Boolean.TRUE);
        account.setDeleted(Boolean.FALSE);
        account.setTenantCode("master");
        account.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(account);

        var entity = new IdentityPasswordEntity();
        entity.setAccountId(account.getId());
        entity.setValue(Passwordx.encrypt("123456"));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var count = this.provider.countBy(Conditions.of(IdentityPassword.class).like("account.name", "张%"));
        assertNotNull(count);
        assertEquals(1L, count);
    }

    /**
     * @see IdentityPasswordProvider#insert
     */
    @Test
    public void case6() {
        var account = new AccountEntity();
        account.setUsername("zhangs");
        account.setEmail("zhangs@central-x.com");
        account.setMobile("18888888888");
        account.setName("张三");
        account.setAvatar("1234");
        account.setAdmin(Boolean.FALSE);
        account.setEnabled(Boolean.TRUE);
        account.setDeleted(Boolean.FALSE);
        account.setTenantCode("master");
        account.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(account);

        var input = IdentityPasswordInput.builder()
                .accountId(account.getId())
                .value("123456")
                .build();

        // 保存数据
        var inserted = this.provider.insert(input, this.properties.getSupervisor().getUsername());
        assertNotNull(inserted);
        assertNotNull(inserted.getId());
        assertEquals(account.getId(), inserted.getAccountId());
        assertEquals(account.getUsername(), inserted.getAccount().getUsername());
        assertEquals(account.getName(), inserted.getAccount().getName());
        assertTrue(Passwordx.verify("123456", inserted.getValue()));

        // 查询数据库
        var entity = this.mapper.findById(inserted.getId());
        assertNotNull(entity);
        assertNotNull(entity.getTenantCode());
        assertEquals(inserted.getId(), entity.getId());
        assertEquals(inserted.getAccountId(), entity.getAccountId());
        assertEquals(inserted.getValue(), entity.getValue());
        assertEquals(inserted.getCreatorId(), entity.getCreatorId());
        assertEquals(inserted.getCreateDate(), entity.getCreateDate());
    }

    /**
     * @see IdentityPasswordProvider#insertBatch
     */
    @Test
    public void case7() {
        var zhangs = new AccountEntity();
        zhangs.setUsername("zhangs");
        zhangs.setEmail("zhangs@central-x.com");
        zhangs.setMobile("18888888888");
        zhangs.setName("张三");
        zhangs.setAvatar("1234");
        zhangs.setAdmin(Boolean.FALSE);
        zhangs.setEnabled(Boolean.TRUE);
        zhangs.setDeleted(Boolean.FALSE);
        zhangs.setTenantCode("master");
        zhangs.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(zhangs);

        var lis = new AccountEntity();
        lis.setUsername("lis");
        lis.setEmail("lis@central-x.com");
        lis.setMobile("17777777777");
        lis.setName("李四");
        lis.setAvatar("4321");
        lis.setAdmin(Boolean.FALSE);
        lis.setEnabled(Boolean.TRUE);
        lis.setDeleted(Boolean.FALSE);
        lis.setTenantCode("master");
        lis.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(lis);

        var zhangsPwdInput = IdentityPasswordInput.builder()
                .accountId(zhangs.getId())
                .value("123456")
                .build();
        var lisPwdInput = IdentityPasswordInput.builder()
                .accountId(lis.getId())
                .value("654321")
                .build();

        var inserted = this.provider.insertBatch(List.of(zhangsPwdInput, lisPwdInput), properties.getSupervisor().getUsername());
        assertNotNull(inserted);
        assertEquals(2, inserted.size());

        var search = this.provider.findByIds(inserted.stream().map(IdentityPassword::getId).toList());
        assertNotNull(search);
        assertEquals(2, search.size());
    }

    /**
     * @see IdentityPasswordProvider#deleteByIds
     */
    @Test
    public void case8() {
        // 插入数据
        var zhangs = new AccountEntity();
        zhangs.setUsername("zhangs");
        zhangs.setEmail("zhangs@central-x.com");
        zhangs.setMobile("18888888888");
        zhangs.setName("张三");
        zhangs.setAvatar("1234");
        zhangs.setAdmin(Boolean.FALSE);
        zhangs.setEnabled(Boolean.TRUE);
        zhangs.setDeleted(Boolean.FALSE);
        zhangs.setTenantCode("master");
        zhangs.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(zhangs);

        var lis = new AccountEntity();
        lis.setUsername("lis");
        lis.setEmail("lis@central-x.com");
        lis.setMobile("17777777777");
        lis.setName("李四");
        lis.setAvatar("4321");
        lis.setAdmin(Boolean.FALSE);
        lis.setEnabled(Boolean.TRUE);
        lis.setDeleted(Boolean.FALSE);
        lis.setTenantCode("master");
        lis.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(lis);

        var zhangsPwdEntity = new IdentityPasswordEntity();
        zhangsPwdEntity.setAccountId(zhangs.getId());
        zhangsPwdEntity.setValue(Passwordx.encrypt("123456"));
        zhangsPwdEntity.setTenantCode("master");
        zhangsPwdEntity.updateCreator(properties.getSupervisor().getUsername());

        var lisPwdEntity = new IdentityPasswordEntity();
        lisPwdEntity.setAccountId(lis.getId());
        lisPwdEntity.setValue(Passwordx.encrypt("654321"));
        lisPwdEntity.setTenantCode("master");
        lisPwdEntity.updateCreator(properties.getSupervisor().getUsername());

        this.mapper.insertBatch(List.of(zhangsPwdEntity, lisPwdEntity));

        // 删除数据
        var deleted = this.provider.deleteByIds(List.of(zhangsPwdEntity.getId(), lisPwdEntity.getId()));

        assertEquals(2L, deleted);

        // 查询删除数据之后数据库的数量
        var count = this.mapper.count();
        assertEquals(0L, count);
    }

    /**
     * @see IdentityPasswordProvider#deleteBy
     */
    @Test
    public void case9() {
        // 插入数据
        var zhangs = new AccountEntity();
        zhangs.setUsername("zhangs");
        zhangs.setEmail("zhangs@central-x.com");
        zhangs.setMobile("18888888888");
        zhangs.setName("张三");
        zhangs.setAvatar("1234");
        zhangs.setAdmin(Boolean.FALSE);
        zhangs.setEnabled(Boolean.TRUE);
        zhangs.setDeleted(Boolean.FALSE);
        zhangs.setTenantCode("master");
        zhangs.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(zhangs);

        var lis = new AccountEntity();
        lis.setUsername("lis");
        lis.setEmail("lis@central-x.com");
        lis.setMobile("17777777777");
        lis.setName("李四");
        lis.setAvatar("4321");
        lis.setAdmin(Boolean.FALSE);
        lis.setEnabled(Boolean.TRUE);
        lis.setDeleted(Boolean.FALSE);
        lis.setTenantCode("master");
        lis.updateCreator(properties.getSupervisor().getUsername());
        this.accountMapper.insert(lis);

        var zhangsPwdEntity = new IdentityPasswordEntity();
        zhangsPwdEntity.setAccountId(zhangs.getId());
        zhangsPwdEntity.setValue(Passwordx.encrypt("123456"));
        zhangsPwdEntity.setTenantCode("master");
        zhangsPwdEntity.updateCreator(properties.getSupervisor().getUsername());

        var lisPwdEntity = new IdentityPasswordEntity();
        lisPwdEntity.setAccountId(lis.getId());
        lisPwdEntity.setValue(Passwordx.encrypt("654321"));
        lisPwdEntity.setTenantCode("master");
        lisPwdEntity.updateCreator(properties.getSupervisor().getUsername());

        this.mapper.insertBatch(List.of(zhangsPwdEntity, lisPwdEntity));


        // 删除数据
        var deleted = this.provider.deleteBy(Conditions.of(IdentityPassword.class).eq(IdentityPassword::getAccountId, zhangs.getId()));
        assertEquals(1, deleted);

        // 检查是否真的被删除了
        var count = this.mapper.countBy(Conditions.of(IdentityPasswordEntity.class).eq(IdentityPasswordEntity::getAccountId, zhangs.getId()));
        assertEquals(0, count);
    }
}
