# Multi-stage Dockerfile for Spring Boot CRM Application
# Build Stage
FROM maven:3.8.6-openjdk-8-slim AS builder

WORKDIR /workspace

# Copy Maven POM and download dependencies (for layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build application
COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime Stage
FROM amazoncorretto:8

WORKDIR /app

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy JAR from builder stage
COPY --from=builder /workspace/target/*.jar app.jar

# Set timezone
ENV TZ=UTC

# JVM memory and performance settings
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Spring Boot profile
ENV SPRING_PROFILES_ACTIVE=docker

# Application configuration
ENV SERVER_PORT=8080
ENV DDL_AUTO=validate
ENV DB_URL=jdbc:mysql://mysql:3306/crm?useSSL=false
ENV DB_USER=root
ENV DB_PASSWORD=changeme

# Change ownership
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose application port
EXPOSE 8080

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]