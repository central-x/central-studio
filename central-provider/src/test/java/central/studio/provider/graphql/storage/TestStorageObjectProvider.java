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

import central.data.storage.StorageBucketInput;
import central.data.storage.StorageObject;
import central.data.storage.StorageObjectInput;
import central.provider.graphql.storage.StorageObjectProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.security.Digestx;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.storage.StorageBucketPersistence;
import central.studio.provider.database.persistence.storage.StorageObjectPersistence;
import central.studio.provider.database.persistence.storage.entity.StorageBucketEntity;
import central.studio.provider.database.persistence.storage.entity.StorageObjectEntity;
import central.studio.provider.graphql.TestContext;
import central.util.Jsonx;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    private StorageObjectProvider provider;

    @Setter(onMethod_ = @Autowired)
    private StorageObjectPersistence persistence;

    @Setter(onMethod_ = @Autowired)
    private StorageBucketPersistence bucketPersistence;

    @Setter(onMethod_ = @Autowired)
    private TestContext context;

    @BeforeAll
    public static void setup(@Autowired DataContext context) throws Exception {
        SaasContainer container = null;
        while (container == null || container.getApplications().isEmpty()) {
            Thread.sleep(100);
            container = context.getData(DataFetcherType.SAAS);
        }
    }

    @AfterEach
    public void clear() throws Exception {
        var tenant = this.context.getTenant();
        // 清空测试数据
        this.persistence.deleteBy(Conditions.of(StorageObjectEntity.class), tenant.getCode());
        this.bucketPersistence.deleteBy(Conditions.of(StorageBucketEntity.class).like(StorageBucketEntity::getCode, "test%"), tenant.getCode());
    }

    /**
     * @see StorageObjectProvider#insert
     * @see StorageObjectProvider#findById
     * @see StorageObjectProvider#update
     * @see StorageObjectProvider#findByIds
     * @see StorageObjectProvider#countBy
     * @see StorageObjectProvider#deleteByIds
     */
    @Test
    public void case1() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();
        var bucket = this.bucketPersistence.insert(StorageBucketInput.builder()
                .applicationId(application.getId())
                .code("test")
                .name("测试文件存储")
                .type("local")
                .enabled(Boolean.TRUE)
                .remark("测试文件存储")
                .params(Jsonx.Default().serialize(Map.of("path", "./storage")))
                .build(), "syssa", tenant.getCode());

        var input = StorageObjectInput.builder()
                .bucketId(bucket.getId())
                .name("测试文件.txt")
                .size(5 * 1024 * 1024L)
                .digest(Digestx.SHA256.digest("test", StandardCharsets.UTF_8))
                .key("test")
                .confirmed(Boolean.FALSE)
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", tenant.getCode());
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getBucketId(), insert.getBucketId());
        assertEquals(input.getBucketId(), insert.getBucket().getId());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getSize(), insert.getSize());
        assertEquals(input.getDigest(), insert.getDigest());
        assertEquals(input.getKey(), insert.getKey());
        assertEquals(input.getConfirmed(), insert.getConfirmed());

        // test findById
        var findById = this.provider.findById(insert.getId(), tenant.getCode());
        assertNotNull(findById);
        assertEquals(insert.getId(), findById.getId());
        assertEquals(input.getBucketId(), findById.getBucketId());
        assertEquals(input.getBucketId(), findById.getBucket().getId());
        assertEquals(input.getName(), findById.getName());
        assertEquals(input.getSize(), findById.getSize());
        assertEquals(input.getDigest(), findById.getDigest());
        assertEquals(input.getKey(), findById.getKey());
        assertEquals(input.getConfirmed(), findById.getConfirmed());

        // test countBy
        var count = this.provider.countBy(Conditions.of(StorageObject.class).eq(StorageObject::getBucketId, bucket.getId()), tenant.getCode());
        assertEquals(1, count);

        // test update
        this.provider.update(insert.toInput().confirmed(Boolean.TRUE).build(), "syssa", tenant.getCode());

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), tenant.getCode());
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(input.getBucketId(), fetched.getBucketId());
        assertEquals(input.getBucketId(), fetched.getBucket().getId());
        assertEquals(input.getName(), fetched.getName());
        assertEquals(input.getSize(), fetched.getSize());
        assertEquals(input.getDigest(), fetched.getDigest());
        assertEquals(input.getKey(), fetched.getKey());
        assertEquals(Boolean.TRUE, fetched.getConfirmed());

        // test deleteByIds
        count = this.provider.deleteByIds(List.of(insert.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(StorageObjectEntity.class).eq(StorageObjectEntity::getBucketId, bucket.getId()), tenant.getCode());
        assertEquals(0, count);
    }

    /**
     * @see StorageObjectProvider#insertBatch
     * @see StorageObjectProvider#findBy
     * @see StorageObjectProvider#updateBatch
     * @see StorageObjectProvider#pageBy
     * @see StorageObjectProvider#deleteBy
     */
    @Test
    public void case2() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();
        var bucket = this.bucketPersistence.insert(StorageBucketInput.builder()
                .applicationId(application.getId())
                .code("test")
                .name("测试文件存储")
                .type("local")
                .enabled(Boolean.TRUE)
                .remark("测试文件存储")
                .params(Jsonx.Default().serialize(Map.of("path", "./storage")))
                .build(), "syssa", tenant.getCode());

        var input = StorageObjectInput.builder()
                .bucketId(bucket.getId())
                .name("测试文件.txt")
                .size(5 * 1024 * 1024L)
                .digest(Digestx.SHA256.digest("test", StandardCharsets.UTF_8))
                .key("test")
                .confirmed(Boolean.FALSE)
                .build();

        // test insertBatch
        var insertBatch = this.provider.insertBatch(List.of(input), "syssa", tenant.getCode());
        assertNotNull(insertBatch);
        assertEquals(1, insertBatch.size());

        var insert = Listx.getFirstOrNull(insertBatch);
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getBucketId(), insert.getBucketId());
        assertEquals(input.getBucketId(), insert.getBucket().getId());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getSize(), insert.getSize());
        assertEquals(input.getDigest(), insert.getDigest());
        assertEquals(input.getKey(), insert.getKey());
        assertEquals(input.getConfirmed(), insert.getConfirmed());

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(StorageObject.class).eq(StorageObject::getBucketId, bucket.getId()), null, tenant.getCode());
        assertNotNull(findBy);
        assertEquals(1, findBy.size());

        var fetched = Listx.getFirstOrNull(findBy);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getBucketId(), fetched.getBucketId());
        assertEquals(insert.getBucketId(), fetched.getBucket().getId());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getSize(), fetched.getSize());
        assertEquals(insert.getDigest(), fetched.getDigest());
        assertEquals(insert.getKey(), fetched.getKey());
        assertEquals(insert.getConfirmed(), fetched.getConfirmed());

        // test updateBatch
        this.provider.updateBatch(List.of(insert.toInput().confirmed(Boolean.TRUE).build()), "syssa", tenant.getCode());

        // test pageBy
        var pageBy = this.provider.pageBy(1, 10, Conditions.of(StorageObject.class).eq(StorageObject::getBucketId, bucket.getId()), null, tenant.getCode());
        assertNotNull(pageBy);
        assertEquals(1, pageBy.getPager().getPageIndex());
        assertEquals(10, pageBy.getPager().getPageSize());
        assertEquals(1, pageBy.getPager().getPageCount());
        assertEquals(1, pageBy.getPager().getItemCount());
        assertEquals(1, pageBy.getData().size());

        fetched = Listx.getFirstOrNull(pageBy.getData());
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getBucketId(), fetched.getBucketId());
        assertEquals(insert.getBucketId(), fetched.getBucket().getId());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getSize(), fetched.getSize());
        assertEquals(insert.getDigest(), fetched.getDigest());
        assertEquals(insert.getKey(), fetched.getKey());
        assertEquals(Boolean.TRUE, fetched.getConfirmed());

        // test deleteBy
        var count = this.provider.deleteBy(Conditions.of(StorageObject.class).eq(StorageObject::getBucketId, bucket.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(StorageObjectEntity.class).eq(StorageObjectEntity::getBucketId, bucket.getId()), tenant.getCode());
        assertEquals(0, count);
    }
}
