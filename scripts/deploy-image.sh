#!/bin/bash
set -e
set -o pipefail

echo "====================================="
echo "AWS ECS Fargate Deployment Script"
echo "====================================="
echo ""

# Configuration
PROJECT_NAME="container-testrah"
TASK_FAMILY="${PROJECT_NAME}-task"
SERVICE_NAME="${PROJECT_NAME}-service"

# Prompt for AWS configuration
read -p "Enter AWS Region (e.g., us-east-1): " AWS_REGION
export AWS_DEFAULT_REGION="$AWS_REGION"

read -p "Enter ECS Cluster Name: " CLUSTER_NAME

# Check if cluster exists, create if not
echo ""
echo "Checking ECS cluster..."
aws ecs describe-clusters --clusters "$CLUSTER_NAME" --region "$AWS_REGION" >/dev/null 2>&1 || {
    echo "Cluster does not exist. Creating ECS cluster: $CLUSTER_NAME"
    aws ecs create-cluster --cluster-name "$CLUSTER_NAME" --region "$AWS_REGION"
    echo "Cluster created successfully"
}

# Get AWS Account ID
echo "Retrieving AWS Account ID..."
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo "Account ID: $ACCOUNT_ID"

# Prompt for network configuration
echo ""
echo "=== Network Configuration ==="
read -p "Enter VPC ID: " VPC_ID
read -p "Enter Subnet IDs (comma-separated, at least 2): " SUBNETS_INPUT
read -p "Enter Security Group ID: " SECURITY_GROUP

# Parse subnets
IFS=',' read -ra SUBNET_ARRAY <<< "$SUBNETS_INPUT"
SUBNET_1=$(echo "${SUBNET_ARRAY[0]}" | xargs)
SUBNET_2=$(echo "${SUBNET_ARRAY[1]:-${SUBNET_ARRAY[0]}}" | xargs)

echo "Subnet 1: $SUBNET_1"
echo "Subnet 2: $SUBNET_2"

# Prompt for container image
echo ""
read -p "Enter ECR Image URI (e.g., 123456789.dkr.ecr.us-east-1.amazonaws.com/app:latest): " IMAGE_URI

# Load balancer configuration
echo ""
read -p "Do you need a load balancer for this service? (y/n): " NEED_LB

if [[ "$NEED_LB" =~ ^[Yy]$ ]]; then
    echo ""
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
        --ip-address-type ipv4 \
        --region "$AWS_REGION" \
        --query 'LoadBalancers[0].LoadBalancerArn' \
        --output text 2>/dev/null || echo "")
    
    if [ -z "$ALB_ARN" ]; then
        echo "ALB may already exist or creation failed. Attempting to describe existing ALB..."
        ALB_ARN=$(aws elbv2 describe-load-balancers \
            --names "$ALB_NAME" \
            --region "$AWS_REGION" \
            --query 'LoadBalancers[0].LoadBalancerArn' \
            --output text 2>/dev/null || echo "")
    fi
    
    if [ -z "$ALB_ARN" ]; then
        echo "ERROR: Failed to create or find ALB"
        exit 1
    fi
    
    echo "ALB ARN: $ALB_ARN"
    
    # Get ALB DNS name
    ALB_DNS=$(aws elbv2 describe-load-balancers \
        --load-balancer-arns "$ALB_ARN" \
        --region "$AWS_REGION" \
        --query 'LoadBalancers[0].DNSName' \
        --output text)
    
    # Create Target Group (ip type for Fargate)
    TG_NAME="${PROJECT_NAME}-tg"
    echo "Creating Target Group: $TG_NAME"
    TARGET_GROUP_ARN=$(aws elbv2 create-target-group \
        --name "$TG_NAME" \
        --protocol HTTP \
        --port 8080 \
        --vpc-id "$VPC_ID" \
        --target-type ip \
        --health-check-enabled \
        --health-check-protocol HTTP \
        --health-check-path "/actuator/health" \
        --health-check-interval-seconds 30 \
        --health-check-timeout-seconds 5 \
        --healthy-threshold-count 2 \
        --unhealthy-threshold-count 3 \
        --region "$AWS_REGION" \
        --query 'TargetGroups[0].TargetGroupArn' \
        --output text 2>/dev/null || echo "")
    
    if [ -z "$TARGET_GROUP_ARN" ]; then
        echo "Target Group may already exist. Attempting to describe existing TG..."
        TARGET_GROUP_ARN=$(aws elbv2 describe-target-groups \
            --names "$TG_NAME" \
            --region "$AWS_REGION" \
            --query 'TargetGroups[0].TargetGroupArn' \
            --output text 2>/dev/null || echo "")
    fi
    
    if [ -z "$TARGET_GROUP_ARN" ]; then
        echo "ERROR: Failed to create or find Target Group"
        exit 1
    fi
    
    echo "Target Group ARN: $TARGET_GROUP_ARN"
    
    # Create listener
    echo "Creating ALB Listener..."
    aws elbv2 create-listener \
        --load-balancer-arn "$ALB_ARN" \
        --protocol HTTP \
        --port 80 \
        --default-actions Type=forward,TargetGroupArn="$TARGET_GROUP_ARN" \
        --region "$AWS_REGION" >/dev/null 2>&1 || echo "Listener may already exist"
    
    USE_LOAD_BALANCER=true
