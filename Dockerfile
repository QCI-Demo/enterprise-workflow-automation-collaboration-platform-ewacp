FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY target/task-scheduler-service-*.jar /app/app.jar
EXPOSE 8080
USER 65532:65532
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
