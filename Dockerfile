# =========================
# BUILD STAGE (MAVEN)
# =========================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /build

# cache de dependencias (MEJORA IMPORTANTE)
COPY pom.xml .
RUN mvn -B dependency:go-offline \
    -Dhttps.protocols=TLSv1.2 \
    -Dmaven.wagon.http.pool=false

# source
COPY src ./src

# build con seguridad TLS + menos crashes
RUN mvn -B clean package \
    -DskipTests \
    -Dquarkus.package.jar.type=uber-jar \
    -Dhttps.protocols=TLSv1.2 \
    -Dmaven.wagon.http.pool=false


# =========================
# RUNTIME STAGE
# =========================
FROM registry.access.redhat.com/ubi8/openjdk-21-runtime:latest

WORKDIR /app

COPY --from=builder /build/target/*-runner.jar app.jar

EXPOSE 8082

ENV JAVA_OPTS="-Djava.util.logging.manager=org.jboss.logmanager.LogManager"

CMD ["java", "-jar", "app.jar"]