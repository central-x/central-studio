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

package central.studio.gateway.core.body;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;
import reactor.netty.Connection;

/**
 * Connection Body
 *
 * @author Alan Yeh
 * @since 2022/10/18
 */
public class ConnectionBody implements HttpResponseBody {
    @Getter
    private final HttpHeaders headers = new HttpHeaders();

    private final Connection connection;

    public ConnectionBody(Connection connection) {
        this.connection = connection;
    }

    @NotNull
    @Override
    public Flux<DataBuffer> get(DataBufferFactory bufferFactory) {
        return connection.inbound()
                .receive()
                .retain()
                .map(byteBuf -> wrap(byteBuf, bufferFactory));
    }

    protected DataBuffer wrap(ByteBuf byteBuf, DataBufferFactory bufferFactory) {
        if (bufferFactory instanceof NettyDataBufferFactory factory) {
            return factory.wrap(byteBuf);
        }
        // MockServerHttpResponse creates these
        else if (bufferFactory instanceof DefaultDataBufferFactory factory) {
            DataBuffer buffer = factory.allocateBuffer(byteBuf.readableBytes());
            buffer.write(byteBuf.nioBuffer());
            byteBuf.release();
            return buffer;
        }
        throw new IllegalArgumentException("Unknown DataBufferFactory type " + bufferFactory.getClass());
    }

    @Override
    public void dispose() {
        if (this.connection.channel().isActive() && !this.connection.isPersistent()) {
            this.connection.dispose();
        }
    }
}
