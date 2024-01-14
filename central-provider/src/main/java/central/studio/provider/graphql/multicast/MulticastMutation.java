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

package central.studio.provider.graphql.multicast;

import central.studio.provider.graphql.multicast.mutation.MulticastBroadcasterMutation;
import central.studio.provider.graphql.multicast.mutation.MulticastMessageMutation;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Multicast Mutation
 * <p>
 * 广播中心修改
 *
 * @author Alan Yeh
 * @since 2022/11/03
 */
@Component
@GraphQLSchema(path = "multicast", types = {MulticastBroadcasterMutation.class, MulticastMessageMutation.class})
public class MulticastMutation {
    /**
     * Broadcaster Mutation
     * <p>
     * 广播器修改
     */
    @GraphQLGetter
    public MulticastBroadcasterMutation getBroadcasters(@Autowired MulticastBroadcasterMutation mutation) {
        return mutation;
    }

    /**
     * Message Mutation
     * <p>
     * 广播消息修改
     */
    @GraphQLGetter
    public MulticastMessageMutation getMessages(@Autowired MulticastMessageMutation mutation) {
        return mutation;
    }
}
