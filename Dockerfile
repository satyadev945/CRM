# Stage 1: Build stage
FROM maven:3.9.4-eclipse-temurin-8 AS builder

WORKDIR /workspace

# Copy pom.xml file first for dependency caching
COPY pom.xml .

# Download dependencies (separated to leverage Docker cache)
RUN mvn dependency:go-offline

# Copy the project source code
COPY src/ ./src/

# Build the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM amazoncorretto:8

WORKDIR /app

# Create a non-root user to run the application
RUN groupadd -r spring && useradd -r -g spring spring

# Copy the built JAR file from the builder stage
COPY --from=builder /workspace/target/*.jar app.jar

# Set proper permissions
RUN chown -R spring:spring /app
USER spring

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENV TZ=UTC
ENV SPRING_PROFILES_ACTIVE=docker

# Expose the port the application runs on
EXPOSE 8080

# Run the application
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]