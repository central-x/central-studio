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

package central.studio.provider.graphql.saas;

import central.data.saas.Application;
import central.data.saas.ApplicationInput;
import central.data.saas.ApplicationRouteInput;
import central.provider.graphql.saas.ApplicationProvider;
import central.sql.query.Conditions;
import central.studio.provider.ProviderApplication;
import central.studio.provider.database.persistence.saas.ApplicationPersistence;
import central.studio.provider.database.persistence.saas.entity.ApplicationEntity;
import central.studio.provider.graphql.TestProvider;
import central.util.Guidx;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Application Provider Test Cases
 * 应用
 *
 * @author Alan Yeh
 * @since 2022/09/28
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestApplicationProvider extends TestProvider {

    @Setter(onMethod_ = @Autowired)
    private ApplicationProvider provider;

    @Setter(onMethod_ = @Autowired)
    private ApplicationPersistence persistence;

    @AfterEach
    public void clear() throws Exception {
        // 清空测试数据
        this.persistence.deleteBy(Conditions.of(ApplicationEntity.class).like(ApplicationEntity::getCode, "test%"));
    }

    /**
     * @see ApplicationProvider#insert
     * @see ApplicationProvider#findById
     * @see ApplicationProvider#update
     * @see ApplicationProvider#findByIds
     * @see ApplicationProvider#countBy
     * @see ApplicationProvider#deleteByIds
     */
    @Test
    public void case1() {
        var input = ApplicationInput.builder()
                .code("test")
                .name("测试应用")
                .logo("1234")
                .url("http://127.0.0.1:3100")
                .contextPath("/test")
                .secret(Guidx.nextID())
                .enabled(Boolean.TRUE)
                .remark("测试应用")
                .routes(List.of(
                        ApplicationRouteInput.builder().contextPath("/test/test").url("http://127.0.0.1:3110").enabled(Boolean.TRUE).remark("测试路由1").build()
                ))
                .build();

        // test insert
        var insert = this.provider.insert(input, "syssa", "master");
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getLogo(), insert.getLogo());
        assertEquals(input.getUrl(), insert.getUrl());
        assertEquals(input.getContextPath(), insert.getContextPath());
        assertEquals(input.getSecret(),insert.getSecret());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getRemark(), insert.getRemark());
        assertEquals(1, insert.getRoutes().size());
        assertEquals(input.getRoutes().get(0), insert.getRoutes().get(0).toInput().build());

        // test findById
        var findById = this.provider.findById(insert.getId(), "master");
        assertNotNull(findById);
        assertEquals(insert.getId(), findById.getId());
        assertEquals(insert.getCode(), findById.getCode());
        assertEquals(insert.getName(), findById.getName());
        assertEquals(insert.getLogo(), findById.getLogo());
        assertEquals(insert.getUrl(), findById.getUrl());
        assertEquals(insert.getContextPath(), findById.getContextPath());
        assertEquals(insert.getSecret(),findById.getSecret());
        assertEquals(insert.getEnabled(), findById.getEnabled());
        assertEquals(insert.getRemark(), findById.getRemark());
        assertEquals(1, findById.getRoutes().size());
        assertEquals(insert.getRoutes().get(0), findById.getRoutes().get(0));

        // test countBy
        var count = this.provider.countBy(Conditions.of(Application.class).like(Application::getCode, "test%"), "master");
        assertEquals(1, count);

        // test update
        this.provider.update(insert.toInput().code("test2").enabled(Boolean.FALSE).build(), "syssa", "master");

        // test findByIds
        var findByIds = this.provider.findByIds(List.of(insert.getId()), "master");
        assertNotNull(findByIds);
        assertEquals(1, findByIds.size());

        var fetched = Listx.getFirstOrNull(findByIds);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals("test2", fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getLogo(), fetched.getLogo());
        assertEquals(insert.getUrl(), fetched.getUrl());
        assertEquals(insert.getContextPath(), fetched.getContextPath());
        assertEquals(insert.getSecret(),fetched.getSecret());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(insert.getRemark(), fetched.getRemark());
        assertEquals(1, fetched.getRoutes().size());
        assertEquals(insert.getRoutes().get(0), fetched.getRoutes().get(0));

        // test deleteById
        count = this.provider.deleteByIds(List.of(insert.getId()), "master");
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(ApplicationEntity.class).like(ApplicationEntity::getCode, "test%"));
        assertEquals(0, count);
    }

    /**
     * @see ApplicationProvider#insertBatch
     * @see ApplicationProvider#findBy
     * @see ApplicationProvider#updateBatch
     * @see ApplicationProvider#pageBy
     * @see ApplicationProvider#deleteBy
     */
    @Test
    public void case2() {
        var input = ApplicationInput.builder()
                .code("test")
                .name("测试应用")
                .logo("1234")
                .url("http://127.0.0.1:3100")
                .contextPath("/test")
                .secret(Guidx.nextID())
                .enabled(Boolean.TRUE)
                .remark("测试应用")
                .routes(List.of(
                        ApplicationRouteInput.builder().contextPath("/test/test").url("http://127.0.0.1:3110").enabled(Boolean.TRUE).remark("测试路由1").build()
                ))
                .build();

        // test insertBatch
        var insertBatch = this.provider.insertBatch(List.of(input), "syssa", "master");
        assertNotNull(insertBatch);
        assertEquals(1, insertBatch.size());

        var insert = Listx.getFirstOrNull(insertBatch);
        assertNotNull(insert);
        assertNotNull(insert.getId());
        assertEquals(input.getCode(), insert.getCode());
        assertEquals(input.getName(), insert.getName());
        assertEquals(input.getLogo(), insert.getLogo());
        assertEquals(input.getUrl(), insert.getUrl());
        assertEquals(input.getContextPath(), insert.getContextPath());
        assertEquals(input.getSecret(),insert.getSecret());
        assertEquals(input.getEnabled(), insert.getEnabled());
        assertEquals(input.getRemark(), insert.getRemark());
        assertEquals(1, insert.getRoutes().size());
        assertEquals(input.getRoutes().get(0), insert.getRoutes().get(0).toInput().build());

        // test findBy
        var findBy = this.provider.findBy(null, null, Conditions.of(Application.class).like(Application::getCode, "test%"), null, "master");
        assertNotNull(findBy);
        assertEquals(1, findBy.size());

        var fetched = Listx.getFirstOrNull(findBy);
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals(insert.getCode(), fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getLogo(), fetched.getLogo());
        assertEquals(insert.getUrl(), fetched.getUrl());
        assertEquals(insert.getContextPath(), fetched.getContextPath());
        assertEquals(insert.getSecret(),fetched.getSecret());
        assertEquals(insert.getEnabled(), fetched.getEnabled());
        assertEquals(insert.getRemark(), fetched.getRemark());
        assertEquals(1, fetched.getRoutes().size());
        assertEquals(insert.getRoutes().get(0), fetched.getRoutes().get(0));

        // test countBy
        var count = this.provider.countBy(Conditions.of(Application.class).like(Application::getCode, "test%"), "master");
        assertEquals(1, count);

        // test update
        this.provider.update(insert.toInput().code("test2").enabled(Boolean.FALSE).build(), "syssa", "master");

        // test findByIds
        var pageBy = this.provider.pageBy(1, 10, Conditions.of(Application.class).like(Application::getCode, "test%"), null, "master");
        assertNotNull(pageBy);
        assertEquals(1, pageBy.getPager().getPageIndex());
        assertEquals(10, pageBy.getPager().getPageSize());
        assertEquals(1, pageBy.getPager().getPageCount());
        assertEquals(1, pageBy.getPager().getItemCount());
        assertEquals(1, pageBy.getData().size());

        fetched = Listx.getFirstOrNull(pageBy.getData());
        assertNotNull(fetched);
        assertEquals(insert.getId(), fetched.getId());
        assertEquals("test2", fetched.getCode());
        assertEquals(insert.getName(), fetched.getName());
        assertEquals(insert.getLogo(), fetched.getLogo());
        assertEquals(insert.getUrl(), fetched.getUrl());
        assertEquals(insert.getContextPath(), fetched.getContextPath());
        assertEquals(insert.getSecret(),fetched.getSecret());
        assertEquals(Boolean.FALSE, fetched.getEnabled());
        assertEquals(insert.getRemark(), fetched.getRemark());
        assertEquals(1, fetched.getRoutes().size());
        assertEquals(insert.getRoutes().get(0), fetched.getRoutes().get(0));

        // test deleteBy
        count = this.provider.deleteBy(Conditions.of(Application.class).like(Application::getCode, "test%"), "master");
        assertEquals(1, count);

        count = this.persistence.countBy(Conditions.of(ApplicationEntity.class).like(ApplicationEntity::getCode, "test%"));
        assertEquals(0, count);
    }
}
