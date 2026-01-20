# Build stage
FROM maven:3.9.4-eclipse-temurin-8 AS builder

WORKDIR /workspace

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies (this layer will be cached)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM amazoncorretto:8

WORKDIR /app

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy the JAR from builder stage
COPY --from=builder /workspace/target/crm-0.0.1-SNAPSHOT.jar /app/crm.jar

# Set ownership
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Set JVM options for containerized environment
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Set timezone
ENV TZ=UTC

# Expose application port
EXPOSE 8080

# Health check is handled by ECS service configuration
# Application provides /actuator/health endpoint

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/crm.jar"]
