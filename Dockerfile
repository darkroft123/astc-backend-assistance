FROM registry.access.redhat.com/ubi8/openjdk-21:latest AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests -Dquarkus.package.jar.type=uber-jar

FROM registry.access.redhat.com/ubi8/openjdk-21-runtime:latest
WORKDIR /app
COPY --from=builder /build/target/*-runner.jar app.jar
EXPOSE 8082
ENV JAVA_OPTS="-Djava.util.logging.manager=org.jboss.logmanager.LogManager"
CMD ["java", "-jar", "app.jar"]
