#!/bin/bash

set -e
set -o pipefail

echo "========================================"
echo "AWS ECS Fargate Deployment Script"
echo "========================================"
echo ""

# Project configuration
PROJECT_NAME="crm"
TASK_FAMILY="crm-task"
SERVICE_NAME="crm-service"

# Prompt for AWS configuration
read -p "Enter AWS Region (e.g., us-east-1): " AWS_REGION
read -p "Enter ECS Cluster Name: " CLUSTER_NAME

echo ""
echo "Checking if ECS cluster exists..."
if ! aws ecs describe-clusters --clusters "$CLUSTER_NAME" --region "$AWS_REGION" --query "clusters[0].clusterName" --output text 2>/dev/null | grep -q "$CLUSTER_NAME"; then
  echo "Cluster does not exist. Creating ECS cluster: $CLUSTER_NAME"
  aws ecs create-cluster --cluster-name "$CLUSTER_NAME" --region "$AWS_REGION"
  echo "Cluster created successfully."
else
  echo "Cluster already exists."
fi

echo ""
echo "========================================"
echo "Network Configuration"
echo "========================================"
read -p "Enter VPC ID: " VPC_ID
read -p "Enter Subnet IDs (comma-separated, at least 2): " SUBNETS_INPUT
read -p "Enter Security Group ID: " SECURITY_GROUP

# Convert comma-separated subnets to array
IFS=',' read -ra SUBNETS_ARRAY <<< "$SUBNETS_INPUT"
SUBNET_1=$(echo "${SUBNETS_ARRAY[0]}" | xargs)
SUBNET_2=$(echo "${SUBNETS_ARRAY[1]}" | xargs)

echo ""
echo "========================================"
echo "Database Configuration"
echo "========================================"
read -p "Enter Database Host: " DB_HOST
read -p "Enter Database Port (default: 3306): " DB_PORT
DB_PORT=${DB_PORT:-3306}
read -p "Enter Database Name (default: crm): " DB_NAME
DB_NAME=${DB_NAME:-crm}
read -p "Enter Database User: " DB_USER
read -sp "Enter Database Password: " DB_PASSWORD
echo ""

echo ""
echo "========================================"
echo "Container Image Configuration"
echo "========================================"
read -p "Enter ECR Image URI (e.g., 123456789.dkr.ecr.us-east-1.amazonaws.com/crm:latest): " IMAGE_URI

echo ""
echo "========================================"
echo "Load Balancer Configuration"
echo "========================================"
read -p "Do you need a load balancer for this service? (y/n): " NEED_LB

if [[ "$NEED_LB" =~ ^[Yy]$ ]]; then
  echo "Creating Application Load Balancer and Target Group..."
  
  # Create ALB
  ALB_NAME="${PROJECT_NAME}-alb"
  echo "Creating ALB: $ALB_NAME"
  ALB_ARN=$(aws elbv2 create-load-balancer \
    --name "$ALB_NAME" \
    --subnets "$SUBNET_1" "$SUBNET_2" \
    --security-groups "$SECURITY_GROUP" \
    --scheme internet-facing \
    --type application \
    --region "$AWS_REGION" \
    --query 'LoadBalancers[0].LoadBalancerArn' \
    --output text)
  
  echo "ALB created: $ALB_ARN"
  
  # Create Target Group with ip target type (required for Fargate)
  TG_NAME="${PROJECT_NAME}-tg"
  echo "Creating Target Group: $TG_NAME"
  TARGET_GROUP_ARN=$(aws elbv2 create-target-group \
    --name "$TG_NAME" \
    --protocol HTTP \
    --port 8080 \
    --vpc-id "$VPC_ID" \
    --target-type ip \
    --health-check-enabled \
    --health-check-path "/appinfo/health" \
    --health-check-interval-seconds 30 \
    --health-check-timeout-seconds 5 \
    --healthy-threshold-count 2 \
    --unhealthy-threshold-count 3 \
    --region "$AWS_REGION" \
    --query 'TargetGroups[0].TargetGroupArn' \
    --output text)
  
  echo "Target Group created: $TARGET_GROUP_ARN"
  
  # Create Listener
  echo "Creating ALB Listener..."
  aws elbv2 create-listener \
    --load-balancer-arn "$ALB_ARN" \
    --protocol HTTP \
    --port 80 \
    --default-actions Type=forward,TargetGroupArn="$TARGET_GROUP_ARN" \
    --region "$AWS_REGION"
  
  echo "Listener created successfully."
  
  # Get ALB DNS name
  ALB_DNS=$(aws elbv2 describe-load-balancers \
    --load-balancer-arns "$ALB_ARN" \
    --region "$AWS_REGION" \
    --query 'LoadBalancers[0].DNSName' \
    --output text)
  
  echo "Load Balancer DNS: $ALB_DNS"
else
  echo "Skipping load balancer creation."
  TARGET_GROUP_ARN=""
fi

echo ""
echo "Getting AWS Account ID..."
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo "Account ID: $ACCOUNT_ID"

echo ""
echo "========================================"
echo "Creating CloudWatch Log Group"
echo "========================================"
LOG_GROUP="/ecs/$PROJECT_NAME"
if ! aws logs describe-log-groups --log-group-name-prefix "$LOG_GROUP" --region "$AWS_REGION" --query "logGroups[?logGroupName=='$LOG_GROUP']" --output text | grep -q "$LOG_GROUP"; then
  echo "Creating log group: $LOG_GROUP"
  aws logs create-log-group --log-group-name "$LOG_GROUP" --region "$AWS_REGION"
  echo "Log group created."
