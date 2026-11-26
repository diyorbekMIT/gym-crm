# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S gym && adduser -S gym -G gym

COPY --from=build /app/target/discovery-service-1.0-SNAPSHOT.jar app.jar

USER gym

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]