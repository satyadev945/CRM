# CRM Application - AWS ECS Fargate Deployment Guide

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Project Overview](#project-overview)
3. [Local Development Setup](#local-development-setup)
4. [Docker Build and Push](#docker-build-and-push)
5. [AWS ECS Fargate Deployment](#aws-ecs-fargate-deployment)
6. [Configuration Management](#configuration-management)
7. [Monitoring and Logging](#monitoring-and-logging)
8. [Troubleshooting](#troubleshooting)
9. [Security Considerations](#security-considerations)

## Prerequisites

### Required Software
- Docker Desktop (version 20.10+)
- AWS CLI (version 2.0+)
- Git
- Java 8 JDK (for local development)
- Maven 3.6+ (for local development)

### AWS Requirements
- AWS Account with appropriate IAM permissions
- AWS CLI configured with credentials
- VPC with at least 2 subnets in different availability zones
- Security group allowing inbound traffic on port 8080
- IAM roles for ECS task execution and task permissions

### IAM Roles Required

#### ECS Task Execution Role (ecsTaskExecutionRole)
Allows ECS to pull container images and write logs:
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "ecs-tasks.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
```

Attach managed policy: `AmazonECSTaskExecutionRolePolicy`

#### ECS Task Role (ecsTaskRole)
Allows your application to access AWS services:
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "ecs-tasks.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
```

Attach policies as needed for your application (S3, RDS, etc.)

## Project Overview

### Technology Stack
- **Framework**: Spring Boot 1.5.10
- **Java Version**: 8
- **Build Tool**: Maven
- **Package Type**: JAR
- **Application Type**: Web Application (Thymeleaf + REST API)
- **Database**: MySQL (configurable)
- **Security**: Spring Security
- **Management**: Spring Boot Actuator

### Application Configuration
- **Application Port**: 8080
- **Management Endpoint**: /appinfo
- **Health Check**: /appinfo/health
- **Database**: MySQL (configured via environment variables)

### Key Dependencies
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Thymeleaf
- Spring Boot Actuator
- MySQL Connector
- Lombok
- iTextPDF (PDF generation)
- Apache POI (Excel processing)
- OpenCSV (CSV processing)

## Local Development Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd mono-rah
```

### 2. Configure Environment Variables
Create a `.env` file in the project root:
```bash
DB_HOST=localhost
DB_PORT=3306
DB_NAME=crm
DB_USER=root
DB_PASSWORD=yourpassword
```

### 3. Build the Application
```bash
mvn clean package -DskipTests
```

### 4. Run Locally with Maven
```bash
mvn spring-boot:run
```

### 5. Run with Docker Compose
```bash
docker-compose up --build
```

Access the application at: `http://localhost:8080`

## Docker Build and Push

### Build Docker Image Locally
```bash
docker build -t crm:latest .
```

### Push to Container Registry

#### Option 1: AWS ECR (Recommended for ECS)
```bash
# Run the build-push script
chmod +x scripts/build-push.sh
./scripts/build-push.sh

# Select option 1 for AWS ECR
# Follow the prompts to enter:
# - AWS Region
# - AWS Account ID
# - ECR Repository Name
# - Image Tag
```

#### Option 2: Docker Hub
```bash
# Run the build-push script
chmod +x scripts/build-push.sh
./scripts/build-push.sh

# Select option 2 for Docker Hub
# Follow the prompts to enter:
# - Docker Hub username
# - Docker Hub password/token
# - Image Tag
```

### Windows Users
```cmd
scripts\build-push.bat
```

## AWS ECS Fargate Deployment

### ECS Fargate Overview
AWS Fargate is a serverless compute engine for containers that eliminates the need to manage EC2 instances. Benefits include:
- No server management required
- Pay only for resources used
- Automatic scaling
- Built-in security isolation
- Seamless integration with AWS services

### Fargate CPU and Memory Combinations
ECS Fargate requires specific CPU/memory combinations:

| CPU (vCPU) | Memory Options (MB) |
|------------|---------------------|
| 256 (.25)  | 512, 1024, 2048 |
| 512 (.5)   | 1024, 2048, 3072, 4096 |
| 1024 (1)   | 2048-8192 (increments of 1024) |
| 2048 (2)   | 4096-16384 (increments of 1024) |
| 4096 (4)   | 8192-30720 (increments of 1024) |

Default configuration: **512 CPU / 1024 MB Memory**

### Network Configuration
Fargate uses `awsvpc` network mode, which provides each task with its own elastic network interface (ENI).

**Requirements:**
- VPC with at least 2 subnets in different availability zones
- Security group allowing:
  - Inbound: Port 8080 (application)
  - Outbound: Port 443 (HTTPS for AWS services)
  - Outbound: Port 3306 (MySQL database)

### Step-by-Step Deployment

#### Step 1: Prepare AWS Resources

1. **Create VPC and Subnets** (if not exists)
```bash
# Use AWS Console or CLI to create VPC
aws ec2 create-vpc --cidr-block 10.0.0.0/16

# Create subnets in different AZs
aws ec2 create-subnet --vpc-id <vpc-id> --cidr-block 10.0.1.0/24 --availability-zone us-east-1a
aws ec2 create-subnet --vpc-id <vpc-id> --cidr-block 10.0.2.0/24 --availability-zone us-east-1b
```

2. **Create Security Group**
```bash
aws ec2 create-security-group \
  --group-name crm-sg \
  --description "Security group for CRM application" \
  --vpc-id <vpc-id>

# Allow inbound traffic on port 8080
aws ec2 authorize-security-group-ingress \
  --group-id <security-group-id> \
  --protocol tcp \
  --port 8080 \
  --cidr 0.0.0.0/0

# Allow inbound traffic on port 80 (if using ALB)
aws ec2 authorize-security-group-ingress \
  --group-id <security-group-id> \
  --protocol tcp \
  --port 80 \
  --cidr 0.0.0.0/0
```

3. **Create IAM Roles** (see Prerequisites section)

#### Step 2: Deploy Database

Option 1: **AWS RDS MySQL**
```bash
aws rds create-db-instance \
  --db-instance-identifier crm-db \
  --db-instance-class db.t3.micro \
  --engine mysql \
  --master-username admin \
  --master-user-password <password> \
  --allocated-storage 20 \
  --vpc-security-group-ids <security-group-id> \
  --db-subnet-group-name <subnet-group-name>
```

Option 2: **External MySQL Server**
Ensure your security group allows outbound traffic to the database.

#### Step 3: Run Deployment Script

**Linux/macOS:**
```bash
chmod +x scripts/deploy-image.sh
./scripts/deploy-image.sh
```

**Windows:**
```cmd
scripts\deploy-image.bat
```

#### Step 4: Follow Interactive Prompts

The script will prompt for:
1. AWS Region (e.g., us-east-1)
2. ECS Cluster Name (creates if not exists)
3. VPC ID
4. Subnet IDs (comma-separated)
5. Security Group ID
6. Database Configuration:
   - Host
   - Port (default: 3306)
   - Database Name (default: crm)
   - Username
   - Password
7. ECR Image URI
8. Load Balancer (y/n)

#### Step 5: Verify Deployment

1. **Check ECS Service Status**
```bash
aws ecs describe-services \
  --cluster <cluster-name> \
  --services crm-service \
  --region <region>
```

2. **View Running Tasks**
```bash
aws ecs list-tasks \
  --cluster <cluster-name> \
  --service-name crm-service \
  --region <region>
```

3. **Check Task Health**
```bash
aws ecs describe-tasks \
  --cluster <cluster-name> \
  --tasks <task-id> \
  --region <region>
```

## Configuration Management

### Environment Variables
The application uses the following environment variables:

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| SPRING_PROFILES_ACTIVE | Spring profile | docker | No |
| JAVA_OPTS | JVM options | -Xmx768m -Xms512m | No |
| DB_HOST | Database host | localhost | Yes |
| DB_PORT | Database port | 3306 | Yes |
| DB_NAME | Database name | crm | Yes |
| DB_USER | Database username | root | Yes |
| DB_PASSWORD | Database password | - | Yes |
| TZ | Timezone | UTC | No |

### Update Environment Variables

1. **Update Task Definition**
Modify `ecs/task-definition.json` and update environment variables.

2. **Register New Task Definition**
```bash
aws ecs register-task-definition \
  --cli-input-json file://ecs/task-definition.json
```

3. **Update Service**
```bash
aws ecs update-service \
  --cluster <cluster-name> \
  --service crm-service \
  --task-definition crm-task:<revision>
```

### Using AWS Systems Manager Parameter Store
For sensitive data, consider using AWS Systems Manager Parameter Store:

```bash
# Store database password
aws ssm put-parameter \
  --name "/crm/db/password" \
  --value "<password>" \
  --type "SecureString"

# Reference in task definition
"secrets": [
  {
    "name": "DB_PASSWORD",
    "valueFrom": "/crm/db/password"
  }
]
```

## Monitoring and Logging

### CloudWatch Logs
Application logs are automatically sent to CloudWatch Logs.

**Log Group**: `/ecs/crm`

**View Logs:**
```bash
aws logs tail /ecs/crm --follow --region <region>
```

**CloudWatch Console:**
https://console.aws.amazon.com/cloudwatch/home?region=<region>#logsV2:log-groups/log-group/$252Fecs$252Fcrm

### CloudWatch Metrics
ECS Fargate automatically publishes metrics:
- CPU Utilization
- Memory Utilization
- Network In/Out
- Task Count

**View Metrics:**
```bash
aws cloudwatch get-metric-statistics \
  --namespace AWS/ECS \
  --metric-name CPUUtilization \
  --dimensions Name=ServiceName,Value=crm-service Name=ClusterName,Value=<cluster-name> \
  --start-time <timestamp> \
  --end-time <timestamp> \
  --period 300 \
  --statistics Average
```

### Spring Boot Actuator Endpoints
The application exposes management endpoints at `/appinfo`:

- **Health Check**: `http://<alb-dns>/appinfo/health`
- **Application Info**: `http://<alb-dns>/appinfo/info`
- **Metrics**: `http://<alb-dns>/appinfo/metrics`

## Troubleshooting

### Common Issues

#### 1. Task Fails to Start
**Symptom**: Tasks start and immediately stop.

**Possible Causes**:
- Image not found in ECR
- Incorrect IAM role permissions
- Invalid environment variables
- Database connection failure

**Solutions**:
```bash
# Check task stopped reason
aws ecs describe-tasks \
  --cluster <cluster-name> \
  --tasks <task-id> \
  --query 'tasks[0].stoppedReason'

# Check CloudWatch logs for errors
aws logs tail /ecs/crm --since 5m

# Verify IAM role
aws iam get-role --role-name ecsTaskExecutionRole
```

#### 2. Health Check Failures
**Symptom**: Tasks fail health checks and are replaced.

**Possible Causes**:
- Application startup time exceeds health check grace period
- Database connection issues
- Incorrect health check path

**Solutions**:
- Increase `healthCheckGracePeriodSeconds` in service definition (currently 300 seconds)
- Verify database connectivity from container
- Check health endpoint: `http://<task-ip>:8080/appinfo/health`

#### 3. Out of Memory Errors
**Symptom**: Tasks crash with OutOfMemoryError.

**Solutions**:
- Increase task memory allocation in task definition
- Adjust JVM heap size in JAVA_OPTS:
  ```json
  "JAVA_OPTS": "-Xmx768m -Xms512m -XX:MaxRAMPercentage=75.0"
  ```
- Monitor memory usage in CloudWatch

#### 4. Database Connection Errors
**Symptom**: Application logs show database connection failures.

**Solutions**:
- Verify security group allows outbound traffic to database
- Check database credentials in environment variables
- Ensure database is accessible from ECS tasks
- Test connection:
  ```bash
  aws ecs execute-command \
    --cluster <cluster-name> \
    --task <task-id> \
    --container crm \
    --interactive \
    --command "/bin/sh"
  ```

#### 5. Load Balancer Target Unhealthy
**Symptom**: ALB targets show unhealthy status.

**Solutions**:
- Verify target group health check settings
- Check security group allows traffic from ALB
- Ensure application responds on health check path
- Increase health check interval and timeout

### Debugging Commands

**View Service Events:**
```bash
aws ecs describe-services \
  --cluster <cluster-name> \
  --services crm-service \
  --query 'services[0].events[0:10]'
```

**Get Task Details:**
```bash
aws ecs describe-tasks \
  --cluster <cluster-name> \
  --tasks <task-id>
```

**View Task Logs:**
```bash
aws logs get-log-events \
  --log-group-name /ecs/crm \
  --log-stream-name ecs/crm/<task-id>
```

**Execute Command in Running Task:**
```bash
aws ecs execute-command \
  --cluster <cluster-name> \
  --task <task-id> \
  --container crm \
  --interactive \
  --command "/bin/sh"
```

## Security Considerations

### 1. IAM Roles and Permissions
- Use least privilege principle for IAM roles
- Separate execution role from task role
- Regularly audit IAM policies

### 2. Secrets Management
- Never hardcode credentials in task definitions
- Use AWS Secrets Manager or Systems Manager Parameter Store
- Rotate credentials regularly

### 3. Network Security
- Use private subnets for tasks when possible
- Restrict security group rules to minimum required
- Enable VPC Flow Logs for network monitoring
- Use AWS PrivateLink for AWS service access

### 4. Container Security
- Use official base images (Amazon Corretto)
- Regularly update base images for security patches
- Run containers as non-root user (implemented in Dockerfile)
- Scan images for vulnerabilities:
  ```bash
  aws ecr start-image-scan \
    --repository-name crm \
    --image-id imageTag=latest
  ```

### 5. Database Security
- Use RDS with encryption at rest
- Enable encryption in transit (SSL/TLS)
- Use RDS IAM authentication when possible
- Restrict database access to application security group only

### 6. Application Security
- Enable Spring Security (already configured)
- Use HTTPS for all external traffic
- Implement rate limiting and DDoS protection
- Regular security audits and penetration testing

### 7. Logging and Monitoring
- Enable CloudWatch Logs encryption
- Set up CloudWatch Alarms for anomalies
- Implement centralized logging with CloudWatch Insights
- Enable AWS CloudTrail for API auditing

## Scaling and Performance

### Auto Scaling
Configure ECS Service Auto Scaling:

```bash
# Register scalable target
aws application-autoscaling register-scalable-target \
  --service-namespace ecs \
  --resource-id service/<cluster-name>/crm-service \
  --scalable-dimension ecs:service:DesiredCount \
  --min-capacity 2 \
  --max-capacity 10

# Create scaling policy (CPU-based)
aws application-autoscaling put-scaling-policy \
  --service-namespace ecs \
  --resource-id service/<cluster-name>/crm-service \
  --scalable-dimension ecs:service:DesiredCount \
  --policy-name cpu-scaling-policy \
  --policy-type TargetTrackingScaling \
  --target-tracking-scaling-policy-configuration file://scaling-policy.json
```

**scaling-policy.json:**
```json
{
  "TargetValue": 70.0,
  "PredefinedMetricSpecification": {
    "PredefinedMetricType": "ECSServiceAverageCPUUtilization"
  },
  "ScaleInCooldown": 300,
  "ScaleOutCooldown": 60
}
```

### JVM Performance Tuning
Optimize JVM settings for containerized environment:

```bash
JAVA_OPTS="
  -Xmx768m 
  -Xms512m 
  -XX:+UseContainerSupport 
  -XX:MaxRAMPercentage=75.0 
  -XX:+UseG1GC 
  -XX:MaxGCPauseMillis=200 
  -XX:ParallelGCThreads=2 
  -XX:ConcGCThreads=1 
  -XX:InitiatingHeapOccupancyPercent=45 
  -Djava.security.egd=file:/dev/./urandom
"
```

### Database Connection Pooling
Configure HikariCP (default in Spring Boot) for optimal performance:

```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
```

## Blue/Green Deployments

Implement blue/green deployments for zero-downtime updates:

```bash
# Create new task definition revision
aws ecs register-task-definition --cli-input-json file://ecs/task-definition.json

# Update service with deployment circuit breaker
aws ecs update-service \
  --cluster <cluster-name> \
  --service crm-service \
  --task-definition crm-task:<new-revision> \
  --deployment-configuration "deploymentCircuitBreaker={enable=true,rollback=true},maximumPercent=200,minimumHealthyPercent=100"
```

## Cost Optimization

### Fargate Spot
Use Fargate Spot for non-critical workloads (up to 70% savings):

```json
"capacityProviderStrategy": [
  {
    "capacityProvider": "FARGATE_SPOT",
    "weight": 2
  },
  {
    "capacityProvider": "FARGATE",
    "weight": 1
  }
]
```

### Right-Sizing
- Monitor CPU and memory utilization
- Adjust task resource allocation based on actual usage
- Use smaller task sizes during off-peak hours

### Reserved Capacity
For predictable workloads, consider Savings Plans for compute resources.

## Support and Resources

### AWS Documentation
- [ECS Fargate Documentation](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/AWS_Fargate.html)
- [ECS Task Definitions](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task_definitions.html)
- [CloudWatch Logs](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/WhatIsCloudWatchLogs.html)

### Spring Boot Resources
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/1.5.x/reference/html/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/1.5.x/reference/html/production-ready.html)

### Useful Commands Reference

**ECS:**
```bash
# List clusters
aws ecs list-clusters

# List services
aws ecs list-services --cluster <cluster-name>

# List tasks
aws ecs list-tasks --cluster <cluster-name> --service-name crm-service

# Stop task
aws ecs stop-task --cluster <cluster-name> --task <task-id>

# Delete service
aws ecs delete-service --cluster <cluster-name> --service crm-service --force
```

**Docker:**
```bash
# Build image
docker build -t crm:latest .

# Run locally
docker run -d -p 8080:8080 --env-file .env crm:latest

# View logs
docker logs -f <container-id>

# Execute command
docker exec -it <container-id> /bin/sh
```

## Conclusion

This deployment guide provides comprehensive instructions for deploying the CRM application to AWS ECS Fargate. For additional support or questions, please contact your DevOps team or refer to the AWS documentation.

---

**Last Updated**: 2026-01-27  
**Version**: 1.0.0  
**Maintained By**: DevOps Team