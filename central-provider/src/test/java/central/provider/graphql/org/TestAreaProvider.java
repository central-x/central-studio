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

package central.provider.graphql.org;

import central.api.provider.org.AreaProvider;
import central.bean.Page;
import central.data.org.Area;
import central.data.org.AreaInput;
import central.data.org.option.AreaType;
import central.provider.ApplicationProperties;
import central.provider.ProviderApplication;
import central.provider.graphql.TestProvider;
import central.provider.graphql.org.entity.AreaEntity;
import central.provider.graphql.org.mapper.AreaMapper;
import central.sql.Conditions;
import central.sql.Orders;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Area Provider Test Cases
 * 行政区划
 *
 * @author Alan Yeh
 * @since 2022/10/06
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ProviderApplication.class)
public class TestAreaProvider extends TestProvider {
    @Setter(onMethod_ = @Autowired)
    private AreaProvider provider;

    @Setter(onMethod_ = @Autowired)
    private ApplicationProperties properties;

    @Setter(onMethod_ = @Autowired)
    private AreaMapper mapper;

    @BeforeEach
    @AfterEach
    public void clear() {
        // 清空数据
        mapper.deleteAll();
    }

    /**
     * @see AreaProvider#findById
     */
    @Test
    public void case1() {
        var entity = new AreaEntity();
        entity.setParentId("");
        entity.setCode("86");
        entity.setName("中国");
        entity.setType(AreaType.COUNTRY.getValue());
        entity.setOrder(0);
        entity.setTenantCode("master");
        entity.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(entity);

        // 查询数据
        var area = this.provider.findById(entity.getId());
        assertNotNull(area);
        assertEquals(entity.getId(), area.getId());
        assertNull(area.getParent());

        assertNotNull(area.getCreator());
        assertEquals(properties.getSupervisor().getUsername(), area.getCreator().getId());
        assertNotNull(area.getModifier());
        assertEquals(properties.getSupervisor().getUsername(), area.getModifier().getId());
    }

    /**
     * @see AreaProvider#findByIds
     */
    @Test
    public void case2() {
        var country = new AreaEntity();
        country.setParentId("");
        country.setCode("86");
        country.setName("中国");
        country.setType(AreaType.COUNTRY.getValue());
        country.setOrder(0);
        country.setTenantCode("master");
        country.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(country);

        var province = new AreaEntity();
        province.setParentId(country.getId());
        province.setCode("440000");
        province.setName("广东省");
        province.setType(AreaType.PROVINCE.getValue());
        province.setOrder(0);
        province.setTenantCode("master");
        province.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(province);

        // 查询数据
        var areas = this.provider.findByIds(List.of(country.getId(), province.getId()));
        assertNotNull(areas);
        assertEquals(2, areas.size());
        assertTrue(areas.stream().anyMatch(it -> Objects.equals(country.getId(), it.getId())));
        assertTrue(areas.stream().anyMatch(it -> Objects.equals(province.getId(), it.getId())));
    }

    /**
     * @see AreaProvider#findBy
     */
    @Test
    public void case3() {
        var country = new AreaEntity();
        country.setParentId("");
        country.setCode("86");
        country.setName("中国");
        country.setType(AreaType.COUNTRY.getValue());
        country.setOrder(0);
        country.setTenantCode("master");
        country.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(country);

        var province = new AreaEntity();
        province.setParentId(country.getId());
        province.setCode("440000");
        province.setName("广东省");
        province.setType(AreaType.PROVINCE.getValue());
        province.setOrder(0);
        province.setTenantCode("master");
        province.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(province);

        List<Area> areas = this.provider.findBy(null, null, Conditions.of(Area.class).eq(Area::getParentId, ""), null);
        assertNotNull(areas);
        assertEquals(1, areas.size());
        assertTrue(areas.stream().allMatch(it -> Objects.equals(country.getId(), it.getId())));
    }

    /**
     * @see AreaProvider#pageBy
     */
    @Test
    public void case4() {
        var country = new AreaEntity();
        country.setParentId("");
        country.setCode("86");
        country.setName("中国");
        country.setType(AreaType.COUNTRY.getValue());
        country.setOrder(0);
        country.setTenantCode("master");
        country.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(country);

        var provinceCodes = List.of("110000", "120000", "130000", "140000", "150000", "210000", "220000", "230000", "310000", "440000");
        var provinceNames = List.of("北京市", "天津市", "河北省", "山西省", "内蒙古自治区", "辽宁省", "吉林省", "黑龙江省", "上海市", "广东省");

        var provinces = new ArrayList<AreaEntity>();
        for (int i = 0; i < provinceNames.size(); i++) {
            var province = new AreaEntity();
            province.setParentId(country.getId());
            province.setCode(provinceCodes.get(i));
            province.setName(provinceNames.get(i));
            province.setType(AreaType.PROVINCE.getValue());
            province.setOrder(0);
            province.setTenantCode("master");
            province.updateCreator(properties.getSupervisor().getUsername());
            provinces.add(province);
        }
        this.mapper.insertBatch(provinces);

        Page<Area> page = this.provider.pageBy(2L, 4L, Conditions.of(Area.class).like(Area::getParentId, country.getId()), Orders.of(Area.class).asc(Area::getCode));
        assertNotNull(page);
        assertEquals(2, page.getPager().getPageIndex());
        assertEquals(4, page.getPager().getPageSize());
        assertEquals(3, page.getPager().getPageCount());
        assertEquals(10, page.getPager().getItemCount());
        assertEquals(4, page.getData().size());
        assertTrue(page.getData().stream().allMatch(it -> it.getCode().endsWith("0000")));
    }

