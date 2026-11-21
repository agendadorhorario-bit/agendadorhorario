# Etapa 1: compilação Maven + Vaadin em modo de produção
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia primeiro o POM para aproveitar o cache das dependências
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copia o código-fonte e compila o frontend Vaadin em produção
COPY src ./src
RUN mvn -B clean package -Pproduction -DskipTests

# Etapa 2: imagem menor para execução
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/agendador-1.0.0.jar app.jar

# O Render define PORT (normalmente 10000); a aplicação deve respeitá-la.
ENV SERVER_PORT=${PORT:-8080}

EXPOSE 8080

ENTRYPOINT ["java", "-Dvaadin.productionMode=true", "-jar", "/app/app.jar"]
