# Multi-stage build for optimized container image
FROM maven:3.8.6-eclipse-temurin-11-focal AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies first (for better caching)
COPY ./pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY ./src ./src

# Build application
RUN mvn clean package -DskipTests

# Create final lightweight runtime image
FROM eclipse-temurin:11-jre-focal

# Set metadata
LABEL maintainer="CRM Cloud Team"
LABEL application="CRM Cloud Application"

# Set working directory
WORKDIR /app

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=cloud

# Create non-root user for security
RUN useradd -ms /bin/bash crmuser

# Create directory for app and set permissions
RUN mkdir -p /app/storage && \
    chown -R crmuser:crmuser /app

# Switch to non-root user
USER crmuser

# Copy built JAR from build stage
COPY --from=build --chown=crmuser:crmuser /app/target/*.jar /app/app.jar

# Expose the port the app will run on
EXPOSE 8080

# Run application with default options
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]

# Default command (can be overridden)
CMD ["--spring.profiles.active=cloud"]