else
  echo "Log group already exists."
fi

echo ""
echo "========================================"
echo "Preparing ECS Task Definition"
echo "========================================"

# Create temporary task definition with replacements
cp ecs/task-definition.json ecs/task-definition.tmp.json

sed -i "s|{{IMAGE_URI}}|$IMAGE_URI|g" ecs/task-definition.tmp.json
sed -i "s|{{AWS_REGION}}|$AWS_REGION|g" ecs/task-definition.tmp.json
sed -i "s|{{ACCOUNT_ID}}|$ACCOUNT_ID|g" ecs/task-definition.tmp.json
sed -i "s|{{DB_HOST}}|$DB_HOST|g" ecs/task-definition.tmp.json
sed -i "s|{{DB_PORT}}|$DB_PORT|g" ecs/task-definition.tmp.json
sed -i "s|{{DB_NAME}}|$DB_NAME|g" ecs/task-definition.tmp.json
sed -i "s|{{DB_USER}}|$DB_USER|g" ecs/task-definition.tmp.json
sed -i "s|{{DB_PASSWORD}}|$DB_PASSWORD|g" ecs/task-definition.tmp.json

echo "Registering ECS task definition..."
TASK_DEF_ARN=$(aws ecs register-task-definition \
  --cli-input-json file://ecs/task-definition.tmp.json \
  --region "$AWS_REGION" \
  --query 'taskDefinition.taskDefinitionArn' \
  --output text)

echo "Task definition registered: $TASK_DEF_ARN"

# Clean up temporary file
rm -f ecs/task-definition.tmp.json

echo ""
echo "========================================"
echo "Preparing ECS Service Definition"
echo "========================================"

# Create temporary service definition with replacements
cp ecs/service-definition.json ecs/service-definition.tmp.json

sed -i "s|{{CLUSTER_NAME}}|$CLUSTER_NAME|g" ecs/service-definition.tmp.json
sed -i "s|{{SUBNET_1}}|$SUBNET_1|g" ecs/service-definition.tmp.json
sed -i "s|{{SUBNET_2}}|$SUBNET_2|g" ecs/service-definition.tmp.json
sed -i "s|{{SECURITY_GROUP}}|$SECURITY_GROUP|g" ecs/service-definition.tmp.json

if [[ "$NEED_LB" =~ ^[Yy]$ ]]; then
  sed -i "s|{{TARGET_GROUP_ARN}}|$TARGET_GROUP_ARN|g" ecs/service-definition.tmp.json
else
  # Remove loadBalancers section if no LB needed
  sed -i '"loadBalancers":/,/],/d' ecs/service-definition.tmp.json
  sed -i '/"healthCheckGracePeriodSeconds":/d' ecs/service-definition.tmp.json
fi

echo "Checking if service exists..."
EXISTING_SERVICE=$(aws ecs describe-services \
  --cluster "$CLUSTER_NAME" \
  --services "$SERVICE_NAME" \
  --region "$AWS_REGION" \
  --query "services[?serviceName=='$SERVICE_NAME' && status=='ACTIVE'].serviceName" \
  --output text)

if [ -z "$EXISTING_SERVICE" ] || [ "$EXISTING_SERVICE" == "None" ]; then
  echo "Service does not exist. Creating new service..."
  aws ecs create-service \
    --cli-input-json file://ecs/service-definition.tmp.json \
    --region "$AWS_REGION"
  echo "Service created successfully."
else
  echo "Service exists. Updating service with new task definition..."
  UPDATE_CMD="aws ecs update-service --cluster $CLUSTER_NAME --service $SERVICE_NAME --task-definition $TASK_DEF_ARN --region $AWS_REGION"
  
  if [[ "$NEED_LB" =~ ^[Yy]$ ]]; then
    UPDATE_CMD="$UPDATE_CMD --health-check-grace-period-seconds 300"
  fi
  
  eval "$UPDATE_CMD"
  echo "Service updated successfully."
fi

# Clean up temporary file
rm -f ecs/service-definition.tmp.json

echo ""
echo "========================================"
echo "Waiting for Service Stability"
echo "========================================"
echo "This may take several minutes..."

aws ecs wait services-stable \
  --cluster "$CLUSTER_NAME" \
  --services "$SERVICE_NAME" \
  --region "$AWS_REGION"

echo ""
echo "========================================"
echo "Deployment Completed Successfully"
echo "========================================"
echo ""
echo "Service Details:"
aws ecs describe-services \
  --cluster "$CLUSTER_NAME" \
  --services "$SERVICE_NAME" \
  --region "$AWS_REGION" \
  --query 'services[0].[serviceName,status,runningCount,desiredCount]' \
  --output table

echo ""
echo "CloudWatch Logs:"
echo "  Log Group: $LOG_GROUP"
echo "  View logs: https://console.aws.amazon.com/cloudwatch/home?region=$AWS_REGION#logsV2:log-groups/log-group/$(echo $LOG_GROUP | sed 's|/|$252F|g')"

if [[ "$NEED_LB" =~ ^[Yy]$ ]]; then
  echo ""
  echo "Application URL:"
  echo "  http://$ALB_DNS"
  echo ""
  echo "Note: It may take a few minutes for the target group health checks to pass."
fi

echo ""
echo "Deployment complete!"
echo ""