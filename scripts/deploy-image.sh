#!/bin/bash
set -e
set -o pipefail

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "${GREEN}=== CRM Container - AWS ECS Fargate Deployment ===${NC}"
echo ""

# Prompt for deployment configuration
read -p "Enter AWS Region (e.g., us-east-1): " AWS_REGION
read -p "Enter ECS Cluster Name: " CLUSTER_NAME
read -p "Enter VPC ID: " VPC_ID
read -p "Enter Subnet IDs (comma-separated, at least 2): " SUBNETS_INPUT
read -p "Enter Security Group ID: " SECURITY_GROUP
read -p "Enter ECR Image URI (e.g., 123456789.dkr.ecr.us-east-1.amazonaws.com/crm-container:latest): " IMAGE_URI

# Convert comma-separated subnets to array
IFS=',' read -ra SUBNETS <<< "$SUBNETS_INPUT"
SUBNET_1=${SUBNETS[0]}
SUBNET_2=${SUBNETS[1]}

# Trim whitespace
SUBNET_1=$(echo $SUBNET_1 | xargs)
SUBNET_2=$(echo $SUBNET_2 | xargs)

echo ""
echo "${YELLOW}Getting AWS Account ID...${NC}"
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo "Account ID: $ACCOUNT_ID"

# Check if cluster exists, create if not
echo "${YELLOW}Checking if ECS cluster exists...${NC}"
aws ecs describe-clusters --clusters $CLUSTER_NAME --region $AWS_REGION >/dev/null 2>&1 || {
    echo "${YELLOW}Creating ECS cluster: $CLUSTER_NAME${NC}"
    aws ecs create-cluster --cluster-name $CLUSTER_NAME --region $AWS_REGION
}

# Ask about load balancer
echo ""
read -p "Do you need a load balancer for this service? (y/n): " USE_LB

if [ "$USE_LB" = "y" ] || [ "$USE_LB" = "Y" ]; then
    echo "${YELLOW}Creating Application Load Balancer and Target Group...${NC}"
    
    # Create ALB
    ALB_NAME="crm-container-alb"
    echo "Creating Application Load Balancer: $ALB_NAME"
    ALB_ARN=$(aws elbv2 create-load-balancer \
        --name $ALB_NAME \
        --subnets $SUBNET_1 $SUBNET_2 \
        --security-groups $SECURITY_GROUP \
        --region $AWS_REGION \
        --query 'LoadBalancers[0].LoadBalancerArn' \
        --output text)
    
    echo "ALB ARN: $ALB_ARN"
    
    # Get ALB DNS Name
    ALB_DNS=$(aws elbv2 describe-load-balancers \
        --load-balancer-arns $ALB_ARN \
        --region $AWS_REGION \
        --query 'LoadBalancers[0].DNSName' \
        --output text)
    
    # Create Target Group
    TG_NAME="crm-container-tg"
    echo "Creating Target Group: $TG_NAME"
    TARGET_GROUP_ARN=$(aws elbv2 create-target-group \
        --name $TG_NAME \
        --protocol HTTP \
        --port 8080 \
        --vpc-id $VPC_ID \
        --target-type ip \
        --health-check-enabled \
        --health-check-path /actuator/health \
        --health-check-interval-seconds 30 \
        --health-check-timeout-seconds 5 \
        --healthy-threshold-count 2 \
        --unhealthy-threshold-count 3 \
        --region $AWS_REGION \
        --query 'TargetGroups[0].TargetGroupArn' \
        --output text)
    
    echo "Target Group ARN: $TARGET_GROUP_ARN"
    
    # Create Listener
    echo "Creating ALB Listener on port 80..."
    aws elbv2 create-listener \
        --load-balancer-arn $ALB_ARN \
        --protocol HTTP \
        --port 80 \
        --default-actions Type=forward,TargetGroupArn=$TARGET_GROUP_ARN \
        --region $AWS_REGION
    
    LOAD_BALANCER_CONFIG=true
else
    echo "${YELLOW}Skipping load balancer configuration${NC}"
    LOAD_BALANCER_CONFIG=false
fi

