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

import central.provider.graphql.org.query.*;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLSchema;
import central.starter.graphql.annotation.GraphQLType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Organization Query
 * 组织架构类查询
 *
 * @author Alan Yeh
 * @since 2022/10/03
 */
@Component
@GraphQLSchema(path = "org", types = {AreaQuery.class, UnitQuery.class, DepartmentQuery.class, RankQuery.class, PostQuery.class, AccountQuery.class})
public class OrgQuery {

    /**
     * Area Query
     * 行政区划查询
     */
    @GraphQLGetter
    public AreaQuery getAreas(@Autowired AreaQuery query) {
        return query;
    }

    /**
     * Unit Query
     * 单位查询
     */
    @GraphQLGetter
    public UnitQuery getUnits(@Autowired UnitQuery query) {
        return query;
    }

    /**
     * Department Query
     * 部门查询
     * TODO 这个是不是应该放到 UnitQuery 下
     */
    @GraphQLGetter
    public DepartmentQuery getDepartments(@Autowired DepartmentQuery query) {
        return query;
    }

    /**
     * Rank Query
     * 职级查询
     * TODO 这个是不是应该放到 UnitQuery 下
     */
    @GraphQLGetter
    public RankQuery getRanks(@Autowired RankQuery query) {
        return query;
    }

    /**
     * Post Query
     * 职务查询
     * TODO 这个是不是应该放到 UnitQuery 下
     */
    @GraphQLGetter
    public PostQuery getPosts(@Autowired PostQuery query) {
        return query;
    }

    /**
     * Account Query
     * 帐户查询
     */
    @GraphQLGetter
    public AccountQuery getAccounts(@Autowired AccountQuery query) {
        return query;
    }
}
