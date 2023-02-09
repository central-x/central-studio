FROM openjdk:17
MAINTAINER Alan Yeh "alan@yeh.cn"

ENV TIME_ZONE="Asia/Shanghai"
RUN set -x \
    && ln -snf /usr/share/zoneinfo/$TIME_ZONE /etc/localtime && echo '$TIME_ZONE' > /etc/timezone \
    && useradd --create-home app

# 使用非 root 用户执行程序
USER app

# 暴露端口
EXPOSE 8080

# 在用户目录下启动应用
WORKDIR ~
ARG JAR_FILE
ADD target/${JAR_FILE} ./app.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.config.additional-location=optional:./config/bootstrap.yml", "-jar", "app.jar"]

# 暴露卷
VOLUME ./config
VOLUME ./data