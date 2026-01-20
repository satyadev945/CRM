# CRM Container - AWS ECS Fargate Deployment Guide

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Local Development Setup](#local-development-setup)
3. [AWS ECS Fargate Prerequisites](#aws-ecs-fargate-prerequisites)
4. [Building and Pushing Docker Images](#building-and-pushing-docker-images)
5. [ECS Fargate Setup](#ecs-fargate-setup)
6. [Deploying to ECS Fargate](#deploying-to-ecs-fargate)
7. [Configuration Management](#configuration-management)
8. [Monitoring and Logging](#monitoring-and-logging)
9. [Troubleshooting](#troubleshooting)
10. [Security Best Practices](#security-best-practices)
11. [Scaling and Management](#scaling-and-management)

---

## Prerequisites

### System Requirements

- **Docker**: Version 20.10 or higher
- **Docker Compose**: Version 2.0 or higher
- **AWS CLI**: Version 2.x
- **Java**: JDK 8 (for local development)
- **Maven**: 3.6+ (for local builds)
- **Git**: For version control

### AWS Requirements

- AWS Account with appropriate permissions
- IAM user with programmatic access
- AWS CLI configured with credentials

---

## Local Development Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd CRM-Container
```

### 2. Build the Application Locally

```bash
# Using Maven (without wrapper)
mvn clean package -DskipTests

# The JAR file will be created at:
# target/crm-0.0.1-SNAPSHOT.jar
```

### 3. Run with Docker Compose

```bash
# Start the application with MySQL database
docker-compose up -d

# View logs
docker-compose logs -f crm-app

# Stop the application
docker-compose down
```

### 4. Access the Application

- **Application URL**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **Actuator Info**: http://localhost:8080/actuator/info

### 5. Default Login Credentials

Refer to `src/main/resources/data.sql` for default users:
- Admin user credentials are configured in the data initialization script

---

## AWS ECS Fargate Prerequisites

### 1. IAM Roles

#### ecsTaskExecutionRole

Create an IAM role with the following trust policy:

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

Attach the following managed policies:
- `AmazonECSTaskExecutionRolePolicy`
- `CloudWatchLogsFullAccess`

#### ecsTaskRole (Optional)

Create for task-specific permissions (e.g., S3 access, DynamoDB access).

### 2. VPC and Networking

#### VPC Requirements
- VPC with public or private subnets
- At least 2 subnets in different Availability Zones
- Internet Gateway (for public subnets) or NAT Gateway (for private subnets)

#### Security Group Configuration

Create a security group with the following rules:

**Inbound Rules:**
- Type: HTTP
- Protocol: TCP
- Port: 80
- Source: 0.0.0.0/0 (or ALB security group)

- Type: Custom TCP
- Protocol: TCP
- Port: 8080
- Source: ALB security group or VPC CIDR

**Outbound Rules:**
- Type: All traffic
- Protocol: All
- Port: All
- Destination: 0.0.0.0/0

### 3. RDS MySQL Database

#### Create RDS Instance

```bash
aws rds create-db-instance \
  --db-instance-identifier crm-mysql \
  --db-instance-class db.t3.micro \
  --engine mysql \
  --engine-version 5.7.44 \
  --master-username admin \
  --master-user-password YourSecurePassword123! \
  --allocated-storage 20 \
  --vpc-security-group-ids sg-xxxxxxxxx \
  --db-subnet-group-name your-db-subnet-group \
  --publicly-accessible false \
  --region us-east-1
```

#### Get RDS Endpoint

```bash
aws rds describe-db-instances \
  --db-instance-identifier crm-mysql \
  --query 'DBInstances[0].Endpoint.Address' \
  --output text
```

Update the task definition environment variables with this endpoint.

### 4. CloudWatch Log Group

Create a log group for the application:

```bash
aws logs create-log-group \
  --log-group-name /ecs/crm-container \
  --region us-east-1
```

---

## Building and Pushing Docker Images

### Option 1: Using AWS ECR

#### Linux/macOS

```bash
cd scripts
chmod +x build-push.sh
./build-push.sh
```

#### Windows

```cmd
cd scripts
build-push.bat
```

#### Manual ECR Push

```bash
# Authenticate Docker to ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin \
  123456789012.dkr.ecr.us-east-1.amazonaws.com

# Create ECR repository
aws ecr create-repository \
  --repository-name crm-container \
  --region us-east-1

# Build image
docker build -t crm-container:latest .

# Tag image
docker tag crm-container:latest \
  123456789012.dkr.ecr.us-east-1.amazonaws.com/crm-container:latest

# Push image
docker push 123456789012.dkr.ecr.us-east-1.amazonaws.com/crm-container:latest
```

### Option 2: Using Docker Hub

```bash
# Login to Docker Hub
docker login

# Build image
docker build -t yourusername/crm-container:latest .

# Push image
docker push yourusername/crm-container:latest
```

---

## ECS Fargate Setup

### 1. Understanding ECS Task Definition

The task definition (`ecs/task-definition.json`) defines:

- **Family**: Task definition name (`crm-container-task`)
- **Launch Type**: FARGATE
- **Network Mode**: awsvpc (required for Fargate)
- **CPU**: 512 (.5 vCPU)
- **Memory**: 1024 MB (1 GB)

#### Valid Fargate CPU/Memory Combinations

| CPU (vCPU) | Memory (MB) |
|------------|-------------|
| 256 (.25)  | 512, 1024, 2048 |
| 512 (.5)   | 1024, 2048, 3072, 4096 |
| 1024 (1)   | 2048-8192 (1GB increments) |
| 2048 (2)   | 4096-16384 (1GB increments) |
| 4096 (4)   | 8192-30720 (1GB increments) |

### 2. Container Definition

- **Image**: Your ECR or Docker Hub image URI
- **Port Mappings**: Container port 8080
- **Environment Variables**: Database connection details, JVM options
- **Logging**: CloudWatch Logs configuration

### 3. ECS Service Configuration

The service definition (`ecs/service-definition.json`) configures:

- **Desired Count**: 2 (for high availability)
- **Launch Type**: FARGATE
- **Network Configuration**: Subnets, security groups, public IP
- **Load Balancer**: Optional ALB integration
- **Deployment Circuit Breaker**: Automatic rollback on failures

---

## Deploying to ECS Fargate

### Automated Deployment

#### Linux/macOS

```bash
cd scripts
chmod +x deploy-image.sh
./deploy-image.sh
```

#### Windows

```cmd
cd scripts
deploy-image.bat
```

### Manual Deployment Steps

#### 1. Register Task Definition

```bash
# Update placeholders in task definition
sed -i 's|{{IMAGE_URI}}|123456789012.dkr.ecr.us-east-1.amazonaws.com/crm-container:latest|g' ecs/task-definition.json
sed -i 's|{{AWS_REGION}}|us-east-1|g' ecs/task-definition.json
sed -i 's|{{ACCOUNT_ID}}|123456789012|g' ecs/task-definition.json

# Register
aws ecs register-task-definition \
  --cli-input-json file://ecs/task-definition.json \
  --region us-east-1
```

#### 2. Create ECS Cluster

```bash
aws ecs create-cluster \
  --cluster-name crm-cluster \
  --region us-east-1
```

#### 3. Create or Update Service

```bash
# Create new service
aws ecs create-service \
  --cli-input-json file://ecs/service-definition.json \
  --region us-east-1

# Update existing service
aws ecs update-service \
  --cluster crm-cluster \
  --service crm-container-service \
  --task-definition crm-container-task:1 \
  --force-new-deployment \
  --region us-east-1
```

#### 4. Wait for Deployment

```bash
aws ecs wait services-stable \
  --cluster crm-cluster \
  --services crm-container-service \
  --region us-east-1
```

---

## Configuration Management

### Environment Variables

Update these in `ecs/task-definition.json`:

```json
"environment": [
  {"name": "DB_HOST", "value": "your-rds-endpoint.rds.amazonaws.com"},
  {"name": "DB_PORT", "value": "3306"},
  {"name": "DB_NAME", "value": "crm"},
  {"name": "DB_USERNAME", "value": "admin"},
  {"name": "DB_PASSWORD", "value": "SecurePassword123!"},
  {"name": "DDL_AUTO", "value": "validate"},
  {"name": "SPRING_PROFILES_ACTIVE", "value": "production"}
]
```

### Using AWS Secrets Manager (Recommended)

```json
"secrets": [
  {
    "name": "DB_PASSWORD",
    "valueFrom": "arn:aws:secretsmanager:us-east-1:123456789012:secret:crm/db-password-AbCdEf"
  }
]
```

### JVM Memory Configuration

Adjust JVM options based on container memory:

```bash
JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
```

---

## Monitoring and Logging

### CloudWatch Logs

#### View Logs

```bash
aws logs tail /ecs/crm-container --follow --region us-east-1
```

#### Query Logs

```bash
aws logs filter-log-events \
  --log-group-name /ecs/crm-container \
  --filter-pattern "ERROR" \
  --region us-east-1
```

### CloudWatch Metrics

ECS automatically publishes metrics:
- CPU Utilization
- Memory Utilization
- Network In/Out

### Application Health Checks

- **Health Check Endpoint**: `/actuator/health`
- **Readiness Check**: `/actuator/info`
- **Metrics Endpoint**: `/actuator/metrics`

---

## Troubleshooting

### Task Fails to Start

**Issue**: Task stops immediately after starting

**Solutions**:
1. Check CloudWatch logs for application errors
2. Verify environment variables (especially DB connection)
3. Ensure RDS security group allows inbound from ECS security group
4. Check IAM execution role has ECR and CloudWatch permissions

```bash
# Describe task to see stopped reason
aws ecs describe-tasks \
  --cluster crm-cluster \
  --tasks <task-arn> \
  --region us-east-1
```

### Database Connection Errors

**Issue**: Cannot connect to RDS database

**Solutions**:
1. Verify RDS endpoint is correct in task definition
2. Check RDS security group allows port 3306 from ECS security group
3. Verify database credentials
4. Ensure database exists: `CREATE DATABASE IF NOT EXISTS crm;`

### CPU/Memory Errors

**Issue**: Task stopped due to OutOfMemory or CPU constraints

**Solutions**:
1. Increase task memory/CPU in task definition
2. Adjust JVM heap size: `-Xmx512m` (should be <75% of container memory)
3. Use valid Fargate CPU/memory combinations

### Network Issues

**Issue**: Cannot access application from ALB

**Solutions**:
1. Verify security group allows inbound on port 8080
2. Check target group health checks are passing
3. Ensure subnets have route to internet (IGW or NAT)
4. Verify `assignPublicIp: ENABLED` if using public subnets

### Service Not Reaching Steady State

**Issue**: Service deployment hangs or rolls back

**Solutions**:
1. Check target group health check configuration
2. Increase `healthCheckGracePeriodSeconds` to 300
3. Verify application starts within grace period
4. Check CloudWatch logs for startup errors

---

## Security Best Practices

### 1. Use AWS Secrets Manager

Store sensitive data (passwords, API keys) in Secrets Manager:

```bash
aws secretsmanager create-secret \
  --name crm/db-password \
  --secret-string "YourSecurePassword" \
  --region us-east-1
```

### 2. Enable VPC Flow Logs

```bash
aws ec2 create-flow-logs \
  --resource-type VPC \
  --resource-ids vpc-xxxxxx \
  --traffic-type ALL \
  --log-destination-type cloud-watch-logs \
  --log-group-name /aws/vpc/flowlogs
```

### 3. Use Private Subnets

Deploy ECS tasks in private subnets with NAT Gateway for outbound access.

### 4. Enable Container Insights

```bash
aws ecs update-cluster-settings \
  --cluster crm-cluster \
  --settings name=containerInsights,value=enabled \
  --region us-east-1
```

### 5. Implement IAM Task Roles

Grant minimal permissions to tasks for AWS service access.

### 6. Rotate Database Credentials

Use RDS automatic password rotation with Secrets Manager.

### 7. Enable WAF on ALB

Protect against common web exploits:

```bash
aws wafv2 associate-web-acl \
  --web-acl-arn <waf-acl-arn> \
  --resource-arn <alb-arn> \
  --region us-east-1
```

---

## Scaling and Management

### Manual Scaling

```bash
aws ecs update-service \
  --cluster crm-cluster \
  --service crm-container-service \
  --desired-count 4 \
  --region us-east-1
```

### Auto Scaling

#### Create Scaling Policy

```bash
aws application-autoscaling register-scalable-target \
  --service-namespace ecs \
  --resource-id service/crm-cluster/crm-container-service \
  --scalable-dimension ecs:service:DesiredCount \
  --min-capacity 2 \
  --max-capacity 10 \
  --region us-east-1

aws application-autoscaling put-scaling-policy \
  --service-namespace ecs \
  --resource-id service/crm-cluster/crm-container-service \
  --scalable-dimension ecs:service:DesiredCount \
  --policy-name cpu-scaling-policy \
  --policy-type TargetTrackingScaling \
  --target-tracking-scaling-policy-configuration file://scaling-policy.json \
  --region us-east-1
```

#### scaling-policy.json

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

Use AWS CodeDeploy for blue/green deployments:

```bash
aws deploy create-deployment-group \
  --application-name AppECS-crm-cluster-crm-container-service \
  --deployment-group-name DgpECS-crm-cluster-crm-container-service \
  --deployment-config-name CodeDeployDefault.ECSAllAtOnce \
  --service-role-arn arn:aws:iam::123456789012:role/CodeDeployServiceRole \
  --ecs-services clusterName=crm-cluster,serviceName=crm-container-service \
  --load-balancer-info targetGroupPairInfoList=[{targetGroups=[{name=crm-tg-blue},{name=crm-tg-green}],prodTrafficRoute={listenerArns=[arn:aws:elasticloadbalancing:us-east-1:123456789012:listener/app/crm-alb/xxx]}}] \
  --region us-east-1
```

### Rollback

```bash
aws ecs update-service \
  --cluster crm-cluster \
  --service crm-container-service \
  --task-definition crm-container-task:1 \
  --region us-east-1
```

---

## Spring Boot 1.5.x Specific Notes

### Actuator Endpoints

Spring Boot 1.5.x uses different actuator paths:
- `/health` (not `/actuator/health` in 2.x)
- Management context path: `/appinfo`

Full health endpoint: `/appinfo/health`

### Upgrade Considerations

Spring Boot 1.5.10 reached EOL. Consider upgrading to:
- Spring Boot 2.7.x (Long-term support)
- Spring Boot 3.x (requires Java 17+)

### Java 8 Runtime

Using Amazon Corretto 8 as specified. For better performance and security, consider:
- Amazon Corretto 11
- Amazon Corretto 17

---

## Additional Resources

- [AWS ECS Documentation](https://docs.aws.amazon.com/ecs/)
- [AWS Fargate Documentation](https://docs.aws.amazon.com/AmazonECS/latest/userguide/what-is-fargate.html)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/1.5.x/reference/html/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)

---

## Support

For issues or questions:
1. Check CloudWatch logs: `/ecs/crm-container`
2. Review ECS service events
3. Verify task definition and service configuration
4. Consult AWS documentation

---

**Last Updated**: 2026-01-20
**Version**: 1.0.0
