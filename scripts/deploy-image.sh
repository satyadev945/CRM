#!/bin/bash

# Deploy to AWS ECS Fargate Script for CRM Application
# This script deploys the Docker image to AWS ECS Fargate

set -e
set -o pipefail

echo "=========================================="
echo "CRM Application - ECS Fargate Deployment"
echo "=========================================="
echo ""

# Project configuration
PROJECT_NAME="crm-045"
TASK_FAMILY="crm-045-task"
SERVICE_NAME="crm-045-service"

echo "Project: $PROJECT_NAME"
echo "Task Family: $TASK_FAMILY"
echo "Service Name: $SERVICE_NAME"
echo ""

# Prompt for AWS configuration
echo "=== AWS Configuration ==="
read -p "Enter AWS region (e.g., us-east-1): " AWS_REGION
read -p "Enter ECS cluster name: " CLUSTER_NAME

echo ""
echo "Checking if ECS cluster exists..."
aws ecs describe-clusters --clusters "$CLUSTER_NAME" --region "$AWS_REGION" >/dev/null 2>&1 || {
    echo "Cluster does not exist. Creating ECS cluster: $CLUSTER_NAME"
    aws ecs create-cluster --cluster-name "$CLUSTER_NAME" --region "$AWS_REGION"
    echo "ECS cluster created successfully"
}
echo ""

# Get AWS Account ID
echo "Retrieving AWS Account ID..."
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo "AWS Account ID: $ACCOUNT_ID"
echo ""

# Prompt for network configuration
echo "=== Network Configuration ==="
read -p "Enter VPC ID: " VPC_ID
read -p "Enter Subnet IDs (comma-separated, at least 2): " SUBNETS_INPUT
read -p "Enter Security Group ID: " SECURITY_GROUP

# Parse subnets
IFS=',' read -ra SUBNET_ARRAY <<< "$SUBNETS_INPUT"
SUBNET_1=$(echo "${SUBNET_ARRAY[0]}" | xargs)
SUBNET_2=$(echo "${SUBNET_ARRAY[1]}" | xargs)

echo ""
echo "VPC: $VPC_ID"
echo "Subnet 1: $SUBNET_1"
echo "Subnet 2: $SUBNET_2"
echo "Security Group: $SECURITY_GROUP"
echo ""

# Prompt for Docker image URI
echo "=== Docker Image Configuration ==="
read -p "Enter Docker image URI (e.g., 123456789.dkr.ecr.us-east-1.amazonaws.com/crm-045:latest): " IMAGE_URI
echo "Image URI: $IMAGE_URI"
echo ""

# Prompt for database configuration
echo "=== Database Configuration ==="
read -p "Enter database host (e.g., mydb.123456.us-east-1.rds.amazonaws.com): " DB_HOST
read -p "Enter database username: " DB_USERNAME
read -sp "Enter database password: " DB_PASSWORD
echo ""
echo ""

# Load balancer configuration
echo "=== Load Balancer Configuration ==="
read -p "Do you need a load balancer for this service? (y/n): " NEED_LB

