# Stage 1: Build stage
FROM maven:3.9.4-eclipse-temurin-8 AS builder

WORKDIR /workspace

# Copy the entire project structure (required for multi-module projects)
COPY . .

# Build the application
# For single module: builds the main project
# For multi-module: builds parent POM and all modules
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM amazoncorretto:8

# Set working directory
WORKDIR /app

# Create non-root user for security
RUN yum install -y shadow-utils && \
    groupadd -r appuser && \
    useradd -r -g appuser appuser && \
    yum clean all

# Copy the built JAR from builder stage
# Adjust the path if this is a multi-module project
COPY --from=builder /workspace/target/*.jar app.jar

# Set ownership
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Set JVM options for container environment
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]