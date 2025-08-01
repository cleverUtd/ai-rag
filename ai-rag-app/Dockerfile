# 基础镜像
FROM openjdk:17-jdk-slim

# 作者
LABEL maintainer="zclau"

# 配置
ENV PARAMS=""

# 时区
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 添加应用
ADD target/ai-rag-app.jar /ai-rag-app.jar

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /ai-rag-app.jar $PARAMS"]
