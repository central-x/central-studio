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

import central.api.provider.organization.AccountUnitProvider;
import central.data.organization.AccountUnit;
import central.data.organization.AccountUnitInput;
import central.data.organization.option.AreaType;
import central.provider.ApplicationProperties;
import central.provider.ProviderApplication;
import central.provider.graphql.TestProvider;
import central.provider.graphql.organization.entity.*;
import central.provider.graphql.organization.mapper.*;
import central.sql.Conditions;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Account Unit Provider
 * 帐户与单位关联关系
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestAccountUnitProvider extends TestProvider {

    @Setter(onMethod_ = @Autowired)
    private AccountUnitProvider provider;

    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    @Setter(onMethod_ = @Autowired)
    private AreaMapper areaMapper;

    @Setter(onMethod_ = @Autowired)
    private UnitMapper unitMapper;

    @Setter(onMethod_ = @Autowired)
    private RankMapper rankMapper;

    @Setter(onMethod_ = @Autowired)
    private AccountMapper accountMapper;

    @Setter(onMethod_ = @Autowired)
    private AccountUnitMapper relMapper;

    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空数据
        areaMapper.deleteAll();
        unitMapper.deleteAll();
        rankMapper.deleteAll();
        accountMapper.deleteAll();
        relMapper.deleteAll();
    }


    /**
     * @see AccountUnitProvider#findById
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

        var rankEntity = new RankEntity();
        rankEntity.setUnitId(unitEntity.getId());
        rankEntity.setCode("10000");
        rankEntity.setName("测试职务");
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
        this.accountMapper.insert(accountEntity);

        var relEntity = new AccountUnitEntity();
        relEntity.setAccountId(accountEntity.getId());
        relEntity.setUnitId(unitEntity.getId());
        relEntity.setRankId(rankEntity.getId());
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
        assertNotNull(rel.getRank());
        assertEquals(rankEntity.getId(), rel.getRank().getId());
        // 关联查询
        assertNotNull(rel.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), rel.getCreator().getId());
    }

    /**
     * @see AccountUnitProvider#findByIds
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

        var rankEntity = new RankEntity();
        rankEntity.setUnitId(unitEntity.getId());
        rankEntity.setCode("10000");
        rankEntity.setName("测试职务");
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
        this.accountMapper.insert(accountEntity);

        var relEntity = new AccountUnitEntity();
        relEntity.setAccountId(accountEntity.getId());
        relEntity.setUnitId(unitEntity.getId());
        relEntity.setRankId(rankEntity.getId());
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
        assertNotNull(rel.getRank());
        assertEquals(rankEntity.getId(), rel.getRank().getId());
        // 关联查询
        assertNotNull(rel.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), rel.getCreator().getId());
    }

    /**
     * @see AccountUnitProvider#findBy
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

        var rankEntity = new RankEntity();
        rankEntity.setUnitId(unitEntity.getId());
        rankEntity.setCode("10000");
        rankEntity.setName("测试职务");
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
        this.accountMapper.insert(accountEntity);

        var relEntity = new AccountUnitEntity();
        relEntity.setAccountId(accountEntity.getId());
        relEntity.setUnitId(unitEntity.getId());
        relEntity.setRankId(rankEntity.getId());
        relEntity.setPrimary(Boolean.TRUE);
        relEntity.setTenantCode("master");
        relEntity.updateCreator(properties.getSupervisor().getUsername());
        this.relMapper.insert(relEntity);

        // 查询数据
        var rels = this.provider.findBy(null, null, Conditions.of(AccountUnit.class).eq(AccountUnit::getId, relEntity.getId()), null);
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
        assertNotNull(rel.getRank());
        assertEquals(rankEntity.getId(), rel.getRank().getId());
        // 关联查询
        assertNotNull(rel.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), rel.getCreator().getId());
    }

    /**
     * @see AccountUnitProvider#pageBy
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

        var rankEntity = new RankEntity();
        rankEntity.setUnitId(unitEntity.getId());
        rankEntity.setCode("10000");
        rankEntity.setName("测试职务");
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
        this.accountMapper.insert(accountEntity);

        var relEntity = new AccountUnitEntity();
        relEntity.setAccountId(accountEntity.getId());
        relEntity.setUnitId(unitEntity.getId());
        relEntity.setRankId(rankEntity.getId());
        relEntity.setPrimary(Boolean.TRUE);
        relEntity.setTenantCode("master");
        relEntity.updateCreator(properties.getSupervisor().getUsername());
        this.relMapper.insert(relEntity);

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(AccountUnit.class).eq(AccountUnit::getId, relEntity.getId()), null);
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
        assertNotNull(rel.getRank());
        assertEquals(rankEntity.getId(), rel.getRank().getId());
        // 关联查询
        assertNotNull(rel.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), rel.getCreator().getId());
    }

    /**
     * @see AccountUnitProvider#countBy
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

        var rankEntity = new RankEntity();
        rankEntity.setUnitId(unitEntity.getId());
        rankEntity.setCode("10000");
        rankEntity.setName("测试职务");
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
        this.accountMapper.insert(accountEntity);

        var relEntity = new AccountUnitEntity();
        relEntity.setAccountId(accountEntity.getId());
        relEntity.setUnitId(unitEntity.getId());
        relEntity.setRankId(rankEntity.getId());
        relEntity.setPrimary(Boolean.TRUE);
        relEntity.setTenantCode("master");
        relEntity.updateCreator(properties.getSupervisor().getUsername());
        this.relMapper.insert(relEntity);

        // 查询数据
        var count = this.provider.countBy(Conditions.of(AccountUnit.class).eq(AccountUnit::getId, relEntity.getId()));
        assertNotNull(count);
        assertEquals(1, count);
    }

    /**
     * @see AccountUnitProvider#insert
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

        var rankEntity = new RankEntity();
        rankEntity.setUnitId(unitEntity.getId());
        rankEntity.setCode("10000");
        rankEntity.setName("测试职务");
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
        this.accountMapper.insert(accountEntity);

        var input = AccountUnitInput.builder()
                .accountId(accountEntity.getId())
                .unitId(unitEntity.getId())
                .rankId(rankEntity.getId())
                .primary(Boolean.TRUE)
                .build();

        var rel = this.provider.insert(input, properties.getSupervisor().getUsername());
        assertNotNull(rel);
        assertNotNull(rel.getId());

        assertTrue(this.relMapper.existsBy(Conditions.of(AccountUnitEntity.class).eq(AccountUnitEntity::getId, rel.getId())));
    }

    /**
     * @see AccountUnitProvider#insertBatch
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

        var rankEntity = new RankEntity();
        rankEntity.setUnitId(unitEntity.getId());
        rankEntity.setCode("10000");
        rankEntity.setName("测试职务");
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
        this.accountMapper.insert(accountEntity);

        var input = AccountUnitInput.builder()
                .accountId(accountEntity.getId())
                .unitId(unitEntity.getId())
                .rankId(rankEntity.getId())
                .primary(Boolean.TRUE)
                .build();

        var rels = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(rels);
        assertEquals(1, rels.size());

        var rel = Listx.getFirstOrNull(rels);
        assertNotNull(rel);
        assertNotNull(rel.getId());

        assertTrue(this.relMapper.existsBy(Conditions.of(AccountUnitEntity.class).eq(AccountUnitEntity::getId, rel.getId())));
    }

    /**
     * @see AccountUnitProvider#deleteByIds
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

        var rankEntity = new RankEntity();
        rankEntity.setUnitId(unitEntity.getId());
        rankEntity.setCode("10000");
        rankEntity.setName("测试职务");
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
        this.accountMapper.insert(accountEntity);

        var relEntity = new AccountUnitEntity();
        relEntity.setAccountId(accountEntity.getId());
        relEntity.setUnitId(unitEntity.getId());
        relEntity.setRankId(rankEntity.getId());
        relEntity.setPrimary(Boolean.TRUE);
        relEntity.setTenantCode("master");
        relEntity.updateCreator(properties.getSupervisor().getUsername());
        this.relMapper.insert(relEntity);

        var deleted = this.provider.deleteByIds(List.of(relEntity.getId()));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.relMapper.existsBy(Conditions.of(AccountUnitEntity.class).eq(AccountUnitEntity::getId, relEntity.getId())));
    }

    /**
     * @see AccountUnitProvider#deleteBy
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

        var rankEntity = new RankEntity();
        rankEntity.setUnitId(unitEntity.getId());
        rankEntity.setCode("10000");
        rankEntity.setName("测试职务");
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
        this.accountMapper.insert(accountEntity);

        var relEntity = new AccountUnitEntity();
        relEntity.setAccountId(accountEntity.getId());
        relEntity.setUnitId(unitEntity.getId());
        relEntity.setRankId(rankEntity.getId());
        relEntity.setPrimary(Boolean.TRUE);
        relEntity.setTenantCode("master");
        relEntity.updateCreator(properties.getSupervisor().getUsername());
        this.relMapper.insert(relEntity);

        var deleted = this.provider.deleteBy(Conditions.of(AccountUnit.class).eq(AccountUnit::getId, relEntity.getId()));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.relMapper.existsBy(Conditions.of(AccountUnitEntity.class).eq(AccountUnitEntity::getId, relEntity.getId())));
    }
}