    /**
     * @see AreaProvider#countBy
     */
    @Test
    public void case5() {
        var country = new AreaEntity();
        country.setParentId("");
        country.setCode("86");
        country.setName("中国");
        country.setType(AreaType.COUNTRY.getValue());
        country.setOrder(0);
        country.setTenantCode("master");
        country.updateCreator(properties.getSupervisor().getUsername());
        this.mapper.insert(country);

        var provinceCodes = List.of("110000", "120000", "130000", "140000", "150000", "210000", "220000", "230000", "310000", "440000");
        var provinceNames = List.of("北京市", "天津市", "河北省", "山西省", "内蒙古自治区", "辽宁省", "吉林省", "黑龙江省", "上海市", "广东省");

        var provinces = new ArrayList<AreaEntity>();
        for (int i = 0; i < provinceNames.size(); i++) {
            var province = new AreaEntity();
            province.setParentId(country.getId());
            province.setCode(provinceCodes.get(i));
            province.setName(provinceNames.get(i));
            province.setType(AreaType.PROVINCE.getValue());
            province.setOrder(0);
            province.setTenantCode("master");
            province.updateCreator(properties.getSupervisor().getUsername());
            provinces.add(province);
        }
        this.mapper.insertBatch(provinces);

        var count = this.provider.countBy(Conditions.of(Area.class).like(Area::getParentId, country.getId()));
        assertNotNull(count);
        assertEquals(10L, count);
    }

    /**
     * @see AreaProvider#insert
     */
    @Test
    public void case6() {
        var country = new AreaInput();
        country.setParentId("");
        country.setCode("86");
        country.setName("中国");
        country.setType(AreaType.COUNTRY.getValue());
        country.setOrder(0);

        // 保存数据
        var inserted = this.provider.insert(country, this.properties.getSupervisor().getUsername());
        assertNotNull(inserted);
        assertNotNull(inserted.getId());

        // 关联查询
        assertNotNull(inserted.getCreator());
        assertEquals(this.properties.getSupervisor().getUsername(), inserted.getCreator().getId());
        assertNotNull(inserted.getModifier());
        assertEquals(this.properties.getSupervisor().getUsername(), inserted.getModifier().getId());

        // 查询数据库
        assertTrue(this.mapper.existsBy(Conditions.of(AreaEntity.class).eq(AreaEntity::getCode, "86")));
    }

    /**
     * @see AreaProvider#insertBatch
     */
    @Test
    public void case7() {
        var country = new AreaInput();
        country.setParentId("");
        country.setCode("86");
        country.setName("中国");
        country.setType(AreaType.COUNTRY.getValue());
        country.setOrder(0);

        var insertedCountry = this.provider.insert(country, properties.getSupervisor().getUsername());

        var provinceCodes = List.of("110000", "120000", "130000", "140000", "150000", "210000", "220000", "230000", "310000", "440000");
        var provinceNames = List.of("北京市", "天津市", "河北省", "山西省", "内蒙古自治区", "辽宁省", "吉林省", "黑龙江省", "上海市", "广东省");

        var provinces = new ArrayList<AreaInput>();
        for (int i = 0; i < provinceNames.size(); i++) {
            var province = new AreaInput();
            province.setParentId(insertedCountry.getId());
            province.setCode(provinceCodes.get(i));
            province.setName(provinceNames.get(i));
            province.setType(AreaType.PROVINCE.getValue());
            province.setOrder(0);
            provinces.add(province);
        }
        var inserted = this.provider.insertBatch(provinces, properties.getSupervisor().getUsername());
        assertNotNull(inserted);
        assertEquals(10, inserted.size());

        var search = this.provider.findByIds(inserted.stream().map(Area::getId).toList());
        assertNotNull(search);
        assertEquals(10, search.size());
    }

