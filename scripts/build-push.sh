#!/bin/bash

set -e

echo "========================================"
echo "Docker Build and Push Script"
echo "========================================"
echo ""

# Project configuration
PROJECT_NAME="crm"

# Prompt for image tag
read -p "Enter image tag (default: latest): " IMAGE_TAG
IMAGE_TAG=${IMAGE_TAG:-latest}

# Sanitize tag: lowercase, replace non-alphanumeric with hyphens, trim leading/trailing hyphens
IMAGE_TAG=$(echo "$IMAGE_TAG" | tr '[:upper:]' '[:lower:]' | tr -cs 'a-z0-9' '-' | sed 's/^-*//;s/-*$//')

# If tag is empty after sanitization, use 'latest'
if [ -z "$IMAGE_TAG" ]; then
  IMAGE_TAG="latest"
fi

echo "Using sanitized tag: $IMAGE_TAG"
echo ""

# Sanitize project name for image name
IMAGE_NAME=$(echo "$PROJECT_NAME" | tr '[:upper:]' '[:lower:]' | tr -cs 'a-z0-9' '-' | sed 's/^-*//;s/-*$//')

echo "Select container registry:"
echo "1. AWS ECR (Elastic Container Registry)"
echo "2. Docker Hub"
read -p "Enter choice (1 or 2): " REGISTRY_CHOICE

if [ "$REGISTRY_CHOICE" == "1" ]; then
  echo ""
  echo "========================================"
  echo "AWS ECR Configuration"
  echo "========================================"
  
  read -p "Enter AWS Region (e.g., us-east-1): " AWS_REGION
  read -p "Enter AWS Account ID: " AWS_ACCOUNT_ID
  read -p "Enter ECR Repository Name (default: $IMAGE_NAME): " ECR_REPO
  ECR_REPO=${ECR_REPO:-$IMAGE_NAME}
  
  # Sanitize ECR repository name
  ECR_REPO=$(echo "$ECR_REPO" | tr '[:upper:]' '[:lower:]' | tr -cs 'a-z0-9-_/' '-' | sed 's/^-*//;s/-*$//')
  
  REGISTRY_URL="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
  FULL_IMAGE_NAME="${REGISTRY_URL}/${ECR_REPO}:${IMAGE_TAG}"
  
  echo ""
  echo "Authenticating with AWS ECR..."
  aws ecr get-login-password --region "$AWS_REGION" | docker login --username AWS --password-stdin "$REGISTRY_URL"
  
  echo ""
  echo "Checking if ECR repository exists..."
  if ! aws ecr describe-repositories --repository-names "$ECR_REPO" --region "$AWS_REGION" >/dev/null 2>&1; then
    echo "Repository does not exist. Creating ECR repository: $ECR_REPO"
    aws ecr create-repository --repository-name "$ECR_REPO" --region "$AWS_REGION"
    echo "Repository created successfully."
  else
    echo "Repository already exists."
  fi
  
elif [ "$REGISTRY_CHOICE" == "2" ]; then
  echo ""
  echo "========================================"
  echo "Docker Hub Configuration"
  echo "========================================"
  
  read -p "Enter Docker Hub username: " DOCKER_USERNAME
  read -sp "Enter Docker Hub password or access token: " DOCKER_PASSWORD
  echo ""
  
  FULL_IMAGE_NAME="${DOCKER_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}"
  
  echo ""
  echo "Authenticating with Docker Hub..."
  echo "$DOCKER_PASSWORD" | docker login --username "$DOCKER_USERNAME" --password-stdin
  
else
  echo "Invalid choice. Exiting."
  exit 1
fi

echo ""
echo "========================================"
echo "Building Docker Image"
echo "========================================"
echo "Image: $FULL_IMAGE_NAME"
echo ""

docker build -t "$FULL_IMAGE_NAME" .

if [ $? -ne 0 ]; then
  echo "Docker build failed. Exiting."
  exit 1
fi

echo ""
echo "========================================"
echo "Pushing Docker Image"
echo "========================================"
echo ""

docker push "$FULL_IMAGE_NAME"

if [ $? -ne 0 ]; then
  echo "Docker push failed. Exiting."
  exit 1
fi

echo ""
echo "========================================"
echo "Build and Push Completed Successfully"
echo "========================================"
echo "Image: $FULL_IMAGE_NAME"
echo ""
echo "To deploy to ECS, run:"
if [ "$REGISTRY_CHOICE" == "1" ]; then
  echo "  ./scripts/deploy-image.sh"
else
  echo "  docker run -d -p 8080:8080 $FULL_IMAGE_NAME"
fi
echo ""