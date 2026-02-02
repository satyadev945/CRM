# CRM Application Deployment Guide

This guide provides comprehensive instructions for containerizing and deploying the CRM application to AWS ECS Fargate. The deployment process is designed to be flexible, allowing for both local development and production deployment on AWS cloud infrastructure.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Project Overview](#project-overview)
- [Local Development Setup](#local-development-setup)
- [Docker Deployment](#docker-deployment)
- [AWS ECS Fargate Deployment](#aws-ecs-fargate-deployment)
- [Configuration Management](#configuration-management)
- [Monitoring and Logging](#monitoring-and-logging)
- [Security Considerations](#security-considerations)
- [Troubleshooting](#troubleshooting)
- [Additional Resources](#additional-resources)

## Prerequisites

### Required Software

- Docker (v20.10 or later) - [Install Docker](https://docs.docker.com/get-docker/)
- Docker Compose (v2.0 or later) - [Install Docker Compose](https://docs.docker.com/compose/install/)
- Java Development Kit (JDK) 8 - [Install JDK](https://adoptium.net/)
- Maven (v3.5 or later) - [Install Maven](https://maven.apache.org/install.html)
- AWS CLI (v2.0 or later) - [Install AWS CLI](https://aws.amazon.com/cli/)
- AWS account with permissions for:
  - ECS (Elastic Container Service)
  - ECR (Elastic Container Registry)
  - CloudWatch
  - IAM (Identity and Access Management)
  - VPC (Virtual Private Cloud)
  - ELB (Elastic Load Balancing)

### AWS Account Setup

1. Ensure you have an AWS account with administrative access
2. Configure AWS CLI:

```bash
aws configure
```

Enter your AWS Access Key ID, Secret Access Key, default region, and output format.

### Required Resources

1. **AWS VPC** with:
   - At least two subnets in different availability zones
   - A security group that allows inbound traffic on port 8080
   - Internet connectivity (via Internet Gateway)

2. **AWS IAM Roles**:
   - `ecsTaskExecutionRole` - Allows ECS to pull Docker images and push logs to CloudWatch
   - `ecsTaskRole` - Allows the container application to access AWS services (if needed)

## Project Overview

The CRM application is a Spring Boot 1.5.10 web application built with the following technologies:

- Java 8
- Spring Boot 1.5.10
- Spring MVC
- Spring Security
- Spring Data JPA
- Thymeleaf templating engine
- MySQL database

The application provides customer relationship management functionality including:
- Customer management
- User management with role-based access control
- Contract management
- Data export (PDF, CSV, Excel)

## Local Development Setup

### Database Setup

1. Install MySQL server:

```bash
# For Ubuntu/Debian
sudo apt-get install mysql-server

# For macOS
brew install mysql

# For Windows
# Download and install from https://dev.mysql.com/downloads/installer/
```

2. Create the database and user:

```sql
CREATE DATABASE crm;
CREATE USER 'root'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON crm.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

### Running the Application Locally

1. Clone the repository:

```bash
git clone <repository-url>
cd CRM
```

2. Build the application:

```bash
mvn clean package
```

3. Run the application:

```bash
java -jar target/crm-0.0.1-SNAPSHOT.jar
```

The application will be available at http://localhost:8080

### Running with Docker Compose

1. Build and start the application using Docker Compose:

```bash
docker-compose up -d
```

This will build the Docker image and start the application container. The application will be available at http://localhost:8080.

2. View the logs:

```bash
docker-compose logs -f
```

3. Stop the containers:

```bash
docker-compose down
```

## Docker Deployment

### Building and Pushing the Docker Image

The project includes scripts to build and push the Docker image to either AWS ECR or Docker Hub.

#### Using the Build Scripts

1. For Linux/macOS:

```bash
chmod +x ./scripts/build-push.sh
./scripts/build-push.sh
```

2. For Windows:

```bash
.\scripts\build-push.bat
```

The script will prompt you for the following information:
- Registry type (AWS ECR or Docker Hub)
- AWS region (if using ECR)
- Docker Hub credentials (if using Docker Hub)
- Image tag (defaults to "latest")

The script handles:
- Building the Docker image
- Tagging the image
- Creating the ECR repository if it doesn't exist (for AWS ECR)
- Authenticating with the registry
- Pushing the image to the registry

### Docker Image Structure

The Docker image is built using a multi-stage approach:

1. **Builder Stage**: Uses `maven:3.9.4-eclipse-temurin-8` to build the application
   - Caches dependencies for faster builds
   - Compiles the application
   - Runs tests
   - Creates the executable JAR file

2. **Runtime Stage**: Uses `amazoncorretto:8` for running the application
   - Creates a non-root user for security
   - Copies the JAR file from the builder stage
   - Sets appropriate Java options
   - Exposes port 8080

### Docker Compose Configuration

The `docker-compose.yml` file is configured with:

- Port mapping (8080:8080)
- Environment variables for database connection
- Volume mounts for logs and configuration
- Health check endpoint
- Restart policy

## AWS ECS Fargate Deployment

### Prerequisites for AWS ECS Deployment

1. **VPC Configuration**:
   - VPC with at least 2 subnets in different availability zones
   - Security group allowing inbound traffic on port 8080
   - Internet access via Internet Gateway or NAT Gateway

2. **IAM Roles**:
   - `ecsTaskExecutionRole` - For pulling images and sending logs
   - `ecsTaskRole` - For application permissions (optional)

3. **Docker Image**:
   - The application image must be pushed to a registry (ECR or Docker Hub)

### Deployment Steps

The project includes scripts to deploy the application to AWS ECS Fargate.

#### Using the Deployment Scripts

1. For Linux/macOS:

```bash
chmod +x ./scripts/deploy-image.sh
./scripts/deploy-image.sh
```

2. For Windows:

```bash
.\scripts\deploy-image.bat
```

The script will prompt you for the following information:
- AWS region
- ECS cluster name
- AWS account ID (retrieved automatically if possible)
- Environment name (default: production)
- Subnet IDs (2 subnets in different availability zones)
- Security group ID
- Database configuration (host, username, password)
- Docker image URI
- Load balancer configuration (optional)

The script handles:
- Creating the ECS cluster if it doesn't exist
- Creating the CloudWatch log group
- Registering the task definition
- Creating or updating the ECS service
- Setting up the load balancer and target group (if requested)
- Waiting for the service to stabilize

### ECS Task Definition

The task definition (`ecs/task-definition.json`) specifies:

- Fargate compatibility
- CPU and memory allocation (512 CPU units, 1024MB memory)
- Network mode (awsvpc, required for Fargate)
- IAM roles for execution and task permissions
- Container definition with:
  - Image URI
  - Port mappings
  - Environment variables
  - CloudWatch logging configuration

### ECS Service Definition

The service definition (`ecs/service-definition.json`) specifies:

- Fargate launch type
- Desired task count
- Network configuration with subnets and security groups
- Load balancer integration (optional)
- Service discovery configuration (optional)
- Auto-scaling configuration (optional)

## Configuration Management

### Environment Variables

The application can be configured using the following environment variables:

| Variable | Description | Default |
|----------|-------------|--------|
| `SPRING_DATASOURCE_URL` | JDBC URL for the database | jdbc:mysql://localhost:3306/crm?useSSL=false |
| `SPRING_DATASOURCE_USERNAME` | Database username | root |
| `SPRING_DATASOURCE_PASSWORD` | Database password | password |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Hibernate schema generation strategy | create-drop (development), update (production) |
| `SPRING_PROFILES_ACTIVE` | Spring profile to activate | default |
| `JAVA_OPTS` | JVM options | -Xmx512m -Xms256m |
| `TZ` | Timezone | UTC |

### Configuration Files

The application uses the following configuration files:

- `application.properties` - Main configuration file
- `application-{profile}.properties` - Profile-specific configuration

### Secrets Management

For production deployments, it's recommended to use AWS Secrets Manager or AWS SSM Parameter Store for sensitive information like database credentials. Update the task definition to use secrets from AWS Secrets Manager.

Example:

```json
"secrets": [
  {
    "name": "SPRING_DATASOURCE_PASSWORD",
    "valueFrom": "arn:aws:secretsmanager:region:account-id:secret:db-password-xyz"
  }
]
```

## Monitoring and Logging

### CloudWatch Logs

The ECS task is configured to send logs to CloudWatch Logs in the `/ecs/crm` log group. You can view these logs in the CloudWatch console or using the AWS CLI:

```bash
aws logs get-log-events --log-group-name /ecs/crm --log-stream-name ecs/crm/<task-id> --region <region>
```

### Spring Boot Actuator

The application includes Spring Boot Actuator for monitoring and management. The following endpoints are available:

- `/appinfo/health` - Health check endpoint
- `/appinfo/info` - Application info
- `/appinfo/metrics` - Application metrics

### Container Health Check

The Docker Compose configuration includes a health check that verifies the application is running correctly by calling the Actuator health endpoint:

```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/appinfo/health"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 60s
```

## Security Considerations

### Container Security

1. **Non-root User**:
   - The Dockerfile creates a non-root user (`spring`) for running the application.

2. **Security Updates**:
   - Regularly update the base image to include security patches.

3. **Secret Management**:
   - Use AWS Secrets Manager for sensitive information.
   - Do not hardcode secrets in the Dockerfile or configuration files.

### Network Security

1. **Security Groups**:
   - Configure security groups to allow only necessary inbound traffic.
   - For production, restrict traffic to the load balancer only.

2. **Private Subnets**:
   - Deploy ECS tasks in private subnets with a NAT Gateway for outbound traffic.

3. **HTTPS**:
   - Use HTTPS for all production traffic.
   - Configure the load balancer with an SSL certificate.

### Application Security

1. **Spring Security**:
   - The application uses Spring Security for authentication and authorization.
   - Ensure proper role-based access control.

2. **Database Security**:
   - Use strong passwords for database access.
   - Limit database user permissions.

## Troubleshooting

### Common Issues

#### Docker Build Issues

1. **Maven Build Fails**:
   - Check the Maven build logs for detailed error messages.
   - Ensure all dependencies are available.

2. **Docker Build Fails**:
   - Check Docker daemon is running.
   - Check disk space availability.

#### ECS Deployment Issues

1. **Task Fails to Start**:
   - Check the CloudWatch logs for application errors.
   - Verify IAM roles have correct permissions.
   - Check subnet configuration has internet access.

2. **Service Cannot Reach Database**:
   - Verify security group allows traffic from ECS tasks to the database.
   - Check database credentials.
   - Verify database hostname is resolvable.

3. **Load Balancer Health Checks Failing**:
   - Verify the health check path is correct.
   - Check if the application is starting correctly.
   - Increase health check grace period if the application takes longer to start.

### Diagnostic Steps

1. **Check Task Status**:

```bash
aws ecs describe-tasks --cluster <cluster-name> --tasks <task-id> --region <region>
```

2. **Check CloudWatch Logs**:

```bash
aws logs get-log-events --log-group-name /ecs/crm --log-stream-name <log-stream-name> --region <region>
```

3. **Check Service Events**:

```bash
aws ecs describe-services --cluster <cluster-name> --services crm-service --region <region>
```

## Additional Resources

### AWS Documentation

- [AWS ECS Documentation](https://docs.aws.amazon.com/ecs)
- [AWS Fargate Documentation](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/AWS_Fargate.html)
- [AWS ECR Documentation](https://docs.aws.amazon.com/ecr)
- [CloudWatch Documentation](https://docs.aws.amazon.com/cloudwatch)

### Spring Boot Documentation

- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/1.5.10.RELEASE/reference/htmlsingle/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/1.5.10.RELEASE/reference/htmlsingle/#production-ready)
- [Spring Security](https://docs.spring.io/spring-security/site/docs/4.2.x/reference/htmlsingle/)

### Docker Documentation

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)