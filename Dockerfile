
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B -U clean package -DskipTests

# Verifica arquivos relacionados ao build Vaadin
RUN jar tf target/agendador-1.0.0.jar | grep -i "flow-build-info" || true

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/agendador-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dvaadin.productionMode=true", "-jar", "app.jar"]
