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

# Project information
PROJECT_NAME="crm"
print_message "Building project: $PROJECT_NAME"

# Select registry type
echo "Select registry type:"
echo "1. AWS ECR (Amazon Elastic Container Registry)"
echo "2. Docker Hub"
read -p "Enter selection (1-2): " REGISTRY_SELECTION

# Process selection and collect required information
if [ "$REGISTRY_SELECTION" == "1" ]; then
    print_message "Selected AWS ECR registry"
    read -p "Enter AWS region (e.g., us-east-1): " AWS_REGION

    # Get AWS account ID
    print_message "Retrieving AWS account ID..."
    ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text 2>/dev/null || echo "")

    if [ -z "$ACCOUNT_ID" ]; then
        read -p "Could not retrieve AWS account ID automatically. Please enter your AWS account ID: " ACCOUNT_ID
    else
        print_message "AWS account ID: $ACCOUNT_ID"
    fi

    # Create ECR repository name from project name
    ECR_REPO=$(echo "$PROJECT_NAME" | tr '[:upper:]' '[:lower:]' | tr -cs 'a-z0-9' '-' | sed 's/^-*//;s/-*$//')

    # Format full registry URL for ECR
    REGISTRY_URL="${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

elif [ "$REGISTRY_SELECTION" == "2" ]; then
    print_message "Selected Docker Hub registry"
    read -p "Enter Docker Hub username: " DOCKER_USERNAME
    read -s -p "Enter Docker Hub password: " DOCKER_PASSWORD
    echo # New line after password
    REGISTRY_URL="docker.io"
else
    print_error "Invalid selection. Please choose 1 for AWS ECR or 2 for Docker Hub."
fi

# Sanitize image name (lowercase, hyphenated)
IMAGE_NAME=$(echo "$PROJECT_NAME" | tr '[:upper:]' '[:lower:]' | tr -cs 'a-z0-9' '-' | sed 's/^-*//;s/-*$//')

# Ask for image tag
read -p "Enter image tag (default: latest): " IMAGE_TAG
if [ -z "$IMAGE_TAG" ]; then
    IMAGE_TAG="latest"
fi

# Sanitize tag
IMAGE_TAG=$(echo "$IMAGE_TAG" | tr -cs 'a-z0-9.-' '-' | sed 's/^-*//;s/-*$//')
if [ -z "$IMAGE_TAG" ]; then
    IMAGE_TAG="latest"
fi

# Build the Docker image
print_message "Building Docker image: $IMAGE_NAME:$IMAGE_TAG"
docker build -t "$IMAGE_NAME:$IMAGE_TAG" -f Dockerfile .

# Handle registry login and push based on selection
if [ "$REGISTRY_SELECTION" == "1" ]; then
    # ECR login
    print_message "Logging in to Amazon ECR in region $AWS_REGION"
    aws ecr get-login-password --region "$AWS_REGION" | docker login --username AWS --password-stdin "$REGISTRY_URL"

    # Check if repository exists, create if not
    print_message "Checking if ECR repository exists: $ECR_REPO"
    aws ecr describe-repositories --repository-names "$ECR_REPO" --region "$AWS_REGION" >/dev/null 2>&1 || \
    (print_message "Creating ECR repository: $ECR_REPO" && \
     aws ecr create-repository --repository-name "$ECR_REPO" --region "$AWS_REGION")

    # Tag and push to ECR
    FULL_IMAGE_NAME="${REGISTRY_URL}/${ECR_REPO}:${IMAGE_TAG}"
    print_message "Tagging image for ECR: $FULL_IMAGE_NAME"
    docker tag "$IMAGE_NAME:$IMAGE_TAG" "$FULL_IMAGE_NAME"

    print_message "Pushing image to ECR: $FULL_IMAGE_NAME"
    docker push "$FULL_IMAGE_NAME"

elif [ "$REGISTRY_SELECTION" == "2" ]; then
    # Docker Hub login
    print_message "Logging in to Docker Hub as $DOCKER_USERNAME"
    echo "$DOCKER_PASSWORD" | docker login --username "$DOCKER_USERNAME" --password-stdin

    # Tag and push to Docker Hub
    FULL_IMAGE_NAME="${DOCKER_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}"
    print_message "Tagging image for Docker Hub: $FULL_IMAGE_NAME"
    docker tag "$IMAGE_NAME:$IMAGE_TAG" "$FULL_IMAGE_NAME"

    print_message "Pushing image to Docker Hub: $FULL_IMAGE_NAME"
    docker push "$FULL_IMAGE_NAME"
fi

# Print success message with image details
print_success "Successfully built and pushed image: $FULL_IMAGE_NAME"
echo "Image URI: $FULL_IMAGE_NAME"
echo "Use this Image URI when deploying to AWS ECS"

# Save image URI to a file for later use
echo "$FULL_IMAGE_NAME" > ./image-uri.txt
print_message "Image URI saved to ./image-uri.txt"