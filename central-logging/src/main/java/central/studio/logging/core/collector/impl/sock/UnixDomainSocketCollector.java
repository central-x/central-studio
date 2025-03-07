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

package central.studio.logging.core.collector.impl.sock;

import central.io.Filex;
import central.lang.Stringx;
import central.pluglet.annotation.Control;
import central.studio.logging.core.collector.Collector;
import central.validation.Label;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Setter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnixDomainSocketAddress;

/// Unit-Domain Socket Collect
///
/// @author Alan Yeh
public class UnixDomainSocketCollector extends Collector implements InitializingBean, DisposableBean {

    @Setter
    @Label("Socket 文件路径")
    @Size(min = 1, max = 256)
    @NotBlank
    @Control(label = "文件路径", comment = "Unit-Domain Socket 监听路径")
    private String path;

    private ServerSocket serverSocket;

    @Override
    public void afterPropertiesSet() throws Exception {
        var socketFile = new File(Stringx.addSuffix(path, ".sock"));
        if (socketFile.exists()) {
            Filex.delete(socketFile);
        }

        serverSocket = new ServerSocket();
        serverSocket.bind(UnixDomainSocketAddress.of(Stringx.addSuffix(path, ".sock")));

        while (true) {
            try (Socket socket = serverSocket.accept()) {
                InputStream input = socket.getInputStream();
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        if (serverSocket != null) {
            serverSocket.close();
        }
    }
}
