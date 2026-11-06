
# Étape 1 : compilation du backend Spring Boot
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Étape 2 : image d'exécution Java
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/le-grande-cinema-0.0.1-SNAPSHOT.war app.war

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.war"]
