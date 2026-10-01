FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY . .
RUN mvn -q -pl risk-service -am package -DskipTests

FROM eclipse-temurin:17-jre
RUN useradd -u 10001 app          # non-root: required by OpenShift's restricted SCC
USER 10001
COPY --from=build /src/risk-service/target/*.jar /app.jar
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","/app.jar"]
