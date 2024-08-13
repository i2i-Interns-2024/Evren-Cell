
# Use Amazon Corretto 17 as the base image
FROM amazoncorretto:17

WORKDIR /app

# Copy the application properties file
COPY src/main/resources/application.properties /app/application.properties

# Copy the external dependency JAR files
COPY src/main/resources/Kafka.jar /app/Kafka.jar

# Copy the compiled application JAR file (without dependencies)
COPY target/ABMF-1.0-SNAPSHOT.jar /app/ABMF.jar

EXPOSE 8080

# Run the application
CMD ["java", "-cp", "ABMF.jar:Kafka.jar", "com.i2i.evrencell.abmf.ABMF"]
