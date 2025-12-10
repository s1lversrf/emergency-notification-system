FROM gradle:8.14.0-jdk21 AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle

COPY settings.gradle .

COPY recipient-service/build.gradle recipient-service/
COPY recipient-service/src recipient-service/src

RUN chmod +x gradlew

RUN ./gradlew :recipient-service:clean :recipient-service:build -x test --no-daemon

FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY --from=build app/recipient-service/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]