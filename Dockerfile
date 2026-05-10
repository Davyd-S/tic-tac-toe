# Етап 1: Збірка (використовуємо Java 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Етап 2: Запуск
FROM eclipse-temurin:21-jre
COPY --from=build /target/tictactoe-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]