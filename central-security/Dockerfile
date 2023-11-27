# 多阶段构建
# 第一阶段将 Spring Boot 应用程序进行拆分

FROM centralx/openjdk:17 as builder

ARG STUDIO_VERSION

# 创建工作目录及常用子目录
WORKDIR /workspace

# 复制应用到工作目录
COPY central-security-${STUDIO_VERSION}.jar application.jar

# 使用 Spring Boot 工具对应用进行分层
RUN java -Djarmode=layertools -jar application.jar extract


# 第二阶段，正式构建应用
FROM centralx/openjdk:17
MAINTAINER Alan Yeh <alan@yeh.cn>
LABEL vender=CentralX \
  license="The MIT License" \
  name="Central Security"

# 创建工作目录及常用子目录
WORKDIR /workspace
RUN mkdir /workspace/config && mkdir /workspace/logs && mkdir /workspace/data

# 将上一阶段提取的应用分层复制到工作目录
COPY --from=builder   /workspace/dependencies            ./
COPY --from=builder   /workspace/spring-boot-loader      ./
COPY --from=builder   /workspace/snapshot-dependencies   ./
COPY --from=builder   /workspace/application             ./

# 添加 runner 用户，并对并授权
RUN adduser runner -D -u 1000 && chown -R runner:runner /workspace

# 切换到非 root 用户启动程序
USER runner

# 最大内存
ENV JVM_MAX_HEAP     768m
# 最小内存
ENV JVM_MIN_HEAP     256m
# 其它 JVM 参数
ENV JVM_OPTS         "-XX:+UseG1GC"
# 启动参数
ENV BOOTSTRAP_OPTS   "-Djava.security.egd=file:/dev/./urandom -Dspring.config.additional-location=optional:./config/"

EXPOSE 3200
VOLUME ["/workspace/config", "/workspace/logs", "/workspace/data"]
ENTRYPOINT ["sh", "-c", "java -Xms$JVM_MIN_HEAP -Xmx$JVM_MAX_HEAP $JVM_OPTS $BOOTSTRAP_OPTS org.springframework.boot.loader.JarLauncher"]