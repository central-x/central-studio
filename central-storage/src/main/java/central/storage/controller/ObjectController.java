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

package central.storage.controller;

import central.api.client.storage.data.Multipart;
import central.api.client.storage.Permission;
import central.api.provider.storage.StorageObjectProvider;
import central.data.storage.StorageObject;
import central.data.storage.StorageObjectInput;
import central.lang.Assertx;
import central.lang.Stringx;
import central.sql.query.Conditions;
import central.starter.webmvc.render.FileRender;
import central.storage.controller.param.*;
import central.storage.controller.query.DetailsQuery;
import central.storage.controller.query.DownloadQuery;
import central.storage.controller.query.ListQuery;
import central.storage.core.BucketCache;
import central.storage.core.Container;
import central.storage.core.DynamicBucket;
import central.storage.core.stream.CacheObjectStream;
import central.storage.core.stream.MultipartFileObjectStream;
import central.util.Listx;
import central.util.Objectx;
import central.web.XForwardedHeaders;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 存储对象上传下载接口
 *
 * @author Alan Yeh
 * @since 2022/10/30
 */
@RestController
@RequestMapping("/api/buckets/{code}/objects")
public class ObjectController {

    @Setter(onMethod_ = @Autowired)
    private Container container;

    @Setter(onMethod_ = @Autowired)
    private StorageObjectProvider provider;

