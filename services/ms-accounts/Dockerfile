# Multi-stage build: el JAR ejecutable sale del módulo Gradle `infrastructure`
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY . .

RUN chmod +x gradlew \
    && ./gradlew :infrastructure:bootJar --no-daemon -x test \
    && JAR_FILE="$(ls infrastructure/build/libs/*.jar | grep -v plain | head -n1)" \
    && test -n "$JAR_FILE" \
    && cp "$JAR_FILE" /app/app.jar

FROM eclipse-temurin:21-jre-alpine AS runtime

RUN apk add --no-cache curl

RUN addgroup -g 1001 -S app && adduser -u 1001 -S app -G app

WORKDIR /app

COPY --from=builder /app/app.jar app.jar

RUN chown app:app app.jar

USER app

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
    CMD curl -fsS http://127.0.0.1:8080/v3/api-docs >/dev/null || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
