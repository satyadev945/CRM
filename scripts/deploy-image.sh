#!/bin/bash

# Enable strict error handling
set -e
set -o pipefail

# Function to display colored output
print_message() {
    echo -e "\e[34m>> $1\e[0m"
}

print_error() {
    echo -e "\e[31m>> ERROR: $1\e[0m"
    exit 1
}

print_success() {
    echo -e "\e[32m>> $1\e[0m"
}

print_warning() {
    echo -e "\e[33m>> WARNING: $1\e[0m"
}

print_message "AWS ECS Fargate Deployment Script for CRM Application"
print_message "------------------------------------------------------"

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    print_error "AWS CLI is not installed. Please install it first."
fi

# Check for required files
if [ ! -f "./ecs/task-definition.json" ]; then
    print_error "Task definition file not found at ./ecs/task-definition.json"
fi

if [ ! -f "./ecs/service-definition.json" ]; then
    print_error "Service definition file not found at ./ecs/service-definition.json"
fi

# Get AWS region
read -p "Enter AWS region (e.g., us-east-1): " AWS_REGION
if [ -z "$AWS_REGION" ]; then
    print_error "AWS region cannot be empty"
fi

# Get ECS cluster name
read -p "Enter ECS cluster name (default: default): " CLUSTER_NAME
if [ -z "$CLUSTER_NAME" ]; then
    CLUSTER_NAME="default"
    print_message "Using default cluster name: default"
fi

# Get AWS account ID
print_message "Retrieving AWS account ID..."
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text 2>/dev/null || echo "")

if [ -z "$ACCOUNT_ID" ]; then
    read -p "Could not retrieve AWS account ID automatically. Please enter your AWS account ID: " ACCOUNT_ID
    if [ -z "$ACCOUNT_ID" ]; then
        print_error "AWS account ID cannot be empty"
    fi
else
    print_message "AWS account ID: $ACCOUNT_ID"
fi

# Get environment name
read -p "Enter environment name (default: production): " ENVIRONMENT
if [ -z "$ENVIRONMENT" ]; then
    ENVIRONMENT="production"
    print_message "Using default environment: production"
fi

# Get subnet information
read -p "Enter subnet ID for first AZ: " SUBNET_1
if [ -z "$SUBNET_1" ]; then
    print_error "First subnet ID cannot be empty"
fi

read -p "Enter subnet ID for second AZ: " SUBNET_2
if [ -z "$SUBNET_2" ]; then
    print_error "Second subnet ID cannot be empty"
fi

# Get security group
read -p "Enter security group ID: " SECURITY_GROUP
if [ -z "$SECURITY_GROUP" ]; then
    print_error "Security group ID cannot be empty"
fi

# Database configuration
read -p "Enter database host (default: localhost): " DATABASE_HOST
if [ -z "$DATABASE_HOST" ]; then
    DATABASE_HOST="localhost"
    print_message "Using default database host: localhost"
fi

read -p "Enter database username (default: root): " DATABASE_USER
if [ -z "$DATABASE_USER" ]; then
    DATABASE_USER="root"
    print_message "Using default database username: root"
fi

read -p "Enter database password (default: password): " DATABASE_PASSWORD
if [ -z "$DATABASE_PASSWORD" ]; then
    DATABASE_PASSWORD="password"
    print_message "Using default database password: password"
    print_warning "Using default database password in production is not recommended!"
fi

# Get Docker image URI
if [ -f "./image-uri.txt" ]; then
    DEFAULT_IMAGE_URI=$(cat ./image-uri.txt)
    read -p "Enter Docker image URI (default: $DEFAULT_IMAGE_URI): " IMAGE_URI
    if [ -z "$IMAGE_URI" ]; then
        IMAGE_URI=$DEFAULT_IMAGE_URI
        print_message "Using image URI from image-uri.txt: $IMAGE_URI"
    fi
else
    read -p "Enter Docker image URI (e.g., 123456789012.dkr.ecr.region.amazonaws.com/crm:latest): " IMAGE_URI
    if [ -z "$IMAGE_URI" ]; then
        print_error "Docker image URI cannot be empty"
    fi
fi

# Ask about load balancer
read -p "Do you want to use a load balancer? (y/n): " USE_LOAD_BALANCER

