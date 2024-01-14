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

import central.studio.provider.graphql.log.mutation.LogCollectorMutation;
import central.studio.provider.graphql.log.mutation.LogFilterMutation;
import central.studio.provider.graphql.log.mutation.LogStorageMutation;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Log Mutation
 * <p>
 * 日志中心修改
 *
 * @author Alan Yeh
 * @since 2022/10/25
 */
@Component
@GraphQLSchema(path = "log", types = {LogCollectorMutation.class, LogFilterMutation.class, LogStorageMutation.class})
public class LogMutation {

    /**
     * Collector Mutation
     * <p>
     * 采集器修改
     */
    @GraphQLGetter
    public LogCollectorMutation getCollectors(@Autowired LogCollectorMutation mutation) {
        return mutation;
    }

    /**
     * Filter Mutation
     * <p>
     * 过滤器修改
     */
    @GraphQLGetter
    public LogFilterMutation getFilters(@Autowired LogFilterMutation mutation) {
        return mutation;
    }

    /**
     * Storage Mutation
     * <p>
     * 存储器修改
     */
    @GraphQLGetter
    public LogStorageMutation getStorages(@Autowired LogStorageMutation mutation) {
        return mutation;
    }
}
