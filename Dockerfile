# Multi-stage Dockerfile for Spring Boot CRM Application
# Stage 1: Build stage using Maven and Eclipse Temurin Java 8
FROM maven:3.9.4-eclipse-temurin-8 AS builder

# Set working directory
WORKDIR /workspace

# Copy Maven project files first for dependency caching
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy application source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests -B

# Verify the JAR was created
RUN ls -lh /workspace/target/

# Stage 2: Runtime stage using Amazon Corretto 8 (explicit base image)
FROM amazoncorretto:8

# Set maintainer label
LABEL maintainer="CRM Application Team"
LABEL application="crm-spring-boot"
LABEL version="0.0.1-SNAPSHOT"

# Create non-root user for security
RUN yum install -y shadow-utils && \
    groupadd -r appuser && \
    useradd -r -g appuser -s /sbin/nologin -c "Application user" appuser && \
    yum clean all

# Create application directory
RUN mkdir -p /app /app/logs /app/config /app/pdf-output && \
    chown -R appuser:appuser /app

# Set working directory
WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /workspace/target/*.jar app.jar

# Change ownership to non-root user
RUN chown appuser:appuser app.jar

# Set environment variables for JVM tuning
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Set timezone
ENV TZ=UTC

# Application environment variables (defaults)
ENV SERVER_PORT=8080 \
    SPRING_PROFILES_ACTIVE=docker \
    DB_URL=jdbc:mysql://mysql:3306/crm?useSSL=false \
    DB_USER=root \
    DB_PASSWORD=password \
    DDL_AUTO=validate \
    PDF_OUTPUT_PATH=/app/pdf-output

# Switch to non-root user
USER appuser

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
