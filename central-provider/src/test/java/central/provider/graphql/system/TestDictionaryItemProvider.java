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

package central.provider.graphql.system;

import central.data.system.DictionaryItem;
import central.data.system.DictionaryItemInput;
import central.studio.provider.ApplicationProperties;
import central.studio.provider.ProviderApplication;
import central.provider.graphql.TestProvider;
import central.studio.provider.graphql.system.entity.DictionaryEntity;
import central.studio.provider.graphql.system.entity.DictionaryItemEntity;
import central.studio.provider.graphql.system.mapper.DictionaryItemMapper;
import central.studio.provider.graphql.system.mapper.DictionaryMapper;
import central.studio.provider.graphql.saas.entity.ApplicationEntity;
import central.studio.provider.graphql.saas.mapper.ApplicationMapper;
import central.sql.query.Conditions;
import central.util.Guidx;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Dictionary Item Provider Test Cases
 * 字典项
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestDictionaryItemProvider extends TestProvider {
    @Setter(onMethod_ = @Autowired)
    private DictionaryItemProvider provider;

    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    @Setter(onMethod_ = @Autowired)
    private DictionaryMapper dictionaryMapper;

    @Setter(onMethod_ = @Autowired)
    private DictionaryItemMapper itemMapper;

    @Setter(onMethod_ = @Autowired)
    private ApplicationMapper applicationMapper;

    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空数据
        dictionaryMapper.deleteAll();
        itemMapper.deleteAll();
        applicationMapper.deleteAll();
    }

    /**
     * @see DictionaryProvider#findById
     */
    @Test
    public void case1() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var dictionaryEntity = new DictionaryEntity();
        dictionaryEntity.setApplicationId(applicationEntity.getId());
        dictionaryEntity.setCode("test");
        dictionaryEntity.setName("测试字典");
        dictionaryEntity.setEnabled(Boolean.TRUE);
        dictionaryEntity.setRemark("测试");
        dictionaryEntity.setTenantCode("master");
        dictionaryEntity.updateCreator(properties.getSupervisor().getUsername());
        this.dictionaryMapper.insert(dictionaryEntity);

        var dictionaryItemEntity1 = new DictionaryItemEntity();
        dictionaryItemEntity1.setDictionaryId(dictionaryEntity.getId());
        dictionaryItemEntity1.setCode("option1");
        dictionaryItemEntity1.setName("测试选项1");
        dictionaryItemEntity1.setPrimary(Boolean.TRUE);
        dictionaryItemEntity1.setOrder(0);
        dictionaryItemEntity1.setTenantCode("master");
        dictionaryItemEntity1.updateCreator(properties.getSupervisor().getUsername());

        var dictionaryItemEntity2 = new DictionaryItemEntity();
        dictionaryItemEntity2.setDictionaryId(dictionaryEntity.getId());
        dictionaryItemEntity2.setCode("option2");
        dictionaryItemEntity2.setName("测试选项2");
        dictionaryItemEntity2.setPrimary(Boolean.FALSE);
        dictionaryItemEntity2.setOrder(0);
        dictionaryItemEntity2.setTenantCode("master");
        dictionaryItemEntity2.updateCreator(properties.getSupervisor().getUsername());
        this.itemMapper.insertBatch(List.of(dictionaryItemEntity1, dictionaryItemEntity2));

        // 查询数据
        var dictionaryItem = this.provider.findById(dictionaryItemEntity1.getId());
        assertNotNull(dictionaryItem);
        assertNotNull(dictionaryItem.getId());

        // 关联查询
        assertNotNull(dictionaryItem.getDictionary());
        assertEquals(dictionaryEntity.getId(), dictionaryItem.getDictionary().getId());

        // 关联查询
        assertNotNull(dictionaryItem.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), dictionaryItem.getCreator().getId());
        assertNotNull(dictionaryItem.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), dictionaryItem.getModifier().getId());
    }

    /**
     * @see DictionaryProvider#findByIds
     */
    @Test
    public void case2() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var dictionaryEntity = new DictionaryEntity();
        dictionaryEntity.setApplicationId(applicationEntity.getId());
        dictionaryEntity.setCode("test");
        dictionaryEntity.setName("测试字典");
        dictionaryEntity.setEnabled(Boolean.TRUE);
        dictionaryEntity.setRemark("测试");
        dictionaryEntity.setTenantCode("master");
        dictionaryEntity.updateCreator(properties.getSupervisor().getUsername());
        this.dictionaryMapper.insert(dictionaryEntity);

        var dictionaryItemEntity1 = new DictionaryItemEntity();
        dictionaryItemEntity1.setDictionaryId(dictionaryEntity.getId());
        dictionaryItemEntity1.setCode("option1");
        dictionaryItemEntity1.setName("测试选项1");
        dictionaryItemEntity1.setPrimary(Boolean.TRUE);
        dictionaryItemEntity1.setOrder(0);
        dictionaryItemEntity1.setTenantCode("master");
        dictionaryItemEntity1.updateCreator(properties.getSupervisor().getUsername());

        var dictionaryItemEntity2 = new DictionaryItemEntity();
        dictionaryItemEntity2.setDictionaryId(dictionaryEntity.getId());
        dictionaryItemEntity2.setCode("option2");
        dictionaryItemEntity2.setName("测试选项2");
        dictionaryItemEntity2.setPrimary(Boolean.FALSE);
        dictionaryItemEntity2.setOrder(0);
        dictionaryItemEntity2.setTenantCode("master");
        dictionaryItemEntity2.updateCreator(properties.getSupervisor().getUsername());
        this.itemMapper.insertBatch(List.of(dictionaryItemEntity1, dictionaryItemEntity2));

        // 查询数据
        var dictionaryItems = this.provider.findByIds(List.of(dictionaryItemEntity1.getId(), dictionaryItemEntity2.getId()));
        assertNotNull(dictionaryItems);
        assertEquals(2, dictionaryItems.size());

        // 关联查询
        assertTrue(dictionaryItems.stream().allMatch(it -> it.getDictionary() != null && Objects.equals(it.getDictionary().getId(), dictionaryEntity.getId())));
    }

    /**
     * @see DictionaryProvider#findBy
     */
    @Test
    public void case3() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var dictionaryEntity = new DictionaryEntity();
        dictionaryEntity.setApplicationId(applicationEntity.getId());
        dictionaryEntity.setCode("test");
        dictionaryEntity.setName("测试字典");
        dictionaryEntity.setEnabled(Boolean.TRUE);
        dictionaryEntity.setRemark("测试");
        dictionaryEntity.setTenantCode("master");
        dictionaryEntity.updateCreator(properties.getSupervisor().getUsername());
        this.dictionaryMapper.insert(dictionaryEntity);

        var dictionaryItemEntity1 = new DictionaryItemEntity();
        dictionaryItemEntity1.setDictionaryId(dictionaryEntity.getId());
        dictionaryItemEntity1.setCode("option1");
        dictionaryItemEntity1.setName("测试选项1");
        dictionaryItemEntity1.setPrimary(Boolean.TRUE);
        dictionaryItemEntity1.setOrder(0);
        dictionaryItemEntity1.setTenantCode("master");
        dictionaryItemEntity1.updateCreator(properties.getSupervisor().getUsername());

        var dictionaryItemEntity2 = new DictionaryItemEntity();
        dictionaryItemEntity2.setDictionaryId(dictionaryEntity.getId());
        dictionaryItemEntity2.setCode("option2");
        dictionaryItemEntity2.setName("测试选项2");
        dictionaryItemEntity2.setPrimary(Boolean.FALSE);
        dictionaryItemEntity2.setOrder(0);
        dictionaryItemEntity2.setTenantCode("master");
        dictionaryItemEntity2.updateCreator(properties.getSupervisor().getUsername());
        this.itemMapper.insertBatch(List.of(dictionaryItemEntity1, dictionaryItemEntity2));

        // 查询数据
        var dictionaryItems = this.provider.findBy(null, null, Conditions.of(DictionaryItem.class).eq("dictionary.code", "test"), null);
        assertNotNull(dictionaryItems);
        assertEquals(2, dictionaryItems.size());

        // 关联查询
        assertTrue(dictionaryItems.stream().allMatch(it -> it.getDictionary() != null && Objects.equals(it.getDictionary().getId(), dictionaryEntity.getId())));
    }

    /**
     * @see DictionaryProvider#pageBy
     */
    @Test
    public void case4() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var dictionaryEntity = new DictionaryEntity();
        dictionaryEntity.setApplicationId(applicationEntity.getId());
        dictionaryEntity.setCode("test");
        dictionaryEntity.setName("测试字典");
        dictionaryEntity.setEnabled(Boolean.TRUE);
        dictionaryEntity.setRemark("测试");
        dictionaryEntity.setTenantCode("master");
        dictionaryEntity.updateCreator(properties.getSupervisor().getUsername());
        this.dictionaryMapper.insert(dictionaryEntity);

        var dictionaryItemEntity1 = new DictionaryItemEntity();
        dictionaryItemEntity1.setDictionaryId(dictionaryEntity.getId());
        dictionaryItemEntity1.setCode("option1");
        dictionaryItemEntity1.setName("测试选项1");
        dictionaryItemEntity1.setPrimary(Boolean.TRUE);
        dictionaryItemEntity1.setOrder(0);
        dictionaryItemEntity1.setTenantCode("master");
        dictionaryItemEntity1.updateCreator(properties.getSupervisor().getUsername());

        var dictionaryItemEntity2 = new DictionaryItemEntity();
        dictionaryItemEntity2.setDictionaryId(dictionaryEntity.getId());
        dictionaryItemEntity2.setCode("option2");
        dictionaryItemEntity2.setName("测试选项2");
        dictionaryItemEntity2.setPrimary(Boolean.FALSE);
        dictionaryItemEntity2.setOrder(0);
        dictionaryItemEntity2.setTenantCode("master");
        dictionaryItemEntity2.updateCreator(properties.getSupervisor().getUsername());
        this.itemMapper.insertBatch(List.of(dictionaryItemEntity1, dictionaryItemEntity2));

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(DictionaryItem.class).eq("dictionary.code", "test"), null);
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1L, page.getPager().getPageIndex());
        assertEquals(20L, page.getPager().getPageSize());
        assertEquals(1L, page.getPager().getPageCount());
        assertEquals(2L, page.getPager().getItemCount());
        assertNotNull(page.getData());

        // 关联查询
        assertTrue(page.getData().stream().allMatch(it -> it.getDictionary() != null && Objects.equals(it.getDictionary().getId(), dictionaryEntity.getId())));
    }

    /**
     * @see DictionaryProvider#countBy
     */
    @Test
    public void case5() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var dictionaryEntity = new DictionaryEntity();
        dictionaryEntity.setApplicationId(applicationEntity.getId());
        dictionaryEntity.setCode("test");
        dictionaryEntity.setName("测试字典");
        dictionaryEntity.setEnabled(Boolean.TRUE);
        dictionaryEntity.setRemark("测试");
        dictionaryEntity.setTenantCode("master");
        dictionaryEntity.updateCreator(properties.getSupervisor().getUsername());
        this.dictionaryMapper.insert(dictionaryEntity);

        var dictionaryItemEntity1 = new DictionaryItemEntity();
        dictionaryItemEntity1.setDictionaryId(dictionaryEntity.getId());
        dictionaryItemEntity1.setCode("option1");
        dictionaryItemEntity1.setName("测试选项1");
        dictionaryItemEntity1.setPrimary(Boolean.TRUE);
        dictionaryItemEntity1.setOrder(0);
        dictionaryItemEntity1.setTenantCode("master");
        dictionaryItemEntity1.updateCreator(properties.getSupervisor().getUsername());

        var dictionaryItemEntity2 = new DictionaryItemEntity();
        dictionaryItemEntity2.setDictionaryId(dictionaryEntity.getId());
        dictionaryItemEntity2.setCode("option2");
        dictionaryItemEntity2.setName("测试选项2");
        dictionaryItemEntity2.setPrimary(Boolean.FALSE);
        dictionaryItemEntity2.setOrder(0);
        dictionaryItemEntity2.setTenantCode("master");
        dictionaryItemEntity2.updateCreator(properties.getSupervisor().getUsername());
        this.itemMapper.insertBatch(List.of(dictionaryItemEntity1, dictionaryItemEntity2));

        // 查询数据
        var count = this.provider.countBy(Conditions.of(DictionaryItem.class).eq("dictionary.code", "test"));
        assertNotNull(count);
        assertEquals(2, count);
    }

    /**
     * @see DictionaryProvider#insert
     */
    @Test
    public void case6() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var dictionaryEntity = new DictionaryEntity();
        dictionaryEntity.setApplicationId(applicationEntity.getId());
        dictionaryEntity.setCode("test");
        dictionaryEntity.setName("测试字典");
        dictionaryEntity.setEnabled(Boolean.TRUE);
        dictionaryEntity.setRemark("测试");
        dictionaryEntity.setTenantCode("master");
        dictionaryEntity.updateCreator(properties.getSupervisor().getUsername());
        this.dictionaryMapper.insert(dictionaryEntity);

        var input = DictionaryItemInput.builder()
                .dictionaryId(dictionaryEntity.getId())
                .code("option1")
                .name("测试选项1")
                .primary(Boolean.TRUE)
                .order(0)
                .build();

        var dictionaryItem = this.provider.insert(input, properties.getSupervisor().getUsername());
        assertNotNull(dictionaryItem);
        assertNotNull(dictionaryItem.getId());

        assertTrue(this.itemMapper.existsBy(Conditions.of(DictionaryItemEntity.class).eq(DictionaryItemEntity::getId, dictionaryItem.getId())));
    }

    /**
     * @see DictionaryProvider#insertBatch
     */
    @Test
    public void case7() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var dictionaryEntity = new DictionaryEntity();
        dictionaryEntity.setApplicationId(applicationEntity.getId());
        dictionaryEntity.setCode("test");
        dictionaryEntity.setName("测试字典");
        dictionaryEntity.setEnabled(Boolean.TRUE);
        dictionaryEntity.setRemark("测试");
        dictionaryEntity.setTenantCode("master");
        dictionaryEntity.updateCreator(properties.getSupervisor().getUsername());
        this.dictionaryMapper.insert(dictionaryEntity);

        var input = DictionaryItemInput.builder()
                .dictionaryId(dictionaryEntity.getId())
                .code("option1")
                .name("测试选项1")
                .primary(Boolean.TRUE)
                .order(0)
                .build();

        var dictionaryItems = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(dictionaryItems);
        assertEquals(1, dictionaryItems.size());
        assertTrue(dictionaryItems.stream().allMatch(it -> it.getDictionary() != null && Objects.equals(it.getDictionary().getId(), dictionaryEntity.getId())));

        assertTrue(this.itemMapper.existsBy(Conditions.of(DictionaryItemEntity.class).eq(DictionaryItemEntity::getId, dictionaryItems.get(0).getId())));
    }

    /**
     * @see DictionaryProvider#update
     */
    @Test
    public void case8() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var dictionaryEntity = new DictionaryEntity();
        dictionaryEntity.setApplicationId(applicationEntity.getId());
        dictionaryEntity.setCode("test");
        dictionaryEntity.setName("测试字典");
        dictionaryEntity.setEnabled(Boolean.TRUE);
        dictionaryEntity.setRemark("测试");
        dictionaryEntity.setTenantCode("master");
        dictionaryEntity.updateCreator(properties.getSupervisor().getUsername());
        this.dictionaryMapper.insert(dictionaryEntity);

        var dictionaryItemEntity1 = new DictionaryItemEntity();
        dictionaryItemEntity1.setDictionaryId(dictionaryEntity.getId());
        dictionaryItemEntity1.setCode("option1");
        dictionaryItemEntity1.setName("测试选项1");
        dictionaryItemEntity1.setPrimary(Boolean.TRUE);
        dictionaryItemEntity1.setOrder(0);
        dictionaryItemEntity1.setTenantCode("master");
        dictionaryItemEntity1.updateCreator(properties.getSupervisor().getUsername());
        this.itemMapper.insert(dictionaryItemEntity1);

        var item = this.provider.findById(dictionaryItemEntity1.getId());

        var input = item.toInput().toBuilder()
                .code("option0")
                .build();

        item = this.provider.update(input, properties.getSupervisor().getUsername());
        assertNotNull(item);
        assertNotEquals(item.getCreateDate(), item.getModifyDate());

        assertTrue(this.itemMapper.existsBy(Conditions.of(DictionaryItemEntity.class).eq(DictionaryItemEntity::getCode, "option0")));
    }

    /**
     * @see DictionaryProvider#updateBatch
     */
    @Test
    public void case9() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var dictionaryEntity = new DictionaryEntity();
        dictionaryEntity.setApplicationId(applicationEntity.getId());
        dictionaryEntity.setCode("test");
        dictionaryEntity.setName("测试字典");
        dictionaryEntity.setEnabled(Boolean.TRUE);
        dictionaryEntity.setRemark("测试");
        dictionaryEntity.setTenantCode("master");
        dictionaryEntity.updateCreator(properties.getSupervisor().getUsername());
        this.dictionaryMapper.insert(dictionaryEntity);

        var dictionaryItemEntity1 = new DictionaryItemEntity();
        dictionaryItemEntity1.setDictionaryId(dictionaryEntity.getId());
        dictionaryItemEntity1.setCode("option1");
        dictionaryItemEntity1.setName("测试选项1");
        dictionaryItemEntity1.setPrimary(Boolean.TRUE);
        dictionaryItemEntity1.setOrder(0);
        dictionaryItemEntity1.setTenantCode("master");
        dictionaryItemEntity1.updateCreator(properties.getSupervisor().getUsername());
        this.itemMapper.insert(dictionaryItemEntity1);

        var item = this.provider.findById(dictionaryItemEntity1.getId());

        var input = item.toInput().toBuilder()
                .code("option0")
                .build();

        var items = this.provider.updateBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(items);
        assertEquals(1, items.size());

        item = Listx.getFirstOrNull(items);
        assertNotNull(item);
        assertNotEquals(item.getCreateDate(), item.getModifyDate());

        assertTrue(this.itemMapper.existsBy(Conditions.of(DictionaryItemEntity.class).eq(DictionaryItemEntity::getCode, "option0")));
    }

    /**
     * @see DictionaryProvider#deleteByIds
     */
    @Test
    public void case10() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var dictionaryEntity = new DictionaryEntity();
        dictionaryEntity.setApplicationId(applicationEntity.getId());
        dictionaryEntity.setCode("test");
        dictionaryEntity.setName("测试字典");
        dictionaryEntity.setEnabled(Boolean.TRUE);
        dictionaryEntity.setRemark("测试");
        dictionaryEntity.setTenantCode("master");
        dictionaryEntity.updateCreator(properties.getSupervisor().getUsername());
        this.dictionaryMapper.insert(dictionaryEntity);

        var dictionaryItemEntity1 = new DictionaryItemEntity();
        dictionaryItemEntity1.setDictionaryId(dictionaryEntity.getId());
        dictionaryItemEntity1.setCode("option1");
        dictionaryItemEntity1.setName("测试选项1");
        dictionaryItemEntity1.setPrimary(Boolean.TRUE);
        dictionaryItemEntity1.setOrder(0);
        dictionaryItemEntity1.setTenantCode("master");
        dictionaryItemEntity1.updateCreator(properties.getSupervisor().getUsername());
        this.itemMapper.insert(dictionaryItemEntity1);

        var deleted = this.provider.deleteByIds(List.of(dictionaryItemEntity1.getId()));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.itemMapper.existsBy(Conditions.of(DictionaryItemEntity.class).eq(DictionaryItemEntity::getId, dictionaryItemEntity1.getId())));
    }

    /**
     * @see DictionaryProvider#deleteBy(Conditions)
     */
    @Test
    public void case11() {
        var applicationEntity = new ApplicationEntity();
        applicationEntity.setCode("central-security");
        applicationEntity.setName("统一认证");
        applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
        applicationEntity.setUrl("http://127.0.0.1:3100");
        applicationEntity.setContextPath("/security");
        applicationEntity.setSecret(Guidx.nextID());
        applicationEntity.setEnabled(Boolean.TRUE);
        applicationEntity.setRemark("统一认班上");
        applicationEntity.updateCreator(properties.getSupervisor().getUsername());
        this.applicationMapper.insert(applicationEntity);

        var dictionaryEntity = new DictionaryEntity();
        dictionaryEntity.setApplicationId(applicationEntity.getId());
        dictionaryEntity.setCode("test");
        dictionaryEntity.setName("测试字典");
        dictionaryEntity.setEnabled(Boolean.TRUE);
        dictionaryEntity.setRemark("测试");
        dictionaryEntity.setTenantCode("master");
        dictionaryEntity.updateCreator(properties.getSupervisor().getUsername());
        this.dictionaryMapper.insert(dictionaryEntity);

        var dictionaryItemEntity1 = new DictionaryItemEntity();
        dictionaryItemEntity1.setDictionaryId(dictionaryEntity.getId());
        dictionaryItemEntity1.setCode("option1");
        dictionaryItemEntity1.setName("测试选项1");
        dictionaryItemEntity1.setPrimary(Boolean.TRUE);
        dictionaryItemEntity1.setOrder(0);
        dictionaryItemEntity1.setTenantCode("master");
        dictionaryItemEntity1.updateCreator(properties.getSupervisor().getUsername());
        this.itemMapper.insert(dictionaryItemEntity1);

        var deleted = this.provider.deleteBy(Conditions.of(DictionaryItem.class).eq(DictionaryItem::getCode, "option1"));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.itemMapper.existsBy(Conditions.of(DictionaryItemEntity.class).eq(DictionaryItemEntity::getId, dictionaryItemEntity1.getId())));
    }
}
