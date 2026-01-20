#!/bin/bash
set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "${GREEN}=== CRM Container - Build and Push Script ===${NC}"
echo ""

# Project name
PROJECT_NAME="crm-container"

# Prompt for registry type
echo "${YELLOW}Select container registry:${NC}"
echo "1. AWS ECR (Elastic Container Registry)"
echo "2. Docker Hub"
read -p "Enter choice (1 or 2): " REGISTRY_CHOICE

if [ "$REGISTRY_CHOICE" = "1" ]; then
    echo "${GREEN}Selected: AWS ECR${NC}"
    
    # AWS ECR Configuration
    read -p "Enter AWS Region (e.g., us-east-1): " AWS_REGION
    read -p "Enter AWS Account ID: " AWS_ACCOUNT_ID
    read -p "Enter ECR Repository Name (default: crm-container): " ECR_REPO
    ECR_REPO=${ECR_REPO:-crm-container}
    
    # Authenticate with ECR
    echo "${YELLOW}Authenticating with AWS ECR...${NC}"
    aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com
    
    if [ $? -ne 0 ]; then
        echo "${RED}ECR authentication failed. Please check your AWS credentials.${NC}"
        exit 1
    fi
    
    # Check if repository exists, create if not
    echo "${YELLOW}Checking if ECR repository exists...${NC}"
    aws ecr describe-repositories --repository-names $ECR_REPO --region $AWS_REGION >/dev/null 2>&1 || \
    {
        echo "${YELLOW}Creating ECR repository: $ECR_REPO${NC}"
        aws ecr create-repository --repository-name $ECR_REPO --region $AWS_REGION
    }
    
    REGISTRY_URL="$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com"
    
elif [ "$REGISTRY_CHOICE" = "2" ]; then
    echo "${GREEN}Selected: Docker Hub${NC}"
    
    # Docker Hub Configuration
    read -p "Enter Docker Hub Username: " DOCKER_USERNAME
    read -sp "Enter Docker Hub Password or Token: " DOCKER_PASSWORD
    echo ""
    
    # Authenticate with Docker Hub
    echo "${YELLOW}Authenticating with Docker Hub...${NC}"
    echo $DOCKER_PASSWORD | docker login --username $DOCKER_USERNAME --password-stdin
    
    if [ $? -ne 0 ]; then
        echo "${RED}Docker Hub authentication failed. Please check your credentials.${NC}"
        exit 1
    fi
    
    REGISTRY_URL="$DOCKER_USERNAME"
    ECR_REPO="$PROJECT_NAME"
    
else
    echo "${RED}Invalid choice. Exiting.${NC}"
    exit 1
fi

# Prompt for image tag
read -p "Enter image tag (default: latest): " IMAGE_TAG
IMAGE_TAG=${IMAGE_TAG:-latest}

# Sanitize image tag
IMAGE_TAG=$(echo "$IMAGE_TAG" | tr '[:upper:]' '[:lower:]' | tr -cs 'a-z0-9.-' '-' | sed 's/^-*//;s/-*$//')

# Build full image name
FULL_IMAGE_NAME="$REGISTRY_URL/$ECR_REPO:$IMAGE_TAG"

echo ""
echo "${GREEN}Building Docker image...${NC}"
echo "Image: $FULL_IMAGE_NAME"
echo ""

# Build Docker image
docker build -t $FULL_IMAGE_NAME .

if [ $? -ne 0 ]; then
    echo "${RED}Docker build failed. Please check the Dockerfile and build context.${NC}"
    exit 1
fi

echo "${GREEN}Docker build completed successfully!${NC}"
echo ""

# Push Docker image
echo "${YELLOW}Pushing image to registry...${NC}"
docker push $FULL_IMAGE_NAME

if [ $? -ne 0 ]; then
    echo "${RED}Docker push failed. Please check your registry credentials and permissions.${NC}"
    exit 1
fi

echo ""
echo "${GREEN}✓ Image pushed successfully!${NC}"
echo "${GREEN}Image URI: $FULL_IMAGE_NAME${NC}"
echo ""
echo "${YELLOW}Use this image URI for deployment to AWS ECS.${NC}"
