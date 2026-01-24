FROM eclipse-temurin:17-jre-alpine
COPY build/libs/jp-0.0.1-SNAPSHOT.jar jp.jar
ENV SPRING_PROFILES_ACTIVE=dev
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "jp.jar"]