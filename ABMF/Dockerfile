# Use Amazon Corretto 17 as the base image
FROM amazoncorretto:17

WORKDIR /app

COPY src/main/resources/application.properties /app/application.properties

COPY src/main/resources/Kafka.jar /app/Kafka.jar

# Copy the compiled application JAR file to the container
COPY target/ABMF-1.0-SNAPSHOT-jar-with-dependencies.jar /app/ABMF.jar

EXPOSE 8080

CMD ["java", "-cp", "your-application.jar:Kafka.jar", "com.yourcompany.YourMainClass"]


