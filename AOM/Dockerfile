FROM amazoncorretto:17-alpine

# Create a new user and group
RUN addgroup -S aomgroup && adduser -S aomuser -G aomgroup

# Set the working directory
WORKDIR /app

# Copy the JAR file to the new location
ARG JAR_FILE=./target/AOM-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /app/aom/jar/app.jar

# Change ownership of the app directory
RUN chown -R aomuser:aomgroup /app

# Create the logs directory and set permissions
RUN mkdir /logs && chown -R aomuser:aomgroup /logs && chmod -R 775 /logs

# Expose the logs directory as a volume
VOLUME /logs

# Expose port 8080
EXPOSE 8080

# Switch to the new user
USER aomuser

# Run the application
ENTRYPOINT ["java","-jar","/app/aom/jar/app.jar"]