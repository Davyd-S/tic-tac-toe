# Етап 1: Збірка (використовуємо Maven з Java 8)
FROM maven:3.8.5-openjdk-8 AS build
COPY . .
RUN mvn clean package -DskipTests

# Етап 2: Запуск (використовуємо легкий образ Java 8)
FROM openjdk:8-jdk-alpine
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]