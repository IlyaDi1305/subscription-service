FROM openjdk:17-jdk-slim

LABEL maintainer="ilia.didorenko@gmail.com"
LABEL version="1.0"
LABEL description="Spring Boot Subscription Service"

WORKDIR /app

COPY target/subscription-service-0.0.1-SNAPSHOT.jar app.jar

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
