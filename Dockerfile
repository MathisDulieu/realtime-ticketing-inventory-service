FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests --no-transfer-progress

ENTRYPOINT ["java", "-jar", "target/*.jar"]

ENV SPRING_PROFILES_ACTIVE=prod
