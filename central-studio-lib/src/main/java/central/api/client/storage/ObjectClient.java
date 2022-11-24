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

package central.api.client.storage;

import central.api.client.storage.data.Multipart;
import central.data.storage.StorageObject;
import central.lang.Assertx;
import central.lang.CompareResult;
import central.lang.Stringx;
import central.security.Digestx;
import central.util.Capacity;
import central.util.Guidx;
import central.util.Listx;
import central.util.Objectx;
import central.web.XForwardedHeaders;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对象存储
 *
 * @author Alan Yeh
 * @since 2022/10/31
 */
public interface ObjectClient {

    /**
     * 创建对象访问凭证
     *
     * @param secret      应用密钥
     * @param objectIds   凭证允许访问的对象主键。如果为空，则允许访问该存储桶（Bucket）下所有对象。
     * @param permissions 凭证权限
     * @param expires     凭证过期时间（毫秒）
     * @return 对象访问凭证
     */
    default String createToken(String secret, List<String> objectIds, List<Permission> permissions, Long expires) {
        Assertx.mustNotEmpty(permissions, "权限[permissions]必须不为空");

        var jwt = JWT.create()
                .withJWTId(Guidx.nextID())
                // 对象访问权限
                .withClaim("permissions", permissions.stream().map(Permission::getValue).collect(Collectors.joining(", ")));

        // 对象访问范围
        if (Listx.isNotEmpty(objectIds)) {
            jwt.withAudience(objectIds.toArray(new String[0]));
        }

        if (expires == null) {
            // 不限制访问范围的凭证比较危险，必须设定过期时间
            Assertx.mustNotEmpty(objectIds, "过期时间[expires]必须不为空");

            // 带有非查看权限的凭证，必须提供过期时间
            Assertx.mustTrue(permissions.size() == 1 && permissions.contains(Permission.VIEW), "过期时间[expires]必须不为空");
        }

        if (expires != null) {
            // 过期时间不允许超过 30 分钟
            Assertx.mustTrue(expires <= Duration.ofMinutes(30).toMillis(), "过期时间[expires]不能超过 30 分钟");

            jwt.withExpiresAt(new Date(System.currentTimeMillis() + expires));
        }

        // 通过应用密钥进行签名
        return jwt.sign(Algorithm.HMAC256(secret));
    }

    /**
     * 获取指定对象的下载地址
     *
     * @param request  HttpServletRequest
     * @param code     存储桶（Bucket）标识
     * @param secret   应用密钥
     * @param objectId 对象主键
     * @param expires  下载地址有效期
     * @return 下载地址
     */
    default String getDownloadUri(HttpServletRequest request, String code, String secret, String objectId, Long expires) {
        Assertx.mustNotBlank(objectId, "对象主键[objectId]必须不为空");
        return getDownloadUris(request, code, secret, List.of(objectId), expires).get(0);
    }

    /**
     * 获取指定对象的下载地址
     *
     * @param request   HttpServletRequest
     * @param code      存储桶（Bucket）标识
     * @param secret    应用密钥
     * @param objectIds 对象主键
     * @param expires   下载地址有效期
     * @return 下载地址
     */
    default List<String> getDownloadUris(HttpServletRequest request, String code, String secret, List<String> objectIds, Long expires) {
        Assertx.mustNotNull(request, "请求[request]必须不为空");
        Assertx.mustNotBlank(code, "存储桶标识[code]必须不为空");
        Assertx.mustNotBlank(secret, "应用密钥[secret]必须不为空");
        Assertx.mustNotEmpty(objectIds, "对象主键[objectIds]必须不为空");

        var token = this.createToken(secret, objectIds, List.of(Permission.VIEW), expires);

        var tenantPath = Stringx.removeSuffix(Objectx.getOrDefault(request.getHeader(XForwardedHeaders.PATH), ""), "/");
        return objectIds.stream().map(id -> UriComponentsBuilder.fromUriString(Stringx.format("http://127.0.0.1:8080{}/storage/api/buckets/{}/objects", tenantPath, code))
                .queryParam("id", id)
                .queryParam("token", token)
                .scheme(request.getScheme())
                .host(request.getServerName())
                .port(request.getServerPort())
                .build().toString()
        ).toList();
    }

