# AWS Deployment Guide for minicompcrm

This guide provides step-by-step instructions for deploying the minicompcrm application to AWS.

## Prerequisites

- AWS Account with appropriate permissions
- AWS CLI installed and configured
- Docker installed locally
- Maven 3.6+ and Java 8+

## Architecture Overview

The application is deployed using the following AWS services:

- **Amazon ECS/Fargate**: Container orchestration
- **Amazon RDS (MySQL)**: Managed database
- **Amazon S3**: Document storage (PDFs, CSVs)
- **Application Load Balancer**: Traffic distribution
- **Amazon CloudWatch**: Logging and monitoring
- **AWS Secrets Manager**: Credential management
- **Amazon ECR**: Container registry

## Step 1: Create S3 Bucket

```bash
# Create S3 bucket for document storage
aws s3 mb s3://minicompcrm-documents-${AWS_ACCOUNT_ID} --region us-east-1

# Enable versioning
aws s3api put-bucket-versioning \
  --bucket minicompcrm-documents-${AWS_ACCOUNT_ID} \
  --versioning-configuration Status=Enabled

# Enable encryption
aws s3api put-bucket-encryption \
  --bucket minicompcrm-documents-${AWS_ACCOUNT_ID} \
  --server-side-encryption-configuration '{
    "Rules": [{
      "ApplyServerSideEncryptionByDefault": {
        "SSEAlgorithm": "AES256"
      }
    }]
  }'
```

## Step 2: Create RDS MySQL Instance

```bash
# Create DB subnet group
aws rds create-db-subnet-group \
  --db-subnet-group-name minicompcrm-db-subnet \
  --db-subnet-group-description "Subnet group for minicompcrm" \
  --subnet-ids subnet-xxxxx subnet-yyyyy

# Create RDS instance
aws rds create-db-instance \
  --db-instance-identifier minicompcrm-db \
  --db-instance-class db.t3.micro \
  --engine mysql \
  --engine-version 8.0.33 \
  --master-username admin \
  --master-user-password YOUR_SECURE_PASSWORD \
  --allocated-storage 20 \
  --db-subnet-group-name minicompcrm-db-subnet \
  --vpc-security-group-ids sg-xxxxx \
  --backup-retention-period 7 \
  --preferred-backup-window "03:00-04:00" \
  --preferred-maintenance-window "mon:04:00-mon:05:00" \
  --storage-encrypted \
  --enable-cloudwatch-logs-exports '["error","general","slowquery"]'
```

## Step 3: Store Database Credentials in Secrets Manager

```bash
# Create secret for database password
aws secretsmanager create-secret \
  --name minicompcrm/db/password \
  --description "Database password for minicompcrm" \
  --secret-string "YOUR_SECURE_PASSWORD"

# Create secret for database connection string
aws secretsmanager create-secret \
  --name minicompcrm/db/url \
  --description "Database URL for minicompcrm" \
  --secret-string "jdbc:mysql://minicompcrm-db.xxxxx.us-east-1.rds.amazonaws.com:3306/crm?useSSL=true"
```

## Step 4: Build and Push Docker Image

```bash
# Login to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com

# Create ECR repository
aws ecr create-repository --repository-name minicompcrm --region us-east-1

# Build Docker image
docker build -t minicompcrm:latest .

# Tag image
docker tag minicompcrm:latest ${AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com/minicompcrm:latest

# Push to ECR
docker push ${AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com/minicompcrm:latest
```

## Step 5: Create IAM Role for ECS Task

```bash
# Create trust policy
cat > trust-policy.json <<EOF
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

# Create IAM role
aws iam create-role \
  --role-name minicompcrm-task-role \
  --assume-role-policy-document file://trust-policy.json

# Attach policies
aws iam attach-role-policy \
  --role-name minicompcrm-task-role \
  --policy-arn arn:aws:iam::aws:policy/AmazonS3FullAccess

aws iam attach-role-policy \
  --role-name minicompcrm-task-role \
  --policy-arn arn:aws:iam::aws:policy/SecretsManagerReadWrite

aws iam attach-role-policy \
  --role-name minicompcrm-task-role \
  --policy-arn arn:aws:iam::aws:policy/CloudWatchLogsFullAccess
```

