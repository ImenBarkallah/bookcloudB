FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /workspace

RUN apk add --no-cache maven ca-certificates && update-ca-certificates

COPY pom.xml ./
RUN mvn -B -DskipTests -e dependency:go-offline

COPY src ./src
RUN mvn -q -B package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S bookcloud && adduser -S bookcloud -G bookcloud

COPY --from=build /workspace/target/*.jar /app/app.jar

RUN chown -R bookcloud:bookcloud /app

USER bookcloud

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=5 \
  CMD wget --no-verbose --tries=1 --spider http://127.0.0.1:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