if [[ "$USE_LOAD_BALANCER" =~ ^[Yy]$ ]]; then
    print_message "Setting up with load balancer..."

    # Check if we need to create new target group
    read -p "Do you want to create a new target group? (y/n, default: y): " CREATE_TARGET_GROUP

    if [[ "$CREATE_TARGET_GROUP" =~ ^[Nn]$ ]]; then
        read -p "Enter existing target group ARN: " TARGET_GROUP_ARN
        if [ -z "$TARGET_GROUP_ARN" ]; then
            print_error "Target group ARN cannot be empty"
        fi
    else
        # Create target group
        print_message "Creating target group..."

        # Get VPC ID for target group
        read -p "Enter VPC ID for target group: " VPC_ID
        if [ -z "$VPC_ID" ]; then
            print_error "VPC ID cannot be empty"
        fi

        TARGET_GROUP_NAME="crm-tg-$(date +%Y%m%d%H%M%S)"
        print_message "Creating target group: $TARGET_GROUP_NAME"

        TARGET_GROUP_RESULT=$(aws elbv2 create-target-group \
            --name "$TARGET_GROUP_NAME" \
            --protocol HTTP \
            --port 8080 \
            --vpc-id "$VPC_ID" \
            --target-type ip \
            --health-check-path "/appinfo/health" \
            --health-check-interval-seconds 30 \
            --health-check-timeout-seconds 5 \
            --healthy-threshold-count 2 \
            --unhealthy-threshold-count 3 \
            --region "$AWS_REGION")

        TARGET_GROUP_ARN=$(echo $TARGET_GROUP_RESULT | jq -r '.TargetGroups[0].TargetGroupArn')
        print_message "Created target group: $TARGET_GROUP_ARN"

        # Create or use existing load balancer
        read -p "Do you want to create a new load balancer? (y/n, default: n): " CREATE_LOAD_BALANCER

        if [[ "$CREATE_LOAD_BALANCER" =~ ^[Yy]$ ]]; then
            LB_NAME="crm-alb-$(date +%Y%m%d%H%M%S)"
            print_message "Creating load balancer: $LB_NAME"

            LB_RESULT=$(aws elbv2 create-load-balancer \
                --name "$LB_NAME" \
                --subnets "$SUBNET_1" "$SUBNET_2" \
                --security-groups "$SECURITY_GROUP" \
                --scheme internet-facing \
                --type application \
                --region "$AWS_REGION")

            LOAD_BALANCER_ARN=$(echo $LB_RESULT | jq -r '.LoadBalancers[0].LoadBalancerArn')
            print_message "Created load balancer: $LOAD_BALANCER_ARN"

            # Create listener
            print_message "Creating listener for load balancer..."
            LISTENER_RESULT=$(aws elbv2 create-listener \
                --load-balancer-arn "$LOAD_BALANCER_ARN" \
                --protocol HTTP \
                --port 80 \
                --default-actions Type=forward,TargetGroupArn=$TARGET_GROUP_ARN \
                --region "$AWS_REGION")

            print_message "Created listener for load balancer"

            # Get load balancer DNS name
            LB_DNS=$(aws elbv2 describe-load-balancers \
                --load-balancer-arns "$LOAD_BALANCER_ARN" \
                --region "$AWS_REGION" \
                --query 'LoadBalancers[0].DNSName' \
                --output text)

            print_message "Load balancer DNS: $LB_DNS"
            echo "Load balancer DNS: $LB_DNS" > ./lb-dns.txt
        else
            read -p "Enter existing load balancer ARN: " LOAD_BALANCER_ARN
            if [ -z "$LOAD_BALANCER_ARN" ]; then
                print_error "Load balancer ARN cannot be empty"
            fi

            # Create listener
            print_message "Creating listener for existing load balancer..."
            LISTENER_RESULT=$(aws elbv2 create-listener \
                --load-balancer-arn "$LOAD_BALANCER_ARN" \
                --protocol HTTP \
                --port 80 \
                --default-actions Type=forward,TargetGroupArn=$TARGET_GROUP_ARN \
                --region "$AWS_REGION")

            print_message "Created listener for existing load balancer"
        fi
    fi
else
    print_message "Not using load balancer, updating service definition..."
    # Remove the loadBalancers section from the service definition
    TMP_FILE=$(mktemp)
    jq 'del(.loadBalancers)' ./ecs/service-definition.json > "$TMP_FILE" && mv "$TMP_FILE" ./ecs/service-definition.json
    # Also remove healthCheckGracePeriodSeconds as it's only applicable with loadBalancer
    TMP_FILE=$(mktemp)
    jq 'del(.healthCheckGracePeriodSeconds)' ./ecs/service-definition.json > "$TMP_FILE" && mv "$TMP_FILE" ./ecs/service-definition.json
    TARGET_GROUP_ARN=""
fi

# Create or use existing ECS cluster
print_message "Checking if ECS cluster exists: $CLUSTER_NAME"
CLUSTER_CHECK=$(aws ecs describe-clusters --clusters "$CLUSTER_NAME" --region "$AWS_REGION" | jq -r '.clusters | length')

if [ "$CLUSTER_CHECK" == "0" ]; then
    print_message "Creating ECS cluster: $CLUSTER_NAME"
    aws ecs create-cluster --cluster-name "$CLUSTER_NAME" --region "$AWS_REGION"
else
    print_message "Using existing ECS cluster: $CLUSTER_NAME"
fi

# Create CloudWatch log group if it doesn't exist
print_message "Checking if CloudWatch log group exists: /ecs/crm"
LOG_GROUP_CHECK=$(aws logs describe-log-groups --log-group-name-prefix "/ecs/crm" --region "$AWS_REGION" | jq -r '.logGroups | length')

