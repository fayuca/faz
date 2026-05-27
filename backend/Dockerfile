FROM eclipse-temurin:22-jdk AS build
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -B dependency:go-offline -DskipTests

COPY src src
RUN ./mvnw -B package -DskipTests

FROM eclipse-temurin:22-jre
WORKDIR /app

COPY --from=build /app/target/faz-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
