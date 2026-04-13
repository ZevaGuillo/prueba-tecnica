# Multi-stage build for ms-clients microservice
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradle/ gradle/
COPY gradlew build.gradle ./

# Create minimal settings.gradle for ms-clients only
RUN echo "rootProject.name = 'banking'" > settings.gradle && \
    echo "include 'ms-clients'" >> settings.gradle && \
    echo "include 'ms-clients:applications:app-service'" >> settings.gradle && \
    echo "include 'ms-clients:domain:model'" >> settings.gradle && \
    echo "include 'ms-clients:domain:usecase'" >> settings.gradle && \
    echo "include 'ms-clients:infrastructure:driven-adapters:jpa-repository'" >> settings.gradle && \
    echo "include 'ms-clients:infrastructure:driven-adapters:kafka-publisher'" >> settings.gradle && \
    echo "include 'ms-clients:infrastructure:entry-points:api-rest'" >> settings.gradle && \
    echo "include 'ms-clients:infrastructure:helpers'" >> settings.gradle

# Copy ms-clients specific files
COPY ms-clients/build.gradle ms-clients/settings.gradle ms-clients/
COPY ms-clients/applications/ ms-clients/applications/
COPY ms-clients/domain/ ms-clients/domain/
COPY ms-clients/infrastructure/ ms-clients/infrastructure/

# Copy contracts directory for OpenAPI generation
COPY contracts/ contracts/

# Build the application (skip tests for faster build)
RUN ./gradlew ms-clients:applications:app-service:bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine AS runtime

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root user for security
RUN addgroup -g 1001 -S app && adduser -u 1001 -S app -G app

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/ms-clients/applications/app-service/build/libs/*.jar app.jar

# Change ownership to non-root user
RUN chown app:app app.jar

# Switch to non-root user
USER app

# Expose port 8080 (default Spring Boot port)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]