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

import central.provider.graphql.storage.query.StorageBucketQuery;
import central.provider.graphql.storage.query.StorageFileQuery;
import central.provider.graphql.storage.query.StorageObjectQuery;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Storage Query
 * <p>
 * 存储中心查询
 *
 * @author Alan Yeh
 * @since 2022/10/30
 */
@Component
@GraphQLSchema(path = "storage", types = {StorageBucketQuery.class, StorageFileQuery.class, StorageObjectQuery.class})
public class StorageQuery {

    /**
     * Bucket Query
     * <p>
     * 存储桶查询
     */
    @GraphQLGetter
    public StorageBucketQuery getBuckets(@Autowired StorageBucketQuery query) {
        return query;
    }

    /**
     * File Query
     * <p>
     * 文件查询
     */
    @GraphQLGetter
    public StorageFileQuery getFiles(@Autowired StorageFileQuery query) {
        return query;
    }

    /**
     * Object Query
     * <p>
     * 对象查询
     */
    @GraphQLGetter
    public StorageObjectQuery getObjects(@Autowired StorageObjectQuery query) {
        return query;
    }
}