    /**
     * @see AreaProvider#update
     */
    @Test
    public void case8() {
        var country = new AreaInput();
        country.setParentId("");
        country.setCode("86");
        country.setName("中国");
        country.setType(AreaType.COUNTRY.getValue());
        country.setOrder(0);

        var inserted = this.provider.insert(country, properties.getSupervisor().getUsername());

        // 获取数据
        var area = this.provider.findById(inserted.getId());
        assertNotNull(area);

        // 修改数据
        var input = area.toInput().toBuilder()
                .name("美国")
                .code("840")
                .order(1)
                .build();
        var updated = this.provider.update(input, this.properties.getSupervisor().getUsername());
        assertNotNull(updated);
        assertEquals(inserted.getId(), updated.getId());
        assertEquals(inserted.getParentId(), updated.getParentId(), "父行政区划主键[parentId]未修改");
        assertNotEquals(inserted.getCode(), updated.getCode(), "标识[code]已修改");
        assertNotEquals(inserted.getName(), updated.getName(), "名称[name]已修改");
        assertNotEquals(inserted.getName(), updated.getName(), "姓名[name]已修改");

        assertEquals(inserted.getType(), updated.getType(), "类型[type]未修改");
        assertEquals(inserted.getType(), updated.getType(), "类型[type]未修改");
        assertNotEquals(inserted.getCode(), updated.getCode(), "序号[code]已修改");

        assertEquals(inserted.getCreateDate(), updated.getCreateDate(), "创建时间[creatorDate]未修改");
        assertNotEquals(inserted.getModifyDate(), updated.getModifyDate(), "修改时间[creatorDate]需更新");
    }

    /**
     * @see AreaProvider#updateBatch
     */
    @Test
    public void case9() {
        var china = new AreaInput();
        china.setParentId("");
        china.setCode("86");
        china.setName("中国");
        china.setType(AreaType.COUNTRY.getValue());
        china.setOrder(0);

        var usa = new AreaInput();
        usa.setParentId("");
        usa.setCode("840");
        usa.setName("美国");
        usa.setType(AreaType.COUNTRY.getValue());
        usa.setOrder(0);

        var inserted = this.provider.insertBatch(List.of(china, usa), properties.getSupervisor().getUsername());
        assertNotNull(inserted);

        // 获取数据
        var areas = this.provider.findByIds(inserted.stream().map(Area::getId).toList());
        assertNotNull(areas);

        // 修改数据
        var inputs = areas.stream().map(it -> it.toInput().toBuilder().order(1).build()).toList();
        var updated = this.provider.updateBatch(inputs, this.properties.getSupervisor().getUsername());
        assertNotNull(updated);
        assertTrue(updated.stream().allMatch(it -> Objects.equals(1, it.getOrder())));

    }

    /**
     * @see AreaProvider#deleteByIds
     */
    @Test
    public void case10() {
        // 插入数据
        var china = new AreaInput();
        china.setParentId("");
        china.setCode("86");
        china.setName("中国");
        china.setType(AreaType.COUNTRY.getValue());
        china.setOrder(0);

        var usa = new AreaInput();
        usa.setParentId("");
        usa.setCode("840");
        usa.setName("美国");
        usa.setType(AreaType.COUNTRY.getValue());
        usa.setOrder(0);

        var inserted = this.provider.insertBatch(List.of(china, usa), properties.getSupervisor().getUsername());
        assertNotNull(inserted);

        // 删除数据
        var ids = inserted.stream().filter(it -> Objects.equals(usa.getCode(), it.getCode())).map(Area::getId).toList();

        // 删除数据
        var deleted = this.provider.deleteByIds(ids);
        assertEquals(1L, deleted);

        // 查询删除数据之后数据库的数量
        var count = this.mapper.count();
        assertEquals(1L, count);

        // 查询是否还存在指定主键的数据
        var areas = this.mapper.findByIds(ids);
        assertEquals(0, areas.size());
    }

    /**
     * @see AreaProvider#deleteBy
     */
    @Test
    public void case11() {
        // 插入数据
        var china = new AreaInput();
        china.setParentId("");
        china.setCode("86");
        china.setName("中国");
        china.setType(AreaType.COUNTRY.getValue());
        china.setOrder(0);

        var usa = new AreaInput();
        usa.setParentId("");
        usa.setCode("840");
        usa.setName("美国");
        usa.setType(AreaType.COUNTRY.getValue());
        usa.setOrder(0);

        var inserted = this.provider.insertBatch(List.of(china, usa), properties.getSupervisor().getUsername());
        assertNotNull(inserted);

        var deleted = this.provider.deleteBy(Conditions.of(Area.class).eq(Area::getCode, "840"));
        assertEquals(1, deleted);

        // 检查是否真的被删除了
        var count = this.mapper.countBy(Conditions.of(AreaEntity.class).eq(AreaEntity::getCode, "840"));
        assertEquals(0, count);
    }
}
