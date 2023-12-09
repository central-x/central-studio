########################################################
# 使用 Spring Runner 运行本程序
########################################################
FROM image

ARG STUDIO_COMPONENT
ARG STUDIO_VERSION

# 复制应用到工作目录
COPY ${STUDIO_COMPONENT}-${STUDIO_VERSION}.jar application.jar

# 暴露端口
EXPOSE 3200