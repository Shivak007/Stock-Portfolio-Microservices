FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/report-service-1.0.0.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar"]