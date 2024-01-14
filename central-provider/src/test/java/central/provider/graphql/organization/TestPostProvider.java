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

import central.data.organization.Post;
import central.data.organization.PostInput;
import central.data.organization.option.AreaType;
import central.studio.provider.ApplicationProperties;
import central.studio.provider.ProviderApplication;
import central.provider.graphql.TestProvider;
import central.studio.provider.graphql.organization.entity.AreaEntity;
import central.studio.provider.graphql.organization.entity.PostEntity;
import central.studio.provider.graphql.organization.entity.UnitEntity;
import central.studio.provider.graphql.organization.mapper.AreaMapper;
import central.studio.provider.graphql.organization.mapper.PostMapper;
import central.studio.provider.graphql.organization.mapper.UnitMapper;
import central.sql.query.Conditions;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Post Provider Test Cases
 * 部务
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestPostProvider extends TestProvider {

    @Setter(onMethod_ = @Autowired)
    private PostProvider provider;

    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    @Setter(onMethod_ = @Autowired)
    private PostMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private UnitMapper unitMapper;

    @Setter(onMethod_ = @Autowired)
    private AreaMapper areaMapper;

    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空数据
        mapper.deleteAll();
        unitMapper.deleteAll();
        areaMapper.deleteAll();
    }

    /**
     * @see PostProvider#findById
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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var postEntity = new PostEntity();
        postEntity.setUnitId(unitEntity.getId());
        postEntity.setCode("10000");
        postEntity.setName("测试职务");
        postEntity.setOrder(0);
        postEntity.setTenantCode("master");
        postEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(postEntity);

        // 查询数据
        var post = this.provider.findById(postEntity.getId());
        assertNotNull(post);
        assertEquals(postEntity.getId(), post.getId());
        assertNotNull(post.getUnit());
        assertEquals(unitEntity.getId(), post.getUnit().getId());
        assertEquals(unitEntity.getAreaId(), post.getUnit().getAreaId());
        assertNotNull(post.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), post.getCreator().getId());
        assertNotNull(post.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), post.getModifier().getId());
    }

    /**
     * @see PostProvider#findByIds
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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var postEntity = new PostEntity();
        postEntity.setUnitId(unitEntity.getId());
        postEntity.setCode("10000");
        postEntity.setName("测试职务");
        postEntity.setOrder(0);
        postEntity.setTenantCode("master");
        postEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(postEntity);

        // 查询数据
        var posts = this.provider.findByIds(List.of(postEntity.getId()));
        assertNotNull(posts);
        assertEquals(1, posts.size());
        var post = Listx.getFirstOrNull(posts);
        assertNotNull(post);
        assertEquals(postEntity.getId(), post.getId());
        assertNotNull(post.getUnit());
        assertEquals(unitEntity.getId(), post.getUnit().getId());
        assertEquals(unitEntity.getAreaId(), post.getUnit().getAreaId());
        assertNotNull(post.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), post.getCreator().getId());
        assertNotNull(post.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), post.getModifier().getId());
    }

    /**
     * @see PostProvider#findBy
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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var postEntity = new PostEntity();
        postEntity.setUnitId(unitEntity.getId());
        postEntity.setCode("10000");
        postEntity.setName("测试职务");
        postEntity.setOrder(0);
        postEntity.setTenantCode("master");
        postEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(postEntity);

        // 查询数据
        var posts = this.provider.findBy(null, null, Conditions.of(Post.class).eq(Post::getCode, "10000"), null);
        assertNotNull(posts);
        assertEquals(1, posts.size());
        var post = Listx.getFirstOrNull(posts);
        assertNotNull(post);
        assertEquals(postEntity.getId(), post.getId());
        assertNotNull(post.getUnit());
        assertEquals(unitEntity.getId(), post.getUnit().getId());
        assertEquals(unitEntity.getAreaId(), post.getUnit().getAreaId());
        assertNotNull(post.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), post.getCreator().getId());
        assertNotNull(post.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), post.getModifier().getId());
    }

    /**
     * @see PostProvider#pageBy
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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var postEntity = new PostEntity();
        postEntity.setUnitId(unitEntity.getId());
        postEntity.setCode("10000");
        postEntity.setName("测试职务");
        postEntity.setOrder(0);
        postEntity.setTenantCode("master");
        postEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(postEntity);

        // 查询数据
        var page = this.provider.pageBy(1L, 20L, Conditions.of(Post.class).eq(Post::getCode, "10000"), null);
        assertNotNull(page);
        assertNotNull(page.getPager());
        assertEquals(1L, page.getPager().getPageIndex());
        assertEquals(20L, page.getPager().getPageSize());
        assertEquals(1L, page.getPager().getPageCount());
        assertEquals(1L, page.getPager().getItemCount());
        assertNotNull(page.getData());
        var post = Listx.getFirstOrNull(page.getData());
        assertNotNull(post);
        assertEquals(postEntity.getId(), post.getId());
        assertNotNull(post.getUnit());
        assertEquals(unitEntity.getId(), post.getUnit().getId());
        assertEquals(unitEntity.getAreaId(), post.getUnit().getAreaId());
        assertNotNull(post.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), post.getCreator().getId());
        assertNotNull(post.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), post.getModifier().getId());
    }

    /**
     * @see PostProvider#countBy
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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var postEntity = new PostEntity();
        postEntity.setUnitId(unitEntity.getId());
        postEntity.setCode("10000");
        postEntity.setName("测试职务");
        postEntity.setOrder(0);
        postEntity.setTenantCode("master");
        postEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(postEntity);

        // 查询数据
        var count = this.provider.countBy(Conditions.of(Post.class).eq(Post::getCode, "10000"));
        assertNotNull(count);
        assertEquals(1, count);
    }

    /**
     * @see PostProvider#insert
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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var postInput = PostInput.builder()
                .unitId(unitEntity.getId())
                .code("10000")
                .name("测试职务")
                .order(0)
                .build();

        var post = this.provider.insert(postInput, properties.getSupervisor().getUsername());
        assertNotNull(post);
        assertNotNull(post.getId());

        assertTrue(this.mapper.existsBy(Conditions.of(PostEntity.class).eq(PostEntity::getCode, "10000")));
    }

    /**
     * @see PostProvider#insertBatch
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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var postInput = PostInput.builder()
                .unitId(unitEntity.getId())
                .code("10000")
                .name("测试职务")
                .order(0)
                .build();

        var posts = this.provider.insertBatch(List.of(postInput), properties.getSupervisor().getUsername());
        assertNotNull(posts);
        assertEquals(1, posts.size());
        assertNotNull(posts.get(0).getId());

        assertTrue(this.mapper.existsBy(Conditions.of(PostEntity.class).eq(PostEntity::getCode, "10000")));
    }

    /**
     * @see PostProvider#update 
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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var postEntity = new PostEntity();
        postEntity.setUnitId(unitEntity.getId());
        postEntity.setCode("10000");
        postEntity.setName("测试职务");
        postEntity.setOrder(0);
        postEntity.setTenantCode("master");
        postEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(postEntity);

        var post = this.provider.findById(postEntity.getId());
        assertNotNull(post);

        var input = post.toInput().toBuilder()
                .code("10001")
                .build();

        post = this.provider.update(input, properties.getSupervisor().getUsername());
        assertNotNull(post);
        assertNotEquals(post.getCreateDate(), post.getModifyDate());

        assertTrue(this.mapper.existsBy(Conditions.of(PostEntity.class).eq(PostEntity::getCode, "10001")));
    }

    /**
     * @see PostProvider#updateBatch
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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var postEntity = new PostEntity();
        postEntity.setUnitId(unitEntity.getId());
        postEntity.setCode("10000");
        postEntity.setName("测试职务");
        postEntity.setOrder(0);
        postEntity.setTenantCode("master");
        postEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(postEntity);

        var post = this.provider.findById(postEntity.getId());
        assertNotNull(post);

        var input = post.toInput().toBuilder()
                .code("10001")
                .build();

        var posts = this.provider.updateBatch(List.of(input), properties.getSupervisor().getUsername());
        assertNotNull(posts);
        assertEquals(1, posts.size());
        assertNotNull(posts.get(0).getId());

        assertTrue(this.mapper.existsBy(Conditions.of(PostEntity.class).eq(PostEntity::getCode, "10001")));
    }

    /**
     * @see PostProvider#deleteByIds
     */
    @Test
    public void case10() {
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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var postEntity = new PostEntity();
        postEntity.setUnitId(unitEntity.getId());
        postEntity.setCode("10000");
        postEntity.setName("测试职务");
        postEntity.setOrder(0);
        postEntity.setTenantCode("master");
        postEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(postEntity);

        var deleted = this.provider.deleteByIds(List.of(postEntity.getId()));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(PostEntity.class).eq(PostEntity::getId, postEntity.getId())));
    }

    /**
     * @see PostProvider#deleteBy(Conditions)
     */
    @Test
    public void case11() {
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
        unitEntity.setCode("100000");
        unitEntity.setName("测试单位");
        unitEntity.setOrder(0);
        unitEntity.setTenantCode("master");
        unitEntity.updateCreator(properties.getSupervisor().getUsername());
        this.unitMapper.insert(unitEntity);

        var postEntity = new PostEntity();
        postEntity.setUnitId(unitEntity.getId());
        postEntity.setCode("10000");
        postEntity.setName("测试职务");
        postEntity.setOrder(0);
        postEntity.setTenantCode("master");
        postEntity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(postEntity);

        var deleted = this.provider.deleteBy(Conditions.of(Post.class).eq(Post::getCode, "10000"));
        assertNotNull(deleted);
        assertEquals(1L, deleted);

        assertFalse(this.mapper.existsBy(Conditions.of(PostEntity.class).eq(PostEntity::getCode, "10000")));
    }
}