if [ "$LOG_GROUP_CHECK" == "0" ]; then
    print_message "Creating CloudWatch log group: /ecs/crm"
    aws logs create-log-group --log-group-name "/ecs/crm" --region "$AWS_REGION"
else
    print_message "Using existing CloudWatch log group: /ecs/crm"
fi

# Replace placeholders in task definition
print_message "Updating task definition file with configuration..."
TMP_FILE=$(mktemp)
cat ./ecs/task-definition.json | \
    sed "s|{{ACCOUNT_ID}}|$ACCOUNT_ID|g" | \
    sed "s|{{AWS_REGION}}|$AWS_REGION|g" | \
    sed "s|{{IMAGE_URI}}|$IMAGE_URI|g" | \
    sed "s|{{DATABASE_HOST}}|$DATABASE_HOST|g" | \
    sed "s|{{DATABASE_USER}}|$DATABASE_USER|g" | \
    sed "s|{{DATABASE_PASSWORD}}|$DATABASE_PASSWORD|g" | \
    sed "s|{{ENVIRONMENT}}|$ENVIRONMENT|g" > "$TMP_FILE"
mv "$TMP_FILE" ./ecs/task-definition.json

# Replace placeholders in service definition
print_message "Updating service definition file with configuration..."
TMP_FILE=$(mktemp)
cat ./ecs/service-definition.json | \
    sed "s|{{CLUSTER_NAME}}|$CLUSTER_NAME|g" | \
    sed "s|{{SUBNET_1}}|$SUBNET_1|g" | \
    sed "s|{{SUBNET_2}}|$SUBNET_2|g" | \
    sed "s|{{SECURITY_GROUP}}|$SECURITY_GROUP|g" | \
    sed "s|{{ENVIRONMENT}}|$ENVIRONMENT|g" > "$TMP_FILE"
mv "$TMP_FILE" ./ecs/service-definition.json

# Replace target group ARN if using load balancer
if [[ "$USE_LOAD_BALANCER" =~ ^[Yy]$ ]]; then
    print_message "Updating target group ARN in service definition..."
    TMP_FILE=$(mktemp)
    cat ./ecs/service-definition.json | sed "s|{{TARGET_GROUP_ARN}}|$TARGET_GROUP_ARN|g" > "$TMP_FILE"
    mv "$TMP_FILE" ./ecs/service-definition.json
fi

# Register task definition
print_message "Registering ECS task definition..."
TASK_DEF_RESULT=$(aws ecs register-task-definition \
    --cli-input-json file://ecs/task-definition.json \
    --region "$AWS_REGION")

TASK_DEFINITION_ARN=$(echo $TASK_DEF_RESULT | jq -r '.taskDefinition.taskDefinitionArn')
print_message "Registered task definition: $TASK_DEFINITION_ARN"

# Check if service exists
print_message "Checking if service exists: crm-service"
SERVICE_CHECK=$(aws ecs describe-services \
    --cluster "$CLUSTER_NAME" \
    --services "crm-service" \
    --region "$AWS_REGION" \
    --query 'services[0].serviceName' \
    --output text 2>/dev/null || echo "")

if [ "$SERVICE_CHECK" == "crm-service" ]; then
    print_message "Updating existing ECS service: crm-service"

    # Update existing service
    aws ecs update-service \
        --cluster "$CLUSTER_NAME" \
        --service "crm-service" \
        --task-definition "$TASK_DEFINITION_ARN" \
        --region "$AWS_REGION"
else
    print_message "Creating new ECS service: crm-service"

    # Create new service
    aws ecs create-service \
        --cli-input-json file://ecs/service-definition.json \
        --region "$AWS_REGION"
fi

# Wait for service to stabilize
print_message "Waiting for service to stabilize..."
aws ecs wait services-stable \
    --cluster "$CLUSTER_NAME" \
    --services "crm-service" \
    --region "$AWS_REGION"

# Print service details
print_message "Deployment completed successfully!"
SERVICE_DETAILS=$(aws ecs describe-services \
    --cluster "$CLUSTER_NAME" \
    --services "crm-service" \
    --region "$AWS_REGION")

RUNNING_COUNT=$(echo $SERVICE_DETAILS | jq -r '.services[0].runningCount')
DESIRED_COUNT=$(echo $SERVICE_DETAILS | jq -r '.services[0].desiredCount')

print_success "CRM Application deployed successfully to ECS Fargate!"
echo "  Cluster:       $CLUSTER_NAME"
echo "  Service:       crm-service"
echo "  Running tasks: $RUNNING_COUNT / $DESIRED_COUNT"
echo "  Task definition: $TASK_DEFINITION_ARN"
echo "  CloudWatch logs: /ecs/crm"

# Print load balancer info if applicable
if [[ "$USE_LOAD_BALANCER" =~ ^[Yy]$ ]] && [ -f "./lb-dns.txt" ]; then
    LB_DNS=$(cat ./lb-dns.txt | cut -d' ' -f3)
    echo "  Application URL: http://$LB_DNS"
    print_message "The application may take a few minutes to become available at the URL."
fi

print_message "For detailed logs, check CloudWatch logs at: /ecs/crm"