# Create CloudWatch Log Group
echo "${YELLOW}Creating CloudWatch Log Group...${NC}"
aws logs create-log-group --log-group-name "/ecs/crm-container" --region $AWS_REGION 2>/dev/null || echo "Log group already exists"

# Replace placeholders in task definition
echo "${YELLOW}Preparing ECS Task Definition...${NC}"
cp ecs/task-definition.json ecs/task-definition-tmp.json

sed -i "s|{{IMAGE_URI}}|$IMAGE_URI|g" ecs/task-definition-tmp.json
sed -i "s|{{AWS_REGION}}|$AWS_REGION|g" ecs/task-definition-tmp.json
sed -i "s|{{ACCOUNT_ID}}|$ACCOUNT_ID|g" ecs/task-definition-tmp.json

# Register task definition
echo "${YELLOW}Registering ECS Task Definition...${NC}"
TASK_DEF_ARN=$(aws ecs register-task-definition \
    --cli-input-json file://ecs/task-definition-tmp.json \
    --region $AWS_REGION \
    --query 'taskDefinition.taskDefinitionArn' \
    --output text)

echo "Task Definition ARN: $TASK_DEF_ARN"

# Prepare service definition
cp ecs/service-definition.json ecs/service-definition-tmp.json

sed -i "s|{{CLUSTER_NAME}}|$CLUSTER_NAME|g" ecs/service-definition-tmp.json
sed -i "s|{{SUBNET_1}}|$SUBNET_1|g" ecs/service-definition-tmp.json
sed -i "s|{{SUBNET_2}}|$SUBNET_2|g" ecs/service-definition-tmp.json
sed -i "s|{{SECURITY_GROUP}}|$SECURITY_GROUP|g" ecs/service-definition-tmp.json

if [ "$LOAD_BALANCER_CONFIG" = true ]; then
    sed -i "s|{{TARGET_GROUP_ARN}}|$TARGET_GROUP_ARN|g" ecs/service-definition-tmp.json
else
    # Remove loadBalancers section if no LB
    jq 'del(.loadBalancers)' ecs/service-definition-tmp.json > ecs/service-definition-tmp2.json
    mv ecs/service-definition-tmp2.json ecs/service-definition-tmp.json
fi

# Check if service exists
SERVICE_NAME="crm-container-service"
echo "${YELLOW}Checking if ECS service exists...${NC}"
EXISTING_SERVICE=$(aws ecs describe-services \
    --cluster $CLUSTER_NAME \
    --services $SERVICE_NAME \
    --region $AWS_REGION \
    --query 'services[?status==`ACTIVE`].serviceName' \
    --output text)

if [ -z "$EXISTING_SERVICE" ]; then
    echo "${YELLOW}Creating ECS Service...${NC}"
    aws ecs create-service \
        --cli-input-json file://ecs/service-definition-tmp.json \
        --region $AWS_REGION
else
    echo "${YELLOW}Updating existing ECS Service...${NC}"
    aws ecs update-service \
        --cluster $CLUSTER_NAME \
        --service $SERVICE_NAME \
        --task-definition $TASK_DEF_ARN \
        --force-new-deployment \
        --region $AWS_REGION
fi

echo "${YELLOW}Waiting for service to become stable...${NC}"
aws ecs wait services-stable --cluster $CLUSTER_NAME --services $SERVICE_NAME --region $AWS_REGION

echo ""
echo "${GREEN}✓ Deployment completed successfully!${NC}"
echo ""
echo "${GREEN}Service Details:${NC}"
aws ecs describe-services --cluster $CLUSTER_NAME --services $SERVICE_NAME --region $AWS_REGION --query 'services[0].{ServiceName:serviceName,Status:status,DesiredCount:desiredCount,RunningCount:runningCount}'

if [ "$LOAD_BALANCER_CONFIG" = true ]; then
    echo ""
    echo "${GREEN}Load Balancer DNS:${NC}"
    echo "http://$ALB_DNS"
fi

echo ""
echo "${GREEN}CloudWatch Logs:${NC}"
echo "Log Group: /ecs/crm-container"
echo "Region: $AWS_REGION"

# Cleanup temp files
rm -f ecs/task-definition-tmp.json ecs/service-definition-tmp.json

echo ""
echo "${YELLOW}Deployment complete!${NC}"
