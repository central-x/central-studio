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

package central.studio.provider.graphql.log;

import central.studio.provider.graphql.log.query.LogCollectorQuery;
import central.studio.provider.graphql.log.query.LogFilterQuery;
import central.studio.provider.graphql.log.query.LogStorageQuery;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Log Query
 * <p>
 * 日志中心查询
 *
 * @author Alan Yeh
 * @since 2022/10/25
 */
@Component
@GraphQLSchema(path = "log", types = {LogCollectorQuery.class, LogFilterQuery.class, LogStorageQuery.class})
public class LogQuery {

    /**
     * Collector Query
     * <p>
     * 采集器查询
     */
    @GraphQLGetter
    public LogCollectorQuery getCollectors(@Autowired LogCollectorQuery query) {
        return query;
    }

    /**
     * Filter Query
     * <p>
     * 过滤器查询
     */
    @GraphQLGetter
    public LogFilterQuery getFilters(@Autowired LogFilterQuery query) {
        return query;
    }

    /**
     * Storage Query
     * <p>
     * 存储器查询
     */
    @GraphQLGetter
    public LogStorageQuery getStorages(@Autowired LogStorageQuery query) {
        return query;
    }
}
