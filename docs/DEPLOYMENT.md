# CRM Application - AWS ECS Fargate Deployment Guide

## Table of Contents
1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Local Development Setup](#local-development-setup)
4. [AWS ECS Fargate Prerequisites](#aws-ecs-fargate-prerequisites)
5. [Building and Pushing Docker Image](#building-and-pushing-docker-image)
6. [ECS Task Definition Explained](#ecs-task-definition-explained)
7. [ECS Service Configuration](#ecs-service-configuration)
8. [Deployment to AWS ECS Fargate](#deployment-to-aws-ecs-fargate)
9. [Monitoring and Logging](#monitoring-and-logging)
10. [Troubleshooting](#troubleshooting)
11. [Scaling and Management](#scaling-and-management)
12. [Security Considerations](#security-considerations)

---

## Overview

This guide provides comprehensive instructions for deploying the CRM Spring Boot application to AWS ECS Fargate. The application is containerized using Docker and deployed as a serverless container service on AWS.

**Application Details:**
- **Framework**: Spring Boot 1.5.10
- **Java Version**: 8
- **Build Tool**: Maven
- **Application Type**: Web Application with Thymeleaf
- **Default Port**: 8080
- **Health Check**: `/appinfo/health`
- **Database**: MySQL (external)

---

## Prerequisites

### Required Software
- **Docker**: Version 20.10 or higher
- **AWS CLI**: Version 2.x
- **Java**: JDK 8 (for local development)
- **Maven**: Version 3.6+ (for local builds)
- **Git**: For version control

### AWS Account Requirements
- Active AWS account with appropriate permissions
- IAM user with permissions for:
  - ECS (Full access)
  - ECR (Full access)
  - CloudWatch Logs (Write access)
  - VPC (Read access)
  - IAM (Role creation/management)
  - Elastic Load Balancing (if using ALB)

### Install AWS CLI
```bash
# Linux/macOS
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

# Windows
# Download and run the AWS CLI MSI installer from:
# https://awscli.amazonaws.com/AWSCLIV2.msi

# Verify installation
aws --version

# Configure AWS CLI
aws configure
```

---

## Local Development Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd crm-045
```

### 2. Configure Application Properties
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/crm?useSSL=false
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Build the Application Locally
```bash
# Using Maven
mvn clean package -DskipTests

# Run locally
java -jar target/crm-0.0.1-SNAPSHOT.jar
```

### 4. Test Locally with Docker Compose
```bash
# Build and run with Docker Compose
docker-compose up --build

# Access the application
# http://localhost:8080

# Stop the application
docker-compose down
```

---

## AWS ECS Fargate Prerequisites

### 1. VPC and Networking Setup

**Create VPC (if not exists):**
```bash
# Create VPC
aws ec2 create-vpc \
  --cidr-block 10.0.0.0/16 \
  --tag-specifications 'ResourceType=vpc,Tags=[{Key=Name,Value=crm-vpc}]'

# Note the VPC ID from the output
VPC_ID=<your-vpc-id>

# Enable DNS hostnames
aws ec2 modify-vpc-attribute \
  --vpc-id $VPC_ID \
  --enable-dns-hostnames
```

**Create Subnets:**
```bash
# Create public subnet 1 (us-east-1a)
aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block 10.0.1.0/24 \
  --availability-zone us-east-1a \
  --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=crm-public-subnet-1}]'

# Create public subnet 2 (us-east-1b)
aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block 10.0.2.0/24 \
  --availability-zone us-east-1b \
  --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=crm-public-subnet-2}]'

# Note the Subnet IDs
SUBNET_1=<subnet-1-id>
SUBNET_2=<subnet-2-id>
```

**Create Internet Gateway:**
```bash
# Create Internet Gateway
aws ec2 create-internet-gateway \
  --tag-specifications 'ResourceType=internet-gateway,Tags=[{Key=Name,Value=crm-igw}]'

# Note the IGW ID
IGW_ID=<igw-id>

# Attach to VPC
aws ec2 attach-internet-gateway \
  --vpc-id $VPC_ID \
  --internet-gateway-id $IGW_ID

# Create route table
aws ec2 create-route-table \
  --vpc-id $VPC_ID \
  --tag-specifications 'ResourceType=route-table,Tags=[{Key=Name,Value=crm-public-rt}]'

# Note the Route Table ID
RT_ID=<route-table-id>

# Add route to Internet Gateway
aws ec2 create-route \
  --route-table-id $RT_ID \
  --destination-cidr-block 0.0.0.0/0 \
  --gateway-id $IGW_ID

# Associate subnets with route table
aws ec2 associate-route-table --subnet-id $SUBNET_1 --route-table-id $RT_ID
aws ec2 associate-route-table --subnet-id $SUBNET_2 --route-table-id $RT_ID
```

### 2. Security Group Configuration

**Create Security Group:**
```bash
# Create security group
aws ec2 create-security-group \
  --group-name crm-ecs-sg \
  --description "Security group for CRM ECS tasks" \
  --vpc-id $VPC_ID

# Note the Security Group ID
SG_ID=<security-group-id>

# Allow inbound HTTP traffic (port 8080)
aws ec2 authorize-security-group-ingress \
  --group-id $SG_ID \
  --protocol tcp \
  --port 8080 \
  --cidr 0.0.0.0/0

# Allow inbound HTTP traffic (port 80) for ALB
aws ec2 authorize-security-group-ingress \
  --group-id $SG_ID \
  --protocol tcp \
  --port 80 \
  --cidr 0.0.0.0/0

# Allow outbound traffic (all)
aws ec2 authorize-security-group-egress \
  --group-id $SG_ID \
  --protocol -1 \
  --cidr 0.0.0.0/0
```

### 3. IAM Roles Setup

**Create ECS Task Execution Role:**
```bash
# Create trust policy file
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

# Attach AWS managed policy
aws iam attach-role-policy \
  --role-name ecsTaskExecutionRole \
  --policy-arn arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy
```

**Create ECS Task Role (for application permissions):**
```bash
# Create task role
aws iam create-role \
  --role-name ecsTaskRole \
  --assume-role-policy-document file://ecs-task-execution-trust-policy.json

# Attach policies as needed (e.g., S3, DynamoDB, etc.)
# Example: S3 read access
aws iam attach-role-policy \
  --role-name ecsTaskRole \
  --policy-arn arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess
```

### 4. CloudWatch Logs Setup

**Create Log Group:**
```bash
aws logs create-log-group \
  --log-group-name /ecs/crm-045 \
  --region us-east-1
```

### 5. Database Setup (RDS MySQL)

**Create RDS MySQL Instance:**
```bash
# Create DB subnet group
aws rds create-db-subnet-group \
  --db-subnet-group-name crm-db-subnet-group \
  --db-subnet-group-description "Subnet group for CRM database" \
  --subnet-ids $SUBNET_1 $SUBNET_2

# Create RDS instance
aws rds create-db-instance \
  --db-instance-identifier crm-mysql-db \
  --db-instance-class db.t3.micro \
  --engine mysql \
  --engine-version 8.0 \
  --master-username admin \
  --master-user-password <your-secure-password> \
  --allocated-storage 20 \
  --vpc-security-group-ids $SG_ID \
  --db-subnet-group-name crm-db-subnet-group \
  --backup-retention-period 7 \
  --publicly-accessible

# Wait for DB to be available (takes 5-10 minutes)
aws rds wait db-instance-available --db-instance-identifier crm-mysql-db

# Get DB endpoint
aws rds describe-db-instances \
  --db-instance-identifier crm-mysql-db \
  --query 'DBInstances[0].Endpoint.Address' \
  --output text
```

---

## Building and Pushing Docker Image

### Option 1: Using Build Script (Recommended)

**Linux/macOS:**
```bash
cd scripts
chmod +x build-push.sh
./build-push.sh
```

**Windows:**
```cmd
cd scripts
build-push.bat
```

The script will:
1. Prompt for registry selection (AWS ECR or Docker Hub)
2. Prompt for registry credentials
3. Build the Docker image
4. Push to the selected registry
5. Automatically create ECR repository if it doesn't exist

### Option 2: Manual Build and Push

**Build Docker Image:**
```bash
docker build -t crm-045:latest .
```

**Push to AWS ECR:**
```bash
# Get AWS Account ID
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
AWS_REGION=us-east-1

# Create ECR repository
aws ecr create-repository \
  --repository-name crm-045 \
  --region $AWS_REGION

# Authenticate Docker to ECR
aws ecr get-login-password --region $AWS_REGION | \
  docker login --username AWS --password-stdin \
  $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

# Tag image
docker tag crm-045:latest \
  $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/crm-045:latest

# Push image
docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/crm-045:latest
```

---

## ECS Task Definition Explained

The task definition (`ecs/task-definition.json`) defines how your container runs on ECS Fargate.

### Key Components:

**1. Fargate Configuration:**
```json
{
  "requiresCompatibilities": ["FARGATE"],
  "networkMode": "awsvpc",
  "cpu": "512",
  "memory": "1024"
}
```

**Valid Fargate CPU/Memory Combinations:**
- CPU: 256 (.25 vCPU) → Memory: 512, 1024, 2048 MB
- CPU: 512 (.5 vCPU) → Memory: 1024, 2048, 3072, 4096 MB
- CPU: 1024 (1 vCPU) → Memory: 2048-8192 MB (1024 MB increments)
- CPU: 2048 (2 vCPU) → Memory: 4096-16384 MB (1024 MB increments)
- CPU: 4096 (4 vCPU) → Memory: 8192-30720 MB (1024 MB increments)

**2. IAM Roles:**
```json
{
  "executionRoleArn": "arn:aws:iam::{{ACCOUNT_ID}}:role/ecsTaskExecutionRole",
  "taskRoleArn": "arn:aws:iam::{{ACCOUNT_ID}}:role/ecsTaskRole"
}
```

- **executionRoleArn**: Allows ECS to pull images and write logs
- **taskRoleArn**: Grants permissions to the application (S3, DynamoDB, etc.)

**3. Container Definition:**
```json
{
  "name": "crm-045",
  "image": "{{IMAGE_URI}}",
  "essential": true,
  "portMappings": [{"containerPort": 8080, "protocol": "tcp"}],
  "environment": [...],
  "logConfiguration": {...}
}
```

**4. Environment Variables:**
- `JAVA_OPTS`: JVM memory settings
- `SPRING_PROFILES_ACTIVE`: Spring Boot profile
- `SPRING_DATASOURCE_*`: Database connection settings
- `MANAGEMENT_*`: Actuator configuration

**5. Logging Configuration:**
```json
{
  "logConfiguration": {
    "logDriver": "awslogs",
    "options": {
      "awslogs-group": "/ecs/crm-045",
      "awslogs-region": "{{AWS_REGION}}",
      "awslogs-stream-prefix": "ecs"
    }
  }
}
```

---

## ECS Service Configuration

The service definition (`ecs/service-definition.json`) manages the deployment and scaling of your tasks.

### Key Components:

**1. Service Configuration:**
```json
{
  "serviceName": "crm-045-service",
  "cluster": "{{CLUSTER_NAME}}",
  "taskDefinition": "crm-045-task",
  "desiredCount": 2,
  "launchType": "FARGATE"
}
```

**2. Network Configuration:**
```json
{
  "networkConfiguration": {
    "awsvpcConfiguration": {
      "subnets": ["{{SUBNET_1}}", "{{SUBNET_2}}"],
      "securityGroups": ["{{SECURITY_GROUP}}"],
      "assignPublicIp": "ENABLED"
    }
  }
}
```

**3. Deployment Configuration:**
```json
{
  "deploymentConfiguration": {
    "maximumPercent": 200,
    "minimumHealthyPercent": 50,
    "deploymentCircuitBreaker": {
      "enable": true,
      "rollback": true
    }
  }
}
```

**4. Load Balancer (Optional):**
```json
{
  "loadBalancers": [
    {
      "targetGroupArn": "{{TARGET_GROUP_ARN}}",
      "containerName": "crm-045",
      "containerPort": 8080
    }
  ],
  "healthCheckGracePeriodSeconds": 300
}
```

---

## Deployment to AWS ECS Fargate

### Using Deployment Script (Recommended)

**Linux/macOS:**
```bash
cd scripts
chmod +x deploy-image.sh
./deploy-image.sh
```

**Windows:**
```cmd
cd scripts
deploy-image.bat
```

The script will:
1. Prompt for AWS region and cluster name
2. Create ECS cluster if it doesn't exist
3. Prompt for network configuration (VPC, subnets, security group)
4. Prompt for Docker image URI
5. Prompt for database configuration
6. Ask if you need a load balancer
7. Create Application Load Balancer and Target Group (if requested)
8. Create CloudWatch log group
9. Register task definition
10. Create or update ECS service
11. Wait for service stability
12. Display deployment status and access URLs

### Manual Deployment Steps

**1. Register Task Definition:**
```bash
# Update placeholders in task-definition.json
# Then register:
aws ecs register-task-definition \
  --cli-input-json file://ecs/task-definition.json \
  --region us-east-1
```

**2. Create ECS Cluster:**
```bash
aws ecs create-cluster \
  --cluster-name crm-cluster \
  --region us-east-1
```

**3. Create ECS Service:**
```bash
# Update placeholders in service-definition.json
# Then create service:
aws ecs create-service \
  --cli-input-json file://ecs/service-definition.json \
  --region us-east-1
```

**4. Wait for Service Stability:**
```bash
aws ecs wait services-stable \
  --cluster crm-cluster \
  --services crm-045-service \
  --region us-east-1
```

---

## Monitoring and Logging

### CloudWatch Logs

**View Logs:**
```bash
# Tail logs in real-time
aws logs tail /ecs/crm-045 --follow --region us-east-1

# View recent logs
aws logs tail /ecs/crm-045 --since 1h --region us-east-1

# Filter logs
aws logs tail /ecs/crm-045 --follow --filter-pattern "ERROR" --region us-east-1
```

**CloudWatch Console:**
1. Navigate to CloudWatch → Log groups
2. Select `/ecs/crm-045`
3. View log streams for each task

### ECS Service Monitoring

**Check Service Status:**
```bash
aws ecs describe-services \
  --cluster crm-cluster \
  --services crm-045-service \
  --region us-east-1
```

**List Running Tasks:**
```bash
aws ecs list-tasks \
  --cluster crm-cluster \
  --service-name crm-045-service \
  --region us-east-1
```

**Describe Task:**
```bash
aws ecs describe-tasks \
  --cluster crm-cluster \
  --tasks <task-id> \
  --region us-east-1
```

### Application Health Checks

**Health Endpoint:**
```bash
# If using ALB
curl http://<alb-dns-name>/appinfo/health

# Direct task access (if public IP assigned)
curl http://<task-public-ip>:8080/appinfo/health
```

**Expected Response:**
```json
{
  "status": "UP"
}
```

### CloudWatch Metrics

Monitor these key metrics in CloudWatch:
- **CPUUtilization**: Container CPU usage
- **MemoryUtilization**: Container memory usage
- **TargetResponseTime**: ALB response time
- **HealthyHostCount**: Number of healthy targets
- **UnHealthyHostCount**: Number of unhealthy targets

---

## Troubleshooting

### Common Issues and Solutions

#### 1. Task Fails to Start

**Symptoms:**
- Tasks start and immediately stop
- Status shows "STOPPED"

**Diagnosis:**
```bash
# Get stopped task ID
aws ecs list-tasks \
  --cluster crm-cluster \
  --desired-status STOPPED \
  --region us-east-1

# Describe stopped task
aws ecs describe-tasks \
  --cluster crm-cluster \
  --tasks <task-id> \
  --region us-east-1
```

**Common Causes:**
- **Image pull error**: Check ECR permissions and image URI
- **Invalid CPU/memory combination**: Use valid Fargate combinations
- **Missing IAM role**: Verify executionRoleArn exists
- **Application crash**: Check CloudWatch logs for errors

**Solutions:**
```bash
# Verify image exists in ECR
aws ecr describe-images \
  --repository-name crm-045 \
  --region us-east-1

# Check IAM role
aws iam get-role --role-name ecsTaskExecutionRole

# View task logs
aws logs tail /ecs/crm-045 --since 30m --region us-east-1
```

#### 2. Network Connectivity Issues

**Symptoms:**
- Cannot access application
- Database connection failures

**Diagnosis:**
```bash
# Check security group rules
aws ec2 describe-security-groups \
  --group-ids <security-group-id> \
  --region us-east-1

# Verify subnet configuration
aws ec2 describe-subnets \
  --subnet-ids <subnet-id> \
  --region us-east-1
```

**Solutions:**
- Ensure security group allows inbound traffic on port 8080
- Verify subnets have route to Internet Gateway
- Check database security group allows connections from ECS tasks
- Ensure `assignPublicIp: ENABLED` if accessing from internet

#### 3. Database Connection Failures

**Symptoms:**
- Application logs show database connection errors
- Tasks fail health checks

**Common Causes:**
- Incorrect database endpoint
- Wrong credentials
- Security group blocking connections
- Database not publicly accessible

**Solutions:**
```bash
# Verify database endpoint
aws rds describe-db-instances \
  --db-instance-identifier crm-mysql-db \
  --query 'DBInstances[0].Endpoint' \
  --region us-east-1

# Test connection from ECS task
# Use ECS Exec to connect to running task
aws ecs execute-command \
  --cluster crm-cluster \
  --task <task-id> \
  --container crm-045 \
  --interactive \
  --command "/bin/sh"

# Inside container, test MySQL connection
mysql -h <db-endpoint> -u admin -p
```

#### 4. High Memory Usage / OOM Errors

**Symptoms:**
- Tasks stop with exit code 137
- High memory utilization in CloudWatch

**Solutions:**
```bash
# Increase task memory in task definition
# Update cpu and memory values:
"cpu": "1024",
"memory": "2048"

# Adjust JVM heap size in environment variables:
"JAVA_OPTS": "-Xmx1536m -Xms768m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Register updated task definition
aws ecs register-task-definition \
  --cli-input-json file://ecs/task-definition.json \
  --region us-east-1

# Update service with new task definition
aws ecs update-service \
  --cluster crm-cluster \
  --service crm-045-service \
  --task-definition crm-045-task \
  --region us-east-1
```

#### 5. Load Balancer Health Check Failures

**Symptoms:**
- Targets marked as unhealthy
- Service unable to reach desired count

**Diagnosis:**
```bash
# Check target health
aws elbv2 describe-target-health \
  --target-group-arn <target-group-arn> \
  --region us-east-1
```

**Solutions:**
- Verify health check path is correct (`/appinfo/health`)
- Increase health check grace period (300 seconds for Spring Boot)
- Check application logs for startup errors
- Ensure security group allows ALB to reach container port

#### 6. Service Update Failures

**Symptoms:**
- Service update stuck in progress
- Deployment circuit breaker triggered

**Solutions:**
```bash
# Check service events
aws ecs describe-services \
  --cluster crm-cluster \
  --services crm-045-service \
  --region us-east-1 \
  --query 'services[0].events[0:10]'

# Force new deployment
aws ecs update-service \
  --cluster crm-cluster \
  --service crm-045-service \
  --force-new-deployment \
  --region us-east-1

# If stuck, delete and recreate service
aws ecs delete-service \
  --cluster crm-cluster \
  --service crm-045-service \
  --force \
  --region us-east-1

# Then recreate using deploy script
./scripts/deploy-image.sh
```

---

## Scaling and Management

### Manual Scaling

**Update Desired Count:**
```bash
aws ecs update-service \
  --cluster crm-cluster \
  --service crm-045-service \
  --desired-count 4 \
  --region us-east-1
```

### Auto Scaling

**Create Auto Scaling Target:**
```bash
aws application-autoscaling register-scalable-target \
  --service-namespace ecs \
  --scalable-dimension ecs:service:DesiredCount \
  --resource-id service/crm-cluster/crm-045-service \
  --min-capacity 2 \
  --max-capacity 10 \
  --region us-east-1
```

**Create Scaling Policy (CPU-based):**
```bash
aws application-autoscaling put-scaling-policy \
  --service-namespace ecs \
  --scalable-dimension ecs:service:DesiredCount \
  --resource-id service/crm-cluster/crm-045-service \
  --policy-name cpu-scaling-policy \
  --policy-type TargetTrackingScaling \
  --target-tracking-scaling-policy-configuration file://scaling-policy.json \
  --region us-east-1
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

### Blue/Green Deployments

**Using AWS CodeDeploy:**
```bash
# Create CodeDeploy application
aws deploy create-application \
  --application-name crm-app \
  --compute-platform ECS \
  --region us-east-1

# Create deployment group
aws deploy create-deployment-group \
  --application-name crm-app \
  --deployment-group-name crm-deployment-group \
  --service-role-arn <codedeploy-role-arn> \
  --ecs-services clusterName=crm-cluster,serviceName=crm-045-service \
  --load-balancer-info targetGroupInfoList=[{name=crm-045-tg}] \
  --blue-green-deployment-configuration file://blue-green-config.json \
  --region us-east-1
```

### Rolling Updates

**Update Task Definition:**
```bash
# Register new task definition version
aws ecs register-task-definition \
  --cli-input-json file://ecs/task-definition.json \
  --region us-east-1

# Update service (rolling update)
aws ecs update-service \
  --cluster crm-cluster \
  --service crm-045-service \
  --task-definition crm-045-task:2 \
  --region us-east-1
```

### Rollback

**Rollback to Previous Task Definition:**
```bash
# List task definition revisions
aws ecs list-task-definitions \
  --family-prefix crm-045-task \
  --region us-east-1

# Update service to previous version
aws ecs update-service \
  --cluster crm-cluster \
  --service crm-045-service \
  --task-definition crm-045-task:1 \
  --region us-east-1
```

---

## Security Considerations

### 1. Secrets Management

**Use AWS Secrets Manager:**
```bash
# Create secret for database password
aws secretsmanager create-secret \
  --name crm/db/password \
  --secret-string "your-secure-password" \
  --region us-east-1

# Update task definition to use secrets
# In containerDefinitions:
"secrets": [
  {
    "name": "SPRING_DATASOURCE_PASSWORD",
    "valueFrom": "arn:aws:secretsmanager:us-east-1:123456789:secret:crm/db/password"
  }
]
```

### 2. Network Security

**Best Practices:**
- Use private subnets for ECS tasks
- Place database in private subnet
- Use NAT Gateway for outbound internet access
- Restrict security group rules to minimum required
- Enable VPC Flow Logs for network monitoring

**Private Subnet Configuration:**
```bash
# Create private subnets
aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block 10.0.10.0/24 \
  --availability-zone us-east-1a

# Create NAT Gateway
aws ec2 create-nat-gateway \
  --subnet-id <public-subnet-id> \
  --allocation-id <elastic-ip-allocation-id>

# Update route table for private subnet
aws ec2 create-route \
  --route-table-id <private-rt-id> \
  --destination-cidr-block 0.0.0.0/0 \
  --nat-gateway-id <nat-gateway-id>
```

### 3. IAM Best Practices

**Principle of Least Privilege:**
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:GetObject",
        "s3:PutObject"
      ],
      "Resource": "arn:aws:s3:::crm-bucket/*"
    }
  ]
}
```

### 4. Container Security

**Best Practices:**
- Use official base images (eclipse-temurin)
- Run as non-root user (implemented in Dockerfile)
- Scan images for vulnerabilities
- Keep base images updated
- Use read-only root filesystem where possible

**Scan Image for Vulnerabilities:**
```bash
# Using AWS ECR image scanning
aws ecr start-image-scan \
  --repository-name crm-045 \
  --image-id imageTag=latest \
  --region us-east-1

# Get scan results
aws ecr describe-image-scan-findings \
  --repository-name crm-045 \
  --image-id imageTag=latest \
  --region us-east-1
```

### 5. Logging and Monitoring

**Enable CloudTrail:**
```bash
aws cloudtrail create-trail \
  --name crm-trail \
  --s3-bucket-name crm-cloudtrail-logs \
  --is-multi-region-trail \
  --region us-east-1

aws cloudtrail start-logging \
  --name crm-trail \
  --region us-east-1
```

**Set Up CloudWatch Alarms:**
```bash
# CPU utilization alarm
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
  --dimensions Name=ServiceName,Value=crm-045-service Name=ClusterName,Value=crm-cluster \
  --region us-east-1
```

### 6. Compliance and Auditing

**Enable ECS Container Insights:**
```bash
aws ecs update-cluster-settings \
  --cluster crm-cluster \
  --settings name=containerInsights,value=enabled \
  --region us-east-1
```

---

## Technology-Specific Notes

### Spring Boot Configuration

**1. Profiles:**
The application uses Spring profiles for environment-specific configuration:
- `default`: Local development
- `docker`: Docker/container environment
- `production`: Production deployment

**2. Actuator Endpoints:**
- Health: `/appinfo/health`
- Info: `/appinfo/info`
- Metrics: `/appinfo/metrics`

**3. Database Configuration:**
The application uses MySQL. Ensure the following environment variables are set:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

**4. JVM Tuning:**
Recommended JVM options for containerized Spring Boot:
```bash
JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"
```

### Maven Build Optimization

**1. Dependency Caching:**
The Dockerfile uses multi-stage build with dependency caching:
```dockerfile
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests
```

**2. Build Performance:**
- Use `mvn dependency:go-offline` to download dependencies once
- Skip tests in Docker build: `-DskipTests`
- Use Maven daemon for faster builds locally

---

## Cost Optimization

### 1. Right-Sizing Resources

**Monitor and Adjust:**
```bash
# Check average CPU/memory usage
aws cloudwatch get-metric-statistics \
  --namespace AWS/ECS \
  --metric-name CPUUtilization \
  --dimensions Name=ServiceName,Value=crm-045-service Name=ClusterName,Value=crm-cluster \
  --start-time 2024-01-01T00:00:00Z \
  --end-time 2024-01-07T23:59:59Z \
  --period 3600 \
  --statistics Average \
  --region us-east-1
```

### 2. Use Fargate Spot

**For non-critical workloads:**
```json
{
  "capacityProviderStrategy": [
    {
      "capacityProvider": "FARGATE_SPOT",
      "weight": 1,
      "base": 0
    }
  ]
}
```

### 3. Schedule Scaling

**Scale down during off-hours:**
```bash
# Create scheduled action to scale down at night
aws application-autoscaling put-scheduled-action \
  --service-namespace ecs \
  --scalable-dimension ecs:service:DesiredCount \
  --resource-id service/crm-cluster/crm-045-service \
  --scheduled-action-name scale-down-night \
  --schedule "cron(0 22 * * ? *)" \
  --scalable-target-action MinCapacity=1,MaxCapacity=2 \
  --region us-east-1
```

---

## Additional Resources

### AWS Documentation
- [ECS Fargate Documentation](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/AWS_Fargate.html)
- [ECS Task Definitions](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task_definitions.html)
- [ECS Service Definition](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/service_definition_parameters.html)
- [CloudWatch Logs](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/WhatIsCloudWatchLogs.html)

### Spring Boot Resources
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Spring Boot Production Ready](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html)

### Support
For issues or questions:
1. Check CloudWatch logs: `/ecs/crm-045`
2. Review ECS service events
3. Consult AWS Support
4. Review application logs

---

## Conclusion

This guide provides comprehensive instructions for deploying the CRM Spring Boot application to AWS ECS Fargate. Follow the steps carefully, and refer to the troubleshooting section for common issues.

**Quick Start Summary:**
1. Set up AWS prerequisites (VPC, subnets, security groups, IAM roles)
2. Build and push Docker image: `./scripts/build-push.sh`
3. Deploy to ECS: `./scripts/deploy-image.sh`
4. Monitor via CloudWatch Logs and ECS console
5. Scale as needed using auto-scaling policies

**Next Steps:**
- Set up CI/CD pipeline for automated deployments
- Implement blue/green deployments
- Configure auto-scaling based on metrics
- Set up monitoring and alerting
- Implement backup and disaster recovery

---

**Document Version:** 1.0  
**Last Updated:** 2024  
**Maintained By:** DevOps Team