if [[ "$NEED_LB" =~ ^[Yy]$ ]]; then
    echo ""
    echo "Creating Application Load Balancer and Target Group..."
    
    # Create target group with ip target type (required for Fargate)
    TG_NAME="crm-045-tg-$(date +%s)"
    echo "Creating target group: $TG_NAME"
    
    TARGET_GROUP_ARN=$(aws elbv2 create-target-group \
        --name "$TG_NAME" \
        --protocol HTTP \
        --port 8080 \
        --vpc-id "$VPC_ID" \
        --target-type ip \
        --health-check-enabled \
        --health-check-protocol HTTP \
        --health-check-path "/appinfo/health" \
        --health-check-interval-seconds 30 \
        --health-check-timeout-seconds 5 \
        --healthy-threshold-count 2 \
        --unhealthy-threshold-count 3 \
        --region "$AWS_REGION" \
        --query 'TargetGroups[0].TargetGroupArn' \
        --output text)
    
    echo "Target Group created: $TARGET_GROUP_ARN"
    
    # Check if ALB already exists
    ALB_NAME="crm-045-alb"
    ALB_ARN=$(aws elbv2 describe-load-balancers \
        --names "$ALB_NAME" \
        --region "$AWS_REGION" \
        --query 'LoadBalancers[0].LoadBalancerArn' \
        --output text 2>/dev/null || echo "")
    
    if [ -z "$ALB_ARN" ] || [ "$ALB_ARN" == "None" ]; then
        echo "Creating Application Load Balancer: $ALB_NAME"
        
        ALB_ARN=$(aws elbv2 create-load-balancer \
            --name "$ALB_NAME" \
            --subnets "$SUBNET_1" "$SUBNET_2" \
            --security-groups "$SECURITY_GROUP" \
            --scheme internet-facing \
            --type application \
            --ip-address-type ipv4 \
            --region "$AWS_REGION" \
            --query 'LoadBalancers[0].LoadBalancerArn' \
            --output text)
        
        echo "Application Load Balancer created: $ALB_ARN"
        
        # Get ALB DNS name
        ALB_DNS=$(aws elbv2 describe-load-balancers \
            --load-balancer-arns "$ALB_ARN" \
            --region "$AWS_REGION" \
            --query 'LoadBalancers[0].DNSName' \
            --output text)
        
        # Create listener
        echo "Creating ALB listener..."
        aws elbv2 create-listener \
            --load-balancer-arn "$ALB_ARN" \
            --protocol HTTP \
            --port 80 \
            --default-actions Type=forward,TargetGroupArn="$TARGET_GROUP_ARN" \
            --region "$AWS_REGION" >/dev/null
        
        echo "ALB listener created"
        echo "ALB DNS Name: $ALB_DNS"
    else
        echo "Using existing Application Load Balancer: $ALB_ARN"
        
        # Get ALB DNS name
        ALB_DNS=$(aws elbv2 describe-load-balancers \
            --load-balancer-arns "$ALB_ARN" \
            --region "$AWS_REGION" \
            --query 'LoadBalancers[0].DNSName' \
            --output text)
        
        echo "ALB DNS Name: $ALB_DNS"
    fi
    
    echo ""
else
    echo "Skipping load balancer configuration"
    TARGET_GROUP_ARN=""
fi

# Create CloudWatch log group
echo "=== CloudWatch Logs Configuration ==="
LOG_GROUP="/ecs/crm-045"
echo "Creating CloudWatch log group: $LOG_GROUP"
aws logs create-log-group --log-group-name "$LOG_GROUP" --region "$AWS_REGION" 2>/dev/null || echo "Log group already exists"
echo ""

# Prepare task definition
echo "=== Preparing Task Definition ==="
TASK_DEF_FILE="../ecs/task-definition.json"
TASK_DEF_TEMP="/tmp/task-definition-$$.json"

cp "$TASK_DEF_FILE" "$TASK_DEF_TEMP"

# Replace placeholders
sed -i "s|{{IMAGE_URI}}|$IMAGE_URI|g" "$TASK_DEF_TEMP"
sed -i "s|{{AWS_REGION}}|$AWS_REGION|g" "$TASK_DEF_TEMP"
sed -i "s|{{ACCOUNT_ID}}|$ACCOUNT_ID|g" "$TASK_DEF_TEMP"
sed -i "s|{{DB_HOST}}|$DB_HOST|g" "$TASK_DEF_TEMP"
sed -i "s|{{DB_USERNAME}}|$DB_USERNAME|g" "$TASK_DEF_TEMP"
sed -i "s|{{DB_PASSWORD}}|$DB_PASSWORD|g" "$TASK_DEF_TEMP"

echo "Task definition prepared"
echo ""

# Register task definition
echo "=== Registering Task Definition ==="
TASK_DEF_ARN=$(aws ecs register-task-definition \
    --cli-input-json file://"$TASK_DEF_TEMP" \
    --region "$AWS_REGION" \
    --query 'taskDefinition.taskDefinitionArn' \
    --output text)

echo "Task definition registered: $TASK_DEF_ARN"
echo ""

# Clean up temp file
rm -f "$TASK_DEF_TEMP"

# Prepare service definition
echo "=== Preparing Service Definition ==="
SERVICE_DEF_FILE="../ecs/service-definition.json"
SERVICE_DEF_TEMP="/tmp/service-definition-$$.json"

