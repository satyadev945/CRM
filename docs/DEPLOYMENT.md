# Container-TestRah - AWS ECS Fargate Deployment Guide

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Local Development Setup](#local-development-setup)
3. [Building and Pushing Docker Images](#building-and-pushing-docker-images)
4. [AWS ECS Fargate Prerequisites](#aws-ecs-fargate-prerequisites)
5. [ECS Fargate Setup](#ecs-fargate-setup)
6. [Understanding ECS Task Definitions](#understanding-ecs-task-definitions)
7. [Understanding ECS Service Configuration](#understanding-ecs-service-configuration)
8. [Deployment to AWS ECS Fargate](#deployment-to-aws-ecs-fargate)
9. [Monitoring and Logging](#monitoring-and-logging)
10. [Troubleshooting](#troubleshooting)
11. [Scaling and Management](#scaling-and-management)
12. [Security Best Practices](#security-best-practices)

---

## Prerequisites

### Required Tools
- **Docker**: Version 20.10 or later
- **Docker Compose**: Version 2.0 or later (for local development)
- **AWS CLI**: Version 2.x
- **Java**: JDK 8 (for local development)
- **Maven**: Version 3.6 or later (for local builds)

### AWS Account Requirements
- Active AWS account with appropriate permissions
- IAM user with ECS, ECR, CloudWatch Logs, and VPC permissions
- AWS CLI configured with credentials (`aws configure`)

### Installation Instructions

#### Docker
**Linux:**
```bash
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER
```

**macOS:**
```bash
brew install docker docker-compose
```

**Windows:**
Download Docker Desktop from https://www.docker.com/products/docker-desktop

#### AWS CLI
**Linux/macOS:**
```bash
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```

**Windows:**
Download the MSI installer from https://aws.amazon.com/cli/

#### Configure AWS CLI
```bash
aws configure
# Enter your AWS Access Key ID
# Enter your AWS Secret Access Key
# Enter default region (e.g., us-east-1)
# Enter default output format (json)
```

---

## Local Development Setup

### Using Docker Compose

1. **Navigate to project directory:**
```bash
cd /modernize-data/studio-data/TNT1001/APP1333/transformed-code/142/studio-workspace/Container-TestRah
```

2. **Build and start the application:**
```bash
docker-compose up --build
```

3. **Access the application:**
- Application: http://localhost:8080
- Health Check: http://localhost:8080/actuator/health
- Application Info: http://localhost:8080/actuator/info

4. **Stop the application:**
```bash
docker-compose down
```

5. **View logs:**
```bash
docker-compose logs -f container-testrah-app
```

### Configuration

The application supports multiple Spring profiles:
- `default`: Local development
- `docker`: Docker container configuration
- `production`: Production environment

Environment variables can be configured in `docker-compose.yml`:
```yaml
environment:
  - SPRING_PROFILES_ACTIVE=docker
  - JAVA_OPTS=-Xmx512m -Xms256m
```

---

## Building and Pushing Docker Images

### Using Build Scripts

#### Linux/macOS
```bash
chmod +x scripts/build-push.sh
./scripts/build-push.sh
```

#### Windows
```cmd
scripts\build-push.bat
```

### Script Workflow

1. **Select Registry Type:**
   - Option 1: AWS ECR (Elastic Container Registry)
   - Option 2: Docker Hub

2. **Provide Registry Details:**
   - For ECR: AWS Region, Account ID, Repository Name
   - For Docker Hub: Username, Password/Token

3. **Enter Image Tag:**
   - Default: `latest`
   - Custom: e.g., `v1.0.0`, `production`, `staging`

4. **Script Actions:**
   - Authenticates with selected registry
   - Creates ECR repository if it doesn't exist (ECR only)
   - Builds Docker image from Dockerfile
   - Tags image appropriately
   - Pushes image to registry

### Manual Build (Alternative)

```bash
# Build image
docker build -t container-testrah:latest .

# Tag for ECR
docker tag container-testrah:latest 123456789.dkr.ecr.us-east-1.amazonaws.com/container-testrah:latest

# Login to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 123456789.dkr.ecr.us-east-1.amazonaws.com

# Push to ECR
docker push 123456789.dkr.ecr.us-east-1.amazonaws.com/container-testrah:latest
```

---

## AWS ECS Fargate Prerequisites

### 1. VPC and Network Configuration

ECS Fargate requires a VPC with at least two subnets in different availability zones.

**Create VPC (if needed):**
```bash
# Create VPC
VPC_ID=$(aws ec2 create-vpc \
  --cidr-block 10.0.0.0/16 \
  --query 'Vpc.VpcId' \
  --output text)

# Enable DNS hostnames
aws ec2 modify-vpc-attribute \
  --vpc-id $VPC_ID \
  --enable-dns-hostnames

# Create Internet Gateway
IGW_ID=$(aws ec2 create-internet-gateway \
  --query 'InternetGateway.InternetGatewayId' \
  --output text)

# Attach Internet Gateway to VPC
aws ec2 attach-internet-gateway \
  --vpc-id $VPC_ID \
  --internet-gateway-id $IGW_ID

# Create Subnets
SUBNET_1=$(aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block 10.0.1.0/24 \
  --availability-zone us-east-1a \
  --query 'Subnet.SubnetId' \
  --output text)

SUBNET_2=$(aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block 10.0.2.0/24 \
  --availability-zone us-east-1b \
  --query 'Subnet.SubnetId' \
  --output text)

# Create Route Table
ROUTE_TABLE_ID=$(aws ec2 create-route-table \
  --vpc-id $VPC_ID \
  --query 'RouteTable.RouteTableId' \
  --output text)

# Create route to Internet Gateway
aws ec2 create-route \
  --route-table-id $ROUTE_TABLE_ID \
  --destination-cidr-block 0.0.0.0/0 \
  --gateway-id $IGW_ID

# Associate subnets with route table
aws ec2 associate-route-table --subnet-id $SUBNET_1 --route-table-id $ROUTE_TABLE_ID
aws ec2 associate-route-table --subnet-id $SUBNET_2 --route-table-id $ROUTE_TABLE_ID
```

### 2. Security Group Configuration

**Create Security Group:**
```bash
# Create security group
SG_ID=$(aws ec2 create-security-group \
  --group-name container-testrah-sg \
  --description "Security group for Container-TestRah ECS tasks" \
  --vpc-id $VPC_ID \
  --query 'GroupId' \
  --output text)

# Allow HTTP traffic (port 8080)
aws ec2 authorize-security-group-ingress \
  --group-id $SG_ID \
  --protocol tcp \
  --port 8080 \
  --cidr 0.0.0.0/0

# Allow HTTPS traffic (if needed)
aws ec2 authorize-security-group-ingress \
  --group-id $SG_ID \
  --protocol tcp \
  --port 443 \
  --cidr 0.0.0.0/0

# Allow ALB health checks (port 80)
aws ec2 authorize-security-group-ingress \
  --group-id $SG_ID \
  --protocol tcp \
  --port 80 \
  --cidr 0.0.0.0/0
```

### 3. IAM Roles

#### Task Execution Role
Required for ECS to pull images from ECR and write logs to CloudWatch.

**Create Trust Policy (trust-policy.json):**
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

**Create Role:**
```bash
# Create execution role
aws iam create-role \
  --role-name ecsTaskExecutionRole \
  --assume-role-policy-document file://trust-policy.json

# Attach AWS managed policy
aws iam attach-role-policy \
  --role-name ecsTaskExecutionRole \
  --policy-arn arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy
```

#### Task Role (Optional)
Required if your application needs to access other AWS services.

```bash
# Create task role
aws iam create-role \
  --role-name ecsTaskRole \
  --assume-role-policy-document file://trust-policy.json

# Attach policies as needed (example: S3 access)
aws iam attach-role-policy \
  --role-name ecsTaskRole \
  --policy-arn arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess
```

---

## ECS Fargate Setup

### Create ECS Cluster

```bash
aws ecs create-cluster --cluster-name container-testrah-cluster
```

### Create CloudWatch Log Group

```bash
aws logs create-log-group --log-group-name /ecs/container-testrah
```

### Set Log Retention (Optional)

```bash
aws logs put-retention-policy \
  --log-group-name /ecs/container-testrah \
  --retention-in-days 7
```

---

## Understanding ECS Task Definitions

### Key Components

#### 1. Launch Type Configuration
```json
"requiresCompatibilities": ["FARGATE"],
"networkMode": "awsvpc"
```
- **FARGATE**: Serverless compute engine
- **awsvpc**: Each task gets its own elastic network interface (ENI)

#### 2. CPU and Memory
```json
"cpu": "512",
"memory": "1024"
```

**Valid Fargate CPU/Memory Combinations:**
| CPU (vCPU) | Memory (MB) |
|------------|-------------|
| 256 (.25)  | 512, 1024, 2048 |
| 512 (.5)   | 1024, 2048, 3072, 4096 |
| 1024 (1)   | 2048-8192 (increments of 1024) |
| 2048 (2)   | 4096-16384 (increments of 1024) |
| 4096 (4)   | 8192-30720 (increments of 1024) |

#### 3. Container Definition
```json
"containerDefinitions": [
  {
    "name": "container-testrah",
    "image": "{{IMAGE_URI}}",
    "essential": true,
    "portMappings": [
      {
        "containerPort": 8080,
        "protocol": "tcp"
      }
    ]
  }
]
```

#### 4. Environment Variables
```json
"environment": [
  {
    "name": "JAVA_OPTS",
    "value": "-Xmx512m -Xms256m -XX:+UseContainerSupport"
  },
  {
    "name": "SPRING_PROFILES_ACTIVE",
    "value": "production"
  }
]
```

#### 5. Logging Configuration
```json
"logConfiguration": {
  "logDriver": "awslogs",
  "options": {
    "awslogs-group": "/ecs/container-testrah",
    "awslogs-region": "us-east-1",
    "awslogs-stream-prefix": "ecs"
  }
}
```

---

## Understanding ECS Service Configuration

### Key Components

#### 1. Service Definition
```json
{
  "serviceName": "container-testrah-service",
  "cluster": "container-testrah-cluster",
  "taskDefinition": "container-testrah-task",
  "desiredCount": 2,
  "launchType": "FARGATE"
}
```

#### 2. Network Configuration
```json
"networkConfiguration": {
  "awsvpcConfiguration": {
    "subnets": ["subnet-xxx", "subnet-yyy"],
    "securityGroups": ["sg-xxx"],
    "assignPublicIp": "ENABLED"
  }
}
```

- **subnets**: At least 2 subnets in different AZs for high availability
- **securityGroups**: Controls inbound/outbound traffic
- **assignPublicIp**: ENABLED for internet access (required if using public subnets)

#### 3. Deployment Configuration
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

- **maximumPercent**: Maximum tasks during deployment (200% = double)
- **minimumHealthyPercent**: Minimum healthy tasks (50% = half)
- **deploymentCircuitBreaker**: Automatic rollback on failure

#### 4. Load Balancer Configuration (Optional)
```json
"loadBalancers": [
  {
    "targetGroupArn": "arn:aws:elasticloadbalancing:...",
    "containerName": "container-testrah",
    "containerPort": 8080
  }
],
"healthCheckGracePeriodSeconds": 300
```

---

## Deployment to AWS ECS Fargate

### Using Deployment Scripts

#### Linux/macOS
```bash
chmod +x scripts/deploy-image.sh
./scripts/deploy-image.sh
```

#### Windows
```cmd
scripts\deploy-image.bat
```

### Script Workflow

1. **AWS Configuration:**
   - Enter AWS Region
   - Enter ECS Cluster Name
   - Script checks/creates cluster if needed

2. **Network Configuration:**
   - Enter VPC ID
   - Enter Subnet IDs (comma-separated)
   - Enter Security Group ID

3. **Container Image:**
   - Enter ECR Image URI

4. **Load Balancer:**
   - Choose whether to create ALB
   - If yes: Script creates ALB, Target Group, and Listener
   - If no: Service runs without load balancer

5. **Deployment:**
   - Creates CloudWatch log group
   - Registers task definition
   - Creates or updates ECS service
   - Waits for service to stabilize
   - Displays deployment status and access URLs

### Manual Deployment (Alternative)

```bash
# Register task definition
aws ecs register-task-definition --cli-input-json file://ecs/task-definition.json

# Create service
aws ecs create-service --cli-input-json file://ecs/service-definition.json

# Or update existing service
aws ecs update-service \
  --cluster container-testrah-cluster \
  --service container-testrah-service \
  --task-definition container-testrah-task:2 \
  --force-new-deployment
```

---

## Monitoring and Logging

### CloudWatch Logs

**View logs in real-time:**
```bash
aws logs tail /ecs/container-testrah --follow
```

**View logs for specific time range:**
```bash
aws logs tail /ecs/container-testrah \
  --since 1h \
  --format short
```

**Filter logs:**
```bash
aws logs tail /ecs/container-testrah \
  --follow \
  --filter-pattern "ERROR"
```

### ECS Service Metrics

**View service status:**
```bash
aws ecs describe-services \
  --cluster container-testrah-cluster \
  --services container-testrah-service
```

**View running tasks:**
```bash
aws ecs list-tasks \
  --cluster container-testrah-cluster \
  --service-name container-testrah-service
```

**View task details:**
```bash
aws ecs describe-tasks \
  --cluster container-testrah-cluster \
  --tasks <task-arn>
```

### CloudWatch Metrics

ECS automatically publishes metrics to CloudWatch:
- CPUUtilization
- MemoryUtilization
- NetworkRxBytes
- NetworkTxBytes

**View metrics:**
```bash
aws cloudwatch get-metric-statistics \
  --namespace AWS/ECS \
  --metric-name CPUUtilization \
  --dimensions Name=ServiceName,Value=container-testrah-service Name=ClusterName,Value=container-testrah-cluster \
  --start-time 2024-01-01T00:00:00Z \
  --end-time 2024-01-01T23:59:59Z \
  --period 3600 \
  --statistics Average
```

---

## Troubleshooting

### Common Issues

#### 1. Task Fails to Start

**Symptoms:**
- Tasks stuck in PENDING state
- Tasks immediately fail after starting

**Diagnosis:**
```bash
# Get task ARN
TASK_ARN=$(aws ecs list-tasks \
  --cluster container-testrah-cluster \
  --service-name container-testrah-service \
  --query 'taskArns[0]' \
  --output text)

# Describe task
aws ecs describe-tasks \
  --cluster container-testrah-cluster \
  --tasks $TASK_ARN
```

**Common Causes:**
- Insufficient CPU/memory resources
- Invalid CPU/memory combination
- Image pull errors (check ECR permissions)
- Container health check failures
- Application startup errors

**Solutions:**
- Check CloudWatch logs for application errors
- Verify executionRoleArn has ECR permissions
- Ensure CPU/memory values are valid Fargate combinations
- Increase health check grace period
- Verify network configuration (subnets, security groups)

#### 2. Network Connectivity Issues

**Symptoms:**
- Cannot access application via load balancer
- Tasks cannot pull images from ECR
- Application cannot connect to external services

**Diagnosis:**
```bash
# Check security group rules
aws ec2 describe-security-groups --group-ids <security-group-id>

# Check subnet route tables
aws ec2 describe-route-tables --filters "Name=association.subnet-id,Values=<subnet-id>"

# Check task network interfaces
aws ecs describe-tasks \
  --cluster container-testrah-cluster \
  --tasks $TASK_ARN \
  --query 'tasks[0].attachments[0].details'
```

**Solutions:**
- Verify security group allows inbound traffic on port 8080
- Ensure subnets have route to Internet Gateway (0.0.0.0/0)
- Check assignPublicIp is ENABLED if using public subnets
- Verify ALB target group health checks are configured correctly

#### 3. Memory/CPU Issues

**Symptoms:**
- Tasks being killed due to OOM
- High CPU utilization
- Slow application performance

**Diagnosis:**
```bash
# Check CloudWatch metrics
aws cloudwatch get-metric-statistics \
  --namespace AWS/ECS \
  --metric-name MemoryUtilization \
  --dimensions Name=ServiceName,Value=container-testrah-service \
  --start-time $(date -u -d '1 hour ago' +%Y-%m-%dT%H:%M:%S) \
  --end-time $(date -u +%Y-%m-%dT%H:%M:%S) \
  --period 300 \
  --statistics Maximum
```

**Solutions:**
- Increase task memory allocation
- Adjust JVM heap size in JAVA_OPTS
- Optimize application memory usage
- Use larger Fargate CPU/memory configuration

#### 4. Deployment Failures

**Symptoms:**
- Service fails to reach steady state
- Circuit breaker triggers rollback
- Tasks fail health checks

**Diagnosis:**
```bash
# Check service events
aws ecs describe-services \
  --cluster container-testrah-cluster \
  --services container-testrah-service \
  --query 'services[0].events[0:10]'
```

**Solutions:**
- Verify new task definition is valid
- Check application health endpoint returns 200 OK
- Increase healthCheckGracePeriodSeconds
- Review application logs for startup errors
- Verify environment variables are correct

### Debug Commands

```bash
# Get task logs
aws logs get-log-events \
  --log-group-name /ecs/container-testrah \
  --log-stream-name ecs/container-testrah/<task-id>

# Execute command in running task (requires ECS Exec enabled)
aws ecs execute-command \
  --cluster container-testrah-cluster \
  --task <task-id> \
  --container container-testrah \
  --interactive \
  --command "/bin/sh"

# Check service deployment status
aws ecs describe-services \
  --cluster container-testrah-cluster \
  --services container-testrah-service \
  --query 'services[0].deployments'
```

---

## Scaling and Management

### Manual Scaling

```bash
# Scale up to 4 tasks
aws ecs update-service \
  --cluster container-testrah-cluster \
  --service container-testrah-service \
  --desired-count 4

# Scale down to 1 task
aws ecs update-service \
  --cluster container-testrah-cluster \
  --service container-testrah-service \
  --desired-count 1
```

### Auto Scaling

#### Create Auto Scaling Target
```bash
aws application-autoscaling register-scalable-target \
  --service-namespace ecs \
  --scalable-dimension ecs:service:DesiredCount \
  --resource-id service/container-testrah-cluster/container-testrah-service \
  --min-capacity 2 \
  --max-capacity 10
```

#### Create Scaling Policy (Target Tracking)
```bash
# CPU-based scaling
aws application-autoscaling put-scaling-policy \
  --service-namespace ecs \
  --scalable-dimension ecs:service:DesiredCount \
  --resource-id service/container-testrah-cluster/container-testrah-service \
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

### Blue/Green Deployments

For zero-downtime deployments, use AWS CodeDeploy with ECS:

```bash
# Create CodeDeploy application
aws deploy create-application \
  --application-name container-testrah-app \
  --compute-platform ECS

# Create deployment group
aws deploy create-deployment-group \
  --application-name container-testrah-app \
  --deployment-group-name container-testrah-dg \
  --deployment-config-name CodeDeployDefault.ECSAllAtOnce \
  --service-role-arn arn:aws:iam::123456789:role/CodeDeployServiceRole \
  --ecs-services clusterName=container-testrah-cluster,serviceName=container-testrah-service \
  --load-balancer-info targetGroupPairInfoList=[{targetGroups=[{name=container-testrah-tg-blue},{name=container-testrah-tg-green}],prodTrafficRoute={listenerArns=[arn:aws:elasticloadbalancing:...]}}]
```

### Rolling Updates

ECS performs rolling updates by default:

```bash
# Update task definition
aws ecs register-task-definition --cli-input-json file://ecs/task-definition.json

# Update service with new task definition
aws ecs update-service \
  --cluster container-testrah-cluster \
  --service container-testrah-service \
  --task-definition container-testrah-task:3 \
  --force-new-deployment
```

---

## Security Best Practices

### 1. Use Private Subnets

For production, deploy tasks in private subnets with NAT Gateway:

```bash
# Create NAT Gateway
EIP_ID=$(aws ec2 allocate-address --domain vpc --query 'AllocationId' --output text)
NAT_GW_ID=$(aws ec2 create-nat-gateway \
  --subnet-id $PUBLIC_SUBNET_ID \
  --allocation-id $EIP_ID \
  --query 'NatGateway.NatGatewayId' \
  --output text)

# Update route table for private subnets
aws ec2 create-route \
  --route-table-id $PRIVATE_ROUTE_TABLE_ID \
  --destination-cidr-block 0.0.0.0/0 \
  --nat-gateway-id $NAT_GW_ID

# Update service to use private subnets
# Set assignPublicIp to DISABLED in service definition
```

### 2. Secrets Management

Use AWS Secrets Manager or SSM Parameter Store for sensitive data:

```bash
# Store secret
aws secretsmanager create-secret \
  --name container-testrah/db-password \
  --secret-string "my-database-password"

# Reference in task definition
{
  "secrets": [
    {
      "name": "DB_PASSWORD",
      "valueFrom": "arn:aws:secretsmanager:us-east-1:123456789:secret:container-testrah/db-password"
    }
  ]
}
```

### 3. IAM Policies

Follow principle of least privilege:

**Task Execution Role Policy:**
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
        "ecr:BatchGetImage",
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Resource": "*"
    }
  ]
}
```

**Task Role Policy (Example: S3 access):**
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:GetObject",
        "s3:ListBucket"
      ],
      "Resource": [
        "arn:aws:s3:::my-app-bucket/*",
        "arn:aws:s3:::my-app-bucket"
      ]
    }
  ]
}
```

### 4. Enable Container Insights

```bash
aws ecs update-cluster-settings \
  --cluster container-testrah-cluster \
  --settings name=containerInsights,value=enabled
```

### 5. Security Group Best Practices

- Use separate security groups for ALB and ECS tasks
- Allow inbound traffic only from ALB security group
- Restrict outbound traffic to required services only
- Use security group rules instead of CIDR blocks when possible

```bash
# Allow inbound from ALB only
aws ec2 authorize-security-group-ingress \
  --group-id $ECS_SG_ID \
  --protocol tcp \
  --port 8080 \
  --source-group $ALB_SG_ID
```

---

## Additional Resources

- [AWS ECS Documentation](https://docs.aws.amazon.com/ecs/)
- [AWS Fargate Documentation](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/AWS_Fargate.html)
- [Spring Boot on ECS](https://spring.io/guides/gs/spring-boot-docker/)
- [ECS Best Practices](https://docs.aws.amazon.com/AmazonECS/latest/bestpracticesguide/intro.html)

---

## Support

For issues or questions:
1. Check CloudWatch logs for application errors
2. Review ECS service events
3. Consult AWS ECS troubleshooting guide
4. Contact your DevOps team or AWS Support

---

**Document Version:** 1.0  
**Last Updated:** 2024  
**Platform:** AWS ECS Fargate  
**Application:** Container-TestRah (Java Spring Boot)
