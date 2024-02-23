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

package central.studio.storage.controller;

import central.io.Filex;
import central.net.http.HttpException;
import central.security.Digestx;
import central.storage.client.ObjectClient;
import central.storage.client.Permission;
import central.studio.storage.StorageApplication;
import central.studio.storage.core.BucketContainer;
import central.util.Guidx;
import central.util.Listx;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Object Controller Test Cases
 *
 * @author Alan Yeh
 * @see ObjectController
 * @since 2022/11/03
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = StorageApplication.class)
public class TestObjectController {

    @Setter(onMethod_ = @Autowired)
    private ObjectClient client;

    @Setter(onMethod_ = @Autowired)
    private BucketContainer container;

    @BeforeEach
    @SuppressWarnings("BusyWait")
    public void boot() throws Exception {
        // 等待存储中心完全启动起来才可以单元测试
        while (container.getBuckets("master").isEmpty()) {
            Thread.sleep(100);
        }
    }

    /**
     * @see ObjectController#upload
     * @see ObjectController#download
     */
    @Test
    public void case1() throws IOException {
        // 创建随机文件
        var file = new File("./test.txt");
        File download = null;
        try {
            var content = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                content.append(Guidx.nextID());
            }
            Filex.writeText(file, content.toString());
            var digest = Digestx.SHA256.digest(Files.newInputStream(file.toPath(), StandardOpenOption.READ));

            var token = this.client.createToken("AkJSi2kmH7vSO5lJcvY", null, List.of(Permission.CREATE), Duration.ofMinutes(1).toMillis());

            var object = this.client.upload("identity", token, file, null, null, true, "master");
            assertNotNull(object);
            assertEquals("test.txt", object.getName());
            assertEquals("txt", object.getExtension());
            assertEquals(file.length(), object.getSize());
            assertEquals(digest, object.getDigest());
            assertTrue(object.getConfirmed());

            token = this.client.createToken("AkJSi2kmH7vSO5lJcvY", List.of(object.getId()), List.of(Permission.VIEW), Duration.ofMinutes(1).toMillis());
            download = this.client.download("identity", token, object.getId(), "case1.txt", "master");
            assertNotNull(download);
            assertTrue(download.exists());
            assertEquals("case1.txt", download.getName());
            assertEquals(file.length(), download.length());
            assertEquals(digest, Digestx.SHA256.digest(Files.newInputStream(download.toPath(), StandardOpenOption.READ)));
        } finally {
            Filex.delete(file);
            if (download != null) {
                Filex.delete(download);
            }
        }
    }

    /**
     * @see ObjectController#upload
     * @see ObjectController#rapidUpload
     */
    @Test
    public void case2() throws IOException {
        var file = new File("./test.txt");
        try {
            var content = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                content.append(Guidx.nextID());
            }
            Filex.writeText(file, content.toString());
            var digest = Digestx.SHA256.digest(Files.newInputStream(file.toPath(), StandardOpenOption.READ));

            var token = this.client.createToken("AkJSi2kmH7vSO5lJcvY", null, List.of(Permission.CREATE), Duration.ofMinutes(1).toMillis());

            assertThrows(HttpException.class, () -> {
                // 直接上传会失败，然后会抛异常
                var object = this.client.rapidUpload("identity", token, "a.txt", digest, true, "master");
                assertNull(object);
            });


            var object = this.client.upload("identity", token, file, null, null, true, "master");
            assertNotNull(object);
            assertEquals("test.txt", object.getName());
            assertEquals("txt", object.getExtension());
            assertEquals(file.length(), object.getSize());
            assertEquals(digest, object.getDigest());
            assertTrue(object.getConfirmed());

            // 测试快速上传
            var rapidObject = this.client.rapidUpload("identity", token, "a.txt", digest, true, "master");
            assertNotNull(rapidObject);
            assertEquals("a.txt", rapidObject.getName());
            assertEquals("txt", rapidObject.getExtension());
            assertEquals(file.length(), rapidObject.getSize());
            assertEquals(digest, rapidObject.getDigest());
            assertTrue(object.getConfirmed());

        } finally {
            Filex.delete(file);
        }
    }

    /**
     * @see ObjectController#upload
     * @see ObjectController#confirm
     * @see ObjectController#findById
     * @see ObjectController#findByIds
     * @see ObjectController#delete
     */
    @Test
    public void case3() throws IOException {
        // 创建随机文件
        var file = new File("./test.txt");
        try {
            var content = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                content.append(Guidx.nextID());
            }
            Filex.writeText(file, content.toString());
            var digest = Digestx.SHA256.digest(Files.newInputStream(file.toPath(), StandardOpenOption.READ));

            var token = this.client.createToken("AkJSi2kmH7vSO5lJcvY", null, List.of(Permission.CREATE), Duration.ofMinutes(1).toMillis());

            var object = this.client.upload("identity", token, file, null, null, false, "master");
            assertNotNull(object);
            assertEquals("test.txt", object.getName());
            assertEquals("txt", object.getExtension());
            assertEquals(file.length(), object.getSize());
            assertEquals(digest, object.getDigest());
            assertFalse(object.getConfirmed());

            token = this.client.createToken("AkJSi2kmH7vSO5lJcvY", List.of(object.getId()), List.of(Permission.UPDATE), Duration.ofMinutes(1).toMillis());
            var effected = this.client.confirm("identity", token, List.of(object.getId()), "master");
            assertEquals(1L, effected);


            token = this.client.createToken("AkJSi2kmH7vSO5lJcvY", List.of(object.getId()), List.of(Permission.VIEW), Duration.ofMinutes(1).toMillis());
            var object2 = this.client.findById("identity", token, object.getId(), "master");
            assertNotNull(object2);
            assertEquals(object.getName(), object2.getName());
            assertEquals(object.getExtension(), object2.getExtension());
            assertEquals(object.getSize(), object2.getSize());
            assertEquals(object.getDigest(), object2.getDigest());
            assertTrue(object2.getConfirmed());

            var objects = this.client.findByIds("identity", token, List.of(object.getId()), "master");
            assertEquals(1, objects.size());
            object2 = Listx.getFirstOrNull(objects);
            assertNotNull(object2);
            assertEquals(object.getName(), object2.getName());
            assertEquals(object.getExtension(), object2.getExtension());
            assertEquals(object.getSize(), object2.getSize());
            assertEquals(object.getDigest(), object2.getDigest());
            assertTrue(object2.getConfirmed());

            token = this.client.createToken("AkJSi2kmH7vSO5lJcvY", List.of(object.getId()), List.of(Permission.DELETE), Duration.ofMinutes(1).toMillis());
            effected = this.client.delete("identity", token, List.of(object.getId()), "master");
            assertEquals(1L, effected);

            token = this.client.createToken("AkJSi2kmH7vSO5lJcvY", List.of(object.getId()), List.of(Permission.VIEW), Duration.ofMinutes(1).toMillis());
            object2 = this.client.findById("identity", token, object.getId(), "master");
            assertNull(object2);
        } finally {
            Filex.delete(file);
        }
    }

    /**
     * 分片上传
     *
     * @see ObjectController#createMultipart
     * @see ObjectController#patchMultipart
     * @see ObjectController#completeMultipart
     * @see ObjectController#completeMultipart
     */
    @Test
    public void case4() throws IOException {
        // 创建随机文件
        var file = new File("./test.txt");
        File download = null;
        try {
            var content = new StringBuilder();
            for (int i = 0; i < 1000000; i++) {
                content.append(Guidx.nextID());
            }
            Filex.writeText(file, content.toString());
            var digest = Digestx.SHA256.digest(Files.newInputStream(file.toPath(), StandardOpenOption.READ));

            var token = this.client.createToken("AkJSi2kmH7vSO5lJcvY", null, List.of(Permission.CREATE), Duration.ofMinutes(1).toMillis());

            var object = this.client.multipartUpload("identity", token, file, digest, null, true, "master");
            assertNotNull(object);
            assertEquals("test.txt", object.getName());
            assertEquals("txt", object.getExtension());
            assertEquals(file.length(), object.getSize());
            assertEquals(digest, object.getDigest());
            assertTrue(object.getConfirmed());

            token = this.client.createToken("AkJSi2kmH7vSO5lJcvY", List.of(object.getId()), List.of(Permission.VIEW), Duration.ofMinutes(1).toMillis());
            download = this.client.download("identity", token, object.getId(), "case4.txt", "master");
            assertNotNull(download);
            assertTrue(download.exists());
            assertEquals("case4.txt", download.getName());
            assertEquals(file.length(), download.length());
            assertEquals(digest, Digestx.SHA256.digest(Files.newInputStream(download.toPath(), StandardOpenOption.READ)));
        } finally {
            Filex.delete(file);
            if (download != null) {
                Filex.delete(download);
            }
        }
    }
}