cp "$SERVICE_DEF_FILE" "$SERVICE_DEF_TEMP"

# Replace placeholders
sed -i "s|{{CLUSTER_NAME}}|$CLUSTER_NAME|g" "$SERVICE_DEF_TEMP"
sed -i "s|{{SUBNET_1}}|$SUBNET_1|g" "$SERVICE_DEF_TEMP"
sed -i "s|{{SUBNET_2}}|$SUBNET_2|g" "$SERVICE_DEF_TEMP"
sed -i "s|{{SECURITY_GROUP}}|$SECURITY_GROUP|g" "$SERVICE_DEF_TEMP"

# Handle load balancer configuration
if [ -z "$TARGET_GROUP_ARN" ]; then
    # Remove loadBalancers section if no load balancer
    jq 'del(.loadBalancers) | del(.healthCheckGracePeriodSeconds)' "$SERVICE_DEF_TEMP" > "${SERVICE_DEF_TEMP}.tmp"
    mv "${SERVICE_DEF_TEMP}.tmp" "$SERVICE_DEF_TEMP"
else
    # Replace target group ARN
    sed -i "s|{{TARGET_GROUP_ARN}}|$TARGET_GROUP_ARN|g" "$SERVICE_DEF_TEMP"
fi

echo "Service definition prepared"
echo ""

# Check if service exists
echo "=== Checking Service Status ==="
EXISTING_SERVICE=$(aws ecs describe-services \
    --cluster "$CLUSTER_NAME" \
    --services "$SERVICE_NAME" \
    --region "$AWS_REGION" \
    --query 'services[?status==`ACTIVE`].serviceName' \
    --output text 2>/dev/null || echo "")

if [ -z "$EXISTING_SERVICE" ] || [ "$EXISTING_SERVICE" == "None" ]; then
    echo "Service does not exist. Creating new service..."
    
    aws ecs create-service \
        --cli-input-json file://"$SERVICE_DEF_TEMP" \
        --region "$AWS_REGION" >/dev/null
    
    echo "Service created: $SERVICE_NAME"
else
    echo "Service exists. Updating service..."
    
    aws ecs update-service \
        --cluster "$CLUSTER_NAME" \
        --service "$SERVICE_NAME" \
        --task-definition "$TASK_DEF_ARN" \
        --desired-count 2 \
        --region "$AWS_REGION" >/dev/null
    
    echo "Service updated: $SERVICE_NAME"
fi

echo ""

# Clean up temp file
rm -f "$SERVICE_DEF_TEMP"

# Wait for service stability
echo "=== Waiting for Service Stability ==="
echo "This may take several minutes..."
aws ecs wait services-stable \
    --cluster "$CLUSTER_NAME" \
    --services "$SERVICE_NAME" \
    --region "$AWS_REGION"

echo "Service is stable"
echo ""

# Verify deployment
echo "=== Deployment Verification ==="
aws ecs describe-services \
    --cluster "$CLUSTER_NAME" \
    --services "$SERVICE_NAME" \
    --region "$AWS_REGION" \
    --query 'services[0].{ServiceName:serviceName,Status:status,DesiredCount:desiredCount,RunningCount:runningCount,TaskDefinition:taskDefinition}' \
    --output table

echo ""
echo "=========================================="
echo "DEPLOYMENT SUCCESSFUL!"
echo "=========================================="
echo ""
echo "Service Details:"
echo "  Cluster: $CLUSTER_NAME"
echo "  Service: $SERVICE_NAME"
echo "  Region: $AWS_REGION"
echo ""

if [ -n "$ALB_DNS" ]; then
    echo "Application URL:"
    echo "  http://$ALB_DNS"
    echo ""
fi

echo "CloudWatch Logs:"
echo "  Log Group: $LOG_GROUP"
echo "  Region: $AWS_REGION"
echo ""
echo "To view logs:"
echo "  aws logs tail $LOG_GROUP --follow --region $AWS_REGION"
echo ""
echo "To check service status:"
echo "  aws ecs describe-services --cluster $CLUSTER_NAME --services $SERVICE_NAME --region $AWS_REGION"
echo ""
echo "=========================================="
