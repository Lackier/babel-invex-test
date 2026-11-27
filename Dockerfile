# Pinned to JDK 17.0.21.1_2, which fixes CVE-2026-114938 (OpenJDK Base64 decoder OOB write).
# maven:3.9.9-eclipse-temurin-17 is a frozen tag (last pushed 2025-06-03) and never receives JDK patches,
# so the build stage uses the floating "3-eclipse-temurin-17" tag pinned to its post-patch digest instead.

# --- Build stage ---
FROM maven:3-eclipse-temurin-17@sha256:01dbb85e5c7236970e0a3b5dc47994af3f5774e2bb0a11156b905a8f4c68ff96 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q package -DskipTests

# --- Runtime stage ---
FROM eclipse-temurin:17.0.21.1_2-jre-alpine@sha256:2b4113ae80e31f103b284dd3bc3e008add670cf8555417b5b289fa1128f2f0a3
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build /build/target/employee-service.jar app.jar
USER spring:spring
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
