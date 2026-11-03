# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy build files first to improve Docker layer caching
COPY pom.xml .

# Download dependencies before copying source code
RUN mvn dependency:go-offline -B

# Copy application source code
COPY src ./src

# Build executable Spring Boot JAR
RUN mvn package -DskipTests -B

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -S gym && adduser -S gym -G gym

COPY --from=build /app/target/gym-crm.jar app.jar

USER gym

# Document the application port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]