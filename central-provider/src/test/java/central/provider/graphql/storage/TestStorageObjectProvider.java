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

package central.provider.graphql.storage;

import central.data.storage.StorageObject;
import central.data.storage.StorageObjectInput;
import central.studio.provider.ApplicationProperties;
import central.studio.provider.ProviderApplication;
import central.studio.provider.graphql.saas.entity.ApplicationEntity;
import central.studio.provider.graphql.saas.mapper.ApplicationMapper;
import central.studio.provider.graphql.saas.mapper.TenantMapper;
import central.studio.provider.graphql.storage.entity.StorageBucketEntity;
import central.studio.provider.graphql.storage.entity.StorageObjectEntity;
import central.studio.provider.graphql.storage.mapper.StorageBucketMapper;
import central.studio.provider.graphql.storage.mapper.StorageObjectMapper;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.security.Digestx;
import central.sql.query.Conditions;
import central.util.Guidx;
import central.util.Jsonx;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StorageObjectProvider Test Cases
 * <p>
 * 文件
 *
 * @author Alan Yeh
 * @since 2022/10/30
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestStorageObjectProvider {
    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    @Setter(onMethod_ = @Autowired)
    private StorageObjectProvider provider;

    @Setter(onMethod_ = @Autowired)
    private StorageObjectMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private StorageBucketMapper bucketMapper;

    @Setter(onMethod_ = @Autowired)
    private ApplicationMapper applicationMapper;

    @Setter(onMethod_ = @Autowired)
    private TenantMapper tenantMapper;

    private static ApplicationEntity applicationEntity;

    @Setter(onMethod_ = @Autowired)
    private DataContext context;

    @BeforeEach
    @AfterEach
    public void clear() throws Exception {
        // 清空测试数据
        this.bucketMapper.deleteAll();
        this.mapper.deleteAll();

        if (applicationEntity == null) {
            this.applicationMapper.deleteAll();
            this.tenantMapper.deleteAll();
            applicationEntity = new ApplicationEntity();
            applicationEntity.setCode("central-security");
            applicationEntity.setName("统一认证中心");
            applicationEntity.setLogoBytes("1234".getBytes(StandardCharsets.UTF_8));
            applicationEntity.setUrl("http://127.0.0.1:3100");
            applicationEntity.setContextPath("/security");
            applicationEntity.setSecret(Guidx.nextID());
            applicationEntity.setEnabled(Boolean.TRUE);
            applicationEntity.setRemark("用于所有应用的认证处理");
            applicationEntity.updateCreator(properties.getSupervisor().getUsername());
            this.applicationMapper.insert(applicationEntity);

            SaasContainer container = null;
            while (container == null || container.getApplications().isEmpty()) {
                Thread.sleep(100);
                container = context.getData(DataFetcherType.SAAS);
            }
        }
    }

    private StorageObjectEntity init() {
        var bucket = new StorageBucketEntity();
        bucket.setApplicationId(applicationEntity.getId());
        bucket.setCode("local");
        bucket.setName("本地文件存储");
        bucket.setType("local");
        bucket.setEnabled(Boolean.TRUE);
        bucket.setRemark("本地文件存储");
        bucket.setParams(Jsonx.Default().serialize(Map.of("path", "./storage")));
        bucket.setTenantCode("master");
        bucket.updateCreator(properties.getSupervisor().getUsername());
        this.bucketMapper.insert(bucket);

        var entity = new StorageObjectEntity();
        entity.setBucketId(bucket.getId());
        entity.setName("测试文件.txt");
        entity.setSize(5 * 1024 * 1024L);
        entity.setDigest(Digestx.SHA256.digest("test", StandardCharsets.UTF_8));
        entity.setKey("test");
        entity.setConfirmed(Boolean.TRUE);
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);
        return entity;
    }

    /**
     * @see StorageObjectProvider#findById
     */
    @Test
    public void case1() {
        var entity = this.init();

        // 查询数据
        var object = this.provider.findById(entity.getId());
        assertNotNull(object);
        assertEquals(entity.getId(), object.getId());
        assertNotNull(object.getBucketId());
        assertNotNull(object.getBucket());
        assertEquals(entity.getBucketId(), object.getBucket().getId());
        assertNotNull(object.getName());
        assertNotNull(object.getExtension());
        assertNotNull(object.getSize());
        assertNotNull(object.getDigest());
        assertNotNull(object.getKey());
        assertNotNull(object.getConfirmed());
        // 关联查询
        assertNotNull(object.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), object.getCreator().getId());
        assertNotNull(object.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), object.getModifier().getId());
    }

    /**
     * @see StorageObjectProvider#findByIds
     */
    @Test
    public void case2() {
        var entity = this.init();

        // 查询数据
        var objects = this.provider.findByIds(List.of(entity.getId()));
        assertNotNull(objects);
        assertEquals(1, objects.size());

        var object = Listx.getFirstOrNull(objects);
        assertNotNull(object);
        assertEquals(entity.getId(), object.getId());
        assertNotNull(object.getBucketId());
        assertNotNull(object.getBucket());
        assertEquals(entity.getBucketId(), object.getBucket().getId());
        assertNotNull(object.getName());
        assertNotNull(object.getExtension());
        assertNotNull(object.getSize());
        assertNotNull(object.getDigest());
        assertNotNull(object.getKey());
        assertNotNull(object.getConfirmed());
        // 关联查询
        assertNotNull(object.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), object.getCreator().getId());
        assertNotNull(object.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), object.getModifier().getId());
    }

    /**
     * @see StorageObjectProvider#findBy
     */
    @Test
    public void case3() {
        var entity = this.init();

        // 查询数据
        var objects = this.provider.findBy(null, null, Conditions.of(StorageObject.class).eq(StorageObject::getKey, "test"), null);
        assertNotNull(objects);
        assertEquals(1, objects.size());

        var object = Listx.getFirstOrNull(objects);
        assertNotNull(object);
        assertEquals(entity.getId(), object.getId());
        assertNotNull(object.getBucketId());
        assertNotNull(object.getBucket());
        assertEquals(entity.getBucketId(), object.getBucket().getId());
        assertNotNull(object.getName());
        assertNotNull(object.getExtension());
        assertNotNull(object.getSize());
        assertNotNull(object.getDigest());
        assertNotNull(object.getKey());
        assertNotNull(object.getConfirmed());
        // 关联查询
        assertNotNull(object.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), object.getCreator().getId());
        assertNotNull(object.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), object.getModifier().getId());
    }

    /**
     * @see StorageObjectProvider#pageBy
     */
    @Test
    public void case4() {
        var entity = init();

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(StorageObject.class).eq(StorageObject::getKey, "test"), null);
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1, page.getPager().getPageIndex());
        assertEquals(20, page.getPager().getPageSize());
        assertEquals(1, page.getPager().getPageCount());
        assertEquals(1, page.getPager().getItemCount());

        var object = Listx.getFirstOrNull(page.getData());
        assertNotNull(object);
        assertEquals(entity.getId(), object.getId());
        assertNotNull(object.getBucketId());
        assertNotNull(object.getBucket());
        assertEquals(entity.getBucketId(), object.getBucket().getId());
        assertNotNull(object.getName());
        assertNotNull(object.getExtension());
        assertNotNull(object.getSize());
        assertNotNull(object.getDigest());
        assertNotNull(object.getKey());
        assertNotNull(object.getConfirmed());
        // 关联查询
        assertNotNull(object.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), object.getCreator().getId());
        assertNotNull(object.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), object.getModifier().getId());
    }

    /**
     * @see StorageObjectProvider#countBy
     */
    @Test
    public void case5() {
        var entity = this.init();

        // 查询数据
        var count = this.provider.countBy(Conditions.of(StorageObject.class).eq(StorageObject::getKey, "test"));
        assertNotNull(count);
        assertEquals(1, count);
    }

    /**
     * @see StorageObjectProvider#insert
     */
    @Test
    public void case6() {
        var entity = this.init();

        var input = StorageObjectInput.builder()
                .bucketId(entity.getBucketId())
                .name("测试文件.txt")
                .size(5 * 1024 * 1024L)
                .digest(Digestx.SHA256.digest("test", StandardCharsets.UTF_8))
                .key("test")
                .confirmed(Boolean.TRUE)
                .build();

        // 查询数据
        var object = this.provider.insert(input, properties.getSupervisor().getUsername());

        assertNotNull(object);
        assertNotNull(object.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(StorageObjectEntity.class).eq(StorageObjectEntity::getId, object.getId())));
    }

    /**
     * @see StorageObjectProvider#insertBatch
     */
    @Test
    public void case7() {
        var entity = this.init();

        var input = StorageObjectInput.builder()
                .bucketId(entity.getBucketId())
                .name("测试文件.txt")
                .size(5 * 1024 * 1024L)
                .digest(Digestx.SHA256.digest("test", StandardCharsets.UTF_8))
                .key("test")
                .confirmed(Boolean.TRUE)
                .build();

        // 查询数据
        var entities = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername());

        assertNotNull(entities);
        assertEquals(1, entities.size());

        var object = Listx.getFirstOrNull(entities);
        assertNotNull(object);
        assertNotNull(object.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(StorageObjectEntity.class).eq(StorageObjectEntity::getId, object.getId())));
    }

    /**
     * @see StorageObjectProvider#update
     */
    @Test
    public void case8() {
        var entity = init();

        // 查询数据
        var object = this.provider.findById(entity.getId());
        assertNotNull(object);
        assertEquals(entity.getId(), object.getId());

        var input = object.toInput().toBuilder()
                .key("test1")
                .build();

        // 更新数据
        object = this.provider.update(input, properties.getSupervisor().getUsername());
        assertNotNull(object);
        assertEquals(entity.getId(), object.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(StorageObjectEntity.class).eq(StorageObjectEntity::getKey, "test1")));
    }

    /**
     * @see StorageObjectProvider#updateBatch
     */
    @Test
    public void case9() {
        var entity = this.init();

        // 查询数据
        var object = this.provider.findById(entity.getId());
        assertNotNull(object);
        assertEquals(entity.getId(), object.getId());

        var input = object.toInput().toBuilder()
                .key("test1")
                .build();

        // 更新数据
        var objects = this.provider.updateBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(objects);
        assertEquals(1, objects.size());

        object = Listx.getFirstOrNull(objects);
        assertNotNull(object);
        assertEquals(entity.getId(), object.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(StorageObjectEntity.class).eq(StorageObjectEntity::getKey, "test1")));
    }

    /**
     * @see StorageObjectProvider#deleteByIds
     */
    @Test
    public void case10() {
        var entity = this.init();

        var deleted = this.provider.deleteByIds(List.of(entity.getId()));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(StorageObjectEntity.class).eq(StorageObjectEntity::getId, entity.getId())));
    }

    /**
     * @see StorageObjectProvider#deleteBy(Conditions)
     */
    @Test
    public void case11() {
        var entity = this.init();

        var deleted = this.provider.deleteBy(Conditions.of(StorageObject.class).eq(StorageObject::getKey, "test"));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(StorageObjectEntity.class).eq(StorageObjectEntity::getId, entity.getId())));
    }
}
