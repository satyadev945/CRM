@echo off
setlocal enabledelayedexpansion

echo === CRM Container - Build and Push Script ===
echo.

set PROJECT_NAME=crm-container

echo Select container registry:
echo 1. AWS ECR (Elastic Container Registry)
echo 2. Docker Hub
set /p REGISTRY_CHOICE="Enter choice (1 or 2): "

if "!REGISTRY_CHOICE!"=="1" (
    echo Selected: AWS ECR
    
    set /p AWS_REGION="Enter AWS Region (e.g., us-east-1): "
    set /p AWS_ACCOUNT_ID="Enter AWS Account ID: "
    set /p ECR_REPO="Enter ECR Repository Name (default: crm-container): "
    if "!ECR_REPO!"=="" set ECR_REPO=crm-container
    
    echo Authenticating with AWS ECR...
    set REGISTRY_URL=!AWS_ACCOUNT_ID!.dkr.ecr.!AWS_REGION!.amazonaws.com
    
    for /f "delims=" %%i in ('aws ecr get-login-password --region !AWS_REGION!') do set ECR_PASSWORD=%%i
    echo !ECR_PASSWORD! | docker login --username AWS --password-stdin !REGISTRY_URL!
    
    if !ERRORLEVEL! neq 0 (
        echo ECR authentication failed. Please check your AWS credentials.
        exit /b 1
    )
    
    echo Checking if ECR repository exists...
    aws ecr describe-repositories --repository-names !ECR_REPO! --region !AWS_REGION! >nul 2>&1
    if !ERRORLEVEL! neq 0 (
        echo Creating ECR repository: !ECR_REPO!
        aws ecr create-repository --repository-name !ECR_REPO! --region !AWS_REGION!
    )
    
) else if "!REGISTRY_CHOICE!"=="2" (
    echo Selected: Docker Hub
    
    set /p DOCKER_USERNAME="Enter Docker Hub Username: "
    set /p DOCKER_PASSWORD="Enter Docker Hub Password or Token: "
    
    echo Authenticating with Docker Hub...
    echo !DOCKER_PASSWORD! | docker login --username !DOCKER_USERNAME! --password-stdin
    
    if !ERRORLEVEL! neq 0 (
        echo Docker Hub authentication failed. Please check your credentials.
        exit /b 1
    )
    
    set REGISTRY_URL=!DOCKER_USERNAME!
    set ECR_REPO=!PROJECT_NAME!
    
) else (
    echo Invalid choice. Exiting.
    exit /b 1
)

set /p IMAGE_TAG="Enter image tag (default: latest): "
if "!IMAGE_TAG!"=="" set IMAGE_TAG=latest

echo Sanitizing image tag...
for /f "delims=" %%a in ('powershell -Command "'!IMAGE_TAG!'.ToLower() -replace '[^a-z0-9.-]', '-' -replace '^-+', '' -replace '-+$', ''"') do set IMAGE_TAG=%%a

set FULL_IMAGE_NAME=!REGISTRY_URL!/!ECR_REPO!:!IMAGE_TAG!

echo.
echo Building Docker image...
echo Image: !FULL_IMAGE_NAME!
echo.

docker build -t !FULL_IMAGE_NAME! .

if !ERRORLEVEL! neq 0 (
    echo Docker build failed. Please check the Dockerfile and build context.
    exit /b 1
)

echo Docker build completed successfully!
echo.

echo Pushing image to registry...
docker push !FULL_IMAGE_NAME!

if !ERRORLEVEL! neq 0 (
    echo Docker push failed. Please check your registry credentials and permissions.
    exit /b 1
)

echo.
echo Image pushed successfully!
echo Image URI: !FULL_IMAGE_NAME!
echo.
echo Use this image URI for deployment to AWS ECS.

endlocal