    /**
     * 智能上传文件
     *
     * @param code  存储桶（Bucket）标识
     * @param token 访问凭证
     * @param file  待上传件
     * @return 对象信息
     */
    default StorageObject smartUpload(@Nonnull String code, @Nonnull String token, @Nonnull File file) throws IOException {
        return this.smartUpload(code, token, file, null, true, null);
    }

    /**
     * 智能上传文件
     *
     * @param code      存储桶（Bucket）标识
     * @param token     访问凭证
     * @param file      待上传文件
     * @param filename  文件名
     * @param confirmed 是否已确认（如果未确认，系统将在一段时间后删除该对象）
     * @param tenant    租户标识
     * @return 对象信息
     */
    default StorageObject smartUpload(@Nonnull String code, @Nonnull String token, @Nonnull File file, @Nullable String filename, @Nullable Boolean confirmed, @Nullable String tenant) throws IOException {
        Assertx.mustTrue(file.exists(), "待上传文件[file]必须存在");

        // 计算对象摘要
        var digest = Digestx.SHA256.digest(Files.newInputStream(file.toPath(), StandardOpenOption.READ));

        // 尝试秒传
        try {
            return this.rapidUpload(code, token, Objectx.getOrDefault(filename, file.getName()), digest, confirmed, tenant);
        } catch (Exception ignored) {
            // 秒传失败
        }

        if (CompareResult.GT.matches(Capacity.ofB(file.length()), Capacity.ofMB(10))) {
            // 大于 10MB 的文件采用分片上传
            return this.multipartUpload(code, token, file, digest, filename, confirmed, tenant);
        } else {
            return this.upload(code, token, file, digest, filename, confirmed, tenant);
        }
    }

