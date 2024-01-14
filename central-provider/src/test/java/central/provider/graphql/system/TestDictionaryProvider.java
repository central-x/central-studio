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

import central.data.system.Dictionary;
import central.data.system.DictionaryInput;
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

/**
 * Dictionary Provider Test Cases
 * 字典
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestDictionaryProvider extends TestProvider {
    @Setter(onMethod_ = @Autowired)
    private DictionaryProvider provider;

    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    @Setter(onMethod_ = @Autowired)
    private DictionaryMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private DictionaryItemMapper itemMapper;

    @Setter(onMethod_ = @Autowired)
    private ApplicationMapper applicationMapper;

    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空数据
        mapper.deleteAll();
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
        this.mapper.insert(dictionaryEntity);

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
        var dictionary = this.provider.findById(dictionaryEntity.getId());
        assertNotNull(dictionary);
        assertNotNull(dictionary.getId());

        // 关联查询
        assertNotNull(dictionary.getApplication());
        assertEquals(applicationEntity.getId(), dictionary.getApplication().getId());

        // 关联查询
        assertNotNull(dictionary.getItems());
        assertEquals(2, dictionary.getItems().size());
        assertTrue(dictionary.getItems().stream().anyMatch(it -> Objects.equals("option1", it.getCode())));
        assertTrue(dictionary.getItems().stream().anyMatch(it -> Objects.equals("option2", it.getCode())));

        // 关联查询
        assertNotNull(dictionary.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), dictionary.getCreator().getId());
        assertNotNull(dictionary.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), dictionary.getModifier().getId());
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
        this.mapper.insert(dictionaryEntity);

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
        var dictionaries = this.provider.findByIds(List.of(dictionaryEntity.getId()));
        assertNotNull(dictionaries);
        assertEquals(1, dictionaries.size());

        var dictionary = Listx.getFirstOrNull(dictionaries);

        assertNotNull(dictionary);
        assertNotNull(dictionary.getId());

        // 关联查询
        assertNotNull(dictionary.getApplication());
        assertEquals(applicationEntity.getId(), dictionary.getApplication().getId());

        // 关联查询
        assertNotNull(dictionary.getItems());
        assertEquals(2, dictionary.getItems().size());
        assertTrue(dictionary.getItems().stream().anyMatch(it -> Objects.equals("option1", it.getCode())));
        assertTrue(dictionary.getItems().stream().anyMatch(it -> Objects.equals("option2", it.getCode())));

        // 关联查询
        assertNotNull(dictionary.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), dictionary.getCreator().getId());
        assertNotNull(dictionary.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), dictionary.getModifier().getId());
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
        this.mapper.insert(dictionaryEntity);

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
        var dictionaries = this.provider.findBy(null, null, Conditions.of(Dictionary.class).eq(Dictionary::getCode, "test"), null);
        assertNotNull(dictionaries);
        assertEquals(1, dictionaries.size());

        var dictionary = Listx.getFirstOrNull(dictionaries);

        assertNotNull(dictionary);
        assertNotNull(dictionary.getId());

        // 关联查询
        assertNotNull(dictionary.getApplication());
        assertEquals(applicationEntity.getId(), dictionary.getApplication().getId());

        // 关联查询
        assertNotNull(dictionary.getItems());
        assertEquals(2, dictionary.getItems().size());
        assertTrue(dictionary.getItems().stream().anyMatch(it -> Objects.equals("option1", it.getCode())));
        assertTrue(dictionary.getItems().stream().anyMatch(it -> Objects.equals("option2", it.getCode())));

        // 关联查询
        assertNotNull(dictionary.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), dictionary.getCreator().getId());
        assertNotNull(dictionary.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), dictionary.getModifier().getId());
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
        this.mapper.insert(dictionaryEntity);

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
        var page = this.provider.pageBy(1L, 20L, Conditions.of(Dictionary.class).eq(Dictionary::getCode, "test"), null);
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1L, page.getPager().getPageIndex());
        assertEquals(20L, page.getPager().getPageSize());
        assertEquals(1L, page.getPager().getPageCount());
        assertEquals(1L, page.getPager().getItemCount());
        assertNotNull(page.getData());

        var dictionary = Listx.getFirstOrNull(page.getData());

        assertNotNull(dictionary);
        assertNotNull(dictionary.getId());

        // 关联查询
        assertNotNull(dictionary.getApplication());
        assertEquals(applicationEntity.getId(), dictionary.getApplication().getId());

        // 关联查询
        assertNotNull(dictionary.getItems());
        assertEquals(2, dictionary.getItems().size());
        assertTrue(dictionary.getItems().stream().anyMatch(it -> Objects.equals("option1", it.getCode())));
        assertTrue(dictionary.getItems().stream().anyMatch(it -> Objects.equals("option2", it.getCode())));

        // 关联查询
        assertNotNull(dictionary.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), dictionary.getCreator().getId());
        assertNotNull(dictionary.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), dictionary.getModifier().getId());
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
        this.mapper.insert(dictionaryEntity);

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
        var count = this.provider.countBy(Conditions.of(Dictionary.class).eq(Dictionary::getCode, "test"));
        assertNotNull(count);
        assertEquals(1, count);
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

        var input = DictionaryInput.builder()
                .applicationId(applicationEntity.getId())
                .code("test")
                .name("测试字典")
                .enabled(Boolean.TRUE)
                .remark("测试")
                .build();

        var dictionary = this.provider.insert(input, properties.getSupervisor().getUsername());
        assertNotNull(dictionary);
        assertNotNull(dictionary.getId());

        assertTrue(this.mapper.existsBy(Conditions.of(DictionaryEntity.class).eq(DictionaryEntity::getId, dictionary.getId())));
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

        var input = DictionaryInput.builder()
                .applicationId(applicationEntity.getId())
                .code("test")
                .name("测试字典")
                .enabled(Boolean.TRUE)
                .remark("测试")
                .build();

        var dictionaries = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(dictionaries);
        assertEquals(1, dictionaries.size());
        assertNotNull(dictionaries.get(0).getId());

        assertTrue(this.mapper.existsBy(Conditions.of(DictionaryEntity.class).eq(DictionaryEntity::getId, dictionaries.get(0).getId())));
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
        this.mapper.insert(dictionaryEntity);

        var dictionary = this.provider.findById(dictionaryEntity.getId());
        assertNotNull(dictionary);

        var input = dictionary.toInput().toBuilder()
                .code("example")
                .build();

        dictionary = this.provider.update(input, properties.getSupervisor().getUsername());
        assertNotNull(dictionary);
        assertNotEquals(dictionary.getCreateDate(), dictionary.getModifyDate());

        assertTrue(this.mapper.existsBy(Conditions.of(DictionaryEntity.class).eq(DictionaryEntity::getCode, "example")));
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
        this.mapper.insert(dictionaryEntity);

        var dictionary = this.provider.findById(dictionaryEntity.getId());
        assertNotNull(dictionary);

        var input = dictionary.toInput().toBuilder()
                .code("example")
                .build();

        var dictionaries = this.provider.updateBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(dictionaries);
        assertEquals(1, dictionaries.size());
        assertTrue(dictionaries.stream().noneMatch(it -> Objects.equals(it.getCreateDate(), it.getModifyDate())));

        assertTrue(this.mapper.existsBy(Conditions.of(DictionaryEntity.class).eq(DictionaryEntity::getCode, "example")));
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
        this.mapper.insert(dictionaryEntity);

        var deleted = this.provider.deleteByIds(List.of(dictionaryEntity.getId()));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(DictionaryEntity.class).eq(DictionaryEntity::getId, dictionaryEntity.getId())));
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
        this.mapper.insert(dictionaryEntity);

        var deleted = this.provider.deleteBy(Conditions.of(Dictionary.class).eq(Dictionary::getCode, "test"));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(DictionaryEntity.class).eq(DictionaryEntity::getId, dictionaryEntity.getId())));
    }
}
