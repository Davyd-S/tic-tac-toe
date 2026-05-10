# Етап 1: Збірка
FROM maven:3.9-eclipse-temurin-8 AS build
COPY . .
RUN mvn clean package -DskipTests

# Етап 2: Запуск
FROM eclipse-temurin:8-jre
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]