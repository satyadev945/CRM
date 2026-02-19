#!/bin/bash

# AWS Deployment Script for CRM Application
# This script helps deploy the application to AWS

set -e

echo "=========================================="
echo "CRM Application - AWS Deployment Script"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if required tools are installed
check_prerequisites() {
    print_info "Checking prerequisites..."
    
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed. Please install Maven first."
        exit 1
    fi
    
    if ! command -v aws &> /dev/null; then
        print_warn "AWS CLI is not installed. Some features may not work."
    fi
    
    if ! command -v docker &> /dev/null; then
        print_warn "Docker is not installed. Docker deployment will not be available."
    fi
    
    print_info "Prerequisites check completed."
}

# Build the application
build_application() {
    print_info "Building application..."
    mvn clean package -DskipTests
    
    if [ $? -eq 0 ]; then
        print_info "Build successful!"
    else
        print_error "Build failed!"
        exit 1
    fi
}

# Create Docker image
build_docker_image() {
    print_info "Building Docker image..."
    
    if command -v docker &> /dev/null; then
        docker build -t crm-app:latest .
        print_info "Docker image built successfully!"
    else
        print_warn "Docker not available. Skipping Docker build."
    fi
}

# Deploy to EC2
deploy_to_ec2() {
    print_info "Deploying to EC2..."
    
    if [ -z "$EC2_HOST" ]; then
        print_error "EC2_HOST environment variable is not set."
        print_info "Set it with: export EC2_HOST=ec2-user@your-instance-ip"
        exit 1
    fi
    
    print_info "Copying JAR to EC2 instance..."
    scp target/crm-0.0.1-SNAPSHOT.jar $EC2_HOST:/home/ec2-user/crm-app.jar
    
    print_info "Restarting application on EC2..."
    ssh $EC2_HOST "sudo systemctl restart crm-app"
    
    print_info "Deployment to EC2 completed!"
}

# Push to ECR
push_to_ecr() {
    print_info "Pushing Docker image to ECR..."
    
    if [ -z "$ECR_REPOSITORY" ]; then
        print_error "ECR_REPOSITORY environment variable is not set."
        print_info "Set it with: export ECR_REPOSITORY=your-account-id.dkr.ecr.region.amazonaws.com/crm-app"
        exit 1
    fi
    
    # Login to ECR
    aws ecr get-login-password --region ${AWS_REGION:-us-east-1} | docker login --username AWS --password-stdin $ECR_REPOSITORY
    
    # Tag and push
    docker tag crm-app:latest $ECR_REPOSITORY:latest
    docker push $ECR_REPOSITORY:latest
    
    print_info "Docker image pushed to ECR successfully!"
}

# Validate environment variables
validate_env_vars() {
    print_info "Validating environment variables..."
    
    required_vars=("DATABASE_URL" "DATABASE_USERNAME" "DATABASE_PASSWORD" "AWS_S3_BUCKET_NAME")
    missing_vars=()
    
    for var in "${required_vars[@]}"; do
        if [ -z "${!var}" ]; then
            missing_vars+=("$var")
        fi
    done
    
    if [ ${#missing_vars[@]} -gt 0 ]; then
        print_warn "The following environment variables are not set:"
        for var in "${missing_vars[@]}"; do
            echo "  - $var"
        done
        print_warn "Make sure to set them before running the application."
    else
        print_info "All required environment variables are set."
    fi
}

# Main menu
show_menu() {
    echo ""
    echo "Select deployment option:"
    echo "1) Build application only"
    echo "2) Build and create Docker image"
    echo "3) Deploy to EC2"
    echo "4) Push to ECR"
    echo "5) Full deployment (Build + Docker + ECR)"
    echo "6) Validate environment variables"
    echo "7) Exit"
    echo ""
    read -p "Enter your choice [1-7]: " choice
    
    case $choice in
        1)
            build_application
            ;;
        2)
            build_application
            build_docker_image
            ;;
        3)
            build_application
            deploy_to_ec2
            ;;
        4)
            build_docker_image
            push_to_ecr
            ;;
        5)
            build_application
            build_docker_image
            push_to_ecr
            ;;
        6)
            validate_env_vars
            ;;
        7)
            print_info "Exiting..."
            exit 0
            ;;
        *)
            print_error "Invalid option. Please try again."
            show_menu
            ;;
    esac
}

# Main execution
main() {
    check_prerequisites
    show_menu
    
    echo ""
    print_info "Deployment process completed!"
    print_info "Check the application health at: http://your-instance:8080/actuator/health"
}

# Run main function
main
