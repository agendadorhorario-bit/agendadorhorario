
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn -B dependency:go-offline

COPY src ./src

# Compila Spring Boot e Vaadin em produção
RUN mvn -B -U clean package -DskipTests

# Confirma que o arquivo de produção foi incluído no JAR
RUN jar tf target/agendador-1.0.0.jar | grep "BOOT-INF/classes/META-INF/VAADIN/config/flow-build-info.json"

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/agendador-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dvaadin.productionMode=true", "-jar", "/app/app.jar"]