else
    USE_LOAD_BALANCER=false
    TARGET_GROUP_ARN=""
fi

# Create CloudWatch Log Group
echo ""
echo "Creating CloudWatch Log Group..."
LOG_GROUP="/ecs/${PROJECT_NAME}"
aws logs create-log-group --log-group-name "$LOG_GROUP" --region "$AWS_REGION" 2>/dev/null || echo "Log group already exists"

# Prepare task definition JSON
echo ""
echo "Preparing task definition..."
cp ecs/task-definition.json /tmp/task-definition.json
sed -i "s|{{ACCOUNT_ID}}|$ACCOUNT_ID|g" /tmp/task-definition.json
sed -i "s|{{AWS_REGION}}|$AWS_REGION|g" /tmp/task-definition.json
sed -i "s|{{IMAGE_URI}}|$IMAGE_URI|g" /tmp/task-definition.json

# Register task definition
echo "Registering task definition..."
TASK_DEF_ARN=$(aws ecs register-task-definition \
    --cli-input-json file:///tmp/task-definition.json \
    --region "$AWS_REGION" \
    --query 'taskDefinition.taskDefinitionArn' \
    --output text)

if [ -z "$TASK_DEF_ARN" ]; then
    echo "ERROR: Failed to register task definition"
    exit 1
fi

echo "Task Definition ARN: $TASK_DEF_ARN"

# Prepare service definition JSON
echo ""
echo "Preparing service definition..."
cp ecs/service-definition.json /tmp/service-definition.json
sed -i "s|{{CLUSTER_NAME}}|$CLUSTER_NAME|g" /tmp/service-definition.json
sed -i "s|{{SUBNET_1}}|$SUBNET_1|g" /tmp/service-definition.json
sed -i "s|{{SUBNET_2}}|$SUBNET_2|g" /tmp/service-definition.json
sed -i "s|{{SECURITY_GROUP}}|$SECURITY_GROUP|g" /tmp/service-definition.json

if [ "$USE_LOAD_BALANCER" = false ]; then
    # Remove loadBalancers section from service definition
    sed -i '/"loadBalancers"/,/\],/d' /tmp/service-definition.json
    sed -i '/"healthCheckGracePeriodSeconds"/d' /tmp/service-definition.json
else
    sed -i "s|{{TARGET_GROUP_ARN}}|$TARGET_GROUP_ARN|g" /tmp/service-definition.json
fi

# Check if service exists
echo "Checking if service exists..."
EXISTING_SERVICE=$(aws ecs describe-services \
    --cluster "$CLUSTER_NAME" \
    --services "$SERVICE_NAME" \
    --region "$AWS_REGION" \
    --query 'services[0].serviceName' \
    --output text 2>/dev/null || echo "None")

if [ "$EXISTING_SERVICE" = "None" ] || [ -z "$EXISTING_SERVICE" ]; then
    echo "Service does not exist. Creating new service..."
    aws ecs create-service \
        --cli-input-json file:///tmp/service-definition.json \
        --region "$AWS_REGION"
else
    echo "Service exists. Updating service with new task definition..."
    aws ecs update-service \
        --cluster "$CLUSTER_NAME" \
        --service "$SERVICE_NAME" \
        --task-definition "$TASK_DEF_ARN" \
        --force-new-deployment \
        --region "$AWS_REGION"
fi

# Wait for service stability
echo ""
echo "Waiting for service to become stable (this may take several minutes)..."
aws ecs wait services-stable \
    --cluster "$CLUSTER_NAME" \
    --services "$SERVICE_NAME" \
    --region "$AWS_REGION"

# Verify deployment
echo ""
echo "====================================="
echo "Deployment Completed Successfully"
echo "====================================="
echo ""

aws ecs describe-services \
    --cluster "$CLUSTER_NAME" \
    --services "$SERVICE_NAME" \
    --region "$AWS_REGION" \
    --query 'services[0].[serviceName,status,runningCount,desiredCount]' \
    --output table

echo ""
echo "Service Name: $SERVICE_NAME"
echo "Cluster: $CLUSTER_NAME"
echo "Task Definition: $TASK_DEF_ARN"
echo "CloudWatch Logs: $LOG_GROUP"

if [ "$USE_LOAD_BALANCER" = true ]; then
    echo "Load Balancer DNS: $ALB_DNS"
    echo "Access your application at: http://$ALB_DNS"
fi

echo ""
echo "To view logs: aws logs tail $LOG_GROUP --follow --region $AWS_REGION"
echo ""