## Step 6: Create ECS Cluster

```bash
# Create ECS cluster
aws ecs create-cluster --cluster-name minicompcrm-cluster --region us-east-1
```

## Step 7: Create Task Definition

```bash
cat > task-definition.json <<EOF
{
  "family": "minicompcrm",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "taskRoleArn": "arn:aws:iam::${AWS_ACCOUNT_ID}:role/minicompcrm-task-role",
  "executionRoleArn": "arn:aws:iam::${AWS_ACCOUNT_ID}:role/ecsTaskExecutionRole",
  "containerDefinitions": [
    {
      "name": "minicompcrm",
      "image": "${AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com/minicompcrm:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {"name": "AWS_REGION", "value": "us-east-1"},
        {"name": "cloud.aws.s3.bucket", "value": "minicompcrm-documents-${AWS_ACCOUNT_ID}"},
        {"name": "LOG_LEVEL", "value": "INFO"},
        {"name": "ENVIRONMENT", "value": "prod"},
        {"name": "DB_DDL_AUTO", "value": "validate"}
      ],
      "secrets": [
        {
          "name": "DB_PASSWORD",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:${AWS_ACCOUNT_ID}:secret:minicompcrm/db/password"
        },
        {
          "name": "DB_URL",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:${AWS_ACCOUNT_ID}:secret:minicompcrm/db/url"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/minicompcrm",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      },
      "healthCheck": {
        "command": ["CMD-SHELL", "wget --quiet --tries=1 --spider http://localhost:8080/appinfo/health || exit 1"],
        "interval": 30,
        "timeout": 5,
        "retries": 3,
        "startPeriod": 60
      }
    }
  ]
}
EOF

# Register task definition
aws ecs register-task-definition --cli-input-json file://task-definition.json
```

## Step 8: Create Application Load Balancer

```bash
# Create ALB
aws elbv2 create-load-balancer \
  --name minicompcrm-alb \
  --subnets subnet-xxxxx subnet-yyyyy \
  --security-groups sg-xxxxx \
  --scheme internet-facing \
  --type application

# Create target group
aws elbv2 create-target-group \
  --name minicompcrm-tg \
  --protocol HTTP \
  --port 8080 \
  --vpc-id vpc-xxxxx \
  --target-type ip \
  --health-check-path /appinfo/health \
  --health-check-interval-seconds 30 \
  --health-check-timeout-seconds 5 \
  --healthy-threshold-count 2 \
  --unhealthy-threshold-count 3

# Create listener
aws elbv2 create-listener \
  --load-balancer-arn arn:aws:elasticloadbalancing:us-east-1:${AWS_ACCOUNT_ID}:loadbalancer/app/minicompcrm-alb/xxxxx \
  --protocol HTTP \
  --port 80 \
  --default-actions Type=forward,TargetGroupArn=arn:aws:elasticloadbalancing:us-east-1:${AWS_ACCOUNT_ID}:targetgroup/minicompcrm-tg/xxxxx
```

## Step 9: Create ECS Service

```bash
aws ecs create-service \
  --cluster minicompcrm-cluster \
  --service-name minicompcrm-service \
  --task-definition minicompcrm:1 \
  --desired-count 2 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[subnet-xxxxx,subnet-yyyyy],securityGroups=[sg-xxxxx],assignPublicIp=ENABLED}" \
  --load-balancers "targetGroupArn=arn:aws:elasticloadbalancing:us-east-1:${AWS_ACCOUNT_ID}:targetgroup/minicompcrm-tg/xxxxx,containerName=minicompcrm,containerPort=8080" \
  --health-check-grace-period-seconds 60
```

