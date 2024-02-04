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

package central.multicast.controller;

import central.multicast.client.MessageClient;
import central.multicast.client.body.Recipient;
import central.multicast.client.body.StandardBody;
import central.data.multicast.option.PublishMode;
import central.studio.multicast.MulticastApplication;
import central.studio.multicast.controller.MessageController;
import central.studio.multicast.core.BroadcasterContainer;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Message Controller Test Cases
 *
 * @author Alan Yeh
 * @since 2022/11/04
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = MulticastApplication.class)
public class TestMessageController {

    @Setter(onMethod_ = @Autowired)
    private MessageClient client;

    @Setter(onMethod_ = @Autowired)
    private BroadcasterContainer container;

    @BeforeEach
    @SuppressWarnings("BusyWait")
    public void boot() throws Exception {
        while (container.getBroadcasters("master").isEmpty()) {
            Thread.sleep(100);
        }
    }

    /**
     * @see MessageController#publish
     */
    @Test
    public void case1() {
        var token = this.client.createToken("AkJSi2kmH7vSO5lJcvY", Duration.ofMinutes(30).toMillis());
        var body = new StandardBody("测试邮件", "测试消息体", List.of(new Recipient("alan", "alan@yeh.cn")));
        var messages = this.client.publish("identity", token, PublishMode.STANDARD, List.of(body), "master");

        assertNotNull(messages);
        assertEquals(1, messages.size());

        var message = Listx.getFirstOrNull(messages);
        assertNotNull(message);
    }
}
