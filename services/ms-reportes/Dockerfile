# Contexto de build: raíz del repo (compose: context .., dockerfile services/ms-reportes/Dockerfile).
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /workspace/services/ms-reportes

COPY config/quality /workspace/config/quality
COPY services/ms-reportes/gradle/ gradle/
COPY services/ms-reportes/gradlew services/ms-reportes/settings.gradle services/ms-reportes/build.gradle ./
COPY services/ms-reportes/src/ src/

RUN chmod +x gradlew \
    && ./gradlew bootJar --no-daemon -x test \
    && FJAR="$(find build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' | head -n1)" \
    && if [ -z "$FJAR" ] || [ ! -f "$FJAR" ]; then \
         echo "No se encontró el JAR ejecutable (bootJar). Contenido de libs:"; \
         ls -la build/libs/ 2>&1 || ls -laR build/ 2>&1 || true; \
         exit 1; \
       fi \
    && cp "$FJAR" /tmp/app.jar

FROM eclipse-temurin:21-jre-alpine AS runtime

RUN apk add --no-cache curl

RUN addgroup -g 1001 -S app && adduser -u 1001 -S app -G app

WORKDIR /app

COPY --from=builder /tmp/app.jar app.jar

RUN chown app:app app.jar

USER app

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
    CMD ["sh", "-c", "curl -fsS \"http://127.0.0.1:$${SERVER_PORT:-8080}/v3/api-docs\" >/dev/null || exit 1"]

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
