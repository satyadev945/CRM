=== docs/DEPLOYMENT.md ===
# CRM Spring Boot Application - AWS ECS Fargate Deployment Guide

## Table of Contents
1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Local Development Setup](#local-development-setup)
4. [Building and Testing Locally](#building-and-testing-locally)
5. [AWS ECS Fargate Prerequisites](#aws-ecs-fargate-prerequisites)
6. [Building and Pushing Docker Image](#building-and-pushing-docker-image)
7. [ECS Task Definition Explained](#ecs-task-definition-explained)
8. [ECS Service Configuration](#ecs-service-configuration)
9. [Deploying to AWS ECS Fargate](#deploying-to-aws-ecs-fargate)
10. [Configuration Management](#configuration-management)
11. [Monitoring and Logging](#monitoring-and-logging)
12. [Troubleshooting](#troubleshooting)
13. [Security Best Practices](#security-best-practices)
14. [Scaling and Performance](#scaling-and-performance)

---

## Overview

This is a comprehensive deployment guide for the **CRM Spring Boot Application**, a Customer Relationship Management system built with:

- **Framework**: Spring Boot 1.5.10.RELEASE
- **Java Version**: Java 8 (1.8)
- **Build Tool**: Maven
- **Application Type**: Web Application with Thymeleaf templates
- **Database**: MySQL (configurable)
- **Features**: Customer management, contract generation, user/role management, PDF/CSV/Excel export

**Deployment Target**: AWS ECS Fargate

---

## Prerequisites

### Required Software

1. **Java Development Kit (JDK) 8**
   ```bash
   java -version  # Should show 1.8.x
   ```

2. **Apache Maven 3.6+**
   ```bash
   mvn -version
   ```

3. **Docker 20.10+**
   ```bash
   docker --version
   docker-compose --version
   ```

4. **AWS CLI v2**
   ```bash
   aws --version
   # Configure AWS credentials
   aws configure
   ```

5. **Git** (for version control)
   ```bash
   git --version
   ```

### AWS Account Requirements

- Active AWS account with appropriate permissions
- IAM user with permissions for:
  - ECS (create clusters, task definitions, services)
  - ECR (push/pull images)
  - EC2 (VPC, subnets, security groups)
  - IAM (create/manage roles)
  - CloudWatch Logs (create log groups)
  - Elastic Load Balancing (create ALB, target groups)

---

## Local Development Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd crm-spring-boot
```

### 2. Configure Application Properties

Edit `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/crm?useSSL=false
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=validate

# Management Endpoints
management.security.enabled=false
management.context-path=/appinfo
management.endpoints.web.exposure.include=health,info

# Thymeleaf Configuration
spring.thymeleaf.mode=LEGACYHTML5
spring.thymeleaf.cache=false
```

### 3. Set Up MySQL Database

```bash
# Using Docker
docker run --name crm-mysql \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=crm \
  -p 3306:3306 \
  -d mysql:5.7

# Create schema (Spring Boot will validate against existing schema)
# Import data.sql from src/main/resources for initial data
```

### 4. Build the Application

```bash
# Build with Maven (skip tests for faster builds)
mvn clean package -DskipTests

# Run the application
java -jar target/crm-0.0.1-SNAPSHOT.jar

# Or run with Maven
mvn spring-boot:run
```

### 5. Access the Application

- **Application URL**: http://localhost:8080
- **Health Check**: http://localhost:8080/appinfo/health
- **Application Info**: http://localhost:8080/appinfo/info

**Default Users** (from data.sql):
- **Admin**: admin@admin / admin
- **User**: user@user / user
- **Manager**: manager@manager / manager
- **Owner**: owner@owner / owner

---

## Building and Testing Locally

### Using Docker Compose

1. **Build and start the application**:
   ```bash
   docker-compose up --build
   ```

2. **Access the application**:
   - Application: http://localhost:8080
   - Health: http://localhost:8080/appinfo/health

3. **View logs**:
   ```bash
   docker-compose logs -f crm-app
   ```

4. **Stop the application**:
   ```bash
   docker-compose down
   ```

### Environment Variables

Customize the deployment by setting these environment variables:

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Application port | `8080` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `docker` |
| `DB_URL` | Database JDBC URL | `jdbc:mysql://mysql:3306/crm?useSSL=false` |
| `DB_USER` | Database username | `root` |
| `DB_PASSWORD` | Database password | `password` |
| `DDL_AUTO` | Hibernate DDL mode | `validate` |
| `PDF_OUTPUT_PATH` | PDF output directory | `/app/pdf-output` |
| `JAVA_OPTS` | JVM options | `-Xmx512m -Xms256m` |

---

## AWS ECS Fargate Prerequisites

### 1. Create IAM Roles

#### ECS Task Execution Role

```bash
# Create trust policy file: ecs-task-execution-trust-policy.json
cat > ecs-task-execution-trust-policy.json <<EOF
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
EOF

# Create the role
aws iam create-role \
  --role-name ecsTaskExecutionRole \
  --assume-role-policy-document file://ecs-task-execution-trust-policy.json

# Attach managed policy
aws iam attach-role-policy \
  --role-name ecsTaskExecutionRole \
  --policy-arn arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy
```

#### ECS Task Role (Optional - for application permissions)

```bash
# Create task role
aws iam create-role \
  --role-name ecsTaskRole \
  --assume-role-policy-document file://ecs-task-execution-trust-policy.json

# Attach policies as needed (S3, DynamoDB, etc.)
```

### 2. Set Up VPC and Networking

**Option A: Use Default VPC**
```bash
# Get default VPC ID
aws ec2 describe-vpcs --filters "Name=isDefault,Values=true" --query "Vpcs[0].VpcId" --output text

# Get default subnets
aws ec2 describe-subnets --filters "Name=default-for-az,Values=true" --query "Subnets[*].SubnetId" --output text
```

**Option B: Create New VPC**
```bash
# Create VPC
aws ec2 create-vpc --cidr-block 10.0.0.0/16 --tag-specifications 'ResourceType=vpc,Tags=[{Key=Name,Value=crm-vpc}]'

# Create subnets in different AZs
aws ec2 create-subnet --vpc-id <vpc-id> --cidr-block 10.0.1.0/24 --availability-zone us-east-1a
aws ec2 create-subnet --vpc-id <vpc-id> --cidr-block 10.0.2.0/24 --availability-zone us-east-1b

# Create internet gateway
aws ec2 create-internet-gateway --tag-specifications 'ResourceType=internet-gateway,Tags=[{Key=Name,Value=crm-igw}]'
aws ec2 attach-internet-gateway --vpc-id <vpc-id> --internet-gateway-id <igw-id>

# Create route table and add route
aws ec2 create-route-table --vpc-id <vpc-id>
aws ec2 create-route --route-table-id <rt-id> --destination-cidr-block 0.0.0.0/0 --gateway-id <igw-id>

# Associate subnets with route table
aws ec2 associate-route-table --route-table-id <rt-id> --subnet-id <subnet-id>
```

### 3. Create Security Group

```bash
# Create security group
aws ec2 create-security-group \
  --group-name crm-sg \
  --description "Security group for CRM application" \
  --vpc-id <vpc-id>

# Allow HTTP traffic (port 80 for ALB)
aws ec2 authorize-security-group-ingress \
  --group-id <sg-id> \
  --protocol tcp \
  --port 80 \
  --cidr 0.0.0.0/0

# Allow application port (8080) from within VPC
aws ec2 authorize-security-group-ingress \
  --group-id <sg-id> \
  --protocol tcp \
  --port 8080 \
  --cidr 10.0.0.0/16

# Allow MySQL port (3306) for RDS
aws ec2 authorize-security-group-ingress \
  --group-id <sg-id> \
  --protocol tcp \
  --port 3306 \
  --source-group <sg-id>
```

### 4. Set Up RDS MySQL Database (Recommended for Production)

```bash
# Create DB subnet group
aws rds create-db-subnet-group \
  --db-subnet-group-name crm-db-subnet-group \
  --db-subnet-group-description "Subnet group for CRM database" \
  --subnet-ids <subnet-id-1> <subnet-id-2>

# Create RDS MySQL instance
aws rds create-db-instance \
  --db-instance-identifier crm-mysql \
  --db-instance-class db.t3.micro \
  --engine mysql \
  --engine-version 5.7.44 \
  --master-username admin \
  --master-user-password <secure-password> \
  --allocated-storage 20 \
  --vpc-security-group-ids <sg-id> \
  --db-subnet-group-name crm-db-subnet-group \
  --backup-retention-period 7 \
  --preferred-backup-window "03:00-04:00" \
  --preferred-maintenance-window "mon:04:00-mon:05:00" \
  --db-name crm

# Wait for instance to be available
aws rds wait db-instance-available --db-instance-identifier crm-mysql

# Get endpoint
aws rds describe-db-instances \
  --db-instance-identifier crm-mysql \
  --query "DBInstances[0].Endpoint.Address" \
  --output text
```

**Update task definition environment variables with RDS endpoint**:
```json
{
  "name": "DB_URL",
  "value": "jdbc:mysql://<rds-endpoint>:3306/crm?useSSL=false"
}
```

---

## Building and Pushing Docker Image

### Option 1: Using AWS ECR (Recommended)

#### Step 1: Run Build Script

**Linux/macOS**:
```bash
chmod +x scripts/build-push.sh
./scripts/build-push.sh
```

**Windows**:
```cmd
scripts\build-push.bat
```

#### Step 2: Follow Interactive Prompts

1. **Select Registry**: Choose `1` for AWS ECR
2. **Enter AWS Region**: e.g., `us-east-1`
3. **Enter AWS Account ID**: Your 12-digit AWS account ID
4. **Enter Repository Name**: e.g., `crm-spring-boot` (will be created automatically)
5. **Enter Image Tag**: e.g., `v1.0.0` or `latest`

The script will:
- Authenticate with ECR
- Create repository if it doesn't exist
- Build Docker image with multi-stage build
- Push image to ECR
- Display image URI for deployment

### Option 2: Using Docker Hub

1. Select `2` for Docker Hub in the build script
2. Enter your Docker Hub username and password/token
3. Image will be pushed to `<username>/crm-spring-boot:<tag>`

### Manual Build (Alternative)

```bash
# Build image
docker build -t crm-spring-boot:latest .

# Tag for ECR
docker tag crm-spring-boot:latest <account-id>.dkr.ecr.<region>.amazonaws.com/crm-spring-boot:latest

# Authenticate with ECR
aws ecr get-login-password --region <region> | docker login --username AWS --password-stdin <account-id>.dkr.ecr.<region>.amazonaws.com

# Push to ECR
docker push <account-id>.dkr.ecr.<region>.amazonaws.com/crm-spring-boot:latest
```

---

## ECS Task Definition Explained

### Key Components

#### 1. Launch Type Configuration
```json
{
  "requiresCompatibilities": ["FARGATE"],
  "networkMode": "awsvpc"
}
```
- **FARGATE**: Serverless compute engine for containers
- **awsvpc**: Required for Fargate, provides each task with its own ENI

#### 2. CPU and Memory
```json
{
  "cpu": "512",
  "memory": "1024"
}
```

**Valid Fargate Combinations**:
- CPU: "256" (.25 vCPU) → Memory: 512, 1024, 2048 MB
- **CPU: "512" (.5 vCPU) → Memory: 1024, 2048, 3072, 4096 MB** ✓ (Used)
- CPU: "1024" (1 vCPU) → Memory: 2048-8192 MB
- CPU: "2048" (2 vCPU) → Memory: 4096-16384 MB
- CPU: "4096" (4 vCPU) → Memory: 8192-30720 MB

**Recommended for Java 8 Spring Boot**:
- Development: CPU "512", Memory "1024"
- Production: CPU "1024", Memory "2048"

#### 3. IAM Roles
```json
{
  "executionRoleArn": "arn:aws:iam::{{ACCOUNT_ID}}:role/ecsTaskExecutionRole",
  "taskRoleArn": "arn:aws:iam::{{ACCOUNT_ID}}:role/ecsTaskRole"
}
```

- **executionRoleArn**: Required - allows ECS to pull images and write logs
- **taskRoleArn**: Optional - grants permissions to application (S3, DynamoDB, etc.)

#### 4. Container Definition
```json
{
  "name": "crm-spring-boot",
  "image": "{{IMAGE_URI}}",
  "essential": true,
  "portMappings": [
    {
      "containerPort": 8080,
      "protocol": "tcp"
    }
  ]
}
```

- **essential**: If true, task stops if this container stops
- **portMappings**: Only containerPort needed (no hostPort in Fargate)

#### 5. Environment Variables
```json
"environment": [
  {
    "name": "DB_URL",
    "value": "jdbc:mysql://mysql-host:3306/crm?useSSL=false"
  }
]
```

**Recommendation**: Use AWS Secrets Manager for sensitive data:
```json
"secrets": [
  {
    "name": "DB_PASSWORD",
    "valueFrom": "arn:aws:secretsmanager:region:account:secret:db-password"
  }
]
```

#### 6. Logging Configuration
```json
"logConfiguration": {
  "logDriver": "awslogs",
  "options": {
    "awslogs-group": "/ecs/crm-spring-boot",
    "awslogs-region": "{{AWS_REGION}}",
    "awslogs-stream-prefix": "ecs"
  }
}
```

---

## ECS Service Configuration

### Service Definition Components

#### 1. Basic Configuration
```json
{
  "serviceName": "crm-spring-boot-service",
  "cluster": "{{CLUSTER_NAME}}",
  "taskDefinition": "crm-spring-boot-task",
  "desiredCount": 2,
  "launchType": "FARGATE"
}
```

- **desiredCount**: Number of task instances (2 for high availability)
- **launchType**: FARGATE for serverless deployment

#### 2. Network Configuration
```json
"networkConfiguration": {
  "awsvpcConfiguration": {
    "subnets": ["{{SUBNET_1}}", "{{SUBNET_2}}"],
    "securityGroups": ["{{SECURITY_GROUP}}"],
    "assignPublicIp": "ENABLED"
  }
}
```

- **subnets**: At least 2 subnets in different AZs for HA
- **assignPublicIp**: ENABLED if tasks need internet access (for ECR pulls)

#### 3. Load Balancer Configuration
```json
"loadBalancers": [
  {
    "targetGroupArn": "{{TARGET_GROUP_ARN}}",
    "containerName": "crm-spring-boot",
    "containerPort": 8080
  }
],
"healthCheckGracePeriodSeconds": 300
```

- **healthCheckGracePeriodSeconds**: Time before first health check (300s for JVM startup)

#### 4. Deployment Configuration
```json
"deploymentConfiguration": {
  "maximumPercent": 200,
  "minimumHealthyPercent": 50,
  "deploymentCircuitBreaker": {
    "enable": true,
    "rollback": true
  }
}
```

- **maximumPercent**: 200 allows doubling tasks during deployment (blue-green)
- **minimumHealthyPercent**: 50 ensures at least half the tasks are running
- **deploymentCircuitBreaker**: Automatically rolls back failed deployments

---

## Deploying to AWS ECS Fargate

### Automated Deployment

#### Step 1: Run Deployment Script

**Linux/macOS**:
```bash
chmod +x scripts/deploy-image.sh
./scripts/deploy-image.sh
```

**Windows**:
```cmd
scripts\deploy-image.bat
```

#### Step 2: Provide Deployment Information

The script will prompt for:

1. **AWS Region**: e.g., `us-east-1`
2. **ECS Cluster Name**: e.g., `crm-cluster` (created if doesn't exist)
3. **VPC ID**: Your VPC ID
4. **Subnet IDs**: Comma-separated subnet IDs (at least 2)
5. **Security Group ID**: Security group ID
6. **ECR Image URI**: Full image URI from build step
7. **Load Balancer**: Yes/No (automatically created if yes)

#### Step 3: Monitor Deployment

The script will:
- Create/verify ECS cluster
- Create CloudWatch log group
- Register task definition
- Create/update ECS service
- Create Application Load Balancer (if requested)
- Wait for service to stabilize
- Display deployment summary

### Manual Deployment (Alternative)

```bash
# 1. Register task definition
aws ecs register-task-definition --cli-input-json file://ecs/task-definition.json

# 2. Create service
aws ecs create-service --cli-input-json file://ecs/service-definition.json

# 3. Monitor deployment
aws ecs describe-services --cluster crm-cluster --services crm-spring-boot-service

# 4. Wait for stability
aws ecs wait services-stable --cluster crm-cluster --services crm-spring-boot-service
```

---

## Configuration Management

### Environment-Specific Configuration

#### Using Spring Profiles

1. **Create profile-specific properties**:
   - `application-dev.properties`
   - `application-staging.properties`
   - `application-prod.properties`

2. **Set profile in task definition**:
   ```json
   {
     "name": "SPRING_PROFILES_ACTIVE",
     "value": "prod"
   }
   ```

#### Using AWS Secrets Manager

1. **Create secret**:
   ```bash
   aws secretsmanager create-secret \
     --name crm/db/password \
     --secret-string "mySecurePassword123"
   ```

2. **Update task definition**:
   ```json
   "secrets": [
     {
       "name": "DB_PASSWORD",
       "valueFrom": "arn:aws:secretsmanager:us-east-1:123456789012:secret:crm/db/password"
     }
   ]
   ```

3. **Grant task role permissions**:
   ```json
   {
     "Effect": "Allow",
     "Action": [
       "secretsmanager:GetSecretValue"
     ],
     "Resource": "arn:aws:secretsmanager:us-east-1:123456789012:secret:crm/db/password"
   }
   ```

#### Using AWS Systems Manager Parameter Store

```bash
# Store parameter
aws ssm put-parameter \
  --name /crm/db/url \
  --value "jdbc:mysql://prod-db.example.com:3306/crm" \
  --type String

# Reference in task definition
"secrets": [
  {
    "name": "DB_URL",
    "valueFrom": "arn:aws:ssm:us-east-1:123456789012:parameter/crm/db/url"
  }
]
```

---

## Monitoring and Logging

### CloudWatch Logs

#### View Logs

```bash
# Tail logs in real-time
aws logs tail /ecs/crm-spring-boot --follow --region us-east-1

# Filter logs
aws logs filter-log-events \
  --log-group-name /ecs/crm-spring-boot \
  --filter-pattern "ERROR" \
  --region us-east-1

# View specific log stream
aws logs get-log-events \
  --log-group-name /ecs/crm-spring-boot \
  --log-stream-name ecs/crm-spring-boot/<task-id> \
  --region us-east-1
```

#### CloudWatch Insights Queries

```sql
# Count errors by hour
fields @timestamp, @message
| filter @message like /ERROR/
| stats count() by bin(@timestamp, 1h)

# Find slow requests
fields @timestamp, @message
| filter @message like /duration/
| parse @message /duration: (?<duration>\d+)ms/
| filter duration > 1000
| sort duration desc

# Memory usage over time
fields @timestamp, @message
| filter @message like /heap/
| parse @message /heap: (?<used>\d+)MB \/ (?<max>\d+)MB/
| stats avg(used), avg(max) by bin(@timestamp, 5m)
```

### CloudWatch Metrics

#### ECS Service Metrics

- **CPUUtilization**: CPU usage percentage
- **MemoryUtilization**: Memory usage percentage
- **TargetResponseTime**: Response time from target group
- **HealthyHostCount**: Number of healthy targets
- **UnhealthyHostCount**: Number of unhealthy targets

#### Create Alarms

```bash
# High CPU alarm
aws cloudwatch put-metric-alarm \
  --alarm-name crm-high-cpu \
  --alarm-description "Alert when CPU exceeds 80%" \
  --metric-name CPUUtilization \
  --namespace AWS/ECS \
  --statistic Average \
  --period 300 \
  --threshold 80 \
  --comparison-operator GreaterThanThreshold \
  --evaluation-periods 2 \
  --dimensions Name=ServiceName,Value=crm-spring-boot-service Name=ClusterName,Value=crm-cluster

# High memory alarm
aws cloudwatch put-metric-alarm \
  --alarm-name crm-high-memory \
  --alarm-description "Alert when memory exceeds 85%" \
  --metric-name MemoryUtilization \
  --namespace AWS/ECS \
  --statistic Average \
  --period 300 \
  --threshold 85 \
  --comparison-operator GreaterThanThreshold \
  --evaluation-periods 2 \
  --dimensions Name=ServiceName,Value=crm-spring-boot-service Name=ClusterName,Value=crm-cluster
```

### Application Performance Monitoring

#### Spring Boot Actuator Endpoints

- **Health**: `/appinfo/health`
- **Info**: `/appinfo/info`
- **Metrics**: `/appinfo/metrics` (if enabled)

#### Custom JVM Metrics

Add to `application.properties`:
```properties
# Enable metrics export to CloudWatch
management.metrics.export.cloudwatch.enabled=true
management.metrics.export.cloudwatch.namespace=CRM-Application
management.metrics.export.cloudwatch.step=1m
```

Add CloudWatch SDK dependency to `pom.xml`:
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-cloudwatch2</artifactId>
</dependency>
```

---

## Troubleshooting

### Common Issues

#### 1. Task Fails to Start

**Symptoms**: Tasks continuously starting and stopping

**Causes**:
- Invalid CPU/memory combination
- Image pull errors (ECR permissions)
- Application startup failures

**Solution**:
```bash
# Check task stopped reason
aws ecs describe-tasks \
  --cluster crm-cluster \
  --tasks <task-arn> \
  --query 'tasks[0].stoppedReason' \
  --output text

# Check CloudWatch logs
aws logs tail /ecs/crm-spring-boot --follow

# Verify ECR permissions
aws ecr get-authorization-token
```

#### 2. Health Check Failures

**Symptoms**: Tasks marked unhealthy by ALB

**Causes**:
- Application not responding on health check endpoint
- JVM startup taking longer than grace period
- Database connection issues

**Solution**:
```bash
# Test health endpoint from task
aws ecs execute-command \
  --cluster crm-cluster \
  --task <task-id> \
  --container crm-spring-boot \
  --interactive \
  --command "/bin/sh"

# Inside container:
curl http://localhost:8080/appinfo/health

# Increase health check grace period in service definition
"healthCheckGracePeriodSeconds": 600
```

#### 3. Database Connection Errors

**Symptoms**: Application logs show connection refused or timeout

**Causes**:
- Security group not allowing traffic
- Incorrect database endpoint
- Database credentials invalid

**Solution**:
```bash
# Test database connectivity
aws ecs execute-command \
  --cluster crm-cluster \
  --task <task-id> \
  --container crm-spring-boot \
  --interactive \
  --command "/bin/sh"

# Inside container:
telnet <db-host> 3306

# Check security group rules
aws ec2 describe-security-groups --group-ids <sg-id>

# Verify RDS endpoint
aws rds describe-db-instances \
  --db-instance-identifier crm-mysql \
  --query "DBInstances[0].Endpoint"
```

#### 4. Out of Memory Errors

**Symptoms**: Tasks restarting with OOMKilled status

**Causes**:
- JVM heap size exceeds container memory
- Memory leak in application
- Insufficient memory allocation

**Solution**:
```bash
# Increase task memory in task definition
"memory": "2048"  # Increase from 1024 to 2048

# Adjust JVM heap size
"JAVA_OPTS": "-Xmx1536m -Xms512m -XX:MaxRAMPercentage=75.0"

# Monitor memory usage
aws cloudwatch get-metric-statistics \
  --namespace AWS/ECS \
  --metric-name MemoryUtilization \
  --dimensions Name=ServiceName,Value=crm-spring-boot-service \
  --start-time 2024-01-01T00:00:00Z \
  --end-time 2024-01-01T23:59:59Z \
  --period 300 \
  --statistics Average
```

#### 5. Service Not Scaling

**Symptoms**: Service stuck at desired count despite load

**Causes**:
- Auto-scaling not configured
- Service reaching task limit
- Insufficient capacity in subnets

**Solution**:
```bash
# Check service events
aws ecs describe-services \
  --cluster crm-cluster \
  --services crm-spring-boot-service \
  --query 'services[0].events[0:10]'

# Configure auto-scaling (see Scaling section)
```

---

## Security Best Practices

### 1. Use Secrets Manager for Sensitive Data

**Never** store passwords or API keys in environment variables or task definitions.

```bash
# Create secrets
aws secretsmanager create-secret \
  --name crm/database/credentials \
  --secret-string '{
    "username": "admin",
    "password": "SecurePassword123!",
    "host": "crm-db.cluster-xxx.us-east-1.rds.amazonaws.com",
    "port": 3306,
    "database": "crm"
  }'

# Reference in task definition
"secrets": [
  {
    "name": "DB_USERNAME",
    "valueFrom": "arn:aws:secretsmanager:region:account:secret:crm/database/credentials:username::"
  },
  {
    "name": "DB_PASSWORD",
    "valueFrom": "arn:aws:secretsmanager:region:account:secret:crm/database/credentials:password::"
  }
]
```

### 2. Enable VPC Flow Logs

```bash
aws ec2 create-flow-logs \
  --resource-type VPC \
  --resource-ids <vpc-id> \
  --traffic-type ALL \
  --log-destination-type cloud-watch-logs \
  --log-group-name /aws/vpc/flowlogs
```

### 3. Use Private Subnets with NAT Gateway

```bash
# Create NAT Gateway
aws ec2 create-nat-gateway \
  --subnet-id <public-subnet-id> \
  --allocation-id <eip-allocation-id>

# Update private subnet route table
aws ec2 create-route \
  --route-table-id <private-rt-id> \
  --destination-cidr-block 0.0.0.0/0 \
  --nat-gateway-id <nat-gw-id>

# Update service definition
"networkConfiguration": {
  "awsvpcConfiguration": {
    "subnets": ["<private-subnet-1>", "<private-subnet-2>"],
    "securityGroups": ["<sg-id>"],
    "assignPublicIp": "DISABLED"  # Use NAT for outbound
  }
}
```

### 4. Implement Least Privilege IAM Policies

**Task Execution Role** (minimal permissions):
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ecr:GetAuthorizationToken",
        "ecr:BatchCheckLayerAvailability",
        "ecr:GetDownloadUrlForLayer",
        "ecr:BatchGetImage"
      ],
      "Resource": "*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Resource": "arn:aws:logs:*:*:log-group:/ecs/crm-spring-boot:*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "secretsmanager:GetSecretValue"
      ],
      "Resource": "arn:aws:secretsmanager:*:*:secret:crm/*"
    }
  ]
}
```

**Task Role** (application permissions):
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:GetObject"
      ],
      "Resource": "arn:aws:s3:::crm-documents/*"
    }
  ]
}
```

### 5. Enable Container Insights

```bash
# Enable at cluster level
aws ecs put-account-setting \
  --name containerInsights \
  --value enabled

# Update cluster
aws ecs update-cluster-settings \
  --cluster crm-cluster \
  --settings name=containerInsights,value=enabled
```

### 6. Scan Images for Vulnerabilities

```bash
# Enable ECR image scanning
aws ecr put-image-scanning-configuration \
  --repository-name crm-spring-boot \
  --image-scanning-configuration scanOnPush=true

# View scan results
aws ecr describe-image-scan-findings \
  --repository-name crm-spring-boot \
  --image-id imageTag=latest
```

---

## Scaling and Performance

### 1. Configure Service Auto Scaling

#### CPU-Based Scaling

```bash
# Register scalable target
aws application-autoscaling register-scalable-target \
  --service-namespace ecs \
  --scalable-dimension ecs:service:DesiredCount \
  --resource-id service/crm-cluster/crm-spring-boot-service \
  --min-capacity 2 \
  --max-capacity 10

# Create scaling policy
aws application-autoscaling put-scaling-policy \
  --service-namespace ecs \
  --scalable-dimension ecs:service:DesiredCount \
  --resource-id service/crm-cluster/crm-spring-boot-service \
  --policy-name cpu-scaling-policy \
  --policy-type TargetTrackingScaling \
  --target-tracking-scaling-policy-configuration '{
    "TargetValue": 70.0,
    "PredefinedMetricSpecification": {
      "PredefinedMetricType": "ECSServiceAverageCPUUtilization"
    },
    "ScaleInCooldown": 300,
    "ScaleOutCooldown": 60
  }'
```

#### Memory-Based Scaling

```bash
aws application-autoscaling put-scaling-policy \
  --service-namespace ecs \
  --scalable-dimension ecs:service:DesiredCount \
  --resource-id service/crm-cluster/crm-spring-boot-service \
  --policy-name memory-scaling-policy \
  --policy-type TargetTrackingScaling \
  --target-tracking-scaling-policy-configuration '{
    "TargetValue": 80.0,
    "PredefinedMetricSpecification": {
      "PredefinedMetricType": "ECSServiceAverageMemoryUtilization"
    },
    "ScaleInCooldown": 300,
    "ScaleOutCooldown": 60
  }'
```

#### Request Count-Based Scaling (ALB)

```bash
aws application-autoscaling put-scaling-policy \
  --service-namespace ecs \
  --scalable-dimension ecs:service:DesiredCount \
  --resource-id service/crm-cluster/crm-spring-boot-service \
  --policy-name request-count-scaling-policy \
  --policy-type TargetTrackingScaling \
  --target-tracking-scaling-policy-configuration '{
    "TargetValue": 1000.0,
    "PredefinedMetricSpecification": {
      "PredefinedMetricType": "ALBRequestCountPerTarget",
      "ResourceLabel": "app/crm-spring-boot-alb/<id>/targetgroup/crm-spring-boot-tg/<id>"
    },
    "ScaleInCooldown": 300,
    "ScaleOutCooldown": 60
  }'
```

### 2. JVM Performance Tuning

**Optimize JVM flags in task definition**:

```json
{
  "name": "JAVA_OPTS",
  "value": "-Xmx1536m -Xms512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:ParallelGCThreads=2 -XX:ConcGCThreads=1 -XX:InitiatingHeapOccupancyPercent=70 -Djava.security.egd=file:/dev/./urandom -Dspring.backgroundpreinitializer.ignore=true"
}
```

**JVM Flag Explanation**:
- `-Xmx1536m`: Maximum heap size (75% of 2GB container memory)
- `-Xms512m`: Initial heap size
- `-XX:+UseContainerSupport`: Enable container awareness
- `-XX:MaxRAMPercentage=75.0`: Use 75% of container memory
- `-XX:+UseG1GC`: Use G1 garbage collector (better for containers)
- `-XX:MaxGCPauseMillis=200`: Target GC pause time
- `-XX:InitiatingHeapOccupancyPercent=70`: Start concurrent GC at 70% heap
- `-Djava.security.egd=file:/dev/./urandom`: Faster random number generation

### 3. Database Connection Pooling

Add to `application.properties`:

```properties
# HikariCP configuration (Spring Boot default)
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.connection-test-query=SELECT 1
```

### 4. Enable HTTP/2 and Compression

Add to `application.properties`:

```properties
# Enable HTTP/2
server.http2.enabled=true

# Enable compression
server.compression.enabled=true
server.compression.mime-types=application/json,application/xml,text/html,text/xml,text/plain,application/javascript,text/css
server.compression.min-response-size=1024
```

### 5. Implement Caching

Add Spring Cache dependency to `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

Enable caching in application:

```java
@SpringBootApplication
@EnableCaching
public class CrmApplication {
    // ...
}

@Service
public class CustomerService {
    @Cacheable("customers")
    public Customer findById(Long id) {
        // ...
    }
}
```

---

## Blue-Green Deployment

### Using ECS Circuit Breaker

Already enabled in service definition:

```json
"deploymentConfiguration": {
  "deploymentCircuitBreaker": {
    "enable": true,
    "rollback": true
  }
}
```

### Manual Blue-Green Deployment

```bash
# 1. Create new task definition version
aws ecs register-task-definition --cli-input-json file://ecs/task-definition.json

# 2. Update service with new task definition
aws ecs update-service \
  --cluster crm-cluster \
  --service crm-spring-boot-service \
  --task-definition crm-spring-boot-task:2 \
  --deployment-configuration "maximumPercent=200,minimumHealthyPercent=100"

# 3. Monitor deployment
aws ecs describe-services \
  --cluster crm-cluster \
  --services crm-spring-boot-service \
  --query 'services[0].deployments'

# 4. Rollback if needed
aws ecs update-service \
  --cluster crm-cluster \
  --service crm-spring-boot-service \
  --task-definition crm-spring-boot-task:1
```

---

## Backup and Disaster Recovery

### 1. RDS Automated Backups

```bash
# Enable automated backups (already configured during creation)
aws rds modify-db-instance \
  --db-instance-identifier crm-mysql \
  --backup-retention-period 7 \
  --preferred-backup-window "03:00-04:00"

# Create manual snapshot
aws rds create-db-snapshot \
  --db-instance-identifier crm-mysql \
  --db-snapshot-identifier crm-mysql-snapshot-$(date +%Y%m%d)
```

### 2. Multi-Region Deployment

```bash
# Enable ECR replication
aws ecr put-replication-configuration \
  --replication-configuration '{
    "rules": [
      {
        "destinations": [
          {
            "region": "us-west-2",
            "registryId": "123456789012"
          }
        ]
      }
    ]
  }'

# Deploy to secondary region
aws ecs create-cluster --cluster-name crm-cluster --region us-west-2
# ... repeat deployment steps in us-west-2
```

### 3. Backup PDF Output Files to S3

```bash
# Create S3 bucket
aws s3api create-bucket \
  --bucket crm-pdf-backups \
  --region us-east-1 \
  --create-bucket-configuration LocationConstraint=us-east-1

# Enable versioning
aws s3api put-bucket-versioning \
  --bucket crm-pdf-backups \
  --versioning-configuration Status=Enabled

# Configure lifecycle policy
aws s3api put-bucket-lifecycle-configuration \
  --bucket crm-pdf-backups \
  --lifecycle-configuration file://s3-lifecycle.json
```

---

## Cost Optimization

### 1. Right-Size Resources

**Monitor and adjust CPU/memory**:

```bash
# Get average utilization
aws cloudwatch get-metric-statistics \
  --namespace AWS/ECS \
  --metric-name CPUUtilization \
  --dimensions Name=ServiceName,Value=crm-spring-boot-service \
  --start-time 2024-01-01T00:00:00Z \
  --end-time 2024-01-07T23:59:59Z \
  --period 86400 \
  --statistics Average
```

If average CPU < 30%, consider downsizing:
- From CPU "512", Memory "1024"
- To CPU "256", Memory "512"

### 2. Use Spot Capacity Providers (Not Recommended for Production)

```bash
# Create Fargate Spot capacity provider
aws ecs put-cluster-capacity-providers \
  --cluster crm-cluster \
  --capacity-providers FARGATE FARGATE_SPOT \
  --default-capacity-provider-strategy \
    capacityProvider=FARGATE,weight=1,base=2 \
    capacityProvider=FARGATE_SPOT,weight=4
```

### 3. Schedule Scaling for Non-Production

```bash
# Scale down at night (8 PM - 6 AM)
aws application-autoscaling put-scheduled-action \
  --service-namespace ecs \
  --resource-id service/crm-cluster/crm-spring-boot-service \
  --scheduled-action-name scale-down-night \
  --schedule "cron(0 20 * * ? *)" \
  --scalable-target-action MinCapacity=1,MaxCapacity=2

# Scale up in morning (6 AM)
aws application-autoscaling put-scheduled-action \
  --service-namespace ecs \
  --resource-id service/crm-cluster/crm-spring-boot-service \
  --scheduled-action-name scale-up-morning \
  --schedule "cron(0 6 * * ? *)" \
  --scalable-target-action MinCapacity=2,MaxCapacity=10
```

### 4. Enable S3 Intelligent-Tiering

```bash
aws s3api put-bucket-intelligent-tiering-configuration \
  --bucket crm-pdf-backups \
  --id EntireBucket \
  --intelligent-tiering-configuration '{
    "Id": "EntireBucket",
    "Status": "Enabled",
    "Tierings": [
      {
        "Days": 90,
        "AccessTier": "ARCHIVE_ACCESS"
      },
      {
        "Days": 180,
        "AccessTier": "DEEP_ARCHIVE_ACCESS"
      }
    ]
  }'
```

---

## Cleanup

### Delete All Resources

```bash
# 1. Delete ECS service
aws ecs update-service \
  --cluster crm-cluster \
  --service crm-spring-boot-service \
  --desired-count 0

aws ecs delete-service \
  --cluster crm-cluster \
  --service crm-spring-boot-service \
  --force

# 2. Delete ECS cluster
aws ecs delete-cluster --cluster crm-cluster

# 3. Delete load balancer
aws elbv2 delete-load-balancer --load-balancer-arn <alb-arn>
aws elbv2 delete-target-group --target-group-arn <tg-arn>

# 4. Delete ECR repository
aws ecr delete-repository --repository-name crm-spring-boot --force

# 5. Delete RDS instance
aws rds delete-db-instance \
  --db-instance-identifier crm-mysql \
  --skip-final-snapshot

# 6. Delete CloudWatch log group
aws logs delete-log-group --log-group-name /ecs/crm-spring-boot

# 7. Delete IAM roles (if created for this project only)
aws iam detach-role-policy --role-name ecsTaskExecutionRole --policy-arn arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy
aws iam delete-role --role-name ecsTaskExecutionRole
aws iam delete-role --role-name ecsTaskRole

# 8. Delete VPC resources (if created for this project)
aws ec2 delete-security-group --group-id <sg-id>
aws ec2 delete-subnet --subnet-id <subnet-id>
aws ec2 detach-internet-gateway --vpc-id <vpc-id> --internet-gateway-id <igw-id>
aws ec2 delete-internet-gateway --internet-gateway-id <igw-id>
aws ec2 delete-vpc --vpc-id <vpc-id>
```

---

## Additional Resources

### AWS Documentation

- [ECS Developer Guide](https://docs.aws.amazon.com/ecs/)
- [Fargate User Guide](https://docs.aws.amazon.com/AmazonECS/latest/userguide/what-is-fargate.html)
- [ECR User Guide](https://docs.aws.amazon.com/ecr/)
- [CloudWatch Logs](https://docs.aws.amazon.com/cloudwatch/)

### Spring Boot Documentation

- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/1.5.x/reference/htmlsingle/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Security](https://docs.spring.io/spring-security/site/docs/current/reference/html5/)
- [Thymeleaf](https://www.thymeleaf.org/documentation.html)

### Docker Documentation

- [Dockerfile Best Practices](https://docs.docker.com/develop/develop-images/dockerfile_best-practices/)
- [Docker Compose](https://docs.docker.com/compose/)

---

## Support and Troubleshooting

For issues with:

1. **Application Code**: Review CloudWatch logs and Spring Boot Actuator endpoints
2. **Infrastructure**: Check AWS service health dashboard and CloudWatch metrics
3. **Deployment**: Review ECS service events and task stopped reasons
4. **Performance**: Use CloudWatch Insights and Container Insights

**Contact**:
- Application Team: [team-email]
- AWS Support: https://console.aws.amazon.com/support/

---

**Document Version**: 1.0
**Last Updated**: 2024-01-22
**Maintained By**: DevOps Team
