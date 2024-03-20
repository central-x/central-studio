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

package central.studio.provider.graphql.multicast.dto;

import central.studio.provider.graphql.multicast.entity.MulticastMessageEntity;
import central.studio.provider.graphql.organization.dto.AccountDTO;
import central.starter.graphql.annotation.GraphQLGetter;
import central.starter.graphql.annotation.GraphQLType;
import lombok.EqualsAndHashCode;
import org.dataloader.DataLoader;

import java.io.Serial;
import java.util.concurrent.CompletableFuture;

/**
 * Multicast Message
 * <p>
 * 广播消息
 *
 * @author Alan Yeh
 * @since 2022/11/04
 */
@GraphQLType("MulticastMessage")
@EqualsAndHashCode(callSuper = true)
public class MulticastMessageDTO extends MulticastMessageEntity {
    @Serial
    private static final long serialVersionUID = -5575480692031075125L;

    /**
     * 广播器信息
     */
    @GraphQLGetter
    public CompletableFuture<MulticastBroadcasterDTO> getBroadcaster(DataLoader<String, MulticastBroadcasterDTO> loader) {
        return loader.load(this.getBroadcasterId());
    }

    /**
     * 创建人信息
     */
    @GraphQLGetter
    public CompletableFuture<AccountDTO> getCreator(DataLoader<String, AccountDTO> loader) {
        return loader.load(this.getCreatorId());
    }

    /**
     * 修改人信息
     */
    @GraphQLGetter
    public CompletableFuture<AccountDTO> getModifier(DataLoader<String, AccountDTO> loader) {
        return loader.load(this.getModifierId());
    }
}