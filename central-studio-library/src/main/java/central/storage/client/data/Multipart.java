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

package central.storage.client.data;

import central.util.Guidx;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 分片上传信息
 *
 * @author Alan Yeh
 * @since 2022/11/01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Multipart implements Serializable {
    @Serial
    private static final long serialVersionUID = -4984737642326600349L;

    /**
     * 创建时间
     */
    private final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    /**
     * 分片上传主键
     */
    private String id;
    /**
     * 文件摘要（Sha256）
     */
    private String digest;
    /**
     * 文件大小
     */
    private Long size;
    /**
     * 分片大小
     */
    private Long chunkSize;
    /**
     * 分片总数
     */
    private Long chunkCount;
    /**
     * 待上传分片下标数组
     */
    private List<Integer> chunks;

    public Multipart(String digest, Long size) {
        this.id = Guidx.nextID();
        this.digest = digest;
        this.size = size;
        this.chunkSize = 5 * 1024 * 1024L;
        this.chunkCount = (long) Math.ceil(((double) size) / chunkSize);
        this.chunks = new ArrayList<>(IntStream.range(0, chunkCount.intValue()).boxed().toList());
    }
}
