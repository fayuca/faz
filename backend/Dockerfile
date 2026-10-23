FROM eclipse-temurin:22-jdk AS build
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -B dependency:go-offline -DskipTests

COPY src src
RUN ./mvnw -B package -DskipTests

FROM eclipse-temurin:22-jre
WORKDIR /app

# eMudhra intermediate CA compromise (Oct 2026): distrust roots in JDK cacerts.
# Oracle advisory: remove these even after patched JDKs (e.g. 21.0.13.0.2).
# Docker Hub Temurin tags may lag Oracle/Adoptium binary releases — this mitigates now.
# https://ops.java/emudhra
RUN keytool -delete -cacerts -storepass changeit -alias "emsigneccrootcag3 [jdk]" || true \
 && keytool -delete -cacerts -storepass changeit -alias "comodoaaaca [jdk]" || true

COPY --from=build /app/target/faz-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