    /**
     * 上传文件
     *
     * @param code      存储桶（Bucket）标识
     * @param token     访问凭证
     * @param file      文件
     * @param digest    摘要(sha256，如果不传，服务器将不校验文件完整性)
     * @param filename  文件名
     * @param confirmed 是否已确认（如果未确认，系统将在一段时间后删除该对象）
     * @return 对象信息
     */
    @PostMapping(value = "/storage/api/buckets/{code}/objects", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    StorageObject upload(@PathVariable String code,
                         @RequestPart String token,
                         @RequestPart File file,
                         @RequestPart(required = false) String digest,
                         @RequestPart(required = false) String filename,
                         @RequestPart(required = false) Boolean confirmed);


    /**
     * 上传文件
     *
     * @param code      存储桶（Bucket）标识
     * @param token     访问凭证
     * @param file      文件
     * @param digest    摘要(sha256，如果不传，服务器将不校验文件完整性)
     * @param filename  文件名
     * @param confirmed 是否已确认（如果未确认，系统将在一段时间后删除该对象）
     * @param tenant    租户标识
     * @return 对象信息
     */
    @PostMapping(value = "/storage/api/buckets/{code}/objects", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    StorageObject upload(@PathVariable String code,
                         @RequestPart String token,
                         @RequestPart File file,
                         @RequestPart(required = false) String digest,
                         @RequestPart(required = false) String filename,
                         @RequestPart(required = false) Boolean confirmed,
                         @RequestHeader(value = XForwardedHeaders.TENANT, required = false) String tenant);

    /**
     * 分片上传文件
     *
     * @param code      存储桶（Bucket）标识
     * @param token     访问凭证
     * @param file      文件
     * @param digest    文件摘要（sha256）
     * @param filename  文件名
     * @param confirmed 是否已确认（如果未确认，系统将在一段时间后删除该对象）
     * @return 对象信息
     */
    default StorageObject multipartUpload(@Nonnull String code, @Nonnull String token, @Nonnull File file, @Nonnull String digest, @Nullable String filename, @Nullable Boolean confirmed) throws IOException {
        return this.multipartUpload(code, token, file, digest, filename, confirmed, null);
    }

    /**
     * 分片上传文件
     *
     * @param code      存储桶（Bucket）标识
     * @param token     访问凭证
     * @param file      文件
     * @param digest    文件摘要（sha256）
     * @param filename  文件名
     * @param confirmed 是否已确认（如果未确认，系统将在一段时间后删除该对象）
     * @param tenant    租户标识
     * @return 对象信息
     */
    default StorageObject multipartUpload(@Nonnull String code, @Nonnull String token, @Nonnull File file, @Nonnull String digest, @Nullable String filename, @Nullable Boolean confirmed, @Nullable String tenant) throws IOException {
        Assertx.mustTrue(file.exists(), "待上传文件[file]必须存在");

        Multipart multipart = null;
        try {
            multipart = this.createMultipart(code, token, digest, file.length(), tenant);

            // 分片缓冲
            var buffer = new byte[multipart.getChunkSize().intValue()];

            // 依次上传指定分片数据
            try (var access = new RandomAccessFile(file, "r")) {
                for (var index : multipart.getChunks()) {
                    var start = index * multipart.getChunkSize();
                    var end = Math.min(file.length(), start + multipart.getChunkSize());
                    var length = Long.valueOf(end - start).intValue();

                    access.seek(start);
                    access.read(buffer, 0, length);

                    this.patchMultipart(code, token, multipart.getId(), new ByteArrayInputStream(buffer, 0, length), index, tenant);
                }
            }

            // 完成分片上传
            return this.completeMultipart(code, token, multipart.getId(), Objectx.getOrDefault(filename, file.getName()), confirmed, tenant);
        } catch (Throwable throwable) {
            if (multipart != null) {
                this.cancelMultipart(code, token, multipart.getId(), tenant);
            }
            throw throwable;
        }
    }

    /**
     * 快速上传（秒传）
     *
     * @param code      存储桶（Bucket）标识
     * @param token     访问凭证
     * @param filename  文件名
     * @param digest    文件摘要（sha256）
     * @param confirmed 是否已确认（如果未确认，系统将在一段时间后删除该对象）
     * @return 对象信息
     */
    @PostMapping(value = "/storage/api/buckets/{code}/objects/rapid", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    StorageObject rapidUpload(@PathVariable String code,
                              @RequestPart String token,
                              @RequestPart String filename,
                              @RequestPart String digest,
                              @RequestPart(required = false) Boolean confirmed);

    /**
     * 快速上传（秒传）
     *
     * @param code      存储桶（Bucket）标识
     * @param token     访问凭证
     * @param filename  文件名
     * @param digest    文件摘要（sha256）
     * @param confirmed 是否已确认（如果未确认，系统将在一段时间后删除该对象）
     * @param tenant    租户标识
     * @return 对象信息
     */
    @PostMapping(value = "/storage/api/buckets/{code}/objects/rapid", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    StorageObject rapidUpload(@PathVariable String code,
                              @RequestPart String token,
                              @RequestPart String filename,
                              @RequestPart String digest,
                              @RequestPart(required = false) Boolean confirmed,
                              @RequestHeader(value = XForwardedHeaders.TENANT, required = false) String tenant);

    /**
     * 确认对象
     *
     * @param code  存储桶（Bucket）标识
     * @param token 访问凭证
     * @param ids   对象主键
     * @return 已确认数量
     */
    @PostMapping(value = "/storage/api/buckets/{code}/objects/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
    Long confirm(@PathVariable String code, @RequestPart String token, @RequestPart List<String> ids);

    /**
     * 确认对象
     *
     * @param code   存储桶（Bucket）标识
     * @param token  访问凭证
     * @param ids    对象主键
     * @param tenant 租户标识
     * @return 已确认数量
     */
    @PostMapping(value = "/storage/api/buckets/{code}/objects/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
    Long confirm(@PathVariable String code, @RequestPart String token, @RequestPart List<String> ids, @RequestHeader(value = XForwardedHeaders.TENANT, required = false) String tenant);

    /**
     * 删除对象
     *
     * @param code  存储桶（Bucket）标识
     * @param token 访问凭证
     * @param ids   对象主键
     * @return 已删除数量
     */
    @DeleteMapping("/storage/api/buckets/{code}/objects")
    Long delete(@PathVariable String code, @RequestParam String token, @RequestParam List<String> ids);

    /**
     * 删除对象
     *
     * @param code   存储桶（Bucket）标识
     * @param token  访问凭证
     * @param ids    对象主键
     * @param tenant 租户标识
     * @return 已删除数量
     */
    @DeleteMapping("/storage/api/buckets/{code}/objects")
    Long delete(@PathVariable String code, @RequestParam String token, @RequestParam List<String> ids, @RequestHeader(value = XForwardedHeaders.TENANT, required = false) String tenant);

    /**
     * 获取对象信息
     *
     * @param code  存储桶（Bucket）标识
     * @param token 访问凭证
     * @param id    对象主键
     * @return 对象信息
     */
    @GetMapping(value = "/storage/api/buckets/{code}/objects/details", produces = MediaType.APPLICATION_JSON_VALUE)
    StorageObject findById(@PathVariable String code, @RequestParam String token, @RequestParam String id);

    /**
     * 获取对象信息
     *
     * @param code  存储桶（Bucket）标识
     * @param token 访问凭证
     * @param id    对象主键
     * @return 对象信息
     */
    @GetMapping(value = "/storage/api/buckets/{code}/objects/details", produces = MediaType.APPLICATION_JSON_VALUE)
    StorageObject findById(@PathVariable String code, @RequestParam String token, @RequestParam String id, @RequestHeader(value = XForwardedHeaders.TENANT, required = false) String tenant);

    /**
     * 获取对象信息
     *
     * @param code  存储桶（Bucket）标识
     * @param token 访问凭证
     * @param ids   对象主键
     * @return 对象信息
     */
    @GetMapping(value = "/storage/api/buckets/{code}/objects/list", produces = MediaType.APPLICATION_JSON_VALUE)
    List<StorageObject> findByIds(@PathVariable String code, @RequestParam String token, @RequestParam List<String> ids);

    /**
     * 获取对象信息
     *
     * @param code  存储桶（Bucket）标识
     * @param token 访问凭证
     * @param ids   对象主键
     * @return 对象信息
     */
    @GetMapping(value = "/storage/api/buckets/{code}/objects/list", produces = MediaType.APPLICATION_JSON_VALUE)
    List<StorageObject> findByIds(@PathVariable String code, @RequestParam String token, @RequestParam List<String> ids, @RequestHeader(value = XForwardedHeaders.TENANT, required = false) String tenant);

    /**
     * 下载文件
     *
     * @param code     存储桶（Bucket）标识
     * @param token    访问凭证
     * @param id       对象主键
     * @param filename 文件名
     * @return 已下载的文件
     */
    @GetMapping("/storage/api/buckets/{code}/objects")
    File download(@PathVariable String code, @RequestParam String token, @RequestParam String id, @RequestParam(required = false) String filename);

    /**
     * 下载文件
     *
     * @param code     存储桶（Bucket）标识
     * @param token    访问凭证
     * @param id       对象主键
     * @param filename 文件名
     * @param tenant   租户标识
     * @return 已下载的文件
     */
    @GetMapping("/storage/api/buckets/{code}/objects")
    File download(@PathVariable String code, @RequestParam String token, @RequestParam String id, @RequestParam(required = false) String filename, @RequestHeader(value = XForwardedHeaders.TENANT, required = false) String tenant);


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 分片上传

    /**
     * 创建分片任务
     * <p>
     * 创建分片任务之后，需要在 30 分钟之内完成分片上传任务，否则将会被自动取消
     *
     * @param code   存储桶（Bucket）标识
     * @param token  访问凭证
     * @param digest 待上传文件摘要（Sha256）
     * @param size   文件大小
     * @return 分片任务
     */
    @PostMapping(value = "/storage/api/buckets/{code}/objects/multiparts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Multipart createMultipart(@PathVariable String code, @RequestPart String token, @RequestPart String digest, @RequestPart Long size);

    /**
     * 创建分片信息
     * <p>
     * 创建分片任务之后，需要在 30 分钟之内完成分片上传任务，否则将会被自动取消
     *
     * @param code   存储桶（Bucket）标识
     * @param token  访问凭证
     * @param digest 待上传文件摘要（Sha256）
     * @param size   文件大小
     * @param tenant 租户标识
     * @return 分片信息
     */
    @PostMapping(value = "/storage/api/buckets/{code}/objects/multiparts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Multipart createMultipart(@PathVariable String code, @RequestPart String token, @RequestPart String digest, @RequestPart Long size, @RequestHeader(value = XForwardedHeaders.TENANT, required = false) String tenant);

    /**
     * 上传分片数据
     *
     * @param code       存储桶（Bucket）标识
     * @param token      访问凭证
     * @param id         分片任务主键
     * @param chunk      分片数据
     * @param chunkIndex 分片下标
     * @return 分片信息
     */
    @PatchMapping(value = "/storage/api/buckets/{code}/objects/multiparts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Multipart patchMultipart(@PathVariable String code, @RequestPart String token, @RequestPart String id, @RequestPart InputStream chunk, @RequestPart Integer chunkIndex);

    /**
     * 上传分片数据
     *
     * @param code       存储桶（Bucket）标识
     * @param token      访问凭证
     * @param id         分片任务主键
     * @param chunk      分片数据
     * @param chunkIndex 分片下标
     * @param tenant     租户标识
     * @return 分片信息
     */
    @PatchMapping(value = "/storage/api/buckets/{code}/objects/multiparts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Multipart patchMultipart(@PathVariable String code, @RequestPart String token, @RequestPart String id, @RequestPart InputStream chunk, @RequestPart Integer chunkIndex, @RequestHeader(value = XForwardedHeaders.TENANT, required = false) String tenant);

    /**
     * 完成分片上传
     *
     * @param code      存储桶（Bucket）标识
     * @param token     访问凭证
     * @param id        分片任务主键
     * @param filename  文件名
     * @param confirmed 是否已确认（如果未确认，系统将在一段时间后删除该对象）
     * @return 存储对象信息
     */
    @PutMapping(value = "/storage/api/buckets/{code}/objects/multiparts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    StorageObject completeMultipart(@PathVariable String code, @RequestPart String token, @RequestPart String id, @RequestPart String filename, @RequestPart(required = false) Boolean confirmed);

    /**
     * 完成分片上传
     *
     * @param code      存储桶（Bucket）标识
     * @param token     访问凭证
     * @param id        分片任务主键
     * @param filename  文件名
     * @param confirmed 是否已确认（如果未确认，系统将在一段时间后删除该对象）
     * @param tenant    租户标识
     * @return 存储对象信息
     */
    @PutMapping(value = "/storage/api/buckets/{code}/objects/multiparts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    StorageObject completeMultipart(@PathVariable String code, @RequestPart String token, @RequestPart String id, @RequestPart String filename, @RequestPart(required = false) Boolean confirmed, @RequestHeader(value = XForwardedHeaders.TENANT, required = false) String tenant);

    /**
     * 取消分片上传
     *
     * @param code  存储桶（Bucket）标识
     * @param token 访问凭证
     * @param id    分片任务主键
     * @return 已取消数量
     */
    @DeleteMapping("/storage/api/buckets/{code}/objects/multiparts")
    Long cancelMultipart(@PathVariable String code, @RequestParam String token, @RequestParam String id);

    /**
     * 取消分片上传
     *
     * @param code   存储桶（Bucket）标识
     * @param token  访问凭证
     * @param id     分片任务主键
     * @param tenant 租户标识
     * @return 已取消数量
     */
    @DeleteMapping("/storage/api/buckets/{code}/objects/multiparts")
    Long cancelMultipart(@PathVariable String code, @RequestParam String token, @RequestParam String id, @RequestHeader(value = XForwardedHeaders.TENANT, required = false) String tenant);
}