## Step 10: Configure Auto Scaling

```bash
# Register scalable target
aws application-autoscaling register-scalable-target \
  --service-namespace ecs \
  --resource-id service/minicompcrm-cluster/minicompcrm-service \
  --scalable-dimension ecs:service:DesiredCount \
  --min-capacity 2 \
  --max-capacity 10

# Create scaling policy
aws application-autoscaling put-scaling-policy \
  --service-namespace ecs \
  --resource-id service/minicompcrm-cluster/minicompcrm-service \
  --scalable-dimension ecs:service:DesiredCount \
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

## Step 11: Set Up CloudWatch Alarms

```bash
# Create alarm for high CPU
aws cloudwatch put-metric-alarm \
  --alarm-name minicompcrm-high-cpu \
  --alarm-description "Alert when CPU exceeds 80%" \
  --metric-name CPUUtilization \
  --namespace AWS/ECS \
  --statistic Average \
  --period 300 \
  --threshold 80 \
  --comparison-operator GreaterThanThreshold \
  --evaluation-periods 2

# Create alarm for unhealthy targets
aws cloudwatch put-metric-alarm \
  --alarm-name minicompcrm-unhealthy-targets \
  --alarm-description "Alert when targets are unhealthy" \
  --metric-name UnHealthyHostCount \
  --namespace AWS/ApplicationELB \
  --statistic Average \
  --period 60 \
  --threshold 1 \
  --comparison-operator GreaterThanOrEqualToThreshold \
  --evaluation-periods 2
```

## Monitoring and Troubleshooting

### View Logs

```bash
# View ECS service logs
aws logs tail /ecs/minicompcrm --follow

# View specific task logs
aws logs tail /ecs/minicompcrm --follow --filter-pattern "ERROR"
```

### Check Service Status

```bash
# Describe service
aws ecs describe-services \
  --cluster minicompcrm-cluster \
  --services minicompcrm-service

# List tasks
aws ecs list-tasks \
  --cluster minicompcrm-cluster \
  --service-name minicompcrm-service
```

### Access Application

```bash
# Get ALB DNS name
aws elbv2 describe-load-balancers \
  --names minicompcrm-alb \
  --query 'LoadBalancers[0].DNSName' \
  --output text
```

Access the application at: `http://<ALB-DNS-NAME>`

## Cost Optimization

1. **Use Fargate Spot** for non-production environments
2. **Enable S3 Lifecycle Policies** to archive old documents
3. **Use RDS Reserved Instances** for production
4. **Configure CloudWatch Log Retention** (e.g., 30 days)
5. **Enable S3 Intelligent-Tiering** for cost savings

## Security Best Practices

1. **Enable VPC Flow Logs** for network monitoring
2. **Use AWS WAF** to protect against common web exploits
3. **Enable RDS encryption at rest**
4. **Use SSL/TLS certificates** with ACM
5. **Implement least privilege IAM policies**
6. **Enable AWS Config** for compliance monitoring
7. **Use AWS GuardDuty** for threat detection

## Rollback Procedure

```bash
# Rollback to previous task definition
aws ecs update-service \
  --cluster minicompcrm-cluster \
  --service minicompcrm-service \
  --task-definition minicompcrm:PREVIOUS_VERSION
```

## Cleanup

```bash
# Delete ECS service
aws ecs delete-service --cluster minicompcrm-cluster --service minicompcrm-service --force

# Delete ECS cluster
aws ecs delete-cluster --cluster minicompcrm-cluster

# Delete RDS instance
aws rds delete-db-instance --db-instance-identifier minicompcrm-db --skip-final-snapshot

# Delete S3 bucket
aws s3 rb s3://minicompcrm-documents-${AWS_ACCOUNT_ID} --force

# Delete ECR repository
aws ecr delete-repository --repository-name minicompcrm --force
```
