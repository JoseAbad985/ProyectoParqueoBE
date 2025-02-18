# Use the official OpenJDK base image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the Maven executable
COPY mvnw .
COPY .mvn .mvn

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Make the mvnw script executable
RUN chmod +x mvnw

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose the port the app runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "target/ProyectoParqueo-1.0.0.jar"]