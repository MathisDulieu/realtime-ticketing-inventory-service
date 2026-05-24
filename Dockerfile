FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

ARG NEXUS_USERNAME
ARG NEXUS_PASSWORD

ENV NEXUS_USERNAME=$NEXUS_USERNAME
ENV NEXUS_PASSWORD=$NEXUS_PASSWORD

COPY pom.xml .
COPY src ./src
COPY config/maven/settings.xml ./config/maven/settings.xml

RUN mvn package -Pprod -s config/maven/settings.xml -DskipTests --no-transfer-progress

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=prod

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
