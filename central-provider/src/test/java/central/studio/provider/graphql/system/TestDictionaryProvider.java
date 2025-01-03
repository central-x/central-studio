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

package central.studio.provider.graphql.system;

import central.data.system.Dictionary;
import central.data.system.DictionaryInput;
import central.data.system.DictionaryItemInput;
import central.provider.graphql.system.DictionaryProvider;
import central.provider.scheduled.DataContext;
import central.provider.scheduled.fetcher.DataFetcherType;
import central.provider.scheduled.fetcher.saas.SaasContainer;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.system.DictionaryPersistence;
import central.studio.provider.database.persistence.system.entity.DictionaryEntity;
import central.studio.provider.graphql.TestContext;
import central.studio.provider.graphql.TestProvider;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    private DictionaryPersistence persistence;

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
        this.persistence.deleteBy(Conditions.of(DictionaryEntity.class).like(DictionaryEntity::getCode, "test%"), tenant.getCode());
    }

    /**
     * @see DictionaryProvider#insert
     * @see DictionaryProvider#findById
     * @see DictionaryProvider#update
     * @see DictionaryProvider#findByIds
     * @see DictionaryProvider#countBy
     * @see DictionaryProvider#deleteByIds
     */
    @Test
    public void case1() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();

        var input = DictionaryInput.builder()
                .applicationId(application.getId())
                .code("test")
                .name("测试字典")
                .enabled(Boolean.TRUE)
                .remark("测试")
                .items(List.of(
                        DictionaryItemInput.builder().code("option1").name("测试选项1").primary(Boolean.TRUE).order(0).build(),
                        DictionaryItemInput.builder().code("option2").name("测试选项2").primary(Boolean.FALSE).order(0).build()
                ))
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", tenant.getCode());
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getApplicationId(), insert.getApplicationId());
        assertEquals(input.getApplicationId(), insert.getApplication().getId());
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getRemark(), insert.getRemark());
        assertEquals(input.getItems().size(), insert.getItems().size());
        assertEquals(input.getItems().get(0), insert.getItems().get(0).toInput().build());
        assertEquals(input.getItems().get(1), insert.getItems().get(1).toInput().build());

        // test findById
        var findById = this.provider.findById(insert.getId(), tenant.getCode());
        assertNotNull(findById);
        assertEquals(insert.getId(), findById.getId());
        assertEquals(insert.getApplicationId(), findById.getApplicationId());
        assertEquals(insert.getApplicationId(), findById.getApplication().getId());
        assertEquals(insert.getCode(), findById.getCode());
        assertEquals(insert.getName(), findById.getName());
        assertEquals(insert.getEnabled(), findById.getEnabled());
        assertEquals(insert.getRemark(), findById.getRemark());
        assertEquals(insert.getItems().size(), findById.getItems().size());
        assertEquals(insert.getItems().get(0), findById.getItems().get(0));
        assertEquals(insert.getItems().get(1), findById.getItems().get(1));

        // test countBy
        var count = this.provider.countBy(Conditions.of(Dictionary.class).like(Dictionary::getCode, "test%"), tenant.getCode());
        assertEquals(1, count);

        // test update
        this.provider.update(insert.toInput().code("test2").enabled(Boolean.FALSE).build(), "syssa", tenant.getCode());

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), tenant.getCode());
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getApplicationId(), fetched.getApplicationId());
        assertEquals(insert.getApplicationId(), fetched.getApplication().getId());
        assertEquals("test2", fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(insert.getRemark(), fetched.getRemark());
        assertEquals(insert.getItems().size(), fetched.getItems().size());
        assertEquals(insert.getItems().get(0), fetched.getItems().get(0));
        assertEquals(insert.getItems().get(1), fetched.getItems().get(1));

        // test deleteByIds
        count = this.provider.deleteByIds(List.of(insert.getId()), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(DictionaryEntity.class).like(DictionaryEntity::getCode, "test%"), tenant.getCode());
        assertEquals(0, count);
    }

    /**
     * @see DictionaryProvider#insertBatch
     * @see DictionaryProvider#findBy
     * @see DictionaryProvider#updateBatch
     * @see DictionaryProvider#pageBy
     * @see DictionaryProvider#deleteBy
     */
    @Test
    public void case2() {
        var tenant = this.context.getTenant();
        var application = this.context.getApplication();

        var input = DictionaryInput.builder()
                .applicationId(application.getId())
                .code("test")
                .name("测试字典")
                .enabled(Boolean.TRUE)
                .remark("测试")
                .items(List.of(
                        DictionaryItemInput.builder().code("option1").name("测试选项1").primary(Boolean.TRUE).order(0).build(),
                        DictionaryItemInput.builder().code("option2").name("测试选项2").primary(Boolean.FALSE).order(0).build()
                ))
                .build();

        // test insertBatch
        var insertBatch = this.provider.insertBatch(List.of(input), "syssa", tenant.getCode());
        assertNotNull(insertBatch);
        assertEquals(1, insertBatch.size());

        var insert = Listx.getFirstOrNull(insertBatch);
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getApplicationId(), insert.getApplicationId());
        assertEquals(input.getApplicationId(), insert.getApplication().getId());
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getRemark(), insert.getRemark());
        assertEquals(input.getItems().size(), insert.getItems().size());
        assertEquals(input.getItems().get(0), insert.getItems().get(0).toInput().build());
        assertEquals(input.getItems().get(1), insert.getItems().get(1).toInput().build());

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(Dictionary.class).like(Dictionary::getCode, "test%"), null, tenant.getCode());
        assertNotNull(findBy);
        assertEquals(1, findBy.size());

        var fetched = Listx.getFirstOrNull(findBy);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getApplicationId(), fetched.getApplicationId());
        assertEquals(insert.getApplicationId(), fetched.getApplication().getId());
        assertEquals(insert.getCode(), fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getEnabled(), fetched.getEnabled());
        assertEquals(insert.getRemark(), fetched.getRemark());
        assertEquals(insert.getItems().size(), fetched.getItems().size());
        assertEquals(insert.getItems().get(0), fetched.getItems().get(0));
        assertEquals(insert.getItems().get(1), fetched.getItems().get(1));

        // test updateBatch
        this.provider.updateBatch(List.of(insert.toInput().code("test2").enabled(Boolean.FALSE).build()), "syssa", tenant.getCode());

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), tenant.getCode());
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        // test pageBy
        var pageBy = this.provider.pageBy(1, 10, Conditions.of(Dictionary.class).like(Dictionary::getCode, "test%"), null, tenant.getCode());
        assertNotNull(pageBy);
        assertEquals(1, pageBy.getPager().getPageIndex());
        assertEquals(10, pageBy.getPager().getPageSize());
        assertEquals(1, pageBy.getPager().getPageCount());
        assertEquals(1, pageBy.getPager().getItemCount());
        assertEquals(1, pageBy.getData().size());

        fetched = Listx.getFirstOrNull(pageBy.getData());
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getApplicationId(), fetched.getApplicationId());
        assertEquals(insert.getApplicationId(), fetched.getApplication().getId());
        assertEquals("test2", fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(insert.getRemark(), fetched.getRemark());
        assertEquals(insert.getItems().size(), fetched.getItems().size());
        assertEquals(insert.getItems().get(0), fetched.getItems().get(0));
        assertEquals(insert.getItems().get(1), fetched.getItems().get(1));

        // test deleteBy
        var count = this.provider.deleteBy(Conditions.of(Dictionary.class).like(Dictionary::getCode, "test%"), tenant.getCode());
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(DictionaryEntity.class).like(DictionaryEntity::getCode, "test%"), tenant.getCode());
        assertEquals(0, count);
    }
}
