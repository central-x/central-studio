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

package central.studio.provider.graphql.storage;

import central.data.storage.StorageBucket;
import central.data.storage.StorageBucketInput;
import central.lang.reflect.TypeRef;
import central.provider.graphql.storage.StorageBucketProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.ProviderProperties;
import central.studio.provider.graphql.saas.entity.ApplicationEntity;
import central.studio.provider.graphql.saas.mapper.ApplicationMapper;
import central.studio.provider.graphql.saas.mapper.TenantMapper;
import central.studio.provider.graphql.storage.entity.StorageBucketEntity;
import central.studio.provider.graphql.storage.mapper.StorageBucketMapper;
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
 * StorageBucketProvider Test Cases
 * <p>
 * 存储桶
 *
 * @author Alan Yeh
 * @since 2022/10/30
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestStorageBucketProvider {

    @Setter(onMethod_ = @Autowired)
    private ProviderProperties properties;

    @Setter(onMethod_ = @Autowired)
    private StorageBucketProvider provider;

    @Setter(onMethod_ = @Autowired)
    private StorageBucketMapper mapper;

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
            applicationEntity.setRoutesJson("[]");
            applicationEntity.updateCreator(properties.getSupervisor().getUsername());
            this.applicationMapper.insert(applicationEntity);

            SaasContainer container = null;
            while (container == null || container.getApplications().isEmpty()) {
                Thread.sleep(100);
                container = context.getData(DataFetcherType.SAAS);
            }
        }
    }


    /**
     * @see StorageBucketProvider#findById
     */
    @Test
    public void case1() {
        var entity = new StorageBucketEntity();
        entity.setApplicationId(applicationEntity.getId());
        entity.setCode("local");
        entity.setName("本地文件存储");
        entity.setType("local");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("本地文件存储");
        entity.setParams(Jsonx.Default().serialize(Map.of("path", "./storage")));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var bucket = this.provider.findById(entity.getId(), "master");
        assertNotNull(bucket);
        assertEquals(entity.getId(), bucket.getId());
        assertNotNull(bucket.getCode());
        assertNotNull(bucket.getName());
        assertNotNull(bucket.getType());
        assertNotNull(bucket.getEnabled());
        assertNotNull(bucket.getRemark());
        assertNotNull(bucket.getParams());
        var params = Jsonx.Default().deserialize(bucket.getParams(), TypeRef.ofMap(String.class, Object.class));
        assertNotNull(params);
        assertEquals(1, params.size());
        // 关联查询
        assertNotNull(bucket.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), bucket.getCreator().getId());
        assertNotNull(bucket.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), bucket.getModifier().getId());
    }

    /**
     * @see StorageBucketProvider#findByIds
     */
    @Test
    public void case2() {
        var entity = new StorageBucketEntity();
        entity.setApplicationId(applicationEntity.getId());
        entity.setCode("local");
        entity.setName("本地文件存储");
        entity.setType("local");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("本地文件存储");
        entity.setParams(Jsonx.Default().serialize(Map.of("path", "./storage")));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var buckets = this.provider.findByIds(List.of(entity.getId()), "master");
        assertNotNull(buckets);
        assertEquals(1, buckets.size());

        var bucket = Listx.getFirstOrNull(buckets);
        assertNotNull(bucket);
        assertEquals(entity.getId(), bucket.getId());
        assertNotNull(bucket.getCode());
        assertNotNull(bucket.getName());
        assertNotNull(bucket.getType());
        assertNotNull(bucket.getEnabled());
        assertNotNull(bucket.getRemark());
        assertNotNull(bucket.getParams());
        // 关联查询
        assertNotNull(bucket.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), bucket.getCreator().getId());
        assertNotNull(bucket.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), bucket.getModifier().getId());
    }

    /**
     * @see StorageBucketProvider#findBy
     */
    @Test
    public void case3() {
        var entity = new StorageBucketEntity();
        entity.setApplicationId(applicationEntity.getId());
        entity.setCode("local");
        entity.setName("本地文件存储");
        entity.setType("local");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("本地文件存储");
        entity.setParams(Jsonx.Default().serialize(Map.of("path", "./storage")));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var buckets = this.provider.findBy(null, null, Conditions.of(StorageBucket.class).eq(StorageBucket::getCode, "local"), null, "master");
        assertNotNull(buckets);
        assertEquals(1, buckets.size());

        var bucket = Listx.getFirstOrNull(buckets);
        assertNotNull(bucket);
        assertEquals(entity.getId(), bucket.getId());
        assertNotNull(bucket.getCode());
        assertNotNull(bucket.getName());
        assertNotNull(bucket.getType());
        assertNotNull(bucket.getEnabled());
        assertNotNull(bucket.getRemark());
        assertNotNull(bucket.getParams());
        // 关联查询
        assertNotNull(bucket.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), bucket.getCreator().getId());
        assertNotNull(bucket.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), bucket.getModifier().getId());
    }

    /**
     * @see StorageBucketProvider#pageBy
     */
    @Test
    public void case4() {
        var entity = new StorageBucketEntity();
        entity.setApplicationId(applicationEntity.getId());
        entity.setCode("local");
        entity.setName("本地文件存储");
        entity.setType("local");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("本地文件存储");
        entity.setParams(Jsonx.Default().serialize(Map.of("path", "./storage")));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(StorageBucket.class).eq(StorageBucket::getCode, "local"), null, "master");
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1, page.getPager().getPageIndex());
        assertEquals(20, page.getPager().getPageSize());
        assertEquals(1, page.getPager().getPageCount());
        assertEquals(1, page.getPager().getItemCount());

        var bucket = Listx.getFirstOrNull(page.getData());
        assertNotNull(bucket);
        assertEquals(entity.getId(), bucket.getId());
        assertNotNull(bucket.getCode());
        assertNotNull(bucket.getName());
        assertNotNull(bucket.getType());
        assertNotNull(bucket.getEnabled());
        assertNotNull(bucket.getRemark());
        assertNotNull(bucket.getParams());
        // 关联查询
        assertNotNull(bucket.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), bucket.getCreator().getId());
        assertNotNull(bucket.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), bucket.getModifier().getId());
    }

    /**
     * @see StorageBucketProvider#countBy
     */
    @Test
    public void case5() {
        var entity = new StorageBucketEntity();
        entity.setApplicationId(applicationEntity.getId());
        entity.setCode("local");
        entity.setName("本地文件存储");
        entity.setType("local");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("本地文件存储");
        entity.setParams(Jsonx.Default().serialize(Map.of("path", "./storage")));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var count = this.provider.countBy(Conditions.of(StorageBucket.class).eq(StorageBucket::getCode, "local"), "master");
        assertNotNull(count);
        assertEquals(1, count);
    }

    /**
     * @see StorageBucketProvider#insert
     */
    @Test
    public void case6() {
        var input = StorageBucketInput.builder()
                .applicationId(applicationEntity.getId())
                .code("local")
                .name("本地文件存储")
                .type("local")
                .enabled(Boolean.TRUE)
                .remark("本地文件存储")
                .params(Jsonx.Default().serialize(Map.of("path", "./storage")))
                .build();

        // 查询数据
        var entity = this.provider.insert(input, properties.getSupervisor().getUsername(), "master");

        assertNotNull(entity);
        assertNotNull(entity.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(StorageBucketEntity.class).eq(StorageBucketEntity::getId, entity.getId())));
    }

    /**
     * @see StorageBucketProvider#insertBatch
     */
    @Test
    public void case7() {
        var input = StorageBucketInput.builder()
                .applicationId(applicationEntity.getId())
                .code("local")
                .name("本地文件存储")
                .type("local")
                .enabled(Boolean.TRUE)
                .remark("本地文件存储")
                .params(Jsonx.Default().serialize(Map.of("path", "./storage")))
                .build();

        // 查询数据
        var entities = this.provider.insertBatch(List.of(input), properties.getSupervisor().getUsername(), "master");

        assertNotNull(entities);
        assertEquals(1, entities.size());

        var entity = Listx.getFirstOrNull(entities);
        assertNotNull(entity);
        assertNotNull(entity.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(StorageBucketEntity.class).eq(StorageBucketEntity::getId, entity.getId())));
    }

    /**
     * @see StorageBucketProvider#update
     */
    @Test
    public void case8() {
        var entity = new StorageBucketEntity();
        entity.setApplicationId(applicationEntity.getId());
        entity.setCode("local");
        entity.setName("本地文件存储");
        entity.setType("local");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("本地文件存储");
        entity.setParams(Jsonx.Default().serialize(Map.of("path", "./storage")));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var bucket = this.provider.findById(entity.getId(), "master");
        assertNotNull(bucket);
        assertEquals(entity.getId(), bucket.getId());

        var input = bucket.toInput().toBuilder()
                .code("test1")
                .build();

        // 更新数据
        bucket = this.provider.update(input, properties.getSupervisor().getUsername(), "master");
        assertNotNull(bucket);
        assertEquals(entity.getId(), bucket.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(StorageBucketEntity.class).eq(StorageBucketEntity::getCode, "test1")));
    }

    /**
     * @see StorageBucketProvider#updateBatch
     */
    @Test
    public void case9() {
        var entity = new StorageBucketEntity();
        entity.setApplicationId(applicationEntity.getId());
        entity.setCode("local");
        entity.setName("本地文件存储");
        entity.setType("local");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("本地文件存储");
        entity.setParams(Jsonx.Default().serialize(Map.of("path", "./storage")));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var bucket = this.provider.findById(entity.getId(), "master");
        assertNotNull(bucket);
        assertEquals(entity.getId(), bucket.getId());

        var input = bucket.toInput().toBuilder()
                .code("test1")
                .build();

        // 更新数据
        var buckets = this.provider.updateBatch(List.of(input), properties.getSupervisor().getUsername(), "master");
        assertNotNull(buckets);
        assertEquals(1, buckets.size());

        bucket = Listx.getFirstOrNull(buckets);
        assertNotNull(bucket);
        assertEquals(entity.getId(), bucket.getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(StorageBucketEntity.class).eq(StorageBucketEntity::getCode, "test1")));
    }

    /**
     * @see StorageBucketProvider#deleteByIds
     */
    @Test
    public void case10() {
        var entity = new StorageBucketEntity();
        entity.setApplicationId(applicationEntity.getId());
        entity.setCode("local");
        entity.setName("本地文件存储");
        entity.setType("local");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("本地文件存储");
        entity.setParams(Jsonx.Default().serialize(Map.of("path", "./storage")));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var deleted = this.provider.deleteByIds(List.of(entity.getId()), "master");
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(StorageBucketEntity.class).eq(StorageBucketEntity::getId, entity.getId())));
    }

    /**
     * @see StorageBucketProvider#deleteBy(Conditions)
     */
    @Test
    public void case11() {

        var entity = new StorageBucketEntity();
        entity.setApplicationId(applicationEntity.getId());
        entity.setCode("local");
        entity.setName("本地文件存储");
        entity.setType("local");
        entity.setEnabled(Boolean.TRUE);
        entity.setRemark("本地文件存储");
        entity.setParams(Jsonx.Default().serialize(Map.of("path", "./storage")));
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        var deleted = this.provider.deleteBy(Conditions.of(StorageBucket.class).eq(StorageBucket::getCode, "local"), "master");
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(StorageBucketEntity.class).eq(StorageBucketEntity::getId, entity.getId())));
    }
}