    /**
     * 校验访问对象的凭证是否有效以及是否包要求的权限
     *
     * @param token      访问凭证
     * @param bucket     存储桶（Bucket）
     * @param permission 必要权限
     * @param ids        必要对象主键
     */
    private void validate(String token, DynamicBucket bucket, Permission permission, List<String> ids) {
        DecodedJWT jwt;

        try {
            jwt = JWT.require(Algorithm.HMAC256(bucket.getData().getApplication().getSecret())).build()
                    .verify(token);
        } catch (AlgorithmMismatchException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "访问凭证[token]无效: 签名算法不匹配");
        } catch (SignatureVerificationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "访问凭证[token]无效: 签名不匹配");
        } catch (TokenExpiredException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "访问凭证[token]无效: 已过期");
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "访问凭证[token]无效: " + ex.getLocalizedMessage());
        }

        // 检测权限
        Set<Permission> permissions = Arrays.stream(jwt.getClaim("permissions").asString().split(","))
                .map(String::trim)
                .filter(Stringx::isNotBlank)
                .map(Permission::resolve)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (permissions.contains(Permission.ALL)) {
            // 拥有所有权限
            permissions.addAll(Arrays.asList(Permission.values()));
        }

        if (!permissions.contains(permission)) {
            // 不包含指定权限
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, Stringx.format("访问凭证[token]无效: 该凭证没有 {} 权限", permission.getValue()));
        }

        if (permissions.size() > 1 || !permissions.contains(Permission.VIEW) || Listx.isNullOrEmpty(jwt.getAudience())) {
            // 如果权限里面包含危险权限（除了 VIEW 之外的所有权限），或者不限范围的对象访问，则必须设置有效期
            var expiresAt = Assertx.requireNotNull(jwt.getExpiresAt(), () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "访问凭证[token]无效: 必须设置有效期"));
            Date maxExpiresDate = new Date(System.currentTimeMillis() + Duration.ofMinutes(30).toMillis());
            Assertx.mustTrue(expiresAt.before(maxExpiresDate), () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "访问凭证[token]无效: 有效期过长"));
        }

        // 检测是否可以访问指定对象
        var audiences = jwt.getAudience();
        if (Listx.isNotEmpty(audiences)) {
            // 如果凭证限定了范围，但是服务却不限定范围，则表示越权了
            if (Listx.isNullOrEmpty(ids)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "访问凭证[token]无效: 越权");
            }

            for (String id : ids) {
                if (!audiences.contains(id)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "访问凭证[token]无效: 该凭证不可以访问文件" + id);
                }
            }
        }
    }

    /**
     * 上传文件
     *
     * @param code      存储桶（Bucket）标识
     * @param params    参数
     * @param accountId 当前用户
     * @param tenant    租户标识
     */
    @PostMapping
    public StorageObject upload(@PathVariable String code,
                                @Validated UploadParams params,
                                @RequestAttribute String accountId,
                                @RequestHeader(XForwardedHeaders.TENANT) String tenant) throws IOException {
        var bucket = this.container.requireBucket(tenant, code);
        this.validate(params.getToken(), bucket, Permission.CREATE, null);

        var stream = new MultipartFileObjectStream(params.getFile());
        // 将对象保存到存储桶中
        var key = bucket.store(stream);

        // 保存数据
        var input = StorageObjectInput.builder()
                .bucketId(bucket.getData().getId())
                .name(Objectx.getOrDefault(params.getFilename(), params.getFile().getOriginalFilename()))
                .size(stream.getSize())
                .digest(stream.getDigest())
                .key(key)
                .confirmed(Objectx.getOrDefault(params.getConfirmed(), Boolean.TRUE))
                .build();

        var object = this.provider.insert(input, Objectx.getOrDefault(accountId, "syssa"), tenant);
        // 忽略存储键，以防泄露
        object.setKey(null);
        return object;
    }

    /**
     * 快速上传（秒传）
     *
     * @param code      存储桶（Bucket）标识
     * @param params    参数
     * @param accountId 当前用户
     * @param tenant    租户标识
     */
    @PostMapping("/rapid")
    public StorageObject rapidUpload(@PathVariable String code,
                                     @Validated @RequestBody RapidUploadParams params,
                                     @RequestAttribute String accountId,
                                     @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var bucket = this.container.requireBucket(tenant, code);
        this.validate(params.getToken(), bucket, Permission.CREATE, null);

        var objects = this.provider.findBy(1L, 0L,
                Conditions.of(StorageObject.class).eq(StorageObject::getBucketId, bucket.getData().getId()).eq(StorageObject::getDigest, params.getDigest()),
                null, tenant);
        var object = Listx.getFirstOrNull(objects);
        if (object == null) {
            // 找不到相同摘要的文件，快速上传失败
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "秒传失败，请继续上传文件");
        }

        // 复制对象信息
        var input = StorageObjectInput.builder()
                .bucketId(bucket.getData().getId())
                .name(params.getFilename())
                .size(object.getSize())
                .digest(object.getDigest())
                .key(object.getKey())
                .confirmed(Objectx.getOrDefault(params.getConfirmed(), Boolean.TRUE))
                .build();

        object = this.provider.insert(input, Objectx.getOrDefault(accountId, "syssa"), tenant);
        // 忽略存储键，以防泄露
        object.setKey(null);
        return object;
    }

    /**
     * 二次确认
     *
     * @param code      存储桶（Bucket）标识
     * @param params    参数
     * @param accountId 当前用户
     * @param tenant    租户标识
     */
    @PostMapping("/confirm")
    public Long confirm(@PathVariable String code,
                        @Validated @RequestBody ConfirmParams params,
                        @RequestAttribute String accountId,
                        @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var bucket = this.container.requireBucket(tenant, code);
        this.validate(params.getToken(), bucket, Permission.UPDATE, params.getIds());

        // 修改对象状态
        var objects = this.provider.findBy(null, null,
                Conditions.of(StorageObject.class).eq(StorageObject::getBucketId, bucket.getData().getId()).in(StorageObject::getId, params.getIds()),
                null, tenant);
        var inputs = objects.stream()
                .map(it -> it.toInput().toBuilder().confirmed(Boolean.TRUE).build())
                .toList();
        return (long) this.provider.updateBatch(inputs, accountId, tenant).size();
    }

    /**
     * 删除对象
     *
     * @param code   存储桶（Bucket）标识
     * @param params 参数
     * @param tenant 租户标识
     */
    @DeleteMapping
    public Long delete(@PathVariable String code,
                       @Validated DeleteParams params,
                       @RequestHeader(XForwardedHeaders.TENANT) String tenant) throws IOException {
        var bucket = this.container.requireBucket(tenant, code);
        this.validate(params.getToken(), bucket, Permission.DELETE, params.getIds());

        var objects = this.provider.findBy(null, null,
                Conditions.of(StorageObject.class).eq(StorageObject::getBucketId, bucket.getData().getId()).in(StorageObject::getId, params.getIds()),
                null, tenant);

        for (var object : objects) {
            // 如果有多个文件引用同一个存储键的话，则删除文件
            var count = this.provider.countBy(Conditions.of(StorageObject.class)
                    .eq(StorageObject::getBucketId, bucket.getData().getId())
                    .eq(StorageObject::getKey, object.getKey()));
            if (count == 1) {
                // 如果只有一个文件引用，删除文件
                bucket.delete(object.getKey());
            }
        }

        return this.provider.deleteByIds(params.getIds(), tenant);
    }

    /**
     * 查询对象信息
     *
     * @param code   存储桶（Bucket）标识
     * @param query  参数
     * @param tenant 租户标识
     */
    @GetMapping("/details")
    public StorageObject findById(@PathVariable String code,
                                  @Validated DetailsQuery query,
                                  @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var bucket = this.container.requireBucket(tenant, code);
        this.validate(query.getToken(), bucket, Permission.VIEW, List.of(query.getId()));

        var objects = this.provider.findBy(null, null,
                Conditions.of(StorageObject.class).eq(StorageObject::getBucketId, bucket.getData().getId()).eq(StorageObject::getId, query.getId()),
                null, tenant);
        return Listx.getFirst(objects).map(it -> {
            // 忽略存储键，以防泄露
            it.setKey(null);
            return it;
        }).orElse(null);
    }

    /**
     * 查询对象信息列表
     *
     * @param code   存储桶（Bucket）标识
     * @param query  参数
     * @param tenant 租户标识
     */
    @GetMapping("/list")
    public List<StorageObject> findByIds(@PathVariable String code, @Validated ListQuery query, @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var bucket = this.container.requireBucket(tenant, code);
        this.validate(query.getToken(), bucket, Permission.VIEW, query.getIds());

        return this.provider.findBy(null, null,
                        Conditions.of(StorageObject.class).eq(StorageObject::getBucketId, bucket.getData().getId()).in(StorageObject::getId, query.getIds()),
                        null, tenant).stream()
                // 忽略存储键，以防泄露
                .peek(it -> it.setKey(null))
                .toList();
    }

    /**
     * 下载对象
     *
     * @param code     存储桶（Bucket）标识
     * @param query    参数
     * @param request  请求
     * @param response 响应
     */
    @GetMapping
    public void download(@PathVariable String code,
                         @Validated DownloadQuery query,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         @RequestHeader(XForwardedHeaders.TENANT) String tenant) throws IOException {
        var bucket = this.container.requireBucket(tenant, code);
        this.validate(query.getToken(), bucket, Permission.VIEW, List.of(query.getId()));

        // 查询对象信息
        var objects = this.provider.findBy(null, null,
                Conditions.of(StorageObject.class).eq(StorageObject::getBucketId, bucket.getData().getId()).eq(StorageObject::getId, query.getId()),
                null, tenant);
        var object = Listx.getFirstOrNull(objects);
        if (object == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Stringx.format("对象[id={}]不存在", query.getId()));
        }

        // 获取对象数据流
        var stream = bucket.get(object.getKey());
        if (stream == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Stringx.format("对象[id={}]不存在", query.getId()));
        }

        // 返回数据流
        new FileRender(request, response)
                .setInputStream(stream.getInputStream())
                .setFileName(Objectx.getOrDefault(query.getFilename(), object.getName()))
                .setContentDisposition(query.getContentDisposition())
                .setContentType(Objectx.getOrDefault(query.getContentType(), MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .setContentLength(object.getSize())
                .setDigest(object.getDigest())
                .render();

//        if (stream.isResumable()){
//            new ResumableFileRender(request, response)
//                    .setFile()
//        } else {
//            new FileRender(request, response)
//                    .setInputStream(stream.getInputStream())
//                    .setFileName(Objectx.getOrDefault(query.getFilename(), object.getName()))
//                    .setContentDisposition(query.getContentDisposition())
//                    .setContentType(query.getContentType())
//                    .setContentLength(object.getSize())
//                    .setDigest(object.getDigest())
//                    .render();
//        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 分片上传


    private final Map<String, Multipart> multiparts = new ConcurrentHashMap<>();

    @Setter(onMethod_ = @Autowired)
    private BucketCache cache;

    /**
     * 创建分片上传
     *
     * @param code   存储桶（Bucket）标识
     * @param params 参数
     * @param tenant 租户标识
     */
    @PostMapping("/multiparts")
    public Multipart createMultipart(@PathVariable String code,
                                     @Validated @RequestBody CreateMultipartParams params,
                                     @RequestHeader(XForwardedHeaders.TENANT) String tenant) {
        var bucket = this.container.requireBucket(tenant, code);
        this.validate(params.getToken(), bucket, Permission.CREATE, null);

        var multipart = new Multipart(params.getDigest(), params.getSize());
        this.multiparts.put(multipart.getId(), multipart);
        return multipart;
    }

    /**
     * 上传分片数据
     *
     * @param code   存储桶（Bucket）标识
     * @param params 参数
     * @param tenant 租户标识
     */
    @PatchMapping("/multiparts")
    public Multipart patchMultipart(@PathVariable String code,
                                    @Validated PatchMultipartParams params,
                                    @RequestHeader(XForwardedHeaders.TENANT) String tenant) throws IOException {
        var bucket = this.container.requireBucket(tenant, code);
        this.validate(params.getToken(), bucket, Permission.CREATE, null);

        var multipart = this.multiparts.get(params.getId());
        if (multipart == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "未找分指定分片上传任务");
        }
        if (!multipart.getChunks().contains(params.getChunkIndex())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "未找到指定下标分片");
        }
        if (params.getChunk().getSize() > multipart.getChunkSize()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "分片大小与分片任务不匹配");
        }
        // 保存分片数据到缓存里
        this.cache.put(multipart.getId() + "_" + params.getChunkIndex(), params.getChunk().getInputStream());
        // 删除缓存中的下标
        multipart.getChunks().remove(params.getChunkIndex());

        return multipart;
    }

    /**
     * 完成分片上传
     *
     * @param code      存储桶（Bucket）标识
     * @param params    参数
     * @param accountId 当前用户
     * @param tenant    租户标识
     */
    @PutMapping("/multiparts")
    public StorageObject completeMultipart(@PathVariable String code,
                                           @Validated @RequestBody CompleteMultipartParams params,
                                           @RequestAttribute String accountId,
                                           @RequestHeader(XForwardedHeaders.TENANT) String tenant) throws IOException {
        var bucket = this.container.requireBucket(tenant, code);
        this.validate(params.getToken(), bucket, Permission.CREATE, null);

        var multipart = this.multiparts.get(params.getId());
        if (multipart == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "未找分指定分片上传任务");
        }
        if (!multipart.getChunks().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "部份分片数据还未上传");
        }

        try {
            var chunkKeys = IntStream.range(0, multipart.getChunkCount().intValue()).mapToObj(it -> multipart.getId() + "_" + it).toList();
            var stream = new CacheObjectStream(this.cache, chunkKeys, params.getFilename(), multipart.getSize());

            // 校验文件是否完整
            if (!Objects.equals(multipart.getDigest(), stream.getDigest())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "文件摘要不匹配");
            }

            // 将对象保存到存储桶里
            var key = bucket.store(stream);

            var input = StorageObjectInput.builder()
                    .bucketId(bucket.getData().getId())
                    .name(params.getFilename())
                    .size(stream.getSize())
                    .digest(stream.getDigest())
                    .key(key)
                    .confirmed(Objectx.getOrDefault(params.getConfirmed(), Boolean.TRUE))
                    .build();

            var object = this.provider.insert(input, Objectx.getOrDefault(accountId, "syssa"), tenant);
            // 忽略存储键，以防泄露
            object.setKey(null);
            return object;
        } finally {
            // 无论上传成功还是上传失败，最后都是要取消并删除相关缓存的
            var cancelParams = new CancelMultipartParams(params.getToken());
            cancelParams.setId(params.getId());
            this.cancelMultipart(code, cancelParams, tenant);
        }
    }

    /**
     * 取消分片上传
     *
     * @param code   存储桶（Bucket）标识
     * @param params 参数
     * @param tenant 租户标识
     */
    @DeleteMapping("/multiparts")
    public Long cancelMultipart(@PathVariable String code,
                                @Validated CancelMultipartParams params,
                                @RequestHeader(XForwardedHeaders.TENANT) String tenant) throws IOException {
        var bucket = this.container.requireBucket(tenant, code);
        this.validate(params.getToken(), bucket, Permission.CREATE, null);

        var multipart = this.multiparts.remove(params.getId());

        if (multipart == null) {
            return 0L;
        }

        var chunkKeys = IntStream.range(0, multipart.getChunkCount().intValue()).mapToObj(it -> multipart.getId() + "_" + it).toList();
        this.cache.delete(chunkKeys);

        return 1L;
    }
}
