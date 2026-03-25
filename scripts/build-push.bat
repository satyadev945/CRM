@echo off
setlocal enabledelayedexpansion

REM Build and Push Docker Image Script for CRM Application
REM This script builds the Docker image and pushes it to the selected registry

echo ==========================================
echo CRM Application - Build and Push Script
echo ==========================================
echo.

REM Project configuration
set PROJECT_NAME=crm-045

REM Sanitize image name (lowercase, replace special chars with hyphens)
set IMAGE_NAME=!PROJECT_NAME!
for %%i in (A B C D E F G H I J K L M N O P Q R S T U V W X Y Z) do (
    set IMAGE_NAME=!IMAGE_NAME:%%i=%%i!
)
set IMAGE_NAME=%IMAGE_NAME: =-%
set IMAGE_NAME=%IMAGE_NAME:_=-%
call :tolower IMAGE_NAME

echo Project: %PROJECT_NAME%
echo Sanitized Image Name: !IMAGE_NAME!
echo.

REM Prompt for image tag
set /p IMAGE_TAG="Enter image tag (default: latest): "
if "!IMAGE_TAG!"=="" set IMAGE_TAG=latest

REM Sanitize tag
call :tolower IMAGE_TAG
set IMAGE_TAG=!IMAGE_TAG: =-!
set IMAGE_TAG=!IMAGE_TAG:_=-!

REM Default to 'latest' if tag is empty
if "!IMAGE_TAG!"=="" set IMAGE_TAG=latest

echo Image tag: !IMAGE_TAG!
echo.

REM Registry selection
echo Select Docker Registry:
echo 1. AWS ECR (Elastic Container Registry)
echo 2. Docker Hub
set /p REGISTRY_CHOICE="Enter choice (1 or 2): "

if "!REGISTRY_CHOICE!"=="1" (
    echo.
    echo === AWS ECR Configuration ===
    
    REM Prompt for AWS region
    set /p AWS_REGION="Enter AWS region (e.g., us-east-1): "
    
    REM Prompt for AWS Account ID
    set /p AWS_ACCOUNT_ID="Enter AWS Account ID: "
    
    REM ECR repository name
    set ECR_REPO=!IMAGE_NAME!
    
    REM Construct ECR registry URL
    set REGISTRY_URL=!AWS_ACCOUNT_ID!.dkr.ecr.!AWS_REGION!.amazonaws.com
    set FULL_IMAGE_NAME=!REGISTRY_URL!/!ECR_REPO!:!IMAGE_TAG!
    
    echo.
    echo ECR Repository: !ECR_REPO!
    echo Full Image Name: !FULL_IMAGE_NAME!
    echo.
    
    REM Authenticate with ECR
    echo Authenticating with AWS ECR...
    for /f "tokens=*" %%i in ('aws ecr get-login-password --region !AWS_REGION!') do set ECR_PASSWORD=%%i
    echo !ECR_PASSWORD! | docker login --username AWS --password-stdin !REGISTRY_URL!
    
    if !ERRORLEVEL! neq 0 (
        echo ERROR: ECR authentication failed
        exit /b 1
    )
    
    echo ECR authentication successful
    echo.
    
    REM Check if ECR repository exists
    echo Checking if ECR repository exists...
    aws ecr describe-repositories --repository-names !ECR_REPO! --region !AWS_REGION! >nul 2>&1
    
    if !ERRORLEVEL! neq 0 (
        echo Repository does not exist. Creating ECR repository: !ECR_REPO!
        aws ecr create-repository --repository-name !ECR_REPO! --region !AWS_REGION!
        if !ERRORLEVEL! neq 0 (
            echo ERROR: Failed to create ECR repository
            exit /b 1
        )
        echo ECR repository created successfully
    )
    echo.
    
) else if "!REGISTRY_CHOICE!"=="2" (
    echo.
    echo === Docker Hub Configuration ===
    
    REM Prompt for Docker Hub username
    set /p DOCKER_USERNAME="Enter Docker Hub username: "
    
    REM Prompt for Docker Hub password
    set /p DOCKER_PASSWORD="Enter Docker Hub password: "
    
    REM Construct Docker Hub image name
    set FULL_IMAGE_NAME=!DOCKER_USERNAME!/!IMAGE_NAME!:!IMAGE_TAG!
    
    echo.
    echo Full Image Name: !FULL_IMAGE_NAME!
    echo.
    
    REM Authenticate with Docker Hub
    echo Authenticating with Docker Hub...
    echo !DOCKER_PASSWORD! | docker login --username !DOCKER_USERNAME! --password-stdin
    
    if !ERRORLEVEL! neq 0 (
        echo ERROR: Docker Hub authentication failed
        exit /b 1
    )
    
    echo Docker Hub authentication successful
    echo.
    
) else (
    echo ERROR: Invalid choice. Please select 1 or 2.
    exit /b 1
)

REM Build Docker image
echo ==========================================
echo Building Docker image...
echo ==========================================
docker build -t !FULL_IMAGE_NAME! .

if !ERRORLEVEL! neq 0 (
    echo ERROR: Docker build failed
    exit /b 1
)

echo.
echo Docker image built successfully: !FULL_IMAGE_NAME!
echo.

REM Push Docker image
echo ==========================================
echo Pushing Docker image to registry...
echo ==========================================
docker push !FULL_IMAGE_NAME!

if !ERRORLEVEL! neq 0 (
    echo ERROR: Docker push failed
    exit /b 1
)

echo.
echo ==========================================
echo SUCCESS!
echo ==========================================
echo Image pushed successfully: !FULL_IMAGE_NAME!
echo.
echo Next steps:
echo 1. Use this image URI in your ECS task definition
echo 2. Run the deploy-image.bat script to deploy to ECS
echo ==========================================

goto :eof

:tolower
for %%L in (a b c d e f g h i j k l m n o p q r s t u v w x y z) do (
    call set %1=%%%1:%%L=%%L%%
)
for %%L in (A B C D E F G H I J K L M N O P Q R S T U V W X Y Z) do (
    call set %1=%%%1:%%L=%%L%%
)
set %1=!%1:A=a!
set %1=!%1:B=b!
set %1=!%1:C=c!
set %1=!%1:D=d!
set %1=!%1:E=e!
set %1=!%1:F=f!
set %1=!%1:G=g!
set %1=!%1:H=h!
set %1=!%1:I=i!
set %1=!%1:J=j!
set %1=!%1:K=k!
set %1=!%1:L=l!
set %1=!%1:M=m!
set %1=!%1:N=n!
set %1=!%1:O=o!
set %1=!%1:P=p!
set %1=!%1:Q=q!
set %1=!%1:R=r!
set %1=!%1:S=s!
set %1=!%1:T=t!
set %1=!%1:U=u!
set %1=!%1:V=v!
set %1=!%1:W=w!
set %1=!%1:X=x!
set %1=!%1:Y=y!
set %1=!%1:Z=z!
goto :eof
